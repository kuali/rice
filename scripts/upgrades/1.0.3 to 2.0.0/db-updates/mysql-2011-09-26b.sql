-- KIM permissions
insert into krcr_nmspc_t
(nmspc_cd, nm, actv_ind, appl_id, ver_nbr, obj_id)
values ('KR-RULE','Kuali Rules','Y','RICE',1,uuid())
;

insert into krim_perm_tmpl_id_s values (null);
insert into krim_perm_tmpl_t
(perm_tmpl_id, nm, nmspc_cd, desc_txt, kim_typ_id, actv_ind, ver_nbr, obj_id)
values ((select max(id) from krim_perm_tmpl_id_s),'KRMS Agenda Permission','KR-RULE','View/Maintain Agenda',
        (select kim_typ_id from krim_typ_t where nm = 'Document Type (Permission)' and nmspc_cd = 'KR-SYS'),
        'Y',1,uuid())
;

insert into krim_perm_id_s values (null);
insert into krim_perm_t
(perm_id, perm_tmpl_id, nmspc_cd, nm, desc_txt, actv_ind, ver_nbr, obj_id)
values ((select max(id) from krim_perm_id_s),
        (select perm_tmpl_id from krim_perm_tmpl_t where nm = 'KRMS Agenda Permission' and nmspc_cd = 'KR-RULE'),
        'KR-RULE','Maintain KRMS Agenda','Allows creation and modification of agendas via the agenda editor','Y',1,uuid())
;

insert into krim_attr_defn_id_s values (null);
insert into krim_perm_attr_data_t
(attr_data_id, perm_id, kim_typ_id, kim_attr_defn_id, attr_val, ver_nbr, obj_id)
values ((select max(id) from krim_attr_defn_id_s),
        (select perm_id from krim_perm_t where nm = 'Maintain KRMS Agenda' and nmspc_cd = 'KR-RULE'),
        (select kim_typ_id from krim_typ_t where nm = 'Document Type (Permission)' and nmspc_cd = 'KR-SYS'),
        (select kim_attr_defn_id from krim_attr_defn_t where nm = 'documentTypeName'),
        'AgendaEditorMaintenanceDocument',1,uuid())
;

insert into krim_perm_id_s values (null);
insert into krim_perm_t
(perm_id, perm_tmpl_id, nmspc_cd, nm, desc_txt, actv_ind, ver_nbr, obj_id)
values ((select max(id) from krim_perm_id_s),
        (select perm_tmpl_id from krim_perm_tmpl_t where nm = 'KRMS Agenda Permission' and nmspc_cd = 'KR-RULE'),
        'KR-RULE','View KRMS Agenda','Allows viewing of agendas via the agenda editor','Y',1,uuid())
;

insert into krim_attr_defn_id_s values (null);
insert into krim_perm_attr_data_t
(attr_data_id, perm_id, kim_typ_id, kim_attr_defn_id, attr_val, ver_nbr, obj_id)
values ((select max(id) from krim_attr_defn_id_s),
        (select perm_id from krim_perm_t where nm = 'View KRMS Agenda' and nmspc_cd = 'KR-RULE'),
        (select kim_typ_id from krim_typ_t where nm = 'Document Type (Permission)' and nmspc_cd = 'KR-SYS'),
        (select kim_attr_defn_id from krim_attr_defn_t where nm = 'documentTypeName'),
        'AgendaEditorMaintenanceDocument',1,uuid())
;

-- roles
insert into krim_role_id_s values (null);
insert into krim_role_t
(role_id, role_nm, nmspc_cd, desc_txt, kim_typ_id, actv_ind, last_updt_dt, obj_id)
values ((select max(id) from krim_role_id_s),
        'Kuali Rules Management System Administrator',
        'KR-RULE',
        'This role maintains KRMS agendas and rules.',
        (select kim_typ_id from krim_typ_t where nm = 'Default' and nmspc_cd = 'KUALI'),
        'Y', curdate(), uuid())
;

insert into krim_role_mbr_id_s values (null);
insert into krim_role_mbr_t
(role_mbr_id, role_id, mbr_id, mbr_typ_cd, last_updt_dt, ver_nbr, obj_id)
values ((select max(id) from krim_role_mbr_id_s),
        (select role_id from krim_role_t where role_nm = 'Kuali Rules Management System Administrator' and nmspc_cd = 'KR-RULE'),
        (select prncpl_id from krim_prncpl_t where prncpl_nm = 'admin'),
        'P', curdate(), 1, uuid())
;

insert into krim_role_perm_id_s values (null);
insert into krim_role_perm_t
(role_perm_id, role_id, perm_id, actv_ind, ver_nbr, obj_id)
values ((select max(id) from krim_role_perm_id_s),
        (select role_id from krim_role_t where role_nm = 'Kuali Rules Management System Administrator' and nmspc_cd = 'KR-RULE'),
        (select perm_id from krim_perm_t where nm = 'Maintain KRMS Agenda' and nmspc_cd = 'KR-RULE'),
        'Y', 1, uuid())
;

insert into krim_role_id_s values (null);
insert into krim_role_t
(role_id, role_nm, nmspc_cd, desc_txt, kim_typ_id, actv_ind, last_updt_dt, obj_id)
values ((select max(id) from krim_role_id_s),
        'Kuali Rules Management System Viewer',
        'KR-RULE',
        'This role views KRMS agendas and rules.',
        (select kim_typ_id from krim_typ_t where nm = 'Default' and nmspc_cd = 'KUALI'),
        'Y', curdate(), uuid())
;

insert into krim_role_mbr_id_s values (null);
insert into krim_role_mbr_t
(role_mbr_id, role_id, mbr_id, mbr_typ_cd, last_updt_dt, ver_nbr, obj_id)
values ((select max(id) from krim_role_mbr_id_s),
        (select role_id from krim_role_t where role_nm = 'Kuali Rules Management System Viewer' and nmspc_cd = 'KR-RULE'),
        (select prncpl_id from krim_prncpl_t where prncpl_nm = 'kuluser'),
        'P', curdate(), 1, uuid())
;

insert into krim_role_perm_id_s values (null);
insert into krim_role_perm_t
(role_perm_id, role_id, perm_id, actv_ind, ver_nbr, obj_id)
values ((select max(id) from krim_role_perm_id_s),
        (select role_id from krim_role_t where role_nm = 'Kuali Rules Management System Viewer' and nmspc_cd = 'KR-RULE'),
        (select perm_id from krim_perm_t where nm = 'View KRMS Agenda' and nmspc_cd = 'KR-RULE'),
        'Y', 1, uuid())
;