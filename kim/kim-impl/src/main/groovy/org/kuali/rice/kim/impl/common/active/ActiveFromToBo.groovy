/*
 * Copyright 2006-2011 The Kuali Foundation
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

package org.kuali.rice.kim.impl.common.active

import java.sql.Timestamp
import javax.persistence.Column
import org.kuali.rice.core.api.mo.common.active.InactivatableFromToUtils
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase

public abstract class ActiveFromToBo extends PersistableBusinessObjectBase {
    @Column(name = "ACTV_FRM_DT")
	Timestamp activeFromDate

	@Column(name = "ACTV_TO_DT")
	Timestamp activeToDate

    boolean isActive(Timestamp activeAsOfDate) {
        return InactivatableFromToUtils.isActive(activeFromDate, activeToDate, activeAsOfDate)
    }
}
