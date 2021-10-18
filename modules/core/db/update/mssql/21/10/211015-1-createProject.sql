create table PFA_PROJECT (
    ID uniqueidentifier,
    VERSION integer not null,
    CREATE_TS datetime2,
    CREATED_BY nvarchar(50),
    UPDATE_TS datetime2,
    UPDATED_BY nvarchar(50),
    DELETE_TS datetime2,
    DELETED_BY nvarchar(50),
    --
    ACCOUNT_ID uniqueidentifier,
    CUSTOMER_NO integer,
    CUSTOMER_ID decimal(7),
    REGION nvarchar(255),
    WELL_ID nvarchar(255),
    WELL nvarchar(255),
    WELL_API nvarchar(255),
    --
    primary key nonclustered (ID)
);