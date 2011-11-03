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

CREATE TABLE KREW_RIA_DOC_T (
	RIA_ID DECIMAL NOT NULL,
    XML_CONTENT VARCHAR(4000),
    RIA_DOC_TYPE_NAME VARCHAR(100),
    VER_NBR DECIMAL(8) DEFAULT 1 NOT NULL,
	OBJ_ID VARCHAR(36) NOT NULL, 
    CONSTRAINT KREW_RIA_DOC_PK PRIMARY KEY (RIA_ID)
)
/
CREATE TABLE KREW_RIA_DOCTYPE_MAP_T (
	ID DECIMAL NOT NULL,
	RIA_DOC_TYPE_NAME VARCHAR(100),
 	UPDATED_AT DATETIME,
 	RIA_URL VARCHAR(255),
 	HELP_URL VARCHAR(255),
 	EDITABLE CHAR,
	INIT_GROUPS VARCHAR(255),
  	VER_NBR DECIMAL(8) DEFAULT 1 NOT NULL,	
  	OBJ_ID VARCHAR(36) NOT NULL, 
    CONSTRAINT KREW_RIA_DOCTYPE_MAP_PK PRIMARY KEY (ID)
)
/
CREATE TABLE KREW_RIA_DOCTYPE_MAP_ID_S
(
	id bigint(19) not null auto_increment, primary key (id) 
)
/
ALTER TABLE KREW_RIA_DOCTYPE_MAP_ID_S auto_increment = 1000;
/
