/**
 * Copyright 2005-2013 The Kuali Foundation
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
package ojbmetadata
import groovy.transform.ToString;

@ToString(includePackage=false)
public class Field {
	def id
	def name
	def column
	def jdbcType
	def primarykey
	def nullable
	def indexed
	def autoincrement
	def sequenceName
	def locking
	def conversion
	def access
}
