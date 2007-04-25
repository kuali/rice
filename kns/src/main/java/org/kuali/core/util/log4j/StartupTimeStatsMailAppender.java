/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.core.util.log4j;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;
import org.kuali.Constants;
import org.kuali.core.mail.InvalidAddressException;
import org.kuali.core.mail.MailMessage;
import org.kuali.rice.KNSServiceLocator;

public class StartupTimeStatsMailAppender extends AppenderSkeleton {
    public static final String STARTUP_NDC_INFO = "startup";
    private static final long MILLISECONDS_TO_SECONDS_CONVERSION = 1000;
    private static final int MAX_LENGTH_OF_PRINTED_MESSAGE = 20;
    private static final String VALID_EMAIL_ADDRESS_REGEXP = ".+@.+\\.[a-z]+";
    private String address;
    private String version;
    private long startTime;
    List<String[]> statistics;

    public StartupTimeStatsMailAppender(long startTime) {
        super();
        ResourceBundle configurationProperties = ResourceBundle.getBundle(Constants.CONFIGURATION_FILE_NAME);
        address = configurationProperties.getString(Constants.STARTUP_STATS_MAILING_LIST_KEY);
        if (doIt()) {
            version = configurationProperties.getString(Constants.VERSION_KEY);
            this.startTime = startTime;
            statistics = new ArrayList();
            addFilter(new NDCFilter(STARTUP_NDC_INFO));
        }
    }

    /**
     * @see org.apache.log4j.AppenderSkeleton#append(org.apache.log4j.spi.LoggingEvent)
     */
    @Override
    protected void append(LoggingEvent event) {
        if (doIt()) {
            String message = event.getRenderedMessage();
            if (event.getRenderedMessage().length() > MAX_LENGTH_OF_PRINTED_MESSAGE) {
                message = message.substring(0, MAX_LENGTH_OF_PRINTED_MESSAGE);
            }
            statistics.add(new String[] {(event.timeStamp - startTime) / MILLISECONDS_TO_SECONDS_CONVERSION + "", event.timeStamp - startTime + "", event.getLoggerName(), message});
        }
    }

    /**
     * @see org.apache.log4j.AppenderSkeleton#requiresLayout()
     */
    @Override
    public boolean requiresLayout() {
        return false;
    }

    /**
     * @see org.apache.log4j.AppenderSkeleton#close()
     */
    @Override
    public void close() {
        if (doIt()) {
            try {
                MailMessage mailMessage = new MailMessage();
                mailMessage.setFromAddress(address);
                mailMessage.addToAddress(address);
                StringBuffer log = new StringBuffer();
                String startupTimeInSeconds = null;
                String startupTimeInMilliseconds = null;
                for (String[] statistic : statistics) {
                    log.append("\n").append(statistic[0]).append(" : ").append(statistic[1]).append(" : ").append(statistic[2]).append(" : ").append(statistic[3]);
                    startupTimeInSeconds = statistic[0];
                    startupTimeInMilliseconds = statistic[1];
                }
                mailMessage.setSubject(new StringBuffer(version).append(" started up in ").append(startupTimeInSeconds).append(" seconds / ").append(startupTimeInMilliseconds).append(" milliseconds").toString());
                mailMessage.setMessage(log.toString());
                KNSServiceLocator.getMailService().sendMessage(mailMessage);
            }
            catch (InvalidAddressException e) {
                throw new RuntimeException("MailAppender caught exception while trying to mail startup log", e);
            }
        }
    }

    private boolean doIt() {
        return !StringUtils.isBlank(address) && address.matches(VALID_EMAIL_ADDRESS_REGEXP);
    }
}
