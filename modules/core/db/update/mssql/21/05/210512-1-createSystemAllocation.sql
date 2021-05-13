create table PFA_SYSTEM_ALLOCATION (
    ID uniqueidentifier,
    VERSION integer not null,
    CREATE_TS datetime2,
    CREATED_BY varchar(50),
    UPDATE_TS datetime2,
    UPDATED_BY varchar(50),
    DELETE_TS datetime2,
    DELETED_BY varchar(50),
    --
    APPLICATION_DATA_ID uniqueidentifier not null,
    SYSTEM_ID uniqueidentifier,
    RUN1 decimal(19, 2),
    RUN2 decimal(19, 2),
    RUN3 decimal(19, 2),
    RUN3_PLUS decimal(19, 2),
    --
    primary key nonclustered (ID)
);