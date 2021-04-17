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
    YEAR_ integer,
    MONTH_ integer,
    VALUE_ decimal(19, 2),
    ACTIVITY_ID varchar(36) not null,
    --
    primary key (ID)
);