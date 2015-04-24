--
-- Copyright 2005-2015 The Kuali Foundation
--
-- Licensed under the Educational Community License, Version 2.0 (the "License")/
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
-- KULRICE-14144 changes for SpringFramework 4.1.4 with Quartz 2.2.1
--
-- drop constraints of tables that are no longer used
--
--  drop table krsb_qrtz_job_listeners
-- /
--  drop table krsb_qrtz_trigger_listeners
-- /
alter table krsb_qrtz_job_listeners drop foreign key KRSB_QUARTZ_JOB_LISTENERS_TR1
/
alter table krsb_qrtz_trigger_listeners drop foreign key KRSB_QRTZ_TRIGGER_LISTENE_TR1
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
alter table krsb_qrtz_job_details add column is_nonconcurrent bool
/
alter table krsb_qrtz_job_details add column is_update_data bool
/
update krsb_qrtz_job_details set is_nonconcurrent = is_stateful
/
update krsb_qrtz_job_details set is_update_data = is_stateful
/
alter table krsb_qrtz_job_details drop column is_stateful
/
alter table krsb_qrtz_fired_triggers add column is_nonconcurrent bool
/
alter table krsb_qrtz_fired_triggers add column is_update_data bool
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
alter table krsb_qrtz_blob_triggers add column sched_name varchar(120) not null DEFAULT 'KrTestScheduler'
/
alter table krsb_qrtz_calendars add column sched_name varchar(120) not null DEFAULT 'KrTestScheduler'
/
alter table krsb_qrtz_cron_triggers add column sched_name varchar(120) not null DEFAULT 'KrTestScheduler'
/
alter table krsb_qrtz_fired_triggers add column sched_name varchar(120) not null DEFAULT 'KrTestScheduler'
/
alter table krsb_qrtz_job_details add column sched_name varchar(120) not null DEFAULT 'KrTestScheduler'
/
alter table krsb_qrtz_locks add column sched_name varchar(120) not null DEFAULT 'KrTestScheduler'
/
alter table krsb_qrtz_paused_trigger_grps add column sched_name varchar(120) not null DEFAULT 'KrTestScheduler'
/
alter table krsb_qrtz_scheduler_state add column sched_name varchar(120) not null DEFAULT 'KrTestScheduler'
/
alter table krsb_qrtz_simple_triggers add column sched_name varchar(120) not null DEFAULT 'KrTestScheduler'
/
alter table krsb_qrtz_triggers add column sched_name varchar(120) not null DEFAULT 'KrTestScheduler'
/

--
-- drop all primary and foreign key constraints, so that we can define new ones
--
 alter table krsb_qrtz_blob_triggers  drop foreign key KRSB_QRTZ_BLOB_TRIGGERS_TR1
/
 alter table krsb_qrtz_simple_triggers drop foreign key KRSB_QRTZ_SIMPLE_TRIGGERS_TR1
/
 alter table krsb_qrtz_cron_triggers drop foreign key KRSB_QRTZ_CRON_TRIGGERS_TR1
/
 alter table krsb_qrtz_job_details drop primary key, add primary key (job_name, job_group, sched_name)
/

--
-- add all primary and foreign key constraint s, based on new columns
--
alter table krsb_qrtz_triggers drop primary key, add primary key (trigger_name, trigger_group, sched_name)
/
alter table krsb_qrtz_blob_triggers drop primary key, add primary key (trigger_name, trigger_group, sched_name)
/
alter table KRSB_QRTZ_BLOB_TRIGGERS add  constraint  KRSB_QRTZ_BLOB_TRIGGERS_TR1 foreign key(trigger_name, trigger_group, sched_name) references KRSB_QRTZ_TRIGGERS (trigger_name, trigger_group, sched_name)
/
alter table krsb_qrtz_cron_triggers drop primary key, add primary key (trigger_name, trigger_group, sched_name)
/
alter table krsb_qrtz_cron_triggers add  constraint  KRSB_QRTZ_CRON_TRIGGERS_TR1 foreign key(trigger_name, trigger_group, sched_name) references krsb_qrtz_triggers(trigger_name, trigger_group, sched_name)
/
alter table krsb_qrtz_simple_triggers drop primary key, add primary key (trigger_name, trigger_group, sched_name)
/
alter table krsb_qrtz_simple_triggers add  constraint  KRSB_QRTZ_SIMPLE_TRIGGERS_TR1 foreign key(trigger_name, trigger_group, sched_name) references krsb_qrtz_triggers(trigger_name, trigger_group, sched_name)
/
alter table krsb_qrtz_fired_triggers drop primary key, add primary key (entry_id, sched_name)
/
alter table krsb_qrtz_calendars drop primary key, add primary key (calendar_name, sched_name)
/
alter table krsb_qrtz_locks drop primary key, add primary key (lock_name, sched_name)
/
alter table krsb_qrtz_paused_trigger_grps drop primary key, add primary key (trigger_group, sched_name)
/
alter table krsb_qrtz_scheduler_state drop primary key, add primary key (instance_name, sched_name)
/
--
-- add new simprop_triggers table
--
CREATE TABLE krsb_qrtz_simprop_triggers
 (
    SCHED_NAME VARCHAR(120) NOT NULL,
    TRIGGER_NAME VARCHAR(200) NOT NULL,
    TRIGGER_GROUP VARCHAR(200) NOT NULL,
    STR_PROP_1 VARCHAR(512) NULL,
    STR_PROP_2 VARCHAR(512) NULL,
    STR_PROP_3 VARCHAR(512) NULL,
    INT_PROP_1 INT NULL,
    INT_PROP_2 INT NULL,
    LONG_PROP_1 BIGINT NULL,
    LONG_PROP_2 BIGINT NULL,
    DEC_PROP_1 NUMERIC(13,4) NULL,
    DEC_PROP_2 NUMERIC(13,4) NULL,
    BOOL_PROP_1 BOOL NULL,
    BOOL_PROP_2 BOOL NULL,
    primary key (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
    constraint KRSB_QRTZ_SIMPROP_TRIGGERS_TR1 foreign key(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
    references krsb_QRTZ_TRIGGERS(TRIGGER_NAME,TRIGGER_GROUP,SCHED_NAME)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin
/
 
