--
-- Copyright 2005-2017 The Kuali Foundation
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
-- KULRICE-9288  - Column 'SESN_ID' cannot be null causes issues
--

UPDATE KRNS_PESSIMISTIC_LOCK_T SET SESN_ID = 'undefined' WHERE SESN_ID IS NULL OR SESN_ID = '';

ALTER TABLE KRNS_PESSIMISTIC_LOCK_T ALTER COLUMN SESN_ID SET DEFAULT 'undefined';



