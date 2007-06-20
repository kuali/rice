CREATE TABLE en_dirty_cache_t (
    cache_entry_id number(19) not null,
    cache_name varchar2(2000) not null,
    cache_id varchar2(2000) not null,
    ip_address varchar2(2000) not null,
CONSTRAINT en_dirty_cache_t_PK PRIMARY KEY (cache_entry_id) USING INDEX
)
/