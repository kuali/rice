package org.kuali.rice.kew.api.preferences;

/**
 * A contract defining the method for a {@link Preferences} model object and its data transfer object equivalent.
 *
 * @see Preferences
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface PreferencesContract {

    boolean isRequiresSave();

    String getEmailNotification();

    String getNotifyPrimaryDelegation();

    String getNotifySecondaryDelegation();

    String getOpenNewWindow();

    String getShowActionRequested();

    String getShowDateCreated();

    String getShowDocumentStatus();

    String getShowAppDocStatus();

    String getShowDocType();

    String getShowInitiator();

    String getShowDocTitle();

    String getShowWorkgroupRequest();

    String getShowDelegator();

    String getShowClearFyi();

    String getPageSize();

    String getRefreshRate();

    String getColorSaved();

    String getColorInitiated();

    String getColorDisapproved();

    String getColorEnroute();

    String getColorApproved();

    String getColorFinal();

    String getColorDisapproveCancel();

    String getColorProcessed();

    String getColorException();

    String getColorCanceled();

    String getDelegatorFilter();

    String getUseOutbox();

    String getShowDateApproved();

    String getShowCurrentNode();

    String getPrimaryDelegateFilter();
    
    String getNotifyAcknowledge();
    
    String getNotifyApprove();
    
    String getNotifyComplete();
    
    String getNotifyFYI();

}
