/*
 * Copyright 2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS
 * IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.kuali.core.service.impl;

import java.util.HashMap;
import java.util.Set;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.ojb.broker.accesslayer.conversions.FieldConversionDefaultImpl;
import org.apache.ojb.broker.metadata.ClassDescriptor;
import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.dao.PostDataLoadEncryptionDao;
import org.kuali.core.exceptions.ClassNotPersistableException;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.EncryptionService;
import org.kuali.core.service.PostDataLoadEncryptionService;
import org.kuali.core.util.OjbKualiEncryptDecryptFieldConversion;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class PostDataLoadEncryptionServiceImpl extends PersistenceServiceImplBase implements PostDataLoadEncryptionService {
    private BusinessObjectService businessObjectService;
    private EncryptionService encryptionService;
    private PostDataLoadEncryptionDao postDataLoadEncryptionDao;

    public void checkArguments(Class businessObjectClass, Set<String> attributeNames) {
	if ((businessObjectClass == null) || (attributeNames == null)) {
	    throw new IllegalArgumentException(
		    "PostDataLoadEncryptionServiceImpl.encrypt does not allow a null business object Class or attributeNames Set");
	}
	ClassDescriptor classDescriptor = null;
	try {
	    classDescriptor = getClassDescriptor(businessObjectClass);
	} catch (ClassNotPersistableException e) {
	    throw new IllegalArgumentException(
		    "PostDataLoadEncryptionServiceImpl.encrypt does not handle business object classes that do not have a corresponding ClassDescriptor defined in the OJB repository",
		    e);
	}
	for (String attributeName : attributeNames) {
	    if (classDescriptor.getFieldDescriptorByName(attributeName) == null) {
		throw new IllegalArgumentException(
			new StringBuffer("Attribute ")
				.append(attributeName)
				.append(
					" specified to PostDataLoadEncryptionServiceImpl.encrypt is not in the OJB repository ClassDescriptor for Class ")
				.append(businessObjectClass).toString());
	    }
	    if (!(classDescriptor.getFieldDescriptorByName(attributeName).getFieldConversion() instanceof OjbKualiEncryptDecryptFieldConversion)) {
		throw new IllegalArgumentException(
			new StringBuffer("Attribute ")
				.append(attributeName)
				.append(" of business object Class ")
				.append(businessObjectClass)
				.append(
					" specified to PostDataLoadEncryptionServiceImpl.encrypt is not configured for encryption in the OJB repository")
				.toString());
	    }
	}
    }

    public void createBackupTable(Class businessObjectClass) {
	postDataLoadEncryptionDao.createBackupTable(getClassDescriptor(businessObjectClass).getFullTableName());
    }

    public void prepClassDescriptor(Class businessObjectClass, Set<String> attributeNames) {
	ClassDescriptor classDescriptor = getClassDescriptor(businessObjectClass);
	for (String attributeName : attributeNames) {
	    classDescriptor.getFieldDescriptorByName(attributeName).setFieldConversionClassName(
		    FieldConversionDefaultImpl.class.getName());
	}
    }

    public void truncateTable(Class businessObjectClass) {
	postDataLoadEncryptionDao.truncateTable(getClassDescriptor(businessObjectClass).getFullTableName());
    }

    public void encrypt(PersistableBusinessObject businessObject, Set<String> attributeNames) {
	for (String attributeName : attributeNames) {
	    try {
		PropertyUtils.setProperty(businessObject, attributeName, encryptionService.encrypt(PropertyUtils
			.getProperty(businessObject, attributeName)));
	    } catch (Exception e) {
		throw new RuntimeException(new StringBuffer(
			"PostDataLoadEncryptionServiceImpl caught exception while attempting to encrypt attribute ").append(
			attributeName).append(" of Class ").append(businessObject.getClass()).toString(), e);
	    }
	}
	businessObjectService.save(businessObject);
    }

    public void restoreClassDescriptor(Class businessObjectClass, Set<String> attributeNames) {
	ClassDescriptor classDescriptor = getClassDescriptor(businessObjectClass);
	for (String attributeName : attributeNames) {
	    classDescriptor.getFieldDescriptorByName(attributeName).setFieldConversionClassName(
		    OjbKualiEncryptDecryptFieldConversion.class.getName());
	}
	businessObjectService.countMatching(businessObjectClass, new HashMap());
    }

    public void restoreTableFromBackup(Class businessObjectClass) {
	postDataLoadEncryptionDao.restoreTableFromBackup(getClassDescriptor(businessObjectClass).getFullTableName());
    }

    public void dropBackupTable(Class businessObjectClass) {
	postDataLoadEncryptionDao.dropBackupTable(getClassDescriptor(businessObjectClass).getFullTableName());
    }

    public void setPostDataLoadEncryptionDao(PostDataLoadEncryptionDao postDataLoadEncryptionDao) {
	this.postDataLoadEncryptionDao = postDataLoadEncryptionDao;
    }

    public void setEncryptionService(EncryptionService encryptionService) {
	this.encryptionService = encryptionService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
	this.businessObjectService = businessObjectService;
    }
}
