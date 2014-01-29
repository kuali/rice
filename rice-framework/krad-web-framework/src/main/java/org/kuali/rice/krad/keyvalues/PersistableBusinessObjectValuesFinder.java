/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.krad.keyvalues;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;

/**
 * This class is a Generic ValuesFinder that builds the list of KeyValuePairs it returns
 * in getKeyValues() based on a BO along with a keyAttributeName and labelAttributeName
 * that are specified.
 * 
 * @param <T> business object type
 */
public class PersistableBusinessObjectValuesFinder extends KeyValuesBase {
    private static final Log LOG = LogFactory.getLog(PersistableBusinessObjectValuesFinder.class);
    private static final long serialVersionUID = 1L;

    protected Class<?> businessObjectClass;
    protected String keyAttributeName;
    protected String labelAttributeName;
    protected boolean includeKeyInDescription = false;
    protected boolean includeBlankRow = false;

    /**
     * Build the list of KeyValues using the key (keyAttributeName) and
     * label (labelAttributeName) of the list of all business objects found
     * for the BO class specified.
     */
    @Override
	public List<KeyValue> getKeyValues() {
    	try {
            @SuppressWarnings("deprecation")
            Collection<?> objects = KRADServiceLocatorWeb.getLegacyDataAdapter().findMatching(businessObjectClass, Collections.<String, String>emptyMap());
            List<KeyValue> labels = new ArrayList<KeyValue>(objects.size());
            if(includeBlankRow) {
            	labels.add(new ConcreteKeyValue("", ""));
            }
            for (Object object : objects) {
            	Object key = PropertyUtils.getProperty(object, keyAttributeName);
            	String label = (String)PropertyUtils.getProperty(object, labelAttributeName);
            	if (includeKeyInDescription) {
            	    label = key + " - " + label;
            	}
            	labels.add(new ConcreteKeyValue(key.toString(), label));
    	    }
            return labels;
    	} catch (Exception e) {
            LOG.error("Exception occurred while trying to build keyValues List: " + this, e);
            throw new RuntimeException("Exception occurred while trying to build keyValues List: " + this, e);
    	}
    }

    public void setBusinessObjectClass(Class<?> businessObjectClass) {
        this.businessObjectClass = businessObjectClass;
    }

    public void setIncludeKeyInDescription(boolean includeKeyInDescription) {
        this.includeKeyInDescription = includeKeyInDescription;
    }

    public void setKeyAttributeName(String keyAttributeName) {
        this.keyAttributeName = keyAttributeName;
    }

    public void setLabelAttributeName(String labelAttributeName) {
        this.labelAttributeName = labelAttributeName;
    }

	public void setIncludeBlankRow(boolean includeBlankRow) {
		this.includeBlankRow = includeBlankRow;
	}

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("PersistableBusinessObjectValuesFinder [businessObjectClass=").append(this.businessObjectClass)
                .append(", keyAttributeName=").append(this.keyAttributeName).append(", labelAttributeName=")
                .append(this.labelAttributeName).append(", includeKeyInDescription=")
                .append(this.includeKeyInDescription).append(", includeBlankRow=").append(this.includeBlankRow)
                .append("]");
        return builder.toString();
    }

}
