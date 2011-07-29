package org.kuali.rice.kew.api.document;

import org.joda.time.DateTime;

public interface DocumentStatusTransitionContract {
    /**
     * The unique id of the DocumentStatusTransition.
     *
     * @return id
     */
    String getId();

    /**
     * The id parent document of the DocumentStatusTransition.
     *
     * @return documentId
     */
	String getDocumentId();

    /**
     * The previous status value of the DocumentStatusTransition.
     *
     * @return oldStatus
     */
	String getOldStatus();

    /**
     * The new status value of the DocumentStatusTransition.
     *
     * @return newStatus
     */
	String getNewStatus();

    /**
     * The date of the DocumentStatusTransition.
     *
     * @return statusTransitionDate
     */
	DateTime getStatusTransitionDate();
}
