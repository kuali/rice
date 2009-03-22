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

-- !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
-- !!! STOP!  Don't put anymore SQL in this file for Rice 1.0. Instead, create files in the !!!
-- !!! 'scripts/upgrades/0.9.3 to 0.9.4/db-updates-during-qa' directory                     !!!
-- !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
