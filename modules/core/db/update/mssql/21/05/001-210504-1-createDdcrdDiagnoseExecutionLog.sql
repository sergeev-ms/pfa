create table DDCRD_DIAGNOSE_EXECUTION_LOG (
    ID uniqueidentifier,
    VERSION integer not null,
    CREATE_TS datetime2,
    CREATED_BY varchar(50),
    UPDATE_TS datetime2,
    UPDATED_BY varchar(50),
    DELETE_TS datetime2,
    DELETED_BY varchar(50),
    --
    EXECUTION_SUCCESSFUL tinyint not null,
    EXECUTION_TIMESTAMP datetime2 not null,
    EXECUTION_USER varchar(255),
    EXECUTION_RESULT_FILE_ID uniqueidentifier,
    DIAGNOSE_TYPE varchar(255) not null,
    EXECUTION_TYPE varchar(255) not null,
    --
    primary key nonclustered (ID)
);