package org.kuali.rice.core.util.jaxb;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * Handles JAXB mapping that ensures that any Collection created from unmarshalled XML will be an immutable
 * non-null Collection reference..
 */
public class ImmutableCollectionAdapter extends XmlAdapter<Object[], Collection<?>> {

    /**
     * <p>Returns an immutable Collection when a Collection is meant to be unmarshalled from XML.  This is done to ensure service
     * contracts, which are to return immutable objects, also return immutable Collections when an XML sequence is returned
     * via a remote SOAP call.</p>
     *
     * <p>If the XML being unmarshalled, represented by the objects parameter, is empty or null then an empty List
     * is returned.</p>
     *
     * @param objects an array of Objects collected from XML to be transformed into an immutable Collection
     * @return An immutable Collection
     * @throws Exception
     */
    @Override
    public Collection<?> unmarshal(Object[] objects) throws Exception {
        return Collections.unmodifiableCollection(Arrays.asList(objects));
    }

    /**
     * Creates an array of Object[] from a passed in Collection for JAXB marshalling. There is no requirement of what kind of
     * Collection is used when marshalling to XML.
     *
     * @param objects
     * @return An Object[] containing the same contents as the passed in Collection.
     * @throws Exception
     */
    @Override
    public Object[] marshal(Collection<?> objects) throws Exception {
        return objects.toArray();
    }
}

