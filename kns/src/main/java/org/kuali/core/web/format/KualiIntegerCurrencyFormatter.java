/*
 * Copyright 2006 The Kuali Foundation.
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
package org.kuali.core.web.format;

import java.text.NumberFormat;

import org.kuali.RiceKeyConstants;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.KualiInteger;

public class KualiIntegerCurrencyFormatter extends CurrencyFormatter {

    protected Object convertToObject(String target) {
        KualiDecimal value = (KualiDecimal) (super.convertToObject(target));
        return new KualiInteger(value.longValue());
    }

    /**
     * Returns a string representation of its argument formatted as a currency value.
     */
    public Object format(Object obj) {
        if (obj == null)
            return null;

        NumberFormat formatter = NumberFormat.getNumberInstance();
        String string = null;

        try {
            KualiInteger number = (KualiInteger) obj;
            string = formatter.format(number.doubleValue());
        }
        catch (IllegalArgumentException e) {
            throw new FormatException("formatting", RiceKeyConstants.ERROR_CURRENCY, obj.toString(), e);
        }
        catch (ClassCastException e) {
            throw new FormatException("formatting", RiceKeyConstants.ERROR_CURRENCY, obj.toString(), e);
        }

        return showSymbol() ? string : removeSymbol(string);
    }
}