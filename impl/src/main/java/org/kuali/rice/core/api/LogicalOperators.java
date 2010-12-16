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
package org.kuali.rice.core.api;

/**
 * This class contains a static list of logical operators used in rice.   
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class LogicalOperators {

	public static final String AND_LOGICAL_OPERATOR = "&&";
    public static final String OR_LOGICAL_OPERATOR = "|";
    public static final String NOT_LOGICAL_OPERATOR = "!";
    public static final String BETWEEN_OPERATOR = "..";
    public static final String NULL_OPERATOR = "NULL";
    public static final String[] QUERY_CHARACTERS = {"*", "?", "%", ">", "<", BETWEEN_OPERATOR, OR_LOGICAL_OPERATOR, NOT_LOGICAL_OPERATOR, "="};
    public static final String[] RANGE_CHARACTERS = {">=","<=",">","<",BETWEEN_OPERATOR};
	
	
}
