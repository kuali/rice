--
-- Copyright 2005-2011 The Kuali Foundation
--
-- Licensed under the Educational Community License, Version 2.0 (the "License");
-- you may not use this file except in compliance with the License.
-- You may obtain a copy of the License at
--
-- http://www.opensource.org/licenses/ecl2.php
--
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed on an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.
--

-- This script is part of the effort to clean up the Rice database prior to packaging for public release

-- IMPORTANT: execute the following before running the demo-server-dataset-cleanup.sql


-- KULRICE-3435
update krim_perm_t set desc_txt = 'Allows users to access the XML Ingester screen.' where perm_id=265 and nmspc_cd='KR-WKFLW' and NM='Use Screen'
/
update krim_perm_t set desc_txt = 'Allows users to access the Document Operation screen.' where perm_id=140 and nmspc_cd='KR-WKFLW' and NM='Use Screen'
/

