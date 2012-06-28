/**
 * Copyright 2005-2011 The Kuali Foundation
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
package org.kuali.test;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.lifecycle.Lifecycle;
import org.kuali.rice.core.framework.resourceloader.SpringResourceLoader;
import org.kuali.rice.krad.datadictionary.DataDictionary;
import org.kuali.rice.test.BaselineTestCase;
import org.kuali.rice.test.SQLDataLoader;
import org.kuali.rice.test.TestHarnessServiceLocator;
import org.kuali.rice.test.TestUtilities;
import org.kuali.rice.test.lifecycles.KEWXmlDataLoaderLifecycle;

import javax.xml.namespace.QName;
import java.util.HashSet;
import java.util.List;

/**
 * Default test base for a full KRAD enabled integration test
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BaselineTestCase.BaselineMode(BaselineTestCase.Mode.ROLLBACK_CLEAR_DB)
public abstract class KRADTestCase extends BaselineTestCase {

    private static final String SQL_FILE = "classpath:org/kuali/rice/krad/test/DefaultSuiteTestData.sql";
    private static final String XML_FILE = "classpath:org/kuali/rice/krad/test/DefaultSuiteTestData.xml";
    private static final String KRAD_MODULE_NAME = "krad";

    private SpringResourceLoader kradTestSpringResourceLoader;

    protected DataDictionary dd = null;

    public KRADTestCase() {
        super(KRAD_MODULE_NAME);
    }

    /**
     * propagate constructor
     * @param moduleName - the name of the module
     */
    public KRADTestCase(String moduleName) {
        super(moduleName);
    }

    @Override
    protected void setUpInternal() throws Exception {
        super.setUpInternal();

        List<Class> classes = TestUtilities.getHierarchyClassesToHandle(getClass(),
                new Class[]{TestDictionaryConfig.class}, new HashSet<String>());

        // if annotation is present then initialize test data dictionary
        if (!classes.isEmpty()) {
            dd = (DataDictionary) kradTestSpringResourceLoader.getContext().getBean("testDataDictionary");

            // add any additional dictionary files required by the test
            for (Class c : classes) {
                if (c.isAnnotationPresent(TestDictionaryConfig.class)) {
                    TestDictionaryConfig testDictionaryConfig = (TestDictionaryConfig) c.getAnnotation(
                            TestDictionaryConfig.class);

                    String dictionaryFileString = testDictionaryConfig.dataDictionaryFiles();
                    String[] dictionaryFiles = StringUtils.split(dictionaryFileString, ",");
                    for (String dictionaryFile : dictionaryFiles) {
                        dd.addConfigFileLocation(dictionaryFile);
                    }
                }
            }

            dd.parseDataDictionaryConfigurationFiles(true);
        }
    }

    /**
     * Returns an instance of the bean with the given id that has been configured in the test dictionary
     *
     * @param id - id of the bean definition
     * @return Object instance of the given bean class, or null if not found or dictionary is not loaded
     */
    protected Object getTestDictionaryObject(String id) {
        if (dd != null) {
            return dd.getDictionaryObject(id);
        }

        return null;
    }

    @Override
    protected List<Lifecycle> getSuiteLifecycles() {
        List<Lifecycle> suiteLifecycles = super.getSuiteLifecycles();
        suiteLifecycles.add(new KEWXmlDataLoaderLifecycle(XML_FILE));
        return suiteLifecycles;
    }

    @Override
    protected void loadSuiteTestData() throws Exception {
        super.loadSuiteTestData();
        new SQLDataLoader(SQL_FILE, ";").runSql();
    }

    @Override
    protected Lifecycle getLoadApplicationLifecycle() {
        return getKRADTestSpringResourceLoader();
    }

    public SpringResourceLoader getKRADTestSpringResourceLoader() {
        if (kradTestSpringResourceLoader == null) {
            kradTestSpringResourceLoader = new SpringResourceLoader(new QName("KRADTestResourceLoader"),
                    "classpath:KRADTestSpringBeans.xml", null);
        }
        return kradTestSpringResourceLoader;
    }
}
