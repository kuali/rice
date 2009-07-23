-- 
-- Copyright 2008-2009 The Kuali Foundation
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
CREATE TABLE EN_ATTACHMENT_T (
	ATTACHMENT_ID				   NUMBER(19) NOT NULL,
	NTE_ID	            	   	   NUMBER(19) NOT NULL,
	FILE_NM			               VARCHAR2(255) NOT NULL,
	FILE_LOC					   VARCHAR2(255) NOT NULL,
	MIME_TYP					   VARCHAR2(255) NOT NULL,
	DB_LOCK_VER_NBR	               NUMBER(8) DEFAULT 0,
	CONSTRAINT EN_ATTACHMENT_T_PK PRIMARY KEY (ATTACHMENT_ID)
);

ALTER TABLE EN_DOC_TYP_T ADD DOC_TYP_NOTIFY_ADDR VARCHAR2(255);
