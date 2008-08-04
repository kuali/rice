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
// Created on Dec 6, 2006

package edu.iu.uis.eden.server;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet that forwards requests via the RequestDispatcher based on regular expression
 * matching and replacement on the URI
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class RequestForwardingServlet extends HttpServlet {
    private LinkedHashMap<Pattern, MessageFormat> forwardTable = new LinkedHashMap<Pattern, MessageFormat>();

    @Override
    public void init() throws ServletException {
        super.init();

        ServletConfig cfg = getServletConfig();
        Enumeration paramNames = cfg.getInitParameterNames(); 
        while (paramNames.hasMoreElements()) {
            String name = (String) paramNames.nextElement();
            Pattern pattern = Pattern.compile(name);
            String forwardPath = cfg.getInitParameter(name);
            MessageFormat messageFormat = new MessageFormat(forwardPath);
            forwardTable.put(pattern, messageFormat);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        for (Map.Entry<Pattern, MessageFormat> entry: forwardTable.entrySet()) {
            Pattern pattern = entry.getKey();
            Matcher matcher = pattern.matcher(requestURI);
            if (matcher.matches()) {
                Object[] params = new Object[matcher.groupCount()];
                for (int i = 0; i < params.length; i++) {
                    params[i] = matcher.group(i + 1); // offset by 1 because group at index 0 denotes the "entire pattern" 
                }
                String forwardPath = entry.getValue().format(params);
                RequestDispatcher rd = request.getRequestDispatcher(forwardPath);
                if (rd == null) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Internal resource '" + forwardPath + "' not found");
                } else {
                    rd.forward(request, response);
                }
                return;
            }
        }
        
    }
}