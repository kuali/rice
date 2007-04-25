/*
 * Copyright 2007 The Kuali Foundation.
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
public class DocumentType extends PersistableBusinessObjectBase {

    private static final long serialVersionUID = 2866566562262830639L;

    private String financialDocumentTypeCode;
    private String financialDocumentGroupCode;
    private String financialDocumentName;
    private boolean finEliminationsEligibilityIndicator;
    private boolean finDocumentTypeActiveIndicator;
    private String finDocumentRoutingRuleCode;
    private Integer finDocAutomaticApprovalDays;
    private boolean financialDocumentBalancedIndicator;
    private boolean transactionScrubberOffsetGenerationIndicator;

    private DocumentGroup documentGroup;
    
    /**
     * Default no-arg constructor.
     */
    public DocumentType() {

    }

    /**
     * Gets the financialDocumentTypeCode attribute.
     * 
     * @return Returns the financialDocumentTypeCode
     * 
     */
    public String getFinancialDocumentTypeCode() {
        return financialDocumentTypeCode;
    }


    /**
     * Sets the financialDocumentTypeCode attribute.
     * 
     * @param financialDocumentTypeCode The financialDocumentTypeCode to set.
     * 
     */
    public void setFinancialDocumentTypeCode(String financialDocumentTypeCode) {
        this.financialDocumentTypeCode = financialDocumentTypeCode;
    }

    /**
     * Gets the financialDocumentGroupCode attribute.
     * 
     * @return Returns the financialDocumentGroupCode
     * 
     */
    public String getFinancialDocumentGroupCode() {
        return financialDocumentGroupCode;
    }


    /**
     * Sets the financialDocumentGroupCode attribute.
     * 
     * @param financialDocumentGroupCode The financialDocumentGroupCode to set.
     * 
     */
    public void setFinancialDocumentGroupCode(String financialDocumentGroupCode) {
        this.financialDocumentGroupCode = financialDocumentGroupCode;
    }

    /**
     * Gets the financialDocumentName attribute.
     * 
     * @return Returns the financialDocumentName
     * 
     */
    public String getFinancialDocumentName() {
        return financialDocumentName;
    }


    /**
     * Sets the financialDocumentName attribute.
     * 
     * @param financialDocumentName The financialDocumentName to set.
     * 
     */
    public void setFinancialDocumentName(String financialDocumentName) {
        this.financialDocumentName = financialDocumentName;
    }

    /**
     * Gets the finEliminationsEligibilityIndicator attribute.
     * 
     * @return Returns the finEliminationsEligibilityIndicator
     * 
     */
    public boolean isFinEliminationsEligibilityIndicator() {
        return finEliminationsEligibilityIndicator;
    }


    /**
     * Sets the finEliminationsEligibilityIndicator attribute.
     * 
     * @param finEliminationsEligibilityIndicator The finEliminationsEligibilityIndicator to set.
     * 
     */
    public void setFinEliminationsEligibilityIndicator(boolean finEliminationsEligibilityIndicator) {
        this.finEliminationsEligibilityIndicator = finEliminationsEligibilityIndicator;
    }

    /**
     * Gets the finDocumentTypeActiveIndicator attribute.
     * 
     * @return Returns the finDocumentTypeActiveIndicator
     * 
     */
    public boolean isFinDocumentTypeActiveIndicator() {
        return finDocumentTypeActiveIndicator;
    }


    /**
     * Sets the finDocumentTypeActiveIndicator attribute.
     * 
     * @param finDocumentTypeActiveIndicator The finDocumentTypeActiveIndicator to set.
     * 
     */
    public void setFinDocumentTypeActiveIndicator(boolean finDocumentTypeActiveIndicator) {
        this.finDocumentTypeActiveIndicator = finDocumentTypeActiveIndicator;
    }

    /**
     * Gets the finDocumentRoutingRuleCode attribute.
     * 
     * @return Returns the finDocumentRoutingRuleCode
     * 
     */
    public String getFinDocumentRoutingRuleCode() {
        return finDocumentRoutingRuleCode;
    }


    /**
     * Sets the finDocumentRoutingRuleCode attribute.
     * 
     * @param finDocumentRoutingRuleCode The finDocumentRoutingRuleCode to set.
     * 
     */
    public void setFinDocumentRoutingRuleCode(String finDocumentRoutingRuleCode) {
        this.finDocumentRoutingRuleCode = finDocumentRoutingRuleCode;
    }

    /**
     * Gets the finDocAutomaticApprovalDays attribute.
     * 
     * @return Returns the finDocAutomaticApprovalDays
     * 
     */
    public Integer getFinDocAutomaticApprovalDays() {
        return finDocAutomaticApprovalDays;
    }


    /**
     * Sets the finDocAutomaticApprovalDays attribute.
     * 
     * @param finDocAutomaticApprovalDays The finDocAutomaticApprovalDays to set.
     * 
     */
    public void setFinDocAutomaticApprovalDays(Integer finDocAutomaticApprovalDays) {
        this.finDocAutomaticApprovalDays = finDocAutomaticApprovalDays;
    }

    /**
     * Gets the financialDocumentBalancedIndicator attribute.
     * 
     * @return Returns the financialDocumentBalancedIndicator
     * 
     */
    public boolean isFinancialDocumentBalancedIndicator() {
        return financialDocumentBalancedIndicator;
    }


    /**
     * Sets the financialDocumentBalancedIndicator attribute.
     * 
     * @param financialDocumentBalancedIndicator The financialDocumentBalancedIndicator to set.
     * 
     */
    public void setFinancialDocumentBalancedIndicator(boolean financialDocumentBalancedIndicator) {
        this.financialDocumentBalancedIndicator = financialDocumentBalancedIndicator;
    }

    /**
     * @return Returns the transactionScrubberOffsetGenerationIndicator.
     */
    public boolean isTransactionScrubberOffsetGenerationIndicator() {
        return transactionScrubberOffsetGenerationIndicator;
    }

    /**
     * @param transactionScrubberOffsetGenerationIndicator The transactionScrubberOffsetGenerationIndicator to set.
     */
    public void setTransactionScrubberOffsetGenerationIndicator(boolean transactionScrubberOffsetGenerationIndicator) {
        this.transactionScrubberOffsetGenerationIndicator = transactionScrubberOffsetGenerationIndicator;
    }

    /**
     * Gets the documentGroup attribute. 
     * @return Returns the documentGroup.
     */
    public DocumentGroup getDocumentGroup() {
        return documentGroup;
    }

    /**
     * Sets the documentGroup attribute value.
     * @param documentGroup The documentGroup to set.
     * @deprecated
     */
    public void setDocumentGroup(DocumentGroup documentGroup) {
        this.documentGroup = documentGroup;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("financialDocumentTypeCode", this.financialDocumentTypeCode);
        return m;
    }


}
