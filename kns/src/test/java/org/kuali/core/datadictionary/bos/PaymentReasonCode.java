/*
 * Copyright 2005-2006 The Kuali Foundation.
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

package org.kuali.core.datadictionary.bos;

import org.kuali.core.bo.KualiCodeBase;

/**
 * 
 */
public class PaymentReasonCode extends KualiCodeBase {

    /**
	 * 
	 */
	private static final long serialVersionUID = -8828220752451376168L;
	private String description;
	private String section2Field1;
	private String section2Field2;

    public String getSection2Field1() {
		return section2Field1;
	}

	public void setSection2Field1(String section2Field1) {
		this.section2Field1 = section2Field1;
	}

	public String getSection2Field2() {
		return section2Field2;
	}

	public void setSection2Field2(String section2Field2) {
		this.section2Field2 = section2Field2;
	}

	/**
     * Default no-arg constructor.
     */
    public PaymentReasonCode() {

    }

    /**
     * @return Returns the description.
     */
    public String getDescription() {
        return description;
    }


    /**
     * @param description The description to set.
     */
    public void setDescription(String description) {
        this.description = description;
    }
}
