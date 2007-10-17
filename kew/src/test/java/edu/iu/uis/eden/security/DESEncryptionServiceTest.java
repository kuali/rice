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
package edu.iu.uis.eden.security;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.kuali.rice.config.Config;
import org.kuali.rice.config.SimpleConfig;
import org.kuali.rice.core.Core;

/**
 * Tests the DESEncryptionService.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class DESEncryptionServiceTest extends TestCase {

	private static boolean failed = false;

	/**
	 * Verfies that the DESEncryptionService is thread-safe.  We had problems originally with the
	 * thread-safety of the implementation so we added this test to verify and prevent regression.
	 */
	public void testEncryptionMultiThreaded() throws Exception {
		String key = DESEncryptionService.generateEncodedKey();
		Config config = Core.getCurrentContextConfig();
		if (config == null) {
		    // because of previously running tests, the config might already be initialized
		    config = new SimpleConfig();
		    Core.init(config);
		}
		config.overrideProperty("encryption.key", key);

		final EncryptionService service = new DESEncryptionService();
		List<Thread> threads = new ArrayList<Thread>();
		failed = false;
		for (int i =0; i < 10; i++) {
			threads.add(new Thread() {
				public void run() {
					try {
						for (int j = 0; j < 100; j++) {
							String badText = "This is so going to no longer explode";
							String badEnc = service.encrypt(badText);
							String badDec = service.decrypt(badEnc);
							assertEquals(badText, badDec);
						}
					} catch (Exception e) {
						e.printStackTrace();
						failed = true;
						fail("Encryption service use to be non-thread safe, but it should be now!");
					}
				}
			});
		}
		for (Thread thread : threads) {
			thread.start();
		}
		for (Thread thread: threads) {
			thread.join();
		}
		// assert that the encryption doesn't fail any longer in a multi-threaded environment, this verifies
		// the fix to the encryption service
		assertFalse(failed);
	}

	/**
	 * Similar to the test above except that a new DESEncryptionService is created for each thread.
	 */
	public void testEncryptionMultiThreadedSafe() throws Exception {
		String key = DESEncryptionService.generateEncodedKey();
		Config config = Core.getCurrentContextConfig();
		if (config == null) {
		    // because of previously running tests, the config might already be initialized
		    config = new SimpleConfig();
		    Core.init(config);
		}
		config.overrideProperty("encryption.key", key);
		List<Thread> threads = new ArrayList<Thread>();
		failed = false;
		for (int i =0; i < 10; i++) {
			threads.add(new Thread() {
				public void run() {
					try {
						final EncryptionService service = new DESEncryptionService();
						for (int j = 0; j < 100; j++) {
							String badText = "This is so going to NOT explode";
							String badEnc = service.encrypt(badText);
							String badDec = service.decrypt(badEnc);
							assertEquals(badText, badDec);
						}
					} catch (Exception e) {
						e.printStackTrace();e.printStackTrace();
						failed = true;
						fail("Encryption service failed in mysterious ways.");
					}
				}
			});
		}
		for (Thread thread : threads) {
			thread.start();
		}
		for (Thread thread: threads) {
			thread.join();
		}
		// assert that the encryption/decription did not fail
		assertFalse(failed);
	}

}
