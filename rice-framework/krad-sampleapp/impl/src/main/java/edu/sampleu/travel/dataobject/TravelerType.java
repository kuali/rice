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
package edu.sampleu.travel.dataobject;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.DataObjectBase;

@Entity
@Table(name="TRVL_TRAVELER_TYP_T")
public class TravelerType extends DataObjectBase implements MutableInactivatable {
	private static final long serialVersionUID = 1L;

	@Id
    @Column(name="code",length=3,nullable=false)
    private String code;

    @Column(name="src_code",length=10)
    private String sourceCode;

    @Column(name="nm",length=40,nullable=false)
    private String name;

    @Column(name="advances_ind",nullable=false,length=1)
    private Boolean advances = Boolean.FALSE;

    @Column(name="actv_ind",nullable=false,length=1)
    private Boolean active = Boolean.TRUE;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getSourceCode() {
        return sourceCode;
    }

    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getAdvances() {
        return advances;
    }

    public void setAdvances(Boolean advances) {
        this.advances = advances;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}

