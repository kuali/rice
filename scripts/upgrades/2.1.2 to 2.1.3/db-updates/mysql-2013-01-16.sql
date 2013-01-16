
-- KULRICE-8177: CONTRIB: Identity Mgmt Section listed as "Undefined"
update KREW_DOC_TYP_T set LBL = 'Identity Management Document' where
  DOC_TYP_NM = 'IdentityManagementDocument' and LBL = 'Undefined'
;