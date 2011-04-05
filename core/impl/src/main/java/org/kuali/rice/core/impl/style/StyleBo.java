/*
 * Copyright 2005-2008 The Kuali Foundation
 *
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
package org.kuali.rice.core.impl.style;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.kuali.rice.core.api.style.Style;
import org.kuali.rice.core.api.style.StyleContract;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;

/**
 * A BusinessObject implementation of the StyleContract which is mapped to the
 * database for persistence.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Entity
@Table(name="KREW_STYLE_T")
//@Sequence(name="KREW_EDL_S", property="edocLiteStyleId")
public class StyleBo extends PersistableBusinessObjectBase implements StyleContract {

	private static final long serialVersionUID = 2020611019976731725L;
    
	@Id
    @GeneratedValue(generator="KREW_EDL_S")
	@GenericGenerator(name="KREW_EDL_S",strategy="org.hibernate.id.enhanced.SequenceStyleGenerator",parameters={
			@Parameter(name="sequence_name",value="KREW_EDL_S"),
			@Parameter(name="value_column",value="id")
	})
	@Column(name="STYLE_ID")
	private Long styleId;

	@Column(name="NM")
	private String name;
    
	@Lob
	@Basic(fetch=FetchType.LAZY)
	@Column(name="XML")
	private String xmlContent;
    
	@Column(name="ACTV_IND")
	private boolean active;

	public StyleBo() {
		// default active to true
		this.active = true;
	}
	
	@Override
    public Long getStyleId() {
        return styleId;
    }
    
    public void setStyleId(Long styleId) {
        this.styleId = styleId;
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
    
    public static Style to(StyleBo styleBo) {
    	if (styleBo == null) {
    		return null;
    	}
    	return Style.Builder.create(styleBo).build();
    }
    
    public static StyleBo from(Style style) {
    	if (style == null) {
    		return null;
    	}
    	StyleBo styleBo = new StyleBo();
    	styleBo.setStyleId(style.getStyleId());
    	styleBo.setName(style.getName());
    	styleBo.setXmlContent(style.getXmlContent());
    	styleBo.setActive(style.isActive());
    	styleBo.setVersionNumber(style.getVersionNumber());
    	styleBo.setObjectId(style.getObjectId());
    	return styleBo;
    }

}
