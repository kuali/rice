/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package edu.iu.uis.eden.clientapp;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.sql.DataSource;

import edu.iu.uis.eden.exception.WorkflowException;

/**
 *	Used in the ejb days to provide easy access to jndi resources 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 * @deprecated just don't use this.
 */
public class ResourceLocator {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ResourceLocator.class);
    private Context ctx;
    private String env;
    private String transactional;
    private String appCode;
    private String businessClass;
    private Object postProcessor;
    private boolean doit;

    protected ResourceLocator(Context ctx) throws WorkflowException {
        this.ctx = ctx;
        this.env = this.getValue("deployEnvironment");
        this.transactional = this.getValue("transactional");
        this.appCode = this.getValue("appCode");
        this.businessClass = this.getValue("busClassLocation");
    }

    public ResourceLocator(String env, String transactional, String appCode, String businessClass) {
        this.env = env;
        this.transactional = transactional;
        this.appCode = appCode;
        this.businessClass = businessClass;
    }

    public String getValue(String key) throws WorkflowException {
        try {
            return (String) ctx.lookup("java:comp/env/" + key);
        } catch (NamingException ex) {
            LOG.error("didn't find value for key (" + key + ") under context java:comp/env/ + key", ex);
            throw new WorkflowException("didn't find value for key (" + key + ") under context java:comp/env/ + key");
        }
    }

    public DataSource getDataSource(String name) throws WorkflowException {
        try {
            return (DataSource) ctx.lookup("java:comp/env/jdbc/" + name);
        } catch (NamingException ex) {
            LOG.error("didn't find a DataSource for name (" + name + ") under context java:comp/env/jdbc/ + key", ex);
            throw new WorkflowException("didn't find a DataSource for name (" + name + ") under context java:comp/env/jdbc/ + key");
        }
    }

    public String getEnv() {
        return env;
    }

    public String getTransactional() {
        return transactional;
    }

    public String getAppCode() {
        return appCode;
    }

    public String getBusinessClass() {
        return businessClass;
    }

    public boolean isDoit() {
        return doit;
    }

    public void setDoit(boolean doit) {
        this.doit = doit;
    }

    public Object getPostProcessor() {
        return postProcessor;
    }

    public void setPostProcessor(Object postProcessor) {
        this.postProcessor = postProcessor;
    }
}