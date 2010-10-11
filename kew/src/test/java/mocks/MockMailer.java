package mocks;

import java.util.Properties;

import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.kuali.rice.kew.mail.Mailer;

public class MockMailer extends Mailer{

	public MockMailer(Properties configProperties) {
		super(null);
	}	
	public MockMailer(Properties configProperties, Authenticator authenticator) {
	    	super(null,null);
	}	    		   
	public MockMailer(Properties configProperties, String username, String password) {
		super(null,null,null);
	}
	
	/* (non-Javadoc)
	 * @see org.kuali.rice.kew.mail.Mailer#sendMessage(java.lang.String, javax.mail.Address[], java.lang.String, java.lang.String, javax.mail.Address[], javax.mail.Address[], boolean)
	 */
	@Override
	public void sendMessage(String sender, Address[] recipients,
			String subject, String messageBody, Address[] ccRecipients,
			Address[] bccRecipients, boolean htmlMessage)
			throws AddressException, MessagingException {
		
		String toValue = "";
		for(Address a: recipients){
			toValue += a.toString();
		}
		//String toValue = (recipients == null) ? "" : recipients.toString();
        String fromValue = (sender == null) ? "" : sender;
        String subjectValue = (subject == null) ? "" : subject;
        String bodyValue = (messageBody == null) ? "" : messageBody;
        LOG.info("\nWILL NOT send e-mail message with to '" + toValue + "'... \nfrom '" + fromValue + "'... \nsubject '" + subjectValue + "'... and \nbody '" + bodyValue);
	}

	/* (non-Javadoc)
	 * @see org.kuali.rice.kew.mail.Mailer#sendMessage(java.lang.String, java.lang.String, java.lang.String, java.lang.String, boolean)
	 */
	@Override
	public void sendMessage(String sender, String recipient, String subject, String messageBody, boolean htmlMessage) throws AddressException, MessagingException {
        final Address[] NO_RECIPIENTS = null;
        Address[] recipients = { new InternetAddress(recipient) };
        sendMessage(sender, recipients, subject, messageBody, NO_RECIPIENTS, NO_RECIPIENTS, htmlMessage);
    }
	
	/* (non-Javadoc)
	 * @see org.kuali.rice.kew.mail.Mailer#getAuthenticator()
	 */
	@Override
	public Authenticator getAuthenticator() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.kuali.rice.kew.mail.Mailer#getConfig()
	 */
	@Override
	public Properties getConfig() {

		return null;
	}

	/* (non-Javadoc)
	 * @see org.kuali.rice.kew.mail.Mailer#getCurrentSession()
	 */
	@Override
	public Session getCurrentSession() throws NoSuchProviderException {
		// TODO Auto-generated method stub
		return null;
	}	

	/* (non-Javadoc)
	 * @see org.kuali.rice.kew.mail.Mailer#setConfig(java.util.Properties)
	 */
	@Override
	public void setConfig(Properties configProperties) {
		// TODO Auto-generated method stub
		super.setConfig(configProperties);
	}

}
