package edu.iu.uis.eden.security;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.Core;

/**
 * An EncryptionService implementation which encrypts and decrypts values using the DES
 * encryption algorithm.  The secret key used for encryption can be set on this service
 * or pulled from the "encryption.key" configuration parameter.
 *
 * @author Eric Westfall
 */
public class DESEncryptionService implements EncryptionService {

	public final static String ALGORITHM = "DES/ECB/PKCS5Padding";

	private final static String CHARSET = "UTF-8";

	private transient SecretKey desKey;

	private boolean enabled = false;

	public DESEncryptionService() throws Exception {
		if (this.desKey != null) {
			throw new RuntimeException("The secret key must be kept secret. Storing it in the Java source code is a really bad idea.");
		}

		String key = Core.getCurrentContextConfig().getProperty("encryption.key");

		if (!StringUtils.isEmpty(key)) {
			setSecretKey(key);
		}

	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public String encrypt(Object valueToHide) throws GeneralSecurityException {
		if (valueToHide == null) {
			return "";
		}

		// Initialize the cipher for encryption
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.ENCRYPT_MODE, this.desKey);

		try {
			// Our cleartext
			byte[] cleartext = valueToHide.toString().getBytes(CHARSET);

			// Encrypt the cleartext
			byte[] ciphertext = cipher.doFinal(cleartext);

			return new String(Base64.encodeBase64(ciphertext), CHARSET);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public String decrypt(String ciphertext) throws GeneralSecurityException {
		if (StringUtils.isBlank(ciphertext)) {
			return "";
		}

		// Initialize the same cipher for decryption
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.DECRYPT_MODE, this.desKey);

		try {
			// un-Base64 encode the encrypted data
			byte[] encryptedData = Base64.decodeBase64(ciphertext.getBytes(CHARSET));

			// Decrypt the ciphertext
			byte[] cleartext1 = cipher.doFinal(encryptedData);

			return new String(cleartext1, CHARSET);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 *
	 * This method generates keys. This method is implementation specific and
	 * should not be present in any general purpose interface extracted from
	 * this class.
	 *
	 * @return
	 * @throws Exception
	 */
	public static String generateEncodedKey() throws Exception {
		KeyGenerator keygen = KeyGenerator.getInstance("DES");
		SecretKey desKey = keygen.generateKey();

		// Create the cipher
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init((Cipher.WRAP_MODE), desKey);

		SecretKeyFactory desFactory = SecretKeyFactory.getInstance("DES");
		DESKeySpec desSpec = (DESKeySpec) desFactory.getKeySpec(desKey, javax.crypto.spec.DESKeySpec.class);
		byte[] rawDesKey = desSpec.getKey();

		return new String(Base64.encodeBase64(rawDesKey));
	}

	private SecretKey unwrapEncodedKey(String key) throws Exception {
		KeyGenerator keygen = KeyGenerator.getInstance("DES");
		SecretKey desKey = keygen.generateKey();

		// Create the cipher
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init((Cipher.UNWRAP_MODE), desKey);

		byte[] bytes = Base64.decodeBase64(key.getBytes());

		SecretKeyFactory desFactory = SecretKeyFactory.getInstance("DES");

		DESKeySpec keyspec = new DESKeySpec(bytes);
		SecretKey k = desFactory.generateSecret(keyspec);

		return k;
	}

	/**
	 * Sets the secretKey attribute value.
	 *
	 * @param secretKey
	 *            The secretKey to set.
	 * @throws Exception
	 */
	public void setSecretKey(String secretKey) throws Exception {
	    this.desKey = this.unwrapEncodedKey(secretKey);
	    this.enabled = true;
	}

}
