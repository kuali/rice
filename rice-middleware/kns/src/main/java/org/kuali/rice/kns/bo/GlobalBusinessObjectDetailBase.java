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
package org.kuali.rice.kns.bo;

import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.MappedSuperclass;

/**
 * @deprecated use BulkUpdate instead
 */
@Deprecated
@MappedSuperclass
public abstract class GlobalBusinessObjectDetailBase extends PersistableBusinessObjectBase implements GlobalBusinessObjectDetail {

    /**
     * EclipseLink static weaving does not weave MappedSuperclass unless an Entity or Embedded is
     * weaved which uses it, hence this class.
     */
    @Embeddable
    private static final class WeaveMe extends GlobalBusinessObjectDetailBase {}

    @Column(name = "DOC_HDR_ID", length = 14)
    private String documentNumber;

    @Override
    public String getDocumentNumber() {
        return documentNumber;
    }

    @Override
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }
}
