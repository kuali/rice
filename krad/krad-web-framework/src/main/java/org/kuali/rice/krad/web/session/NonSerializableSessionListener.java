package org.kuali.rice.krad.web.session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

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

    private static void logSerializationViolations(HttpSessionBindingEvent se, String action) {
        final Object o = se.getValue();
        if (!isSerializable(o)) {
            LOG.error("Attribute of class " + o.getClass().getName() + " with name " + se.getName() + " from source " + se.getSource().getClass().getName() + " was " + action + " to session and does not implement " + Serializable.class.getName());
        } else if (!canBeSerialized((Serializable) o)){
            LOG.error("Attribute of class " + o.getClass().getName() + " with name " + se.getName() + " from source " + se.getSource().getClass().getName() + " was " + action + " to session and cannot be Serialized");
        }
    }

    private static boolean isSerializable(Object o) {
        return o instanceof Serializable;
    }

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
