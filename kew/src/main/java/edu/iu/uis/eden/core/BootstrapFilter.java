/*
 * Copyright 2005-2006 The Kuali Foundation.
 *
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.iu.uis.eden.core;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.kuali.rice.config.Config;
import org.kuali.rice.core.Core;
import org.kuali.rice.definition.ObjectDefinition;
import org.kuali.rice.resourceloader.GlobalResourceLoader;

import edu.iu.uis.eden.util.ClassLoaderUtils;

/**
 * A filter which at runtime reads a series of filter configurations, constructs
 * and initializes those filters, and invokes them when it is invoked. This
 * allows runtime user configuration of arbitrary filters in the webapp context.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class BootstrapFilter implements Filter {
	private static final Logger LOG = Logger.getLogger(BootstrapFilter.class);

	private static final String FILTER_PREFIX = "filter.";

	private static final String CLASS_SUFFIX = ".class";

	private static final String FILTER_MAPPING_PREFIX = "filtermapping.";

	private FilterConfig config;

	private final Map filters = new HashMap();

	private final SortedSet filterMappings = new TreeSet();

	private boolean initted = false;

	public void init(FilterConfig cfg) throws ServletException {
		this.config = cfg;
	}

	private void addFilter(String name, String classname, Properties props) throws ServletException {
		LOG.debug("Adding filter: " + name + "=" + classname);
		Object filterObject = GlobalResourceLoader.getResourceLoader().getObject(new ObjectDefinition(classname));
		if (filterObject == null) {
			throw new ServletException("Filter '" + name + "' class not found: " + classname);

		}
		if (!(filterObject instanceof Filter)) {
			LOG.error("Class '" + filterObject.getClass() + "' does not implement servlet javax.servlet.Filter");
			return;
		}
		Filter filter = (Filter) filterObject;
		BootstrapFilterConfig fc = new BootstrapFilterConfig(config.getServletContext(), name);
		Iterator it = props.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			String key = entry.getKey().toString();
			final String prefix = FILTER_PREFIX + name + ".";
			if (!key.startsWith(prefix) || key.equals(FILTER_PREFIX + name + CLASS_SUFFIX)) {
				continue;
			}
			String paramName = key.substring(prefix.length());
			fc.addInitParameter(paramName, (String) entry.getValue());
		}
		try {
			filter.init(fc);
			filters.put(name, filter);
		} catch (ServletException se) {
			LOG.error("Error initializing filter: " + name + " [" + classname + "]", se);
		}
	}

	private void addFilterMapping(String filterName, String orderNumber, String value) {
		filterMappings.add(new FilterMapping(filterName, orderNumber, value));
	}

	private synchronized void init() throws ServletException {
		if (initted)
			return;
		LOG.debug("initializing...");
		Config cfg = Core.getRootConfig();
		Properties p = cfg.getProperties();
		Iterator entries = p.entrySet().iterator();
		while (entries.hasNext()) {
			Map.Entry entry = (Map.Entry) entries.next();
			String key = entry.getKey().toString();
			if (key.startsWith(FILTER_MAPPING_PREFIX)) {
				String[] values = key.split("\\.");
				if (values.length != 2 && values.length != 3) {
					throw new ServletException("Invalid filter mapping defined.  Should contain 2 or 3 pieces in the form of filtermapping.<<filter name>>.<<order number>> with the last piece optional.");
				}
				String filterName = values[1];
				String orderNumber = (values.length == 2 ? "0" : values[2]);
				String value = (String) entry.getValue();
				addFilterMapping(filterName, orderNumber, value);
			} else if (key.startsWith(FILTER_PREFIX) && key.endsWith(CLASS_SUFFIX)) {
				String name = key.substring(FILTER_PREFIX.length(), key.length() - CLASS_SUFFIX.length());
				String value = (String) entry.getValue();
				// ClassLoader cl =
				// SpringServiceLocator.getPluginRegistry().getInstitutionPlugin().getClassLoader();
				// addFilter(name, value, cl, p);
				addFilter(name, value, p);
			}
		}
		// do a diff log a warn if any filter has no mappings
		for (Iterator iter = filters.keySet().iterator(); iter.hasNext();) {
			String filterName = (String) iter.next();
			if (!hasFilterMapping(filterName)) {
				LOG.warn("NO FILTER MAPPING DETECTED.  Filter " + filterName + " has no mapping and will not be called.");
			}
		}
		initted = true;
	}

	private boolean hasFilterMapping(String filterName) {
		for (Iterator iter = filterMappings.iterator(); iter.hasNext();) {
			FilterMapping filterMapping = (FilterMapping) iter.next();
			if (filterMapping.getFilterName().equals(filterName)) {
				return true;
			}
		}
		return false;
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
	    LOG.debug("Begin BootstrapFilter...");
		init();
		// build the filter chain and execute it
		if (!filterMappings.isEmpty() && request instanceof HttpServletRequest) {
			chain = buildChain((HttpServletRequest) request, chain);
		}
		LOG.debug("...ending BootstrapFilter preperation, executing BootstrapFilter Chain.");
		chain.doFilter(request, response);

	}

	// private static void initializeServiceThreadLocal() {
	// servicesToRemove.set(new ArrayList());
	// }
	//
	// private static void cleanUpServiceThreadLocal() {
	// LOG.debug("cleaning up services from Boostrap Filter Thread: " +
	// Thread.currentThread().getName());
	// try {
	// List services = (List) servicesToRemove.get();
	// for (Iterator iter = services.iterator(); iter.hasNext();) {
	// String serviceName = (String) iter.next();
	// SpringServiceLocator.getMessageHelper().sendMessage(MessageServiceNames.SERVICE_REMOVER_SERVICE,
	// serviceName);
	// }
	// } finally {
	// servicesToRemove.set(null);
	// }
	// }

	// public static boolean isDoingServiceCleanup() {
	// return servicesToRemove.get() != null;
	// }

	// public static void addServiceForCleanUp(String serviceName) {
	// if (! isDoingServiceCleanup()) {
	// throw new WorkflowRuntimeException("This service is not being accessed
	// from the web layer");
	// }
	// ((List)servicesToRemove.get()).add(serviceName);
	// }

	private FilterChain buildChain(HttpServletRequest request, FilterChain targetChain) {
		BootstrapFilterChain chain = new BootstrapFilterChain(targetChain, ClassLoaderUtils.getDefaultClassLoader());
		String requestPath = request.getServletPath();
		for (Iterator iter = filterMappings.iterator(); iter.hasNext();) {
			FilterMapping mapping = (FilterMapping) iter.next();
			Filter filter = (Filter) filters.get(mapping.getFilterName());
			if (!chain.containsFilter(filter) && matchFiltersURL(mapping.getUrlPattern(), requestPath)) {
				chain.addFilter(filter);
			}
		}
		return chain;
	}

	public void destroy() {
		Iterator it = filters.values().iterator();
		while (it.hasNext()) {
			Filter filter = (Filter) it.next();
			try {
				filter.destroy();
			} catch (Exception e) {
				LOG.error("Error destroying filter: " + filter, e);
			}
		}
	}

	/**
	 * This method was borrowed from the Tomcat codebase.
	 */
	private boolean matchFiltersURL(String urlPattern, String requestPath) {

		if (requestPath == null)
			return (false);

		// Match on context relative request path
		if (urlPattern == null)
			return (false);

		// Case 1 - Exact Match
		if (urlPattern.equals(requestPath))
			return (true);

		// Case 2 - Path Match ("/.../*")
		if (urlPattern.equals("/*") || urlPattern.equals("*"))
			return (true);
		if (urlPattern.endsWith("/*")) {
			if (urlPattern.regionMatches(0, requestPath, 0, urlPattern.length() - 2)) {
				if (requestPath.length() == (urlPattern.length() - 2)) {
					return (true);
				} else if ('/' == requestPath.charAt(urlPattern.length() - 2)) {
					return (true);
				}
			}
			return (false);
		}

		// Case 3 - Extension Match
		if (urlPattern.startsWith("*.")) {
			int slash = requestPath.lastIndexOf('/');
			int period = requestPath.lastIndexOf('.');
			if ((slash >= 0) && (period > slash) && (period != requestPath.length() - 1) && ((requestPath.length() - period) == (urlPattern.length() - 1))) {
				return (urlPattern.regionMatches(2, requestPath, period + 1, urlPattern.length() - 2));
			}
		}

		// Case 4 - "Default" Match
		return (false); // NOTE - Not relevant for selecting filters

	}

}

/**
 * A filter chain that invokes a series of filters with which it was
 * initialized, and then delegates to a target filterchain.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
class BootstrapFilterChain implements FilterChain {

	private final List filters = new LinkedList();

	private final FilterChain target;

	private Iterator filterIterator;

	private ClassLoader originalClassLoader;

	public BootstrapFilterChain(FilterChain target, ClassLoader originalClassLoader) {
		this.target = target;
		this.originalClassLoader = originalClassLoader;
	}

	public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
		if (filterIterator == null) {
			filterIterator = filters.iterator();
		}
		if (filterIterator.hasNext()) {
			((Filter) filterIterator.next()).doFilter(request, response, this);
		} else {
			// reset the CCL to the original classloader before calling the non
			// workflow configured filter - this makes it so our
			// CCL is the webapp classloader in workflow action classes and the
			// code they call
			Thread.currentThread().setContextClassLoader(originalClassLoader);
			target.doFilter(request, response);
		}
	}

	public void addFilter(Filter filter) {
		filters.add(filter);
	}

	public boolean containsFilter(Filter filter) {
		return filters.contains(filter);
	}

	public boolean isEmpty() {
		return filters.isEmpty();
	}

}

/**
 * Borrowed from spring-mock.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
class BootstrapFilterConfig implements FilterConfig {

	private final ServletContext servletContext;

	private final String filterName;

	private final Properties initParameters = new Properties();

	public BootstrapFilterConfig() {
		this(null, "");
	}

	public BootstrapFilterConfig(String filterName) {
		this(null, filterName);
	}

	public BootstrapFilterConfig(ServletContext servletContext) {
		this(servletContext, "");
	}

	public BootstrapFilterConfig(ServletContext servletContext, String filterName) {
		this.servletContext = servletContext;
		this.filterName = filterName;
	}

	public String getFilterName() {
		return filterName;
	}

	public ServletContext getServletContext() {
		return servletContext;
	}

	public void addInitParameter(String name, String value) {
		this.initParameters.setProperty(name, value);
	}

	public String getInitParameter(String name) {
		return this.initParameters.getProperty(name);
	}

	public Enumeration getInitParameterNames() {
		return this.initParameters.keys();
	}

}

class FilterMapping implements Comparable {

	private String filterName;

	private String orderValue;

	private String urlPattern;

	public FilterMapping(String filterName, String orderValue, String urlPattern) {
		this.filterName = filterName;
		this.orderValue = orderValue;
		this.urlPattern = urlPattern;
	}

	public int compareTo(Object object) {
		return orderValue.compareTo(((FilterMapping) object).orderValue);
	}

	public String getFilterName() {
		return filterName;
	}

	public String getOrderValue() {
		return orderValue;
	}

	public String getUrlPattern() {
		return urlPattern;
	}

}