create table PFA_ACTIVITY (
    ID uniqueidentifier,
    VERSION integer not null,
    CREATE_TS datetime2,
    CREATED_BY varchar(50),
    UPDATE_TS datetime2,
    UPDATED_BY varchar(50),
    DELETE_TS datetime2,
    DELETED_BY varchar(50),
    --
    ACCOUNT_ID uniqueidentifier,
    RECORD_TYPE varchar(50),
    YEAR_ integer,
    --
    primary key nonclustered (ID)
);