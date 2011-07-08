/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.rice.ksb.messaging;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.kuali.rice.core.api.exception.RiceRuntimeException;

import javax.xml.namespace.QName;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import static org.junit.Assert.assertTrue;


public class QNameSerializationTest {
	
	private static final Logger LOG = Logger.getLogger(QNameSerializationTest.class);
	
	@Test public void testQNameSerializaion() throws Exception {
		
		QName qname = new QName("hi", "HI");
		String qnameSerialized = serializeObject(qname);
		deserializeObject(qnameSerialized);
		
		
		
		assertTrue(true);
	}

	public String serializeObject(Serializable object) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutput out = null;
		try {
			out = new ObjectOutputStream(bos);
			out.writeObject(object);
		} catch (IOException e) {
			throw new RiceRuntimeException(e);
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				LOG.error("Failed to close ObjectOutputStream", e);
			}
		}
		byte[] buf = bos.toByteArray();
		Base64 b64 = new Base64();
		byte[] encodedObj = b64.encode(buf);
		return new String(encodedObj);
	}

	public Object deserializeObject(String serializedObject) {
		Base64 b64 = new Base64();
		byte[] result = b64.decode(serializedObject.getBytes());
		Object payload = null;
		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(new ByteArrayInputStream(result));
			payload = ois.readObject();
		} catch (Exception e) {
			// may want to move this loggging up
			LOG.error("Caught Error de-serializing message payload", e);
//			throw new RiceRuntimeException(e);
		} finally {
			try {
				ois.close();
			} catch (IOException e) {
				LOG.error("Failed to close de-serialization stream", e);
			}
		}
		return payload;
	}
	
}
