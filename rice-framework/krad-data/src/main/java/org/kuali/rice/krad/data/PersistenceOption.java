/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.krad.data;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

public class PersistenceOption implements Serializable {
	private static final long serialVersionUID = 1L;

    public static PersistenceOption LINK = new PersistenceOption("org.kuali.rice.krad.data.LINK");
    public static PersistenceOption FLUSH = new PersistenceOption("org.kuali.rice.krad.data.FLUSH");

    private final String optionId;

    public PersistenceOption(String optionId) {
        if (StringUtils.isBlank(optionId)) {
            throw new IllegalArgumentException("optionId must not be a null or blank value");
        }
        this.optionId = optionId;
    }

    public String getOptionId() {
        return this.optionId;
    }

}
