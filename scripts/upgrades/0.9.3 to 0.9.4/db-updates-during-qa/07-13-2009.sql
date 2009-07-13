-- KULRICE-3126 This will turn off field level help by default.
UPDATE KRNS_PARM_T SET TXT = 'N' WHERE NMSPC_CD = 'KR-NS' AND PARM_DTL_TYP_CD = 'All' AND PARM_NM = 'ENABLE_FIELD_LEVEL_HELP_IND'
/
-- KULRICE-3349: Add the doc handler URL to the Campus, Campus Type, Country, County, Postal Code, and State document types.
UPDATE KREW_DOC_TYP_T SET DOC_HDLR_URL='${kr.url}/maintenance.do?methodToCall=docHandler' WHERE DOC_TYP_NM='CampusMaintenanceDocument' OR DOC_TYP_NM='CampusTypeMaintenanceDocument' OR DOC_TYP_NM='CountryMaintenanceDocument' OR DOC_TYP_NM='CountyMaintenanceDocument' OR DOC_TYP_NM='PostalCodeMaintenanceDocument' OR DOC_TYP_NM='StateMaintenanceDocument'
/