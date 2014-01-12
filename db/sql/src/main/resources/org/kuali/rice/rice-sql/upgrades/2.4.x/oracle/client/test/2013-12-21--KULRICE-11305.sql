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

CREATE TABLE KRTST_TEST_DISABLE_NO_VER_T (
    ID              VARCHAR2(10),
    OBJ_ID      VARCHAR2(36) NOT NULL,
    STR_PROP	      VARCHAR2(40),
    PRIMARY KEY(ID)
)
/
CREATE TABLE KRTST_TEST_DISABLE_VER_T (
    ID              VARCHAR2(10),
    OBJ_ID      VARCHAR2(36) NOT NULL,
    STR_PROP	      VARCHAR2(40),
    VER_NBR    	    NUMBER(8,0) DEFAULT 1 NOT NULL,
    PRIMARY KEY(ID)
)
/
