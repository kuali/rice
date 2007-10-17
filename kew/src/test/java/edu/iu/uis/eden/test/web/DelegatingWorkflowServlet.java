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
// Copyright (c) 2005 Aaron Hamid.  All rights reserved.

package edu.iu.uis.eden.test.web;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import edu.iu.uis.eden.web.UserLoginFilter;

/**
 * A wrapper servlet that invokes the UserLoginFilter, and then delegates to a
 * target servlet
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class DelegatingWorkflowServlet implements Servlet {
    private static final UserLoginFilter USERLOGINFILTER = new UserLoginFilter();

    private static class ServletFilterChain implements FilterChain {
        private Servlet servlet;
        public ServletFilterChain(Servlet servlet) {
            this.servlet = servlet;
        }
        public void doFilter(ServletRequest req, ServletResponse res) throws IOException, ServletException {
            servlet.service(req, res);
        }
    }

    private Servlet delegate;
    private FilterChain chain;

    public DelegatingWorkflowServlet(Servlet servlet) {
        this.delegate = servlet;
        this.chain = new ServletFilterChain(servlet);
    }

    public String getServletInfo() {
        return delegate.getServletInfo();
    }

    public ServletConfig getServletConfig() {
        return delegate.getServletConfig();
    }

    public void init(ServletConfig config) throws ServletException {
        delegate.init(config);
    }

    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        USERLOGINFILTER.doFilter(req, res, chain);
    }

    public void destroy() {
        delegate.destroy();
    }
}