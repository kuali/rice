package org.kuali.bus.security.credentials;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Principal;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Set;

import junit.framework.TestCase;

/**
 * 
 * @author Scott Battaglia
 * @version $Revision: 1.2 $ $Date: 2007-06-19 14:35:13 $
 * @since 0.9
 *
 */
public class X509CredentialsSourceTest extends TestCase {

	private X509CredentialsSource credentialsSource;
	
	private X509Certificate cert = new KualiX509Certificate();

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		this.credentialsSource = new X509CredentialsSource(cert);
	}
	
	public void testX509Certificate() {
		final X509Credentials context = (X509Credentials) this.credentialsSource.getCredentials("test");
		assertNotNull(context);
		final X509Certificate cert = context.getX509Certificate();
		
		assertEquals(this.cert, cert);
	}
	
	public static class KualiX509Certificate extends X509Certificate {
		
		protected KualiX509Certificate() {
			// nothing to do
		}

		public void checkValidity() throws CertificateExpiredException, CertificateNotYetValidException {
			// nothing to do
		}

		public void checkValidity(Date date) throws CertificateExpiredException, CertificateNotYetValidException {
			// nothing to do
		}

		public int getBasicConstraints() {
			return 0;
		}

		public Principal getIssuerDN() {
			return null;
		}

		public boolean[] getIssuerUniqueID() {
			return null;
		}

		public boolean[] getKeyUsage() {
			return null;
		}

		public Date getNotAfter() {
			return null;
		}

		public Date getNotBefore() {
			return null;
		}

		public BigInteger getSerialNumber() {
			return null;
		}

		public String getSigAlgName() {
			return null;
		}

		public String getSigAlgOID() {
			return null;
		}

		public byte[] getSigAlgParams() {
			return null;
		}

		public byte[] getSignature() {
			return null;
		}

		public Principal getSubjectDN() {
			return null;
		}

		public boolean[] getSubjectUniqueID() {
			return null;
		}

		public byte[] getTBSCertificate() throws CertificateEncodingException {
			return null;
		}

		public int getVersion() {
			return 0;
		}

		public Set<String> getCriticalExtensionOIDs() {
			return null;
		}

		public byte[] getExtensionValue(String arg0) {
			return null;
		}

		public Set<String> getNonCriticalExtensionOIDs() {
			return null;
		}

		public boolean hasUnsupportedCriticalExtension() {
			return false;
		}

		public byte[] getEncoded() throws CertificateEncodingException {
			return null;
		}

		public PublicKey getPublicKey() {
			return null;
		}

		public String toString() {
			return null;
		}

		public void verify(PublicKey arg0, String arg1) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException {
			// nothing to do
		}

		public void verify(PublicKey arg0) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException {
			// nothing to do
		}
	}
}
