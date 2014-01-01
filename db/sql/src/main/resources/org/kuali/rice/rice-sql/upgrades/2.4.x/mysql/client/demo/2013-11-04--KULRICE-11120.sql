--
-- Copyright 2005-2014 The Kuali Foundation
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

-- KULRICE-11120: samples for I12
-- adding an inactive travel company for the show / hide inactive demonstration

INSERT INTO TRVL_CO_ID_S VALUES ('0')
/

INSERT INTO TRVL_CO_T (CO_ID, CO_NM,OBJ_ID,VER_NBR, ACTV_IND ) VALUES (last_insert_id(), 'AAA Travel', uuid(),1,'N')
/


