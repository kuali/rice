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
package org.kuali.rice.krms.impl.repository;

import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.krad.data.platform.MaxValueIncrementerFactory;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;

import javax.sql.DataSource;

/**
 * Class used to help migrate KRMS BOs to use DataFieldMaxValueIncrementer
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class RepositoryBoIncrementer {

    private final String seqName;
    private DataFieldMaxValueIncrementer boIncrementer;

    public RepositoryBoIncrementer(String seqName) {
        this.seqName = seqName;
    }

    /**
     * Set the incrementer, useful for testing.
     *
     * @param boIncrementer DataFieldMaxValueIncrementer to use for getNewId()
     */
    public void setDataFieldMaxValueIncrementer(DataFieldMaxValueIncrementer boIncrementer) {
        this.boIncrementer = boIncrementer;
    }

    /**
     * Returns the next available id value.
     *
     * @return String the next available id
     */
    public String getNewId() {
        if (boIncrementer == null) {
            // we don't assign to boIncrementer to preserve existing behavior
            return MaxValueIncrementerFactory.getIncrementer((DataSource) GlobalResourceLoader.getService(
                    "krmsDataSource"), seqName).nextStringValue();
        }

        return boIncrementer.nextStringValue();
    }
}
