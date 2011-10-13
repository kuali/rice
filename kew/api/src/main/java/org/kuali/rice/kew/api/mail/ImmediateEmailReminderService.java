package org.kuali.rice.kew.api.mail;
 
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.api.action.ActionItem;


 /**
  * A message queue which can be used for sending immediate email reminders.
  *
  * @author Kuali Rice Team (rice.collab@kuali.org)
  */
@WebService(name = "immediateEmailReminderServiceSoap", targetNamespace = KewApiConstants.Namespaces.KEW_NAMESPACE_2_0)
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface ImmediateEmailReminderService {
 
	@WebMethod(operationName = "sendReminder")
	void sendReminder(@WebParam(name="actionItem") ActionItem actionItem, @WebParam(name="skipOnApprovals") Boolean skipOnApprovals) throws RiceIllegalArgumentException;

 }