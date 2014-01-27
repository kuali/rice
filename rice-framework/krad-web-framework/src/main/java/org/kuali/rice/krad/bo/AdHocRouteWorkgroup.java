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
package org.kuali.rice.krad.bo;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;


/**
 * Ad Hoc Route Workgroup Business Object.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Entity
@Table(name="KRNS_ADHOC_RTE_ACTN_RECIP_T",uniqueConstraints= {
        @UniqueConstraint(name="KRNS_ADHOC_RTE_ACTN_RECIP_TC0",columnNames="OBJ_ID")
})
public class AdHocRouteWorkgroup extends AdHocRouteRecipient {
    private static final long serialVersionUID = 1L;

    @Transient
    private String recipientNamespaceCode;

    @Transient
    private String recipientName;

    /**
     * Sets type to {@link #WORKGROUP_TYPE}.
     */
    public AdHocRouteWorkgroup() {
        setType(WORKGROUP_TYPE);
    }

    @Override
    public void setType(Integer type) {
        if (!WORKGROUP_TYPE.equals(type)) {
            throw new IllegalArgumentException("cannot change type to " + type);
        }
        super.setType(type);
    }

    @Override
    public String getName() {
        return "";
    }

    /**
     * Gets recipient namespace code.
     * @return namespace code
     */
    public String getRecipientNamespaceCode() {
        return this.recipientNamespaceCode;
    }

    /**
     * Gets recipient name.
     *
     * @return recipient name
     */
    public String getRecipientName() {
        return this.recipientName;
    }

    /**
     * Setter for {@link #getRecipientNamespaceCode()}.
     *
     * @param recipientNamespaceCode recipient namespace code
     */
    public void setRecipientNamespaceCode(String recipientNamespaceCode) {
        this.recipientNamespaceCode = recipientNamespaceCode;
    }

    /**
     * Setter for {@link #recipientName}.
     *
     * @param recipientName recipient name
     */
    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }
}
