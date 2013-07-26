package org.kuali.rice.krad.data.converters;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.CoreApiServiceLocator;
import org.kuali.rice.core.api.encryption.EncryptionService;

/**
 * This class calls core service to hash values going to the database.
 * 
 * 
 */
@Converter
public class HashConverter implements AttributeConverter<String, String> {

	@Override
	public String convertToDatabaseColumn(String objectValue) {
		// don't attempt to encrypt nulls or empty strings
		if (objectValue == null) {
			return null;
		}
		if (StringUtils.isEmpty(objectValue.toString())) {
			return "";
		}
		// don't convert if already a hashed value
		if (objectValue.toString().endsWith(EncryptionService.HASH_POST_PREFIX)) {
			return StringUtils.stripEnd(objectValue.toString(), EncryptionService.HASH_POST_PREFIX);
		} else {
			try {
				return CoreApiServiceLocator.getEncryptionService().hash(objectValue);
			} catch (Exception e) {
				throw new RuntimeException("Exception while attempting to hash value for DB: ", e);
			}
		}
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
		return dataValue.toString() + EncryptionService.HASH_POST_PREFIX;
	}

}
