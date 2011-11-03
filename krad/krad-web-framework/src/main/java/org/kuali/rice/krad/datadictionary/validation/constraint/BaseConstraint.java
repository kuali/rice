/**
 * Copyright 2005-2011 The Kuali Foundation
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
package org.kuali.rice.krad.datadictionary.validation.constraint;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;


/**
 * A class that implements the required accessor for label keys. This provides a convenient base class
 * from which other constraints can be derived.
 * 
 * This class is a direct copy of one that was in Kuali Student. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * @since 1.1
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class BaseConstraint implements Constraint {
    @XmlElement
    protected String labelKey; 
    @XmlElement
    protected Boolean applyClientSide;
    
    List<String> validationMessageParams;
    
    public BaseConstraint(){
    	applyClientSide = Boolean.valueOf(true);
    }
    
	/**
	 * LabelKey should be a single word key.  This key is used to find a message to use for this
	 * constraint from available messages.  The key is also used for defining/retrieving validation method
	 * names when applicable for ValidCharactersContraints.
	 * 
	 * If a comma separated list of keys is used, a message will be generated that is a comma separated list of
	 * the messages retrieved for each key.
	 * 
	 * @see ValidCharactersConstraint
	 * 
	 * @return
	 */
	public String getLabelKey() {
		return labelKey;
	}

	public void setLabelKey(String labelKey) {
		this.labelKey = labelKey;
	}

	/**
	 * If this is true, the constraint should be applied on the client side when the user interacts with
	 * a field - if this constraint can be interpreted for client side use. Default is true.
	 * @return the applyClientSide
	 */
	public Boolean getApplyClientSide() {
		return this.applyClientSide;
	}

	/**
	 * @param applyClientSide the applyClientSide to set
	 */
	public void setApplyClientSide(Boolean applyClientSide) {
		this.applyClientSide = applyClientSide;
	}
	

    /**
     * Parameters to be used in the string retrieved by this constraint's labelKey, ordered by number of
     * the param
     * @return the validationMessageParams
     */
    public List<String> getValidationMessageParams() {
        return this.validationMessageParams;
    }

    /**
     * @param validationMessageParams the validationMessageParams to set
     */
    public void setValidationMessageParams(List<String> validationMessageParams) {
        this.validationMessageParams = validationMessageParams;
    }
	

}
