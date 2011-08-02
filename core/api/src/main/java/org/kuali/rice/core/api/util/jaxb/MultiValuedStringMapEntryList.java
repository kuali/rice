package org.kuali.rice.core.api.util.jaxb;

import org.kuali.rice.core.api.mo.AbstractDataTransferObject;
import org.w3c.dom.Element;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
* Created by IntelliJ IDEA.
* User: ewestfal
* Date: 7/29/11
* Time: 2:49 PM
* To change this template use File | Settings | File Templates.
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MultiValuedStringMapEntryListType")
public class MultiValuedStringMapEntryList extends AbstractDataTransferObject {

    private static final long serialVersionUID = 1L;

    @XmlElement(name = "entry")
    private final List<MultiValuedStringMapEntry> entries;

    @SuppressWarnings("unused") @XmlAnyElement
    private final Collection<Element> _futureElements = null;

    @SuppressWarnings("unused")
    MultiValuedStringMapEntryList() {
        this.entries = null;
    }

    public MultiValuedStringMapEntryList(List<MultiValuedStringMapEntry> entries) {
        this.entries = new ArrayList<MultiValuedStringMapEntry>(entries);
    }

    /**
     * @return the attribute
     */
    public List<MultiValuedStringMapEntry> getEntries() {
        if (this.entries == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(entries);
    }
}
