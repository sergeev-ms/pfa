create table PFA_EQUIPMENT_UTILIZATION_DETAIL_VALUE (
    ID uniqueidentifier,
    VERSION integer not null,
    CREATE_TS datetime2,
    CREATED_BY nvarchar(50),
    UPDATE_TS datetime2,
    UPDATED_BY nvarchar(50),
    DELETE_TS datetime2,
    DELETED_BY nvarchar(50),
    --
    DETAIL_ID uniqueidentifier,
    VALUE_TYPE_ID uniqueidentifier,
    VALUE_ decimal(19, 2),
    --
    primary key nonclustered (ID)
);