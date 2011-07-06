/*
 * Copyright 2011 The Kuali Foundation
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
package edu.sampleu.kew.krad.form;

import java.util.ArrayList;
import java.util.List;

import org.kuali.rice.krad.web.form.UifFormBase;
import org.springframework.web.multipart.MultipartFile;

/**
 * This is a description of what this class does - Venkat don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class IngesterForm  extends UifFormBase {

	private static final long serialVersionUID = -6874672285597100759L;
	
	private List<MultipartFile> files;
	
	/**
	 * @return the files
	 */
	public List<MultipartFile> getFiles() {
		return this.files;
	}

	/**
	 * @param files the files to set
	 */
	public void setFiles(List<MultipartFile> files) {
		this.files = files;
	}

	public IngesterForm(){
		files = new ArrayList<MultipartFile>();
		files.add(null);
		files.add(null);
		files.add(null);
		files.add(null);
		files.add(null);
		files.add(null);
		files.add(null);
		files.add(null);
		files.add(null);
		files.add(null);
	}

}
