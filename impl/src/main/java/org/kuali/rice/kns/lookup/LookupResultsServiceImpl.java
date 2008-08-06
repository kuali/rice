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
package org.kuali.rice.kns.lookup;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.codec.binary.Base64;
import org.kuali.core.web.ui.ResultRow;
import org.kuali.rice.KNSServiceLocator;
import org.kuali.rice.kns.bo.LookupResults;
import org.kuali.rice.kns.bo.MultipleValueLookupMetadata;
import org.kuali.rice.kns.bo.PersistableBusinessObject;
import org.kuali.rice.kns.bo.SelectedObjectIds;
import org.kuali.rice.kns.dao.PersistedLookupMetadataDao;
import org.kuali.rice.kns.exception.AuthorizationException;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.KNSPropertyConstants;
import org.kuali.rice.kns.util.ObjectUtils;

public class LookupResultsServiceImpl implements LookupResultsService {
    private BusinessObjectService businessObjectService;
    private PersistedLookupMetadataDao persistedLookupMetadataDao;
    
    /**
     * @see org.kuali.rice.kns.lookup.LookupResultsService#persistResultsTable(java.lang.String, java.util.List, java.lang.String)
     */
    public void persistResultsTable(String lookupResultsSequenceNumber, List<ResultRow> resultTable, String universalUserId) throws Exception {
        String resultTableString = new String(Base64.encodeBase64(ObjectUtils.toByteArray(resultTable)));
        
        Timestamp now = KNSServiceLocator.getDateTimeService().getCurrentTimestamp();
        
        LookupResults lookupResults = retrieveLookupResults(lookupResultsSequenceNumber);
        if (lookupResults == null) {
            lookupResults = new LookupResults();
            lookupResults.setLookupResultsSequenceNumber(lookupResultsSequenceNumber);
        }
        lookupResults.setLookupResultsSequenceNumber(lookupResultsSequenceNumber);
        lookupResults.setLookupUniversalUserId(universalUserId);
        lookupResults.setSerializedLookupResults(resultTableString);
        lookupResults.setLookupDate(now);
        businessObjectService.save(lookupResults);
    }

    /**
     * @see org.kuali.rice.kns.lookup.LookupResultsService#persistSelectedObjectIds(java.lang.String, java.util.Set, java.lang.String)
     */
    public void persistSelectedObjectIds(String lookupResultsSequenceNumber, Set<String> selectedObjectIds, String universalUserId) throws Exception {
        SelectedObjectIds selectedObjectIdsBO = retrieveSelectedObjectIds(lookupResultsSequenceNumber);
        if (selectedObjectIdsBO == null) {
            selectedObjectIdsBO = new SelectedObjectIds();
            selectedObjectIdsBO.setLookupResultsSequenceNumber(lookupResultsSequenceNumber);
        }
        selectedObjectIdsBO.setLookupResultsSequenceNumber(lookupResultsSequenceNumber);
        selectedObjectIdsBO.setLookupUniversalUserId(universalUserId);
        selectedObjectIdsBO.setSelectedObjectIds(LookupUtils.convertSetOfObjectIdsToString(selectedObjectIds));
        selectedObjectIdsBO.setLookupDate(KNSServiceLocator.getDateTimeService().getCurrentTimestamp());
        businessObjectService.save(selectedObjectIdsBO);
    }

    /**
     * Retrieves the LookupResults BO with the given sequence number.  Does not check authentication.
     * @param lookupResultsSequenceNumber
     * @return
     * @throws Exception
     */
    protected LookupResults retrieveLookupResults(String lookupResultsSequenceNumber) throws Exception {
        Map<String, String> queryCriteria = new HashMap<String, String>();
        queryCriteria.put(KNSConstants.LOOKUP_RESULTS_SEQUENCE_NUMBER, lookupResultsSequenceNumber);
        LookupResults lookupResults = (LookupResults) businessObjectService.findByPrimaryKey(LookupResults.class, queryCriteria);
        
        return lookupResults;
    }

    /**
     * Retrieves the SelectedObjectIds BO with the given sequence number.  Does not check authentication.
     * @param lookupResultsSequenceNumber
     * @return
     * @throws Exception
     */
    protected SelectedObjectIds retrieveSelectedObjectIds(String lookupResultsSequenceNumber) throws Exception {
        Map<String, String> queryCriteria = new HashMap<String, String>();
        queryCriteria.put(KNSConstants.LOOKUP_RESULTS_SEQUENCE_NUMBER, lookupResultsSequenceNumber);
        SelectedObjectIds selectedObjectIds = (SelectedObjectIds) businessObjectService.findByPrimaryKey(SelectedObjectIds.class, queryCriteria);
        
        return selectedObjectIds;
    }

    /**
     * @see org.kuali.rice.kns.lookup.LookupResultsService#isAuthorizedToAccessLookupResults(java.lang.String, java.lang.String)
     */
    public boolean isAuthorizedToAccessLookupResults(String lookupResultsSequenceNumber, String universalUserId) {
        try {
            LookupResults lookupResults = retrieveLookupResults(lookupResultsSequenceNumber);
            return isAuthorizedToAccessLookupResults(lookupResults, universalUserId);
        }
        catch (Exception e) {
            return false;
        }
    }

    /**
     * Returns whether the user ID parameter is allowed to view the results.
     * 
     * @param lookupResults
     * @param universalUserId
     * @return
     */
    protected boolean isAuthorizedToAccessLookupResults(LookupResults lookupResults, String universalUserId) {
        return isAuthorizedToAccessMultipleValueLookupMetadata(lookupResults, universalUserId);
    }

    /**
     * @see org.kuali.rice.kns.lookup.LookupResultsService#isAuthorizedToAccessSelectedObjectIds(java.lang.String, java.lang.String)
     */
    public boolean isAuthorizedToAccessSelectedObjectIds(String lookupResultsSequenceNumber, String universalUserId) {
        try {
            SelectedObjectIds selectedObjectIds = retrieveSelectedObjectIds(lookupResultsSequenceNumber);
            return isAuthorizedToAccessSelectedObjectIds(selectedObjectIds, universalUserId);
        }
        catch (Exception e) {
            return false;
        }
    }

    /**
     * Returns whether the user ID parameter is allowed to view the selected object IDs
     * 
     * @param selectedObjectIds
     * @param universalUserId
     * @return
     */
    protected boolean isAuthorizedToAccessSelectedObjectIds(SelectedObjectIds selectedObjectIds, String universalUserId) {
        return isAuthorizedToAccessMultipleValueLookupMetadata(selectedObjectIds, universalUserId);
    }
    

    /**
     * @see org.kuali.rice.kns.lookup.LookupResultsService#retrieveResultsTable(java.lang.String, java.lang.String)
     */
    public List<ResultRow> retrieveResultsTable(String lookupResultsSequenceNumber, String universalUserId) throws Exception {
        LookupResults lookupResults = retrieveLookupResults(lookupResultsSequenceNumber);
        if (!isAuthorizedToAccessLookupResults(lookupResults, universalUserId)) {
            // TODO: use the other identifier
            throw new AuthorizationException(universalUserId, "retrieve lookup results", "lookup sequence number " + lookupResultsSequenceNumber);
        }
        List<ResultRow> resultTable = (List<ResultRow>) ObjectUtils.fromByteArray(Base64.decodeBase64(lookupResults.getSerializedLookupResults().getBytes()));
        return resultTable;
    }

    /**
     * Returns a list of BOs that were selected.
     * 
     * This implementation makes an attempt to retrieve all BOs with the given object IDs, unless they have been deleted or the object ID changed.
     * Since data may have changed since the search, the returned BOs may not match the criteria used to search.
     * 
     * @see org.kuali.rice.kns.lookup.LookupResultsService#retrieveSelectedResultBOs(java.lang.String, java.lang.Class, java.lang.String)
     */
    public Collection<PersistableBusinessObject> retrieveSelectedResultBOs(String lookupResultsSequenceNumber, Class boClass, String universalUserId) throws Exception {
        SelectedObjectIds selectedObjectIds = retrieveSelectedObjectIds(lookupResultsSequenceNumber);
        
        if (!isAuthorizedToAccessSelectedObjectIds(selectedObjectIds, universalUserId)) {
            // TODO: use the other identifier
            throw new AuthorizationException(universalUserId, "retrieve lookup results", "lookup sequence number " + lookupResultsSequenceNumber);
        }
        
        Set<String> setOfSelectedObjIds = LookupUtils.convertStringOfObjectIdsToSet(selectedObjectIds.getSelectedObjectIds());
        
        if (setOfSelectedObjIds.isEmpty()) {
            // OJB throws exception if querying on empty set
            return new ArrayList<PersistableBusinessObject>();
        }
        Map<String, Collection<String>> queryCriteria = new HashMap<String, Collection<String>>();
        queryCriteria.put(KNSPropertyConstants.OBJECT_ID, setOfSelectedObjIds);
        return businessObjectService.findMatching(boClass, queryCriteria);
    }
    
    /**
     * @see org.kuali.rice.kns.lookup.LookupResultsService#clearPersistedLookupResults(java.lang.String)
     */
    public void clearPersistedLookupResults(String lookupResultsSequenceNumber) throws Exception {
        LookupResults lookupResults = retrieveLookupResults(lookupResultsSequenceNumber);
        if (lookupResults != null) {
            businessObjectService.delete(lookupResults);
        }
    }
    
    /**
     * @see org.kuali.rice.kns.lookup.LookupResultsService#clearPersistedSelectedObjectIds(java.lang.String)
     */
    public void clearPersistedSelectedObjectIds(String lookupResultsSequenceNumber) throws Exception {
        SelectedObjectIds selectedObjectIds = retrieveSelectedObjectIds(lookupResultsSequenceNumber);
        if (selectedObjectIds != null) {
            businessObjectService.delete(selectedObjectIds);
        }
    }

    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
    
    /**
     * Determines whether the passed in user ID is allowed to view the lookup metadata (object IDs or results table)
     * @param mvlm
     * @param universalUserId
     * @return
     */
    protected boolean isAuthorizedToAccessMultipleValueLookupMetadata(MultipleValueLookupMetadata mvlm, String universalUserId) {
        return universalUserId.equals(mvlm.getLookupUniversalUserId());
    }

    
    public void deleteOldLookupResults(Timestamp expirationDate) {
        persistedLookupMetadataDao.deleteOldLookupResults(expirationDate);
        
    }

    public void deleteOldSelectedObjectIds(Timestamp expirationDate) {
        persistedLookupMetadataDao.deleteOldSelectedObjectIds(expirationDate);
    }

    public PersistedLookupMetadataDao getPersistedLookupMetadataDao() {
        return persistedLookupMetadataDao;
    }

    public void setPersistedLookupMetadataDao(PersistedLookupMetadataDao persistedLookupMetadataDao) {
        this.persistedLookupMetadataDao = persistedLookupMetadataDao;
    }
}
