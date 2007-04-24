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

import java.sql.Timestamp;
import java.util.LinkedHashMap;

/**
 * 
 */
public class ConcurrentLock extends PersistableBusinessObjectBase {

    private String transactionSemaphoreTypeCode;
    private String transactionSemaphoreId;
    private String personUniversalIdentifier;
    private Timestamp transactionLocktimeTimestamp;

    private LockTypeDescription lockTypeDescription;
    
    /**
     * Default constructor.
     */
    public ConcurrentLock() {

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
     * Gets the transactionSemaphoreId attribute.
     * 
     * @return Returns the transactionSemaphoreId
     * 
     */
    public String getTransactionSemaphoreId() {
        return transactionSemaphoreId;
    }

    /**
     * Sets the transactionSemaphoreId attribute.
     * 
     * @param transactionSemaphoreId The transactionSemaphoreId to set.
     * 
     */
    public void setTransactionSemaphoreId(String transactionSemaphoreId) {
        this.transactionSemaphoreId = transactionSemaphoreId;
    }


    /**
     * Gets the personUniversalIdentifier attribute.
     * 
     * @return Returns the personUniversalIdentifier
     * 
     */
    public String getPersonUniversalIdentifier() {
        return personUniversalIdentifier;
    }

    /**
     * Sets the personUniversalIdentifier attribute.
     * 
     * @param personUniversalIdentifier The personUniversalIdentifier to set.
     * 
     */
    public void setPersonUniversalIdentifier(String personUniversalIdentifier) {
        this.personUniversalIdentifier = personUniversalIdentifier;
    }


    /**
     * Gets the transactionLocktimeTimestamp attribute.
     * 
     * @return Returns the transactionLocktimeTimestamp
     * 
     */
    public Timestamp getTransactionLocktimeTimestamp() {
        return transactionLocktimeTimestamp;
    }

    /**
     * Sets the transactionLocktimeTimestamp attribute.
     * 
     * @param transactionLocktimeTimestamp The transactionLocktimeTimestamp to set.
     * 
     */
    public void setTransactionLocktimeTimestamp(Timestamp transactionLocktimeTimestamp) {
        this.transactionLocktimeTimestamp = transactionLocktimeTimestamp;
    }

    /**
     * Gets the lockTypeDescription attribute. 
     * @return Returns the lockTypeDescription.
     */
    public LockTypeDescription getLockTypeDescription() {
        return lockTypeDescription;
    }

    /**
     * Sets the lockTypeDescription attribute value.
     * @param lockTypeDescription The lockTypeDescription to set.
     * @deprecated
     */
    public void setLockTypeDescription(LockTypeDescription lockTypeDescription) {
        this.lockTypeDescription = lockTypeDescription;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("transactionSemaphoreTypeCode", this.transactionSemaphoreTypeCode);
        m.put("transactionSemaphoreId", this.transactionSemaphoreId);
        return m;
    }
}
