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
package org.kuali.rice.ksb.messaging.bam.service.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.core.api.config.property.Config;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.api.criteria.Predicate;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.core.api.reflect.ObjectDefinition;
import org.kuali.rice.krad.data.DataObjectService;
import org.kuali.rice.krad.data.PersistenceOption;
import org.kuali.rice.ksb.api.bus.ServiceConfiguration;
import org.kuali.rice.ksb.api.bus.ServiceDefinition;
import org.kuali.rice.ksb.messaging.bam.BAMParam;
import org.kuali.rice.ksb.messaging.bam.BAMTargetEntry;
import org.kuali.rice.ksb.messaging.bam.service.BAMService;

import javax.xml.namespace.QName;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static org.kuali.rice.core.api.criteria.PredicateFactory.equal;
import static org.kuali.rice.core.api.criteria.PredicateFactory.like;

public class BAMServiceImpl implements BAMService {

	private static final Logger LOG = Logger.getLogger(BAMServiceImpl.class);

    private DataObjectService dataObjectService;

	public BAMTargetEntry recordClientInvocation(ServiceConfiguration serviceConfiguration, Object target, Method method, Object[] params) {
		if (isEnabled()) {
			try {
				LOG.debug("A call was received... for service: " + serviceConfiguration.getServiceName().toString() + " method: " + method.getName());
				BAMTargetEntry bamTargetEntry = getBAMTargetEntry(Boolean.FALSE, serviceConfiguration, target, method, params);
                return dataObjectService.save(bamTargetEntry, PersistenceOption.FLUSH);
			} catch (Throwable t) {
				LOG.error("BAM Failed to record client invocation", t);
			}
		}
		return null;
	}

	public BAMTargetEntry recordServerInvocation(Object target, ServiceDefinition serviceDefinition, Method method, Object[] params) {
		if (isEnabled()) {
			try {
				LOG.debug("A call was received... for service: " + target.getClass().getName() + " method: " + method.getName());
				BAMTargetEntry bamTargetEntry = getBAMTargetEntry(Boolean.TRUE, serviceDefinition, target, method, params);
                return dataObjectService.save(bamTargetEntry, PersistenceOption.FLUSH);
			} catch (Throwable t) {
				LOG.error("BAM Failed to record server invocation", t);
			}
		}
		return null;
	}

	public BAMTargetEntry recordClientInvocationError(Throwable throwable, BAMTargetEntry bamTargetEntry) {
		if (bamTargetEntry != null) {
			try {
				setThrowableOnBAMTargetEntry(throwable, bamTargetEntry);
                return dataObjectService.save(bamTargetEntry, PersistenceOption.FLUSH);
			} catch (Exception e) {
				LOG.error("BAM Failed to record client invocation error", e);
			}
		}
		return null;
	}

	public BAMTargetEntry recordServerInvocationError(Throwable throwable, BAMTargetEntry bamTargetEntry) {
		if (bamTargetEntry != null) {
			try {
				setThrowableOnBAMTargetEntry(throwable, bamTargetEntry);
                return dataObjectService.save(bamTargetEntry, PersistenceOption.FLUSH);
			} catch (Exception e) {
				LOG.error("BAM Failed to record service invocation error", e);
			}
		}
		return null;
	}

	private void setThrowableOnBAMTargetEntry(Throwable throwable, BAMTargetEntry bamTargetEntry) {
		if (throwable != null) {
			bamTargetEntry.setExceptionMessage(throwable.getMessage());
			bamTargetEntry.setExceptionToString(makeStringfit(throwable.toString()));
		}
	}

	private BAMTargetEntry getBAMTargetEntry(Boolean serverInd, ServiceConfiguration serviceConfiguration, Object target, Method method, Object[] params) {
		BAMTargetEntry bamEntry = new BAMTargetEntry();
		bamEntry.setServerInvocation(serverInd);
		bamEntry.setServiceName(serviceConfiguration.getServiceName().toString());
		bamEntry.setServiceURL(serviceConfiguration.getEndpointUrl().toExternalForm());
		bamEntry.setTargetToString(makeStringfit(target.toString()));
		bamEntry.setMethodName(method.getName());
		bamEntry.setThreadName(Thread.currentThread().getName());
		bamEntry.setCallDate(new Timestamp(System.currentTimeMillis()));
		setBamParams(params, bamEntry);
		return bamEntry;
	}
	
	private BAMTargetEntry getBAMTargetEntry(Boolean serverInd, ServiceDefinition serviceDefinition, Object target, Method method, Object[] params) {
		BAMTargetEntry bamEntry = new BAMTargetEntry();
		bamEntry.setServerInvocation(serverInd);
		bamEntry.setServiceName(serviceDefinition.getServiceName().toString());
		bamEntry.setServiceURL(serviceDefinition.getEndpointUrl().toExternalForm());
		bamEntry.setTargetToString(makeStringfit(target.toString()));
		bamEntry.setMethodName(method.getName());
		bamEntry.setThreadName(Thread.currentThread().getName());
		bamEntry.setCallDate(new Timestamp(System.currentTimeMillis()));
		setBamParams(params, bamEntry);
		return bamEntry;
	}

	private void setBamParams(Object[] params, BAMTargetEntry bamEntry) {
		if (params == null) {
			return;
		}
		for (int i = 0; i < params.length; i++) {
			BAMParam bamParam = new BAMParam();
			bamParam.setBamTargetEntry(bamEntry);
			bamParam.setParam(params[i].toString());
			bamEntry.addBamParam(bamParam);
		}
	}

	private String makeStringfit(String string) {
		if (string.length() > 1999) {
			return string.substring(0, 1999);
		}
		return string;
	}

	public boolean isEnabled() {
		return Boolean.valueOf(ConfigContext.getCurrentContextConfig().getProperty(Config.BAM_ENABLED));
	}

	public List<BAMTargetEntry> getCallsForService(QName serviceName) {
        return getCallsForService(serviceName, null);
	}

    public List<BAMTargetEntry> getCallsForService(QName serviceName, String methodName) {
        QueryByCriteria.Builder builder = QueryByCriteria.Builder.create();
        List<Predicate> predicates = new ArrayList<Predicate>();
        predicates.add(equal("serviceName", serviceName.toString()));
        if (StringUtils.isNotBlank(methodName)) {
            predicates.add(equal("methodName", methodName));
        }
        builder.setPredicates(predicates.toArray(new Predicate[predicates.size()]));
        return dataObjectService.findMatching(BAMTargetEntry.class, builder.build()).getResults();
    }

	public List<BAMTargetEntry> getCallsForRemotedClasses(ObjectDefinition objDef) {
		return getCallsForRemotedClasses(objDef, null);
	}

    public List<BAMTargetEntry> getCallsForRemotedClasses(ObjectDefinition objDef, String methodName) {
        QueryByCriteria.Builder builder = QueryByCriteria.Builder.create();
        List<Predicate> predicates = new ArrayList<Predicate>();
        QName qname = new QName(objDef.getApplicationId(), objDef.getClassName());
        predicates.add(like("serviceName", qname.toString() + "*"));
        if (StringUtils.isNotBlank(methodName)) {
            predicates.add(equal("methodName", methodName));
        }
        builder.setPredicates(predicates.toArray(new Predicate[predicates.size()]));
        return dataObjectService.findMatching(BAMTargetEntry.class, builder.build()).getResults();
    }

	public void clearBAMTables() {
        dataObjectService.deleteAll(BAMTargetEntry.class);
        dataObjectService.deleteAll(BAMParam.class);
	}

    public DataObjectService getDataObjectService() {
        return dataObjectService;
    }

    public void setDataObjectService(DataObjectService dataObjectService) {
        this.dataObjectService = dataObjectService;
    }

}
