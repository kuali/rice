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
package edu.iu.uis.eden.test.stress;

import java.util.Map;

/**
 * This interface defines a Test fixture which is executed multiple times until the doWork method
 * return true.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public interface Test {

    /**
     * Executes the test, returning true when the test has completed, false otherwise.
     * To force the test to fail, throw an exception.
     */
	public boolean doWork() throws Exception;
    
    /**
     * Sets the request parameters coming in from the test harness servlet.
     */
    public void setParameters(Map parameters);
	
}
