/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
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
package edu.iu.uis.eden.xml;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.DOMBuilder;
import org.xml.sax.SAXException;

import edu.iu.uis.eden.user.AuthenticationUserId;
import edu.iu.uis.eden.user.EmplId;
import edu.iu.uis.eden.user.UserService;
import edu.iu.uis.eden.user.UuId;
import edu.iu.uis.eden.user.WorkflowUser;
import edu.iu.uis.eden.user.WorkflowUserId;

/**
 * Parses users from XML.
 * 
 * @see WorkflowUser
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class UserXmlHandler {
    
    private static final Namespace NAMESPACE = Namespace.getNamespace("", "ns:workflow/User");

    private static final String USERS_ELEMENT = "users";
    private static final String USER_ELEMENT = "user";
    private static final String WORKFLOW_ID_ELEMENT = "workflowId";
    private static final String EMPL_ID_ELEMENT = "emplId";
    private static final String AUTHENTICATION_ID_ELEMENT = "authenticationId";
    private static final String UUID_ELEMENT = "uuId";
    private static final String EMAIL_ELEMENT = "emailAddress";
    private static final String DISPLAY_NAME_ELEMENT = "displayName";
    private static final String GIVEN_NAME_ELEMENT = "givenName";
    private static final String LAST_NAME_ELEMENT = "lastName";
    //private static final String CREATE_DATE_ELEMENT = "createDate";
    //private static final String LAST_UPDATE_DATE_ELEMENT = "lastUpdateDate";
    //private static final String ID_MISSING_INDICATOR_ELEMENT = "identificationMissingIndicator";
    
    public List parseUserEntries(UserService userService, InputStream file) throws JDOMException, SAXException, IOException, ParserConfigurationException, FactoryConfigurationError {
        List userEntries = new ArrayList();

        org.w3c.dom.Document w3cDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
        //LOG.debug("parsing document: " + XmlHelper.jotNode(w3cDocument.getFirstChild()));
        Document document = new DOMBuilder().build(w3cDocument);
        Element root = document.getRootElement();

        for (Iterator usersElementIt = root.getChildren(USERS_ELEMENT, NAMESPACE).iterator(); usersElementIt.hasNext();) {
            Element usersElement = (Element) usersElementIt.next();
            for (Iterator iterator = usersElement.getChildren(USER_ELEMENT, NAMESPACE).iterator(); iterator.hasNext();) {
                Element userElement = (Element) iterator.next();
                
                WorkflowUser userEntry = userService.getBlankUser();
                userEntry.setAuthenticationUserId(new AuthenticationUserId(userElement.getChildTextTrim(AUTHENTICATION_ID_ELEMENT, NAMESPACE)));
                
                userEntry.setDisplayName(userElement.getChildTextTrim(DISPLAY_NAME_ELEMENT, NAMESPACE));
                userEntry.setEmailAddress(userElement.getChildTextTrim(EMAIL_ELEMENT, NAMESPACE));
                userEntry.setEmplId(new EmplId(userElement.getChildTextTrim(EMPL_ID_ELEMENT, NAMESPACE)));
                userEntry.setGivenName(userElement.getChildTextTrim(GIVEN_NAME_ELEMENT, NAMESPACE));
                userEntry.setLastName(userElement.getChildTextTrim(LAST_NAME_ELEMENT, NAMESPACE));
                userEntry.setUuId(new UuId(userElement.getChildTextTrim(UUID_ELEMENT, NAMESPACE)));
                userEntry.setWorkflowUserId(new WorkflowUserId(userElement.getChildTextTrim(WORKFLOW_ID_ELEMENT, NAMESPACE)));                
                userEntries.add(userEntry);
           }
       }
       return userEntries;
    }
}