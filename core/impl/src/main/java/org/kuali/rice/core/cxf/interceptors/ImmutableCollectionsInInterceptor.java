package org.kuali.rice.core.cxf.interceptors;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;

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
 * type returned from
 */
@SuppressWarnings("unused")
public class ImmutableCollectionsInInterceptor extends AbstractPhaseInterceptor<Message> {

    /**
     * Instantiates an ImmutableCollectionsInInterceptor
     */
    public ImmutableCollectionsInInterceptor() {
        super(Phase.USER_LOGICAL);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void handleMessage(final Message message) throws Fault {
        List contents = message.getContent(List.class);
        for (int i = 0; i < contents.size(); i++) {
            Object o = contents.get(i);
            if (o instanceof List) {
                List unmodifiable = Collections.unmodifiableList((List) o);
                contents.set(i, unmodifiable);
            }
            else if (o instanceof Set) {
                Set unmodifiable = Collections.unmodifiableSet((Set) o);
                contents.set(i, unmodifiable);
            }
            else if (o instanceof Collection) {
                Collection unmodifiable = Collections.unmodifiableCollection((Collection) o);
                contents.set(i,unmodifiable);
            } else if (o instanceof Map) {
                Map unmodifiable = Collections.unmodifiableMap((Map) o);
                contents.set(i, unmodifiable);
            }
        }
    }
}
