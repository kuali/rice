package org.kuali.rice.krad.data.jpa.eclipselink;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.kuali.rice.krad.bo.DataObjectBase;
import org.kuali.rice.krad.data.jpa.FilterGenerator;
import org.kuali.rice.krad.data.jpa.FilterGenerators;
import org.kuali.rice.krad.data.jpa.PortableSequenceGenerator;
import org.kuali.rice.krad.data.provider.annotation.ExtensionFor;
import org.kuali.rice.krad.test.KRADTestCase;
import org.kuali.rice.krad.test.document.bo.SimpleAccount;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import java.util.ArrayList;
import java.util.List;

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
        EntityManagerFactory factory = (EntityManagerFactory) context.getBean("entityManagerFactory");
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
            assertEquals(Integer.valueOf(Integer.valueOf(testEntity1.getNumber()).intValue() + 1), Integer.valueOf(
                    testEntity2.getNumber()));
        } finally {
            entityManager.close();
        }

    }

    @Test
    public void testSequences_AnnotationAtFieldLevel() throws Exception {
        EntityManagerFactory factory = (EntityManagerFactory) context.getBean("entityManagerFactory");
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
            assertEquals(Integer.valueOf(Integer.valueOf(testEntity1.getNumber()).intValue() + 1), Integer.valueOf(
                    testEntity2.getNumber()));
        } finally {
            entityManager.close();
        }

    }

    @Test
    public void testSequences_AnnotationAtFieldLevel_MappedSuperClass() throws Exception {
        EntityManagerFactory factory = (EntityManagerFactory) context.getBean("entityManagerFactory");
        assertNotNull(factory);

        TestEntity5 testEntity1 = new TestEntity5();
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

        TestEntity5 testEntity2 = new TestEntity5();
        testEntity2.setName("MyAwesomeTestEntity2");

        assertNull(testEntity2.getNumber());

        entityManager = factory.createEntityManager();
        try {
            // try merge here and make sure it works with that as well
            testEntity2 = entityManager.merge(testEntity2);
            assertNotNull(testEntity2.getNumber());
            assertEquals(Integer.valueOf(Integer.valueOf(testEntity1.getNumber()).intValue() + 1), Integer.valueOf(
                    testEntity2.getNumber()));
        } finally {
            entityManager.close();
        }

    }

    @Test
    public void testSequences_AnnotationAtMethodLevel() throws Exception {
        EntityManagerFactory factory = (EntityManagerFactory) context.getBean("entityManagerFactory");
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
            assertEquals(Integer.valueOf(Integer.valueOf(testEntity1.getNumber()).intValue() + 1), Integer.valueOf(
                    testEntity2.getNumber()));
        } finally {
            entityManager.close();
        }

    }

    @Test
    public void testSequences_MappedSuperClass() throws Exception {
        EntityManagerFactory factory = (EntityManagerFactory) context.getBean("entityManagerFactory");
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
            assertEquals(Integer.valueOf(Integer.valueOf(testEntity1.getNumber()).intValue() + 1), Integer.valueOf(
                    testEntity2.getNumber()));
        } finally {
            entityManager.close();
        }

    }

    /**
     * Simple entity for testing of id generation. Using the TRV_ACCT table because it's available and easy to
     * map to.
     */
    @Entity
    @Table(name = "TRV_ACCT")
    @PortableSequenceGenerator(name = "TRVL_ID_SEQ") // sequence name should default to TRVL_ID_SEQ
    public static class TestEntity extends DataObjectBase {

        @Id @Column(name = "ACCT_NUM") @GeneratedValue(generator = "TRVL_ID_SEQ")
        private String number;

        @Column(name = "ACCT_NAME")
        private String name;

        @Column(name = "ACCT_FO_ID")
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
    @Table(name = "TRV_ACCT")
    public static class TestEntity2 extends DataObjectBase {

        @Id @GeneratedValue(generator = "TRVL_ID_SEQ_2") @PortableSequenceGenerator(name = "TRVL_ID_SEQ_2",
                sequenceName = "TRVL_ID_SEQ") @Column(name = "ACCT_NUM")
        private String number;

        @Column(name = "ACCT_NAME")
        private String name;

        @Column(name = "ACCT_FO_ID")
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
    @Table(name = "TRV_ACCT")
    @Access(AccessType.PROPERTY)
    public static class TestEntity3 extends DataObjectBase {

        private String number;
        private String name;
        private Long amId;

        @Column(name = "ACCT_FO_ID")
        public Long getAmId() {
            return amId;
        }

        public void setAmId(Long amId) {
            this.amId = amId;
        }

        @Column(name = "ACCT_NAME")
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Id
        @GeneratedValue(generator = "TRVL_ID_SEQ_3")
        @PortableSequenceGenerator(name = "TRVL_ID_SEQ_3", sequenceName = "TRVL_ID_SEQ")
        @Column(name = "ACCT_NUM")
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
    @Table(name = "TRV_ACCT")
    public static class TestEntity4 extends ParentTestEntity {

        @Id @GeneratedValue(generator = "TRVL_ID_SEQ_4") @Column(name = "ACCT_NUM")
        private String number;

        @Column(name = "ACCT_NAME")
        private String name;

        @Column(name = "ACCT_FO_ID")
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

    @MappedSuperclass
    public abstract static class ParentTestEntity2 extends DataObjectBase {

        @Id @GeneratedValue(generator = "TRVL_ID_SEQ_5") @PortableSequenceGenerator(name = "TRVL_ID_SEQ_5",
                sequenceName = "TRVL_ID_SEQ", initialValue = 1000) @Column(name = "ACCT_NUM")
        private String number;

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

    }

    @Entity
    @Table(name = "TRV_ACCT")
    public static class TestEntity5 extends ParentTestEntity2 {

        @Column(name = "ACCT_NAME")
        private String name;

        @Column(name = "ACCT_FO_ID")
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

    }

    @Test
    public void testQueryCustomizerMatch() throws Exception {
        EntityManagerFactory factory = (EntityManagerFactory) context.getBean("entityManagerFactory");
        assertNotNull(factory);

        TestEntity6 testEntity1 = new TestEntity6();
        testEntity1.setName("MyAwesomeTestEntity1");

        TestRelatedExtension extension = new TestRelatedExtension();
        extension.setAccountTypeCode("TS");

        EntityManager entityManager = factory.createEntityManager();

        try {
            testEntity1 = new TestEntity6();
            testEntity1.setName("MyCustomFilter");
            entityManager.persist(testEntity1);
            extension.setNumber(testEntity1.getNumber());
            entityManager.persist(extension);
            entityManager.flush();
        } finally {
            entityManager.close();
        }

        //Now confirm that the entity fetch found travel extension
        try {
            entityManager = factory.createEntityManager();
            testEntity1 = entityManager.find(TestEntity6.class, testEntity1.getNumber());
            assertTrue("Match found for base entity", testEntity1 != null && StringUtils.equals("MyCustomFilter",
                    testEntity1.getName()));
            assertTrue("Found matching travel extension that matches", testEntity1.getAccountExtension() != null);
        } finally {
            entityManager.close();
        }

    }

    @Test
    public void testQueryCustomizerNoMatch() throws Exception {
        EntityManagerFactory factory = (EntityManagerFactory) context.getBean("entityManagerFactory");
        assertNotNull(factory);

        TestEntity6 testEntity1 = new TestEntity6();
        testEntity1.setName("MyAwesomeTestEntity1");

        TestRelatedExtension extension = new TestRelatedExtension();
        extension.setAccountTypeCode("NM");

        EntityManager entityManager = factory.createEntityManager();

        try {
            testEntity1 = new TestEntity6();
            testEntity1.setName("MyCustomFilter");
            entityManager.persist(testEntity1);
            extension.setNumber(testEntity1.getNumber());
            entityManager.persist(extension);
            entityManager.flush();
        } finally {
            entityManager.close();
        }

        //Now confirm that the entity fetch found travel extension
        try {
            entityManager = factory.createEntityManager();
            testEntity1 = entityManager.find(TestEntity6.class, testEntity1.getNumber());
            assertTrue("Matched found for base entity", testEntity1 != null && StringUtils.equals("MyCustomFilter",
                    testEntity1.getName()));
            assertTrue("No matching travel extension", testEntity1.getAccountExtension() == null);
        } finally {
            entityManager.close();
        }

    }

    @Test
    public void testQueryCustomizerSort() throws Exception {
        EntityManagerFactory factory = (EntityManagerFactory) context.getBean("entityManagerFactory");
        assertNotNull(factory);

        TestEntity7 testEntity1 = new TestEntity7();
        testEntity1.setName("MyAwesomeTestEntity1");

        TestRelatedExtension2 extension2 = new TestRelatedExtension2();
        extension2.setAccountTypeCode("ZZ");

        TestRelatedExtension2 extension = new TestRelatedExtension2();
        extension.setAccountTypeCode("NM");

        EntityManager entityManager = factory.createEntityManager();

        try {
            testEntity1 = new TestEntity7();
            testEntity1.setName("MyCustomFilter");
            entityManager.persist(testEntity1);
            extension.setNumber(testEntity1.getNumber());
            entityManager.persist(extension);
            entityManager.flush();
        } finally {
            entityManager.close();
        }

        try {
            entityManager = factory.createEntityManager();
            extension2.setNumber(testEntity1.getNumber());
            entityManager.persist(extension2);
            entityManager.flush();
        } finally {
            entityManager.close();
        }

        entityManager = factory.createEntityManager();

        //Now confirm that the entity fetch found travel extension
        try {
            entityManager = factory.createEntityManager();
            testEntity1 = entityManager.find(TestEntity7.class, testEntity1.getNumber());
            assertTrue("Matched found for base entity", testEntity1 != null && StringUtils.equals("MyCustomFilter",
                    testEntity1.getName()));
            assertTrue("Fetching 2 extensions",
                    testEntity1.getAccountExtension() != null && testEntity1.getAccountExtension().size() == 2);
            assertTrue("Fetched ZZ extension first ", StringUtils.equals(testEntity1.getAccountExtension().get(0)
                    .getAccountTypeCode(), "ZZ"));
        } finally {
            entityManager.close();
        }

    }

    @Test
    public void testQueryCustomizerValueClass() throws Exception {
        EntityManagerFactory factory = (EntityManagerFactory) context.getBean("entityManagerFactory");
        assertNotNull(factory);

        TestEntity8 testEntity1 = new TestEntity8();
        testEntity1.setName("MyAwesomeTestEntity1");

        TestRelatedExtension extension = new TestRelatedExtension();
        extension.setAccountTypeCode("NM");

        EntityManager entityManager = factory.createEntityManager();

        try {
            testEntity1 = new TestEntity8();
            testEntity1.setName("MyCustomFilter");
            entityManager.persist(testEntity1);
            extension.setNumber(testEntity1.getNumber());
            entityManager.persist(extension);
            entityManager.flush();
        } finally {
            entityManager.close();
        }

        //Now confirm that the entity fetch found travel extension
        try {
            entityManager = factory.createEntityManager();
            testEntity1 = entityManager.find(TestEntity8.class, testEntity1.getNumber());
            assertTrue("Matched found for base entity", testEntity1 != null && StringUtils.equals("MyCustomFilter",
                    testEntity1.getName()));
            assertTrue("Matching travel extension", testEntity1.getAccountExtension() == null);
        } finally {
            entityManager.close();
        }

    }

    @Test
    public void testQueryCustomizerNoMatchMultipleCustomizers() throws Exception {
        EntityManagerFactory factory = (EntityManagerFactory) context.getBean("entityManagerFactory");
        assertNotNull(factory);

        TestEntity9 testEntity1 = new TestEntity9();
        testEntity1.setName("MyAwesomeTestEntity1");

        TestRelatedExtension extension = new TestRelatedExtension();
        extension.setAccountTypeCode("TS");

        EntityManager entityManager = factory.createEntityManager();

        try {
            testEntity1 = new TestEntity9();
            testEntity1.setName("MyCustomFilter");
            entityManager.persist(testEntity1);
            extension.setNumber(testEntity1.getNumber());
            entityManager.persist(extension);
            entityManager.flush();
        } finally {
            entityManager.close();
        }

        //Now confirm that the entity fetch found travel extension
        try {
            entityManager = factory.createEntityManager();
            testEntity1 = entityManager.find(TestEntity9.class, testEntity1.getNumber());
            assertTrue("Match found for base entity", testEntity1 != null && StringUtils.equals("MyCustomFilter",
                    testEntity1.getName()));
            assertTrue("Found no travel extension that matches", testEntity1.getAccountExtension() == null);
        } finally {
            entityManager.close();
        }

    }

    @Entity
    @Table(name = "TRV_ACCT")
    public static class TestEntity6 extends ParentTestEntity2 {

        @ManyToOne(targetEntity = TestRelatedExtension.class, fetch = FetchType.EAGER) @JoinColumn(name = "ACCT_NUM",
                insertable = false, updatable = false) @FilterGenerator(attributeName = "accountTypeCode",
                attributeValue = "TS")
        private TestRelatedExtension accountExtension;

        @Column(name = "ACCT_NAME")
        private String name;

        @Column(name = "ACCT_FO_ID")
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

        public TestRelatedExtension getAccountExtension() {
            return accountExtension;
        }

        public void setAccountExtension(TestRelatedExtension accountExtension) {
            this.accountExtension = accountExtension;
        }
    }

    @Entity
    @Table(name = "TRV_ACCT_EXT")
    @ExtensionFor(SimpleAccount.class)
    public static class TestRelatedExtension extends DataObjectBase {

        @Id @Column(name = "ACCT_NUM")
        private String number;
        @Column(name = "ACCT_TYPE")
        private String accountTypeCode;

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public String getAccountTypeCode() {
            return accountTypeCode;
        }

        public void setAccountTypeCode(String accountTypeCode) {
            this.accountTypeCode = accountTypeCode;
        }
    }

    @Entity
    @Table(name = "TRV_ACCT")
    public static class TestEntity7 extends ParentTestEntity2 {

        @OneToMany(targetEntity = TestRelatedExtension2.class, fetch = FetchType.EAGER) @JoinColumn(
                name = "ACCT_NUM") @OrderBy("accountTypeCode DESC")
        private List<TestRelatedExtension2> accountExtension = new ArrayList<TestRelatedExtension2>();

        @Column(name = "ACCT_NAME")
        private String name;

        @Column(name = "ACCT_FO_ID")
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

        public List<TestRelatedExtension2> getAccountExtension() {
            return accountExtension;
        }

        public void setAccountExtension(List<TestRelatedExtension2> accountExtension) {
            this.accountExtension = accountExtension;
        }

    }

    @Entity
    @Table(name = "TRV_ACCT_EXT")
    public static class TestRelatedExtension2 extends DataObjectBase {

        @Id @Column(name = "ACCT_NUM")
        private String number;
        @Id @Column(name = "ACCT_TYPE")
        private String accountTypeCode;

        @ManyToOne(targetEntity = TestEntity7.class, fetch = FetchType.EAGER) @JoinColumn(name = "ACCT_NUM",
                insertable = false, updatable = false)
        private TestEntity7 testEntity7;

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public String getAccountTypeCode() {
            return accountTypeCode;
        }

        public void setAccountTypeCode(String accountTypeCode) {
            this.accountTypeCode = accountTypeCode;
        }

        public TestEntity7 getTestEntity7() {
            return testEntity7;
        }

        public void setTestEntity7(TestEntity7 testEntity7) {
            this.testEntity7 = testEntity7;
        }
    }

    @Entity
    @Table(name = "TRV_ACCT")
    public static class TestEntity8 extends ParentTestEntity2 {

        @ManyToOne(targetEntity = TestRelatedExtension.class, fetch = FetchType.EAGER) @JoinColumn(name = "ACCT_NUM",
                insertable = false, updatable = false) @FilterGenerator(attributeName = "accountTypeCode",
                attributeResolverClass = org.kuali.rice.krad.data.jpa.testbo.TestQueryCustomizerValue.class)
        private TestRelatedExtension accountExtension;

        @Column(name = "ACCT_NAME")
        private String name;

        @Column(name = "ACCT_FO_ID")
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

        public TestRelatedExtension getAccountExtension() {
            return accountExtension;
        }

        public void setAccountExtension(TestRelatedExtension accountExtension) {
            this.accountExtension = accountExtension;
        }
    }

    @Entity
    @Table(name = "TRV_ACCT")
    public static class TestEntity9 extends ParentTestEntity2 {

        @ManyToOne(targetEntity = TestRelatedExtension.class, fetch = FetchType.EAGER) @JoinColumn(name = "ACCT_NUM",
                insertable = false, updatable = false)
        @FilterGenerators({
        @FilterGenerator(attributeName = "accountTypeCode",attributeValue = "TS"),
        @FilterGenerator(attributeName = "accountTypeCode",attributeValue = "NM")})
        private TestRelatedExtension accountExtension;

        @Column(name = "ACCT_NAME")
        private String name;

        @Column(name = "ACCT_FO_ID")
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

        public TestRelatedExtension getAccountExtension() {
            return accountExtension;
        }

        public void setAccountExtension(TestRelatedExtension accountExtension) {
            this.accountExtension = accountExtension;
        }
    }

}
