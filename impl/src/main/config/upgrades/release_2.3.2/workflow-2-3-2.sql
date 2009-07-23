/*
 * Copyright 2008-2009 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
CREATE TABLE EN_SERVICE_DEF_DUEX_T (
	SERVICE_DEF_ID			   	   NUMBER(14) NOT NULL,
	SERVICE_NM				   	   VARCHAR2(255) NOT NULL,
    SERVICE_URL	            	   VARCHAR2(500) NOT NULL,
	SERVER_IP					   VARCHAR2(40) NOT NULL,
	MESSAGE_ENTITY_NM 		  	   VARCHAR2(10) NOT NULL,
	SERVICE_ALIVE				   NUMBER(1) NOT NULL,
	SERVICE_DEFINITION 		       CLOB NOT NULL,
	DB_LOCK_VER_NBR	               NUMBER(8) DEFAULT 0,
	CONSTRAINT EN_SERVICE_DEF_DUEX_T_PK PRIMARY KEY (SERVICE_DEF_ID)
)

/

CREATE INDEX EN_SERVICE_DEF_DUEX_TI1 ON EN_SERVICE_DEF_DUEX_T (SERVER_IP, MESSAGE_ENTITY_NM)

/

