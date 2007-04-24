/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.core.web.struts.action;

import java.math.BigDecimal;
import java.math.BigInteger;

import javax.servlet.ServletException;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.BigDecimalConverter;
import org.apache.commons.beanutils.converters.BigIntegerConverter;
import org.apache.commons.beanutils.converters.BooleanConverter;
import org.apache.commons.beanutils.converters.ByteConverter;
import org.apache.commons.beanutils.converters.CharacterConverter;
import org.apache.commons.beanutils.converters.DoubleConverter;
import org.apache.commons.beanutils.converters.FloatConverter;
import org.apache.commons.beanutils.converters.IntegerConverter;
import org.apache.commons.beanutils.converters.LongConverter;
import org.apache.commons.beanutils.converters.ShortConverter;
import org.apache.struts.action.ActionServlet;

public class KualiActionServlet extends ActionServlet {

    /**
     * <p>Initialize other global characteristics of the controller servlet.</p>
     * Overridden to remove the ConvertUtils.deregister() command that caused problems 
     * with the concurrent data dictionary load.  (KULRNE-4405)
     * 
     * @exception ServletException if we cannot initialize these resources
     */
    protected void initOther() throws ServletException {

        String value = null;
        value = getServletConfig().getInitParameter("config");
        if (value != null) {
            config = value;
        }

        // Backwards compatibility for form beans of Java wrapper classes
        // Set to true for strict Struts 1.0 compatibility
        value = getServletConfig().getInitParameter("convertNull");
        if ("true".equalsIgnoreCase(value)
            || "yes".equalsIgnoreCase(value)
            || "on".equalsIgnoreCase(value)
            || "y".equalsIgnoreCase(value)
            || "1".equalsIgnoreCase(value)) {

            convertNull = true;
        }

        if (convertNull) {
            ConvertUtils.register(new BigDecimalConverter(null), BigDecimal.class);
            ConvertUtils.register(new BigIntegerConverter(null), BigInteger.class);
            ConvertUtils.register(new BooleanConverter(null), Boolean.class);
            ConvertUtils.register(new ByteConverter(null), Byte.class);
            ConvertUtils.register(new CharacterConverter(null), Character.class);
            ConvertUtils.register(new DoubleConverter(null), Double.class);
            ConvertUtils.register(new FloatConverter(null), Float.class);
            ConvertUtils.register(new IntegerConverter(null), Integer.class);
            ConvertUtils.register(new LongConverter(null), Long.class);
            ConvertUtils.register(new ShortConverter(null), Short.class);
        }

    }
    
}
