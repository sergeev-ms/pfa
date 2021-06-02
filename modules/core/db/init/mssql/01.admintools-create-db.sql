-- begin DDCRD_DIAGNOSE_EXECUTION_LOG
create table DDCRD_DIAGNOSE_EXECUTION_LOG (
    ID uniqueidentifier,
    VERSION integer not null,
    CREATE_TS datetime2,
    CREATED_BY nvarchar(50),
    UPDATE_TS datetime2,
    UPDATED_BY nvarchar(50),
    DELETE_TS datetime2,
    DELETED_BY nvarchar(50),
    --
    EXECUTION_SUCCESSFUL tinyint not null,
    EXECUTION_TIMESTAMP datetime2 not null,
    EXECUTION_USER nvarchar(255),
    EXECUTION_RESULT_FILE_ID uniqueidentifier,
    DIAGNOSE_TYPE nvarchar(255) not null,
    EXECUTION_TYPE nvarchar(255) not null,
    --
    primary key nonclustered (ID)
)^
-- end DDCRD_DIAGNOSE_EXECUTION_LOG
-- begin CUBAAT_SSH_CREDENTIALS
create table CUBAAT_SSH_CREDENTIALS (
    ID uniqueidentifier,
    VERSION integer not null,
    CREATE_TS datetime2,
    CREATED_BY nvarchar(50),
    UPDATE_TS datetime2,
    UPDATED_BY nvarchar(50),
    DELETE_TS datetime2,
    DELETED_BY nvarchar(50),
    --
    HOSTNAME nvarchar(255) not null,
    SESSION_NAME nvarchar(255) not null,
    IS_FOR_EVERYONE tinyint,
    PORT integer not null,
    LOGIN nvarchar(255) not null,
    PRIVATE_KEY_ID uniqueidentifier,
    --
    primary key nonclustered (ID)
)^
-- end CUBAAT_SSH_CREDENTIALS
