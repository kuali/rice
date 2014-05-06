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
package org.kuali.rice.krad.bo;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.data.jpa.converters.BooleanYNConverter;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Embeddable;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class KualiCodeBase extends PersistableBusinessObjectBase implements KualiCode {

    private static final long serialVersionUID = 1194744068788100482L;

    /**
     * EclipseLink static weaving does not weave MappedSuperclass unless an Entity or Embedded is
     * weaved which uses it, hence this class.
     */
    @Embeddable
    private static final class WeaveMe extends KualiCodeBase {}

    // Code and Name will be overridden by Column annotations in their children classes
    @Id
    @Column(name = "CODE", length=10)
    protected String code;
    @Column(name = "NM", length=40)
    protected String name;

    @Column(name = "ACTV_IND")
    @Convert(converter = BooleanYNConverter.class)
    protected Boolean active;

    public KualiCodeBase() {
        this.active = true;
    }

    public KualiCodeBase(String code) {
        this();
        this.code = code;
    }

    /**
     * @return Getter for the Code.
     */
    @Override
    public String getCode() {
        return code;
    }

    /**
     * @param code - Setter for the Code.
     */
    @Override
    public void setCode(String code) {
        this.code = code;
    }


    /**
     * @return Getter for the Name.
     */
    @Override
    public String getName() {
        return name;
    }


    /**
     * @param name - Setter for the name.
     */
    @Override
    public void setName(String name) {
        this.name = name;
    }


    /**
     * @return Getter for the active field.
     */
    @Override
    public boolean isActive() {
        return active;
    }


    /**
     * @param name - Setter for the active field.
     */
    @Override
    public void setActive(boolean a) {
        this.active = a;
    }

    /**
     * @return Returns the code and description in format: xx - xxxxxxxxxxxxxxxx
     */
    public String getCodeAndDescription() {
    	return KualiCodeBase.getCodeAndDescription(getCode(), getName());
    }

    /**
     * Static helper method to allow other classes to provide consistent "code and description"
     * behavior, even if not extending from this class.
     */
	public static String getCodeAndDescription(String code, String desc) {
		if (code != null) {
			if (desc == null) {
				return code;
			} else {
				return code + " - " + desc;
			}
		}
		return "";
	}

    /**
     * Implements equals comparing code to code.
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof KualiCodeBase) {
            return StringUtils.equals(this.getCode(), ((KualiCodeBase) obj).getCode());
        }
        return false;
    }

    /**
     * Overriding equals requires writing a hashCode method.
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int hashCode = 0;

        if (getCode() != null) {
            hashCode = getCode().hashCode();
        }

        return hashCode;
    }
}
