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
package edu.iu.uis.eden.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.log4j.Logger;

/**
 * Dumps the fields of the given class.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class ClassDumper {
    private static final Logger LOG = Logger.getLogger(ClassDumper.class);

    public static void dumpFieldsToLog(Object o) {
        if (LOG.isDebugEnabled()) {
            LOG.debug(dumpFields(o));
        } else if (LOG.isInfoEnabled()) {
            if (o == null) {
                LOG.info("null");
            } else {
                LOG.info(o.getClass() + ": " + o.toString());
            }
        }
    }

    public static String dumpFields(Object o) {
        StringBuffer buf = new StringBuffer();

        if (o == null) {
            return "NULL";
        }

        Class clazz = o.getClass();
        // maybe just iterating over getter methods themselves would be a better strategy?
        // or maybe just jakarta commons lang ToStringBuilder.reflectionToString(String, MULTI_LINE_STYLE):
        // http://jakarta.apache.org/commons/lang/api/org/apache/commons/lang/builder/ToStringBuilder.html
        Field[] fields = clazz.getDeclaredFields();

        for (int i = 0; i < fields.length; ++i) {
            try {
                String methodName = "get" + fields[i].getName().substring(0, 1).toUpperCase() + fields[i].getName().substring(1);
                Method method = clazz.getMethod(methodName, null);
                Object value = method.invoke(o, null);
                buf.append(fields[i].getName()).append(" : ");

                if (value == null) {
                    buf.append("null\n");
                } else {
                    buf.append(value.toString()).append("\n");
                }
            } catch (IllegalAccessException e) {
                buf.append(fields[i].getName()).append(" unavailable by security policy\n");
            } catch (NoSuchMethodException ex) {
                buf.append(fields[i].getName()).append(" no getter method for this field\n");
            } catch (InvocationTargetException ex) {
                buf.append(fields[i].getName()).append(" unable to invoke the method on target\n");
            }
        }

        return buf.toString();
    }
}

/*
 * Copyright 2003 The Trustees of Indiana University. All rights reserved.
 * 
 * This file is part of the EDEN software package. For license information, see
 * the LICENSE file in the top level directory of the EDEN source distribution.
 */