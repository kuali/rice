package org.kuali.rice.ken.services.impl;

import org.junit.Test;
import org.kuali.rice.ken.bo.NotificationBo;
import org.kuali.rice.ken.bo.NotificationMessageDelivery;
import org.kuali.rice.ken.test.KENTestCase;
import org.kuali.rice.ken.test.TestConstants;
import org.kuali.rice.ken.util.NotificationConstants;
import org.kuali.rice.ken.util.Util;
import org.kuali.rice.kew.api.KewApiServiceLocator;
import org.kuali.rice.kew.api.document.Document;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.test.BaselineTestCase;

import static org.junit.Assert.assertEquals;

/**
 * This class tests the notification workflow document service service impl.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BaselineTestCase.BaselineMode(BaselineTestCase.Mode.CLEAR_DB)
public class NotificationWorkflowDocumentServiceImplTest extends KENTestCase {

    private static final String CUSTOM_NOTIFICATION_DOC_TYPE = "CustomNotification";

    @Test
    public void createAndAdHocRouteNotificationWorkflowDocument_defaultKEW() throws WorkflowException {
        NotificationBo notification = services.getNotificationService().getNotification(TestConstants.NOTIFICATION_1);
        notification.setDocTypeName(NotificationConstants.KEW_CONSTANTS.NOTIFICATION_DOC_TYPE);

        Document document = createNotificationWorkflowDocument(notification);

        assertEquals(NotificationConstants.KEW_CONSTANTS.NOTIFICATION_DOC_TYPE, document.getDocumentTypeName());
    }

    @Test
    public void createAndAdHocRouteNotificationWorkflowDocument_undefinedKEW() throws WorkflowException {
        NotificationBo notification = services.getNotificationService().getNotification(TestConstants.NOTIFICATION_1);

        Document document = createNotificationWorkflowDocument(notification);

        assertEquals(NotificationConstants.KEW_CONSTANTS.NOTIFICATION_DOC_TYPE, document.getDocumentTypeName());
    }

    @Test
    public void createAndAdHocRouteNotificationWorkflowDocument_customKEW() throws WorkflowException {
        NotificationBo notification = services.getNotificationService().getNotification(TestConstants.NOTIFICATION_1);
        notification.setDocTypeName(CUSTOM_NOTIFICATION_DOC_TYPE);

        Document document = createNotificationWorkflowDocument(notification);

        assertEquals(CUSTOM_NOTIFICATION_DOC_TYPE, document.getDocumentTypeName());
    }

    protected Document createNotificationWorkflowDocument(NotificationBo notification) {
        NotificationMessageDelivery messageDelivery = new NotificationMessageDelivery();
        messageDelivery.setId(0L);
        messageDelivery.setMessageDeliveryStatus(NotificationConstants.MESSAGE_DELIVERY_STATUS.UNDELIVERED);
        messageDelivery.setNotification(notification);
        messageDelivery.setUserRecipientId(TestConstants.TEST_USER_FIVE);

        String documentId =
                services.getNotificationWorkflowDocumentService().createAndAdHocRouteNotificationWorkflowDocument(
                        messageDelivery, Util.getNotificationSystemUser(), messageDelivery.getUserRecipientId(),
                        NotificationConstants.KEW_CONSTANTS.GENERIC_DELIVERY_ANNOTATION);
        Document document = KewApiServiceLocator.getWorkflowDocumentService().getDocument(documentId);

        return document;
    }
}
