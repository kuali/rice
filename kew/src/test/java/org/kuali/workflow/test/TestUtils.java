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
package org.kuali.workflow.test;

/**
 * This is a description of what this class does - ewestfal don't forget to fill this in.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class TestUtils {

    public static final String KEW_MODULE_NAME = "kew";
    public static final String BASEDIR_PROP = "basedir";

    public static String getModuleName() {
	return KEW_MODULE_NAME;
    }

    public static String getModuleBaseDir() {
	return getModuleName();
    }

    public static String getBaseDir() {
	if (System.getProperty(BASEDIR_PROP) == null) {
		System.setProperty(BASEDIR_PROP, System.getProperty("user.dir") + "/" + getModuleBaseDir());
	}
	return System.getProperty(BASEDIR_PROP);
    }


}
