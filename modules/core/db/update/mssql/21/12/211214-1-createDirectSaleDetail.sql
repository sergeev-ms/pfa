create table PFA_DIRECT_SALE_DETAIL (
    ID uniqueidentifier,
    VERSION integer not null,
    CREATE_TS datetime2,
    CREATED_BY nvarchar(50),
    UPDATE_TS datetime2,
    UPDATED_BY nvarchar(50),
    DELETE_TS datetime2,
    DELETED_BY nvarchar(50),
    --
    DIRECT_SALE_ID uniqueidentifier not null,
    PART_ID uniqueidentifier,
    LENGTH decimal(19,6),
    PRICE integer,
    --
    primary key nonclustered (ID)
);