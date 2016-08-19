/**
 * Copyright (C) 2010 Orbeon, Inc.
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The full text of the license is available at http://www.gnu.org/copyleft/lesser.html
 */
package org.orbeon.oxf.xforms.processor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class XFormsFeatures {

    private static final ResourceConfig[] stylesheets = {
        // Standard CSS
        new ResourceConfig("/ops/yui/container/assets/skins/sam/container.css", null),
        new ResourceConfig("/ops/yui/progressbar/assets/skins/sam/progressbar.css", null),
        // Calendar CSS
        new ResourceConfig("/ops/yui/calendar/assets/skins/sam/calendar.css", null),
        // Other standard stylesheets
        new ResourceConfig("/apps/fr/style/bootstrap/css/bootstrap.css", "/apps/fr/style/bootstrap/css/bootstrap.min.css"),
        new ResourceConfig("/apps/fr/style/form-runner-bootstrap-override.css", null),
        new ResourceConfig("/config/theme/xforms.css", null),
        new ResourceConfig("/config/theme/error.css", null),
        new ResourceConfig("/ops/nprogress-0.2.0/nprogress.css", null)
    };

    private static final ResourceConfig[] scripts = {
        // jQuery
        new ResourceConfig("/ops/jquery/jquery-1.12.0.js", "/ops/jquery/jquery-1.12.0.min.js"),
        new ResourceConfig("/ops/jquery/jquery-browser-mobile.js", "/ops/jquery/jquery-browser-mobile.js"),
        new ResourceConfig("/apps/fr/style/bootstrap/js/bootstrap.js", "/apps/fr/style/bootstrap/js/bootstrap.min.js"),
        new ResourceConfig("/ops/javascript/orbeon/util/jquery-orbeon.js", "/ops/javascript/orbeon/util/jquery-orbeon-min.js"),
        new ResourceConfig("/ops/javascript/autosize/autosize.js", "/ops/javascript/autosize/autosize.min.js"),
        new ResourceConfig("/ops/nprogress-0.2.0/nprogress.js", "/ops/nprogress-0.2.0/nprogress-min.js"),
        // Yahoo UI Library
        new ResourceConfig("/ops/yui/yahoo/yahoo.js", "/ops/yui/yahoo/yahoo-min.js"),
        new ResourceConfig("/ops/yui/event/event.js", "/ops/yui/event/event-min.js"),
        new ResourceConfig("/ops/yui/dom/dom.js", "/ops/yui/dom/dom-min.js"),
        new ResourceConfig("/ops/yui/connection/connection.js", "/ops/yui/connection/connection-min.js"),
        new ResourceConfig("/ops/yui/element/element.js", "/ops/yui/element/element-min.js"),
        new ResourceConfig("/ops/yui/animation/animation.js", "/ops/yui/animation/animation-min.js"),
        new ResourceConfig("/ops/yui/progressbar/progressbar.js", "/ops/yui/progressbar/progressbar-min.js"),
        new ResourceConfig("/ops/yui/dragdrop/dragdrop.js", "/ops/yui/dragdrop/dragdrop-min.js"),
        new ResourceConfig("/ops/yui/container/container.js", "/ops/yui/container/container-min.js"),
        new ResourceConfig("/ops/yui/examples/container/assets/containerariaplugin.js", "/ops/yui/examples/container/assets/containerariaplugin-min.js"),
        new ResourceConfig("/ops/yui/calendar/calendar.js", "/ops/yui/calendar/calendar-min.js"),
        new ResourceConfig("/ops/yui/slider/slider.js", "/ops/yui/slider/slider-min.js"),
        // Underscore library
        new ResourceConfig("/ops/javascript/underscore/underscore.js", "/ops/javascript/underscore/underscore-min.js"),
        // XForms client
        new ResourceConfig("/ops/javascript/xforms.js",                                           "/ops/javascript/xforms-min.js"),
        new ResourceConfig("/ops/javascript/orbeon/util/fQuery.js",                               "/ops/javascript/orbeon/util/fQuery-min.js"),
        new ResourceConfig("/ops/javascript/orbeon/util/StringOps.js",                            "/ops/javascript/orbeon/util/StringOps-min.js"),
        new ResourceConfig("/ops/javascript/orbeon/util/ExecutionQueue.js",                       "/ops/javascript/orbeon/util/ExecutionQueue-min.js"),
        new ResourceConfig("/ops/javascript/orbeon/util/FiniteStateMachine.js",                   "/ops/javascript/orbeon/util/FiniteStateMachine-min.js"),
        new ResourceConfig("/ops/javascript/orbeon/xforms/server/Server.js",                      "/ops/javascript/orbeon/xforms/server/Server-min.js"),
        new ResourceConfig("/ops/javascript/orbeon/xforms/server/AjaxServer.js",                  "/ops/javascript/orbeon/xforms/server/AjaxServer-min.js"),
        new ResourceConfig("/ops/javascript/orbeon/xforms/server/AjaxServer/AjaxServerEvent.js",  "/ops/javascript/orbeon/xforms/server/AjaxServer/AjaxServerEvent-min.js"),
        new ResourceConfig("/ops/javascript/orbeon/xforms/server/AjaxServer/nextAjaxResponse.js", "/ops/javascript/orbeon/xforms/server/AjaxServer/nextAjaxResponse-min.js"),
        new ResourceConfig("/ops/javascript/orbeon/xforms/server/UploadServer.js",                "/ops/javascript/orbeon/xforms/server/UploadServer-min.js"),
        new ResourceConfig("/ops/javascript/orbeon/xforms/LoadingIndicator.js",                   "/ops/javascript/orbeon/xforms/LoadingIndicator-min.js"),
        new ResourceConfig("/ops/javascript/orbeon/xforms/Document.js",                           "/ops/javascript/orbeon/xforms/Document-min.js"),
        new ResourceConfig("/ops/javascript/orbeon/xforms/Form.js",                               "/ops/javascript/orbeon/xforms/Form-min.js"),
        new ResourceConfig("/ops/javascript/orbeon/xforms/Page.js",                               "/ops/javascript/orbeon/xforms/Page-min.js"),
        new ResourceConfig("/ops/javascript/orbeon/xforms/control/Control.js",                    "/ops/javascript/orbeon/xforms/control/Control-min.js"),
        new ResourceConfig("/ops/javascript/orbeon/xforms/control/CalendarResources.js",          "/ops/javascript/orbeon/xforms/control/CalendarResources-min.js"),
        new ResourceConfig("/ops/javascript/orbeon/xforms/control/Calendar.js",                   "/ops/javascript/orbeon/xforms/control/Calendar-min.js"),
        new ResourceConfig("/ops/javascript/orbeon/xforms/control/Upload.js",                     "/ops/javascript/orbeon/xforms/control/Upload-min.js"),
        new ResourceConfig("/ops/javascript/orbeon/xforms/action/Message.js",                     "/ops/javascript/orbeon/xforms/action/Message-min.js"),
        new ResourceConfig("/ops/javascript/orbeon/xforms/control/Placeholder.js",                "/ops/javascript/orbeon/xforms/control/Placeholder-min.js"),
        new ResourceConfig("/ops/javascript/orbeon/xforms/controls/Placement.js",                 "/ops/javascript/orbeon/xforms/controls/Placement-min.js"),
        new ResourceConfig("/ops/javascript/orbeon/xforms/controls/Help.js",                      "/ops/javascript/orbeon/xforms/controls/Help-min.js"),
        new ResourceConfig("/ops/javascript/orbeon/xforms/controls/Hint.js",                      "/ops/javascript/orbeon/xforms/controls/Hint-min.js"),
        new ResourceConfig("/ops/javascript/orbeon/xforms/controls/Textarea.js",                  "/ops/javascript/orbeon/xforms/controls/Textarea-min.js")
    };

    public static class ResourceConfig {
        private String fullResource;
        private String minResource;

        public ResourceConfig(String fullResource, String minResource) {
            this.fullResource = fullResource;
            this.minResource = minResource;
        }

        public String getResourcePath(boolean tryMinimal) {
            // Load minimal resource if requested and there exists a minimal resource
            return (tryMinimal && minResource != null) ? minResource : fullResource;
        }
    }

    public static List<ResourceConfig> cssResources        = null;
    public static List<ResourceConfig> javaScriptResources = null;

    static {
        final List<ResourceConfig> result = new ArrayList<ResourceConfig>();
        Collections.addAll(result, stylesheets);
        cssResources = Collections.unmodifiableList(result);
    }

    static {
        final List<ResourceConfig> result = new ArrayList<ResourceConfig>();
        Collections.addAll(result, scripts);
        javaScriptResources = Collections.unmodifiableList(result);
    }
}
