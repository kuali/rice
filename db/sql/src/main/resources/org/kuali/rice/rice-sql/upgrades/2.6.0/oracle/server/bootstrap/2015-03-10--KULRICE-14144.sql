-- KULRICE-14144 changes for SpringFramework 4.1.4 with Quartz 2.2.1
--
--
-- drop tables that are no longer used
--
--     drop table krsb_qrtz_job_listeners/
--     drop table krsb_qrtz_trigger_listeners/
--
-- drop constraints instead of drop tables
--
alter table krsb_qrtz_job_listeners drop constraint   KRSB_QUARTZ_JOB_LISTENERS_TR1
/
alter table krsb_qrtz_trigger_listeners drop constraint   KRSB_QRTZ_TRIGGER_LISTENE_TR1
/
--
-- drop columns that are no longer used
--
alter table krsb_qrtz_job_details drop column is_volatile
/
alter table krsb_qrtz_triggers drop column is_volatile
/
alter table krsb_qrtz_fired_triggers drop column is_volatile
/
--
-- add new columns that replace the 'is_stateful' column
--
alter table krsb_qrtz_job_details add is_nonconcurrent VARCHAR2(1)
/
alter table krsb_qrtz_job_details add is_update_data VARCHAR2(1)
/
update krsb_qrtz_job_details set is_nonconcurrent = is_stateful
/
update krsb_qrtz_job_details set is_update_data = is_stateful
/
alter table krsb_qrtz_job_details drop column is_stateful
/
alter table krsb_qrtz_fired_triggers add is_nonconcurrent VARCHAR2(1)
/
alter table krsb_qrtz_fired_triggers add is_update_data VARCHAR2(1)
/
update krsb_qrtz_fired_triggers set is_nonconcurrent = is_stateful
/
update krsb_qrtz_fired_triggers set is_update_data = is_stateful
/
alter table krsb_qrtz_fired_triggers drop column is_stateful
/
--
-- add new 'sched_name' column to all tables
--
alter table krsb_qrtz_blob_triggers add sched_name varchar(120) DEFAULT 'KrTestScheduler' not null
/
alter table krsb_qrtz_calendars add sched_name varchar(120) DEFAULT 'KrTestScheduler' not null
/
alter table krsb_qrtz_cron_triggers add sched_name varchar(120) DEFAULT 'KrTestScheduler' not null
/
alter table krsb_qrtz_fired_triggers add sched_name varchar(120)  DEFAULT 'KrTestScheduler' not null
/
alter table krsb_qrtz_fired_triggers add sched_time number(13) NOT NULL
/
alter table krsb_qrtz_job_details add sched_name varchar(120)  DEFAULT 'KrTestScheduler' not null
/
alter table krsb_qrtz_locks add sched_name varchar(120) DEFAULT 'KrTestScheduler' not null
/
alter table krsb_qrtz_paused_trigger_grps add sched_name varchar(120)  DEFAULT 'KrTestScheduler' not null
/
alter table krsb_qrtz_scheduler_state add sched_name varchar(120)  DEFAULT 'KrTestScheduler' not null
/
alter table krsb_qrtz_simple_triggers add sched_name varchar(120)  DEFAULT 'KrTestScheduler' not null
/
alter table krsb_qrtz_triggers add sched_name varchar(120) DEFAULT 'KrTestScheduler' not null
/
--
-- drop all primary and foreign key constraints, so that we can define new ones
--
alter table krsb_qrtz_blob_triggers drop constraint KRSB_QRTZ_BLOB_TRIGGERSP1
/
alter table krsb_qrtz_blob_triggers drop constraint KRSB_QRTZ_BLOB_TRIGGERS_TR1
/
alter table krsb_qrtz_simple_triggers drop constraint KRSB_QRTZ_SIMPLE_TRIGGERSP1
/
alter table krsb_qrtz_simple_triggers drop constraint KRSB_QRTZ_SIMPLE_TRIGGERS_TR1
/
alter table krsb_qrtz_cron_triggers drop constraint KRSB_QRTZ_CRON_TRIGGERSP1
/
alter table krsb_qrtz_cron_triggers drop constraint KRSB_QRTZ_CRON_TRIGGERS_TR1
/
alter table krsb_qrtz_job_details drop constraint KRSB_QRTZ_JOB_DETAILSP1
/
alter table krsb_qrtz_triggers drop constraint KRSB_QRTZ_TRIGGERSP1
/
--
--
-- add all primary and foreign key constraints, based on new columns
--
alter table krsb_qrtz_job_details add constraint KRSB_QRTZ_JOB_DETAILSP1 primary key (job_name, job_group, sched_name)
/
alter table krsb_qrtz_triggers add constraint KRSB_QRTZ_TRIGGERSP1 primary key (trigger_name, trigger_group, sched_name)
/
alter table krsb_qrtz_triggers add constraint KRSB_QRTZ_TRIGGERS_TR1 foreign key (job_name, job_group, sched_name) references krsb_qrtz_job_details(job_name, job_group, sched_name)
/
alter table krsb_qrtz_blob_triggers add constraint KRSB_QRTZ_BLOB_TRIGGERSP1 primary key (trigger_name, trigger_group, sched_name)
/
alter table krsb_qrtz_blob_triggers add constraint KRSB_QRTZ_BLOB_TRIGGERS_TR1 foreign key (trigger_name, trigger_group, sched_name) references krsb_qrtz_triggers(trigger_name, trigger_group, sched_name)
/
alter table krsb_qrtz_cron_triggers add constraint KRSB_QRTZ_CRON_TRIGGERSP1 primary key (trigger_name, trigger_group, sched_name)
/
alter table krsb_qrtz_cron_triggers add constraint KRSB_QRTZ_CRON_TRIGGERS_TR1 foreign key (trigger_name, trigger_group, sched_name) references krsb_qrtz_triggers(trigger_name, trigger_group, sched_name)
/
alter table krsb_qrtz_simple_triggers add constraint KRSB_QRTZ_SIMPLE_TRIGGERSP1 primary key (trigger_name, trigger_group, sched_name)
/
alter table krsb_qrtz_simple_triggers add constraint KRSB_QRTZ_SIMPLE_TRIGGERS_TR1 foreign key (trigger_name, trigger_group, sched_name) references krsb_qrtz_triggers(trigger_name, trigger_group, sched_name)
/
alter table krsb_qrtz_fired_triggers drop constraint KRSB_QRTZ_FIRED_TRIGGERSP1
/
alter table krsb_qrtz_fired_triggers add constraint KRSB_QRTZ_FIRED_TRIGGERSP1 primary key (entry_id, sched_name)
/
alter table krsb_qrtz_calendars drop constraint KRSB_QRTZ_CALENDARSP1
/
alter table krsb_qrtz_calendars add constraint KRSB_QRTZ_CALENDARSP1 primary key (calendar_name, sched_name)
/
alter table krsb_qrtz_locks drop constraint KRSB_QRTZ_LOCKSP1
/
alter table krsb_qrtz_locks add constraint KRSB_QRTZ_LOCKSP1 primary key (lock_name, sched_name)
/
alter table krsb_qrtz_paused_trigger_grps drop constraint KRSB_QRTZ_PAUSED_TRIGGER_GRP1
/
alter table krsb_qrtz_paused_trigger_grps add constraint KRSB_QRTZ_PAUSED_TRIGGER_GRP1 primary key (trigger_group, sched_name)
/
alter table krsb_qrtz_scheduler_state drop constraint KRSB_QRTZ_SCHEDULER_STATEP1
/
alter table krsb_qrtz_scheduler_state add constraint KRSB_QRTZ_SCHEDULER_STATEP1 primary key (instance_name, sched_name)
/
--
-- add new simprop_triggers table
--
CREATE TABLE krsb_qrtz_simprop_triggers
  (          
    SCHED_NAME VARCHAR2(120) NOT NULL,
    TRIGGER_NAME VARCHAR2(200) NOT NULL,
    TRIGGER_GROUP VARCHAR2(200) NOT NULL,
    STR_PROP_1 VARCHAR2(512) NULL,
    STR_PROP_2 VARCHAR2(512) NULL,
    STR_PROP_3 VARCHAR2(512) NULL,
    INT_PROP_1 NUMBER(10) NULL,
    INT_PROP_2 NUMBER(10) NULL,
    LONG_PROP_1 NUMBER(13) NULL,
    LONG_PROP_2 NUMBER(13) NULL,
    DEC_PROP_1 NUMERIC(13,4) NULL,
    DEC_PROP_2 NUMERIC(13,4) NULL,
    BOOL_PROP_1 VARCHAR2(1) NULL,
    BOOL_PROP_2 VARCHAR2(1) NULL,
    constraint KRSB_QRTZ_SIMPROP_TRIGGERSP1 primary key (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
    constraint KRSB_QRTZ_SIMPROP_TRIGGERS_TR1 foreign key (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP) 
    references KRSB_QRTZ_TRIGGERS(TRIGGER_NAME,TRIGGER_GROUP, SCHED_NAME)
)
/
