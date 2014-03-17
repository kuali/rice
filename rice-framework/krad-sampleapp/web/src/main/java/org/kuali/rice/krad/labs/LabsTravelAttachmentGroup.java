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


import org.kuali.rice.core.api.util.type.KualiPercent;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.krad.bo.DataObjectBase;
import org.kuali.rice.krad.bo.PersistableAttachmentList;
import org.kuali.rice.krad.data.provider.annotation.Description;
import org.kuali.rice.krad.data.provider.annotation.ForceUppercase;
import org.kuali.rice.krad.data.provider.annotation.InheritProperties;
import org.kuali.rice.krad.data.provider.annotation.InheritProperty;
import org.kuali.rice.krad.data.provider.annotation.KeyValuesFinderClass;
import org.kuali.rice.krad.data.provider.annotation.Label;
import org.kuali.rice.krad.data.provider.annotation.Relationship;
import org.kuali.rice.krad.data.provider.annotation.UifAutoCreateViewType;
import org.kuali.rice.krad.data.provider.annotation.UifAutoCreateViews;
import org.kuali.rice.krad.data.provider.annotation.UifDisplayHint;
import org.kuali.rice.krad.data.provider.annotation.UifDisplayHintType;
import org.kuali.rice.krad.data.provider.annotation.UifDisplayHints;
import org.kuali.rice.krad.data.provider.annotation.UifValidCharactersConstraintBeanName;
import org.kuali.rice.krad.demo.travel.dataobject.TravelAccountType;
import org.kuali.rice.krad.demo.travel.dataobject.TravelSubAccount;
import org.kuali.rice.krad.demo.travel.options.AccountTypeKeyValues;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name="TRV_ATT_GRP_SAMPLE")
@UifAutoCreateViews({UifAutoCreateViewType.INQUIRY,UifAutoCreateViewType.LOOKUP})
public class LabsTravelAttachmentGroup extends DataObjectBase implements PersistableAttachmentList<LabsTravelAttachment>
        ,Serializable {
    private static final long serialVersionUID = -7739303391609395867L;

    @Id
    @Column(name="ATT_GRP_NUM",length=10)
    @Label("Attachment Group Number")
    @Description("Unique identifier for account")
    @UifValidCharactersConstraintBeanName("AlphaNumericPatternConstraint")
    private String number;

    @Column(name="ATT_GRP_NAME",length=40)
    @Label("Attachment Group Name")
    private String name;

    @OneToMany(fetch=FetchType.EAGER, orphanRemoval=true, cascade= {CascadeType.ALL}, mappedBy = "labsTravelAttachmentGroup")
    protected List<LabsTravelAttachment> attachments;

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

    public List<LabsTravelAttachment> getAttachments() {
        if(attachments == null) {
            attachments = new ArrayList<LabsTravelAttachment>();
        }
        return attachments;
    }

    public void setAttachments(List<LabsTravelAttachment> attachments) {
        this.attachments = attachments;
    }

}
