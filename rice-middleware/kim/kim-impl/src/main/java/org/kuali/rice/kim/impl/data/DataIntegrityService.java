/**
 * Copyright 2005-2016 The Kuali Foundation
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
package org.kuali.rice.kim.impl.data;

import java.util.List;

/**
 * A simple service for checking integrity of KIM data and for performing automatic repair of the data if issues are
 * found.
 *
 * @author Eric Westfall
 */
public interface DataIntegrityService {

    /**
     * Performs and integrity check on KIM data, returning a list of messages.
     *
     * @return a list of messages detailing the results of the integrity check
     */
    List<String> checkIntegrity();

    /**
     * Executes any automatic repair of data integrity issues on KIM data, returning a list of messages.
     *
     * @return a list of messages detailing the results of the data repair
     */
    List<String> repair();

}
