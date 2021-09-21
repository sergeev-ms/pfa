create table PFA_SUPPLEMENTARY_DETAIL (
    ID uniqueidentifier,
    VERSION integer not null,
    CREATE_TS datetime2,
    CREATED_BY nvarchar(50),
    UPDATE_TS datetime2,
    UPDATED_BY nvarchar(50),
    DELETE_TS datetime2,
    DELETED_BY nvarchar(50),
    --
    TYPE_ID uniqueidentifier,
    VALUE_ decimal(19, 2),
    SUPPLEMENTARY_ID uniqueidentifier not null,
    YEAR_ integer,
    MONTH_ integer,
    --
    primary key nonclustered (ID)
);