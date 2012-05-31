/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.rice.core.ojb;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.accesslayer.QueryCustomizer;
import org.apache.ojb.broker.metadata.CollectionDescriptor;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.kuali.rice.kns.util.ObjectUtils;

public class CustomCollectionJoinQueryCustomizer implements QueryCustomizer {
    // used to AND in additional criteria on a collection
    private static final String FIELD_PREFIX = "parent.";
    
    protected Map<String,String> attributeMap = new HashMap<String,String>();

    /**
     * @see org.apache.ojb.broker.metadata.AttributeContainer#addAttribute(java.lang.String, java.lang.String)
     */
    public void addAttribute(String attributeName, String attributeValue) {
    	attributeMap.put( attributeName, attributeValue );
    }
    
    /**
     * @see org.apache.ojb.broker.metadata.AttributeContainer#getAttribute(java.lang.String)
     */
    public String getAttribute(String attributeName) {
    	return getAttribute( attributeName, null );
    }
    /**
     * @see org.apache.ojb.broker.metadata.AttributeContainer#getAttribute(java.lang.String, java.lang.String)
     */
    public String getAttribute(String attributeName, String defaultValue) {
    	String val = attributeMap.get( attributeName );
    	if ( StringUtils.isBlank( val ) ) {
    		return defaultValue;
    	}
    	return val;
    }
    
    public Query customizeQuery(Object obj, PersistenceBroker pb, CollectionDescriptor collDesc, QueryByCriteria query) {

        // now, do what we wanted to do to start with if we could've just gotten m_attributeList easily
        Criteria criteria = query.getCriteria();
        for (String key : attributeMap.keySet()) {
        	String val = attributeMap.get( key );
            // if beginning with FIELD_PREFIX is too hacky, or more flexibility is needed, another query customizer class can be
            // made,
            // and this method can be renamed to take a parameter to specify which we want to do
            // (and the customizeQuery method here made to call the new method with the parameter).
            // However, making another class would mean you couldn't intermix constants and field values,
            // since OJB won't use have multiple query-customizers per collection-descriptor.
            if (val.startsWith(FIELD_PREFIX)) {
                criteria.addEqualTo(key, ObjectUtils.getPropertyValue(obj, val.substring(FIELD_PREFIX.length())));
            } else {
                criteria.addEqualTo(key, this.getAttribute(key));
            }
        }
        query.setCriteria(criteria);
        return query;
    }
}
