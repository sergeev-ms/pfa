create table PFA_CUSTOMER (
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
    DIM_CUSTOMER_ID decimal(7),
    --
    primary key nonclustered (ID)
);