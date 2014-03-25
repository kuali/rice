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
package org.kuali.rice.krad.data.provider;

import java.util.List;

/**
 * This metadata provider forms the main provider for the krad-data module.
 *
 * <p>It's responsible for merging the provided list of metadata providers.</p>
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * 
 */
public interface CompositeMetadataProvider extends MetadataProvider {

	/**
	 * The list of providers this provider will use.
     *
     * <p>They must be in the necessary order for processing. Later providers in the list.</p>
	 * 
	 * @return a list of all metadata providers contained within this composite provider
	 */
	List<MetadataProvider> getProviders();

}