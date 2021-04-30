create table PFA_MARKET_DATA (
    ID uniqueidentifier,
    VERSION integer not null,
    CREATE_TS datetime2,
    CREATED_BY varchar(50),
    UPDATE_TS datetime2,
    UPDATED_BY varchar(50),
    DELETE_TS datetime2,
    DELETED_BY varchar(50),
    --
    YEAR_ integer,
    MONTH_ integer,
    CONTRACT_TYPE varchar(50),
    APPLICATION_TYPE varchar(255),
    FIELD_TYPE varchar(50),
    RUNS_NUMBER varchar(50),
    F_RUN_DURATION integer,
    S_RUN_DURATION integer,
    TH_RUN_DURATION integer,
    THP_RUN_DURATION integer,
    TRL integer,
    ARL integer,
    ACCOUNT_ID uniqueidentifier not null,
    --
    primary key nonclustered (ID)
);