/*
 * Copyright 2005-2006 The Kuali Foundation.
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
package edu.iu.uis.eden.xml;

import edu.iu.uis.eden.workgroup.Workgroup;

/**
 * Constants for element and attribute names from the {@link Workgroup}
 * XML.
 *
 * @see WorkgroupXmlHandler
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public interface WorkgroupXmlConstants {

    public static final String WORKGROUP_NAME = "workgroupName";
    public static final String ACTIVE_IND = "activeInd";
    public static final String WORKGROUP_CUR_IND = "workgroupCurInd";
    public static final String WORKFLOW_ID = "workflowId";
    public static final String AUTHENTICATION_ID = "authenticationId";
    public static final String UU_ID = "uuId";
    public static final String EMPL_ID = "emplId";
    public static final String MEMBERS = "members";

}
