/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.rice.kew.batch;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Stream-based XML doc 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class StreamXmlDoc extends BaseXmlDoc {
    private byte[] bytes;
    private InputStream stream;
    public StreamXmlDoc(InputStream stream, XmlDocCollection collection) {
        super(collection);
        this.stream = stream;
    }
    public synchronized InputStream getStream() throws IOException {
        // this sucks, but does it suck less than copying the file?
        // or dealing with concurrency issues on a surveilling input stream
        if (bytes == null) {
            bytes = IOUtils.toByteArray(stream);
        }
        return new ByteArrayInputStream(bytes);
    }
    public String getName() {
        return stream.toString();
    }
}
