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
package org.kuali.notification.services.ws.impl;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.TimeZone;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Load tester for notification
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
@Ignore
public class NotificationLoadTester extends NotificationUsageSimulator {
    private static final Logger LOG = Logger.getLogger(NotificationLoadTester.class);

    // from http://ws.apache.org/wss4j/xref/org/apache/ws/security/util/XmlSchemaDateFormat.html
    // thanks wss4j
    /***
     * DateFormat for Zulu (UTC) form of an XML Schema dateTime string.
     * This DateFormat will match the XSD datetime builtin type.
     */
    private static final DateFormat DATEFORMAT_XSD_ZULU = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"/*.SSS'Z'"*/);
    
    static {
        DATEFORMAT_XSD_ZULU.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    /**
     * 0 channel
     * 1 producer
     * 2 senders
     * 3 recipients
     * 4 deliverytype
     * 5 senddatetime
     * 6 autoremovedatetime
     * 7 priority
     * 8 message
     */
    protected static final String MSG_FORMAT_STRING = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><!-- A Simple Notification Message -->" +
    "<notification xmlns=\"ns:notification/NotificationRequest\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
    "xsi:schemaLocation=\"ns:notification/NotificationRequest resource:notification/NotificationRequest\"><!-- this is the " +
    "name of the notification channel --><!-- that has been registered in the system --><channel>{0}</channel>" +
    " <!-- this is the name of the producing system --><!-- the value must match a registered producer --><producer>{1}</producer>" +
    "<!-- these are the people that the message is sent on --><!-- behalf of --><senders>{2}</senders>" +
    "<!-- who is the notification going to? --><recipients>{3}" +
    "</recipients><!--  fyi or acknowledge --><deliveryType>{4}</deliveryType><!-- optional date and time that a notification should be sent -->" +
    "<!-- use this for scheduling a single future notification to happen --><sendDateTime>{5}</sendDateTime>" +
    "<!-- optional date and time that a notification should be removed --><!-- from all recipients'' lists, b/c the message no longer applies -->" +
    "<autoRemoveDateTime>{6}</autoRemoveDateTime><title>a title</title><!-- this is the name of the priority of the message -->" +
    "<!-- priorities are registered in the system, so your value --><!-- here must match one of the registered priorities --><priority>{7}</priority>" +
    "<!-- this is the name of the content type for the message --><!-- content types are registered in the system, so your value -->" +
    "<!-- here must match one of the registered contents --><contentType>Simple</contentType><!-- actual content of the message -->" +
    "<content xmlns=\"ns:notification/ContentTypeSimple\" xsi:schemaLocation=\"ns:notification/ContentTypeSimple resource:notification/ContentTypeSimple\">" +
    "<message>{8}</message></content></notification>";
    
    protected static final MessageFormat NOTIFICATION_TEMPLATE = new MessageFormat(MSG_FORMAT_STRING);

    protected static final String[] CHANNELS = { "Test Channel #1", "Test Channel #2" };
    //protected static final String[] CHANNELS = { "Kuali Rice Channel", "Library Events Channel", "Overdue Books" };
    protected static final String[] PRODUCERS = { "Test Producer #1", "Test Producer #2", "Test Producer #3", "Test Producer #4" };
    //protected static final String[] PRODUCERS = { "Notification System" };
    protected static final String[] RECIPIENTS = { "<group>Group0</group>", "<user>user1</user>", "<user>user2</user>", "<user>user3</user>", "<user>edna</user>", "<user>earl</user>" };
    protected static final String[] SENDERS = { "<sender>John Fereira</sender>", "<sender>Aaron Godert</sender>", "<sender>Aaron Hamid</sender>" };
    protected static final String[] DELIVERY_TYPES = { "FYI", "ACK" };
    protected static final String[] PRIORITIES = { "Normal", "Low", "High" };

    protected static String generateXMLDateTime(Date date) {
        return DATEFORMAT_XSD_ZULU.format(date);
    }

    protected Random random = new Random();

    private String webServiceHost = null;

    protected String selectRandomEntry(String[] entries) {
        return entries[random.nextInt(entries.length)];
    }

    protected String generateRandomChannel() {
        return selectRandomEntry(CHANNELS);
    }

    protected String generateRandomRecipient() {
        return selectRandomEntry(RECIPIENTS);
    }
    
    protected String generateRandomProducer() {
        return selectRandomEntry(PRODUCERS);
    }
    
    protected String generateRandomSender() {
        return selectRandomEntry(SENDERS);
    }
    
    protected String generateRandomDeliveryType() {
        return selectRandomEntry(DELIVERY_TYPES);
    }

    protected String generateRandomPriority() {
        return selectRandomEntry(PRIORITIES);
    }

    protected String generateRandomSenders() {
        // senders must be unique, no duplicates
        Set<String> senderStrings = new HashSet<String>();
        /*ALTER TABLE NOTIFICATION_SENDERS
        ADD CONSTRAINT NOTIFICATION_SENDERS_UK1 UNIQUE
        (
        NOTIFICATION_ID,
        NAME
        )
         ENABLE
        ;*/
        // since senders must be unique, we obviously can't attempt to select
        // more than the maximum number of senders
        // (this will busy-spin until it gets the required number of unique
        // entries, oh well)
        int senders = 1 + random.nextInt(SENDERS.length);
        while (senderStrings.size() < senders) {
            String sender = generateRandomSender();
            if (!senderStrings.contains(sender)) {
                senderStrings.add(sender);
            }

        }
        StringBuilder sendersString = new StringBuilder();
        for (String s: senderStrings) {
            sendersString.append(s);
        }
        return sendersString.toString();
    }

    protected String generateRandomRecipients() {
        // recipients must be unique also
        Set<String> recipStrings = new HashSet<String>();
        /*ALTER TABLE NOTIFICATION_RECIPIENTS
        ADD CONSTRAINT NOTIFICATION_RECIPIENTS_UK1 UNIQUE
        (
        NOTIFICATION_ID,
        RECIPIENT_TYPE,
        RECIPIENT_ID
        )
         ENABLE
        ;
        */
        // 5% change of very large list of recipients
        boolean largeNumberOfRecipients = random.nextInt(100) < 5;
        int recipients = 1 + random.nextInt(5);
        if (largeNumberOfRecipients) {
            recipients += 20 + random.nextInt(10);
        }
        recipients = Math.max(recipients, RECIPIENTS.length);

        while (recipStrings.size() < recipients) {
            String recip = generateRandomRecipient();
            if (!recipStrings.contains(recip)) {
                recipStrings.add(recip);
            }
        }

        StringBuilder recipientsString = new StringBuilder();
        for (String s: recipStrings) {
            recipientsString.append(s);
        }
        
        return recipientsString.toString();
    }

    protected String generateRandomMessage() {
        int size = 100 + random.nextInt(300);
        return RandomStringUtils.randomAlphanumeric(size);
    }

    @Override
    protected String generateNotificationMessage() {
        /**
         * 0 channel
         * 1 producer
         * 2 senders
         * 3 recipients
         * 4 deliverytype
         * 5 senddatetime
         * 6 autoremovedatetime
         * 7 priority
         * 8 message
         */
        String channel = generateRandomChannel();
        String producer = generateRandomProducer();
        String senders = generateRandomSenders();
        String recipients = generateRandomRecipients();
        String deliverytype = generateRandomDeliveryType();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR, -1);
        // set send time back an hour to make sure it's sent immediately
        String senddatetime = generateXMLDateTime(cal.getTime());
        // set autoremove time forward a day
        cal = Calendar.getInstance();
        cal.add(Calendar.DATE, +1);
        String autoRemoveDateTime = generateXMLDateTime(cal.getTime());
        String priority = generateRandomPriority();
        String message = generateRandomMessage();
        
        String[] args = new String[] { channel, producer, senders, recipients, deliverytype, senddatetime, autoRemoveDateTime, priority, message };
        //LOG.info("args: " + channel + " " + producer + " " + senders + " " + recipients + " " + deliverytype + " " + senddatetime + " " + autoRemoveDateTime + " " + priority + " " + message);
        
        String notification;
        synchronized (NOTIFICATION_TEMPLATE) {
            //LOG.info(MSG_FORMAT_STRING);
            //LOG.info(NOTIFICATION_TEMPLATE);
            //LOG.info(NOTIFICATION_TEMPLATE.toPattern());
            //LOG.info("ARGS: " + NOTIFICATION_TEMPLATE.getFormats().length);
            assertTrue(NOTIFICATION_TEMPLATE.getFormats().length == 9);
            notification = NOTIFICATION_TEMPLATE.format(args);
        }
        //LOG.debug("Generated notification: " + notification);
        return notification;
    }

    @Override
    protected int getNumThreads() {
        return 10;
    }

    @Override
    protected long getSleepTimeMillis() {
        return 2000;
    }

    @Override
    protected long getTestDuration() {
        return 1000 * 60; // * 5; // 5 minutes
    }

    /* I guess preventTransaction() is not available in this version of spring?? 
    @Override
    public void runBare() throws Throwable {
        preventTransaction();
    }
    */

    /**
     * Override runTest directly, as it is seems not to be called by either Eclipse or Ant JUnit test runner
     * (which is the behavoir we want: we don't want a load test included with all the other tests)
     */
    @Test
    public void runTest() throws Throwable {
        // don't bother rolling back anything that was committed within the unit test transaction
        //setComplete();

        // expose this method in this subclass for JUnit
        super.runSimulation();

    }

    @Override
    protected int getWebServicePort() {
        return 8080;
    }

    @Override
    protected String getWebServiceHost() {
        return webServiceHost;
    }

    public void setWebServiceHost(String s) {
        this.webServiceHost = s;
    }

    @Override
    protected boolean shouldStartWebService() {
        return false;
    }

    public static void main(String[] args) {
        // can't use anonymous class to expose the real test
        // http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4071957
        NotificationLoadTester test = new NotificationLoadTester();
        if (args.length > 0) {
            test.setWebServiceHost(args[0]);
        }
        //TestResult result = TestRunner.run(test);
    }
}