package org.kuali.rice.kew.api.document.lookup;

import org.joda.time.DateTime;
import org.kuali.rice.kew.api.document.DocumentStatus;
import org.kuali.rice.kew.api.document.DocumentStatusCategory;

import java.util.List;
import java.util.Map;

/**
 * TODO...
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * 
 */
public interface DocumentLookupCriteriaContract {

    String getDocumentId();

    List<DocumentStatus> getDocumentStatuses();

    List<DocumentStatusCategory> getDocumentStatusCategories();

    String getTitle();

    String getApplicationDocumentId();

    String getApplicationDocumentStatus();

    String getInitiatorPrincipalName();

    String getViewerPrincipalName();

    String getViewerGroupId();

    String getApproverPrincipalName();

    String getRouteNodeName();
    
    RouteNodeLookupLogic getRouteNodeLookupLogic();
    
    String getDocumentTypeName();

    DateTime getDateCreatedFrom();

    DateTime getDateCreatedTo();

    DateTime getDateLastModifiedFrom();

    DateTime getDateLastModifiedTo();

    DateTime getDateApprovedFrom();

    DateTime getDateApprovedTo();

    DateTime getDateFinalizedFrom();

    DateTime getDateFinalizedTo();

    DateTime getDateApplicationDocumentStatusChangedFrom();

    DateTime getDateApplicationDocumentStatusChangedTo();

    Map<String, List<String>> getDocumentAttributeValues();

    String getSaveName();

	Integer getStartAtIndex();

	Integer getMaxResults();

}
