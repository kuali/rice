/**
 * Copyright 2005-2017 The Kuali Foundation
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
package org.kuali.rice.krad.bo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.eclipse.persistence.annotations.Index;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Entity
@Table(name="KRNS_MAINT_DOC_ATT_LST_T",uniqueConstraints= {
        @UniqueConstraint(name="KRNS_MAINT_DOC_ATT_LST_TC0",columnNames="OBJ_ID")
})
public class MultiDocumentAttachment extends PersistableAttachmentBase {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name="ATT_ID",length=40)
    private String id;

    @Column(name="DOC_HDR_ID",length=14)
    @Index(name="KRNS_MAINT_DOC_ATT_LST_TI1")
    private String documentNumber;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDocumentNumber() {
        return this.documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

}
