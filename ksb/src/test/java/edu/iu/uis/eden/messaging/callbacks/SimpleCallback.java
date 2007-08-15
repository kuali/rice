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
package edu.iu.uis.eden.messaging.callbacks;

import java.io.Serializable;

import edu.iu.uis.eden.messaging.AsynchronousCall;
import edu.iu.uis.eden.messaging.AsynchronousCallback;

/**
 * This is a description of what this class does - rkirkend don't forget to fill this in. 
 * 
 * @author rkirkend
 *
 */
public class SimpleCallback implements AsynchronousCallback {

    private static final long serialVersionUID = -1097606463996858063L;
    
    private Serializable returnObject;
    private AsynchronousCall methodCall;

    public void callback(Serializable returnObject, AsynchronousCall methodCall) {
	this.returnObject = returnObject;
	this.methodCall = methodCall;
    }

    public AsynchronousCall getMethodCall() {
        return this.methodCall;
    }

    public void setMethodCall(AsynchronousCall methodCall) {
        this.methodCall = methodCall;
    }

    public Serializable getReturnObject() {
        return this.returnObject;
    }

    public void setReturnObject(Serializable returnObject) {
        this.returnObject = returnObject;
    }
    
    public synchronized void waitForAsyncCall() throws InterruptedException {
	waitForAsyncCall(-1);
    }
    
    public synchronized void waitForAsyncCall(long millis) throws InterruptedException {
	if (millis < 0) {
	    this.wait(30000);
	} else {
	    this.wait(millis);    
	}
    }

}
