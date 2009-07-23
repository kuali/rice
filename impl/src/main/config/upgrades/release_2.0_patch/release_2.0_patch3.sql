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
CREATE INDEX EN_RULE_RSP_TI1 ON EN_RULE_RSP_T (RULE_BASE_VAL_ID);
CREATE INDEX EN_DOC_TYP_ATTRIB_TI1 ON EN_DOC_TYP_ATTRIB_T (DOC_TYP_ID);
CREATE INDEX EN_ACTN_RQST_TI6 ON EN_ACTN_RQST_T (ACTN_RQST_STAT_CD, ACTN_RQST_RESP_ID);
