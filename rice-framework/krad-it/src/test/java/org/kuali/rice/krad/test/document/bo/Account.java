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
package org.kuali.rice.krad.test.document.bo;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

import java.util.List;

@Entity
@Table(name="TRV_ACCT")
public class Account extends PersistableBusinessObjectBase {
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name="acct_num")
	private String number;
	@Column(name="acct_name")
    private String name;
	@Column(name="acct_fo_id")
    private Long amId;

    @OneToMany(fetch=FetchType.LAZY, cascade={CascadeType.PERSIST,CascadeType.MERGE,CascadeType.REMOVE})
    @JoinColumn(name="acct_num",referencedColumnName="acct_num",insertable=false,updatable=false)
    protected List<SubAccount> subAccounts;

    public Account() {}

    public Account(String number, String name) {
        super();
        this.number = number;
        this.name = name;
    }

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

    public Long getAmId() {
        return this.amId;
    }

    public void setAmId(Long id) {
        this.amId = id;
    }

    public List<SubAccount> getSubAccounts() {
        return subAccounts;
    }

    public void setSubAccounts(List<SubAccount> subAccounts) {
        this.subAccounts = subAccounts;
    }
}
