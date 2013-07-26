/*
 * Copyright 2006-2013 The Kuali Foundation
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

package org.kuali.rice.krad.data.provider.jpa;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.persistence.Converter;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;
import javax.persistence.spi.PersistenceProvider;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.sql.DataSource;

import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.krad.data.converters.BooleanYNConverter;
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

/**
 * A KRAD-managed {@link javax.persistence.EntityManagerFactory} factory bean which can be used to configure a JPA
 * persistence unit using the Spring Framework. This implementation does not support the use of a custom
 * PersistenceUnitManager, but rather stores and manages one internally. This is intended to be an alternative to direct
 * usage of Spring's {@link LocalContainerEntityManagerFactoryBean} in order to make JPA configuration with KRAD
 * simpler.
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
 * <p></p>Note that persistence unit names must be unique, so choose a name which is unlikely to clash
 * with any potential persistence units configured within the runtime environment.</p>
 *
 * <h1>Behavior</h1>
 *
 * <p>When leveraging this class, persistence.xml files are not used. Rather, persistence unit configuration is loaded
 * via the various settings provided on this factory bean which contain everything needed to create the desired
 * persistence unit configuration in the majority of cases. If a KRAD application needs more control over the
 * configuration of the persistence unit or wants to use persistence.xml and JPA's default bootstrapping and classpath
 * scanning behavior, an EntityManagerFactory can be configured using the other classes provided by the Spring
 * framework or by other means as needed. See {@link LocalContainerEntityManagerFactoryBean} for an alternative
 * approach.</p>
 *
 * <p>Only one of JTA or non-JTA datasource can be set. Depending on which one is set, the underlying persistence unit
 * will have it's {@link javax.persistence.spi.PersistenceUnitTransactionType} set to either RESOURCE_LOCAL or JTA. If
 * both of these are set, then this class will throw an {@code IllegalStateException} when the
 * {@link #afterPropertiesSet()} method is invoked by the Spring Framework.</p>
 *
 * <p>Elsewhere, this class delegates to implementations of {@link LocalContainerEntityManagerFactoryBean} and
 * {@link DefaultPersistenceUnitManager}, so information on the specific behavior of some of the settings and methods on
 * this class can be found on the javadoc for those classes as well.</p>
 *
 * <h1>JPA Property Defaults</h1>
 *
 * <p>When {@link #afterPropertiesSet()} is invoked, this class will scan the current {@link ConfigContext} for JPA
 * properties and make them available to the persistence unit. It will combine these with any properties that were set
 * directly on this factory bean via the {@link #setJpaProperties(java.util.Properties)} or
 * {@link #setJpaPropertyMap(java.util.Map)} methods.</p
 *
 * <p>This scanning occurs in the following order, items later in the list will override any properties from earlier if
 * they happen to set the same effective property value:</p>
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
 * This class can be subclassed to provide additional specialized implementations of this factory bean. A potential use
 * for this might be to provide a factory bean that defaults certain values as part of it's default setup.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class KradEntityManagerFactoryBean implements FactoryBean<EntityManagerFactory>, BeanClassLoaderAware,
        BeanFactoryAware, BeanNameAware, InitializingBean, DisposableBean, EntityManagerFactoryInfo,
        PersistenceExceptionTranslator, ResourceLoaderAware, LoadTimeWeaverAware {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
			.getLogger(KradEntityManagerFactoryBean.class);

    private static final boolean DEFAULT_EXCLUDE_UNLISTED_CLASSES = true;
    private static final String JPA_PROPERTY_PREFIX = "rice.krad.jpa.";
    private static final String GLOBAL_JPA_PROPERTY_PREFIX = JPA_PROPERTY_PREFIX + "global.";
    private static final String DEFAULT_CONVERTERS_PACKAGE = BooleanYNConverter.class.getPackage().getName();

    private final DefaultPersistenceUnitManager persistenceUnitManager;
    private final LocalContainerEntityManagerFactoryBean internalFactoryBean;

    private List<PersistenceUnitPostProcessor> persistenceUnitPostProcessors;
    private List<String> managedClassNames;
	private List<String> converterPackageNames;

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
     * bean. Primarily intended to allow subclasses to access this internal factory bean when needed.
     *
     * @return the internal {@link LocalContainerEntityManagerFactoryBean} managed by this bean
     */
    protected LocalContainerEntityManagerFactoryBean getInternalFactoryBean() {
        return this.internalFactoryBean;
    }

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

    protected LocalContainerEntityManagerFactoryBean createInternalFactoryBean(PersistenceUnitManager manager) {
        LocalContainerEntityManagerFactoryBean delegate = new LocalContainerEntityManagerFactoryBean();
        delegate.setPersistenceUnitManager(manager);
        return delegate;
    }

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

    private Map<String, ?> createDefaultJpaProperties() {
        Map<String, String> jpaProperties = new HashMap<String, String>();
        loadGlobalJpaDefaults(jpaProperties);
        loadPersistenceUnitJpaDefaults(jpaProperties);
        loadCustomJpaDefaults(jpaProperties);
        return jpaProperties;
    }

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
     * not call super.loadCustomJpaDefaults. Subclasses are free to override this method as they see fit. This method is
     * executed after other defaults are loaded. A reference to the current Map of JPA properties is passed. Subclasses
     * should take care if removing or overwriting any of the values which already exist in the given Map.
     *
     * @param jpaProperties the current Map of JPA property defaults
     */
    protected void loadCustomJpaDefaults(Map<String, String> jpaProperties) {
        // subclass can override
    }

    protected void loadGlobalJpaDefaults(Map<String, String> jpaProperties) {
        jpaProperties.putAll(ConfigContext.getCurrentContextConfig().getPropertiesWithPrefix(GLOBAL_JPA_PROPERTY_PREFIX,
                true));
    }

    protected void loadPersistenceUnitJpaDefaults(Map<String, String> jpaProperties) {
        jpaProperties.putAll(ConfigContext.getCurrentContextConfig().getPropertiesWithPrefix(
                constructPersistenceUnitJpaPropertyPrefix(), true));
    }

    protected String constructPersistenceUnitJpaPropertyPrefix() {
        return GLOBAL_JPA_PROPERTY_PREFIX + getPersistenceUnitName() + ".";
    }

    protected PersistenceUnitPostProcessor[] assemblePersistenceUnitPostProcessors() {
        this.persistenceUnitPostProcessors = new ArrayList<PersistenceUnitPostProcessor>(this.persistenceUnitPostProcessors);
        this.persistenceUnitPostProcessors.add(new InternalPersistenceUnitPostProcessor());
        return this.persistenceUnitPostProcessors.toArray(new PersistenceUnitPostProcessor[this.persistenceUnitPostProcessors.size()]);
    }

    public List<String> getManagedClassNames() {
        return managedClassNames;
    }

    public PersistenceUnitPostProcessor[] getPersistenceUnitPostProcessors() {
        return persistenceUnitPostProcessors.toArray(new PersistenceUnitPostProcessor[persistenceUnitPostProcessors.size()]);
    }

    protected DefaultPersistenceUnitManager getPersistenceUnitManager() {
        return persistenceUnitManager;
    }

    @Override
    public void destroy() {
        internalFactoryBean.destroy();
    }

    @Override
    public Class<? extends EntityManagerFactory> getObjectType() {
        return internalFactoryBean.getObjectType();
    }

    @Override
    public boolean isSingleton() {
        return internalFactoryBean.isSingleton();
    }

    @Override
    public EntityManagerFactory getObject() {
        return internalFactoryBean.getObject();
    }

    @Override
    public EntityManagerFactory getNativeEntityManagerFactory() {
        return internalFactoryBean.getNativeEntityManagerFactory();
    }

    @Override
    public void setBeanName(String name) {
        internalFactoryBean.setBeanName(name);
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        internalFactoryBean.setBeanFactory(beanFactory);
    }

    @Override
    public ClassLoader getBeanClassLoader() {
        return internalFactoryBean.getBeanClassLoader();
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        internalFactoryBean.setBeanClassLoader(classLoader);
    }

    @Override
    public Class<? extends EntityManager> getEntityManagerInterface() {
        return internalFactoryBean.getEntityManagerInterface();
    }

    @Override
    public void setLoadTimeWeaver(LoadTimeWeaver loadTimeWeaver) {
        persistenceUnitManager.setLoadTimeWeaver(loadTimeWeaver);
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        persistenceUnitManager.setResourceLoader(resourceLoader);
    }

    @Override
    public PersistenceUnitInfo getPersistenceUnitInfo() {
        return internalFactoryBean.getPersistenceUnitInfo();
    }

    @Override
    public String getPersistenceUnitName() {
        return internalFactoryBean.getPersistenceUnitName();
    }

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

    @Override
    public JpaDialect getJpaDialect() {
        return internalFactoryBean.getJpaDialect();
    }

    @Override
    public PersistenceProvider getPersistenceProvider() {
        return internalFactoryBean.getPersistenceProvider();
    }

    @Override
    public DataAccessException translateExceptionIfPossible(RuntimeException ex) {
        return internalFactoryBean.translateExceptionIfPossible(ex);
    }

    public void setManagedClassNames(List<String> managedClassNames) {
		if (LOG.isInfoEnabled()) {
			LOG.info(getPersistenceUnitName() + ": Setting Managed Class Names JPA:\n" + managedClassNames);
		}
        this.managedClassNames = managedClassNames;
    }

    public void setEntityManagerInterface(Class<? extends EntityManager> emInterface) {
        internalFactoryBean.setEntityManagerInterface(emInterface);
    }

    public void setEntityManagerFactoryInterface(Class<? extends EntityManagerFactory> emfInterface) {
        internalFactoryBean.setEntityManagerFactoryInterface(emfInterface);
    }

    public Map<String, Object> getJpaPropertyMap() {
        return internalFactoryBean.getJpaPropertyMap();
    }

    public void setJpaPropertyMap(Map<String, ?> jpaProperties) {
        internalFactoryBean.setJpaPropertyMap(jpaProperties);
    }

    public void setJpaProperties(Properties jpaProperties) {
        internalFactoryBean.setJpaProperties(jpaProperties);
    }

    public void setPersistenceUnitName(String persistenceUnitName) {
        internalFactoryBean.setPersistenceUnitName(persistenceUnitName);
        persistenceUnitManager.setDefaultPersistenceUnitName(persistenceUnitName);
    }
    public void setPackagesToScan(String... packagesToScan) {
		if (LOG.isInfoEnabled()) {
			LOG.info(getPersistenceUnitName() + ": Setting Packages to Scan for JPA Annotations:\n"
					+ Arrays.deepToString(packagesToScan));
		}
        persistenceUnitManager.setPackagesToScan(packagesToScan);
		converterPackageNames = Arrays.asList(packagesToScan);
    }

    public void setMappingResources(String... mappingResources) {
        persistenceUnitManager.setMappingResources(mappingResources);
    }

    public void setDataSource(DataSource dataSource) {
        persistenceUnitManager.setDefaultDataSource(dataSource);
    }

    public void setJtaDataSource(DataSource jtaDataSource) {
        persistenceUnitManager.setDefaultJtaDataSource(jtaDataSource);
    }

    public void setPersistenceProvider(PersistenceProvider persistenceProvider) {
        this.internalFactoryBean.setPersistenceProvider(persistenceProvider);
    }

    public void setJpaDialect(JpaDialect jpaDialect) {
        this.internalFactoryBean.setJpaDialect(jpaDialect);
    }

    public void setJpaVendorAdapter(JpaVendorAdapter jpaVendorAdapter) {
        this.internalFactoryBean.setJpaVendorAdapter(jpaVendorAdapter);
    }

    public void setPersistenceUnitPostProcessors(PersistenceUnitPostProcessor... postProcessors) {
        // persistence unit post processors will get applied to the internal factory bean during afterPropertiesSet(),
        // this allows us to add our own internal post processor to the list)
        this.persistenceUnitPostProcessors = new ArrayList<PersistenceUnitPostProcessor>(Arrays.asList(postProcessors));
    }

    private final class InternalPersistenceUnitPostProcessor implements PersistenceUnitPostProcessor {

		private final TypeFilter converterAnnotationTypeFilter = new AnnotationTypeFilter(Converter.class);
		private ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
		private static final String ENTITY_CLASS_RESOURCE_PATTERN = "/**/*.class";

        @Override
        public void postProcessPersistenceUnitInfo(MutablePersistenceUnitInfo pui) {
            pui.setExcludeUnlistedClasses(DEFAULT_EXCLUDE_UNLISTED_CLASSES);
			processConverterPackages(pui);
            List<String> managedClassNames = getManagedClassNames();
            if (managedClassNames != null) {
                for (String managedClassName : managedClassNames) {
                    pui.addManagedClassName(managedClassName);
                }
            }
        }

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
							if (resource.isReadable()) {
								if (LOG.isDebugEnabled()) {
									LOG.debug(getPersistenceUnitName() + ": Found Matching Resource: " + resource);
								}
								MetadataReader reader = readerFactory.getMetadataReader(resource);
								String className = reader.getClassMetadata().getClassName();
								if (!pui.getManagedClassNames().contains(className)) {
									if (converterAnnotationTypeFilter.match(reader, readerFactory)) {
										pui.addManagedClassName(className);
										if (LOG.isDebugEnabled()) {
											LOG.debug(getPersistenceUnitName()
													+ ": Registering Converter in JPA Persistence Unit: " + className);
										}
									}
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
