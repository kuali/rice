package org.kuali.rice.kew.api.mail;
 
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.api.action.ActionItem;


 /**
  * Service for sending immediate email reminders.
  *
  * @author Kuali Rice Team (rice.collab@kuali.org)
  */
@WebService(name = "immediateEmailReminderServiceSoap", targetNamespace = KewApiConstants.Namespaces.KEW_NAMESPACE_2_0)
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface ImmediateEmailReminderService {
 
	@WebMethod(operationName = "sendReminder")
	public void sendReminder(ActionItem actionItem, Boolean skipOnApprovals);
 	
 }