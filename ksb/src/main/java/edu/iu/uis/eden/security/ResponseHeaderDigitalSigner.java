/*
 * Copyright 2005-2006 The Kuali Foundation.
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

import java.security.Signature;

import javax.servlet.http.HttpServletResponse;

import org.kuali.rice.RiceConstants;

/**
 * A DigitalSinger which places the alias and digital signature into the response headers of an HttpServletResponse.
 * 
 * @author ewestfal
 */
public class ResponseHeaderDigitalSigner extends AbstractDigitalSigner {

	private String alias;
	private HttpServletResponse response;
	
	public ResponseHeaderDigitalSigner(Signature signature, String alias, HttpServletResponse response) {
		super(signature);
		this.alias = alias;
		this.response = response;
	}
	
	public void sign() throws Exception {
	    this.response.setHeader(RiceConstants.KEYSTORE_ALIAS_HEADER, this.alias);
	    this.response.setHeader(RiceConstants.DIGITAL_SIGNATURE_HEADER, getEncodedSignature());
	}

}
