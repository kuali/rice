/*
 * Copyright 2005-2007 The Kuali Foundation.
 * 
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.iu.uis.eden.test.web;

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import edu.iu.uis.eden.test.web.framework.Filter;

/**
 * Filter which invokes a series of Filters in a chain 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class ChainingFilter implements Filter {
    private static final Logger LOG = Logger.getLogger(ChainingFilter.class);

    private List filters;

    public ChainingFilter(List filters) {
        this.filters = filters;
    }

    public String filter(String value) {
        Iterator it = filters.iterator();
        while (it.hasNext()) {
            Filter f = (Filter) it.next();
            LOG.debug("Applying filter in list: " + f);
            value= f.filter(value);
        }
        return value;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        Iterator it = filters.iterator();
        while (it.hasNext()) {
            Filter f = (Filter) it.next();
            sb.append(f.toString());
            sb.append(", ");
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 2);
        }
        return sb.toString();
    }
}