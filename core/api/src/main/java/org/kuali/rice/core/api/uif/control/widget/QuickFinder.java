package org.kuali.rice.core.api.uif.control.widget;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.Map;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = QuickFinder.Constants.TYPE_NAME)
public class QuickFinder extends AbstractWidget {

    @XmlElement(name = Elements.BASE_LOOKUP_URL, required = true)
    private String baseLookupUrl;

    @XmlElement(name = Elements.DATA_OBJECT_CLASS, required = true)
    private String dataObjectClass;

    @XmlElement(name = Elements.LOOKUP_PARAMETERS, required = false)
    private Map<String, String> lookupParameters;

    @XmlElement(name = Elements.FIELD_CONVERSIONS, required = false)
    private Map<String, String> fieldConversions;

    /**
     * Defines some internal constants used on this class.
     */
    static final class Constants {
        static final String TYPE_NAME = "QuickFinderType";
    }

    static final class Elements {
        static final String BASE_LOOKUP_URL = "baseLookupUrl";
        static final String DATA_OBJECT_CLASS = "dataObjectClass";
        static final String LOOKUP_PARAMETERS = "lookupParameters";
        static final String FIELD_CONVERSIONS = "fieldConversions";
    }
}
