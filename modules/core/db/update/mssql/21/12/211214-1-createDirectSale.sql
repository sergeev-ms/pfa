create table PFA_DIRECT_SALE (
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
    DATE_ datetime2,
    RECORD_TYPE nvarchar(50),
    STATUS nvarchar(50),
    PROBABILITY decimal(19, 2),
    B_SHARE decimal(19, 2),
    --
    primary key nonclustered (ID)
);