--INSERT INTO FS_PARM_SEC_T values ('SYSTEM', '1', 1, 'WorkflowAdmin', 'Desc')
--/
--INSERT INTO FS_PARM_SEC_T values ('CoreMaintenanceEDoc', '2', 1, 'WorkflowAdmin', 'Desc')
--/
--INSERT INTO FS_PARM_T VALUES('SYSTEM','HELP_URL','07D71A3FF0D604D8E043814FD88104D8','1','http://www.fms.indiana.edu/fis/home.asp','','N', 'MC')
--/
--INSERT INTO FS_PARM_T VALUES('SYSTEM','lookup.results.limit','1AFCED30C07B2070E043814FD8812070','0','200','Limit of results returned in a lookup query','N', 'MC')
--/
--INSERT INTO FS_PARM_T VALUES('SYSTEM','demonstrationEncryptionCheck_FLAG','1C3D291AAD51A08CE043814FD881A08C','1','Y','Flag for enabling/disabling the demonstration encryption check.','N', 'MC')
--/
--INSERT INTO FS_PARM_T VALUES('SYSTEM','loadDataFileStep_USER','1F75EFB795DFB050E043814FD881B050','1','KULUSER','determines who the loadDataFileStep of pcdo_batch.sh will run as','N', 'MC')
--/
--insert into FS_PARM_T values ('CoreMaintenanceEDoc','Kuali.Document.RoutingReport.Workgroup','263A097060A3F152E043814FD881F152','1','WorkflowAdmin','Workgroup which can perform the route report on documents.','N', 'MC')
--/
--insert into FS_PARM_T values ('CoreMaintenanceEDoc','CASPasswordEnabled','26C8E6D6E77F40B4E043814FD88140B4','1','N','Whether the built in CAS implementation should ask for a password. The password will be verified against the Universal User Table.','N', 'MC')
--/
--insert into FS_PARM_T values ('CoreMaintenanceEDoc','UniversalUser.EditWorkgroup','2409BD6AB4CA800EE043814FD881800E','1','WorkflowAdmin','Workgroup which can edit the universal user table.','N', 'MC')
--/
--insert into FS_PARM_T values ('CoreMaintenanceEDoc','Workflow.Exception.Workgroup','2409BD6AB4CB800EE043814FD881800E','1','WorkflowAdmin','Workgroup which can perform functions on documents in exception routing status.','N', 'MC')
--/
--insert into FS_PARM_T values ('CoreMaintenanceEDoc','Kuali.Supervisor.Workgroup','2409BD6AB4CC800EE043814FD881800E','1','WorkflowAdmin','Workgroup which can perform almost any function within Kuali.','N', 'MC')
--/

insert into SH_PARM_TYP_T values ('CONFG', 3, 0,'Config', 'Y')
/
insert into SH_PARM_NMSPC_T values ('KR-NS', 3, 0, 'Kuali Rice', 'Y')
/
INSERT INTO sh_parm_t ("SH_PARM_NMSPC_CD","SH_PARM_DTL_TYP_CD","SH_PARM_NM","SH_PARM_TYP_CD","SH_PARM_TXT","SH_PARM_DESC","SH_PARM_CONS_CD","WRKGRP_NM") VALUES ('KR-NS','Lookup','RESULTS_DEFAULT_MAX_COLUMN_LENGTH','CONFG','70','If a maxLength attribute has not been set on a lookup result field in the data dictionary, then the result column''s max length will be the value of this parameter. Set this parameter to 0 for an unlimited default length or a positive value (i.e. greater than 0) for a finite max length.','A','KUALI_FMSOPS')
/
INSERT INTO sh_parm_t ("SH_PARM_NMSPC_CD","SH_PARM_DTL_TYP_CD","SH_PARM_NM","SH_PARM_TYP_CD","SH_PARM_TXT","SH_PARM_DESC","SH_PARM_CONS_CD","WRKGRP_NM") VALUES ('KR-NS','Lookup','RESULTS_LIMIT','CONFG','70','If a maxLength attribute has not been set on a lookup result field in the data dictionary, then the result column''s max length will be the value of this parameter. Set this parameter to 0 for an unlimited default length or a positive value (i.e. greater than 0) for a finite max length.','A','KUALI_FMSOPS')
/
insert into EN_APPL_CNST_T values ('Feature.CheckRouteLogAuthentication.CheckFuture', 'true', 1)
/
insert into EN_APPL_CNST_T values ('RouteQueue.maxRetryAttempts', '0', 1)
/
insert into EN_APPL_CNST_T values ('BAM', 'true', 1)
/
insert into EN_APPL_CNST_T values ('Security.HttpInvoker.SignMessages', 'true', 1)
/
insert into EN_APPL_CNST_T values ('Workflow.AdminWorkgroup', 'WorkflowAdmin', 1)
/
insert into EN_APPL_CNST_T values ('Routing.ImmediateExceptionRouting', 'true', 1)
/
insert into EN_APPL_CNST_T values ('Workgroup.IsRouteLogPopup', 'false', 0)
/
insert into EN_APPL_CNST_T values ('DocumentType.IsRouteLogPopup', 'false', 0)
/
insert into EN_APPL_CNST_T values ('DocumentSearch.IsRouteLogPopup', 'true', 0)
/
insert into EN_APPL_CNST_T values ('DocumentSearch.IsDocumentPopup', 'true', 0)
/
insert into EN_APPL_CNST_T values ('Config.Backdoor.TargetFrameName', 'iframe_51148', 0)
/
insert into FP_DOC_STATUS_T values ('A',	'2E0671732A684002E043814FD8814002',	1,	'Approved')
/
insert into FP_DOC_STATUS_T values ('C',	'2E0671732A694002E043814FD8814002',	1,	'Cancelled')
/
insert into FP_DOC_STATUS_T values ('E',	'2E0671732A6A4002E043814FD8814002',	1,	'Extracted')
/
insert into FP_DOC_STATUS_T values ('I',	'2E0671732A6B4002E043814FD8814002',	1,	'In Process')
/
insert into FP_DOC_STATUS_T values ('II',	'2E0671732A6C4002E043814FD8814002',	1,	'In Process')
/
insert into FP_DOC_STATUS_T values ('O',	'2E0671732A6D4002E043814FD8814002',	1,	'Pend Org')
/
insert into FP_DOC_STATUS_T values ('OO',	'2E0671732A6E4002E043814FD8814002',	1,	'Pend Org')
/
insert into FP_DOC_STATUS_T values ('P',	'2E0671732A6F4002E043814FD8814002',	1,	'Pend Acct')
/
insert into FP_DOC_STATUS_T values ('PP',	'2E0671732A704002E043814FD8814002',	1,	'Pend Acct')
/
insert into FP_DOC_STATUS_T values ('R',	'2E0671732A714002E043814FD8814002',	1,	'Pend Specl')
/
insert into FP_DOC_STATUS_T values ('RR',	'2E0671732A724002E043814FD8814002',	1,	'Pend Specl')
/
insert into FP_DOC_STATUS_T values ('S',	'2E0671732A734002E043814FD8814002',	1,	'Pend CG')
/
insert into FP_DOC_STATUS_T values ('V',	'2E0671732A744002E043814FD8814002',	1,	'Validation')
/
insert into FP_DOC_STATUS_T values ('Q',	'2E0671732A754002E043814FD8814002',	1,	'Doc Specif')
/

INSERT INTO sh_parm_typ_t
("SH_PARM_TYP_CD","OBJ_ID","VER_NBR","SH_PARM_TYP_NM","ACTIVE_IND")
VALUES
('VALID','3A6A61E1427C414AE043814FD881414A',1,'Document Validation','Y')
/
INSERT INTO sh_parm_typ_t
("SH_PARM_TYP_CD","OBJ_ID","VER_NBR","SH_PARM_TYP_NM","ACTIVE_IND")
VALUES
('AUTH','3A6A61E1427D414AE043814FD881414A',1,'Authorization','Y')
/
INSERT INTO sh_parm_typ_t
("SH_PARM_TYP_CD","OBJ_ID","VER_NBR","SH_PARM_TYP_NM","ACTIVE_IND")
VALUES
('HELP','3A6A61E1427E414AE043814FD881414A',1,'Help','Y')
/

INSERT INTO sh_parm_dtl_typ_t
("SH_PARM_NMSPC_CD","SH_PARM_DTL_TYP_CD","OBJ_ID","VER_NBR","SH_PARM_DTL_TYP_NM","ACTIVE_IND")
VALUES
('KR-NS','All','5DA619601EC14DC8B5533631F153915C',1,'All','Y')
/
INSERT INTO sh_parm_dtl_typ_t
("SH_PARM_NMSPC_CD","SH_PARM_DTL_TYP_CD","OBJ_ID","VER_NBR","SH_PARM_DTL_TYP_NM","ACTIVE_IND")
VALUES
('KR-NS','Document','76F469D2EBE347F8AFA946F8D98ABB12',1,'Document','Y')
/
INSERT INTO sh_parm_dtl_typ_t
("SH_PARM_NMSPC_CD","SH_PARM_DTL_TYP_CD","OBJ_ID","VER_NBR","SH_PARM_DTL_TYP_NM","ACTIVE_IND")
VALUES
('KR-NS','Lookup','BE854CD08B004FF499E441778A55DEA2',1,'Lookup','Y')
/
INSERT INTO sh_parm_dtl_typ_t
("SH_PARM_NMSPC_CD","SH_PARM_DTL_TYP_CD","OBJ_ID","VER_NBR","SH_PARM_DTL_TYP_NM","ACTIVE_IND")
VALUES
('KR-NS','N/A','4C4AE503F3644C97B6C90540B10751CA',1,'Not Applicable','Y')
/




INSERT INTO sh_parm_t
("SH_PARM_NMSPC_CD","SH_PARM_DTL_TYP_CD","SH_PARM_NM","OBJ_ID","VER_NBR","SH_PARM_TYP_CD","SH_PARM_TXT","SH_PARM_DESC","SH_PARM_CONS_CD","WRKGRP_NM")
VALUES
('KR-NS','All','CHECK_ENCRYPTION_SERVICE_OVERRIDE_IND','C6D1ECCA75C6426CBBE7BD84556D7528',1,'CONFG','Y','Flag for enabling/disabling the demonstration encryption check.','A','WorkflowAdmin')
/
INSERT INTO sh_parm_t
("SH_PARM_NMSPC_CD","SH_PARM_DTL_TYP_CD","SH_PARM_NM","OBJ_ID","VER_NBR","SH_PARM_TYP_CD","SH_PARM_TXT","SH_PARM_DESC","SH_PARM_CONS_CD","WRKGRP_NM")
VALUES
('KR-NS','Document','DEFAULT_CAN_PERFORM_ROUTE_REPORT','8DE1B1CC0D2D413DAD28C845940F0119',1,'CONFG','N','If Y, the "route report" button will be displayed on the document actions bar if the document is using the default DocumentAuthorizerBase.getDocumentActionFlags to set the canPerformRouteReport property of the returned DocumentActionFlags instance.','A','WorkflowAdmin')
/
INSERT INTO sh_parm_t
("SH_PARM_NMSPC_CD","SH_PARM_DTL_TYP_CD","SH_PARM_NM","OBJ_ID","VER_NBR","SH_PARM_TYP_CD","SH_PARM_TXT","SH_PARM_DESC","SH_PARM_CONS_CD","WRKGRP_NM")
VALUES
('KR-NS','Document','EXCEPTION_GROUP','604D7BC43D914B68A490C9443B7D4299',1,'CONFG','KUALI_ROLE_EXCEPTION','Workgroup which can perform functions on documents in exception routing status.','A','WorkflowAdmin')
/
INSERT INTO sh_parm_t
("SH_PARM_NMSPC_CD","SH_PARM_DTL_TYP_CD","SH_PARM_NM","OBJ_ID","VER_NBR","SH_PARM_TYP_CD","SH_PARM_TXT","SH_PARM_DESC","SH_PARM_CONS_CD","WRKGRP_NM")
VALUES
('KR-NS','Document','SUPERVISOR_GROUP','21273383BDF544C192978FE1A4EDDE6F',1,'AUTH','KUALI_ROLE_SUPERVISOR','Workgroup which can perform almost any function within Kuali.','A','WorkflowAdmin')
/
INSERT INTO sh_parm_t
("SH_PARM_NMSPC_CD","SH_PARM_DTL_TYP_CD","SH_PARM_NM","OBJ_ID","VER_NBR","SH_PARM_TYP_CD","SH_PARM_TXT","SH_PARM_DESC","SH_PARM_CONS_CD","WRKGRP_NM")
VALUES
('KR-NS','Lookup','MULTIPLE_VALUE_RESULTS_EXPIRATION_SECONDS','5B413AA4AD7F40C7BBC4C75CE99E743A',1,'CONFG','86400','Lookup results may continue to be persisted in the DB long after they are needed. This parameter represents the minimum amount of time, in seconds, that the results will be allowed to persist in the DB before they are deleted from the DB.','A','WorkflowAdmin')
/
INSERT INTO sh_parm_t
("SH_PARM_NMSPC_CD","SH_PARM_DTL_TYP_CD","SH_PARM_NM","OBJ_ID","VER_NBR","SH_PARM_TYP_CD","SH_PARM_TXT","SH_PARM_DESC","SH_PARM_CONS_CD","WRKGRP_NM")
VALUES
('KR-NS','Lookup','MULTIPLE_VALUE_RESULTS_PER_PAGE','90D766F2396C4C9DAE37FDECA0682233',1,'CONFG','100','The max number of rows that will be displayed on a lookup results screen','A','WorkflowAdmin')
/


INSERT INTO sh_parm_t
("SH_PARM_NMSPC_CD","SH_PARM_DTL_TYP_CD","SH_PARM_NM","OBJ_ID","VER_NBR","SH_PARM_TYP_CD","SH_PARM_TXT","SH_PARM_DESC","SH_PARM_CONS_CD","WRKGRP_NM")
VALUES
('KR-NS','UniversalUser','UNIVERSAL_USER_EDIT_GROUP','F8ED3336B99546878BE7B41311E4F63E',1,'AUTH','UNIVERSAL_USER_MAINTAINERS','Workgroup which can edit the universal user table.','A','WorkflowAdmin')
/
INSERT INTO sh_parm_t
("SH_PARM_NMSPC_CD","SH_PARM_DTL_TYP_CD","SH_PARM_NM","SH_PARM_TYP_CD","SH_PARM_TXT","SH_PARM_DESC","SH_PARM_CONS_CD","WRKGRP_NM")
VALUES
('KR-NS','All','DEFAULT_MAX_UPLOAD_FILE_SIZE','CONFG','5M','Maximum file upload size for the application. Used by PojoFormBase. Must be an integer, optionally followed by "K", "M", or "G". Only used if no other upload limits are in effect.','A','KUALI_FMSOPS')
/
INSERT INTO sh_parm_t
("SH_PARM_NMSPC_CD","SH_PARM_DTL_TYP_CD","SH_PARM_NM","SH_PARM_TYP_CD","SH_PARM_TXT","SH_PARM_DESC","SH_PARM_CONS_CD","WRKGRP_NM")
VALUES
('KR-NS','Document','ATTACHMENT_MAX_FILE_SIZE','CONFG','5M','Maximum attachment upload size for the application. Used by KualiDocumentFormBase. Must be an integer, optionally followed by "K", "M", or "G".','A','KUALI_FMSOPS')
/
INSERT INTO SH_PARM_T
(SH_PARM_NMSPC_CD, SH_PARM_DTL_TYP_CD, SH_PARM_NM, OBJ_ID, VER_NBR, SH_PARM_TYP_CD, SH_PARM_TXT, SH_PARM_DESC, SH_PARM_CONS_CD, WRKGRP_NM) 
VALUES
('KR-NS', 'Document', 'SEND_NOTE_WORKFLOW_NOTIFICATION_ACTIONS', sys_guid(), 0, 'CONFG', 'K', 'Some documents provide the functionality to send notes to another user using a workflow FYI or acknowledge functionality. This parameter specifies the default action that will be used when sending notes. This parameter should be one of the following 2 values: "K" for acknowledge or "F" for fyi. Depending on the notes and workflow service implementation, other values may be possible (see edu.iu.uis.eden.EdenConstants javadocs for details).', 'A', 'KUALI_FMSOPS')
/


INSERT INTO kr_qrtz_locks values('TRIGGER_ACCESS')
/
INSERT INTO kr_qrtz_locks values('JOB_ACCESS')
/
INSERT INTO kr_qrtz_locks values('CALENDAR_ACCESS')
/
INSERT INTO kr_qrtz_locks values('STATE_ACCESS')
/
INSERT INTO kr_qrtz_locks values('MISFIRE_ACCESS')
/
INSERT INTO KIM_NAMESPACES_T (ID, NAME, DESCRIPTION) VALUES (1, 'KIM', 'This record represents the actual KIM system and must always be loaded by default in order for the system to work properly.') 
/
INSERT INTO KIM_PERSONS_T (ID, USERNAME, PASSWORD) VALUES (1, 'admin', 'admin')
/

-- KEN sample app data below...

-- NOTIFICATION_PRIORITIES --
INSERT INTO NOTIFICATION_PRIORITIES
(ID, NAME, DESCRIPTION, PRIORITY_ORDER)
VALUES
(1, 'Normal', 'Normal priority', 2)
/

INSERT INTO NOTIFICATION_PRIORITIES
(ID, NAME, DESCRIPTION, PRIORITY_ORDER)
VALUES
(2, 'Low', 'A low priority', 3)
/

INSERT INTO NOTIFICATION_PRIORITIES
(ID, NAME, DESCRIPTION, PRIORITY_ORDER)
VALUES
(3, 'High', 'A high priority', 1)
/

-- NOTIFICATION_CONTENT_TYPES --
INSERT INTO NOTIFICATION_CONTENT_TYPES
(ID, NAME, DESCRIPTION, NAMESPACE, XSD, XSL)
VALUES
(1, 'Simple', 'Simple content type', 'notification/ContentTypeSimple',
'<?xml version="1.0" encoding="UTF-8"?>
<!-- This schema describes a simple notification.  It only contains a content
element which is a String...about as simple as one can get -->
<schema xmlns="http://www.w3.org/2001/XMLSchema"
  xmlns:c="ns:notification/common"
  xmlns:cs="ns:notification/ContentTypeSimple"
  targetNamespace="ns:notification/ContentTypeSimple"
  attributeFormDefault="unqualified" 
    elementFormDefault="qualified">
  <annotation>
    <documentation xml:lang="en">
      Simple Content Schema
    </documentation>
  </annotation>
  <import namespace="ns:notification/common" schemaLocation="resource:notification/notification-common" />
  <!--  The content element is just a String -->
  <element name="content">
    <complexType>
      <sequence>
        <element name="message" type="c:LongStringType"/>
      </sequence>
    </complexType>
  </element>
</schema>',
'<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
   version="1.0" 
   xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
   xmlns:n="ns:notification/ContentTypeSimple" 
   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
   xsi:schemaLocation="ns:notification/ContentTypeSimple resource:notification/ContentTypeSimple" 
   exclude-result-prefixes="n xsi">
   <xsl:output method="html" omit-xml-declaration="yes" />
   <xsl:template match="/n:content/n:message">
      <strong>
          <xsl:value-of select="." disable-output-escaping="yes"/>
      </strong>
   </xsl:template>
</xsl:stylesheet>')
/

INSERT INTO NOTIFICATION_CONTENT_TYPES
(ID, NAME, DESCRIPTION, NAMESPACE, XSD, XSL)
VALUES
(2, 'Event', 'Event content type', 'notification/ContentTypeEvent',
'<?xml version="1.0" encoding="UTF-8"?>
<!-- This schema defines an generic event notification type in order for it
to be accepted into the system. -->
<schema xmlns="http://www.w3.org/2001/XMLSchema" xmlns:c="ns:notification/common" xmlns:ce="ns:notification/ContentTypeEvent" targetNamespace="ns:notification/ContentTypeEvent" attributeFormDefault="unqualified" elementFormDefault="qualified">
  <annotation>
    <documentation xml:lang="en">Content Event Schema</documentation>
  </annotation>
  <import namespace="ns:notification/common" schemaLocation="resource:notification/notification-common" />
  <!-- The content element describes the content of the notification.  It
  contains a message (a simple String) and a message element -->
  <element name="content">
    <complexType>
      <sequence>
        <element name="message" type="c:LongStringType"/>
        <element ref="ce:event"/>
      </sequence>
    </complexType>
  </element>
  <!-- This is the event element.  It describes a simple event type containing a
  summary, description, location, and start/stop times -->
  <element name="event">
    <complexType>
      <sequence>
        <element name="summary" type="c:NonEmptyShortStringType" />
        <element name="description" type="c:NonEmptyShortStringType" />
        <element name="location" type="c:NonEmptyShortStringType" />
        <element name="startDateTime" type="dateTime" />
        <element name="stopDateTime" type="dateTime" />
      </sequence>
    </complexType>
  </element>
</schema>', 
'<?xml version="1.0" encoding="UTF-8"?>
<!-- style sheet declaration: be very careful editing the following, the
     default namespace must be used otherwise elements will not match -->
<xsl:stylesheet
    version="1.0" 
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
    xmlns:n="ns:notification/ContentTypeEvent" 
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
    xsi:schemaLocation="ns:notification/ContentTypeEvent resource:notification/ContentTypeEvent" 
    exclude-result-prefixes="n xsi">
    <!-- output an html fragment -->
    <xsl:output method="html" indent="yes" />
    <!-- match everything -->
    <xsl:template match="/n:content" >
        <table class="bord-all">
            <xsl:apply-templates />
        </table>
    </xsl:template>
    <!--  match message element in the default namespace and render as strong -->
    <xsl:template match="n:message" >
        <caption>
            <strong><xsl:value-of select="." disable-output-escaping="yes"/></strong>
        </caption>
    </xsl:template>
    <!-- match on event in the default namespace and display all children -->
    <xsl:template match="n:event">
        <tr>
            <td class="thnormal"><strong>Summary: </strong></td>
            <td class="thnormal"><xsl:value-of select="n:summary" /></td>
        </tr>
        <tr>
            <td class="thnormal"><strong>Description: </strong></td>
            <td class="thnormal"><xsl:value-of select="n:description" /></td>
        </tr>
        <tr>
            <td class="thnormal"><strong>Location: </strong></td>
            <td class="thnormal"><xsl:value-of select="n:location" /></td>
        </tr>
        <tr>
            <td class="thnormal"><strong>Start Time: </strong></td>
            <td class="thnormal"><xsl:value-of select="n:startDateTime" /></td>
        </tr>
        <tr>
            <td class="thnormal"><strong>End Time: </strong></td>
            <td class="thnormal"><xsl:value-of select="n:stopDateTime" /></td>
        </tr>
    </xsl:template> 
</xsl:stylesheet>')
/

-- NOTIFICATION_PRODUCERS --
INSERT INTO NOTIFICATION_PRODUCERS 
(ID, NAME, DESCRIPTION, CONTACT_INFO) 
VALUES 
(1, 'Notification System', 'This producer represents messages sent from the general message sending forms.', 'kuali-ken-testing@cornell.edu')
/

INSERT INTO NOTIFICATION_PRODUCERS 
(ID, NAME, DESCRIPTION, CONTACT_INFO) 
VALUES 
(2, 'University Library System', 'This producer represents messages sent from the University Library system.', 'kuali-ken-testing@cornell.edu')
/

INSERT INTO NOTIFICATION_PRODUCERS 
(ID, NAME, DESCRIPTION, CONTACT_INFO) 
VALUES 
(3, 'University Events Office', 'This producer represents messages sent from the University Events system.', 'kuali-ken-testing@cornell.edu')
/

-- NOTIFICATION_CHANNELS --
DELETE FROM NOTIFICATION_CHANNELS
/

INSERT INTO NOTIFICATION_CHANNELS
(ID, NAME, DESCRIPTION, SUBSCRIBABLE)
VALUES
(1, 'Kuali Rice Channel', 'This channel is used for sending out information about the Kuali Rice effort.', 'Y')
/

INSERT INTO NOTIFICATION_CHANNELS
(ID, NAME, DESCRIPTION, SUBSCRIBABLE)
VALUES
(2, 'Library Events Channel', 'This channel is used for sending out information about Library Events.', 'Y')
/

INSERT INTO NOTIFICATION_CHANNELS
(ID, NAME, DESCRIPTION, SUBSCRIBABLE)
VALUES
(3, 'Overdue Library Books', 'This channel is used for sending out information about your overdue books.', 'N')
/

INSERT INTO NOTIFICATION_CHANNELS
(ID, NAME, DESCRIPTION, SUBSCRIBABLE)
VALUES
(4, 'Concerts Coming to Campus', 'This channel broadcasts any concerts coming to campus.', 'Y')
/

INSERT INTO NOTIFICATION_CHANNELS
(ID, NAME, DESCRIPTION, SUBSCRIBABLE)
VALUES
(5, 'University Alerts', 'This channel broadcasts general announcements for the university.', 'N')
/

-- NOTIFICATION_CHANNEL_SUBSCRIPTIONS

INSERT INTO USER_CHANNEL_SUBSCRIPTIONS
(ID, CHANNEL_ID, USER_ID)
VALUES
(1, 1, 'TestUser4')
/

-- NOTIFICATION_RECIPIENTS_LISTS

INSERT INTO NOTIFICATION_RECIPIENTS_LISTS
(ID, CHANNEL_ID, RECIPIENT_TYPE, RECIPIENT_ID)
values
(1, 4, 'USER', 'TestUser1')
/

INSERT INTO NOTIFICATION_RECIPIENTS_LISTS
(ID, CHANNEL_ID, RECIPIENT_TYPE, RECIPIENT_ID)
values
(2, 4, 'USER', 'TestUser3')
/

-- NOTIFICATION_CHANNEL_REVIEWERS

INSERT INTO NOTIFICATION_REVIEWERS
(ID, CHANNEL_ID, REVIEWER_TYPE, REVIEWER_ID) 
VALUES 
(1, 1, 'GROUP', 'RiceTeam')
/

INSERT INTO NOTIFICATION_REVIEWERS
(ID, CHANNEL_ID, REVIEWER_TYPE, REVIEWER_ID) 
VALUES 
(2, 5, 'USER', 'TestUser3')
/

INSERT INTO NOTIFICATION_REVIEWERS
(ID, CHANNEL_ID, REVIEWER_TYPE, REVIEWER_ID) 
VALUES 
(3, 5, 'GROUP', 'TestGroup1')
/

-- NOTIFICATION_CHANNEL_PRODUCERS --
DELETE FROM NOTIFICATION_CHANNEL_PRODUCERS
/

INSERT INTO NOTIFICATION_CHANNEL_PRODUCERS 
(CHANNEL_ID, PRODUCER_ID) 
VALUES 
(1, 1)
/

INSERT INTO NOTIFICATION_CHANNEL_PRODUCERS 
(CHANNEL_ID, PRODUCER_ID) 
VALUES 
(2, 1)
/

INSERT INTO NOTIFICATION_CHANNEL_PRODUCERS 
(CHANNEL_ID, PRODUCER_ID) 
VALUES 
(3, 1)
/

INSERT INTO NOTIFICATION_CHANNEL_PRODUCERS 
(CHANNEL_ID, PRODUCER_ID) 
VALUES 
(4, 1)
/

INSERT INTO NOTIFICATION_CHANNEL_PRODUCERS 
(CHANNEL_ID, PRODUCER_ID) 
VALUES 
(5, 1)
/

INSERT INTO NOTIFICATION_CHANNEL_PRODUCERS 
(CHANNEL_ID, PRODUCER_ID) 
VALUES 
(2, 2)
/

INSERT INTO NOTIFICATION_CHANNEL_PRODUCERS 
(CHANNEL_ID, PRODUCER_ID) 
VALUES 
(3, 2)
/

INSERT INTO NOTIFICATION_CHANNEL_PRODUCERS 
(CHANNEL_ID, PRODUCER_ID) 
VALUES 
(4, 3)
/

