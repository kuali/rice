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
package org.kuali.rice.kew.routeheader;

import org.kuali.rice.krad.data.jpa.converters.EncryptionConverter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A JPA Converter which supports mixed encrypted and non-encrypted document content XML.
 *
 * <p>Leverages behavior from the  standard {@link EncryptionConverter} but additionally when converting to the entity
 * attribute value, it will detect whether or not the value is encrypted XML or plain text XML and decrypt if necessary.
 * This allows for this situation where encryption is enabled at a later date. Using this mechanism, existing plain text
 * docs can be loaded (but could potentially be saved back to the database encrypted if encryption is enabled).</p>
 *
 * <p>Note that the mixed mode only works one way. If you have been using encryption and then disable it, this
 * converter will not be able to decrypt your old doc content for you since it will no longer have the encryption key
 * available to it.</p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Converter
public class DocumentContentEncryptionConverter implements AttributeConverter<String, String> {

    /**
     * It's XML if it starts with a '<' though there can be some whitespace in front of it as well. Encrypted and
     * Base64 encoded content will *never* start with a '<' so this should be a safe check.
     */
    private static final Pattern IS_XML = Pattern.compile("^\\s*<");

    private static final EncryptionConverter encryptionConverter = new EncryptionConverter();

    @Override
    public String convertToEntityAttribute(String dataValue) {
        // can't pass 'null' to Matcher, so let's check that first
        if (dataValue == null) {
            return null;
        }
        Matcher matcher = IS_XML.matcher(dataValue);
        if (matcher.lookingAt()) {
            return dataValue;
        }
        return encryptionConverter.convertToEntityAttribute(dataValue);
    }

    @Override
    public String convertToDatabaseColumn(String attribute) {
        return encryptionConverter.convertToDatabaseColumn(attribute);
    }
}
