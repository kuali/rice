package org.kuali.rice.kew.framework.document.lookup;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "documentLookupCustomization")
@XmlType(name = "DocumentLookupCustomizationType")
@XmlEnum
public enum DocumentLookupCustomization {

    CRITERIA,
    CLEAR_CRITERIA,
    RESULTS,
    RESULT_SET_FIELDS;

}
