package org.kuali.rice.ojb;

import java.util.Collection;

import org.apache.ojb.broker.PBKey;
import org.apache.ojb.broker.PersistenceBrokerException;
import org.apache.ojb.broker.core.proxy.ListProxyDefaultImpl;
import org.apache.ojb.broker.query.Query;
import org.kuali.rice.util.ClassLoaderUtils;
import org.kuali.rice.util.ContextClassLoaderBinder;


/**
 * Sets up the context classloader properly for OJB proxies.  The sequence of events in the super class is as
 * follows:
 * 
 * <pre>
 * public synchronized Collection getData()
 * {
 *    if (!isLoaded())
 *    {
 *      beforeLoading();
 *      setData(loadData());
 *      afterLoading();
 *    }
 *    return _data;
 * }
 * </pre>
 *
 * Therefore, since there is no try-catch-finally in conjunction with this call, we need to handle exceptions thrown
 * from loadData and unbind the classloader appropriately.
 */
public class ContextClassLoaderListProxy extends ListProxyDefaultImpl {

	private static final long serialVersionUID = -6734848267763746202L;

	// this object is used by object that's are serialized and we don't want to take this reference
	// across the wire.  May need to acquire the CCL if this is null on beforeLoading() doing nothing for now...
	private transient ClassLoader contextClassLoader;
	
	public ContextClassLoaderListProxy(PBKey aKey, Class aCollClass, Query aQuery) {
		super(aKey, aCollClass, aQuery);
		this.contextClassLoader = ClassLoaderUtils.getDefaultClassLoader();
	}

	public ContextClassLoaderListProxy(PBKey aKey, Query aQuery) {
		super(aKey, aQuery);
		this.contextClassLoader = ClassLoaderUtils.getDefaultClassLoader();	
	}

	@Override
	protected void beforeLoading() {
		ContextClassLoaderBinder.bind(this.contextClassLoader);
		super.beforeLoading();
	}
	
	@Override
	protected Collection loadData() throws PersistenceBrokerException {
		try {
			return super.loadData();
		} catch (Throwable t) {
			ContextClassLoaderBinder.unbind();
			if (t instanceof PersistenceBrokerException) {
				throw (PersistenceBrokerException)t;
			} else if (t instanceof Error) {
				throw (Error)t;
			} else if (t instanceof RuntimeException) {
				throw (RuntimeException)t;
			} else {
				throw new PersistenceBrokerException("Invalid exception type thrown!", t);
			}
		}
	}

	@Override
	protected void afterLoading() {
		super.afterLoading();
		ContextClassLoaderBinder.unbind();
	}

}
