/*
 * Copyright 2006-2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.core.util;

import java.security.GeneralSecurityException;

import org.apache.ojb.broker.accesslayer.conversions.FieldConversion;
import org.kuali.rice.KNSServiceLocator;


/**
 * This class calls core service to encrypt values going to the database and decrypt values coming back from the database.
 * 
 * 
 */

public class OjbKualiEncryptDecryptFieldConversion implements FieldConversion {
    private static final long serialVersionUID = 2450111778124335242L;

    /**
     * @see FieldConversion#javaToSql(Object)
     */
    public Object javaToSql(Object source) {
        Object converted = source;

        try {
            converted = KNSServiceLocator.getEncryptionService().encrypt(converted);
        }
        catch (GeneralSecurityException e) {
            throw new RuntimeException("Unable to encrypt value to db: " + e.getMessage());
        }

        return converted;
    }

    /**
     * @see FieldConversion#sqlToJava(Object)
     */
    public Object sqlToJava(Object source) {
        String converted = "";
        if (source != null) {
            converted = source.toString();
        }

        try {
            converted = KNSServiceLocator.getEncryptionService().decrypt(converted);
        }
        catch (GeneralSecurityException e) {
            throw new RuntimeException("Unable to decrypt value from db: " + e.getMessage());
        }

        return converted;
    }
}