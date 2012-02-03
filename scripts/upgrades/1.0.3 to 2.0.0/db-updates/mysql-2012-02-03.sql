-- KULRICE-6630: Add SQL scripts for missing data in master database

insert into krew_typ_t values ('1', 'Sample Type', 'KR-SAP', 'sampleAppPeopleFlowTypeService', 'Y', 1);

insert into krew_attr_defn_t values ('1', 'number', 'KR-SAP', 'Travel Number', 'Y', 'edu.sampleu.travel.bo.TravelAccount', 1, null);

insert into krew_attr_defn_t values ('2', 'id', 'KR-SAP', null, 'Y', 'edu.sampleu.travel.bo.FiscalOfficer', 1, null);

insert into krew_typ_attr_t values ('1', 1, '1', '1', 'Y', 1);

insert into krew_typ_attr_t values ('2', 2, '1', '2', 'Y', 1);

