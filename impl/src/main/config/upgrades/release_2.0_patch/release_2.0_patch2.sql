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
-- This should be run on 9/15/2005 --;

CREATE INDEX EN_ACTN_RQST_TI6
 ON EN_ACTN_RQST_T (ACTN_RQST_STAT_CD, ACTN_RQST_RESP_ID);
 
insert into EN_APPL_CNST_T values ('RouteQueue.requeueWaitTime', '20000', 0);
