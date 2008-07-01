/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
CREATE TABLE FP_DOC_TYPE_ATTR_T (
        ID                             NUMBER(8) CONSTRAINT FP_DOC_TYPE_ATTR_TN1 NOT NULL,
        OBJ_ID                         VARCHAR2(36) DEFAULT SYS_GUID() CONSTRAINT FP_DOC_TYPE_ATTR_TN3 NOT NULL,
        VER_NBR                        NUMBER(8) DEFAULT 1 CONSTRAINT FP_DOC_TYPE_ATTR_TN4 NOT NULL,
        ACTIVE_IND                     CHAR(1) DEFAULT 'Y' CONSTRAINT FP_DOC_TYPE_ATTR_TN5 NOT NULL,
        DOC_TYP_ATTR_CD                VARCHAR2(100) CONSTRAINT FP_DOC_TYPE_ATTR_TN2 NOT NULL,
        DOC_TYP_ATTR_VAL               VARCHAR2(400),
        DOC_TYP_ATTR_LBL               VARCHAR2(400),
        FDOC_TYP_CD                    VARCHAR2(4) CONSTRAINT FP_DOC_TYPE_ATTR_TN6 NOT NULL,
     CONSTRAINT FP_DOC_TYPE_ATTR_TP1 PRIMARY KEY (
        ID) ,
     CONSTRAINT FP_DOC_TYPE_ATTR_TC0 UNIQUE (OBJ_ID) 
)
/
