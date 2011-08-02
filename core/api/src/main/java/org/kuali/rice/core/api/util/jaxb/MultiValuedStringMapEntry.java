package org.kuali.rice.core.api.util.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
* Created by IntelliJ IDEA.
* User: ewestfal
* Date: 7/29/11
* Time: 2:49 PM
* To change this template use File | Settings | File Templates.
*/
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "MultiValuedStringMapEntryType")
public final class MultiValuedStringMapEntry implements Serializable {

    private static final long serialVersionUID = -9609663434312103L;

    @XmlAttribute(name = "key")
    private final String key;

    @XmlElementWrapper(name = "values")
    @XmlElement(name = "value")
    private final List<String> values;

    /**
     * Used only by JAXB.
     */
    @SuppressWarnings("unused")
    MultiValuedStringMapEntry() {
        this.key = null;
        this.values = null;
    }

    public MultiValuedStringMapEntry(String key, List<String> values) {
        this.key = key;
        this.values = values;
    }

    public MultiValuedStringMapEntry(Map.Entry<String, List<String>> entry) {
        this.key = entry.getKey();
        this.values = new ArrayList<String>(entry.getValue());
    }

    public String getKey() {
        return this.key;
    }

    public List<String> getValues() {
        return this.values;
    }

}
