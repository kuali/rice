update KREW_DOC_TYP_T set POST_PRCSR='org.kuali.rice.edl.framework.workflow.EDocLitePostProcessor'
where POST_PRCSR='org.kuali.rice.kew.edl.EDocLitePostProcessor'
/

update KREW_DOC_TYP_T set POST_PRCSR='org.kuali.rice.edl.framework.workflow.EDocLiteDatabasePostProcessor'
where POST_PRCSR='org.kuali.rice.kew.edl.EDLDatabasePostProcessor'
/

UPDATE KREW_DOC_TYP_T SET PARNT_ID='2681' WHERE DOC_TYP_NM='TravelAccountMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET PARNT_ID='2681' WHERE DOC_TYP_NM='FiscalOfficerMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET PARNT_ID='2681' WHERE DOC_TYP_NM='TravelRequest'
/