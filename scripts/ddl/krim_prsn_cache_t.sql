-- 
-- Copyright 2009 The Kuali Foundation
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
CREATE TABLE krim_prsn_cache_t (
	prncpl_id		VARCHAR(40),
	prncpl_nm		VARCHAR(40),
	entity_id		VARCHAR(40),
	entity_typ_cd		VARCHAR(40),
	first_nm		VARCHAR(40),
	middle_nm		VARCHAR(40),
	last_nm		VARCHAR(40),
	prsn_nm		VARCHAR(40),
	campus_cd		VARCHAR(40),
	prmry_dept_cd		VARCHAR(40),
	emp_id		VARCHAR(40),
	last_updt_ts		TIMESTAMP,
	obj_id		VARCHAR2(36) NOT NULL,
	CONSTRAINT krim_prsn_cache_tp1 PRIMARY KEY ( prncpl_id )
)
/
