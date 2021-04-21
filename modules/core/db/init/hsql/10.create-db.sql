-- begin PFA_ACCOUNT
create table PFA_ACCOUNT (
    ID varchar(36) not null,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    NAME varchar(255),
    TYPE_ varchar(50),
    --
    primary key (ID)
)^
-- end PFA_ACCOUNT
-- begin PFA_ACTIVITY
create table PFA_ACTIVITY (
    ID varchar(36) not null,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    ACCOUNT_ID varchar(36),
    YEAR_ integer,
    --
    primary key (ID)
)^
-- end PFA_ACTIVITY
-- begin PFA_ACTIVITY_DETAIL
create table PFA_ACTIVITY_DETAIL (
    ID varchar(36) not null,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    RECORD_TYPE varchar(50),
    CONTRACT_TYPE varchar(50),
    JOB_TYPE varchar(50),
    WELL_EQUIP varchar(50),
    WELL_TAG varchar(50),
    YEAR_MONTH_ varchar(10),
    VALUE_ integer,
    ACTIVITY_ID varchar(36) not null,
    --
    primary key (ID)
)^
-- end PFA_ACTIVITY_DETAIL
