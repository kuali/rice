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

package edu.iu.uis.eden.test.web.framework;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import edu.iu.uis.eden.test.web.framework.schemes.LiteralScheme;
import edu.iu.uis.eden.test.web.framework.schemes.ResourceScheme;
import edu.iu.uis.eden.test.web.framework.schemes.URLScheme;
import edu.iu.uis.eden.test.web.framework.schemes.VariableScheme;

public interface PropertyScheme {
    public static final PropertyScheme VARIABLE_SCHEME = new VariableScheme();
    public static final PropertyScheme LITERAL_SCHEME = new LiteralScheme();
    public static final PropertyScheme RESOURCE_SCHEME = new ResourceScheme();
    public static final PropertyScheme URL_SCHEME = new URLScheme();

    /* interfaces can't have static initializers */
    public static final Collection SCHEMES = Collections.unmodifiableCollection(
                                                             Arrays.
                                                                 asList(new PropertyScheme[] {
                                                                    VARIABLE_SCHEME,
                                                                    LITERAL_SCHEME,
                                                                    RESOURCE_SCHEME,
                                                                    URL_SCHEME }));

    public String getName();
    public String getShortName();
    public Object load(Property property, ScriptState state);
}