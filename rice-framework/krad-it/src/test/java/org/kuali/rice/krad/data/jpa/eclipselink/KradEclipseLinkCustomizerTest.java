package org.kuali.rice.krad.data.jpa.eclipselink;

import org.junit.Test;
import org.kuali.rice.krad.bo.DataObjectBase;
import org.kuali.rice.krad.test.KRADTestCase;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
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
    public void testSequences_AnnotationAtClassLevel() throws Exception {
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

    @Test
    public void testSequences_AnnotationAtFieldLevel() throws Exception {
        EntityManagerFactory factory = (EntityManagerFactory)context.getBean("entityManagerFactory");
        assertNotNull(factory);

        TestEntity2 testEntity1 = new TestEntity2();
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

        TestEntity2 testEntity2 = new TestEntity2();
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

    @Test
    public void testSequences_AnnotationAtMethodLevel() throws Exception {
        EntityManagerFactory factory = (EntityManagerFactory)context.getBean("entityManagerFactory");
        assertNotNull(factory);

        TestEntity3 testEntity1 = new TestEntity3();
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

        TestEntity3 testEntity2 = new TestEntity3();
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

    @Test
    public void testSequences_MappedSuperClass() throws Exception {
        EntityManagerFactory factory = (EntityManagerFactory)context.getBean("entityManagerFactory");
        assertNotNull(factory);

        TestEntity4 testEntity1 = new TestEntity4();
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

        TestEntity4 testEntity2 = new TestEntity4();
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

    /**
     * Simple entity for testing of id generation. Using the TRV_ACCT table because it's available and easy to
     * map to.
     */
    @Entity
    @Table(name="TRV_ACCT")
    @PortableSequenceGenerator(name="TRVL_ID_SEQ") // sequence name should default to TRVL_ID_SEQ
    public static class TestEntity extends DataObjectBase {

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

    /**
     * Simple entity for testing of id generation. Using the TRV_ACCT table because it's available and easy to
     * map to.
     */
    @Entity
    @Table(name="TRV_ACCT")
    public static class TestEntity2 extends DataObjectBase {

        @Id
        @GeneratedValue(generator="TRVL_ID_SEQ_2")
        @PortableSequenceGenerator(name="TRVL_ID_SEQ_2", sequenceName="TRVL_ID_SEQ")
        @Column(name="ACCT_NUM")
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

    /**
     * Simple entity for testing of id generation. Using the TRV_ACCT table because it's available and easy to
     * map to.
     */
    @Entity
    @Table(name="TRV_ACCT")
    @Access(AccessType.PROPERTY)
    public static class TestEntity3 extends DataObjectBase {

        private String number;
        private String name;
        private Long amId;

        @Column(name="ACCT_FO_ID")
        public Long getAmId() {
            return amId;
        }

        public void setAmId(Long amId) {
            this.amId = amId;
        }

        @Column(name="ACCT_NAME")
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Id
        @GeneratedValue(generator="TRVL_ID_SEQ_3")
        @PortableSequenceGenerator(name="TRVL_ID_SEQ_3", sequenceName="TRVL_ID_SEQ")
        @Column(name="ACCT_NUM")
        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }
    }

    @MappedSuperclass
    @PortableSequenceGenerator(name = "TRVL_ID_SEQ_4", sequenceName = "TRVL_ID_SEQ", initialValue = 1000)
    public abstract static class ParentTestEntity extends DataObjectBase {

        public abstract String getNumber();

    }

    @Entity
    @Table(name="TRV_ACCT")
    public static class TestEntity4 extends ParentTestEntity {

        @Id
        @GeneratedValue(generator = "TRVL_ID_SEQ_4")
        @Column(name="ACCT_NUM")
        private String number;

        @Column(name="ACCT_NAME")
        private String name;

        @Column(name="ACCT_FO_ID")
        private Long amId;

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

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

    }

}
