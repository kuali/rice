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

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import edu.emory.mathcs.backport.java.util.concurrent.ExecutorService;
import edu.emory.mathcs.backport.java.util.concurrent.Executors;

/**
 * This class is the simulator for s2s notification requests for testers.
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public abstract class NotificationUsageSimulator extends NotificationWebServiceTestCaseBase {
    private static final Logger LOG = Logger.getLogger(NotificationUsageSimulator.class);

    private ExecutorService executorService;
    private List<Throwable> exceptions = new ArrayList<Throwable>();
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
        executorService = Executors.newFixedThreadPool(getNumThreads());
    }

    @Override
    public void tearDown() throws Exception {
        executorService.shutdownNow();
        super.tearDown();
    }

    protected abstract int getNumThreads();

    protected abstract long getSleepTimeMillis();

    protected abstract long getTestDuration();

    protected abstract String generateNotificationMessage();

    protected void assertFinalState() {}
    
    protected void initialize() {}

    public void runSimulation() throws Exception {
        long start = System.currentTimeMillis();
        long duration = getTestDuration();
        final URL webserviceEndpoint = new URL(getWebServiceURL());
        final Pattern pattern = Pattern.compile("(?s).*<notificationId>([0-9]+)</notificationId>.*");
        initialize();
        while (duration == 0 || System.currentTimeMillis() - start < duration) {
            executorService.submit(new Runnable() {
                public void run() {
                    try {
                        /*
                        NotificationWebServiceSoapBindingStub stub = new NotificationWebServiceSoapBindingStub(webserviceEndpoint, null);
    
                        String notification = generateNotificationMessage();
                        
                        LOG.info(Thread.currentThread().getName() + " Sending notification");
                        String responseAsXml = stub.sendNotification(notification);

                        assertTrue(StringUtils.contains(responseAsXml, "<status>Success</status>"));

                        //LOG.info("response: " + responseAsXml);

                        Matcher matcher = pattern.matcher(responseAsXml);
                        assertTrue(matcher.matches());
                        
                        if (matcher.groupCount() > 0) {
                            
                            String idAsString = matcher.group(1);
                            Long id = Long.valueOf(idAsString);
                            assertNotNull(id);
                            LOG.info("Successfully sent notification id: " + id);
                        }*/
                    } catch (Exception e) {
                        LOG.info("Exception occurred sending notification", e);
                        synchronized (exceptions) {
                            exceptions.add(e);
                        }
                    }
                }
            });
            try {
                Thread.sleep(getSleepTimeMillis());
            } catch (InterruptedException ie) {
                LOG.info("Interrupted while sleeping", ie);
            }
        }
        
        assertFinalState();
    }
}