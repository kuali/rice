create index idx_kr_qrtz_t_next_fire_time on kr_qrtz_triggers(NEXT_FIRE_TIME)
/
create index idx_kr_qrtz_t_state on kr_qrtz_triggers(TRIGGER_STATE)
/
create index idx_kr_qrtz_t_nft_st on kr_qrtz_triggers(NEXT_FIRE_TIME,TRIGGER_STATE)
/
create index idx_kr_qrtz_t_volatile on kr_qrtz_triggers(IS_VOLATILE)
/