package org.kuali.rice.krad.data.provider.jpa.eclipselink;

import org.junit.Test;
import org.kuali.rice.krad.bo.VersionedAndGloballyUniqueBase;
import org.kuali.rice.krad.test.KRADTestCase;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import static org.junit.Assert.*;

/**
 * Integration test for KradEclipseLinkCustomizer.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class KradEclipseLinkCustomizerTest extends KRADTestCase {

    private ConfigurableApplicationContext context;

    @Override
    protected void setUpInternal() throws Exception {
        super.setUpInternal();
        context = new ClassPathXmlApplicationContext("KradEclipseLinkCustomizerTest.xml", getClass());
    }

    @Override
    public void tearDown() throws Exception {
        if (context != null) {
            context.close();
        }
        super.tearDown();
    }

    @Test
    public void testSequences() throws Exception {
        EntityManagerFactory factory = (EntityManagerFactory)context.getBean("entityManagerFactory");
        assertNotNull(factory);

        TestEntity testEntity1 = new TestEntity();
        testEntity1.setName("MyAwesomeTestEntity1");

        // number in this case is generated from a sequence
        assertNull(testEntity1.getNumber());

        EntityManager entityManager = factory.createEntityManager();
        try {
            entityManager.persist(testEntity1);
            assertNotNull(testEntity1.getNumber());
        } finally {
            entityManager.close();
        }

        TestEntity testEntity2 = new TestEntity();
        testEntity2.setName("MyAwesomeTestEntity2");

        assertNull(testEntity2.getNumber());

        entityManager = factory.createEntityManager();
        try {
            // try merge here and make sure it works with that as well
            testEntity2 = entityManager.merge(testEntity2);
            assertNotNull(testEntity2.getNumber());
            assertEquals(Integer.valueOf(Integer.valueOf(testEntity1.getNumber()).intValue() + 1), Integer.valueOf(testEntity2.getNumber()));
        } finally {
            entityManager.close();
        }

    }

//    TODO - need to hook up and test the other aspects of PortableSequenceGenerator!
//
//    Is there anyway to hook this up to schema generation because that woudl be cool?

    /**
     * Simple entity for testing of id generation. Using the TRV_ACCT table because it's available and easy to
     * map to.
     */
    @Entity
    @Table(name="TRV_ACCT")
    @PortableSequenceGenerator(name="TRVL_ID_SEQ", sequenceName="TRVL_ID_SEQ")
    public static class TestEntity extends VersionedAndGloballyUniqueBase {

        @Id
        @Column(name="ACCT_NUM")
        @GeneratedValue(generator="TRVL_ID_SEQ")
        private String number;

        @Column(name="ACCT_NAME")
        private String name;

        @Column(name="ACCT_FO_ID")
        private Long amId;

        public Long getAmId() {
            return amId;
        }

        public void setAmId(Long amId) {
            this.amId = amId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }
    }


}
