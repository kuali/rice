/*
 * Copyright 2005-2008 The Kuali Foundation
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

package org.kuali.rice.kns.datadictionary;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.datadictionary.exception.ClassValidationException;

/**
 * A single BusinessObject entry in the DataDictionary, which contains information relating to the display, validation, and general
 * maintenance of a BusinessObject and its attributes.
 *
 *
    DD: See BusinessObjectEntry.java

    JSTL: each businessObject is exposed as a Map which is accessed
    using a key of the business object class name.
    This map contains enties with the following keys

        * businessObjectClass (String)
        * exporterClass (String)
        * inquiry (Map, optional)
        * lookup (Map, optional)
        * attributes (Map)
        * collections (Map, optional)
        * relationships (Map, optional)
        * objectLabel (String, optional)
        * objectDescription (String, optional)

    See BusinessObjectEntryMapper.java

    Note: the use of extraButton in the <businessObject> tag is deprecated, and may be removed in future versions of the data dictionary.

 * Note: the setters do copious amounts of validation, to facilitate generating errors during the parsing process.
 */
public class BusinessObjectEntry extends DataObjectEntry {
    // logger
    //private static Log LOG = LogFactory.getLog(BusinessObjectEntry.class);

    protected Class<? extends BusinessObject> businessObjectClass;
    protected Class<? extends BusinessObject> baseBusinessObjectClass;
    protected InquiryDefinition inquiryDefinition;
    protected LookupDefinition lookupDefinition;
    
    protected boolean boNotesEnabled = false;

    protected List<InactivationBlockingDefinition> inactivationBlockingDefinitions;
    
    protected List<String> groupByAttributesForEffectiveDating;


    public void setBusinessObjectClass(Class<? extends BusinessObject> businessObjectClass) {
        super.setObjectClass(businessObjectClass);
        
        if (businessObjectClass == null) {
            throw new IllegalArgumentException("invalid (null) businessObjectClass");
        }

        if ( getRelationships() != null ) {
        	for ( RelationshipDefinition rd : getRelationships() ) {
        		rd.setSourceClass(businessObjectClass);
        	}
        }

        this.businessObjectClass = businessObjectClass;
    }

    public Class<? extends BusinessObject> getBusinessObjectClass() {
        return businessObjectClass;
    }

    /**
     * The baseBusinessObjectClass is an optional parameter for specifying a superclass
     * for the businessObjectClass, allowing the data dictionary to index by superclass
     * in addition to the current class.
     */

    public void setBaseBusinessObjectClass(Class<? extends BusinessObject> baseBusinessObjectClass) {
        this.baseBusinessObjectClass = baseBusinessObjectClass;
    }

    public Class<? extends BusinessObject> getBaseBusinessObjectClass() {
        return baseBusinessObjectClass;
    }

	public boolean isBoNotesEnabled() {
        return boNotesEnabled;
    }

    /**
     *            boNotesEnabled = true or false
           * true indicates that notes and attachments will be permanently
             associated with the business object
           * false indicates that notes and attachments are associated
             with the document used to create or edit the business object.
     */
    public void setBoNotesEnabled(boolean boNotesEnabled) {
        this.boNotesEnabled = boNotesEnabled;
    }



    /**
     * Directly validate simple fields, call completeValidation on Definition fields.
     */
    @Override
    public void completeValidation() {
        try {

	        if (baseBusinessObjectClass != null && !baseBusinessObjectClass.isAssignableFrom(businessObjectClass)) {
	        	throw new ClassValidationException("The baseBusinessObjectClass " + baseBusinessObjectClass.getName() +
	            		" is not a superclass of the businessObjectClass " + businessObjectClass.getName());
	        }

	        super.completeValidation();

	        if (hasInquiryDefinition()) {
	            inquiryDefinition.completeValidation(businessObjectClass, null);
	        }

	        if (hasLookupDefinition()) {
	            lookupDefinition.completeValidation(businessObjectClass, null);
	        }

	        if (inactivationBlockingDefinitions != null && !inactivationBlockingDefinitions.isEmpty()) {
	            for (InactivationBlockingDefinition inactivationBlockingDefinition : inactivationBlockingDefinitions) {
	                inactivationBlockingDefinition.completeValidation(businessObjectClass, null);
	            }
	        }
        } catch ( DataDictionaryException ex ) {
        	// just rethrow
        	throw ex;
        } catch ( Exception ex ) {
        	throw new DataDictionaryException( "Exception validating " + this, ex);
        }
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
	public String toString() {
        return "BusinessObjectEntry for " + getBusinessObjectClass();
    }

    public List<InactivationBlockingDefinition> getInactivationBlockingDefinitions() {
        return this.inactivationBlockingDefinitions;
    }

    public void setInactivationBlockingDefinitions(List<InactivationBlockingDefinition> inactivationBlockingDefinitions) {
        this.inactivationBlockingDefinitions = inactivationBlockingDefinitions;
    }
	
	public List<String> getGroupByAttributesForEffectiveDating() {
		return this.groupByAttributesForEffectiveDating;
	}

	public void setGroupByAttributesForEffectiveDating(List<String> groupByAttributesForEffectiveDating) {
		this.groupByAttributesForEffectiveDating = groupByAttributesForEffectiveDating;
	}

	/**
     * @see org.kuali.rice.kns.datadictionary.DataDictionaryEntryBase#afterPropertiesSet()
     */
    @SuppressWarnings("unchecked")
	@Override
    public void afterPropertiesSet() throws Exception {
    	super.afterPropertiesSet();
    	if ( inactivationBlockingDefinitions != null ) {
	    	for ( InactivationBlockingDefinition ibd : inactivationBlockingDefinitions ) {
	    		ibd.setBusinessObjectClass( getBusinessObjectClass() );
	            if (StringUtils.isNotBlank(ibd.getBlockedReferencePropertyName()) && ibd.getBlockedBusinessObjectClass() == null) {
	                // if the user didn't specify a class name for the blocked reference, determine it here
	            	ibd.setBlockedBusinessObjectClass( DataDictionary.getAttributeClass(businessObjectClass, ibd.getBlockedReferencePropertyName()) );
	            }
	    		ibd.setBlockingReferenceBusinessObjectClass(getBusinessObjectClass());
	    	}
    	}
    }
    
    /**
     * @return true if this instance has an inquiryDefinition
     */
    public boolean hasInquiryDefinition() {
        return (inquiryDefinition != null);
    }

    /**
     * @return current inquiryDefinition for this BusinessObjectEntry, or null if there is none
     */
    public InquiryDefinition getInquiryDefinition() {
        return inquiryDefinition;
    }

    /**
           The inquiry element is used to specify the fields that will be displayed on the
            inquiry screen for this business object and the order in which they will appear.

            DD: See InquiryDefinition.java

            JSTL: The inquiry element is a Map which is accessed using
            a key of "inquiry".  This map contains the following keys:
                * title (String)
                * inquiryFields (Map)

            See InquiryMapBuilder.java
     */
    public void setInquiryDefinition(InquiryDefinition inquiryDefinition) {
        this.inquiryDefinition = inquiryDefinition;
    }
    
    /**
     * @return true if this instance has a lookupDefinition
     */
    public boolean hasLookupDefinition() {
        return (lookupDefinition != null);
    }

    /**
     * @return current lookupDefinition for this BusinessObjectEntry, or null if there is none
     */
    public LookupDefinition getLookupDefinition() {
        return lookupDefinition;
    }

    /**
            The lookup element is used to specify the rules for "looking up"
            a business object.  These specifications define the following:
            * How to specify the search criteria used to locate a set of business objects
            * How to display the search results

            DD: See LookupDefinition.java

            JSTL: The lookup element is a Map which is accessed using
            a key of "lookup".  This map contains the following keys:
            * lookupableID (String, optional)
            * title (String)
            * menubar (String, optional)
            * defaultSort (Map, optional)
            * lookupFields (Map)
            * resultFields (Map)
            * resultSetLimit (String, optional)

            See LookupMapBuilder.java
     */
    public void setLookupDefinition(LookupDefinition lookupDefinition) {
        this.lookupDefinition = lookupDefinition;
    }

}
