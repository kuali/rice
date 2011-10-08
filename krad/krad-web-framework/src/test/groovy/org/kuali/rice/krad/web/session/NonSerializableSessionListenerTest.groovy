package org.kuali.rice.krad.web.session

import javax.servlet.http.HttpSessionBindingEvent
import org.junit.Test
import org.kuali.rice.core.api.config.property.Config
import org.kuali.rice.core.api.config.property.ConfigContext
import org.kuali.rice.core.framework.config.property.SimpleConfig
import org.springframework.mock.web.MockHttpSession
import static org.junit.Assert.assertFalse
import static org.junit.Assert.assertTrue

/**
 * Tests that NonSerializableSessionListener serialization checks are disabled in production
 */
class NonSerializableSessionListenerTest {
    static class TestNonSerializableSessionListener extends NonSerializableSessionListener {
        private serializationChecked = false
        protected void checkSerialization(final HttpSessionBindingEvent se, String action) {
            serializationChecked = true
            super.checkSerialization(se, action)
        }
    }

    @Test
    void listenerIsExecutedInNonProductionEnvironment() {
        def config = new SimpleConfig()
        config.putProperty(Config.ENVIRONMENT, "dev")
        ConfigContext.init(config)

        def listener = new TestNonSerializableSessionListener()
        listener.attributeAdded(new HttpSessionBindingEvent(new MockHttpSession(), "attrib", "value"))
        assertTrue(listener.serializationChecked)
    }

    @Test
    void listenerIsNotExecutedInProductionEnvironment() {
        def config = new SimpleConfig()
        config.putProperty(Config.PROD_ENVIRONMENT_CODE, "prod")
        config.putProperty(Config.ENVIRONMENT, "prod")
        ConfigContext.init(config)

        def listener = new TestNonSerializableSessionListener()
        listener.attributeAdded(new HttpSessionBindingEvent(new MockHttpSession(), "attrib", "value"))
        assertFalse(listener.serializationChecked)
    }
}