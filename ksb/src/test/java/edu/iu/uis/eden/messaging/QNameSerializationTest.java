package edu.iu.uis.eden.messaging;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javax.xml.namespace.QName;

import junit.framework.TestCase;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.kuali.rice.exceptions.RiceRuntimeException;


public class QNameSerializationTest extends TestCase {
	
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
