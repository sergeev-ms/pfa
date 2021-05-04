create table CUBAAT_SSH_CREDENTIALS (
    ID uniqueidentifier,
    VERSION integer not null,
    CREATE_TS datetime2,
    CREATED_BY varchar(50),
    UPDATE_TS datetime2,
    UPDATED_BY varchar(50),
    DELETE_TS datetime2,
    DELETED_BY varchar(50),
    --
    HOSTNAME varchar(255) not null,
    SESSION_NAME varchar(255) not null,
    IS_FOR_EVERYONE tinyint,
    PORT integer not null,
    LOGIN varchar(255) not null,
    PRIVATE_KEY_ID uniqueidentifier,
    --
    primary key nonclustered (ID)
);