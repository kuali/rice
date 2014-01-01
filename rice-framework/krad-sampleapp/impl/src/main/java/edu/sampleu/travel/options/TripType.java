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
package edu.sampleu.travel.options;

import org.kuali.rice.core.api.mo.common.Coded;

/**
 * This enum provides location based trip options for travel destinations
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public enum TripType implements Coded {

    IS("IS", "In State"), IN("IN", "International"), OS("OS", "Out of State");

    private final String code;
    private final String label;

    TripType(String code, String label) {
        this.code = code;
        this.label = label;
    }

    @Override
    public String getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

}
