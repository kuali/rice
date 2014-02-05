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
package org.kuali.rice.krad.labs;

import org.kuali.rice.kim.api.identity.principal.PrincipalContract;
import org.kuali.rice.krad.bo.DataObjectBase;
import org.kuali.rice.krad.data.jpa.PortableSequenceGenerator;
import org.kuali.rice.krad.data.jpa.converters.BooleanYNConverter;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
@Entity
@Table(name = "KRIM_PRNCPL_T")

public class LabsUserControl extends DataObjectBase implements PrincipalContract {

    @Transient
    private String myPrincipalName;
    @Transient
    private String myPersonName;

    private static final long serialVersionUID = 1L;

    @PortableSequenceGenerator(name = "KRIM_PRNCPL_ID_S")
    @GeneratedValue(generator = "KRIM_PRNCPL_ID_S")
    @Id
    @Column(name = "PRNCPL_ID")
    private String principalId;

    @Column(name = "PRNCPL_NM")
    private String principalName;

    @Column(name = "ENTITY_ID")
    private String entityId;

    @Column(name = "PRNCPL_PSWD")
    private String password;

    @Column(name = "ACTV_IND")
    @Convert(converter = BooleanYNConverter.class)
    private boolean active;

    @Override
    public String getPrincipalId() {
        return principalId;
    }

    public void setPrincipalId(String principalId) {
        this.principalId = principalId;
    }

    @Override
    public String getPrincipalName() {
        return principalName;
    }

    public void setPrincipalName(String principalName) {
        this.principalName = principalName;
    }

    @Override
    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean getActive() {
        return active;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getMyPrincipalName() {
        return myPrincipalName;
    }

    public void setMyPrincipalName(String myPrincipalName) {
        this.myPrincipalName = myPrincipalName;
    }

    public String getMyPersonName() {
        return myPersonName;
    }

    public void setMyPersonName(String myPersonName) {
        this.myPersonName = myPersonName;
    }
}
