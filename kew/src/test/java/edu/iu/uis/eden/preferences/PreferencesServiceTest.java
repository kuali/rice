/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package edu.iu.uis.eden.preferences;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.kuali.workflow.test.KEWTestCase;

import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.user.AuthenticationUserId;
import edu.iu.uis.eden.user.WorkflowUser;
import edu.iu.uis.eden.useroptions.UserOptionsService;

public class PreferencesServiceTest extends KEWTestCase {

    /**
     * Test that the preferences are saved by default when going through the preferences service.  This 
     * means that the preferences service will persist any user option that was not in the db when it went
     * to fetch that preference.
     */
	@Test public void testPreferencesDefaultSave() throws Exception {
       //verify that user doesn't have any preferences in the db.
        
       UserOptionsService userOptionsService = KEWServiceLocator.getUserOptionsService();
       WorkflowUser user = KEWServiceLocator.getUserService().getWorkflowUser(new AuthenticationUserId("rkirkend"));
       Collection userOptions = userOptionsService.findByWorkflowUser(user);
       assertTrue("Preferences should be empty", userOptions.isEmpty());
       
       PreferencesService preferencesService = KEWServiceLocator.getPreferencesService();
       preferencesService.getPreferences(user);
       userOptions = userOptionsService.findByWorkflowUser(user);
       assertTrue("Preferences should not be empty", !userOptions.isEmpty());
    }
	
	/**
     * Tests default saving concurrently which can cause a race condition on startup
     * that leads to constraint violations
     */
    @Test public void testPreferencesConcurrentDefaultSave() throws Throwable {
       //verify that user doesn't have any preferences in the db.
       final UserOptionsService userOptionsService = KEWServiceLocator.getUserOptionsService();
       final WorkflowUser user = KEWServiceLocator.getUserService().getWorkflowUser(new AuthenticationUserId("rkirkend"));
       Collection userOptions = userOptionsService.findByWorkflowUser(user);
       assertTrue("Preferences should be empty", userOptions.isEmpty());

       final PreferencesService preferencesService = KEWServiceLocator.getPreferencesService();
       Runnable getPrefRunnable = new Runnable() {
           public void run() {
               preferencesService.getPreferences(user);
               Collection updatedOptions = userOptionsService.findByWorkflowUser(user);
               assertTrue("Preferences should not be empty", !updatedOptions.isEmpty());        
           }           
       };
       final List<Throwable> errors = new ArrayList<Throwable>();
       Thread.UncaughtExceptionHandler ueh = new Thread.UncaughtExceptionHandler() {
           public void uncaughtException(Thread thread, Throwable error) {
               errors.add(error);
           }
       };
       
       // 3 threads should do
       Thread t1 = new Thread(getPrefRunnable);
       Thread t2 = new Thread(getPrefRunnable);
       Thread t3 = new Thread(getPrefRunnable);
       t1.setUncaughtExceptionHandler(ueh);
       t2.setUncaughtExceptionHandler(ueh);
       t3.setUncaughtExceptionHandler(ueh);
       t1.start();
       t2.start();
       t3.start();
       t1.join();
       t2.join();
       t3.join();
       
       if (errors.size() > 0) {
           throw errors.iterator().next();
       }
    }
}