create index idx_kr_qrtz_ft_trig_name on kr_qrtz_fired_triggers(TRIGGER_NAME)
/
create index idx_kr_qrtz_ft_trig_group on kr_qrtz_fired_triggers(TRIGGER_GROUP)
/
create index idx_kr_qrtz_ft_trig_nm_gp on kr_qrtz_fired_triggers(TRIGGER_NAME,TRIGGER_GROUP)
/
create index idx_kr_qrtz_ft_trig_volatile on kr_qrtz_fired_triggers(IS_VOLATILE)
/
create index idx_kr_qrtz_ft_trig_inst_name on kr_qrtz_fired_triggers(INSTANCE_NAME)
/
create index idx_kr_qrtz_ft_job_name on kr_qrtz_fired_triggers(JOB_NAME)
/
create index idx_kr_qrtz_ft_job_group on kr_qrtz_fired_triggers(JOB_GROUP)
/
create index idx_kr_qrtz_ft_job_stateful on kr_qrtz_fired_triggers(IS_STATEFUL)
/
create index idx_kr_qrtz_ft_job_req_recov on kr_qrtz_fired_triggers(REQUESTS_RECOVERY)
/