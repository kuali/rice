package org.kuali.rice.kew.xml;

import java.io.InputStream;

import org.kuali.rice.kew.exception.WorkflowServiceErrorException;
import org.kuali.rice.kew.exception.WorkflowServiceErrorImpl;

public class UserXmlServiceImpl implements XmlLoader {

	private static final String XML_PARSE_ERROR = "general.error.parsexml";
	
    public void loadXml(InputStream inputStream, String principalId) {
        //GroupXmlParser parser = new GroupXmlParser();
        try {
            //parser.parseGroups(inputStream);
        } catch (Exception e) { //any other exception
            WorkflowServiceErrorException xe = new WorkflowServiceErrorException("Error loading xml file", new WorkflowServiceErrorImpl("Error loading xml file", XML_PARSE_ERROR));
            e.initCause(xe);
            throw xe;
        }
    }
    
//    private static final Namespace NAMESPACE = Namespace.getNamespace("", "ns:workflow/User");
//
//    private static final String USERS_ELEMENT = "users";
//    private static final String USER_ELEMENT = "user";
//    private static final String WORKFLOW_ID_ELEMENT = "workflowId";
//    private static final String EMPL_ID_ELEMENT = "emplId";
//    private static final String AUTHENTICATION_ID_ELEMENT = "authenticationId";
//    private static final String UUID_ELEMENT = "uuId";
//    private static final String EMAIL_ELEMENT = "emailAddress";
//    private static final String DISPLAY_NAME_ELEMENT = "displayName";
//    private static final String GIVEN_NAME_ELEMENT = "givenName";
//    private static final String LAST_NAME_ELEMENT = "lastName";    
//    
//    public List parseUserEntries(UserService userService, InputStream file) throws JDOMException, SAXException, IOException, ParserConfigurationException, FactoryConfigurationError {
//        List userEntries = new ArrayList();
//
//        org.w3c.dom.Document w3cDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
//        //LOG.debug("parsing document: " + XmlHelper.jotNode(w3cDocument.getFirstChild()));
//        Document document = new DOMBuilder().build(w3cDocument);
//        Element root = document.getRootElement();
//
//        for (Iterator usersElementIt = root.getChildren(USERS_ELEMENT, NAMESPACE).iterator(); usersElementIt.hasNext();) {
//            Element usersElement = (Element) usersElementIt.next();
//            for (Iterator iterator = usersElement.getChildren(USER_ELEMENT, NAMESPACE).iterator(); iterator.hasNext();) {
//                Element userElement = (Element) iterator.next();
//                
//                WorkflowUser userEntry = userService.getBlankUser();
//                userEntry.setAuthenticationUserId(new AuthenticationUserId(userElement.getChildTextTrim(AUTHENTICATION_ID_ELEMENT, NAMESPACE)));
//                
//                userEntry.setDisplayName(userElement.getChildTextTrim(DISPLAY_NAME_ELEMENT, NAMESPACE));
//                userEntry.setEmailAddress(userElement.getChildTextTrim(EMAIL_ELEMENT, NAMESPACE));
//                userEntry.setEmplId(new EmplId(userElement.getChildTextTrim(EMPL_ID_ELEMENT, NAMESPACE)));
//                userEntry.setGivenName(userElement.getChildTextTrim(GIVEN_NAME_ELEMENT, NAMESPACE));
//                userEntry.setLastName(userElement.getChildTextTrim(LAST_NAME_ELEMENT, NAMESPACE));
//                userEntry.setUuId(new UuId(userElement.getChildTextTrim(UUID_ELEMENT, NAMESPACE)));
//                userEntry.setWorkflowUserId(new WorkflowUserId(userElement.getChildTextTrim(WORKFLOW_ID_ELEMENT, NAMESPACE)));                
//                userEntries.add(userEntry);
//           }
//       }
//       return userEntries;
//    }
    
//    public void save(WorkflowUser user) {
//		if (user == null) {
//			return;
//		}
//		Long entityId = KNSServiceLocator.getSequenceAccessorService().getNextAvailableSequenceNumber("KRIM_ENTITY_ENT_TYP_ID_S");
//		KimEntityImpl entity = new KimEntityImpl();
//		entity.setActive(true);
//		entity.setEntityId("" + entityId);
//		
//		Long entityTypeId = KNSServiceLocator.getSequenceAccessorService().getNextAvailableSequenceNumber("KRIM_ENTITY_ENT_TYP_ID_S");
//		EntityEntityTypeImpl entityType = new EntityEntityTypeImpl();
//		entity.getEntityTypes().add(entityType);
//		entityType.setEntityTypeCode("PERSON");
//		entityType.setEntityId(entity.getEntityId());
//		entityType.setEntityEntityTypeId(""+entityTypeId);
//		entityType.setActive(true);
//		
//		Long entityNameId = KNSServiceLocator.getSequenceAccessorService().getNextAvailableSequenceNumber("KRIM_ENTITY_NM_ID_S");
//		EntityNameImpl name = new EntityNameImpl();
//		name.setActive(true);
//		name.setEntityNameId("" + entityNameId);
//		name.setEntityId(entity.getEntityId());
//		name.setNameTypeCode("PREFERRED");
//		name.setFirstName(user.getGivenName());
//		name.setMiddleName("");
//		name.setLastName(user.getLastName());
//		name.setDefault(true);
//		
//		entity.getNames().add(name);
//				
//		KNSServiceLocator.getBusinessObjectService().save(entity);
//		
//		if (!StringUtils.isBlank(user.getEmailAddress())) {
//			Long emailId = KNSServiceLocator.getSequenceAccessorService().getNextAvailableSequenceNumber("KRIM_ENTITY_EMAIL_ID_S");
//			EntityEmailImpl email = new EntityEmailImpl();
//			email.setActive(true);
//			email.setEntityEmailId("" + emailId);
//			email.setEntityTypeCode("PERSON");
//			email.setEmailTypeCode("CAMPUS");
//			email.setEmailAddress(user.getEmailAddress());
//			email.setDefault(true);
//			email.setEntityId(entity.getEntityId());
//			KNSServiceLocator.getBusinessObjectService().save(email);
//		}
//		
//		KimPrincipalImpl principal = new KimPrincipalImpl();
//		principal.setActive(true);
//		principal.setPrincipalName(user.getAuthenticationUserId().getId());
//		principal.setPrincipalId(user.getWorkflowId());
//		principal.setEntityId(entity.getEntityId());
//		KNSServiceLocator.getBusinessObjectService().save(principal);
//	}

//	public static WorkflowUser convertPersonToWorkflowUser(Person person) {
//		if (person == null) {
//			logger.error("KimUserService.convertPersonToWorkflowUser() was passed a null Person object");
//			return null;
//		}
//		BaseWorkflowUser user = new org.kuali.rice.kns.workflow.bo.WorkflowUser();
//		user.setWorkflowUserId(new WorkflowUserId(person.getPrincipalId()));
//		user.setLockVerNbr(1);
//		user.setAuthenticationUserId(new org.kuali.rice.kew.user.AuthenticationUserId(person.getPrincipalName()));
//		user.setDisplayName(person.getName());
//		user.setEmailAddress(person.getEmailAddress());
//		user.setEmplId(new EmplId(person.getExternalId("EMPLOYEE")));
//		user.setGivenName(person.getFirstName());
//		user.setLastName(person.getLastName());
//		user.setUuId(new UuId(person.getPrincipalId()));
//		user.setCreateDate(new Timestamp(new Date().getTime()));
//		user.setLastUpdateDate(new Timestamp(new Date().getTime()));
//		return user;
//	}

}
