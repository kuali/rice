/*
 * Copyright 2005-2008 The Kuali Foundation
 * 
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
package org.kuali.rice.kew.util;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import javax.activation.DataSource;

import org.kuali.rice.kew.exception.WorkflowRuntimeException;


/**
 * A simple DataSource for demonstration purposes. This class implements a
 * DataSource from: an InputStream a byte array a String.
 */
public class ByteArrayDataSource implements DataSource {

	private byte[] data; // data
    private String type; // content-type

    /* Create a DataSource from an input stream */
    public ByteArrayDataSource(InputStream is, String type) {
        this.type = type;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            OutputStream os = new BufferedOutputStream(baos);
            int byteData;
            while ((byteData = is.read()) != -1) {
                os.write(byteData);
            }
            os.flush();
            data = baos.toByteArray();
        } catch (IOException ioex) {
        	throw new WorkflowRuntimeException(ioex);
        }
    }

    /* Create a DataSource from a byte array */
    public ByteArrayDataSource(byte[] data, String type) {
        this.data = data;
        this.type = type;
    }

    /* Create a DataSource from a String */
    public ByteArrayDataSource(String data, String type) {
        try {
            // Assumption that the string contains only ASCII
            // characters! Otherwise just pass a charset into this
            // constructor and use it in getBytes()
            this.data = data.getBytes("iso-8859-1");
        } catch (UnsupportedEncodingException uex) {}
        this.type = type;
    }

    /**
     * Return an InputStream for the data. Note - a new stream must be returned
     * each time.
     */
    public InputStream getInputStream() throws IOException {
        if (data == null)
            throw new IOException("no data");
        return new ByteArrayInputStream(data);
    }

    public OutputStream getOutputStream() throws IOException {
        throw new IOException("cannot do this");
    }

    public String getContentType() {
        return type;
    }

    public String getName() {
        return "dummy";
    }
}
