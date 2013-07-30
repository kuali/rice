/**
 * Copyright 2005-2013 The Kuali Foundation
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
package org.kuali.rice.kim.impl.group;

import org.joda.time.DateTime;
import org.kuali.rice.core.api.mo.common.active.InactivatableFromToUtils;
import org.kuali.rice.kim.api.group.Group;
import org.kuali.rice.kim.api.group.GroupHistory;
import org.kuali.rice.kim.api.group.GroupHistoryContract;
import org.kuali.rice.kim.impl.common.attribute.KimAttributeDataHistoryBo;
import org.kuali.rice.krad.data.jpa.eclipselink.PortableSequenceGenerator;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@Entity
@Table(name="KRIM_HIST_GRP_T")
public class GroupHistoryBo extends GroupBase implements GroupHistoryContract{
    private static final long serialVersionUID = 2322098027572496681L;

    @Id
    @GeneratedValue(generator = "KRIM_HIST_GRP_ID_S")
    @PortableSequenceGenerator(name = "KRIM_HIST_GRP_ID_S")
    @Column(name ="GRP_HIST_ID", nullable = false)
    private Long historyId;

    @Column(name="GRP_ID")
    private String id;

    @Column(name = "ACTV_FRM_DT")
    private Timestamp activeFromDateValue;

    @Column(name = "ACTV_TO_DT")
    private Timestamp activeToDateValue;

    @OneToMany(cascade = {CascadeType.ALL})
    @JoinColumn(name = "GRP_HIST_ID", nullable = false)
    private List<GroupAttributeHistoryBo> attributeHistoryDetails;

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public Long getHistoryId() {
        return historyId;
    }

    public void setHistoryId(Long historyId) {
        this.historyId = historyId;
    }

    public boolean isActive(Timestamp activeAsOfDate) {
        return InactivatableFromToUtils.isActive(getActiveFromDate(), getActiveToDate(), new DateTime(
                activeAsOfDate.getTime()));
    }

    @Override
    public boolean isActive(DateTime activeAsOfDate) {
        return this.isActive() && InactivatableFromToUtils.isActive(getActiveFromDate(), getActiveToDate(), activeAsOfDate);
    }

    @Override
    public boolean isActiveNow() {
        return this.isActive() && InactivatableFromToUtils.isActive(getActiveFromDate(), getActiveToDate(), null);
    }

    @Override
    public DateTime getActiveFromDate() {
        return this.activeFromDateValue == null ? null : new DateTime(this.activeFromDateValue.getTime());
    }

    @Override
    public DateTime getActiveToDate() {
        return this.activeToDateValue == null ? null : new DateTime(this.activeToDateValue.getTime());
    }

    public void setAttributeHistoryDetails(List<GroupAttributeHistoryBo> attributeHistoryDetails) {
        this.attributeHistoryDetails = attributeHistoryDetails;
    }

    public List<GroupAttributeHistoryBo> getAttributeHistoryDetails() {
        return this.attributeHistoryDetails;
    }

    @Override
    public Map<String,String> getAttributes() {
        return attributeHistoryDetails != null ? KimAttributeDataHistoryBo.toAttributes(attributeHistoryDetails) : attributes;
    }

    /*public List<GroupAttributeBo> getAttributeDetails() {
        List<GroupAttributeBo> attributes = new ArrayList<GroupAttributeBo>();
        for(GroupAttributeHistoryBo histAttr : getAttributeHistoryDetails()) {
            attributes.add(GroupAttributeBo.from(KimAttributeData.Builder.create(histAttr).build()));
        }
        return attributes;
    }*/

    public Timestamp getActiveFromDateValue() {
        return activeFromDateValue;
    }

    public void setActiveFromDateValue(Timestamp activeFromDateValue) {
        this.activeFromDateValue = activeFromDateValue;
    }

    public Timestamp getActiveToDateValue() {
        return activeToDateValue;
    }

    public void setActiveToDateValue(Timestamp activeToDateValue) {
        this.activeToDateValue = activeToDateValue;
    }

    /**
     * Converts a mutable bo to its immutable counterpart
     * @param bo the mutable business object
     * @return the immutable object
     */
    public static GroupHistory to(GroupHistoryBo bo) {
        if (bo == null) {
            return null;
        }

        return GroupHistory.Builder.create(bo).build();
    }
    /**
     * Converts a main object to its historical counterpart
     * @param group immutable object
     * @return the history bo
     */
    public static GroupHistoryBo from(Group group,
                                      DateTime fromDate,
                                      DateTime toDate) {
        if (group == null) {
            return null;
        }

        GroupHistory.Builder b = GroupHistory.Builder.create(group);
        b.setActiveFromDate(fromDate == null? null : fromDate);
        b.setActiveToDate(toDate == null? null : toDate);
        b.setVersionNumber(null);
        b.setObjectId(null);

        return GroupHistoryBo.from(b.build());
    }

    /**
     * Converts a main object to its historical counterpart
     * @param im immutable object
     * @return the history bo
     */
    public static GroupHistoryBo from(GroupHistory im) {
        if (im == null) {
            return null;
        }

        GroupHistoryBo bo = new GroupHistoryBo();
        bo.setHistoryId(im.getHistoryId());
        bo.setId(im.getId());
        bo.setNamespaceCode(im.getNamespaceCode());
        bo.setName(im.getName());
        bo.setDescription(im.getDescription());
        bo.setActive(im.isActive());
        bo.setKimTypeId(im.getKimTypeId());
        bo.setAttributes(im.getAttributes());
        bo.setVersionNumber(im.getVersionNumber());
        bo.setObjectId(im.getObjectId());
        bo.setActiveFromDateValue(im.getActiveFromDate() == null? null : new Timestamp(
                im.getActiveFromDate().getMillis()));
        bo.setActiveToDateValue(im.getActiveToDate() == null ? null : new Timestamp(
                im.getActiveToDate().getMillis()));


        return bo;
    }
}
