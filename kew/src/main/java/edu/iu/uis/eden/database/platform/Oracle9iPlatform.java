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
package edu.iu.uis.eden.database.platform;

/**
 * This allows us to use Oracle9i as an alias for the Oracle platform.  Oracle9i is one
 * of the platforms supported by OJB so we want to keep our platform identifiers
 * consistent with OJB.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class Oracle9iPlatform extends OraclePlatform {}