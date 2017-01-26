/**
 * Copyright 2005-2017 The Kuali Foundation
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
package org.kuali.rice.kew.framework.validation

import groovy.mock.interceptor.MockFor
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.kuali.rice.core.api.config.property.Config
import org.kuali.rice.core.api.config.property.ConfigContext
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader
import org.kuali.rice.core.api.util.RiceConstants
import org.kuali.rice.core.framework.resourceloader.BaseResourceLoader
import org.kuali.rice.kew.api.rule.Rule
import org.kuali.rice.kew.api.validation.RuleValidationContext
import org.kuali.rice.kew.api.validation.ValidationResults
import org.kuali.rice.kew.impl.extension.ExtensionRepositoryServiceImpl
import org.kuali.rice.kew.rule.RuleValidationAttribute
import org.kuali.rice.kew.rule.bo.RuleAttribute
import org.kuali.rice.kew.rule.service.RuleAttributeService
import org.kuali.rice.kew.validation.RuleValidationAttributeExporterServiceImpl
import org.kuali.rice.kew.validation.RuleValidationAttributeResolver
import org.kuali.rice.kew.validation.RuleValidationAttributeResolverImpl

import javax.xml.namespace.QName
/**
 * Unit test for RuleValidationAttributeExporterService
 */
public class RuleValidationAttributeServicesTest {
    public static class TestRuleValidationAttribute implements RuleValidationAttribute {
        public ValidationResults validate(RuleValidationContext validationContext) {
            return ValidationResults.Builder.create().build();
        }
    }

    static final def MOCK_ATTR_NAME = "mock_attr_name"
    static final def MOCK_APP_ID = "mock_app_id"
    private RuleValidationAttributeExporterService exporter
    private RuleValidationAttributeResolver resolver= new RuleValidationAttributeResolverImpl() {
        protected RuleValidationAttributeExporterService findRuleValidationAttributeExporterService(String applicationId) {
            return exporter;
        }
    }

    private static void mockTheConfig() {
        def mock_config = [getProperty:{ MOCK_APP_ID }] as Config
        ConfigContext.init(mock_config);
    }

    private static void mockTheResourceLoader() {
        mockTheConfig()
        GlobalResourceLoader.stop()
        GlobalResourceLoader.addResourceLoader(new BaseResourceLoader(new QName(MOCK_APP_ID, RiceConstants.DEFAULT_ROOT_RESOURCE_LOADER_NAME)))
        GlobalResourceLoader.start()
    }

    @Before
    void init() {
        mockTheResourceLoader()

        def mock_attr = new RuleAttribute()
        // ExtensionDefinition requires: name, type, classname
        mock_attr.name = MOCK_ATTR_NAME
        mock_attr.type = "RuleAttribute"
        mock_attr.resourceDescriptor = TestRuleValidationAttribute.class.name

        def attr_svc = new MockFor(RuleAttributeService)
        attr_svc.ignore.findByName { name -> mock_attr }

        def extension_repo = new ExtensionRepositoryServiceImpl()
        extension_repo.setRuleAttributeService(attr_svc.proxyDelegateInstance())

        exporter = new RuleValidationAttributeExporterServiceImpl()
        exporter.setExtensionRepositoryService(extension_repo)
    }

    @After
    void destroy() {
        GlobalResourceLoader.stop();
    }

    @Test(expected=IllegalArgumentException.class)
    void test_exporter_validate_no_attribute_name() {
        exporter.validate(null, RuleValidationContext.Builder.create(Rule.Builder.create().build()).build())
    }

    @Test
    void test_exporter_load_attribute() {
        def result = exporter.validate(MOCK_ATTR_NAME, RuleValidationContext.Builder.create(Rule.Builder.create().build()).build())
        Assert.assertNotNull(result)
    }

    @Test
    void test_resolver() {
        def attrib = resolver.resolveRuleValidationAttribute(MOCK_ATTR_NAME, MOCK_APP_ID)
        Assert.assertNotNull(attrib)
        def result = attrib.validate(RuleValidationContext.Builder.create(Rule.Builder.create().build()).build())
        Assert.assertNotNull(result)
    }
}

class DummyConfig implements Config{

    void parseConfig() throws IOException {

    }

    String getDailyEmailFirstDeliveryDate() {
        return null
    }

    String getWeeklyEmailFirstDeliveryDate() {
        return null
    }

    String getBaseWebServiceURL() {
        return null
    }

    String getBaseWebServiceWsdlPath() {
        return null
    }

    String getClientWSDLFullPathAndFileName() {
        return null
    }

    String getWebServicesConnectRetry() {
        return null
    }

    String getKEWBaseURL() {
        return null
    }

    String getKIMBaseURL() {
        return null
    }

    String getKRBaseURL() {
        return null
    }

    String getKENBaseURL() {
        return null
    }

    String getLog4jFileLocation() {
        return null
    }

    String getLog4jReloadInterval() {
        return null
    }

    String getTransactionTimeout() {
        return null
    }

    String getEmailConfigurationPath() {
        return null
    }

    String getRiceVersion() {
        return null
    }

    String getApplicationName() {
        return null
    }

    String getApplicationVersion() {
        return null
    }

    String getEnvironment() {
        return null
    }

    String getProductionEnvironmentCode() {
        return null
    }

    String getEDLConfigLocation() {
        return null
    }

    String getDefaultKewNoteClass() {
        return null
    }

    String getEmbeddedPluginLocation() {
        return null
    }

    Integer getRefreshRate() {
        return null
    }

    String getEndPointUrl() {
        return null
    }

    String getAlternateSpringFile() {
        return null
    }

    String getAlternateOJBFile() {
        return null
    }

    String getKeystoreAlias() {
        return null
    }

    String getKeystorePassword() {
        return null
    }

    String getKeystoreFile() throws IOException {
        return null
    }

    String getDocumentLockTimeout() {
        return null
    }

    Boolean getEmailReminderLifecycleEnabled() {
        return null
    }

    Boolean getXmlPipelineLifeCycleEnabled() {
        return null
    }

    Boolean getExternalActnListNotificationLifeCycleEnabled() {
        return null
    }

    Boolean getDevMode() {
        return null
    }

    Boolean getBatchMode() {
        return null
    }

    Boolean getOutBoxOn() {
        return null
    }

    boolean isProductionEnvironment() {
        return false
    }

    Properties getProperties() {
        return null
    }

    boolean getBooleanProperty(String key, boolean defaultValue) {
        return false
    }

    Boolean getBooleanProperty(String key) {
        return null
    }

    long getNumericProperty(String key, long defaultValue) {
        return 0
    }

    Long getNumericProperty(String key) {
        return null
    }

    Map<String, String> getPropertiesWithPrefix(String prefix, boolean stripPrefix) {
        return null
    }

    Map<String, Object> getObjects() {
        return null
    }

    Object getObject(String key) {
        return null
    }

    void putConfig(Config config) {

    }

    void putProperties(Properties properties) {

    }

    void putProperty(String key, String value) {

    }

    void removeProperty(String key) {

    }

    void putObjects(Map<String, Object> objects) {

    }

    void putObject(String key, Object value) {

    }

    void removeObject(String key) {

    }

    public String getProperty(String key){}
}