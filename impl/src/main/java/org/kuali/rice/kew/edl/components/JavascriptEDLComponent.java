package org.kuali.rice.kew.edl.components;

import org.kuali.rice.kew.edl.EDLContext;
import org.kuali.rice.kew.edl.EDLModelComponent;
import org.kuali.rice.kew.edl.EDLXmlUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 * This class exists solely to propagate the javascript element content into the edl
 * element of the dom destined to be transformed so the transform can include the specified
 * javascript.  This is because the EDL definition itself is no longer present in this dom.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class JavascriptEDLComponent implements EDLModelComponent {

    public void updateDOM(Document dom, Element configElement, EDLContext edlContext) {
        
        Element edlElement = EDLXmlUtils.getEDLContent(dom, false);
        Element edlSubElement = EDLXmlUtils.getOrCreateChildElement(edlElement, "edl", true);
        
        Node n = dom.importNode(configElement, true);
        edlSubElement.appendChild(n);
    }
}

