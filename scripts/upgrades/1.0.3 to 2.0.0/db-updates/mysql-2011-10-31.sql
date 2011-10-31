-- Fix existing data
update krms_typ_t set nmspc_cd = 'KRMS_TEST'
where srvc_nm = 'notificationPeopleFlowActionTypeService' and nmspc_cd = 'KRMS'
;
update krms_typ_t set nmspc_cd = 'KRMS_TEST'
where srvc_nm = 'approvalPeopleFlowActionTypeService' and nmspc_cd = 'KRMS'
;

insert into krms_attr_defn_s values(null);
insert into krms_attr_defn_t
(attr_defn_id, nm, nmspc_cd, lbl, actv, ver_nbr, desc_txt)
values ((select max(id) from krms_attr_defn_s)
        ,'peopleFlowId', 'KRMS_TEST', 'People Flow ID', 'Y', 1, 'the people flow id')
;

insert into krms_typ_attr_s values(null);
insert into krms_typ_attr_t
(typ_attr_id, seq_no, typ_id, attr_defn_id, actv, ver_nbr)
values ((select max(id) from krms_typ_attr_s), 1,
        (select typ_id from krms_typ_t where srvc_nm = 'notificationPeopleFlowActionTypeService' and nmspc_cd = 'KRMS_TEST') ,
        (select attr_defn_id from krms_attr_defn_t where nm = 'peopleFlowId' and nmspc_cd = 'KRMS_TEST'),
        'Y',1)
;

insert into krms_typ_attr_s values(null);
insert into krms_typ_attr_t
(typ_attr_id, seq_no, typ_id, attr_defn_id, actv, ver_nbr)
values ((select max(id) from krms_typ_attr_s), 1,
        (select typ_id from krms_typ_t where srvc_nm = 'approvalPeopleFlowActionTypeService' and nmspc_cd = 'KRMS_TEST') ,
        (select attr_defn_id from krms_attr_defn_t where nm = 'peopleFlowId' and nmspc_cd = 'KRMS_TEST'),
        'Y',1)
;
