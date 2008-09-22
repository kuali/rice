alter table kcb_msg_delivs modify DELIVERER_SYSTEM_ID varchar2(255)
/
alter table kcb_recip_delivs modify RECIPIENT_ID varchar2(255)
/
alter table kcb_recip_delivs modify CHANNEL varchar2(255)
/
alter table kcb_recip_delivs modify DELIVERER_NAME varchar2(255)
/
alter table KCB_RECIP_PREFS modify RECIPIENT_ID varchar2(255)
/
alter table KCB_RECIP_PREFS modify PROPERTY varchar2(255)
/
alter table KCB_RECIP_PREFS modify VALUE varchar2(255)
/