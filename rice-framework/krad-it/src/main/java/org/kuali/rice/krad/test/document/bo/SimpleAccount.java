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

import org.kuali.rice.krad.bo.DataObjectBase;
import org.kuali.rice.krad.data.jpa.PortableSequenceGenerator;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * Duplicate of {@link Account} which overrides {@link #getExtension()} to avoid
 * automatic extension creation
 */
@Entity
@Table(name="TRV_ACCT")
public class SimpleAccount extends DataObjectBase {

    @Id
    @GeneratedValue(generator="TRVL_ID_SEQ")
    @PortableSequenceGenerator(name="TRVL_ID_SEQ")
    @Column(name="ACCT_NUM")
    private String number;

    @Column(name="ACCT_NAME")
    private String name;

    @Column(name="ACCT_FO_ID")
    private Long amId;

    @Transient
    private Object extension;

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
        System.err.println("Setting AmId from " + this.amId + " to " + id);
        this.amId = id;
    }

    public Object getExtension() {
        return extension;
    }

    public void setExtension(Object extension) {
        this.extension = extension;
    }
}
