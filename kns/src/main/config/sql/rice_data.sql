-- Concatenating KNSBootstrap.sql
-- KNS Core Data --
-- Core Doc Group Codes --
INSERT INTO FP_DOC_GROUP_T VALUES ('KR', '054EDFB3B260C8D2E043816FD881C8D2', 1, 'Kuali Rice', null)
/
INSERT INTO FP_DOC_GROUP_T VALUES ('MO', '054EDFB3B260C8D2E043816FD881C8EE', 1,	'Obsolete Maintenance Table', null)
/
INSERT INTO FP_DOC_GROUP_T VALUES ('MR', '054EDFB3B260C8D2E043816FD881C8EA', 1,	'Reference Table Maintenance', null)
/
INSERT INTO FP_DOC_TYPE_T VALUES ('PTYP', '1A6FEB2501C7607EE043814FD111607E', 1, 'MO', 'Parameter Type', 'N', 'Y', 'N', 0, 'N', 'N')
/
INSERT INTO FP_DOC_TYPE_T VALUES ('PDTP', '1A6FEB2501C7607EE043814FD112607E', 1, 'MR', 'Parameter Detailed Type', 'N', 'Y', 'N', 0, 'N', 'N')
/
INSERT INTO FP_DOC_TYPE_T VALUES ('PNMS', '1A6FEB2501C7607EE043814FD113607E', 1, 'MR', 'Parameter Namespace', 'N', 'Y', 'N', 0, 'N', 'N')
/

-- Core Params and Param Types --
insert into SH_PARM_TYP_T values ('CONFG', 121223422, 0,'Config',1)
/
insert into SH_PARM_TYP_T values ('AUTH', 121223423, 0,'Authorization',1)
/
insert into SH_PARM_NMSPC_T values ('KR-NS', 121223424, 0, 'Kuali Rice', 1)
/
INSERT INTO sh_parm_t
(SH_PARM_NMSPC_CD, SH_PARM_DTL_TYP_CD, SH_PARM_NM, SH_PARM_TYP_CD, SH_PARM_TXT, SH_PARM_DESC, SH_PARM_CONS_CD, WRKGRP_NM)
VALUES
('KR-NS','Lookup','RESULTS_DEFAULT_MAX_COLUMN_LENGTH','CONFG','70','If a maxLength attribute has not been set on a lookup result field in the data dictionary, then the result column''s max length will be the value of this parameter. Set this parameter to 0 for an unlimited default length or a positive value (i.e. greater than 0) for a finite max length.','A','KUALI_FMSOPS')
/
INSERT INTO sh_parm_t
(SH_PARM_NMSPC_CD, SH_PARM_DTL_TYP_CD, SH_PARM_NM, SH_PARM_TYP_CD, SH_PARM_TXT, SH_PARM_DESC, SH_PARM_CONS_CD, WRKGRP_NM)
VALUES
('KR-NS','Lookup','RESULTS_LIMIT','CONFG','70','If a maxLength attribute has not been set on a lookup result field in the data dictionary, then the result column''s max length will be the value of this parameter. Set this parameter to 0 for an unlimited default length or a positive value (i.e. greater than 0) for a finite max length.','A','KUALI_FMSOPS')
/
INSERT INTO SH_PARM_T
(SH_PARM_NMSPC_CD, SH_PARM_DTL_TYP_CD, SH_PARM_NM, SH_PARM_TYP_CD, SH_PARM_TXT, SH_PARM_DESC, SH_PARM_CONS_CD, ACTIVE_IND)
VALUES
('KR-NS','Lookup','MULTIPLE_VALUE_RESULTS_EXPIRATION_SECONDS','CONFG','60','Limit results returned for lookup - seconds expiration','A','Y')
/
INSERT INTO SH_PARM_T
(SH_PARM_NMSPC_CD, SH_PARM_DTL_TYP_CD, SH_PARM_NM, SH_PARM_TYP_CD, SH_PARM_TXT, SH_PARM_DESC, SH_PARM_CONS_CD, ACTIVE_IND)
VALUES
('KR-NS','Lookup','MULTIPLE_VALUE_RESULTS_PER_PAGE','CONFG','100','Limit results returned for lookup - page','A','Y')
/
insert into SH_NTE_TYP_T values ('BO', '2D3C44FE49415102E043814FD8815102',  1,  'DOCUMENT BUSINESS OBJECT', 'Y')
/
insert into SH_NTE_TYP_T values ('DH', '2D3C44FE49425102E043814FD8815102',  1,  'DOCUMENT HEADER', 'Y')
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

insert into FP_DOC_STATUS_T values ('A',    '2E0671732A684002E043814FD8814002', 1,  'Approved')
/
insert into FP_DOC_STATUS_T values ('C',    '2E0671732A694002E043814FD8814002', 1,  'Cancelled')
/
insert into FP_DOC_STATUS_T values ('E',    '2E0671732A6A4002E043814FD8814002', 1,  'Extracted')
/
insert into FP_DOC_STATUS_T values ('I',    '2E0671732A6B4002E043814FD8814002', 1,  'In Process')
/
insert into FP_DOC_STATUS_T values ('II',   '2E0671732A6C4002E043814FD8814002', 1,  'In Process')
/
insert into FP_DOC_STATUS_T values ('O',    '2E0671732A6D4002E043814FD8814002', 1,  'Pend Org')
/
insert into FP_DOC_STATUS_T values ('OO',   '2E0671732A6E4002E043814FD8814002', 1,  'Pend Org')
/
insert into FP_DOC_STATUS_T values ('P',    '2E0671732A6F4002E043814FD8814002', 1,  'Pend Acct')
/
insert into FP_DOC_STATUS_T values ('PP',   '2E0671732A704002E043814FD8814002', 1,  'Pend Acct')
/
insert into FP_DOC_STATUS_T values ('R',    '2E0671732A714002E043814FD8814002', 1,  'Pend Specl')
/
insert into FP_DOC_STATUS_T values ('RR',   '2E0671732A724002E043814FD8814002', 1,  'Pend Specl')
/
insert into FP_DOC_STATUS_T values ('S',    '2E0671732A734002E043814FD8814002', 1,  'Pend CG')
/
insert into FP_DOC_STATUS_T values ('V',    '2E0671732A744002E043814FD8814002', 1,  'Validation')
/
insert into FP_DOC_STATUS_T values ('Q',    '2E0671732A754002E043814FD8814002', 1,  'Doc Specif')
/

-- Concatenating KEWBootstrap.sql
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

insert into en_usr_t values ('admin','admin','admin','admin','admin@localhost','admin','admin','admin',to_date('01/01/2000', 'dd/mm/yyyy'),to_date('01/01/2100', 'dd/mm/yyyy'),0,0)
/
insert into en_wrkgrp_t values (1,1,'WorkflowAdmin',1,'W','Workflow Administrator Workgroup',1,null,0)
/
insert into EN_WRKGRP_MBR_T values ('admin', 1, 'U', 1, 0)
/

-- Concatenating KSBBootstrap.sql
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
-- Concatenating KIMBootstrap.sql
INSERT INTO KIM_NAMESPACES_T (ID, NAME, DESCRIPTION) VALUES (1, 'KIM', 'This record represents the actual KIM system and must always be loaded by default in order for the system to work properly.')
/
INSERT INTO FP_DOC_TYPE_T values ('KPMD', SYS_GUID(), 1, 'KR', 'PRINCIPAL', 'N', 'Y', 'N', 0, 'N', 'N')
/
INSERT INTO FP_DOC_TYPE_T values ('KGMD', SYS_GUID(), 1, 'KR', 'GROUP', 'N', 'Y', 'N', 0, 'N', 'N')
/
INSERT INTO FP_DOC_TYPE_T values ('KRMD', SYS_GUID(), 1, 'KR', 'ROLE', 'N', 'Y', 'N', 0, 'N', 'N')
/
INSERT INTO FP_DOC_TYPE_T values ('KGAM', SYS_GUID(), 1, 'KR', 'GROUP ATTRIBUTE', 'N', 'Y', 'N', 0, 'N', 'N')
/

-- Concatenating KENBootstrap.sql
-- KEN Core Data --

-- KEN Core Producers --
INSERT INTO NOTIFICATION_PRODUCERS
(ID, NAME, DESCRIPTION, CONTACT_INFO)
VALUES
(1, 'Notification System', 'This producer represents messages sent from the general message sending forms.', 'kuali-ken-testing@cornell.edu')
/

-- KEN Core Priorities --
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

-- KEN Core Priorities --
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

