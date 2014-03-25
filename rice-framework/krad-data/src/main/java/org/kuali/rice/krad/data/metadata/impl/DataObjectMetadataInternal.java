/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.krad.data.metadata.impl;

import org.kuali.rice.krad.data.metadata.DataObjectMetadata;


/**
 * Additional methods which are needed to support the linking between embedded metadata objects but that we don't want
 * to expose on our public interfaces.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface DataObjectMetadataInternal extends DataObjectMetadata {
	/**
	 * The embedded DataObjectMetadata which will be used to fill in values not specified by this DataObjectMetadata.
	 * 
	 * @return the embedded metadata, or no if no metadata is embedded
	 */
	DataObjectMetadataInternal getEmbedded();

    /**
    * Sets the embedded DataObjectMetadata.
    *
    * @param embedded or null
    */
	void setEmbedded(DataObjectMetadataInternal embedded);
}
