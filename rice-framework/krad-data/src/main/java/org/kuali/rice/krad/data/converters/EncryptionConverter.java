package org.kuali.rice.krad.data.converters;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.CoreApiServiceLocator;

/**
 * This class calls core service to encrypt values going to the database and decrypt values coming back from the
 * database.
 * 
 * 
 */
@Converter
public class EncryptionConverter implements AttributeConverter<String, String> {

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
