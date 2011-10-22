package org.kuali.rice.kew.framework.document.search;

import org.kuali.rice.kew.api.document.lookup.DocumentLookupCriteria;
import org.kuali.rice.kew.api.document.lookup.DocumentLookupResult;

import java.util.List;

/**
 * An abstract implementation of a {@link DocumentSearchCustomizer} which classes can extend from and override the
 * individual methods that they require in order to perform desired customization.  All of the base method
 * implementations in this class perform the default operation of doing no customization.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class DocumentSearchCustomizerBase implements DocumentSearchCustomizer {

    /**
     * Always returns a null reference which instructs the document lookup framework that the criteria was not
     * customized.
     *
     * @param documentLookupCriteria the criteria on which to perform customization
     * @return a null reference indicating that no customization was performed
     */
    @Override
    public DocumentLookupCriteria customizeCriteria(DocumentLookupCriteria documentLookupCriteria) {
        return null;
    }

    /**
     * Always returns a null reference which instructs the document lookup framework that custom criteria clearing was not
     * performed.
     *
     * @param documentLookupCriteria the criteria on which to perform a customized clear
     * @return a null reference indicating that no customization was performed
     */
    @Override
    public DocumentLookupCriteria customizeClearCriteria(DocumentLookupCriteria documentLookupCriteria) {
        return null;
    }

    /**
     * Always returns a null reference which instructs the document lookup framework that the customization of results
     * was not performed.
     *
     * @param documentLookupCriteria the lookup criteria
     * @param defaultResults the results obtained when executing the lookup
     * @return a null reference indicating that no customization was performed
     */
    @Override
    public DocumentSearchResultValues customizeResults(DocumentLookupCriteria documentLookupCriteria,
            List<DocumentLookupResult> defaultResults) {
        return null;
    }

    /**
     * Always returns a null reference which instructs the document lookup framework that the customization of result
     * set fields was not performed.
     *
     * @param documentLookupCriteria the lookup criteria
     * @return a null reference indicating that no customization was performed
     */
    @Override
    public DocumentSearchResultSetConfiguration customizeResultSetConfiguration(
            DocumentLookupCriteria documentLookupCriteria) {
        return null;
    }

    /**
     * Always returns false indicating that criteria customization is disabled and should not be performed.
     *
     * @param documentTypeName the name of the document type under consideration
     * @return false to indicate that no customization should be performed
     */
    @Override
    public boolean isCustomizeCriteriaEnabled(String documentTypeName) {
        return false;
    }

    /**
     * Always returns false indicating that criteria clearing customization is disabled and should not be performed.
     *
     * @param documentTypeName the name of the document type under consideration
     * @return false to indicate that no customization should be performed
     */
    @Override
    public boolean isCustomizeClearCriteriaEnabled(String documentTypeName) {
        return false;
    }

    /**
     * Always returns false indicating that results customization is disabled and should not be performed.
     *
     * @param documentTypeName the name of the document type under consideration
     * @return false to indicate that no customization should be performed
     */
    @Override
    public boolean isCustomizeResultsEnabled(String documentTypeName) {
        return false;
    }

    /**
     * Always returns false indicating that result set field customization is disabled and should not be performed.
     *
     * @param documentTypeName the name of the document type under consideration
     * @return false to indicate that no customization should be performed
     */
    @Override
    public boolean isCustomizeResultSetFieldsEnabled(String documentTypeName) {
        return false;
    }

}
