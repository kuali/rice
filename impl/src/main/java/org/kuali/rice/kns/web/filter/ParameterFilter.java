/*
 * Copyright 2006-2014 The Kuali Foundation
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

package org.kuali.rice.kns.web.filter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

/**
 * Filters parameters coming in through Struts requests to exclude those that could be damaging to the class loader in
 * response to CVE-2014-0114.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ParameterFilter implements Filter {

    private String excludeParams;

    public void init(FilterConfig filterConfig) throws ServletException {
        this.excludeParams = filterConfig.getInitParameter("excludeParams");
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        chain.doFilter(new FilteredServletRequest(request, excludeParams), response);
    }

    public void destroy() { }

    private static class FilteredServletRequest extends HttpServletRequestWrapper {

        private final String excludeParams;

        private FilteredServletRequest(ServletRequest request, String excludeParams) {
            super((HttpServletRequest) request);

            this.excludeParams = excludeParams;
        }

        @Override
        @SuppressWarnings({"rawtypes", "unchecked"})
        public Enumeration getParameterNames() {
            List<String> finalParameterNames = new ArrayList<String>();

            ArrayList<String> requestParameterNames = Collections.list(super.getParameterNames());

            for (String parameterName : requestParameterNames) {
                if (!parameterName.matches(excludeParams)) {
                    finalParameterNames.add(parameterName);
                }
            }

            return Collections.enumeration(finalParameterNames);
        }
    }

}
