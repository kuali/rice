package org.kuali.core.web.servlet.dwr;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.kuali.rice.resourceloader.GlobalResourceLoader;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.support.RequestContextUtils;

import uk.ltd.getahead.dwr.Messages;
import uk.ltd.getahead.dwr.WebContextFactory;
import uk.ltd.getahead.dwr.create.SpringCreator;

/**
 * A {@link SpringCreator} that checks the {@link GlobalResourceLoader} for the
 * bean name in question if the default {@link BeanFactory} (the applications)
 * does not have the bean in question.
 * 
 * @author rkirkend
 * 
 */
public class GlobalResourceDelegatingSpringCreator extends SpringCreator {

	private static final Logger LOG = Logger.getLogger(GlobalResourceDelegatingSpringCreator.class);

	private BeanFactory factory;
	
	public static BeanFactory APPLICATION_BEAN_FACTORY;

	@Override
	/*
	 * This is largely ripped from the super class. Implementation details
	 * prevent calling the super class and then going to the
	 * GlobalResourceLoader ( the BeanFactory wll throw an exception if the bean
	 * isn't there). The defalt SpringCreator's static BeanFactory override
	 * could not be used - what a pity.
	 * 
	 */
	public Object getInstance() throws InstantiationException {
		if (factory == null) {
			factory = getBeanFactory();
		}
		
		if (factory == null) {
			factory = APPLICATION_BEAN_FACTORY;
		}

		//we could just delegation to the GRL here no need for a resource factory
		if (factory == null) {
			LOG.error("DWR can't find a spring config. See following info logs for solutions"); //$NON-NLS-1$
			LOG.info("- Option 1. In dwr.xml, <create creator='spring' ...> add <param name='location1' value='beans.xml'/> for each spring config file."); //$NON-NLS-1$
			LOG.info("- Option 2. Use a spring org.springframework.web.context.ContextLoaderListener."); //$NON-NLS-1$
			LOG.info("- Option 3. Call SpringCreator.setOverrideBeanFactory() from your web-app"); //$NON-NLS-1$
			throw new InstantiationException(Messages.getString("SpringCreator.MissingConfig")); //$NON-NLS-1$
		}

		if (factory.containsBean(this.getBeanName())) {
			return factory.getBean(this.getBeanName());
		} else {
			Object bean = GlobalResourceLoader.getService(this.getBeanName());
			if (bean == null) {
				throw new InstantiationException("Unable to find bean " + this.getBeanName() + " in Rice Global Resource Loader");
			}
			return bean;
		}
	}

	/**
	 * @return A found BeanFactory configuration
	 */
	private BeanFactory getBeanFactory() {
		ServletContext srvCtx = WebContextFactory.get().getServletContext();
		HttpServletRequest request = WebContextFactory.get().getHttpServletRequest();

		if (request != null) {
			return RequestContextUtils.getWebApplicationContext(request, srvCtx);
		} else {
			return WebApplicationContextUtils.getWebApplicationContext(srvCtx);
		}
	}

}
