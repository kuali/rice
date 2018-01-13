/**
 * Copyright 2005-2018 The Kuali Foundation
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
package org.kuali.rice.core.api.util.type;

import java.math.BigDecimal;



/**
 * This class is a wrapper around java.math.BigDecimal for percents.
 *
 *
 */
public class KualiPercent extends KualiDecimal {
    private static final long serialVersionUID = 1L;

    public static final KualiPercent ZERO = new KualiPercent(BigDecimal.ZERO);

    protected static final KualiPercent ONE_HUNDRED = new KualiPercent("100.00");

    /**
     * Constructs a KualiPercent.java.
     *
     * @param d
     */
    public KualiPercent(double d) {
        super(d);

    }

    public KualiPercent(BigDecimal d) {
        super(d);
    }

    public KualiPercent(String d) {
        super(d);
    }

    /**
     * @return the percent as a decimal
     */
    public KualiDecimal toKualiDecimal() {
        return divide(ONE_HUNDRED);
    }
}
