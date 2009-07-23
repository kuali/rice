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
CREATE TABLE EN_DOC_HDR_CNTNT_T (
	DOC_HDR_ID 			    NUMBER(14) NOT NULL,
	DOC_CNTNT_TXT			CLOB NULL,
	CONSTRAINT EN_DOC_HDR_T_CNTNT_PK PRIMARY KEY (DOC_HDR_ID)
);

INSERT INTO EN_DOC_HDR_CNTNT_T SELECT DOC_HDR_ID, TO_LOB(DOC_CNTNT_TXT) FROM EN_DOC_HDR_T;

ALTER TABLE EN_RULE_ATTRIB_T MODIFY ( RULE_ATTRIB_XML_RTE_TXT CLOB );

ALTER INDEX EN_RULE_ATTRIB_PK REBUILD;

ALTER TABLE EN_MESSAGE_QUE_T MODIFY ( MESSAGE_PAYLOAD CLOB );

ALTER INDEX EN_MESSAGE_QUE_T_PK REBUILD;

ALTER TABLE EN_BAM_T MODIFY ( EXCEPTION_MSG CLOB );

ALTER INDEX EN_BAM_T_PK REBUILD;

ALTER TABLE EN_BAM_PARAM_T MODIFY ( PARAM CLOB );

ALTER INDEX EN_BAM_PARAM_T_PK REBUILD;

ALTER TABLE EN_RTE_BRCH_T MODIFY ( INIT_RTE_NODE_INSTN_ID NUMBER(19) NULL );
