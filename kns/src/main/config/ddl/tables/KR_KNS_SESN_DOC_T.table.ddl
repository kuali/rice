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
CREATE TABLE KR_KNS_SESN_DOC_T (
        SESSION_ID                     VARCHAR2(40) CONSTRAINT KR_KNS_SESN_DOC_TN1 NOT NULL,
        FDOC_NBR                       VARCHAR2(14) CONSTRAINT KR_KNS_SESN_DOC_TN2 NOT NULL,
        SERIALIZED_DOC_FRM             BLOB,
        LST_UPDATE_DT                  DATE,
     CONSTRAINT KR_KNS_SESN_DOC_TP1 PRIMARY KEY (SESSION_ID, FDOC_NBR) 
)

/
