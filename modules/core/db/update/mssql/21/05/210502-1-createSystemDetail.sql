create table PFA_SYSTEM_DETAIL (
    ID uniqueidentifier,
    VERSION integer not null,
    CREATE_TS datetime2,
    CREATED_BY varchar(50),
    UPDATE_TS datetime2,
    UPDATED_BY varchar(50),
    DELETE_TS datetime2,
    DELETED_BY varchar(50),
    --
    EQUIPMENT_TYPE_ID uniqueidentifier,
    PART_NUMBER varchar(255),
    QTY integer,
    SYSTEM_ID uniqueidentifier not null,
    --
    primary key nonclustered (ID)
);