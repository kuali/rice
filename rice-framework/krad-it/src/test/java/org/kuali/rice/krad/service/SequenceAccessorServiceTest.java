package org.kuali.rice.krad.service;

import org.junit.Test;
import org.kuali.rice.core.api.config.ConfigurationException;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.krad.document.authorization.PessimisticLock;
import org.kuali.rice.krad.test.KRADTestCase;
import org.kuali.rice.krad.test.document.OjbOnly;
import org.kuali.rice.krad.test.document.bo.JPADataObject;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests various scenarios on SequenceAccessorService to make sure that it still works with OJB but throws
 * exception if you try to use it with the new krad-data + JPA module.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@KRADTestCase.Legacy
public class SequenceAccessorServiceTest extends KRADTestCase {

    private static final String ARBITRARY_SEQUENCE = "trvl_id_seq";

    /**
     * Checks what happens when you call the SequenceAccessorService with KRAD Data in a non-legacy context. In this
     * case it should throw a ConfigurationException.
     */
    @Test(expected = ConfigurationException.class)
    public void testExceptionForKradData() {
        KNSServiceLocator.getSequenceAccessorService().getNextAvailableSequenceNumber(ARBITRARY_SEQUENCE, JPADataObject.class);
    }

    @Test
    public void testOjbOnlyWorks() {
        Long nextAvailableSequenceNumber =
                KNSServiceLocator.getSequenceAccessorService().getNextAvailableSequenceNumber(ARBITRARY_SEQUENCE, OjbOnly.class);
        assertNotNull(nextAvailableSequenceNumber);
        assertTrue(nextAvailableSequenceNumber.longValue() > 0);

    }

    @Test
    @Legacy
    public void testNoExceptionForBothKradDataAndOjb_InLegacyContext() {
        Long nextAvailableSequenceNumber =
                KNSServiceLocator.getSequenceAccessorService().getNextAvailableSequenceNumber(ARBITRARY_SEQUENCE, PessimisticLock.class);
        assertNotNull(nextAvailableSequenceNumber);
        assertTrue(nextAvailableSequenceNumber.longValue() > 0);
    }

    @Test(expected = ConfigurationException.class)
    public void testExceptionForBothKradDataAndOjb_NotInLegacyContext() {
        KNSServiceLocator.getSequenceAccessorService().getNextAvailableSequenceNumber(ARBITRARY_SEQUENCE, PessimisticLock.class);
    }

}