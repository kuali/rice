package org.kuali.rice.core.xml;

import org.xml.sax.helpers.XMLFilterImpl;

/**
 * Represents a Terminal link in the filtering chain.  An instance of this
 * class is automatically added to the end of a chain so that an implementer
 * needn't worry if there is a next link in the filtering chain.  If one
 * decides to use an instance of their class in their own wiring, they
 * should be aware that it will short-circuit all further processing.
 */
public class TerminalXMLFilter extends ChainedXMLFilterBase {
    
}
