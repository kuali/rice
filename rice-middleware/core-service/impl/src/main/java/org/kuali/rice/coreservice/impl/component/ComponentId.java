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
package org.kuali.rice.coreservice.impl.component;

import org.kuali.rice.krad.data.jpa.IdClassBase;

public class ComponentId extends IdClassBase {

    private static final long serialVersionUID = -5335910085543358227L;

    private String namespaceCode;
    private String code;

    public ComponentId() {}

    public ComponentId(String namespaceCode, String code) {
        this.namespaceCode = namespaceCode;
        this.code = code;
    }

    public String getNamespaceCode() {
        return namespaceCode;
    }

    public String getCode() {
        return code;
    }

}