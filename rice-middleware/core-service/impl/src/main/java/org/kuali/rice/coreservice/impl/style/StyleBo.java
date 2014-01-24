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
package org.kuali.rice.coreservice.impl.style;

import org.kuali.rice.coreservice.api.style.Style;
import org.kuali.rice.coreservice.api.style.StyleContract;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.data.jpa.converters.Boolean01BigDecimalConverter;
import org.kuali.rice.krad.data.jpa.PortableSequenceGenerator;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * A BusinessObject implementation of the StyleContract which is mapped to the
 * database for persistence.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Entity
@Table(name="KRCR_STYLE_T")
@NamedQuery(name="StyleBo.findAllStyleNames",
        query="SELECT sb.name FROM StyleBo sb where sb.active=true"
)
public class StyleBo extends PersistableBusinessObjectBase implements StyleContract {

    private static final long serialVersionUID = 2020611019976731725L;

    @Id
    @Column(name="STYLE_ID")
    @GeneratedValue(generator = "KREW_EDL_S")
    @PortableSequenceGenerator(name = "KREW_EDL_S")
    private String id;

    @Column(name="NM")
    private String name;

    @Column(name="XML")
    private String xmlContent;

    @Column(name="ACTV_IND")
    @Convert(converter=Boolean01BigDecimalConverter.class)
    boolean active = true;

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getXmlContent() {
        return xmlContent;
    }

    public void setXmlContent(String xmlContent) {
        this.xmlContent = xmlContent;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Converts the given StyleBo to a Style object.
     *
     * @param styleBo the StyleBo to convert
     * @return the resulting Style object, or null if the given styleBo was null
     */
    public static Style to(StyleBo styleBo) {
        if (styleBo == null) {
            return null;
        }
        return Style.Builder.create(styleBo).build();
    }

    /**
     * Constructs a StyleBo from the given Style.
     *
     * @param style the Style to convert
     * @return the resulting StyleBo object, or null if the given style was null
     */
    public static StyleBo from(Style style) {
        if (style == null) {
            return null;
        }
        StyleBo styleBo = new StyleBo();
        styleBo.setId(style.getId());
        styleBo.setName(style.getName());
        styleBo.setXmlContent(style.getXmlContent());
        styleBo.setActive(style.isActive());
        styleBo.setVersionNumber(style.getVersionNumber());
        styleBo.setObjectId(style.getObjectId());
        return styleBo;
    }

}

