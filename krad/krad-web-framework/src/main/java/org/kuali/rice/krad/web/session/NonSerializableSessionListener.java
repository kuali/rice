package org.kuali.rice.krad.web.session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.rice.core.api.config.property.Config;
import org.kuali.rice.core.api.config.property.ConfigContext;

import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

//TODO: May want to have a way to turn this off that way we aren't incurring the overhead
//of testing every session object if it is serializable by actually serializing it!

/** A session listener that detects when a non-serializable attributes is added to session. **/
public class NonSerializableSessionListener implements HttpSessionAttributeListener {
    private static final Log LOG = LogFactory.getLog(NonSerializableSessionListener.class);

    @Override
    public void attributeAdded(HttpSessionBindingEvent se) {
        logSerializationViolations(se, "added");
    }

    @Override
    public void attributeRemoved(HttpSessionBindingEvent se) {
        //do nothing
    }

    @Override
    public void attributeReplaced(HttpSessionBindingEvent se) {
        logSerializationViolations(se, "replaced");
    }

    /**
     * Tests and logs serialization violations in non-production environments
     */
    private void logSerializationViolations(HttpSessionBindingEvent se, String action) {
        if (!productionEnvironmentDetected()) {
            checkSerialization(se, action);
        }
    }

    /**
     * Determines whether we are running in a production environment.  Factored out for testability.
     */
    private static boolean productionEnvironmentDetected() {
        Config c = ConfigContext.getCurrentContextConfig();
        return c != null && c.isProductionEnvironment();
    }

    /**
     * Tests whether the attribute value is serializable and logs an error if it isn't.  Note, this can be expensive
     * so we avoid it in production environments.
     * @param se the session binding event
     * @param action the listener event for logging purposes (added or replaced)
     */
    protected void checkSerialization(final HttpSessionBindingEvent se, String action) {
        final Object o = se.getValue();
        if(o != null) {
            if (!isSerializable(o)) {
                LOG.error("Attribute of class " + o.getClass().getName() + " with name " + se.getName() + " from source " + se.getSource().getClass().getName() + " was " + action + " to session and does not implement " + Serializable.class.getName());
            } else if (!canBeSerialized((Serializable) o)){
                LOG.error("Attribute of class " + o.getClass().getName() + " with name " + se.getName() + " from source " + se.getSource().getClass().getName() + " was " + action + " to session and cannot be Serialized");
            }
        }
    }

    /**
     * Simply tests whether the object implements the Serializable interface
     */
    private static boolean isSerializable(Object o) {
        return o instanceof Serializable;
    }

    /**
     * Performs an expensive test of serializability by attempting to serialize the object graph
     */
    private static boolean canBeSerialized(Serializable o) {
        ByteArrayOutputStream baos = null;
        ObjectOutputStream out = null;
        try {
            baos = new ByteArrayOutputStream(512);
            out = new ObjectOutputStream(baos);
            out.writeObject((Serializable) o);
            return true;
        } catch (IOException e) {
            LOG.warn("error serializing object" , e);
        } finally {
            try {
                if (baos != null) {
                    try {
                        baos.close();
                    } catch (IOException e) {
                        LOG.warn("error closing stream" , e);
                    }
                }
            } finally {
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                         LOG.warn("error closing stream" , e);
                    }
                }
            }
        }

        return false;
    }
}