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
package org.kuali.notification.core;

/**
 * Interface describing an object life cycle
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public interface Lifecycle {
    /**
     * This method starts the lifecycle.
     * @throws Exception
     */
    public void start() throws Exception;
    
    /**
     * This method stops the lifecycle.
     * @throws Exception
     */
    public void stop() throws Exception;
    
    /**
     * This method returns whether the lifecycle is running or not.
     * @return
     */
    public boolean isStarted();
}