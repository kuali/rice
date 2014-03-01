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
package org.kuali.rice.krad.data;

import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.core.framework.resourceloader.BeanFactoryResourceLoader;
import org.kuali.rice.krad.data.DataObjectService;
import org.kuali.rice.krad.data.KradDataServiceLocator;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.xml.namespace.QName;

/**
 * A factory bean which load and/or acquires a reference to the data framework.
 *
 * <p>Will lazy-initialize the framework if it's not already been initialized. The factory bean will return a reference
 * the {@link DataObjectService} which is the main API entry point into the data framework.</p>
 *
 * @see DataObjectService
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class KradDataFactoryBean implements FactoryBean<DataObjectService> {

    private static final String SPRING_FILE = "classpath:org/kuali/rice/krad/data/config/KRADDataSpringBeans.xml";

    @Override
    public DataObjectService getObject() throws Exception {
        // first, let's see if it's already been initialized
        DataObjectService dataObjectService = KradDataServiceLocator.getDataObjectService();
        if (dataObjectService == null) {
            ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(SPRING_FILE);
            BeanFactoryResourceLoader rl = new BeanFactoryResourceLoader(new QName("krad-data"), context);
            rl.start();
            GlobalResourceLoader.addResourceLoader(rl);
            dataObjectService = KradDataServiceLocator.getDataObjectService();
        }
        if (dataObjectService == null) {
            throw new IllegalStateException("Failed to locate or initialize krad data framework.");
        }
        return dataObjectService;
    }

    @Override
    public Class<?> getObjectType() {
        return DataObjectService.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

}
