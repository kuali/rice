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
package org.kuali.rice.kim.api.identity.personal;

import org.kuali.rice.core.api.mo.common.GloballyUnique;
import org.kuali.rice.core.api.mo.common.Identifiable;
import org.kuali.rice.core.api.mo.common.Versioned;
import org.kuali.rice.core.api.util.type.KualiPercent;
import org.kuali.rice.kim.api.identity.CodedAttributeContract;

/**
 * ethnicity information for a KIM identity
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public interface EntityEthnicityContract extends Versioned, GloballyUnique, Identifiable {
	
	/**
     * Gets this {@link EntityEthnicityContract}'s identity id.
     * @return the identity id for this {@link EntityEthnicityContract}, or null if none has been assigned.
     */
	String getEntityId();
	
	/**
     * @deprecated Deprecated in Rice 2.3.0.  Use getRaceEthnicityCodes()
     * Gets this {@link EntityEthnicityContract}'s ethnicity code.
     * @return the ethnicity code for this {@link EntityEthnicityContract}, or null if none has been assigned.
     */
    @Deprecated
	String getEthnicityCode();
	
	/**
     * @deprecated Deprecated in Rice 2.3.0.  Use getRaceEthnicityCodesUnmasked()
     * Gets this {@link EntityEthnicityContract}'s unmasked ethnicity code.
     * @return the unmasked ethnicity code for this {@link EntityEthnicityContract}, or null if none has been assigned.
     */
    @Deprecated
	String getEthnicityCodeUnmasked();
	
	/**
     * @deprecated Deprecated in Rice 2.3.0.  Use getLocalRaceEthnicityCodes()
     * Gets this {@link EntityEthnicityContract}'s sub-ethnicity code.
     * @return the sub-ethnicity code for this {@link EntityEthnicityContract}, or null if none has been assigned.
     */
    @Deprecated
	String getSubEthnicityCode();
	
	/**
     * @deprecated Deprecated in Rice 2.3.0.  Use getLocalRaceEthnicityCodesUnmasked()
     * Gets this {@link EntityEthnicityContract}'s unmasked sub-ethnicity code.
     * @return the unmasked sub-ethnicity code for this {@link EntityEthnicityContract}, or null if none has been assigned.
     */
    @Deprecated
    String getSubEthnicityCodeUnmasked();

    /**
     * Gets this {@link EntityEthnicityContract}'s is hispanic or latino.
     * @return whether the {@link EntityEthnicityContract}, is hispanic or latino.
     */
    boolean isHispanicOrLatino();

    /**
     * Gets a {@link EntityEthnicityContract}'s ethnicity code.
     * @return the ethnicity codes for this {@link EntityEthnicityContract}, or null if none has been assigned.
     */
    CodedAttributeContract getRaceEthnicityCode();

    /**
     * Gets a {@link EntityEthnicityContract}'s ethnicity code's percentage.
     * @return the percent for this {@link EntityEthnicityContract} record.
     */
    Double getPercentage();

    /**
     * Gets a {@link EntityEthnicityContract}'s ethnicity code's unmasked percentage.
     * @return the percent for this {@link EntityEthnicityContract} record.
     */
    Double getPercentageUnmasked();

    /**
     * Gets a {@link EntityEthnicityContract}'s ethnicity code unmasked.
     * @return the unmasked ethnicity codes for this {@link EntityEthnicityContract}, or null if none has been assigned.
     */
    CodedAttributeContract getRaceEthnicityCodeUnmasked();

    /**
     * Categories used in local exchanges (such as within states or provinces) to describe groups to which individuals
     * belong or identify with by race or ethnicity. This element may be used to add more information or granularity
     * to nationally defined codes. This element may also be used in place of a national code set where required by
     * local practice.
     *
     * @return the local ethnicity code used in local exchanges for this {@link EntityEthnicityContract}, or null if none has been assigned
     */
    String getLocalRaceEthnicityCode();

    /**
     * Unmasked categories used in local exchanges (such as within states or provinces) to describe groups to which individuals
     * belong or identify with by race or ethnicity. This element may be used to add more information or granularity
     * to nationally defined codes. This element may also be used in place of a national code set where required by
     * local practice.
     *
     * @return the local ethnicity code used in local exchanges for this {@link EntityEthnicityContract}, or null if none has been assigned
     */
    String getLocalRaceEthnicityCodeUnmasked();

    /**
     * Returns a boolean value that determines if personal fields should be suppressed.
     * @return boolean value that determines if personal fields should be suppressed.
     */
	boolean isSuppressPersonal();
}
