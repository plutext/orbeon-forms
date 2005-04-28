/**
 *  Copyright (C) 2005 Orbeon, Inc.
 *
 *  This program is free software; you can redistribute it and/or modify it under the terms of the
 *  GNU Lesser General Public License as published by the Free Software Foundation; either version
 *  2.1 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  The full text of the license is available at http://www.gnu.org/copyleft/lesser.html
 */
package org.orbeon.oxf.processor.xforms.event;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.QName;
import org.orbeon.oxf.common.OXFException;
import org.orbeon.oxf.pipeline.api.PipelineContext;
import org.orbeon.oxf.processor.ProcessorImpl;
import org.orbeon.oxf.processor.ProcessorInputOutputInfo;
import org.orbeon.oxf.processor.ProcessorOutput;
import org.orbeon.oxf.processor.xforms.Model;
import org.orbeon.oxf.processor.xforms.XFormsConstants;
import org.orbeon.oxf.processor.xforms.input.Instance;
import org.orbeon.oxf.util.LoggerFactory;
import org.orbeon.oxf.util.PooledXPathExpression;
import org.orbeon.oxf.util.XPathCache;
import org.orbeon.oxf.xml.ContentHandlerHelper;
import org.orbeon.oxf.xml.EmbeddedDocumentContentHandler;
import org.orbeon.oxf.xml.dom4j.Dom4jUtils;
import org.orbeon.saxon.dom4j.DocumentWrapper;
import org.orbeon.saxon.xpath.XPathException;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Implements XForms event handling.
 */
public class XFormsEvent extends ProcessorImpl {

    static private Logger logger = LoggerFactory.createLogger(XFormsEvent.class);

    final static private String INPUT_INSTANCE = "instance";
    final static private String INPUT_MODEL = "model";
    final static private String INPUT_CONTROLS = "controls";
    final static private String INPUT_EVENT = "event";

    final static private String OUTPUT_RESPONSE = "response";

    private final static Map XFORMS_NAMESPACES = new HashMap();

    static {
        XFORMS_NAMESPACES.put(XFormsConstants.XFORMS_SHORT_PREFIX, XFormsConstants.XFORMS_NAMESPACE_URI);
        XFORMS_NAMESPACES.put(XFormsConstants.XML_EVENTS_PREFIX, XFormsConstants.XML_EVENTS_NAMESPACE_URI);
        XFORMS_NAMESPACES.put(XFormsConstants.XXFORMS_SHORT_PREFIX, XFormsConstants.XXFORMS_NAMESPACE_URI);
    }

    public XFormsEvent() {
        addInputInfo(new ProcessorInputOutputInfo(INPUT_INSTANCE));
        addInputInfo(new ProcessorInputOutputInfo(INPUT_MODEL));
        addInputInfo(new ProcessorInputOutputInfo(INPUT_CONTROLS));
        addInputInfo(new ProcessorInputOutputInfo(INPUT_EVENT));
        addOutputInfo(new ProcessorInputOutputInfo(OUTPUT_RESPONSE));
    }

    public ProcessorOutput createOutput(String name) {
        ProcessorOutput output = new ProcessorImpl.CacheableTransformerOutputImpl(getClass(), name) {
            public void readImpl(final PipelineContext pipelineContext, ContentHandler contentHandler) {

                // Get XForms model and instance, etc.
                Model model = new Model(pipelineContext, readInputAsDOM4J(pipelineContext, INPUT_MODEL));
                Instance instance = new Instance(pipelineContext, readInputAsDOM4J(pipelineContext, INPUT_INSTANCE));
                Document controlsDocument = readInputAsDOM4J(pipelineContext, INPUT_CONTROLS);
                Document eventDocument = readInputAsDOM4J(pipelineContext, INPUT_EVENT);

                // Extract action element
                Element actionElement;
                {
                    String controlId = eventDocument.getRootElement().attributeValue("source-control-id");
                    String eventName = eventDocument.getRootElement().attributeValue("name");

                    Map variables = new HashMap();
                    variables.put("control-id", controlId);
                    variables.put("control-name", eventName);

                    PooledXPathExpression xpathExpression =
                        XPathCache.getXPathExpression(pipelineContext, new DocumentWrapper(controlsDocument, null).wrap(controlsDocument),
                                "/xxf:controls//*[@xxf:id = $control-id]/xf:*[@ev:event = $control-name]", XFORMS_NAMESPACES, variables);
                    try {
                        actionElement = (Element) xpathExpression.evaluateSingle();

                        if (actionElement == null)
                            throw new OXFException("Cannot find control with id '" + controlId + "' and event '" + eventName + "'.");
                    } catch (XPathException e) {
                        throw new OXFException(e);
                    } finally {
                        if (xpathExpression != null)
                            xpathExpression.returnToPool();
                    }
                }

                // Interpret action
                interpretAction(pipelineContext, actionElement, instance);

                // Create resulting document
                try {
                    ContentHandlerHelper ch = new ContentHandlerHelper(contentHandler);
                    ch.startDocument();
                    contentHandler.startPrefixMapping("xxf", XFormsConstants.XXFORMS_NAMESPACE_URI);
                    ch.startElement("xxf", XFormsConstants.XXFORMS_NAMESPACE_URI, "event-response");

                    // Output new controls values
                    ch.startElement("xxf", XFormsConstants.XXFORMS_NAMESPACE_URI, "control-values");

                    {
                        PooledXPathExpression xpathExpression =
                            XPathCache.getXPathExpression(pipelineContext, new DocumentWrapper(controlsDocument, null).wrap(controlsDocument), "/xxf:controls//(xf:input|xf:secret|xf:textarea|xf:output|xf:upload|xf:range|xf:trigger|xf:submit|xf:select|xf:select1)[@ref]", XFORMS_NAMESPACES);
                        try {
                            for (Iterator i = xpathExpression.evaluate().iterator(); i.hasNext();) {
                                Element controlElement = (Element) i.next();

                                String controlId = controlElement.attributeValue(new QName("id", XFormsConstants.XXFORMS_NAMESPACE));
                                String controlValue = getControlValue(pipelineContext, controlElement, instance);

                                ch.element("xxf", XFormsConstants.XXFORMS_NAMESPACE_URI, "control", new String[] {"id", controlId, "value", controlValue });
                            }
                        } catch (XPathException e) {
                            throw new OXFException(e);
                        } finally {
                            if (xpathExpression != null)
                                xpathExpression.returnToPool();
                        }
                    }

                    ch.endElement();

                    // Output updated instances
                    ch.startElement("xxf", XFormsConstants.XXFORMS_NAMESPACE_URI, "instances");
                    ch.startElement("xxf", XFormsConstants.XXFORMS_NAMESPACE_URI, "instance");
                    instance.read(new EmbeddedDocumentContentHandler(contentHandler));
                    ch.endElement();
                    ch.endElement();

                    ch.endElement();

                    contentHandler.endPrefixMapping("xxf");
                    ch.endDocument();
                } catch (SAXException e) {
                    throw new OXFException(e);
                }
            }
        };
        addOutput(name, output);
        return output;
    }

    private void interpretAction(final PipelineContext pipelineContext, Element actionElement, Instance instance) {

        String actionNamespaceURI = actionElement.getNamespaceURI();
        if (!XFormsConstants.XFORMS_NAMESPACE_URI.equals(actionNamespaceURI)) {
            throw new OXFException("Invalid action namespace: " + actionNamespaceURI);
        }

        String actionName = actionElement.getName();
        if ("setvalue".equals(actionName)) {
            // xforms:setvalue

            String ref = actionElement.attributeValue("ref");// TODO: support relative refs
            String value = actionElement.attributeValue("value");
            String content = actionElement.getStringValue();

            Map namespaceContext = Dom4jUtils.getNamespaceContext(actionElement);

            String valueToSet;
            if (value != null) {
                // Value to set is computed with an XPath expression
                valueToSet = instance.evaluateXPath(pipelineContext, value, namespaceContext);
            } else {
                // Value to set is static content
                valueToSet = content;
            }

            instance.setValueForParam(pipelineContext, ref, namespaceContext, valueToSet);
        } else if ("action".equals(actionName)) {
            // xforms:action

            for (Iterator i = actionElement.elementIterator(); i.hasNext();) {
                Element embeddedActionElement = (Element) i.next();
                interpretAction(pipelineContext, embeddedActionElement, instance);
            }

        } else {
            throw new OXFException("Invalid action requested: " + actionName);
        }
    }

    private String getControlValue(final PipelineContext pipelineContext, Element controlElement, Instance instance) {

        String ref = controlElement.attributeValue("ref");// TODO: support relative refs

        Map namespaceContext = Dom4jUtils.getNamespaceContext(controlElement);
        return instance.evaluateXPath(pipelineContext, ref, namespaceContext);
    }
}
