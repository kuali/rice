/**
 * Copyright 2005-2011 The Kuali Foundation
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
package org.kuali.rice.core.api.search;

/**
 * Represents a search range
 */
public class Range {
    private String lowerBoundValue;
    private String upperBoundValue;
    private boolean lowerBoundInclusive = true;
    private boolean upperBoundInclusive = true;

    public String getLowerBoundValue() {
        return lowerBoundValue;
    }

    public void setLowerBoundValue(String lowerBoundValue) {
        this.lowerBoundValue = lowerBoundValue;
    }

    public String getUpperBoundValue() {
        return upperBoundValue;
    }

    public void setUpperBoundValue(String upperBoundValue) {
        this.upperBoundValue = upperBoundValue;
    }

    public boolean isLowerBoundInclusive() {
        return lowerBoundInclusive;
    }

    public void setLowerBoundInclusive(boolean lowerBoundInclusive) {
        this.lowerBoundInclusive = lowerBoundInclusive;
    }

    public boolean isUpperBoundInclusive() {
        return upperBoundInclusive;
    }

    public void setUpperBoundInclusive(boolean upperBoundInclusive) {
        this.upperBoundInclusive = upperBoundInclusive;
    }
    
}
