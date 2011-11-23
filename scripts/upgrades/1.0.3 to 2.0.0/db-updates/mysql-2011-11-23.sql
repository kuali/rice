-- give PeopleFlows friendlier names

update krms_typ_t set nm='Notify PeopleFlow' where typ_id = '1000';
update krms_typ_t set nm='Route to PeopleFlow' where typ_id = '1001';

-- remove constraint that is preventing compound props from persisting

alter table krms_cmpnd_prop_props_t modify seq_no decimal(5,0) default null;
