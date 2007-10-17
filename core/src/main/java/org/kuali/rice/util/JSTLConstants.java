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
package org.kuali.rice.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

/**
 * Have constants class extend this class to expose them to JSTL as a HashMap.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class JSTLConstants extends HashMap {

	private static final long serialVersionUID = 6701136401021219281L;
	private boolean initialised = false;

    public JSTLConstants() {
        Class c = this.getClass();
        Field[] fields = c.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {

            Field field = fields[i];
            int modifier = field.getModifiers();
            if (Modifier.isFinal(modifier) && !Modifier.isPrivate(modifier))
                try {
                    this.put(field.getName(), field.get(this));
                } catch (IllegalAccessException e) {
                }
        }
        initialised = true;
    }

    public void clear() {
        if (!initialised)
            super.clear();
        else
            throw new UnsupportedOperationException("Cannot modify this map");
    }

    public Object put(Object key, Object value) {
        if (!initialised)
            return super.put(key, value);
        else
            throw new UnsupportedOperationException("Cannot modify this map");
    }

    public void putAll(Map m) {
        if (!initialised)
            super.putAll(m);
        else
            throw new UnsupportedOperationException("Cannot modify this map");
    }

    public Object remove(Object key) {
        if (!initialised)
            return super.remove(key);
        else
            throw new UnsupportedOperationException("Cannot modify this map");
    }
}