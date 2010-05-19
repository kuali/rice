package org.kuali.rice.core.xml;

import org.xml.sax.XMLFilter;

/**
 * A ChainedXMLFilter is a marker interface that identifies a Class as being
 * capable of cooperating in a transforming XML parse.  The reason a marker
 * interface is required is to ensure that the implementing class supports
 * a version of setParent that allows the filter chain to be reconfigured on
 * the fly.
 */
public interface ChainedXMLFilter extends XMLFilter {
}
