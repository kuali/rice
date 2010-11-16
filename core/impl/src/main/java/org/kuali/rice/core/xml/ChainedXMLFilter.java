/*
 * Copyright 2010 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
