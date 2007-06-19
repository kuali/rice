package org.kuali.bus.security.credentials;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Map;

import junit.framework.TestCase;

import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.GrantedAuthorityImpl;
import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.context.SecurityContextImpl;
import org.acegisecurity.providers.cas.CasAuthenticationToken;
import org.acegisecurity.ui.cas.CasProcessingFilter;
import org.acegisecurity.userdetails.User;
import org.kuali.rice.security.credentials.Credentials;
import org.kuali.rice.security.credentials.CredentialsSource.CredentialsType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockServletConfig;

import edu.yale.its.tp.cas.proxy.ExtendedProxyGrantingTicket;
import edu.yale.its.tp.cas.proxy.ProxyTicketReceptor;

/**
 * 
 * @author Scott Battaglia
 * @version $Revision: 1.2 $ $Date: 2007-06-19 14:35:13 $
 * @since 0.9
 *
 */
public class CasProxyTicketCredentialsSourceTest extends TestCase {

	private CasProxyTicketCredentialsSource credentialsSource;
	
	private ProxyTicketReceptor receptor = new ProxyTicketReceptor();
	
	private final String proxyUrl = "https://localhost:8080/cas/proxy";

	protected void setUp() throws Exception {
		this.credentialsSource = new CasProxyTicketCredentialsSource();
		final CasAuthenticationToken token = new CasAuthenticationToken("test", "cas_user", "ticketId", new GrantedAuthority[] {new GrantedAuthorityImpl("ROLE_USER")}, new User("cas_user", "password", true, true, true, true, new GrantedAuthority[] {new GrantedAuthorityImpl("ROLE_USER")}), new ArrayList<String>(), "PGT-IOU");
		final SecurityContextImpl impl = new SecurityContextImpl();
		impl.setAuthentication(token);
		
		SecurityContextHolder.setContext(impl);
		
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.setParameter("pgtIou", "PGT-IOU");
		request.setParameter("pgtId", "PGT-ID");
		
		final MockServletConfig config = new MockServletConfig();
		config.addInitParameter(ProxyTicketReceptor.CAS_PROXYURL_INIT_PARAM, proxyUrl);
		
		this.receptor.init(config);
		
		final ExtendedProxyGrantingTicket ticket = new ExtendedProxyGrantingTicket("test", "test");
		
		final Field field = receptor.getClass().getDeclaredField("pgtMap");
		field.setAccessible(true);
		final Map map = (Map) field.get(receptor);
		
		map.put("PGT-IOU", ticket);
	}
	
	public void testCredentialsType() {
		assertEquals(CredentialsType.CAS, this.credentialsSource.getSupportedCredentialsType());		
	}
	
	public void testGetterWithCasServerInstance() {
		final Credentials c = this.credentialsSource.getCredentials("http://www.cnn.com");
		assertNotNull(c);
		assertTrue(c instanceof UsernamePasswordCredentials);
		final UsernamePasswordCredentials upc = (UsernamePasswordCredentials) c;
		assertEquals(CasProcessingFilter.CAS_STATELESS_IDENTIFIER, upc.getUsername());
		assertEquals("PT", upc.getPassword());
	}	
}
