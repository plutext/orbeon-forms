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
package org.orbeon.oxf.xforms.event.events;

import org.orbeon.oxf.xforms.event.XFormsEvent;
import org.orbeon.oxf.xforms.event.XFormsEventTarget;
import org.orbeon.saxon.om.SequenceIterator;
import org.orbeon.saxon.value.SequenceExtent;

import java.util.Map;
import java.util.HashMap;

/**
 * Custom (i.e. non-XForms) event.
 */
public class XFormsCustomEvent extends XFormsEvent {
    public XFormsCustomEvent(String eventName, XFormsEventTarget targetObject, boolean bubbles, boolean cancelable) {
        super(eventName, targetObject, bubbles, cancelable);
    }

    private Map customAttributes;

    public void setAttribute(String name, SequenceExtent value) {
        if (customAttributes == null)
            customAttributes = new HashMap();
        customAttributes.put(name, value.iterate(null)); // NOTE: With Saxon 8, the param is not used, and Saxon 9 has value.iterate()
    }

    public SequenceIterator getAttribute(String name) {
        // Get custom attribute if available, or call superclass
        final Object customAttribute = (customAttributes != null) ? customAttributes.get(name) : null;
        return (SequenceIterator) ((customAttribute != null) ? customAttribute : super.getAttribute(name));
    }
}
