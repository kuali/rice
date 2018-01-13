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
package org.kuali.rice.krad.uif.field;

import org.kuali.rice.krad.datadictionary.parse.BeanTag;
import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute;
import org.kuali.rice.krad.uif.component.ComponentSecurity;

/**
 * Field security adds the edit in line and view in line flags to the standard component security
 *
 * <p>
 * These flags are only applicable when the field is part of a collection group. They indicate there is security
 * on the field within the collection line
 * </p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BeanTag(name = "fieldSecurity")
public class FieldSecurity extends ComponentSecurity {

    private Boolean editInLineAuthz;
    private Boolean viewInLineAuthz;

    public FieldSecurity() {
        super();
    }

    /**
     * Indicates whether the field has edit in line authorization and KIM should be consulted
     *
     * @return true if the field has edit in line authorization, false if not
     */
    @BeanTagAttribute
    public Boolean isEditInLineAuthz() {
        return editInLineAuthz;
    }

    /**
     * Setter for the edit in line authorization flag
     *
     * @param editInLineAuthz
     */
    public void setEditInLineAuthz(Boolean editInLineAuthz) {
        this.editInLineAuthz = editInLineAuthz;
    }

    /**
     * Indicates whether the field has view in line unmask authorization and KIM should be consulted
     *
     * @return true if the field has view in line unmask authorization, false if not
     */
    @BeanTagAttribute
    public Boolean isViewInLineAuthz() {
        return viewInLineAuthz;
    }

    /**
     * Setter for the view in line authorization flag
     *
     * @param viewInLineAuthz
     */
    public void setViewInLineAuthz(Boolean viewInLineAuthz) {
        this.viewInLineAuthz = viewInLineAuthz;
    }

}
