package org.kuali.rice.core.config.xsd;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "config", namespace = "http://rice.kuali.org/xsd/core/config")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Config", namespace = "http://rice.kuali.org/xsd/core/config", propOrder = {
    "paramList"
})
public class Config {
    
    @XmlElement(name="param", namespace = "http://rice.kuali.org/xsd/core/config")
    private List<Param> paramList;

    public List<Param> getParamList() {
        if(paramList == null) {
            paramList = new ArrayList<Param>();
        }
        return paramList;
    }

}
