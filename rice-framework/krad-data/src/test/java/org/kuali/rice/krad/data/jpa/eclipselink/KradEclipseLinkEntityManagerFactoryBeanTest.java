/**
 * Copyright 2005-2014 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.krad.data.jpa.eclipselink;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.metamodel.EntityType;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.persistence.spi.PersistenceUnitTransactionType;
import javax.transaction.TransactionManager;

import org.apache.commons.dbcp.BasicDataSource;
import org.eclipse.persistence.internal.jpa.EntityManagerFactoryDelegate;
import org.eclipse.persistence.internal.jpa.EntityManagerFactoryImpl;
import org.eclipse.persistence.jpa.JpaEntityManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.framework.config.property.SimpleConfig;
import org.kuali.rice.core.framework.persistence.jta.Jta;
import org.kuali.rice.krad.data.jpa.KradEntityManagerFactoryBean;
import org.kuali.rice.krad.data.jpa.eclipselink.testentities.TestEntity;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.instrument.classloading.SimpleInstrumentableClassLoader;
import org.springframework.orm.jpa.persistenceunit.MutablePersistenceUnitInfo;
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitPostProcessor;

/**
 * Tests the {@link KradEclipseLinkEntityManagerFactoryBean}.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@RunWith(MockitoJUnitRunner.class)
public class KradEclipseLinkEntityManagerFactoryBeanTest {

    @Mock TransactionManager transactionManager;
    @Mock javax.transaction.UserTransaction userTransaction;

    private ClassPathXmlApplicationContext context;
    private EntityManagerFactory entityManagerFactory;

    @Before
    public void setUp() throws Exception {
        initializeConfig();
    }

    @After
    public void tearDown() throws Exception {
        if (context != null) {
            context.destroy();
        }
        ConfigContext.destroy();
    }

    private void initializeConfig() {
        SimpleConfig config = new SimpleConfig();
        config.putProperty("rice.krad.jpa.global.randomProperty", "randomValue");
        config.putProperty("rice.krad.jpa.global.eclipselink.weaving", "false");
        ConfigContext.init(config);
    }

    private void loadContext(String springXmlFile) throws Exception {
        this.context = new ClassPathXmlApplicationContext(springXmlFile, getClass());
        Map<String, EntityManagerFactory> factories =  context.getBeansOfType(EntityManagerFactory.class);
        assertEquals(1, factories.size());
        this.entityManagerFactory = factories.values().iterator().next();
    }

    @Test
    public void testMinimal() throws Exception {
        loadContext(getClass().getSimpleName() +  "_Minimal.xml");

        Set<EntityType<?>> minimalEntities = entityManagerFactory.getMetamodel().getEntities();
        // there should be only the ConverterHolder which is loaded by default
        assertEquals(0, minimalEntities.size());

        // create the entity manager to verify that it works
        EntityManager entityManager = entityManagerFactory.createEntityManager();
		// assertFalse(entityManager.contains(new ConverterHolder()));
        entityManager.close();

        assertFalse(isJtaEnabled());
    }

    @Test
    public void testFull() throws Exception {
        loadContext(getClass().getSimpleName() +  "_Full.xml");

        Set<EntityType<?>> fullEntities = entityManagerFactory.getMetamodel().getEntities();
        assertEquals(5, fullEntities.size());
        Set<Class<?>> entityClasses = new HashSet<Class<?>>();
        for (EntityType<?> entityType : fullEntities) {
            entityClasses.add(entityType.getJavaType());
        }

        assertTrue(entityClasses.contains(TestEntity.class));
        assertTrue(entityClasses.contains(TestEntity1.class));
        assertTrue(entityClasses.contains(TestEntity2.class));
        assertTrue(entityClasses.contains(TestEntity3.class));
        assertTrue(entityClasses.contains(TestEntity4.class));

        assertFalse(isJtaEnabled());
    }

    @Test
    public void testJta() throws Exception {
        Jta.configure(transactionManager, userTransaction);
        try {
            loadContext(getClass().getSimpleName() + "_Jta.xml");
            assertTrue("JTA should be enabled.", isJtaEnabled());
        } finally {
            Jta.reset();
        }
    }

    @Test
    public void testLoadTimeWeaving() throws Exception {
        loadContext(getClass().getSimpleName() +  "_LoadTimeWeaving.xml");
        EntityManagerFactoryDelegate delegate =
                entityManagerFactory.unwrap(EntityManagerFactoryDelegate.class);
        PersistenceUnitInfo info = delegate.getSetupImpl().getPersistenceUnitInfo();
        assertTrue(info.getClassLoader() instanceof SimpleInstrumentableClassLoader);
    }

    /**
     * Verifies that it's not permitted to configure with both a JTA and Non-JTA datasource.
     */
    @Test(expected = IllegalStateException.class)
    public void testInvalidDataSourceConfiguration() throws Exception {
        // kind of a random addition to throw the superclass in here, but allows me to get some
        // coverage on some of the other methods from the superclass without having to write a separate test
        KradEntityManagerFactoryBean factoryBean =
                new KradEntityManagerFactoryBean();
        factoryBean.setDataSource(new BasicDataSource());
        factoryBean.setJtaDataSource(new BasicDataSource());
        factoryBean.afterPropertiesSet();
    }

    @Test
    public void testGetDataSource() throws Exception {
        KradEclipseLinkEntityManagerFactoryBean factoryBean =
                new KradEclipseLinkEntityManagerFactoryBean();

        BasicDataSource dataSourceNonJta = new BasicDataSource();
        factoryBean.setDataSource(dataSourceNonJta);
        assertEquals(dataSourceNonJta, factoryBean.getDataSource());

        BasicDataSource dataSourceJta = new BasicDataSource();
        factoryBean.setJtaDataSource(dataSourceJta);
        assertEquals(dataSourceJta, factoryBean.getDataSource());
    }

    /**
     * Just tests some of the getters to ensure they are delegating down to the internal factory bean and the
     * PersistenceUnitManager appropriately.
     */
    @Test
    public void testVariousGetters() throws Exception {
        loadContext(getClass().getSimpleName() +  "_LoadTimeWeaving.xml");
        KradEclipseLinkEntityManagerFactoryBean factoryBean =
                context.getBean(KradEclipseLinkEntityManagerFactoryBean.class);
        assertNotNull(factoryBean);

        assertEquals(2, factoryBean.getPersistenceUnitPostProcessors().length);
        EntityManagerFactory entityManagerFactory = factoryBean.getNativeEntityManagerFactory();
        assertTrue(entityManagerFactory instanceof EntityManagerFactoryImpl);
        assertEquals(factoryBean.getBeanClassLoader(), getClass().getClassLoader());
        assertEquals(JpaEntityManager.class, factoryBean.getEntityManagerInterface());

    }

    private boolean isJtaEnabled() throws Exception {
        EntityManagerFactoryDelegate delegate =
                entityManagerFactory.unwrap(EntityManagerFactoryDelegate.class);
        PersistenceUnitInfo info = delegate.getSetupImpl().getPersistenceUnitInfo();
        return info.getJtaDataSource() != null && info.getTransactionType() == PersistenceUnitTransactionType.JTA;
    }

    public static final class TestPersistenceUnitPostProcessor implements PersistenceUnitPostProcessor {
        @Override
        public void postProcessPersistenceUnitInfo(MutablePersistenceUnitInfo pui) {
            pui.getManagedClassNames().add(TestEntity3.class.getName());
        }
    }

    @Entity
    public static final class TestEntity1 {
        @Id
        private String id;
    }

    @Entity
    public static final class TestEntity2 {
        @Id
        private String id;
    }

    @Entity
    public static final class TestEntity3 {
        @Id
        private String id;
    }

    public static final class TestEntity4 {
        private String id;
    }

    @Entity
    public static final class TestEntity5 {
        @Id
        private String id;

        @ManyToOne(fetch = FetchType.LAZY)
        private TestEntity3 testEntity3;

    }


}
