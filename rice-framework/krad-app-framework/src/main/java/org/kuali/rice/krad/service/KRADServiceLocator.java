/**
 * Copyright 2005-2014 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.krad.service;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.core.framework.persistence.platform.DatabasePlatform;
import org.kuali.rice.krad.data.DataObjectService;
import org.kuali.rice.krad.data.metadata.MetadataRepository;
import org.kuali.rice.krad.data.provider.ProviderRegistry;

/**
 * Service locator for the KRAD App Module
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class KRADServiceLocator {
    public static final String ATTACHMENT_SERVICE = "attachmentService";
    public static final String NOTE_SERVICE = "noteService";
    public static final String ENTITY_MANAGER_FACTORY = "entityManagerFactory";
    public static final String APPLICATION_ENTITY_MANAGER_FACTORY = "kradApplicationEntityManagerFactory";
    public static final String XML_OBJECT_SERIALIZER_SERVICE = "xmlObjectSerializerService";
    public static final String XML_OBJECT_SERIALIZER_IGNORE_MISSING_FIELDS_SERVICE =
            "xmlObjectSerializerIgnoreMissingFieldsService";
    public static final String KNS_SERIALIZER_SERVICE = "businessObjectSerializerService";
    public static final String KRAD_SERIALIZER_SERVICE = "dataObjectSerializerService";
    public static final String MAIL_SERVICE = "mailService";
    public static final String DB_PLATFORM = "dbPlatform";
    public static final String INACTIVATEABLE_FROM_TO_SERVICE = "inactivateableFromToService";
    public static final String DATA_OBJECT_SERVICE = "dataObjectService";
    public static final String METADATA_REPOSITORY = "metadataRepository";
    public static final String PROVIDER_REGISTRY = "providerRegistry";
    public static final String KRAD_APPLICATION_DATA_SOURCE = "kradApplicationDataSource";
    public static final String LEGACY_DATA_ADAPTER = "legacyDataAdapter";

    static <T> T getService(String serviceName) {
        return GlobalResourceLoader.<T>getService(serviceName);
    }

    public static AttachmentService getAttachmentService() {
        return getService(ATTACHMENT_SERVICE);
    }

    public static NoteService getNoteService() {
        return getService(NOTE_SERVICE);
    }

    public static EntityManagerFactory getEntityManagerFactory() {
        return getService(ENTITY_MANAGER_FACTORY);
    }

    public static EntityManagerFactory getApplicationEntityManagerFactory() {
        return getService(APPLICATION_ENTITY_MANAGER_FACTORY);
    }

    public static XmlObjectSerializerService getXmlObjectSerializerService() {
        return getService(XML_OBJECT_SERIALIZER_SERVICE);
    }

    public static XmlObjectSerializerService getXmlObjectSerializerIgnoreMissingFieldsService() {
        return getService(XML_OBJECT_SERIALIZER_IGNORE_MISSING_FIELDS_SERVICE);
    }

    public static BusinessObjectSerializerService getBusinessObjectSerializerService() {
        return getService(KNS_SERIALIZER_SERVICE);
    }

    public static BusinessObjectSerializerService getDataObjectSerializerService() {
        return getService(KRAD_SERIALIZER_SERVICE);
    }

    public static final MailService getMailService() {
        return (MailService) getService(MAIL_SERVICE);
    }

    public static DatabasePlatform getDatabasePlatform() {
        return (DatabasePlatform) getService(DB_PLATFORM);
    }

    public static InactivateableFromToService getInactivateableFromToService() {
        return (InactivateableFromToService) getService(INACTIVATEABLE_FROM_TO_SERVICE);
    }

    public static DataObjectService getDataObjectService() {
        return getService(DATA_OBJECT_SERVICE);
    }

    public static MetadataRepository getMetadataRepository() {
        return getService(METADATA_REPOSITORY);
    }

    public static ProviderRegistry getProviderRegistry() {
        return getService(PROVIDER_REGISTRY);
    }

    public static DataSource getKradApplicationDataSource() {
        return getService(KRAD_APPLICATION_DATA_SOURCE);
    }

    /**
     * Returns the legacy data adapter for handling legacy KNS and KRAD data and metadata.
     *
     * @return the legacy data adapter
     * @deprecated application code should never use this! Always use KRAD code directly.
     */
    @Deprecated
    public static LegacyDataAppAdapter getLegacyDataAdapter() {
        return getService(LEGACY_DATA_ADAPTER);
    }

}