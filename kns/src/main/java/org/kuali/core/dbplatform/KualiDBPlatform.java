/*
 * Copyright 2006-2007 The Kuali Foundation.
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

import java.util.List;

import org.apache.ojb.broker.query.Criteria;

/**
 * This interface should represent the bare minimum SQLcalls needed to specifically handle differences between RDBMS
 */
public interface KualiDBPlatform {
    public static final String LINE_DELIMITER = "\t\n";
    public static final String FIELD_DELIMITER = "\t";

    public String getCurTimeFunction();

    public String getStrToDateFunction();

    public String getDateFormatString(String dateFormatString);

    public void applyLimit(Integer limit, Criteria criteria);
}
