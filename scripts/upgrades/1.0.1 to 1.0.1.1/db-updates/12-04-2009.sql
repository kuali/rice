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

DELETE FROM KRSB_SVC_DEF_T
/
ALTER TABLE KRSB_SVC_DEF_T DROP (SVC_DEF)
/
ALTER TABLE KRSB_SVC_DEF_T ADD (
	FLT_SVC_DEF_ID NUMBER(14, 0) NOT NULL,
	SVC_DEF_CHKSM VARCHAR2(30) NOT NULL
)
/
CREATE UNIQUE INDEX KRSB_SVC_DEF_TI2 ON KRSB_SVC_DEF_T (FLT_SVC_DEF_ID)
/
CREATE TABLE KRSB_FLT_SVC_DEF_T (
	FLT_SVC_DEF_ID NUMBER(14, 0),
	FLT_SVC_DEF CLOB NOT NULL,
	CONSTRAINT KRSB_FLT_SVC_DEF_TP1 PRIMARY KEY (FLT_SVC_DEF_ID)
)
/
CREATE SEQUENCE KRSB_FLT_SVC_DEF_S START WITH 1000 INCREMENT BY 1
