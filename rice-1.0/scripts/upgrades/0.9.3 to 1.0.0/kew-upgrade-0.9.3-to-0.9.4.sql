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
alter table "EN_DOC_HDR_T" drop column "DOC_OVRD_IND"
/
alter table "EN_DOC_HDR_T" drop column "DOC_LOCK_CD"
/
alter table "EN_RTE_NODE_T" drop column "CONTENT_FRAGMENT"
/
alter table "EN_DOC_HDR_T" drop column "DTYPE"
/
alter table "EN_ACTN_ITM_T" drop column "DTYPE"
/
alter table "EN_USR_T" drop column "DTYPE"
/
alter table "EN_DOC_TYP_T" drop column "CSTM_ACTN_LIST_ATTRIB_CLS_NM"
/
alter table "EN_DOC_TYP_T" drop column "CSTM_ACTN_EMAIL_ATTRIB_CLS_NM"
/
alter table "EN_DOC_TYP_T" drop column "CSTM_DOC_NTE_ATTRIB_CLS_NM"
/

-- Convert KEW group ids from numbers to strings /

CREATE TABLE KRTMP_DOC_TYP_T (
    DOC_TYP_ID NUMBER(19) NOT NULL,
    GRP_ID VARCHAR2(40),
    BLNKT_APPR_GRP_ID VARCHAR2(40),
    RPT_GRP_ID VARCHAR2(40))
/
INSERT INTO KRTMP_DOC_TYP_T
SELECT DOC_TYP_ID, GRP_ID, BLNKT_APPR_GRP_ID, RPT_GRP_ID
FROM KREW_DOC_TYP_T
/

ALTER TABLE KREW_DOC_TYP_T DROP COLUMN BLNKT_APPR_GRP_ID
/
ALTER TABLE KREW_DOC_TYP_T ADD BLNKT_APPR_GRP_ID VARCHAR2(40)
/

ALTER TABLE KREW_DOC_TYP_T DROP COLUMN RPT_GRP_ID
/
ALTER TABLE KREW_DOC_TYP_T ADD RPT_GRP_ID VARCHAR2(40)
/

ALTER TABLE KREW_DOC_TYP_T DROP COLUMN GRP_ID
/
ALTER TABLE KREW_DOC_TYP_T ADD GRP_ID VARCHAR2(40)
/

UPDATE KREW_DOC_TYP_T a SET a.BLNKT_APPR_GRP_ID = (select b.BLNKT_APPR_GRP_ID from KRTMP_DOC_TYP_T b where b.DOC_TYP_ID=a.DOC_TYP_ID)
/
UPDATE KREW_DOC_TYP_T a SET a.RPT_GRP_ID = (select b.RPT_GRP_ID from KRTMP_DOC_TYP_T b where b.DOC_TYP_ID=a.DOC_TYP_ID)
/
UPDATE KREW_DOC_TYP_T a SET a.GRP_ID = (select b.GRP_ID from KRTMP_DOC_TYP_T b where b.DOC_TYP_ID=a.DOC_TYP_ID)
/

DROP TABLE KRTMP_DOC_TYP_T
/

CREATE TABLE KRTMP_RTE_NODE_T (
    RTE_NODE_ID NUMBER(19) NOT NULL,
    GRP_ID VARCHAR2(40))
/

INSERT INTO KRTMP_RTE_NODE_T
SELECT RTE_NODE_ID, GRP_ID
FROM KREW_RTE_NODE_T
/

ALTER TABLE KREW_RTE_NODE_T DROP COLUMN GRP_ID
/
ALTER TABLE KREW_RTE_NODE_T ADD GRP_ID VARCHAR2(40)
/

UPDATE KREW_RTE_NODE_T a SET a.GRP_ID = (select b.GRP_ID from KRTMP_RTE_NODE_T b where b.RTE_NODE_ID=a.RTE_NODE_ID)
/

DROP TABLE KRTMP_RTE_NODE_T
/

-- Convert Action Request group ids to varchar /

CREATE TABLE KRTMP_ACTN_RQST_T (
    ACTN_RQST_ID NUMBER(19) NOT NULL,
    GRP_ID VARCHAR2(40))
/
INSERT INTO KRTMP_ACTN_RQST_T
SELECT ACTN_RQST_ID, GRP_ID
FROM KREW_ACTN_RQST_T
/

ALTER TABLE KREW_ACTN_RQST_T DROP COLUMN GRP_ID
/
ALTER TABLE KREW_ACTN_RQST_T ADD GRP_ID VARCHAR2(40)
/

UPDATE KREW_ACTN_RQST_T a SET a.GRP_ID = (select b.GRP_ID from KRTMP_ACTN_RQST_T b where b.ACTN_RQST_ID=a.ACTN_RQST_ID)
/

DROP TABLE KRTMP_ACTN_RQST_T
/

-- Convert Action Taken group ids to varchar /

CREATE TABLE KRTMP_ACTN_TKN_T (
    ACTN_TKN_ID NUMBER(19) NOT NULL,
    DLGTR_GRP_ID VARCHAR2(40))
/
INSERT INTO KRTMP_ACTN_TKN_T
SELECT ACTN_TKN_ID, DLGTR_GRP_ID
FROM KREW_ACTN_TKN_T
/

ALTER TABLE KREW_ACTN_TKN_T DROP COLUMN DLGTR_GRP_ID
/
ALTER TABLE KREW_ACTN_TKN_T ADD DLGTR_GRP_ID VARCHAR2(40)
/

UPDATE KREW_ACTN_TKN_T a SET a.DLGTR_GRP_ID = (select b.DLGTR_GRP_ID from KRTMP_ACTN_TKN_T b where b.ACTN_TKN_ID=a.ACTN_TKN_ID)
/

DROP TABLE KRTMP_ACTN_TKN_T
/
-- Convert Action Item group ids to varchar /

CREATE TABLE KRTMP_ACTN_ITM_T (
    ACTN_ITM_ID NUMBER(19) NOT NULL,
    GRP_ID VARCHAR2(40),
	DLGN_GRP_ID VARCHAR2(40))
/
INSERT INTO KRTMP_ACTN_ITM_T
SELECT ACTN_ITM_ID, GRP_ID, DLGN_GRP_ID
FROM KREW_ACTN_ITM_T
/

ALTER TABLE KREW_ACTN_ITM_T DROP COLUMN GRP_ID
/
ALTER TABLE KREW_ACTN_ITM_T ADD GRP_ID VARCHAR2(40)
/

ALTER TABLE KREW_ACTN_ITM_T DROP COLUMN DLGN_GRP_ID
/
ALTER TABLE KREW_ACTN_ITM_T ADD DLGN_GRP_ID VARCHAR2(40)
/

UPDATE KREW_ACTN_ITM_T a SET a.GRP_ID = (select b.GRP_ID from KRTMP_ACTN_ITM_T b where b.ACTN_ITM_ID=a.ACTN_ITM_ID)
/

DROP TABLE KRTMP_ACTN_ITM_T
/

-- Convert Action Item Outbox group ids to varchar /

CREATE TABLE KRTMP_OUT_BOX_ITM_T (
    ACTN_ITM_ID NUMBER(19) NOT NULL,
    GRP_ID VARCHAR2(40),
	DLGN_GRP_ID VARCHAR2(40))
/
INSERT INTO KRTMP_OUT_BOX_ITM_T
SELECT ACTN_ITM_ID, GRP_ID, DLGN_GRP_ID
FROM KREW_OUT_BOX_ITM_T
/

ALTER TABLE KREW_OUT_BOX_ITM_T DROP COLUMN GRP_ID
/
ALTER TABLE KREW_OUT_BOX_ITM_T ADD GRP_ID VARCHAR2(40)
/

ALTER TABLE KREW_OUT_BOX_ITM_T DROP COLUMN DLGN_GRP_ID
/
ALTER TABLE KREW_OUT_BOX_ITM_T ADD DLGN_GRP_ID VARCHAR2(40)
/

UPDATE KREW_OUT_BOX_ITM_T a SET a.GRP_ID = (select b.GRP_ID from KRTMP_OUT_BOX_ITM_T b where b.ACTN_ITM_ID=a.ACTN_ITM_ID)
/

DROP TABLE KRTMP_OUT_BOX_ITM_T
/



-- New System Parameters /

INSERT INTO KRNS_NMSPC_T ("NMSPC_CD","VER_NBR","NM","ACTV_IND") VALUES ('KR-WKFLW', 0,'Workflow','Y')
/

INSERT INTO KRNS_PARM_T ("NMSPC_CD","PARM_DTL_TYP_CD","PARM_NM","TXT","CONS_CD","PARM_DESC_TXT","PARM_TYP_CD","GRP_NM") VALUES ('KR-WKFLW','All','KIM_PRIORITY_ON_DOC_TYP_PERMS_IND','N','A','Flag for enabling/disabling document type permission checks to use KIM Permissions as priority over Document Type policies.','CONFG','WorkflowAdmin')
/

-- Delete specific application constants (Leaving constants that will be replaced by KIM) and create equivalent system parameters
Delete from KREW_APP_CNST_T where APPL_CNST_NM='BAM'
/
Delete from KREW_APP_CNST_T where APPL_CNST_NM='Config.Application.MinutesToCacheUsers'
/
Delete from KREW_APP_CNST_T where APPL_CNST_NM='Config.Mailer.LastDailyReminderDate'
/
Delete from KREW_APP_CNST_T where APPL_CNST_NM='Config.Mailer.LastWeeklyReminderDate'
/
Delete from KREW_APP_CNST_T where APPL_CNST_NM='DocumentType.IsRouteLogPopup'
/
Delete from KREW_APP_CNST_T where APPL_CNST_NM='RouteManagerDriver.isRunning'
/
Delete from KREW_APP_CNST_T where APPL_CNST_NM='RouteManagerPool.numWorkers'
/
Delete from KREW_APP_CNST_T where APPL_CNST_NM='RouteManagerQueue.initialDelay'
/
Delete from KREW_APP_CNST_T where APPL_CNST_NM='RouteManagerQueue.waitTime'
/
Delete from KREW_APP_CNST_T where APPL_CNST_NM='RouteQueue.isRoutingByIPNumber'
/
Delete from KREW_APP_CNST_T where APPL_CNST_NM='RouteTypeSearch.Instructions'
/
Delete from KREW_APP_CNST_T where APPL_CNST_NM='RuleAttribute.CreateNew.Instruction'
/
Delete from KREW_APP_CNST_T where APPL_CNST_NM='RuleAttribute.Search.Instruction'
/
Delete from KREW_APP_CNST_T where APPL_CNST_NM='Security.HttpInvoker.SignMessages'
/
Delete from KREW_APP_CNST_T where APPL_CNST_NM='Config.Application.AdminUserList'
/
Delete from KREW_APP_CNST_T where APPL_CNST_NM='RouteQueue.maxRetryAttempts'
/
Delete from KREW_APP_CNST_T where APPL_CNST_NM='RouteQueue.requeueWaitTime'
/
Delete from KREW_APP_CNST_T where APPL_CNST_NM='RouteQueue.timeIncrement'
/
Delete from KREW_APP_CNST_T where APPL_CNST_NM='Routing.ImmediateExceptionRouting'
/
Delete from KREW_APP_CNST_T where APPL_CNST_NM='Feature.CheckRouteLogAuthentication.CheckFuture'
/
Delete from KREW_APP_CNST_T where APPL_CNST_NM='Authorization.UserIds.AllowViewRoles'
/
Delete from KREW_APP_CNST_T where APPL_CNST_NM='ActionList.EmailNotification.TestAddress'
/
Delete from KREW_APP_CNST_T where APPL_CNST_NM='ActionList.IsDocumentPopup'
/
Delete from KREW_APP_CNST_T where APPL_CNST_NM='ActionList.IsRouteLogPopup'
/
Delete from KREW_APP_CNST_T where APPL_CNST_NM='ActionList.pageSizeThrottle'
/
Delete from KREW_APP_CNST_T where APPL_CNST_NM='ActionList.sendEmailNotification'
/
Delete from KREW_APP_CNST_T where APPL_CNST_NM='ApplicationContext'
/
Delete from KREW_APP_CNST_T where APPL_CNST_NM='Backdoor.ShowbackDoorLogin'
/
Delete from KREW_APP_CNST_T where APPL_CNST_NM='Config.Application.DelegateLimit'
/
Delete from KREW_APP_CNST_T where APPL_CNST_NM='Config.Application.RuleLockingOn'
/
Delete from KREW_APP_CNST_T where APPL_CNST_NM='Config.Backdoor.TargetFrameName'
/
Delete from KREW_APP_CNST_T where APPL_CNST_NM='Config.Immutables.AllowedRoles'
/
Delete from KREW_APP_CNST_T where APPL_CNST_NM='Config.Immutables.DeniedRoles'
/
Delete from KREW_APP_CNST_T where APPL_CNST_NM='Config.Mailer.FromAddress'
/
Delete from KREW_APP_CNST_T where APPL_CNST_NM='Config.Maximum.Nodes.Before.Runaway'
/
Delete from KREW_APP_CNST_T where APPL_CNST_NM='Config.Workgroup.NotifyExcludedUsers'
/
Delete from KREW_APP_CNST_T where APPL_CNST_NM='DocumentSearch.FetchMoreIterationLimit'
/
Delete from KREW_APP_CNST_T where APPL_CNST_NM='DocumentSearch.IsDocumentPopup'
/
Delete from KREW_APP_CNST_T where APPL_CNST_NM='DocumentSearch.IsRouteLogPopup'
/
Delete from KREW_APP_CNST_T where APPL_CNST_NM='DocumentSearch.ResultCap'
/
Delete from KREW_APP_CNST_T where APPL_CNST_NM='DocumentType.Search.Instruction'
/
Delete from KREW_APP_CNST_T where APPL_CNST_NM='EDL.DebugTransform'
/
Delete from KREW_APP_CNST_T where APPL_CNST_NM='EDL.UseXSLTC'
/
Delete from KREW_APP_CNST_T where APPL_CNST_NM='Feature.IsLastApprover.ActivateFirst'
/
Delete from KREW_APP_CNST_T where APPL_CNST_NM='GlobalReviewer.Replace.Instruction'
/
Delete from KREW_APP_CNST_T where APPL_CNST_NM='HelpDeskActionList.helpDeskActionListName'
/
Delete from KREW_APP_CNST_T where APPL_CNST_NM='Note.CreateNew.Instruction'
/
Delete from KREW_APP_CNST_T where APPL_CNST_NM='QuickLinks.restrictDocumentTypes'
/
Delete from KREW_APP_CNST_T where APPL_CNST_NM='Rule.Config.CustomDocTypes'
/
Delete from KREW_APP_CNST_T where APPL_CNST_NM='Rule.CreateNew.Instruction'
/
Delete from KREW_APP_CNST_T where APPL_CNST_NM='Rule.IsRouteLogPopup'
/
Delete from KREW_APP_CNST_T where APPL_CNST_NM='Rule.Search.Instruction'
/
Delete from KREW_APP_CNST_T where APPL_CNST_NM='RuleChange.IsGenerateActionRequests'
/
Delete from KREW_APP_CNST_T where APPL_CNST_NM='RuleService.IsCaching'
/
Delete from KREW_APP_CNST_T where APPL_CNST_NM='RuleTemplate.CreateNew.Instruction'
/
Delete from KREW_APP_CNST_T where APPL_CNST_NM='RuleTemplate.Search.Instruction'
/
Delete from KREW_APP_CNST_T where APPL_CNST_NM='showAttachments'
/
Insert into KRNS_PARM_DTL_TYP_T ("NMSPC_CD", "OBJ_ID", "PARM_DTL_TYP_CD", "NM") VALUES ('KR-WKFLW', '1821D8BAB21E498F9FB1ECCA25C37F9B', 'ActionList', 'Action List')
/
Insert into KRNS_PARM_DTL_TYP_T ("NMSPC_CD", "OBJ_ID", "PARM_DTL_TYP_CD", "NM") VALUES ('KR-WKFLW', 'F7E44233C2C440FFB1A399548951160A', 'Backdoor', 'Backdoor')
/
Insert into KRNS_PARM_DTL_TYP_T ("NMSPC_CD", "OBJ_ID", "PARM_DTL_TYP_CD", "NM") VALUES ('KR-WKFLW', '18695E69ED0D4FBE8B084FCA8066D21C', 'DocumentSearch', 'Document Search ')
/
Insert into KRNS_PARM_DTL_TYP_T ("NMSPC_CD", "OBJ_ID", "PARM_DTL_TYP_CD", "NM") VALUES ('KR-WKFLW', '51DD5B9FACDD4EDAA9CA8D53A82FCCCA', 'EDocLite  ', 'eDocLite')
/
Insert into KRNS_PARM_DTL_TYP_T ("NMSPC_CD", "OBJ_ID", "PARM_DTL_TYP_CD", "NM") VALUES ('KR-WKFLW', 'BBD9976498A4441F904013004F3D70B3', 'Feature', 'Feature')
/
Insert into KRNS_PARM_DTL_TYP_T ("NMSPC_CD", "OBJ_ID", "PARM_DTL_TYP_CD", "NM") VALUES ('KR-WKFLW', 'C21B0C6229144F6FBC52A10A38E51E3B', 'GlobalReviewer', 'Global Reviewer')
/
Insert into KRNS_PARM_DTL_TYP_T ("NMSPC_CD", "OBJ_ID", "PARM_DTL_TYP_CD", "NM") VALUES ('KR-WKFLW', '5DB9D1433E214325BE380C82762A223B', 'Mailer', 'Mailer')
/
Insert into KRNS_PARM_DTL_TYP_T ("NMSPC_CD", "OBJ_ID", "PARM_DTL_TYP_CD", "NM") VALUES ('KR-WKFLW', '868D39EC269B4402B3136C74C2342F22', 'Note', 'Note')
/
Insert into KRNS_PARM_DTL_TYP_T ("NMSPC_CD", "OBJ_ID", "PARM_DTL_TYP_CD", "NM") VALUES ('KR-WKFLW', '3E26DA76458A46D68CBAF209DA036157', 'QuickLink', 'Quick Link')
/
Insert into KRNS_PARM_DTL_TYP_T ("NMSPC_CD", "OBJ_ID", "PARM_DTL_TYP_CD", "NM") VALUES ('KR-WKFLW', 'D4F6DDEF69B24265AA2A170A62A1CADB', 'RouteQueue', 'Route Queue')
/
Insert into KRNS_PARM_DTL_TYP_T ("NMSPC_CD", "OBJ_ID", "PARM_DTL_TYP_CD", "NM") VALUES ('KR-WKFLW', '583C2D3562D44DBAA5FEA998EB601DC9', 'Route', 'Routing')
/
Insert into KRNS_PARM_DTL_TYP_T ("NMSPC_CD", "OBJ_ID", "PARM_DTL_TYP_CD", "NM") VALUES ('KR-WKFLW', 'FC831215ED534549845BCE2C59B16FD9', 'Rule', 'Rule')
/
Insert into KRNS_PARM_DTL_TYP_T ("NMSPC_CD", "OBJ_ID", "PARM_DTL_TYP_CD", "NM") VALUES ('KR-WKFLW', 'A8FBD6D9A72347CFBB47994C35A45A5F', 'RuleService', 'Rule Service')
/
Insert into KRNS_PARM_DTL_TYP_T ("NMSPC_CD", "OBJ_ID", "PARM_DTL_TYP_CD", "NM") VALUES ('KR-WKFLW', 'FB2565730CB74E3C9077A8B8CF3E4618', 'RuleTemplate', 'Rule Template')
/
Insert into KRNS_PARM_DTL_TYP_T ("NMSPC_CD", "OBJ_ID", "PARM_DTL_TYP_CD", "NM") VALUES ('KR-WKFLW', 'D04AFB1812E34723ABEB64986AC61DC9', 'Workgroup', 'Workgroup')
/
INSERT INTO KRNS_PARM_T ("NMSPC_CD", "OBJ_ID","PARM_DTL_TYP_CD","PARM_NM","PARM_TYP_CD","TXT","PARM_DESC_TXT","CONS_CD","GRP_NM") VALUES ('KR-WKFLW', '340789CDF30F4252A1A2A42AD39B90B2', 'ActionList', 'EMAIL_NOTIFICATION_TEST_ADDRESS ', 'CONFG', '', 'Default email address used for testing.', 'A', 'WorkflowAdmin')
/
INSERT INTO KRNS_PARM_T ("NMSPC_CD", "OBJ_ID","PARM_DTL_TYP_CD","PARM_NM","PARM_TYP_CD","TXT","PARM_DESC_TXT","CONS_CD","GRP_NM") VALUES ('KR-WKFLW', '290E45BA032F4F4FB423CE5F78AC52E1', 'ActionList', 'ACTION_LIST_DOCUMENT_POPUP_IND', 'CONFG', 'Y', 'Flag to specify if clicking on a Document ID from the Action List will load the Document in a new window.', 'A', 'WorkflowAdmin')
/
INSERT INTO KRNS_PARM_T ("NMSPC_CD", "OBJ_ID","PARM_DTL_TYP_CD","PARM_NM","PARM_TYP_CD","TXT","PARM_DESC_TXT","CONS_CD","GRP_NM") VALUES ('KR-WKFLW', '967B0311A5E94F7191B2C544FA7DE095', 'ActionList', 'ACTION_LIST_ROUTE_LOG_POPUP_IND', 'CONFG', 'N', 'Flag to specify if clicking on a Route Log from the Action List will load the Route Log in a new window.', 'A', 'WorkflowAdmin')
/
INSERT INTO KRNS_PARM_T ("NMSPC_CD", "OBJ_ID","PARM_DTL_TYP_CD","PARM_NM","PARM_TYP_CD","TXT","PARM_DESC_TXT","CONS_CD","GRP_NM") VALUES ('KR-WKFLW', '2CE075BC0C59435CA6DEFF724492DE3F', 'ActionList', 'PAGE_SIZE_THROTTLE', 'CONFG', '', 'Throttles the number of results returned on all users Action Lists, regardless of their user preferences.  This is intended to be used in a situation where excessively large Action Lists are causing performance issues.', 'A', 'WorkflowAdmin')
/
INSERT INTO KRNS_PARM_T ("NMSPC_CD", "OBJ_ID","PARM_DTL_TYP_CD","PARM_NM","PARM_TYP_CD","TXT","PARM_DESC_TXT","CONS_CD","GRP_NM") VALUES ('KR-WKFLW', 'A87659E198214A8B90BE5BEF41630411', 'ActionList', 'SEND_EMAIL_NOTIFICATION_IND', 'CONFG', 'N', 'Flag to determine whether or not to send email notification.', 'A', 'WorkflowAdmin')
/
INSERT INTO KRNS_PARM_T ("NMSPC_CD", "OBJ_ID","PARM_DTL_TYP_CD","PARM_NM","PARM_TYP_CD","TXT","PARM_DESC_TXT","CONS_CD","GRP_NM") VALUES ('KR-WKFLW', '396623E27D0649FCB6E7E7CD45F32E13', 'All', 'APPLICATION_CONTEXT', 'CONFG', 'en-dev', 'Web application context name of the application.', 'A', 'WorkflowAdmin')
/
INSERT INTO KRNS_PARM_T ("NMSPC_CD", "OBJ_ID","PARM_DTL_TYP_CD","PARM_NM","PARM_TYP_CD","TXT","PARM_DESC_TXT","CONS_CD","GRP_NM") VALUES ('KR-WKFLW', '9BD6785416434C4D9E5F05AF077DB9B7', 'Backdoor', 'SHOW_BACK_DOOR_LOGIN_IND', 'CONFG', 'Y', 'Flag to show the backdoor login.', 'A', 'WorkflowAdmin')
/
INSERT INTO KRNS_PARM_T ("NMSPC_CD", "OBJ_ID","PARM_DTL_TYP_CD","PARM_NM","PARM_TYP_CD","TXT","PARM_DESC_TXT","CONS_CD","GRP_NM") VALUES ('KR-WKFLW', '21EA54B9A9E846709E76C176DE0AF47C', 'Rule', 'DELEGATE_LIMIT', 'CONFG', '20', 'Specifies that maximum number of delegation rules that will be displayed on a Rule inquiry before the screen shows a count of delegate rules and provides a link for the user to show them.', 'A', 'WorkflowAdmin')
/
INSERT INTO KRNS_PARM_T ("NMSPC_CD", "OBJ_ID","PARM_DTL_TYP_CD","PARM_NM","PARM_TYP_CD","TXT","PARM_DESC_TXT","CONS_CD","GRP_NM") VALUES ('KR-WKFLW', '88167F03AAD0474281908E03CC681C06', 'Rule', 'RULE_LOCKING_ON_IND', 'CONFG', 'Y', 'Defines whether rule locking it enabled.', 'A', 'WorkflowAdmin')
/
INSERT INTO KRNS_PARM_T ("NMSPC_CD", "OBJ_ID","PARM_DTL_TYP_CD","PARM_NM","PARM_TYP_CD","TXT","PARM_DESC_TXT","CONS_CD","GRP_NM") VALUES ('KR-WKFLW', 'AD71949E2CCF422D941AAA9D4CB44D10', 'Backdoor', 'TARGET_FRAME_NAME', 'CONFG', 'iframe_51148', 'Defines the target iframe name that the KEW internal portal uses for its menu links.', 'A', 'WorkflowAdmin')
/
INSERT INTO KRNS_PARM_T ("NMSPC_CD", "OBJ_ID","PARM_DTL_TYP_CD","PARM_NM","PARM_TYP_CD","TXT","PARM_DESC_TXT","CONS_CD","GRP_NM") VALUES ('KR-WKFLW', '700AB6A6E23740D0B3E00E02A8FB6347', 'Mailer', 'FROM_ADDRESS', 'CONFG', 'quickstart@localhost', 'Default from email address for notifications.', 'A', 'WorkflowAdmin')
/
INSERT INTO KRNS_PARM_T ("NMSPC_CD", "OBJ_ID","PARM_DTL_TYP_CD","PARM_NM","PARM_TYP_CD","TXT","PARM_DESC_TXT","CONS_CD","GRP_NM") VALUES ('KR-WKFLW', '4656B6E7E9844E2C9E2255014AFC86B5', 'All', 'MAXIMUM_NODES_BEFORE_RUNAWAY', 'CONFG', '', 'The maximum number of nodes the workflow engine will process before it determines the process is a runaway process.  This is prevent infinite "loops" in the workflow engine.', 'A', 'WorkflowAdmin')
/
INSERT INTO KRNS_PARM_T ("NMSPC_CD", "OBJ_ID","PARM_DTL_TYP_CD","PARM_NM","PARM_TYP_CD","TXT","PARM_DESC_TXT","CONS_CD","GRP_NM") VALUES ('KR-WKFLW', '08280F2575904F3586CF48BB97907506', 'Workgroup', 'NOTIFY_EXCLUDED_USERS_IND', 'CONFG', '', 'Defines whether or not to send a notification to users excluded from a workgroup.', 'A', 'WorkflowAdmin')
/
INSERT INTO KRNS_PARM_T ("NMSPC_CD", "OBJ_ID","PARM_DTL_TYP_CD","PARM_NM","PARM_TYP_CD","TXT","PARM_DESC_TXT","CONS_CD","GRP_NM") VALUES ('KR-WKFLW', 'D43459D143FC46C6BF83C71AC2383B76', 'DocumentSearch', 'FETCH_MORE_ITERATION_LIMIT', 'CONFG', '', 'Limit of fetch more iterations for document searches.', 'A', 'WorkflowAdmin')
/
INSERT INTO KRNS_PARM_T ("NMSPC_CD", "OBJ_ID","PARM_DTL_TYP_CD","PARM_NM","PARM_TYP_CD","TXT","PARM_DESC_TXT","CONS_CD","GRP_NM") VALUES ('KR-WKFLW', 'E78100F6F14C4932B54F7719FA5C27E9', 'DocumentSearch', 'DOCUMENT_SEARCH_POPUP_IND', 'CONFG', 'Y', 'Flag to specify if clicking on a Document ID from Document Search will load the Document in a new window.', 'A', 'WorkflowAdmin')
/
INSERT INTO KRNS_PARM_T ("NMSPC_CD", "OBJ_ID","PARM_DTL_TYP_CD","PARM_NM","PARM_TYP_CD","TXT","PARM_DESC_TXT","CONS_CD","GRP_NM") VALUES ('KR-WKFLW', '632680DDE9A7478CBD379FAF90C7AE72', 'DocumentSearch', 'DOCUMENT_SEARCH_ROUTE_LOG_POPUP_IND', 'CONFG', 'N', 'Flag to specify if clicking on a Route Log from Document Search will load the Route Log in a new window.', 'A', 'WorkflowAdmin')
/
INSERT INTO KRNS_PARM_T ("NMSPC_CD", "OBJ_ID","PARM_DTL_TYP_CD","PARM_NM","PARM_TYP_CD","TXT","PARM_DESC_TXT","CONS_CD","GRP_NM") VALUES ('KR-WKFLW', 'E324D85082184EB6967537B3EE1F655B', 'DocumentSearch', 'RESULT_CAP', 'CONFG', '', 'Maximum number of documents to return from a search.', 'A', 'WorkflowAdmin')
/
INSERT INTO KRNS_PARM_T ("NMSPC_CD", "OBJ_ID","PARM_DTL_TYP_CD","PARM_NM","PARM_TYP_CD","TXT","PARM_DESC_TXT","CONS_CD","GRP_NM") VALUES ('KR-WKFLW', '7ADC4995AB7E47299A13A5B66E495683', 'DocumentType', 'DOCUMENT_TYPE_SEARCH_INSTRUCTION', 'CONFG', 'Enter document type information below and click search.', 'Instructions for searching document types.', 'A', 'WorkflowAdmin')
/
INSERT INTO KRNS_PARM_T ("NMSPC_CD", "OBJ_ID","PARM_DTL_TYP_CD","PARM_NM","PARM_TYP_CD","TXT","PARM_DESC_TXT","CONS_CD","GRP_NM") VALUES ('KR-WKFLW', '68B2EA08E13A4FF3B9EDBD5415818C93', 'EDocLite', 'DEBUG_TRANSFORM_IND', 'CONFG', 'N', 'Defines whether the debug transform is enabled for eDcoLite.', 'A', 'WorkflowAdmin')
/
INSERT INTO KRNS_PARM_T ("NMSPC_CD", "OBJ_ID","PARM_DTL_TYP_CD","PARM_NM","PARM_TYP_CD","TXT","PARM_DESC_TXT","CONS_CD","GRP_NM") VALUES ('KR-WKFLW', 'FCAEE745A7E64AF5982937C47EBC2698', 'EDocLite', 'USE_XSLTC_IND', 'CONFG', 'N', 'Defines whether XSLTC is used for eDocLite.', 'A', 'WorkflowAdmin')
/
INSERT INTO KRNS_PARM_T ("NMSPC_CD", "OBJ_ID","PARM_DTL_TYP_CD","PARM_NM","PARM_TYP_CD","TXT","PARM_DESC_TXT","CONS_CD","GRP_NM") VALUES ('KR-WKFLW', 'BEBDBCFA74A5458EADE2CF075FFF206E', 'Feature', 'IS_LAST_APPROVER_ACTIVATE_FIRST_IND', 'CONFG', '', 'A flag to specify whether the WorkflowInfo.isLastApproverAtNode(...) API method attempts to active requests first, prior to execution.', 'A', 'WorkflowAdmin')
/
INSERT INTO KRNS_PARM_T ("NMSPC_CD", "OBJ_ID","PARM_DTL_TYP_CD","PARM_NM","PARM_TYP_CD","TXT","PARM_DESC_TXT","CONS_CD","GRP_NM") VALUES ('KR-WKFLW', '0594B51D2619468294D084F24DA25A03', 'GlobalReviewer', 'REPLACE_INSTRUCTION', 'CONFG', 'Enter the reviewer to replace.', 'Instructions for replacing a reviewer.', 'A', 'WorkflowAdmin')
/
INSERT INTO KRNS_PARM_T ("NMSPC_CD", "OBJ_ID","PARM_DTL_TYP_CD","PARM_NM","PARM_TYP_CD","TXT","PARM_DESC_TXT","CONS_CD","GRP_NM") VALUES ('KR-WKFLW', 'BD2EA23177374930B2E97C6F7AC819DA', 'ActionList', 'HELP_DESK_NAME_GROUP', 'CONFG', 'WorkflowAdmin', 'The name of the group who has access to the "Help Desk" feature on the Action List.', 'A', 'WorkflowAdmin')
/
INSERT INTO KRNS_PARM_T ("NMSPC_CD", "OBJ_ID","PARM_DTL_TYP_CD","PARM_NM","PARM_TYP_CD","TXT","PARM_DESC_TXT","CONS_CD","GRP_NM") VALUES ('KR-WKFLW', '09217B953D1F4265B1106291925B8F08', 'Note', 'NOTE_CREATE_NEW_INSTRUCTION', 'CONFG', 'Create or modify note information.', 'Instructions for creating a new note.', 'A', 'WorkflowAdmin')
/
INSERT INTO KRNS_PARM_T ("NMSPC_CD", "OBJ_ID","PARM_DTL_TYP_CD","PARM_NM","PARM_TYP_CD","TXT","PARM_DESC_TXT","CONS_CD","GRP_NM") VALUES ('KR-WKFLW', '5292CFD9A0EA48BEB22A2EB3B3BD3CDA', 'QuickLinks', 'RESTRICT_DOCUMENT_TYPES', 'CONFG', '', 'Comma seperated list of Document Types to exclude from the Rule Quicklinks.', 'A', 'WorkflowAdmin')
/
INSERT INTO KRNS_PARM_T ("NMSPC_CD", "OBJ_ID","PARM_DTL_TYP_CD","PARM_NM","PARM_TYP_CD","TXT","PARM_DESC_TXT","CONS_CD","GRP_NM") VALUES ('KR-WKFLW', '8AE796DB88484468830A8879630CCF5D', 'Rule', 'RULE_CACHE_REQUEUE_DELAY', 'CONFG', '5000', 'Amount of time after a rule change is made before the rule cache update message is sent.', 'A', 'WorkflowAdmin')
/
INSERT INTO KRNS_PARM_T ("NMSPC_CD", "OBJ_ID","PARM_DTL_TYP_CD","PARM_NM","PARM_TYP_CD","TXT","PARM_DESC_TXT","CONS_CD","GRP_NM") VALUES ('KR-WKFLW', 'BDE964269F2743338C00A4326B676195', 'Rule', 'CUSTOM_DOCUMENT_TYPES', 'CONFG', '', 'Defines custom Document Type processes to use for certain types of routing rules.', 'A', 'WorkflowAdmin')
/
INSERT INTO KRNS_PARM_T ("NMSPC_CD", "OBJ_ID","PARM_DTL_TYP_CD","PARM_NM","PARM_TYP_CD","TXT","PARM_DESC_TXT","CONS_CD","GRP_NM") VALUES ('KR-WKFLW', '83F4AE3D84C948B99118D602574B4E72', 'Rule', 'RULE_CREATE_NEW_INSTRUCTION', 'CONFG', 'Please select a rule template and document type.', 'Instructions for creating a new rule.', 'A', 'WorkflowAdmin')
/
INSERT INTO KRNS_PARM_T ("NMSPC_CD", "OBJ_ID","PARM_DTL_TYP_CD","PARM_NM","PARM_TYP_CD","TXT","PARM_DESC_TXT","CONS_CD","GRP_NM") VALUES ('KR-WKFLW', '1C0C01E55A90472EAF65941ACE9DDCA2', 'Rule', 'ROUTE_LOG_POPUP_IND', 'CONFG', 'F', 'Flag to specify if clicking on a Route Log from a Routing Rule inquiry will load the Route Log in a new window.', 'A', 'WorkflowAdmin')
/
INSERT INTO KRNS_PARM_T ("NMSPC_CD", "OBJ_ID","PARM_DTL_TYP_CD","PARM_NM","PARM_TYP_CD","TXT","PARM_DESC_TXT","CONS_CD","GRP_NM") VALUES ('KR-WKFLW', 'E390513347EA44AD87923C391D1645F2', 'Rule', 'RULE_SEARCH_INSTRUCTION', 'CONFG', 'Use fields below to search for rules.', 'Instructions for the rule search.', 'A', 'WorkflowAdmin')
/
INSERT INTO KRNS_PARM_T ("NMSPC_CD", "OBJ_ID","PARM_DTL_TYP_CD","PARM_NM","PARM_TYP_CD","TXT","PARM_DESC_TXT","CONS_CD","GRP_NM") VALUES ('KR-WKFLW', '96868C896B4B4A8BA87AD20E42948431', 'Rule', 'GENERATE_ACTION_REQUESTS_IND', 'CONFG', 'Y', 'Flag to determine whether or not a change to a routing rule should be applied retroactively to existing documents.', 'A', 'WorkflowAdmin')
/
INSERT INTO KRNS_PARM_T ("NMSPC_CD", "OBJ_ID","PARM_DTL_TYP_CD","PARM_NM","PARM_TYP_CD","TXT","PARM_DESC_TXT","CONS_CD","GRP_NM") VALUES ('KR-WKFLW', 'E05A692D62E54B87901D872DC37208A1', 'Rule', 'CACHING_IND', 'CONFG', 'Y', 'Indicator to determine if rule caching is enabled.', 'A', 'WorkflowAdmin')
/
INSERT INTO KRNS_PARM_T ("NMSPC_CD", "OBJ_ID","PARM_DTL_TYP_CD","PARM_NM","PARM_TYP_CD","TXT","PARM_DESC_TXT","CONS_CD","GRP_NM") VALUES ('KR-WKFLW', '09ECF812733D499C906ACBE17F13AFEE', 'RuleTemplate', 'RULE_TEMPLATE_CREATE_NEW_INSTRUCTION', 'CONFG', 'Enter a rule template name and description. Please select all necessary rule attributes for this template.', 'Instructions for creating new rule templates.', 'A', 'WorkflowAdmin')
/
INSERT INTO KRNS_PARM_T ("NMSPC_CD", "OBJ_ID","PARM_DTL_TYP_CD","PARM_NM","PARM_TYP_CD","TXT","PARM_DESC_TXT","CONS_CD","GRP_NM") VALUES ('KR-WKFLW', '983690D9FD3244BAB1EF6ED7CCAF63EF', 'RuleTemplate', 'RULE_TEMPLATE_SEARCH_INSTRUCTION', 'CONFG', 'Use fields below to search for rule templates.', 'Instructions for the rule template search.', 'A', 'WorkflowAdmin')
/
INSERT INTO KRNS_PARM_T ("NMSPC_CD", "OBJ_ID","PARM_DTL_TYP_CD","PARM_NM","PARM_TYP_CD","TXT","PARM_DESC_TXT","CONS_CD","GRP_NM") VALUES ('KR-WKFLW', '8A37388A2D7A46EF9E6BF3FA8D08A03A', 'All', 'SHOW_ATTACHMENTS_IND', 'CONFG', 'Y', 'Flag to specify whether or not a file upload box is displayed for KEW notes which allows for uploading of an attachment with the note.', 'A', 'WorkflowAdmin')
/
UPDATE KRIM_GRP_T SET NMSPC_CD = 'KR-WKFLW' WHERE GRP_NM = 'WorkflowAdmin' AND NMSPC_CD = 'KFS'
/
UPDATE KRIM_GRP_T SET NMSPC_CD = 'KR-WKFLW' WHERE GRP_NM = 'NotificationAdmin' AND NMSPC_CD = 'KFS'
/
UPDATE KRNS_PARM_T SET TXT = 'KFS:KUALI_ROLE_SUPERVISOR' WHERE NMSPC_CD = 'KR-NS' AND PARM_DTL_TYP_CD = 'Document' AND PARM_NM = 'PESSIMISTIC_LOCK_ADMIN_GROUP'
/
UPDATE KRNS_PARM_T SET TXT = 'KR-WKFLW:WorkflowAdmin' WHERE NMSPC_CD = 'KR-NS' AND PARM_DTL_TYP_CD = 'Document' AND PARM_NM = 'EXCEPTION_GROUP'
/
UPDATE KRNS_PARM_T SET TXT = 'KR-WKFLW:WorkflowAdmin' WHERE NMSPC_CD = 'KR-NS' AND PARM_DTL_TYP_CD = 'Batch' AND PARM_NM = 'SCHEDULE_ADMIN_GROUP'
/
UPDATE KRNS_PARM_T SET TXT = 'KR-WKFLW:WorkflowAdmin' WHERE NMSPC_CD = 'KR-NS' AND PARM_DTL_TYP_CD = 'Document' AND PARM_NM = 'SUPERVISOR_GROUP'
/
UPDATE KRNS_PARM_T SET TXT = 'KR-WKFLW:WorkflowAdmin' WHERE NMSPC_CD = 'KR-WKFLW' AND PARM_DTL_TYP_CD = 'ActionList' AND PARM_NM = 'HELP_DESK_NAME_GROUP'
/
ALTER TABLE KREW_ACTN_RQST_T ADD (RQST_LBL VARCHAR2(255))
/
ALTER TABLE KREW_ACTN_ITM_T ADD (RQST_LBL VARCHAR2(255))
/
ALTER TABLE KREW_OUT_BOX_ITM_T ADD (RQST_LBL VARCHAR2(255))
/


ALTER TABLE KREW_DOC_TYP_T MODIFY DOC_TYP_DESC VARCHAR2(4000)
/
ALTER TABLE KREW_DOC_TYP_T MODIFY LBL VARCHAR2(500)
/
UPDATE KREW_DOC_TYP_T SET POST_PRCSR = 'org.kuali.rice.kns.workflow.postprocessor.KualiPostProcessor' WHERE CUR_IND = 1 AND DOC_TYP_NM = 'EDENSERVICE-DOCS.DocumentType'
/
UPDATE KREW_DOC_TYP_T SET DOC_HDLR_URL = '${kr.url}/maintenance.do?methodToCall=docHandler' WHERE CUR_IND = 1 AND DOC_TYP_NM = 'EDENSERVICE-DOCS.DocumentType'
/



UPDATE KREW_DOC_TYP_T set DOC_TYP_NM='DocumentTypeDocument', PARNT_ID=(select DOC_TYP_ID from KREW_DOC_TYP_T where DOC_TYP_NM = 'RiceDocument' and CUR_IND=1) where DOC_TYP_NM='EDENSERVICE-DOCS.DocumentType' and CUR_IND=1
/
UPDATE KREW_DOC_TYP_T set DOC_TYP_NM='RoutingRuleDocument', PARNT_ID=(select DOC_TYP_ID from KREW_DOC_TYP_T where DOC_TYP_NM = 'RiceDocument' and CUR_IND=1) where DOC_TYP_NM='EDENSERVICE-DOCS.RuleDocument' and CUR_IND=1
/
UPDATE KREW_DOC_TYP_T set DOC_TYP_NM='RemoveReplaceUserDocument', PARNT_ID=(select DOC_TYP_ID from KREW_DOC_TYP_T where DOC_TYP_NM = 'RiceDocument' and CUR_IND=1) where DOC_TYP_NM='EDENSERVICE-DOCS.RemoveReplaceUser' and CUR_IND=1
/

UPDATE KREW_ACTN_ITM_T set DOC_TYP_NM='DocumentTypeDocument' where DOC_TYP_NM='EDENSERVICE-DOCS.DocumentType'
/
UPDATE KREW_ACTN_ITM_T set DOC_TYP_NM='RoutingRuleDocument' where DOC_TYP_NM='EDENSERVICE-DOCS.RuleDocument'
/
UPDATE KREW_ACTN_ITM_T set DOC_TYP_NM='RemoveReplaceUserDocument' where DOC_TYP_NM='EDENSERVICE-DOCS.RemoveReplaceUser'
/

UPDATE KREW_EDL_ASSCTN_T set DOC_TYP_NM='DocumentTypeDocument' where DOC_TYP_NM='EDENSERVICE-DOCS.DocumentType'
/
UPDATE KREW_EDL_ASSCTN_T set DOC_TYP_NM='RoutingRuleDocument' where DOC_TYP_NM='EDENSERVICE-DOCS.RuleDocument'
/
UPDATE KREW_EDL_ASSCTN_T set DOC_TYP_NM='RemoveReplaceUserDocument' where DOC_TYP_NM='EDENSERVICE-DOCS.RemoveReplaceUser'
/

UPDATE KREW_EDL_DMP_T set DOC_TYP_NM='DocumentTypeDocument' where DOC_TYP_NM='EDENSERVICE-DOCS.DocumentType'
/
UPDATE KREW_EDL_DMP_T set DOC_TYP_NM='RoutingRuleDocument' where DOC_TYP_NM='EDENSERVICE-DOCS.RuleDocument'
/
UPDATE KREW_EDL_DMP_T set DOC_TYP_NM='RemoveReplaceUserDocument' where DOC_TYP_NM='EDENSERVICE-DOCS.RemoveReplaceUser'
/

UPDATE KREW_OUT_BOX_ITM_T set DOC_TYP_NM='DocumentTypeDocument' where DOC_TYP_NM='EDENSERVICE-DOCS.DocumentType'
/
UPDATE KREW_OUT_BOX_ITM_T set DOC_TYP_NM='RoutingRuleDocument' where DOC_TYP_NM='EDENSERVICE-DOCS.RuleDocument'
/
UPDATE KREW_OUT_BOX_ITM_T set DOC_TYP_NM='RemoveReplaceUserDocument' where DOC_TYP_NM='EDENSERVICE-DOCS.RemoveReplaceUser'
/

UPDATE KREW_RULE_T set DOC_TYP_NM='DocumentTypeDocument' where DOC_TYP_NM='EDENSERVICE-DOCS.DocumentType'
/
UPDATE KREW_RULE_T set DOC_TYP_NM='RoutingRuleDocument' where DOC_TYP_NM='EDENSERVICE-DOCS.RuleDocument'
/
UPDATE KREW_RULE_T set DOC_TYP_NM='RemoveReplaceUserDocument' where DOC_TYP_NM='EDENSERVICE-DOCS.RemoveReplaceUser'
/
DELETE FROM KREW_DOC_TYP_PLCY_RELN_T where doc_plcy_nm like 'PRE_APPROVE'
/

ALTER TABLE KREW_DOC_TYP_T ADD HELP_DEF_URL VARCHAR2(4000)
/

-- add OJB_ID column to new PersistableBusinessObjects

ALTER TABLE KREW_RULE_EXPR_T ADD (OBJ_ID VARCHAR2(36))
/
ALTER TABLE KREW_RULE_EXPR_T ADD (VER_NBR NUMBER(8) DEFAULT 0)
/

ALTER TABLE KREW_RULE_ATTR_T ADD (OBJ_ID VARCHAR2(36))
/
ALTER TABLE KREW_DOC_HDR_T ADD (OBJ_ID VARCHAR2(36))
/
ALTER TABLE KREW_DOC_TYP_T ADD (OBJ_ID VARCHAR2(36))
/
ALTER TABLE KREW_RULE_TMPL_T ADD (OBJ_ID VARCHAR2(36))
/
ALTER TABLE KREW_RULE_TMPL_ATTR_T ADD (OBJ_ID VARCHAR2(36))
/
ALTER TABLE KREW_RULE_T ADD (OBJ_ID VARCHAR2(36))
/
ALTER TABLE KREW_RULE_RSP_T ADD (OBJ_ID VARCHAR2(36))
/
ALTER TABLE KREW_STYLE_T ADD (OBJ_ID VARCHAR2(36))
/
ALTER TABLE KREW_EDL_DEF_T ADD (OBJ_ID VARCHAR2(36))
/
ALTER TABLE KREW_EDL_ASSCTN_T ADD (OBJ_ID VARCHAR2(36))
/
ALTER TABLE KREW_DLGN_RSP_T ADD (OBJ_ID VARCHAR2(36))
/

UPDATE KREW_RULE_EXPR_T SET OBJ_ID=SYS_GUID() WHERE OBJ_ID IS NULL
/
UPDATE KREW_RULE_ATTR_T SET OBJ_ID=SYS_GUID() WHERE OBJ_ID IS NULL
/
UPDATE KREW_DOC_HDR_T SET OBJ_ID=SYS_GUID() WHERE OBJ_ID IS NULL
/
UPDATE KREW_DOC_TYP_T SET OBJ_ID=SYS_GUID() WHERE OBJ_ID IS NULL
/
UPDATE KREW_RULE_TMPL_T SET OBJ_ID=SYS_GUID() WHERE OBJ_ID IS NULL
/
UPDATE KREW_RULE_TMPL_ATTR_T SET OBJ_ID=SYS_GUID() WHERE OBJ_ID IS NULL
/
UPDATE KREW_RULE_T SET OBJ_ID=SYS_GUID() WHERE OBJ_ID IS NULL
/
UPDATE KREW_RULE_RSP_T SET OBJ_ID=SYS_GUID() WHERE OBJ_ID IS NULL
/
UPDATE KREW_STYLE_T SET OBJ_ID=SYS_GUID() WHERE OBJ_ID IS NULL
/
UPDATE KREW_EDL_DEF_T SET OBJ_ID=SYS_GUID() WHERE OBJ_ID IS NULL
/
UPDATE KREW_EDL_ASSCTN_T SET OBJ_ID=SYS_GUID() WHERE OBJ_ID IS NULL
/
UPDATE KREW_DLGN_RSP_T SET OBJ_ID=SYS_GUID() WHERE OBJ_ID IS NULL
/

ALTER TABLE KREW_RULE_EXPR_T ADD CONSTRAINT KREW_RULE_EXPR_TC0 UNIQUE (OBJ_ID)
/
ALTER TABLE KREW_RULE_ATTR_T ADD CONSTRAINT KREW_RULE_ATTR_TC0 UNIQUE (OBJ_ID)
/
ALTER TABLE KREW_DOC_HDR_T ADD CONSTRAINT KREW_DOC_HDR_TC0 UNIQUE (OBJ_ID)
/
ALTER TABLE KREW_DOC_TYP_T ADD CONSTRAINT KREW_DOC_TYP_TC0 UNIQUE (OBJ_ID)
/
ALTER TABLE KREW_RULE_TMPL_T ADD CONSTRAINT KREW_RULE_TMPL_TC0 UNIQUE (OBJ_ID)
/
ALTER TABLE KREW_RULE_TMPL_ATTR_T ADD CONSTRAINT KREW_RULE_TMPL_ATTR_TC0 UNIQUE (OBJ_ID)
/
ALTER TABLE KREW_RULE_T ADD CONSTRAINT KREW_RULE_TC0 UNIQUE (OBJ_ID)
/
ALTER TABLE KREW_RULE_RSP_T ADD CONSTRAINT KREW_RULE_RSP_TC0 UNIQUE (OBJ_ID)
/
ALTER TABLE KREW_STYLE_T ADD CONSTRAINT KREW_STYLE_TC0 UNIQUE (OBJ_ID)
/
ALTER TABLE KREW_EDL_DEF_T ADD CONSTRAINT KREW_EDL_DEF_TC0 UNIQUE (OBJ_ID)
/
ALTER TABLE KREW_EDL_ASSCTN_T ADD CONSTRAINT KREW_EDL_ASSCTN_TC0 UNIQUE (OBJ_ID)
/
ALTER TABLE KREW_DLGN_RSP_T ADD CONSTRAINT KREW_DLGN_RSP_TC0 UNIQUE (OBJ_ID)
/

ALTER TABLE KREW_RULE_EXPR_T MODIFY (OBJ_ID VARCHAR2(36) NOT NULL)
/
ALTER TABLE KREW_RULE_ATTR_T MODIFY (OBJ_ID VARCHAR2(36) NOT NULL)
/
ALTER TABLE KREW_DOC_HDR_T MODIFY (OBJ_ID VARCHAR2(36) NOT NULL)
/
ALTER TABLE KREW_DOC_TYP_T MODIFY (OBJ_ID VARCHAR2(36) NOT NULL)
/
ALTER TABLE KREW_RULE_TMPL_T MODIFY (OBJ_ID VARCHAR2(36) NOT NULL)
/
ALTER TABLE KREW_RULE_TMPL_ATTR_T MODIFY (OBJ_ID VARCHAR2(36) NOT NULL)
/
ALTER TABLE KREW_RULE_T MODIFY (OBJ_ID VARCHAR2(36) NOT NULL)
/
ALTER TABLE KREW_RULE_RSP_T MODIFY (OBJ_ID VARCHAR2(36) NOT NULL)
/
ALTER TABLE KREW_STYLE_T MODIFY (OBJ_ID VARCHAR2(36) NOT NULL)
/
ALTER TABLE KREW_EDL_DEF_T MODIFY (OBJ_ID VARCHAR2(36) NOT NULL)
/
ALTER TABLE KREW_EDL_ASSCTN_T MODIFY (OBJ_ID VARCHAR2(36) NOT NULL)
/
ALTER TABLE KREW_DLGN_RSP_T MODIFY (OBJ_ID VARCHAR2(36) NOT NULL)
/

-- Convert System User role to just Rice

update KRIM_ROLE_T set KIM_TYP_ID='1' where ROLE_ID='62'
/
update KRIM_ROLE_T set ROLE_NM='Rice' where ROLE_ID='62'
/

update krim_perm_attr_data_t set attr_val = 'RiceDocument' where attr_data_id = '222'
/

delete from krim_role_mbr_attr_data_t where kim_typ_id = '44'
/
delete from krim_typ_attr_t where kim_typ_id = '44'
/
delete from krim_typ_t where kim_typ_id = '44'
/

-- SQL updates for the rule document

UPDATE KREW_DOC_TYP_T set POST_PRCSR='org.kuali.rice.kns.workflow.postprocessor.KualiPostProcessor' where DOC_TYP_NM='RoutingRuleDocument' and CUR_IND=1
/

ALTER TABLE KREW_RULE_T MODIFY FRM_DT DATE NULL
/
ALTER TABLE KREW_RULE_T MODIFY TO_DT DATE NULL
/

UPDATE KREW_DLGN_RSP_T RSP1 SET RSP1.RULE_RSP_ID=(SELECT RSP2.RSP_ID FROM KREW_RULE_RSP_T RSP2 WHERE RSP1.RULE_RSP_ID=RSP2.RULE_RSP_ID)
/

ALTER TABLE KREW_DLGN_RSP_T RENAME COLUMN RULE_RSP_ID TO RSP_ID
/

-- Remove the PRE_APPROVE document type policy

DELETE FROM KREW_DOC_TYP_PLCY_RELN_T where DOC_PLCY_NM='PRE_APPROVE'
/

-- Add object id field to document type policy table

ALTER TABLE KREW_DOC_TYP_PLCY_RELN_T ADD (OBJ_ID VARCHAR2(36))
/
UPDATE KREW_DOC_TYP_PLCY_RELN_T SET OBJ_ID=SYS_GUID() WHERE OBJ_ID IS NULL
/
ALTER TABLE KREW_DOC_TYP_PLCY_RELN_T ADD CONSTRAINT KREW_DOC_TYP_PLCY_RELN_TC0 UNIQUE (OBJ_ID)
/
ALTER TABLE KREW_DOC_TYP_PLCY_RELN_T MODIFY (OBJ_ID VARCHAR2(36) NOT NULL)
/

-- fix some lingering bad sequence names

RENAME SEQ_DOCUMENT_TYPE_ATTRIBUTE TO KREW_DOC_TYP_ATTR_S
/

RENAME SEQ_ROUTE_QUEUE TO KRSB_MSG_QUE_S
/

-- update the doc handler for the rule maintenance document

UPDATE KREW_DOC_TYP_T SET DOC_HDLR_URL='${kr.url}/maintenance.do?methodToCall=docHandler' WHERE DOC_TYP_NM='RoutingRuleDocument'
/

UPDATE KREW_DOC_TYP_T SET DOC_HDLR_URL='${kr.url}/maintenance.do?methodToCall=docHandler' WHERE DOC_TYP_NM='RoutingRuleDelegationMaintenanceDocument'
/

-- !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
-- !!! STOP!  Don't put anymore SQL in this file for Rice 1.0. Instead, create files in the !!!
-- !!! 'scripts/upgrades/0.9.3 to 0.9.4/db-updates-during-qa' directory                     !!!
-- !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
