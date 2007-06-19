package edu.iu.uis.eden.security;

import java.security.GeneralSecurityException;

import org.apache.ojb.broker.accesslayer.conversions.FieldConversion;
import org.kuali.bus.services.KSBServiceLocator;

public class OjbEncryptDecryptFieldConversion implements FieldConversion {

	private static final long serialVersionUID = 5065288024404819443L;

	/**
     * @see FieldConversion#javaToSql(Object)
     */
    public Object javaToSql(Object source) {
        String converted = source.toString();

        try {
            if (KSBServiceLocator.getEncryptionService().isEnabled()) {
                converted = KSBServiceLocator.getEncryptionService().encrypt(converted);
            }
        } catch (GeneralSecurityException e) {
            throw new RuntimeException("Unable to encrypt value to db: ", e);
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
            if (KSBServiceLocator.getEncryptionService().isEnabled()) {
                converted = KSBServiceLocator.getEncryptionService().decrypt(converted);
            }
        } catch (GeneralSecurityException e) {
            throw new RuntimeException("Unable to decrypt value from db: ", e);
        }

        return converted;
    }

}
