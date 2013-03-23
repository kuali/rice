/**
 * Copyright 2005-2013 The Kuali Foundation
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
package edu.samplu.common;

/**
 * Possible way to help keep JUnit and NG separate, the idea being that
 * framework specific Failures will be generalized and encapsulated behind
 * the SeleneseFailable
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface SeleneseFailable {

    /**
     * Fail in a framework appropriate way.
     * @link SelenseFailable#seFail
     * @param string message to display for the failure.
     */
    void seFail(String string);
}
