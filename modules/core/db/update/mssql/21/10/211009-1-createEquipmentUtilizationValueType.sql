create table PFA_EQUIPMENT_UTILIZATION_VALUE_TYPE (
    ID uniqueidentifier,
    VERSION integer not null,
    CREATE_TS datetime2,
    CREATED_BY nvarchar(50),
    UPDATE_TS datetime2,
    UPDATED_BY nvarchar(50),
    DELETE_TS datetime2,
    DELETED_BY nvarchar(50),
    --
    NAME nvarchar(255),
    --
    primary key nonclustered (ID)
);