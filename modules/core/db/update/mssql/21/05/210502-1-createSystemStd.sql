create table PFA_SYSTEM_STD (
    ID uniqueidentifier,
    VERSION integer not null,
    CREATE_TS datetime2,
    CREATED_BY varchar(50),
    UPDATE_TS datetime2,
    UPDATED_BY varchar(50),
    DELETE_TS datetime2,
    DELETED_BY varchar(50),
    --
    SYSTEM_ID varchar(255),
    CASING_SIZE varchar(50),
    CASING_WEIGHT varchar(50),
    PUMP varchar(255),
    MOTOR_POWER integer,
    HEAD integer,
    MOTOR varchar(255),
    COMMENT_ varchar(255),
    --
    primary key nonclustered (ID)
);