package org.kuali.rice.krad.service;

import org.junit.Test;
import org.kuali.rice.core.api.config.ConfigurationException;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.krad.test.KRADTestCase;
import org.kuali.rice.krad.test.document.OjbAndJpa;
import org.kuali.rice.krad.test.document.OjbOnly;
import org.kuali.rice.krad.test.document.bo.JPADataObject;

import static org.junit.Assert.*;

/**
 * Tests various scenarios on SequenceAccessorService to make sure that it still works with OJB but throws
 * exception if you try to use it with the new krad-data + JPA module.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class SequenceAccessorServiceTest extends KRADTestCase {

    private static final String ARBITRARY_SEQUENCE = "trvl_id_seq";

    /**
     * Checks what happens when you call the SequenceAccessorService with KRAD Data in a non-legacy context. In this
     * case it should throw a ConfigurationException.
     */
    @Test
    public void testExceptionForKradData() {
        try {
            KNSServiceLocator.getSequenceAccessorService().getNextAvailableSequenceNumber(ARBITRARY_SEQUENCE, JPADataObject.class);
            fail( "Using Legacy SequenceAccessorService in non-Legacy Context - should have failed." );
        } catch ( ConfigurationException ex ) {
            // we expected this, do nothing
        } catch ( Exception ex ) {
            fail( "We should have failed with a configuration Exception - but intead got a: " + ex.getClass().getName() + " : " + ex.getMessage() );
            ex.printStackTrace();
        }
    }

    @Test
    public void testOjbOnlyWorks() {
        Long nextAvailableSequenceNumber =
                KNSServiceLocator.getSequenceAccessorService().getNextAvailableSequenceNumber(ARBITRARY_SEQUENCE, OjbOnly.class);
        assertNotNull("Next sequence number should not have been null",nextAvailableSequenceNumber);
        assertTrue( "Nest sequence number should have been created than zero.  Was: " + nextAvailableSequenceNumber, nextAvailableSequenceNumber.longValue() > 0);
    }

    @Test
    @Legacy
    public void testNoExceptionForBothKradDataAndOjb_InLegacyContext() {
        Long nextAvailableSequenceNumber =
                KNSServiceLocator.getSequenceAccessorService().getNextAvailableSequenceNumber(ARBITRARY_SEQUENCE, OjbAndJpa.class);
        assertNotNull("Next sequence number should not have been null",nextAvailableSequenceNumber);
        assertTrue( "Nest sequence number should have been created than zero.  Was: " + nextAvailableSequenceNumber, nextAvailableSequenceNumber.longValue() > 0);
    }

    @Test
    public void testExceptionForBothKradDataAndOjb_NotInLegacyContext() {
        try {
            // using DocumentType because it's (currently) mapped in both OJB
            KNSServiceLocator.getSequenceAccessorService().getNextAvailableSequenceNumber(ARBITRARY_SEQUENCE, OjbAndJpa.class);
            fail( "Using Legacy SequenceAccessorService in non-Legacy Context - should have failed." );
        } catch ( ConfigurationException ex ) {
            // we expected this, do nothing
        } catch ( Exception ex ) {
            fail( "We should have failed with a configuration Exception - but intead got a: " + ex.getClass().getName() + " : " + ex.getMessage() );
            ex.printStackTrace();
        }
   }

}