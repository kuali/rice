DROP TABLE KRIM_PERM_RQRD_ATTR_T
/
DROP TABLE KRIM_RSP_RQRD_ATTR_T
/

-- KULRICE-3287

DELETE FROM KRNS_PARM_T where NMSPC_CD='KR-WKFLW' AND PARM_DTL_TYP_CD='All' AND PARM_NM='APPLICATION_CONTEXT'
/

-- KULRICE-3278

DECLARE

    ref_perm_id VARCHAR2(40);

BEGIN

    SELECT perm_id INTO ref_perm_id FROM krim_perm_attr_data_t WHERE attr_val = 'org.kuali.rice.kew.web.backdoor.AdministrationAction';
    DELETE FROM krim_perm_attr_data_t WHERE perm_id = ref_perm_id;
    DELETE FROM krim_role_perm_t WHERE perm_id = ref_perm_id;
    DELETE FROM krim_perm_t WHERE perm_id = ref_perm_id;

END;
/ 