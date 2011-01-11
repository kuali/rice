package org.kuali.rice.kns.service;

import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.DateTimeService;
import org.kuali.rice.core.resourceloader.GlobalResourceLoader;
import org.kuali.rice.core.service.EncryptionService;

public class KNSServiceLocator {
    public static final String ATTACHMENT_SERVICE = "attachmentService";
    public static final String PERSISTENCE_SERVICE = "persistenceService";
    public static final String PERSISTENCE_STRUCTURE_SERVICE = "persistenceStructureService";
    public static final String NOTE_SERVICE = "noteService";
    public static final String BUSINESS_OBJECT_SERVICE = "businessObjectService";

    public static <T extends Object> T getService(String serviceName) {
        return GlobalResourceLoader.<T>getService(serviceName);
    }

    public static AttachmentService getAttachmentService() {
        return  getService(ATTACHMENT_SERVICE);
    }

    public static PersistenceService getPersistenceService() {
        return getService(PERSISTENCE_SERVICE);
    }

    public static PersistenceStructureService getPersistenceStructureService() {
        return getService(PERSISTENCE_STRUCTURE_SERVICE);
    }

    public static DateTimeService getDateTimeService() {
        return getService(CoreConstants.Services.DATETIME_SERVICE);
    }

    public static NoteService getNoteService() {
        return getService(NOTE_SERVICE);
    }

    public static BusinessObjectService getBusinessObjectService() {
        return getService(BUSINESS_OBJECT_SERVICE);
    }

    public static final EncryptionService getEncryptionService() {
    	return getService(CoreConstants.Services.ENCRYPTION_SERVICE);
    }
}