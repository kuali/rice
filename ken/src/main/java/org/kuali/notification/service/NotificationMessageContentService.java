/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.notification.service;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;

import org.kuali.notification.bo.Notification;
import org.kuali.notification.bo.NotificationResponse;
import org.kuali.notification.exception.InvalidXMLException;
import org.xml.sax.SAXException;

/**
 * Notification Message Content service - handles parsing the notification XML message and also marshalling out BOs for the response.
 * @see <a href="http://wiki.library.cornell.edu/wiki/display/notsys/Hi-Level+Service+Interface+Definitions#Hi-LevelServiceInterfaceDefinitions-NotificationMessageContentService">NotificationMessageContentService</a>
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public interface NotificationMessageContentService {
    /**
     * Parses a Notification request message into business objects.  Performs syntactic and semantic validation.  
     * This method takes an InputStream.
     * @param stream request message stream
     * @return Notification business object
     * @throws SAXException
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws InvalidXMLException
     */
    public Notification parseNotificationRequestMessage(InputStream stream) throws IOException, InvalidXMLException;
    
    /**
     * Parses a Notification request message into business objects.  Performs syntactic and semantic validation.  
     * This method takes a String of XML.
     * @param notificationMessageAsXml
     * @return
     * @throws IOException
     * @throws InvalidXMLException
     */
    public Notification parseNotificationRequestMessage(String notificationMessageAsXml) throws IOException, InvalidXMLException;
    
    /**
     * Generates a Notification response message
     * @param response
     * @return String XML representation of a Notification response object
     */
    public String generateNotificationResponseMessage(NotificationResponse response);

    /**
     * This method is responsible for marshalling out the passed in Notification object in and XML representation. 
     * @param notification
     * @return String of XML.
     */
    public String generateNotificationMessage(Notification notification);
    
    /**
     * This method is responsible for marshalling out the passed in Notification object in and XML representation, with 
     * the addition of adding the specific recipient to the recipients list and removing the others. 
     * @param notification
     * @param userRecipientId
     * @return String of XML.
     */
    public String generateNotificationMessage(Notification notification, String userRecipientId);
    
    /**
     * This method parses out the serialized XML version of Notification BO and populates a Notification BO with it.
     * @param xmlAsBytes
     * @return Notification
     * @throws Exception
     */
    public Notification parseSerializedNotificationXml(byte[] xmlAsBytes) throws Exception;
}
