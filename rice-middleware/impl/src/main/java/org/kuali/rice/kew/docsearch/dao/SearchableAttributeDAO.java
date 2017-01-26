/**
 * Copyright 2005-2017 The Kuali Foundation
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
package org.kuali.rice.kew.docsearch.dao;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

public interface SearchableAttributeDAO {

	public List<String> getSearchableAttributeStringValuesByKey(
			String documentId, String key);

	public List<Timestamp> getSearchableAttributeDateTimeValuesByKey(
			String documentId, String key);

	public List<BigDecimal> getSearchableAttributeFloatValuesByKey(
			String documentId, String key);

	public List<Long> getSearchableAttributeLongValuesByKey(String documentId,
			String key);
}
