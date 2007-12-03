-- This should be run on 9/15/2005 --;

CREATE INDEX EN_ACTN_RQST_TI6
 ON EN_ACTN_RQST_T (ACTN_RQST_STAT_CD, ACTN_RQST_RESP_ID);
 
insert into EN_APPL_CNST_T values ('RouteQueue.requeueWaitTime', '20000', 0);