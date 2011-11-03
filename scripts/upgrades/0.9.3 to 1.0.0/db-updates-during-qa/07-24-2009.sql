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

-- KULRICE-3376
ALTER TABLE KRIM_ENTITY_ADDR_T MODIFY
  (
    ADDR_LINE_1 VARCHAR2(45),
    ADDR_LINE_2 VARCHAR2(45),
    ADDR_LINE_3 VARCHAR2(45)
  )
/


