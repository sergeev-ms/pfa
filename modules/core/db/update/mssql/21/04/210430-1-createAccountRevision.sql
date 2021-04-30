create table PFA_ACCOUNT_REVISION (
    ID uniqueidentifier,
    VERSION integer not null,
    CREATE_TS datetime2,
    CREATED_BY varchar(50),
    UPDATE_TS datetime2,
    UPDATED_BY varchar(50),
    DELETE_TS datetime2,
    DELETED_BY varchar(50),
    --
    YEAR_ integer,
    MONTH_ integer,
    MANAGER_ID uniqueidentifier,
    TYPE_ varchar(50),
    ACCOUNT_ID uniqueidentifier not null,
    --
    primary key nonclustered (ID)
);