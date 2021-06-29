create table PFA_EQUIPMENT_UTILIZATION_DETAIL (
    ID uniqueidentifier,
    VERSION integer not null,
    CREATE_TS datetime2,
    CREATED_BY nvarchar(50),
    UPDATE_TS datetime2,
    UPDATED_BY nvarchar(50),
    DELETE_TS datetime2,
    DELETED_BY nvarchar(50),
    --
    EQUIPMENT_TYPE_ID uniqueidentifier,
    FIRST_RUN_VALUE decimal(19, 2),
    SEQUENT_RUN_VALUE decimal(19, 2),
    EQUIPMENT_UTILIZATION_ID uniqueidentifier not null,
    REVENUE_MODE nvarchar(50),
    ORDER_ integer,
    --
    primary key nonclustered (ID)
);