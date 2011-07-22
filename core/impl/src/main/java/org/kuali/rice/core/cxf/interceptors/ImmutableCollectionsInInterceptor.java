package org.kuali.rice.core.cxf.interceptors;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.kuali.rice.core.api.util.CollectionUtils;

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
                CollectionUtils.makeUnmodifiableAndNullSafe(o);
            }
        } catch (IllegalAccessException e) {
            throw new Fault(e);
        }
    }
}
