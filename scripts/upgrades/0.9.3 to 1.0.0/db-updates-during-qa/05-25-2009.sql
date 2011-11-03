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

ALTER TABLE KRIM_DLGN_MBR_T DROP CONSTRAINT KRIM_DLGN_MBR_TR2
/
commit
/
ALTER TABLE KRIM_DLGN_MBR_T
    ADD CONSTRAINT KRIM_DLGN_MBR_TR2 FOREIGN KEY (DLGN_ID)
    REFERENCES KRIM_DLGN_T (DLGN_ID)
/    
