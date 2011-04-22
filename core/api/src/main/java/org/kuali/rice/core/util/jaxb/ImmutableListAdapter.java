package org.kuali.rice.core.util.jaxb;


import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Handles JAXB mapping that ensures that any List created from unmarshalled XML will be immutable.
 */
public class ImmutableListAdapter extends XmlAdapter<Object[], List<?>> {

    /**
     * Returns an immutable List when a List is meant to be unmarshalled from XML.  This is done to ensure service
     * contracts, which are to return immutable objects, also return immutable Lists when an XML sequence is returned
     * via a remote SOAP call.
     *
     * @param objects an array of Objects collected from XML to be transformed into an immutable List
     * @return An immutable List
     * @throws Exception
     */
    @Override
    public List<?> unmarshal(Object[] objects) throws Exception {
        return Collections.unmodifiableList(Arrays.asList(objects));
    }

    /**
     * Creates an array of Object[] from a passed in List for JAXB marshalling. There is no requirement of what kind of
     * List is used when marshalling to XML.
     *
     * @param objects
     * @return The same List object that was passed in
     * @throws Exception
     */
    @Override
    public Object[] marshal(List<?> objects) throws Exception {
        return objects.toArray();
    }
}
