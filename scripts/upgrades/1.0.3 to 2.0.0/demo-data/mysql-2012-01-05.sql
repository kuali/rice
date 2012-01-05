--
-- Copyright 2005-2012 The Kuali Foundation
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

--
-- Adds some permissions and a role for testing KIM authorization in the sample app
--

INSERT INTO KRIM_ROLE_T VALUES ('10003', uuid(), 1, 'Sample App Admin', 'KR-SAP', 'Test role for the sample app', '1', 'Y', now());


