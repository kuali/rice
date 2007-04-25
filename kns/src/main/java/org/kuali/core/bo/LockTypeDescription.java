/*
 * Copyright 2006-2007 The Kuali Foundation.
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

package org.kuali.core.bo;

import java.util.LinkedHashMap;

/**
 * 
 */
public class LockTypeDescription extends PersistableBusinessObjectBase {

    private String transactionSemaphoreTypeCode;
    private Long personUpdateAbilityNumber;
    private String transactionSemaphoreDescription;

    /**
     * Default constructor.
     */
    public LockTypeDescription() {

    }

    /**
     * Gets the transactionSemaphoreTypeCode attribute.
     * 
     * @return Returns the transactionSemaphoreTypeCode
     * 
     */
    public String getTransactionSemaphoreTypeCode() {
        return transactionSemaphoreTypeCode;
    }

    /**
     * Sets the transactionSemaphoreTypeCode attribute.
     * 
     * @param transactionSemaphoreTypeCode The transactionSemaphoreTypeCode to set.
     * 
     */
    public void setTransactionSemaphoreTypeCode(String transactionSemaphoreTypeCode) {
        this.transactionSemaphoreTypeCode = transactionSemaphoreTypeCode;
    }


    /**
     * Gets the personUpdateAbilityNumber attribute.
     * 
     * @return Returns the personUpdateAbilityNumber
     * 
     */
    public Long getPersonUpdateAbilityNumber() {
        return personUpdateAbilityNumber;
    }

    /**
     * Sets the personUpdateAbilityNumber attribute.
     * 
     * @param personUpdateAbilityNumber The personUpdateAbilityNumber to set.
     * 
     */
    public void setPersonUpdateAbilityNumber(Long personUpdateAbilityNumber) {
        this.personUpdateAbilityNumber = personUpdateAbilityNumber;
    }


    /**
     * Gets the transactionSemaphoreDescription attribute.
     * 
     * @return Returns the transactionSemaphoreDescription
     * 
     */
    public String getTransactionSemaphoreDescription() {
        return transactionSemaphoreDescription;
    }

    /**
     * Sets the transactionSemaphoreDescription attribute.
     * 
     * @param transactionSemaphoreDescription The transactionSemaphoreDescription to set.
     * 
     */
    public void setTransactionSemaphoreDescription(String transactionSemaphoreDescription) {
        this.transactionSemaphoreDescription = transactionSemaphoreDescription;
    }


    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("transactionSemaphoreTypeCode", this.transactionSemaphoreTypeCode);
        return m;
    }
}
