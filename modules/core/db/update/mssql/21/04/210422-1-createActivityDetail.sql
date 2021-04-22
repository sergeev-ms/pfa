create table PFA_ACTIVITY_DETAIL (
    ID uniqueidentifier,
    VERSION integer not null,
    CREATE_TS datetime2,
    CREATED_BY varchar(50),
    UPDATE_TS datetime2,
    UPDATED_BY varchar(50),
    DELETE_TS datetime2,
    DELETED_BY varchar(50),
    --
    RECORD_TYPE varchar(50),
    CONTRACT_TYPE varchar(50),
    JOB_TYPE varchar(50),
    WELL_EQUIP varchar(50),
    WELL_TAG varchar(50),
    YEAR_ integer,
    MONTH_ integer,
    VALUE_ integer,
    ACTIVITY_ID uniqueidentifier not null,
    --
    primary key nonclustered (ID)
);