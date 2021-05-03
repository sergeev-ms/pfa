create table PFA_EQUIPMENT_BREAKDOWN (
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
    FIRST_RUN_VALUE decimal(19, 2),
    SEQUENT_RUN_VALUE decimal(19, 2),
    APPLICATION_DATA_ID uniqueidentifier not null,
    --
    primary key nonclustered (ID)
);