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

import org.kuali.rice.krad.data.jpa.PortableSequenceGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "KRMS_ACTN_ATTR_T")
public class ActionAttributeBo extends BaseAttributeBo implements Serializable {

    private static final long serialVersionUID = 1l;

    @PortableSequenceGenerator(name = "KRMS_ACTN_ATTR_S")
    @GeneratedValue(generator = "KRMS_ACTN_ATTR_S")
    @Id
    @Column(name = "ACTN_ATTR_DATA_ID")
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ACTN_ID")
    private ActionBo action;

    @ManyToOne()
    @JoinColumn(name = "ATTR_DEFN_ID", referencedColumnName = "ATTR_DEFN_ID")
    private KrmsAttributeDefinitionBo attributeDefinition;

    @Override
    public KrmsAttributeDefinitionBo getAttributeDefinition() {
        return attributeDefinition;
    }

    public void setAttributeDefinition(KrmsAttributeDefinitionBo attributeDefinition) {
        this.attributeDefinition = attributeDefinition;
    }

    public String getActionId() {
        if (action != null) {
            return action.getId();
        }

        return null;
    }

    public ActionBo getAction() {
        return action;
    }

    public void setAction(ActionBo action) {
        this.action = action;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
