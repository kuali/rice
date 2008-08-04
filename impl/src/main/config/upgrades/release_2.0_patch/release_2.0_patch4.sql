delete from en_actn_itm_t ai where ai.actn_rqst_id in
(select ar.actn_rqst_id from en_actn_rqst_t ar where ar.actn_rqst_stat_cd='D');