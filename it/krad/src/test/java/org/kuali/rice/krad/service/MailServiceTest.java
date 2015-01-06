/*
 * Copyright 2006-2014 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.krad.service;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;
import org.junit.Test;
import org.kuali.rice.core.api.CoreApiServiceLocator;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.api.mail.MailMessage;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.krad.bo.Note;
import org.kuali.rice.krad.service.impl.MailServiceImpl;
import org.kuali.rice.krad.util.NoteType;
import org.kuali.test.KRADTestCase;

import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * This class is used to test the {@link org.kuali.rice.krad.service.MailService} implementation
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class MailServiceTest extends KRADTestCase {

    boolean devMode;

    @Override
    public void setUp() throws Exception {
        // Set the log.email property to true
        System.setProperty("rice.krad.dev.log.email", "true");
        super.setUp();
    }

    @Override
    public void tearDown() throws Exception {
        try {
            System.clearProperty("rice.krad.dev.log.email");
        } finally {
            super.tearDown();
        }
    }

    /**
     * This method sets the "rice.krad.dev.log.email" config proprety to true and tests to see if the MailService will log the
     * email content instead of sending an email
     * 
     * @throws Exception
     */
    @Test public void testMailLogService() throws Exception {

        // Create a new FileAppender in the target directory to log the mailservice output
        String fileName = getBaseDir() + "/target/maillog.txt";

        // Clear the file if one exists already
        File logFile = new File(fileName);
        PrintWriter writer = new PrintWriter(logFile);
        writer.print("");
        writer.close();

        FileAppender fa = new FileAppender();
        fa.setName("FileLogger");
        fa.setFile(fileName);
        fa.setThreshold(Level.DEBUG);
        fa.setLayout(new PatternLayout("%d %-5p [%c{1}] %m%n"));
        fa.setAppend(true);
        fa.activateOptions();

        org.apache.log4j.Logger.getLogger(MailServiceImpl.class).addAppender(fa);

        MailService mailService = GlobalResourceLoader.getService("mailService");

        MailMessage message = new MailMessage();
        message.setMessage("Test Message");
        message.setSubject("Test Subject");

        mailService.sendMessage(message);

        // Check to see if the log file has the mail text
        Scanner scanner = new Scanner(logFile);
        boolean foundMessage = false;
        boolean foundSubject = false;
        while(scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if(line.contains("Test Message")) foundMessage = true;
            if(line.contains("Test Subject")) foundSubject = true;
        }

        fa.close();
        scanner.close();

        // Remove the temporary file
        Path filePath = Paths.get(fileName);
        Files.delete(filePath);

        assertTrue(foundMessage);
        assertTrue(foundSubject);
    }
    
}

