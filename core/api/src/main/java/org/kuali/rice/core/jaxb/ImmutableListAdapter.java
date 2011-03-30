package org.kuali.rice.core.jaxb;


import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Handles JAXB mapping that ensures that any List created from unmarshalled XML will be immutable.
 */
public class ImmutableListAdapter extends XmlAdapter<ArrayList<?>,List<?>> {

    /**
     * Returns an immutable List when a List is unmarshalled from XML.  This is done to ensure service contracts,
     * which are to return immutable objects, also return immutable Lists when an XML sequence is returned via a
     * remote method invocation.
     * @param objects
     * @return An immutable List
     * @throws Exception
     */
    @Override
    public List<?> unmarshal(ArrayList<?> objects) throws Exception {
        return Collections.unmodifiableList(objects);
    }

    /**
     * Returns the same List that was passed in as an ArrayList.  There is no requirement of what kind of List is ued when
     * marshalling to XML.
     * @param objects
     * @return The same List object that was passed in
     * @throws Exception
     */
    @Override
    public ArrayList<?> marshal(List<?> objects) throws Exception {
        return new ArrayList(objects);
    }
}
