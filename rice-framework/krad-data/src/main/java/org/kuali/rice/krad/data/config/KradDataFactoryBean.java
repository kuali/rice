package org.kuali.rice.krad.data.config;

import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.core.framework.resourceloader.BeanFactoryResourceLoader;
import org.kuali.rice.krad.data.DataObjectService;
import org.kuali.rice.krad.data.KradDataServiceLocator;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.xml.namespace.QName;

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
