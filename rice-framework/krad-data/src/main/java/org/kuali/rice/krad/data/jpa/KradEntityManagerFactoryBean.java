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
package org.kuali.rice.krad.data.jpa;

import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.krad.data.jpa.converters.BooleanYNConverter;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.weaving.LoadTimeWeaverAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.support.PersistenceExceptionTranslator;
import org.springframework.instrument.classloading.LoadTimeWeaver;
import org.springframework.orm.jpa.EntityManagerFactoryInfo;
import org.springframework.orm.jpa.JpaDialect;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.persistenceunit.DefaultPersistenceUnitManager;
import org.springframework.orm.jpa.persistenceunit.MutablePersistenceUnitInfo;
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitManager;
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitPostProcessor;
import org.springframework.util.ClassUtils;

import javax.persistence.Converter;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;
import javax.persistence.spi.PersistenceProvider;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.sql.DataSource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * A KRAD-managed {@link javax.persistence.EntityManagerFactory} factory bean which can be used to configure a JPA
 * persistence unit using the Spring Framework.
 *
 * <p>
 * This implementation does not support the use of a custom PersistenceUnitManager, but rather stores and manages one
 * internally. This is intended to be an alternative to direct usage of Spring's
 * {@link LocalContainerEntityManagerFactoryBean} in order to make JPA configuration with KRAD simpler.
 * </p>
 *
 * <h1>Minimal Configuration</h1>
 *
 * <p>Minimal configuration of this factory bean will include the following:</p>
 *
 * <ul>
 *     <li>{@code persistenceUnitName}</li>
 *     <li>{@code dataSource} or {@code jtaDataSource}</li>
 *     <li>{@code persistenceProvider} or {@code jpaVendorAdapter}</li>
 * </ul>
 *
 * <p>
 * Note that persistence unit names must be unique, so choose a name which is unlikely to clash with any potential
 * persistence units configured within the runtime environment.
 * </p>
 *
 * <h1>Behavior</h1>
 *
 * <p>
 * When leveraging this class, persistence.xml files are not used. Rather, persistence unit configuration is loaded
 * via the various settings provided on this factory bean which contain everything needed to create the desired
 * persistence unit configuration in the majority of cases. If a KRAD application needs more control over the
 * configuration of the persistence unit or wants to use persistence.xml and JPA's default bootstrapping and classpath
 * scanning behavior, an EntityManagerFactory can be configured using the other classes provided by the Spring
 * framework or by other means as needed. See {@link LocalContainerEntityManagerFactoryBean} for an alternative
 * approach.
 * </p>
 *
 * <p>
 * Only one of JTA or non-JTA datasource can be set. Depending on which one is set, the underlying persistence unit
 * will have it's {@link javax.persistence.spi.PersistenceUnitTransactionType} set to either RESOURCE_LOCAL or JTA. If
 * both of these are set, then this class will throw an {@code IllegalStateException} when the
 * {@link #afterPropertiesSet()} method is invoked by the Spring Framework.
 * </p>
 *
 * <p>
 * Elsewhere, this class delegates to implementations of {@link LocalContainerEntityManagerFactoryBean} and
 * {@link DefaultPersistenceUnitManager}, so information on the specific behavior of some of the settings and methods on
 * this class can be found on the javadoc for those classes as well.
 * </p>
 *
 * <h1>JPA Property Defaults</h1>
 *
 * <p>
 * When {@link #afterPropertiesSet()} is invoked, this class will scan the current {@link ConfigContext} for JPA
 * properties and make them available to the persistence unit. It will combine these with any properties that were set
 * directly on this factory bean via the {@link #setJpaProperties(java.util.Properties)} or
 * {@link #setJpaPropertyMap(java.util.Map)} methods.
 * </p
 *
 * <p>
 * This scanning occurs in the following order, items later in the list will override any properties from earlier if
 * they happen to set the same effective property value:
 * </p>
 *
 * <ol>
 *   <li>Scan ConfigContext for properties that begin with "rice.krad.jpa.global." For any found, strip off this prefix
 *       prior to placing it into the JPA property map.</li>
 *   <li>Scan ConfigContext for properties that being with "rice.krad.jpa.&lt;persistence-unit-name&gt;" where
 *       "persistence-unit-name" is the configured name of this persistence unit. For any found, strip off this prefix
 *       prior to placing it into the JPA property map.</li>
 *   <li>Invoke {@link #loadCustomJpaDefaults(java.util.Map)} to allow for possible subclass customization of JPA
 *       property defaults</li>
 *   <li>Load the JPA properties configured via {@link #setJpaPropertyMap(java.util.Map)} and
 *   {@link #setJpaProperties(java.util.Properties)}. It is potentially non-deterministic which of these setters will
 *   take precedence over the other, so it is recommended to only invoke one of them on a given instance of this
 *   factory bean.</li>
 * </ol>
 *
 * <h1>Subclassing</h1>
 *
 * <p>
 * This class can be subclassed to provide additional specialized implementations of this factory bean. A potential use
 * for this might be to provide a factory bean that defaults certain values as part of it's default setup.
 * </p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class KradEntityManagerFactoryBean implements FactoryBean<EntityManagerFactory>, BeanClassLoaderAware,
        BeanFactoryAware, BeanNameAware, InitializingBean, DisposableBean, EntityManagerFactoryInfo,
        PersistenceExceptionTranslator, ResourceLoaderAware, LoadTimeWeaverAware {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
			.getLogger(KradEntityManagerFactoryBean.class);

    /**
     * Prefix for property names that are passed to the JPA persistence context as JPA properties.
     *
     * <p>
     * To use this, concatenate this prefix with the persistence context name. The total prefix (including the
     * persistence context) will then be stripped before being passed to the JPA entity manager factory when using
     * the {@link org.kuali.rice.krad.data.jpa.KradEntityManagerFactoryBean}.
     * </p>
     *
     * @see org.kuali.rice.krad.data.jpa.KradEntityManagerFactoryBean
     */
    public static final String JPA_PROPERTY_PREFIX = "rice.krad.jpa.";

    /**
     * Prefix for property names that are passed to *all* JPA persistence contexts as JPA properties.
     *
     * <p>
     * The total prefix will then be stripped before being passed to all JPA entity manager factories that use the
     * {@link org.kuali.rice.krad.data.jpa.KradEntityManagerFactoryBean}.
     * </p>
     *
     * @see org.kuali.rice.krad.data.jpa.KradEntityManagerFactoryBean
     */
    public static final String GLOBAL_JPA_PROPERTY_PREFIX = JPA_PROPERTY_PREFIX + "global.";

    private static final boolean DEFAULT_EXCLUDE_UNLISTED_CLASSES = true;
    private static final String DEFAULT_CONVERTERS_PACKAGE = BooleanYNConverter.class.getPackage().getName();

    private final DefaultPersistenceUnitManager persistenceUnitManager;
    private final LocalContainerEntityManagerFactoryBean internalFactoryBean;

    private List<PersistenceUnitPostProcessor> persistenceUnitPostProcessors;
    private List<String> managedClassNames;
	private List<String> converterPackageNames;

    /**
     * Creates a default KRAD-managed factory bean.
     */
    public KradEntityManagerFactoryBean() {
        this.persistenceUnitPostProcessors = new ArrayList<PersistenceUnitPostProcessor>();
        this.managedClassNames = new ArrayList<String>();
		this.converterPackageNames = new ArrayList<String>();
		converterPackageNames.add(DEFAULT_CONVERTERS_PACKAGE); // set as default
        this.persistenceUnitManager = createPersistenceUnitManager();
        this.internalFactoryBean = createInternalFactoryBean(this.persistenceUnitManager);
        this.internalFactoryBean.setJpaPropertyMap(createDefaultJpaProperties());
    }

    /**
     * Retrieve a reference to the internal {@link LocalContainerEntityManagerFactoryBean} which is used by this factory
     * bean.
     *
     * <p>Primarily intended to allow subclasses to access this internal factory bean when needed.</p>
     *
     * @return the internal {@link LocalContainerEntityManagerFactoryBean} managed by this bean
     */
    protected LocalContainerEntityManagerFactoryBean getInternalFactoryBean() {
        return this.internalFactoryBean;
    }

    /**
     * Creates a persistence unit manager.
     *
     * @return a persistence unit manager.
     */
    protected DefaultPersistenceUnitManager createPersistenceUnitManager() {
        DefaultPersistenceUnitManager pum = new DefaultPersistenceUnitManager();
        // IMPORTANT! - setting these to empty String arrays, this triggers the DefaultPersistenceUnitManager to
        // behave appropriately and ignore persistence.xml files from META-INF/persistence.xml as well as allowing for
        // an empty/minimal persistence unit to be created.
        //
        // Note that while Intellij complains about "Redundant array creation for calling varargs method", we really do
        // need to pass an empty array here in order for this code to work properly.
        pum.setPersistenceXmlLocations(new String[0]);
        pum.setMappingResources(new String[0]);
        pum.setPackagesToScan(new String[0]);
        return pum;
    }

    /**
     * Creates a JPA-specific entity manager factory bean.
     *
     * @param manager the persistence unit manager to use.
     * @return a JPA-specific entity manager factory bean.
     */
    protected LocalContainerEntityManagerFactoryBean createInternalFactoryBean(PersistenceUnitManager manager) {
        LocalContainerEntityManagerFactoryBean delegate = new LocalContainerEntityManagerFactoryBean();
        delegate.setPersistenceUnitManager(manager);
        return delegate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterPropertiesSet() throws PersistenceException {
        if (persistenceUnitManager.getDefaultJtaDataSource() != null &&
                persistenceUnitManager.getDefaultDataSource() != null) {
			throw new IllegalStateException(getPersistenceUnitName() + ": " + getClass().getSimpleName()
					+ " was configured with both a JTA and Non-JTA "
                + " datasource. Must configure one or the other, but not both.");
        }

        this.internalFactoryBean.setJpaPropertyMap(defaultAndMergeJpaProperties());
        persistenceUnitManager.setPersistenceUnitPostProcessors(assemblePersistenceUnitPostProcessors());
        persistenceUnitManager.afterPropertiesSet();
        internalFactoryBean.afterPropertiesSet();
    }

    /**
     * Creates default JPA properties.
     *
     * @return a map of default JPA properties.
     */
    private Map<String, ?> createDefaultJpaProperties() {
        Map<String, String> jpaProperties = new HashMap<String, String>();
        loadGlobalJpaDefaults(jpaProperties);
        loadPersistenceUnitJpaDefaults(jpaProperties);
        loadCustomJpaDefaults(jpaProperties);

        return jpaProperties;
    }

    /**
     * Gets the default JPA properties and merges them with the configured JPA properties.
     *
     * @return a map of merged JPA properties.
     */
    private Map<String, ?> defaultAndMergeJpaProperties() {
        Map<String, Object> jpaProperties = new HashMap<String, Object>(createDefaultJpaProperties());
        Map<String, Object> configuredJpaPropertyMap = this.internalFactoryBean.getJpaPropertyMap();
        jpaProperties.putAll(configuredJpaPropertyMap);
		if (LOG.isDebugEnabled()) {
			LOG.debug(getPersistenceUnitName() + ": JPA Properties Set:\n" + jpaProperties);
		}

        return jpaProperties;
    }

    /**
     * Allows for loading of custom JPA defaults by subclasses, default implementation does nothing so subclasses need
     * not call super.loadCustomJpaDefaults.
     *
     * <p>
     * Subclasses are free to override this method as they see fit. This method is executed after other defaults are
     * loaded. A reference to the current Map of JPA properties is passed. Subclasses should take care if removing or
     * overwriting any of the values which already exist in the given Map.
     * </p>
     *
     * @param jpaProperties the current Map of JPA property defaults.
     */
    protected void loadCustomJpaDefaults(Map<String, String> jpaProperties) {
        // subclass can override
    }

    /**
     * Loads the global JPA defaults from the config.
     *
     * @param jpaProperties the current Map of JPA property defaults.
     */
    protected void loadGlobalJpaDefaults(Map<String, String> jpaProperties) {
        jpaProperties.putAll(ConfigContext.getCurrentContextConfig().getPropertiesWithPrefix(GLOBAL_JPA_PROPERTY_PREFIX,
                true));
    }

    /**
     * Loads the persistence unit JPA defaults from the config.
     *
     * @param jpaProperties the current Map of JPA property defaults.
     */
    protected void loadPersistenceUnitJpaDefaults(Map<String, String> jpaProperties) {
        jpaProperties.putAll(ConfigContext.getCurrentContextConfig().getPropertiesWithPrefix(
                constructPersistenceUnitJpaPropertyPrefix(), true));
    }

    /**
     * Builds a persistence unit JPA property prefix.
     * @return a persistence unit JPA property prefix.
     */
    protected String constructPersistenceUnitJpaPropertyPrefix() {
        return JPA_PROPERTY_PREFIX + getPersistenceUnitName() + ".";
    }

    /**
     * Assembles the {@link PersistenceUnitPostProcessor}s into an array.
     *
     * @return an array of the {@link PersistenceUnitPostProcessor}s.
     */
    protected PersistenceUnitPostProcessor[] assemblePersistenceUnitPostProcessors() {
        this.persistenceUnitPostProcessors = new ArrayList<PersistenceUnitPostProcessor>(this.persistenceUnitPostProcessors);
        this.persistenceUnitPostProcessors.add(new InternalPersistenceUnitPostProcessor());
        return this.persistenceUnitPostProcessors.toArray(new PersistenceUnitPostProcessor[this.persistenceUnitPostProcessors.size()]);
    }

    /**
     * Returns the list of all managed class names which have been configured on this factory bean.
     *
     * <p>This list is modifiable, so the returned list may be modified directly if desired.</p>
     *
     * @return list of all managed class names, may be an empty list but will never return null
     */
    public List<String> getManagedClassNames() {
        return managedClassNames;
    }

    /**
     * Returns an array of the {@link PersistenceUnitPostProcessor} instances which have been configured on this
     * factory bean.
     *
     * @return array of post processors, may be empty but will never return null
     */
    public PersistenceUnitPostProcessor[] getPersistenceUnitPostProcessors() {
        return persistenceUnitPostProcessors.toArray(new PersistenceUnitPostProcessor[persistenceUnitPostProcessors.size()]);
    }

    /**
     * Returns a reference to the internal {@link DefaultPersistenceUnitManager} which is used by this factory bean.
     *
     * @return the internal persistence unit manager, will never return null
     */
    protected DefaultPersistenceUnitManager getPersistenceUnitManager() {
        return persistenceUnitManager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy() {
        internalFactoryBean.destroy();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<? extends EntityManagerFactory> getObjectType() {
        return internalFactoryBean.getObjectType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSingleton() {
        return internalFactoryBean.isSingleton();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EntityManagerFactory getObject() {
        return internalFactoryBean.getObject();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EntityManagerFactory getNativeEntityManagerFactory() {
        return internalFactoryBean.getNativeEntityManagerFactory();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBeanName(String name) {
        internalFactoryBean.setBeanName(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        internalFactoryBean.setBeanFactory(beanFactory);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ClassLoader getBeanClassLoader() {
        return internalFactoryBean.getBeanClassLoader();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        internalFactoryBean.setBeanClassLoader(classLoader);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<? extends EntityManager> getEntityManagerInterface() {
        return internalFactoryBean.getEntityManagerInterface();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLoadTimeWeaver(LoadTimeWeaver loadTimeWeaver) {
        persistenceUnitManager.setLoadTimeWeaver(loadTimeWeaver);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        persistenceUnitManager.setResourceLoader(resourceLoader);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PersistenceUnitInfo getPersistenceUnitInfo() {
        return internalFactoryBean.getPersistenceUnitInfo();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPersistenceUnitName() {
        return internalFactoryBean.getPersistenceUnitName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DataSource getDataSource() {
        PersistenceUnitInfo pui = internalFactoryBean.getPersistenceUnitInfo();
        if (internalFactoryBean.getPersistenceUnitInfo() != null) {
            return (pui.getJtaDataSource() != null ?
                    pui.getJtaDataSource() :
                    pui.getNonJtaDataSource());
        }
        return (persistenceUnitManager.getDefaultJtaDataSource() != null ?
                persistenceUnitManager.getDefaultJtaDataSource() :
                this.persistenceUnitManager.getDefaultDataSource());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JpaDialect getJpaDialect() {
        return internalFactoryBean.getJpaDialect();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PersistenceProvider getPersistenceProvider() {
        return internalFactoryBean.getPersistenceProvider();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DataAccessException translateExceptionIfPossible(RuntimeException ex) {
        return internalFactoryBean.translateExceptionIfPossible(ex);
    }

    /**
     * Specifies a List of class names which should be loaded into the resulting EntityManagerFactory as managed
     * JPA classes.
     *
     * @param managedClassNames the List of managed class names to set on this factory bean
     */
    public void setManagedClassNames(List<String> managedClassNames) {
        if (managedClassNames == null) {
            managedClassNames = new ArrayList<String>();
        }
        if (LOG.isInfoEnabled()) {
            LOG.info(getPersistenceUnitName() + ": Setting Managed Class Names JPA:\n" + managedClassNames);
        }
        this.managedClassNames = managedClassNames;
    }

    /**
     * Specify the (potentially vendor-specific) EntityManager interface that this factory's EntityManagers are supposed
     * to implement.
     *
     * <p>
     * The default will be taken from the specific JpaVendorAdapter, if any, or set to the standard
     * {@code EntityManager} interface else.
     * </p>
     *
     * @param emInterface the {@link EntityManager} interface to use
     *
     * @see JpaVendorAdapter#getEntityManagerInterface()
     * @see EntityManagerFactoryInfo#getEntityManagerInterface()
     */
    public void setEntityManagerInterface(Class<? extends EntityManager> emInterface) {
        internalFactoryBean.setEntityManagerInterface(emInterface);
    }

    /**
     * Specify the (potentially vendor-specific) EntityManagerFactory interface that this EntityManagerFactory proxy is
     * supposed to implement.
     *
     * <p>
     * The default will be taken from the specific JpaVendorAdapter, if any, or set to the standard
     * {@code EntityManagerFactory} interface else.
     * </p>
     *
     * @param emfInterface the {@link EntityManagerFactory} interface to use
     *
     * @see JpaVendorAdapter#getEntityManagerFactoryInterface()
     */
    public void setEntityManagerFactoryInterface(Class<? extends EntityManagerFactory> emfInterface) {
        internalFactoryBean.setEntityManagerFactoryInterface(emfInterface);
    }

    /**
     * Allow Map access to the JPA properties to be passed to the persistence provider, with the option to add or
     * override specific entries.
     *
     * <p>Useful for specifying entries directly, for example via "jpaPropertyMap[myKey]".</p>
     *
     * @return the map of JPA properties
     */
    public Map<String, Object> getJpaPropertyMap() {
        return internalFactoryBean.getJpaPropertyMap();
    }

    /**
     * Specify JPA properties as a Map, to be passed into {@code Persistence.createEntityManagerFactory} (if any).
     *
     * <p>Can be populated with a "map" or "props" element in XML bean definitions.</p>
     *
     * @param jpaProperties map of JPA properties to set
     *
     * @see javax.persistence.Persistence#createEntityManagerFactory(String, java.util.Map)
     * @see javax.persistence.spi.PersistenceProvider#createContainerEntityManagerFactory(javax.persistence.spi.PersistenceUnitInfo, java.util.Map)
     */
    public void setJpaPropertyMap(Map<String, ?> jpaProperties) {
        internalFactoryBean.setJpaPropertyMap(jpaProperties);
    }

    /**
     * Specify JPA properties, to be passed into {@code Persistence.createEntityManagerFactory} (if any).
     *
     * <p>Can be populated with a String "value" (parsed via PropertiesEditor) or a "props" element in XML bean definitions.</p>
     *
     * @param jpaProperties the properties to set
     *
     * @see javax.persistence.Persistence#createEntityManagerFactory(String, java.util.Map)
     * @see javax.persistence.spi.PersistenceProvider#createContainerEntityManagerFactory(javax.persistence.spi.PersistenceUnitInfo, java.util.Map)
     */
    public void setJpaProperties(Properties jpaProperties) {
        internalFactoryBean.setJpaProperties(jpaProperties);
    }

    /**
     * Specify the name of the EntityManagerFactory configuration.
     *
     * <p>
     * Default is none, indicating the default EntityManagerFactory configuration. The persistence provider will throw
     * an exception if ambiguous EntityManager configurations are found.
     * </p>
     *
     * @param persistenceUnitName the name of the persistence unit
     *
     * @see javax.persistence.Persistence#createEntityManagerFactory(String)
     */
    public void setPersistenceUnitName(String persistenceUnitName) {
        internalFactoryBean.setPersistenceUnitName(persistenceUnitName);
        persistenceUnitManager.setDefaultPersistenceUnitName(persistenceUnitName);
    }

    /**
     * Set whether to use Spring-based scanning for entity classes in the classpath instead of using JPA's standard
     * scanning of jar files with {@code persistence.xml} markers in them.
     *
     * <p>
     * In case of Spring-based scanning, no {@code persistence.xml} is necessary; all you need to do is to specify base
     * packages to search here.
     * </p>
     *
     * <p>
     * Default is none. Specify packages to search for autodetection of your entity classes in the classpath. This is
     * analogous to Spring's component-scan feature
     * ({@link org.springframework.context.annotation.ClassPathBeanDefinitionScanner}).
     * </p>
     *
     * @param packagesToScan one or more base packages to search, analogous to Spring's component-scan configuration for
     *        regular Spring components
     *
     * @see {@link DefaultPersistenceUnitManager#setPackagesToScan(String...)}
     */
    public void setPackagesToScan(String... packagesToScan) {
        if (LOG.isInfoEnabled()) {
            LOG.info(getPersistenceUnitName() + ": Setting Packages to Scan for JPA Annotations:\n"
                + Arrays.deepToString(packagesToScan));
        }
        persistenceUnitManager.setPackagesToScan(packagesToScan);
		converterPackageNames = Arrays.asList(packagesToScan);
    }

    /**
     * Specify one or more mapping resources (equivalent to {@code &lt;mapping-file&gt;} entries in
     * {@code persistence.xml}) for the default persistence unit.
     *
     * <p>
     * Can be used on its own or in combination with entity scanning in the classpath, in both cases avoiding
     * {@code persistence.xml}.
     * </p>
     *
     * <p>
     * Note that mapping resources must be relative to the classpath root, e.g. "META-INF/mappings.xml" or
     * "com/mycompany/repository/mappings.xml", so that they can be loaded through {@code ClassLoader.getResource}.
     * </p>
     *
     * @param mappingResources one or more mapping resources to use
     *
     * @see {@link DefaultPersistenceUnitManager#setMappingResources(String...)}
     */
    public void setMappingResources(String... mappingResources) {
        persistenceUnitManager.setMappingResources(mappingResources);
    }

    /**
     * Specify the JDBC DataSource that the JPA persistence provider is supposed to use for accessing the database.
     *
     * <p>
     * This is an alternative to keeping the JDBC configuration in {@code persistence.xml}, passing in a Spring-managed
     * DataSource instead.
     * </p>
     *
     * <p>
     * In JPA speak, a DataSource passed in here will be used as "nonJtaDataSource" on the PersistenceUnitInfo passed
     * to the PersistenceProvider, as well as overriding data source configuration in {@code persistence.xml} (if any).
     * Note that this variant typically works for JTA transaction management as well; if it does not, consider using the
     * explicit {@link #setJtaDataSource} instead.
     * </p>
     *
     * @param dataSource the DataSource to use for this EntityManagerFactory
     *
     * @see javax.persistence.spi.PersistenceUnitInfo#getNonJtaDataSource()
     * @see {@link DefaultPersistenceUnitManager#setDefaultDataSource(javax.sql.DataSource)}
     */
    public void setDataSource(DataSource dataSource) {
        persistenceUnitManager.setDefaultDataSource(dataSource);
    }

    /**
     * Specify the JDBC DataSource that the JPA persistence provider is supposed to use for accessing the database.
     *
     * <p>
     * This is an alternative to keeping the JDBC configuration in {@code persistence.xml}, passing in a Spring-managed
     * DataSource instead.
     * </p>
     *
     * <p>
     * In JPA speak, a DataSource passed in here will be used as "jtaDataSource" on the PersistenceUnitInfo passed to
     * the PersistenceProvider, as well as overriding data source configuration in {@code persistence.xml} (if any).
     * </p>
     *
     * @param jtaDataSource the JTA-enabled DataSource to use for this EntityManagerFactory
     *
     * @see javax.persistence.spi.PersistenceUnitInfo#getJtaDataSource()
     * @see {@link DefaultPersistenceUnitManager#setDefaultJtaDataSource(javax.sql.DataSource)}
     */
    public void setJtaDataSource(DataSource jtaDataSource) {
        persistenceUnitManager.setDefaultJtaDataSource(jtaDataSource);
    }

    /**
     * Set the PersistenceProvider instance to use for creating the EntityManagerFactory.
     *
     * <p>
     * If not specified, the persistence provider will be taken from the JpaVendorAdapter (if any) or determined by the
     * persistence unit deployment descriptor (as far as possible).
     * </p>
     *
     * @param persistenceProvider the PersistenceProvider to set
     *
     * @see JpaVendorAdapter#getPersistenceProvider()
     * @see javax.persistence.spi.PersistenceProvider
     * @see javax.persistence.Persistence
     */
    public void setPersistenceProvider(PersistenceProvider persistenceProvider) {
        this.internalFactoryBean.setPersistenceProvider(persistenceProvider);
    }

    /**
     * Specify the vendor-specific JpaDialect implementation to associate with this EntityManagerFactory.
     *
     * <p>
     * This will be exposed through the EntityManagerFactoryInfo interface, to be picked up as default dialect by
     * accessors that intend to use JpaDialect functionality.
     * </p>
     *
     * @param jpaDialect the JPA dialect to set
     *
     * @see EntityManagerFactoryInfo#getJpaDialect()
     */
    public void setJpaDialect(JpaDialect jpaDialect) {
        this.internalFactoryBean.setJpaDialect(jpaDialect);
    }

    /**
     * Specify the JpaVendorAdapter implementation for the desired JPA provider, if any.
     *
     * <p>
     * This will initialize appropriate defaults for the given provider, such as persistence provider class and
     * JpaDialect, unless locally overridden in this FactoryBean.
     * </p>
     *
     * @param jpaVendorAdapter the JpaVendorAdapter to set
     */
    public void setJpaVendorAdapter(JpaVendorAdapter jpaVendorAdapter) {
        this.internalFactoryBean.setJpaVendorAdapter(jpaVendorAdapter);
    }

    /**
     * Set the PersistenceUnitPostProcessors to be applied to the * PersistenceUnitInfo used for creating this
     * EntityManagerFactory.
     *
     * <p>
     * Note that if executed before {@link #afterPropertiesSet()} then this factory bean may introduce its own post
     * processor instances. If invoked after, then this method will override internally configured post processors.
     * </p>
     *
     * <p>
     * Such post-processors can, for example, register further entity classes and jar files, in addition to the metadata
     * read from {@code persistence.xml}.
     * </p>
     *
     * @param postProcessors one or more post processors to set
     */
    public void setPersistenceUnitPostProcessors(PersistenceUnitPostProcessor... postProcessors) {
        // persistence unit post processors will get applied to the internal factory bean during afterPropertiesSet(),
        // this allows us to add our own internal post processor to the list)
        this.persistenceUnitPostProcessors = new ArrayList<PersistenceUnitPostProcessor>(Arrays.asList(postProcessors));
    }

    /**
     * A {@link PersistenceUnitPostProcessor} to handle {@link Converter} annotations.
     */
    private final class InternalPersistenceUnitPostProcessor implements PersistenceUnitPostProcessor {

		private final TypeFilter converterAnnotationTypeFilter = new AnnotationTypeFilter(Converter.class);
		private ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
		private static final String ENTITY_CLASS_RESOURCE_PATTERN = "/**/*.class";

        /**
         * {@inheritDoc}
         */
        @Override
        public void postProcessPersistenceUnitInfo(MutablePersistenceUnitInfo pui) {
            pui.setExcludeUnlistedClasses(DEFAULT_EXCLUDE_UNLISTED_CLASSES);
			processConverterPackages(pui);
            for (String managedClassName : getManagedClassNames()) {
                pui.addManagedClassName(managedClassName);
            }
        }

        /**
         * Determines whether the managed classes contain {@link Converter} annotations and adds them if necessary.
         *
         * @param pui the list of current list of managed classes.
         */
		private void processConverterPackages(MutablePersistenceUnitInfo pui) {
			if (converterPackageNames != null) {
				for (String converterPackage : converterPackageNames) {
					// Code below lifted and modified from Spring's DefaultPersistenceUnitManager
					try {
						String pattern = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
								+ ClassUtils.convertClassNameToResourcePath(converterPackage)
								+ ENTITY_CLASS_RESOURCE_PATTERN;
						if (LOG.isInfoEnabled()) {
							LOG.info(getPersistenceUnitName() + ": Scanning for JPA @Converter annotations in: "
									+ pattern);
						}
						Resource[] resources = this.resourcePatternResolver.getResources(pattern);
						MetadataReaderFactory readerFactory = new CachingMetadataReaderFactory(
								this.resourcePatternResolver);
						for (Resource resource : resources) {
							if (!resource.isReadable()) {
								continue;
							}
							if (LOG.isDebugEnabled()) {
								LOG.debug(getPersistenceUnitName() + ": Found Matching Resource: " + resource);
							}
							MetadataReader reader = readerFactory.getMetadataReader(resource);
							String className = reader.getClassMetadata().getClassName();
							if (!pui.getManagedClassNames().contains(className)
									&& converterAnnotationTypeFilter.match(reader, readerFactory)) {
								pui.addManagedClassName(className);
								if (LOG.isDebugEnabled()) {
									LOG.debug(getPersistenceUnitName()
											+ ": Registering Converter in JPA Persistence Unit: " + className);
								}
							}
						}
					} catch (IOException ex) {
						throw new PersistenceException("Failed to scan classpath converters in package: "
								+ converterPackage, ex);
					}
				}
			}
		}
    }
}
