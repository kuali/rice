/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.rice.core.util;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

/**
 * This class wraps a Writer in an Outputstream.  Supports setting a custom encoding
 * in the constructor.  Otherwise the default encoding is used.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class WriterOutputStream extends OutputStream {

	private Writer writer;
	private String encoding;
	private byte[] tmpBuffer = new byte[1];
	
	public WriterOutputStream(Writer writer, String encoding) {
		this.writer = writer;
		this.encoding = encoding;
	}
	
	public WriterOutputStream(Writer writer) {
		this(writer, null);
	}
	
	@Override
	public synchronized void write(int data) throws IOException {
		tmpBuffer[0] = (byte)data;
		writer.write(new String(tmpBuffer));
	}

	@Override
	public void close() throws IOException {
		writer.close();
	}

	@Override
	public void flush() throws IOException {
		writer.flush();
	}

	@Override
	public void write(byte[] data, int offset, int length) throws IOException {
		if (encoding == null) {
			writer.write(new String(data, offset, length));
		} else {
			writer.write(new String(data, offset, length, encoding));
		}
	}

	@Override
	public void write(byte[] data) throws IOException {
		if (encoding == null) {
			writer.write(new String(data));
		} else {
			writer.write(new String(data, encoding));
		}

	}

}
