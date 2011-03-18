/*
 * Copyright 2006-2007 The Kuali Foundation
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

package edu.sampleu.travel.bo;

import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;

import javax.persistence.*;

@Entity
@Table(name="TRV_ACCT")
public class TravelAccount extends PersistableBusinessObjectBase {
    
	private static final long serialVersionUID = -7739303391609093875L;
	
	@Id
	@Column(name="acct_num")
	private String number;
    
	@Column(name="acct_name")
	private String name;
    
	@Column(name="acct_fo_id")
	private Long foId;
    
    @ManyToOne(fetch=FetchType.EAGER, cascade={CascadeType.PERSIST, CascadeType.MERGE})
	@JoinColumn(name="acct_fo_id", insertable=false, updatable=false)
	private FiscalOfficer fiscalOfficer;  
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public FiscalOfficer getFiscalOfficer() {
        return fiscalOfficer;
    }

    public void setFiscalOfficer(FiscalOfficer fiscalOfficer) {
        this.fiscalOfficer = fiscalOfficer;
    }

    public Long getFoId() {
        return foId;
    }

    public void setFoId(Long foId) {
        this.foId = foId;
    }
    
}
