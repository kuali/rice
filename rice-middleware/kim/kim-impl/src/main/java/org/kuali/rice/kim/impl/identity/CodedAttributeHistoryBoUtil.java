/*
 * Copyright 2006-2013 The Kuali Foundation
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

package org.kuali.rice.kim.impl.identity;

import org.kuali.rice.kim.api.identity.CodedAttribute;
import org.kuali.rice.kim.api.identity.CodedAttributeHistory;
import org.kuali.rice.kim.api.identity.CodedAttributeHistoryContract;

import javax.persistence.MappedSuperclass;
import java.sql.Timestamp;

@MappedSuperclass
public abstract class CodedAttributeHistoryBoUtil {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CodedAttributeHistoryBoUtil.class);

    /**
     * Converts a mutable CodedAttributeHistoryBo to an immutable CodedAttributeHistory representation.
     *
     * @param bo
     * @return an immutable CodedAttributeHistory
     */
    public static <T extends CodedAttributeHistoryContract> CodedAttributeHistory to(T bo) {
        if (bo == null) {
            return null;
        }

        return CodedAttributeHistory.Builder.create(bo).build();
    }

    /**
     * Creates a CodedAttributeHistoryBo business object from an immutable representation of a
     * CodedAttributeHistory.
     *
     * @param immutable an immutable CodedAttribute
     * @return an object extending from CodedAttributeBo
     */
    public static <T extends CodedAttributeHistoryBoContract> T from(Class<T> type, CodedAttributeHistory immutable) {
        if (immutable == null) {
            return null;
        }
        T bo = null;
        try {
            bo = type.newInstance();

            bo.setCode(immutable.getCode());
            bo.setName(immutable.getName());
            bo.setSortCode(immutable.getSortCode());
            bo.setActive(immutable.isActive());
            bo.setVersionNumber(immutable.getVersionNumber());
            bo.setObjectId(immutable.getObjectId());
            bo.setHistoryId(immutable.getHistoryId());
            bo.setActiveFromDateValue(immutable.getActiveFromDate() == null? null : new Timestamp(
                    immutable.getActiveFromDate().getMillis()));
            bo.setActiveToDateValue(immutable.getActiveToDate() == null ? null : new Timestamp(
                    immutable.getActiveToDate().getMillis()));
        } catch (InstantiationException e) {
            LOG.error(e.getMessage(), e);
        } catch (IllegalAccessException e) {
            LOG.error(e.getMessage(), e);
        }
        return bo;
    }

    /**
     * Creates a CodedAttributeHistoryBo business object from an immutable representation of a CodedAttribute.
     *
     * @param type class of object extending from codedAttributeHistoryBo
     * @param immutable an immutable CodedAttribute
     * @param fromDate starting date of historical record
     * @param fromDate ending date of historical record
     * @return an object extending CodedAttributeHistoryBo
     */
    public static <T extends CodedAttributeHistoryBoContract> T from(
            Class<T> type,
            CodedAttribute immutable,
            Timestamp fromDate,
            Timestamp toDate) {
        if (immutable == null) {
            return null;
        }

        T bo = null;
        try {
            bo = type.newInstance();

            bo.setCode(immutable.getCode());
            bo.setName(immutable.getName());
            bo.setSortCode(immutable.getSortCode());
            bo.setActive(immutable.isActive());
            bo.setVersionNumber(immutable.getVersionNumber());
            bo.setObjectId(immutable.getObjectId());
            bo.setActiveFromDateValue(fromDate == null ? null : new Timestamp(fromDate.getTime()));
            bo.setActiveToDateValue(toDate == null ? null : new Timestamp(toDate.getTime()));
        } catch (InstantiationException e) {
            LOG.error(e.getMessage(), e);
        } catch (IllegalAccessException e) {
            LOG.error(e.getMessage(), e);
        }


        return bo;
    }



}
