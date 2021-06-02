-- begin PFA_PRICE_LIST_DETAIL
create table PFA_PRICE_LIST_DETAIL (
    ID uniqueidentifier,
    VERSION integer not null,
    CREATE_TS datetime2,
    CREATED_BY nvarchar(50),
    UPDATE_TS datetime2,
    UPDATED_BY nvarchar(50),
    DELETE_TS datetime2,
    DELETED_BY nvarchar(50),
    --
    PRICE_LIST_ID uniqueidentifier not null,
    ANALYTIC_ID uniqueidentifier,
    REVENUE_TYPE_ID uniqueidentifier,
    VALUE_ decimal(19, 2),
    --
    primary key nonclustered (ID)
)^
-- end PFA_PRICE_LIST_DETAIL
-- begin PFA_ACTIVITY
create table PFA_ACTIVITY (
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
    RECORD_TYPE nvarchar(50),
    YEAR_ integer,
    --
    primary key nonclustered (ID)
)^
-- end PFA_ACTIVITY
-- begin PFA_ACTIVITY_DETAIL
create table PFA_ACTIVITY_DETAIL (
    ID uniqueidentifier,
    VERSION integer not null,
    CREATE_TS datetime2,
    CREATED_BY nvarchar(50),
    UPDATE_TS datetime2,
    UPDATED_BY nvarchar(50),
    DELETE_TS datetime2,
    DELETED_BY nvarchar(50),
    --
    RECORD_TYPE nvarchar(50),
    ANALYTIC_ID uniqueidentifier,
    YEAR_ integer,
    MONTH_ integer,
    VALUE_ integer,
    ACTIVITY_ID uniqueidentifier not null,
    --
    primary key nonclustered (ID)
)^
-- end PFA_ACTIVITY_DETAIL
-- begin PFA_PRICE_LIST
create table PFA_PRICE_LIST (
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
    RECORD_TYPE nvarchar(50),
    YEAR_ integer,
    MONTH_ integer,
    --
    primary key nonclustered (ID)
)^
-- end PFA_PRICE_LIST
-- begin PFA_ACCOUNT_REVISION
create table PFA_ACCOUNT_REVISION (
    ID uniqueidentifier,
    VERSION integer not null,
    CREATE_TS datetime2,
    CREATED_BY nvarchar(50),
    UPDATE_TS datetime2,
    UPDATED_BY nvarchar(50),
    DELETE_TS datetime2,
    DELETED_BY nvarchar(50),
    --
    YEAR_ integer,
    MONTH_ integer,
    MANAGER_ID uniqueidentifier,
    TYPE_ nvarchar(50),
    ACCOUNT_ID uniqueidentifier not null,
    --
    primary key nonclustered (ID)
)^
-- end PFA_ACCOUNT_REVISION
-- begin PFA_ACCOUNT
create table PFA_ACCOUNT (
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
    PARENT_ID uniqueidentifier,
    CUSTOMER_ID decimal(7),
    ACTUAL_REVISION_ID uniqueidentifier,
    ACTUAL_MARKET_DETAIL_ID uniqueidentifier,
    ACTUAL_APP_DETAIL_ID uniqueidentifier,
    --
    primary key nonclustered (ID)
)^
-- end PFA_ACCOUNT
-- begin PFA_ANALYTIC_SET
create table PFA_ANALYTIC_SET (
    ID uniqueidentifier,
    VERSION integer not null,
    CREATE_TS datetime2,
    CREATED_BY nvarchar(50),
    UPDATE_TS datetime2,
    UPDATED_BY nvarchar(50),
    DELETE_TS datetime2,
    DELETED_BY nvarchar(50),
    --
    CONTRACT_TYPE nvarchar(50),
    JOB_TYPE nvarchar(50),
    WELL_EQUIP nvarchar(50),
    WELL_TAG nvarchar(50),
    --
    primary key nonclustered (ID)
)^
-- end PFA_ANALYTIC_SET
-- begin PFA_MARKET_DATA
create table PFA_MARKET_DATA (
    ID uniqueidentifier,
    VERSION integer not null,
    CREATE_TS datetime2,
    CREATED_BY nvarchar(50),
    UPDATE_TS datetime2,
    UPDATED_BY nvarchar(50),
    DELETE_TS datetime2,
    DELETED_BY nvarchar(50),
    --
    YEAR_ integer,
    MONTH_ integer,
    RECORD_TYPE nvarchar(50),
    CONTRACT_TYPE nvarchar(50),
    APPLICATION_TYPE nvarchar(50),
    FIELD_TYPE nvarchar(50),
    RUNS_NUMBER nvarchar(50),
    F_RUN_DURATION integer,
    S_RUN_DURATION integer,
    TH_RUN_DURATION integer,
    THP_RUN_DURATION integer,
    TRL integer,
    ARL integer,
    WELL_COUNT integer,
    CONVERSION_RATE decimal(19, 2),
    OIL_PERMITS integer,
    RIG_QTY integer,
    DUC_QTY integer,
    COMPLETION decimal(19, 2),
    ACTIVITY_RATE decimal(19, 2),
    BUDGET integer,
    B_SHARE decimal(19, 2),
    IS_WELL_MONITOR tinyint,
    WELL_MONITOR_QTY integer,
    B_WELL_COUNT integer,
    RENTAL_CAPEX integer,
    ACCOUNT_ID uniqueidentifier not null,
    NEW_WELL_YEAR integer,
    --
    primary key nonclustered (ID)
)^
-- end PFA_MARKET_DATA
-- begin PFA_REVENUE_TYPE
create table PFA_REVENUE_TYPE (
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
    FULL_NAME nvarchar(255),
    ORDER_ integer,
    --
    primary key nonclustered (ID)
)^
-- end PFA_REVENUE_TYPE
-- begin PFA_SYSTEM_DETAIL
create table PFA_SYSTEM_DETAIL (
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
    PART_NUMBER_ID uniqueidentifier,
    QTY integer,
    SYSTEM_ID uniqueidentifier not null,
    --
    primary key nonclustered (ID)
)^
-- end PFA_SYSTEM_DETAIL
-- begin PFA_SYSTEM_STD
create table PFA_SYSTEM_STD (
    ID uniqueidentifier,
    VERSION integer not null,
    CREATE_TS datetime2,
    CREATED_BY nvarchar(50),
    UPDATE_TS datetime2,
    UPDATED_BY nvarchar(50),
    DELETE_TS datetime2,
    DELETED_BY nvarchar(50),
    --
    SYSTEM_ID nvarchar(255),
    CASING_SIZE nvarchar(50),
    CASING_WEIGHT nvarchar(50),
    PUMP nvarchar(255),
    MOTOR_POWER integer,
    HEAD integer,
    MOTOR nvarchar(255),
    COMMENT_ nvarchar(255),
    --
    primary key nonclustered (ID)
)^
-- end PFA_SYSTEM_STD
-- begin PFA_APPLICATION_DATA
create table PFA_APPLICATION_DATA (
    ID uniqueidentifier,
    VERSION integer not null,
    CREATE_TS datetime2,
    CREATED_BY nvarchar(50),
    UPDATE_TS datetime2,
    UPDATED_BY nvarchar(50),
    DELETE_TS datetime2,
    DELETED_BY nvarchar(50),
    --
    YEAR_ integer,
    MONTH_ integer,
    RECORD_TYPE nvarchar(50),
    ACCOUNT_ID uniqueidentifier not null,
    --
    primary key nonclustered (ID)
)^
-- end PFA_APPLICATION_DATA
-- begin PFA_EQUIPMENT_UTILIZATION
create table PFA_EQUIPMENT_UTILIZATION (
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
    APPLICATION_DATA_ID uniqueidentifier not null,
    --
    primary key nonclustered (ID)
)^
-- end PFA_EQUIPMENT_UTILIZATION
-- begin PFA_EQUIPMENT_CATEGORY
create table PFA_EQUIPMENT_CATEGORY (
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
)^
-- end PFA_EQUIPMENT_CATEGORY
-- begin PFA_EQUIPMENT_TYPE
create table PFA_EQUIPMENT_TYPE (
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
    CATEGORY_ID uniqueidentifier,
    MANDATORY tinyint,
    REVENUE_TYPE_ID uniqueidentifier,
    --
    primary key nonclustered (ID)
)^
-- end PFA_EQUIPMENT_TYPE
-- begin PFA_SYSTEM_ALLOCATION
create table PFA_SYSTEM_ALLOCATION (
    ID uniqueidentifier,
    VERSION integer not null,
    CREATE_TS datetime2,
    CREATED_BY nvarchar(50),
    UPDATE_TS datetime2,
    UPDATED_BY nvarchar(50),
    DELETE_TS datetime2,
    DELETED_BY nvarchar(50),
    --
    APPLICATION_DATA_ID uniqueidentifier not null,
    SYSTEM_ID uniqueidentifier,
    RUN1 decimal(19, 2),
    RUN2 decimal(19, 2),
    RUN3 decimal(19, 2),
    RUN3_PLUS decimal(19, 2),
    --
    primary key nonclustered (ID)
)^
-- end PFA_SYSTEM_ALLOCATION
