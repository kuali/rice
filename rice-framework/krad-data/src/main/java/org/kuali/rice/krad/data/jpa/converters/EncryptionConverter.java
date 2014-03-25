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
package org.kuali.rice.krad.data.jpa.converters;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.CoreApiServiceLocator;

/**
 * Calls the core service to encrypt values going to the database and decrypt values coming back from the database.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Converter
public class EncryptionConverter implements AttributeConverter<String, String> {

    /**
     * {@inheritDoc}
     *
     * This implementation encrypts the value going to the database.
     */
	@Override
	public String convertToDatabaseColumn(String objectValue) {
		// don't attempt to encrypt nulls or empty strings
		if (objectValue == null) {
			return null;
		}
		if (StringUtils.isEmpty(objectValue.toString())) {
			return "";
		}
		try {
			// check if the encryption service is enable before using it
			if (CoreApiServiceLocator.getEncryptionService().isEnabled()) {
				return CoreApiServiceLocator.getEncryptionService().encrypt(objectValue);
			}
		} catch (Exception e) {
			throw new RuntimeException("Exception while attempting to encrypt value for DB: ", e);
		}
		return objectValue;
	}

    /**
     * {@inheritDoc}
     *
     * This implementation decrypts the value coming from the database.
     */
	@Override
	public String convertToEntityAttribute(String dataValue) {
		// don't attempt to decrypt nulls or empty strings
		if (dataValue == null) {
			return null;
		}
		if (StringUtils.isEmpty(dataValue.toString())) {
			return "";
		}
		try {
			// check if the encryption service is enable before using it
			if (CoreApiServiceLocator.getEncryptionService().isEnabled()) {
				return CoreApiServiceLocator.getEncryptionService().decrypt(dataValue.toString());
			}
		} catch (Exception e) {
			throw new RuntimeException("Exception while attempting to decrypt value from DB: ", e);
		}
		return dataValue;
	}

}
