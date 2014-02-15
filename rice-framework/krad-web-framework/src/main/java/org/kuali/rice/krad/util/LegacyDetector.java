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
package org.kuali.rice.krad.util;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.framework.util.ReflectionUtils;
import org.kuali.rice.krad.data.metadata.MetadataRepository;
import org.kuali.rice.krad.datadictionary.BusinessObjectEntry;
import org.kuali.rice.krad.service.DataDictionaryService;
import org.springframework.util.ClassUtils;

import com.google.common.collect.Lists;

/**
 * Utility class which is used to determine whether the given object or class has been configured in the "legacy" KRAD/KNS
 * implementation, or for the new KRAD-Data layer.
 *
 * <ol>
 *     <li>The configuration parameter "rice.krad.enableLegacyDataFramework" governs this determination. It is false unless the KNSConfigurer is loaded in which case it will be defaulted to true.
 *         <ul>
 *             <li>This indicates that if a given business/data object exists in both the old and new data layers that it will use the old one. This is important for classes like DocumentHeader and the other KRAD classes because they are mapped and loaded into both.</li>
 *             <li>KRAD appliations which are not using legacy KNS (and therefore not loading the KNSConfigurer) but still using the KRAD data layer as it exists today, need to manually set this to true in order for their applications to load properly.</li>
 *             <li>If it's set to false then OJB metadata won't even be loaded for KRAD objects like DocumentHeader, Notes, Attachments, etc.</li>
 *         </ul>
 *     </li>
 *     <li>The logic for determining whether to an object should be handled by the legacy or new data layers is as follows:
 *         <ul>
 *             <li>If rice.krad.enableLegacyDataFramework is false, use new framework (DataObjectService)</li>
 *             <li>If rice.krad.enableLegacyDataFramework is true, check if object/class is in OJB metadata loaded by legacy framework:
 *                 <ul>
 *                     <li>if it is, use legacy framework (BusinessObjectService, etc.)</li>
 *                     <li>if not, use new framework (DataObjectService)</li>
 *                 </ul>
 *             </li>
 *             <li>In order to check if an object/class is in OJB metadata that was loaded by the legacy framework:</li>
 *                 <ul>
 *                     <li>Check for OJB MetadataManager on classpath using reflection to avoid compile-time dependency</li>
 *                     <li>This may mean we need to enhance something like ModuleConfiguration so that when it loads the given OJB files via the OjbConfigurer that it keeps track of entities loaded this way (to distinguish those laoded by the legacy framework vs. those loaded by the OJB PersistenceProvider)</li>
 *                 </ul>
 *             </li>
 *         </ul>
 *     </li>
 *     <li>If rice.krad.enableLegacyDataFramework is false, then calls to deprecated legacy data services (such as BusinessObjectService) should throw an exception indicating that legacy data framework support is disabled and rice.krad.enableLegacyDataFramework must be set to true in order to use these services</li>
 * </ol>
 */
class LegacyDetector {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LegacyDetector.class);
    /**
     * The "legacy" (OJB) metadata provider class
     */
    private static final String OJB_METADATA_MANAGER_CLASS = "org.apache.ojb.broker.metadata.MetadataManager";

    private final MetadataRepository metadataRepository;
    private final DataDictionaryService dataDictionaryService;

    private final Map<Class<?>, Boolean> legacyLoadedCache = new ConcurrentHashMap<Class<?>, Boolean>();

    private static ThreadLocal<Integer> legacyContext = new ThreadLocal<Integer>();

    LegacyDetector(MetadataRepository metadataRepository, DataDictionaryService dataDictionaryService) {
        Validate.notNull(metadataRepository, "The metadataRepository must not be null");
        Validate.notNull(dataDictionaryService, "The dataDictionaryService must not be null");
        this.metadataRepository = metadataRepository;
        this.dataDictionaryService = dataDictionaryService;
    }

    public boolean isInLegacyContext() {
        return legacyContext.get() != null;
    }

    public void beginLegacyContext() {
        Integer count = legacyContext.get();
        if (count == null) {
            // if attempting to establish legacy context, the legacy data framework must be enabled
            if (!isLegacyDataFrameworkEnabled()) {
                throw new IllegalStateException(
                        "Attempting to enter legacy data context without the legacy data framework enabled."
                                + " To enable, please load the KNS module or set "
                                + KRADConstants.Config.ENABLE_LEGACY_DATA_FRAMEWORK
                                + " config property to 'true'.");
            }
            legacyContext.set(new Integer(0));
        } else {
            legacyContext.set(new Integer(count.intValue() + 1));
        }
    }

    public void endLegacyContext() {
        Integer count = legacyContext.get();
        if (count == null) {
            throw new IllegalStateException("Attempting to end a non-existent legacy context!");
        } else if (count.intValue() == 0) {
            legacyContext.set(null);
        } else {
            legacyContext.set(new Integer(count.intValue() - 1));
        }
    }

    public boolean isLegacyManaged(Class<?> type) {
        BusinessObjectEntry businessObjectEntry =
                dataDictionaryService.getDataDictionary().getBusinessObjectEntry(type.getName());
        return businessObjectEntry != null || isOjbLoadedClass(type);
    }

    /**
     * A type is considered krad-data managed if it is included in the metadata repository.
     * @param type data type
     * @return true if the type is krad-data managed
     */
    public boolean isKradDataManaged(Class<?> type) {
        return metadataRepository.contains(type);
    }

    /**
     * Returns whether or not the KNS module of Rice has been loaded. This is based on whether or not the KNS_ENABLED
     * flag has been set in the Config by the KRADConfigurer.
     *
     * @return true if the KNS module has been loaded, false otherwsie
     */
    public boolean isKnsEnabled() {
        return ConfigContext.getCurrentContextConfig().getBooleanProperty(KRADConstants.Config.KNS_ENABLED, false);
    }

    /**
     * Return whether the legacy data framework is enabled.
     *
     * @return true if the legacy data framework has been enabled, false otherwise
     */
    public boolean isLegacyDataFrameworkEnabled() {
        return ConfigContext.getCurrentContextConfig().getBooleanProperty(KRADConstants.Config.ENABLE_LEGACY_DATA_FRAMEWORK, isKnsEnabled());
    }

    /**
     * Determines whether the given class is loaded into OJB. Accesses the OJB metadata manager via reflection to avoid
     * compile-time dependency. If null is passed to this method, it will always return false.
     *
     * @param dataObjectClass the data object class which may be loaded by the legacy framework
     * @return true if the legacy data framework is present and has loaded the specified class, false otherwise
     */
    public boolean isOjbLoadedClass(Class<?> dataObjectClass) {
        if (dataObjectClass == null) {
            return false;
        }
        // some OJB objects may come in as proxies, we need to clear the CGLIB portion of the generated class name
        // before we can check it properly
        String dataObjectClassName = dataObjectClass.getName().replaceAll("\\$\\$EnhancerByCGLIB\\$\\$[0-9a-f]{0,8}", "" );
        try {
            dataObjectClass = Class.forName(dataObjectClassName);
        } catch (ClassNotFoundException ex) {
            LOG.warn( "Unable to resolve converted class name: " + dataObjectClassName + " from original: " + dataObjectClass + " -- Using as is" );
        }
        Boolean isLegacyLoaded = legacyLoadedCache.get(dataObjectClass);
        if (isLegacyLoaded == null) {
            if ( dataObjectClass.getPackage() != null
                    && StringUtils.startsWith( dataObjectClass.getPackage().getName(), "org.apache.ojb." ) ) {
                isLegacyLoaded = Boolean.TRUE;
            } else {
                try {
                    Class<?> metadataManager = Class.forName(OJB_METADATA_MANAGER_CLASS, false, ClassUtils.getDefaultClassLoader());

                    // determine, via reflection, whether the legacy persistence layer has loaded the given class
                    Object metadataManagerInstance = ReflectionUtils.invokeViaReflection(metadataManager, (Object) null, "getInstance", null);
                    Validate.notNull(metadataManagerInstance , "unable to obtain " + OJB_METADATA_MANAGER_CLASS + " instance");

                    Object descriptorRepository = ReflectionUtils.invokeViaReflection(metadataManagerInstance, "getGlobalRepository", null);
                    Validate.notNull(descriptorRepository, "unable to invoke legacy metadata provider (" + OJB_METADATA_MANAGER_CLASS + ")");

                    isLegacyLoaded = ReflectionUtils.invokeViaReflection(descriptorRepository, "hasDescriptorFor", new Class[] { Class.class }, dataObjectClass);

                } catch (ClassNotFoundException e) {
                    // the legacy provider does not exist, so this class can't possibly have been loaded through it
                    isLegacyLoaded = Boolean.FALSE;
                }
            }
            legacyLoadedCache.put(dataObjectClass, isLegacyLoaded);
        }
        return isLegacyLoaded.booleanValue();
    }

    /**
     * Return whether objects of the given class should be handled via the legacy data framework
     * @param dataObjectClass the data object class
     * @return whether objects of the given class should be handled via the legacy data framework
     */
    public boolean useLegacy(Class<?> dataObjectClass) {
        // if we are in a legacy context, always use the legacy framework, if they are using stuff that's not mapped
        // up properly then they are doing it wrong
        boolean ojbLoadedClass = isOjbLoadedClass(dataObjectClass);
        if (isInLegacyContext() && ojbLoadedClass) {
            return true;
        }
        // if it's only loaded in legacy, then we can indicate to use the legacy framework
        //ADDED hack to handle classes like PersonImpl that are not in OJB but are Legacy and should
        //goto that adapter
        if (isLegacyDataFrameworkEnabled() &&
                (ojbLoadedClass || isTransientBO(dataObjectClass)) &&
                !isKradDataManaged(dataObjectClass)) {
            return true;
        }
        // default to non-legacy when in a non-legacy context
        return false;
    }

    /**
     * Confirm if this is a BO that is not mapped by OJB but should be handled by the Legacy Adapter.
     *
     * @param dataObjectClass the data object class
     *
     * @return true if {@code dataObjectClass} should be handled by the Legacy Adapter, false otherwise
     */
    private boolean isTransientBO(Class dataObjectClass) {
        boolean isTransientBo = false;

        List<String> transientClassNames = Lists.newArrayList(
                "org.kuali.rice.krad.bo.TransientBusinessObjectBase");

        try {
            Object dataObject = dataObjectClass.newInstance();

            for (String transientClassName : transientClassNames) {
                Class<?> transientClass = Class.forName(transientClassName);

                if (transientClass.isInstance(dataObject)) {
                    isTransientBo = true;
                    break;
                }
            }
        } catch (Exception e) {
            return false;
        }

        return isTransientBo;
    }

    /**
     * Return whether the object should be handled via the legacy data framework
     * @param dataObject the data object
     * @return whether the object should be handled via the legacy data framework
     */
    public boolean useLegacyForObject(Object dataObject) {
        Validate.notNull(dataObject, "Data Object must not be null");
        if (dataObject instanceof Class) {
            throw new IllegalArgumentException("Passed a Class object to useLegacyForObject, call useLegacy instead!");
        }
        return useLegacy(dataObject.getClass());
    }
}