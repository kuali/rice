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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Ignore;
import org.junit.Test;

/**
 * This class runs the s2s generator that throws notifications at the system to simulate that situation for users.
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
@Ignore
public class NotificationTestUsageGenerator extends NotificationUsageSimulator {
    private String webServiceHost = null;

    private final String[] NOTIFICATIONS = new String[5];

    protected static String readStream(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int read;
        try {
            while ((read = is.read(buf)) != -1) {
                baos.write(buf, 0, read);
            }
        } finally {
            is.close();
        }
        return new String(baos.toByteArray());
    }

    protected int counter = 0;

    @Override
    protected void initialize() {
        try {
            NOTIFICATIONS[0] = readStream(getClass().getResourceAsStream("StaticMessage0.xml"));
            NOTIFICATIONS[1] = readStream(getClass().getResourceAsStream("StaticMessage1.xml"));
            NOTIFICATIONS[2] = readStream(getClass().getResourceAsStream("StaticMessage2.xml"));
            NOTIFICATIONS[3] = readStream(getClass().getResourceAsStream("StaticMessage3.xml"));
            NOTIFICATIONS[4] = readStream(getClass().getResourceAsStream("StaticMessage4.xml"));
        } catch (IOException ioe) {
            throw new RuntimeException("Error loading test notification data", ioe);
        }
    }

    @Override
    protected synchronized String generateNotificationMessage() {
        String notification = NOTIFICATIONS[counter];
        counter = (counter + 1) % NOTIFICATIONS.length;
        return notification;
    }

    @Override
    protected int getNumThreads() {
        return 1;
    }

    @Override
    protected long getSleepTimeMillis() {
        return 1000 * 60 * 30; // 30 minutes
    }

    @Override
    protected long getTestDuration() {
        return 0; // forever
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

    public static void main(String[] args) {
        // can't use anonymous class to expose the real test
        // http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4071957
        NotificationTestUsageGenerator test = new NotificationTestUsageGenerator();
        if (args.length > 0) {
            test.setWebServiceHost(args[0]);
        }
        //TestResult result = TestRunner.run(test);
    }
}