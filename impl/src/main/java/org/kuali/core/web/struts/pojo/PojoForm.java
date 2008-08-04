/*
 * Copyright 2004 Jonathan M. Lehr
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 * 
 * MODIFIED BY THE KUALI FOUNDATION
 */
package org.kuali.core.web.struts.pojo;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * begin Kuali Foundation modification
 * This interface defines methods that Pojo Forms must provide.
 * end Kuali Foundation modification
 */
// Kuali Foundation modification: original name: SLForm
public interface PojoForm {
    public void populate(HttpServletRequest request);

    // begin Kuali Foundation modification
    // cachedActionErrors() method removed
    public void postprocessRequestParameters(Map requestParameters);
    // end Kuali Foundation modification

    public Map getUnconvertedValues();

    public Object formatValue(Object value, String keypath, Class type);

    // begin Kuali Foundation modification
    public void processValidationFail();
    // end Kuali Foundation modification
}
