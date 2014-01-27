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
package org.kuali.rice.coreservice.impl.parameter;

import org.kuali.rice.krad.data.jpa.IdClassBase;

public class ParameterId extends IdClassBase {

    private static final long serialVersionUID = -4654502682966630371L;

    private String namespaceCode;
    private String componentCode;
    private String name;
    private String applicationId;

    public ParameterId() {}

    public ParameterId(String namespaceCode, String componentCode, String name, String applicationId) {
        this.namespaceCode = namespaceCode;
        this.componentCode = componentCode;
        this.name = name;
        this.applicationId = applicationId;
    }

    public String getNamespaceCode() {
        return namespaceCode;
    }

    public String getComponentCode() {
        return componentCode;
    }

    public String getName() {
        return name;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public String getCacheKey() {
        return this.applicationId + this.componentCode + this.namespaceCode + this.name;
    }
}

