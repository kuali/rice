/*
 * Copyright 2005-2007 The Kuali Foundation.
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
// Created on May 8, 2006

package edu.iu.uis.eden.test.web.framework.schemes;

import edu.iu.uis.eden.test.web.framework.Property;
import edu.iu.uis.eden.test.web.framework.PropertyScheme;
import edu.iu.uis.eden.test.web.framework.ScriptState;

/**
 * A property scheme that just returns the literal value of the locator portion
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public final class LiteralScheme implements PropertyScheme {
    public String getName() {
        return "literal";
    }
    public String getShortName() {
        return "lit";
    }

    public Object load(Property property, ScriptState state) {
        // just returns the literal text
        return property.locator;
    }

    public String toString() {
        return "[LiteralScheme]";
    }
}