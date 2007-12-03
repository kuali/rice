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
// Created on May 8, 2006

package edu.iu.uis.eden.test.web;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A trivial servlet which echoes back request params, and inserts a few
 * other parameters which can be used for testing purposes, into parseable response metadata.
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class TestServlet extends HttpServlet {
    private static final Random RANDOM = new Random();

    private static String getValue(Object value) {
        if (value instanceof String[]) {
            String[] va = (String[]) value;
            if (va.length > 0) {
                value = va[0];
            } else {
                value = "";
            }
        }
        return String.valueOf(value);
    }

    private static String redact(Object o) {
        return "<!-- [transient start] -->" + String.valueOf(o) + "<!-- [transient end] -->";
    }

    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Map params = request.getParameterMap();
        /* some data to redact */
        Date date = new Date();
        int rand = RANDOM.nextInt(10000);
        /* some data that we will persist accross requests */
        String s = request.getParameter("persist_num");
        boolean first_creation;
        int persist_num;
        if (s == null) {
            persist_num = RANDOM.nextInt(10000);
            first_creation = true;
        } else {
            persist_num = Integer.parseInt(s);
            first_creation = false;
        }

        /* list of incoming params we know to be "variable" and thus need to be redacted
         * We only need this because we are iterating through and printing all the incoming
         * params arbitrarily...so we need to know which ones to skip.  A real servlet wouldn't be
         * arbitrarily echoing parameters and would intrinsically know about output that needs to be redacted 
         */
        final List redact_params = Arrays.asList(new String[] { "persist_num", "TheRandomNumberWeGotFromTheInitialRequest" });

        ServletOutputStream out = response.getOutputStream();

        out.println("Method: " + request.getMethod() + "\r\n\r\n");

        Iterator it = params.entrySet().iterator();
        /* first print out parameters for human interpretation */
        out.println("Request parameters:\r\n");
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            out.print(entry.getKey() + ": ");
            boolean redact = redact_params.contains(entry.getKey()); 
            if (redact) {
                out.print("<!-- [transient start] -->");
            }
            out.print(getValue(entry.getValue()));
            if (redact) {
                out.print("<!-- [transient end] -->");
            }
            out.print("\r\n");
        }
        
        /* now let's put print the data that should be redacted before comparison */
        out.print("\r\nSome data to redact:\r\n");
        out.print("Date: " + redact(date) + "\r\n");
        out.print("Random number: " + redact(new Integer(rand)) + "\r\n");
        /* now print the persistent number */
        out.print("Persistent number: " + redact(new Integer(persist_num)) + (first_creation ? " (created)" : " (taken from input parameters)"));

        /* then use meta-data notation to print those parameters in a format that can be interpreted
         * by the script
         */
        out.print("\r\n<!-- Meta-data block for automation/testing\r\n");
        it = params.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            boolean redact = redact_params.contains(entry.getKey()); 
            if (redact) {
                out.print("redacting: " + entry.getKey() + "\r\n");
                out.print("[transient start]\r\n");
            }
            out.print("[var " + entry.getKey() + "=" + getValue(entry.getValue()) + "]\r\n");
            if (redact) {
                out.print("[transient end]\r\n");
            }
        }
        /* now let's put some data to be redacted in there
         * the script knows to redact anything between [transient start]/[transient end]
         * (maybe that is not the best choice of words)
         */
        out.print("some data to redact follows\r\n");
        out.print("[transient start]\r\n");
        out.print("[var date=" + date + "]\r\n");
        out.print("[var random_number=" + rand + "]\r\n");
        /* finally write that number we want to persist accross requests
         * This is just a normal variable.  We will configure our script to
         * look for it, and resend it, to simulate, for example, a docId.
         * It is in the "transient" block because it is random (so perhaps
         * "transient" should be changed to "variable" or "dynamic" or simply "redact"
         * to contrast against "static" unchanging content which can be easily
         * compared for verification.  The redaction will occur AFTER
         * the variable is parsed.
         */
        out.print("[var persist_num=" + persist_num + "]\r\n");
        out.print("[transient end]\r\n");
        out.print("-->");
    }
}