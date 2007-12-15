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
package org.kuali.core.dbplatform;

/**
 * Used to reference classes/methods/constants which contain SQL statements rather than OJB calls.
 * 
 * This can be used within an IDE to find all locations of SQL that might need to be reviewed when implementing a new database platform.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public @interface RawSQL {

}
