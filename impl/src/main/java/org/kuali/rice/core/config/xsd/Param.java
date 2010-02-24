package org.kuali.rice.core.config.xsd;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Param", namespace = "http://rice.kuali.org/xsd/core/config",  propOrder = {
    "value"
})
public class Param {
    
    @XmlAttribute(required = true)
    protected String name;
    
    @XmlAttribute
    protected Boolean override;
    
    @XmlAttribute
    protected Boolean random;
    
    @XmlAttribute
    protected Boolean system;
    
    @XmlValue
    protected String value;
    

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean isOverride() {
        if (override == null) {
            return true;
        } else {
            return override;
        }
    }

    public void setOverride(Boolean override) {
        this.override = override;
    }
    
    public Boolean isRandom() {
        if (random == null) {
            return false;
        } else {
            return random;
        }
    }

    public void setRandom(Boolean random) {
        this.random = random;
    }
    
    public Boolean isSystem() {
        if (system == null) {
            return false;
        } else {
            return system;
        }
    }

    public void setSystem(Boolean system) {
        this.system = system;
    }
    
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
