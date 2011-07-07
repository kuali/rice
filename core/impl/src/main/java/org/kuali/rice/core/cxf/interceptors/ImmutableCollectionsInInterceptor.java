package org.kuali.rice.core.cxf.interceptors;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A CXF Interceptor that binds itself to the USER_LOGICAL phase to be used on inbound
 * messages.  This interceptor is invoked in the interceptor chain after unmarshalling
 * from XML to Java has occurred.  The role of this interceptor is to ensure that any
 * Collection (and specifically List, Set, or Map) used in a @WebMethod is ultimately of the
 * expected immutable type returned from the local service.
 */
@SuppressWarnings("unused")
public class ImmutableCollectionsInInterceptor extends AbstractPhaseInterceptor<Message> {

    /**
     * Instantiates an ImmutableCollectionsInInterceptor and adds it to the USER_LOGICAL phase.
     */
    public ImmutableCollectionsInInterceptor() {
        super(Phase.USER_LOGICAL);
    }

    @Override
    public void handleMessage(final Message message) throws Fault {
        try {
            List contents = message.getContent(List.class);
            for (Object o : contents) {
                makeCollectionFieldsImmutable(o);
            }
        } catch (Exception e) {
            throw new Fault(e);
        }
    }

    /**
     * Accepts an object whose fields of type List, Set, or Collection need to be made immutable irrespective of field
     * visibility.  The passed in object is mutated as a result.
     *
     * @param o - The object whose List, Set, or Collection fields will be made immutable
     * @throws IllegalAccessException
     */
    @SuppressWarnings("unchecked")
    void makeCollectionFieldsImmutable(Object o) throws IllegalAccessException {
        Class<?> targetClass = o.getClass();
        for (Field f : targetClass.getDeclaredFields()) {

            f.setAccessible(true);

            if (f.getType().isAssignableFrom(List.class)) {
                List original = (List) f.get(o);
                if (original == null) {
                    original = Collections.emptyList();
                }
                List immutable = Collections.unmodifiableList(original);
                f.set(o, immutable);

            } else if (f.getType().isAssignableFrom(Set.class)) {
                Set original = (Set) f.get(o);
                if (original == null) {
                    original = Collections.emptySet();
                }
                Set immutable = Collections.unmodifiableSet(original);
                f.set(o, immutable);
            } else if (f.getType().isAssignableFrom(Collection.class)) {
                Collection original = (Collection) f.get(o);
                if (original == null) {
                    original = Collections.emptyList();
                }
                Collection immutable = Collections.unmodifiableCollection(original);
                f.set(o, immutable);
            } else if (f.getType().isAssignableFrom(Map.class)) {
                Map original = (Map) f.get(o);
                if (original == null) {
                    original = Collections.emptyMap();
                }
                Map immutable = Collections.unmodifiableMap(original);
                f.set(o, immutable);
            }

            f.setAccessible(false);
        }
    }
}
