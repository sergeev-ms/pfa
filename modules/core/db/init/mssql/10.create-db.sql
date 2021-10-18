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
    MONTH_ integer,
    PERIOD_FROM datetime2,
    PERIOD_TO datetime2,
    COMMENT_ nvarchar(max),
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
    RENTAL_RATE integer,
    REMOTE_MONITORING integer,
    WELL_CHECK integer,
    YEAR_ integer,
    MONTH_ integer,
    COMMENT_ nvarchar(max),
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
    COUNTRY_ID uniqueidentifier,
    NAME nvarchar(255),
    PARENT_ID uniqueidentifier,
    CUSTOMER_ID decimal(7),
    ACTUAL_REVISION_ID uniqueidentifier,
    ACTUAL_MARKET_DETAIL_ID uniqueidentifier,
    ACTUAL_APP_DETAIL_ID uniqueidentifier,
    CONTRACT_TYPE nvarchar(50),
    APPLICATION_TYPE nvarchar(50),
    ACTUAL_EQUIPMENT_UTILIZATION_ID uniqueidentifier,
    FIELD_TYPE nvarchar(50),
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
    CREATED_BY varchar(50),
    UPDATE_TS datetime2,
    UPDATED_BY varchar(50),
    DELETE_TS datetime2,
    DELETED_BY varchar(50),
    --
    YEAR_ integer,
    MONTH_ integer,
    RECORD_TYPE varchar(50),
    RUNS_NUMBER varchar(50),
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
    WELL_CHECK_RATE decimal(19, 2),
    ESP_LT_TARGET integer,
    MARKET_SHARE_TARGET integer,
    BORETS_RUN_LIFE integer,
    DELAY_FACTOR decimal(19, 2),
    CUSTOMER_PULLS_IN_YEAR integer,
    CUSTOMER_RUNBACK_IN_YEAR integer,
    CUSTOMER_INSTALL_IN_YEAR integer,
    CUSTOMER_WELLS_CLOSING_IN_YEAR integer,
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
    LENGTH decimal(19,6),
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
    DTYPE nvarchar(31),
    --
    SYSTEM_ID nvarchar(255),
    CASING_SIZE nvarchar(50),
    CASING_WEIGHT nvarchar(50),
    PUMP_TYPE_ID uniqueidentifier,
    DEPTH_ID uniqueidentifier,
    MOTOR_TYPE_ID uniqueidentifier,
    INTAKE_CONFIG_ID uniqueidentifier,
    VAPRO_CONFIG_ID uniqueidentifier,
    SEAL_CONFIG_ID uniqueidentifier,
    PUMP_CONFIG_ID uniqueidentifier,
    PUMP_MATERIALS_ID uniqueidentifier,
    SEAL_MATERIALS_ID uniqueidentifier,
    MOTOR_MATERIALS_ID uniqueidentifier,
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
    ACCOUNT_ID uniqueidentifier,
    RECORD_TYPE nvarchar(50),
    VALID_FROM datetime2,
    YEAR_ integer,
    MONTH_ integer,
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
    ORDER_ integer,
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
-- begin PFA_EMPLOYEE
create table PFA_EMPLOYEE (
    ID uniqueidentifier,
    VERSION integer not null,
    CREATE_TS datetime2,
    CREATED_BY nvarchar(50),
    UPDATE_TS datetime2,
    UPDATED_BY nvarchar(50),
    DELETE_TS datetime2,
    DELETED_BY nvarchar(50),
    --
    FIRST_NAME nvarchar(255),
    LAST_NAME nvarchar(255),
    NAME nvarchar(255),
    USER_ID uniqueidentifier,
    --
    primary key nonclustered (ID)
)^
-- end PFA_EMPLOYEE
-- begin PFA_DEPTH
create table PFA_DEPTH (
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
-- end PFA_DEPTH
-- begin PFA_MATERIALS
create table PFA_MATERIALS (
    ID uniqueidentifier,
    VERSION integer not null,
    CREATE_TS datetime2,
    CREATED_BY nvarchar(50),
    UPDATE_TS datetime2,
    UPDATED_BY nvarchar(50),
    DELETE_TS datetime2,
    DELETED_BY nvarchar(50),
    DTYPE nvarchar(31),
    --
    NAME nvarchar(255),
    --
    primary key nonclustered (ID)
)^
-- end PFA_MATERIALS
-- begin PFA_PUMP_CONFIG
create table PFA_PUMP_CONFIG (
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
-- end PFA_PUMP_CONFIG
-- begin PFA_INTAKE_CONFIG
create table PFA_INTAKE_CONFIG (
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
-- end PFA_INTAKE_CONFIG
-- begin PFA_SEAL_CONFIG
create table PFA_SEAL_CONFIG (
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
-- end PFA_SEAL_CONFIG
-- begin PFA_VAPRO_CONFIG
create table PFA_VAPRO_CONFIG (
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
-- end PFA_VAPRO_CONFIG
-- begin PFA_MOTOR_TYPE
create table PFA_MOTOR_TYPE (
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
-- end PFA_MOTOR_TYPE
-- begin PFA_PUMP_TYPE
create table PFA_PUMP_TYPE (
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
-- end PFA_PUMP_TYPE
-- begin PFA_EQUIPMENT_UTILIZATION_DETAIL
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
    SEQUENT_RUN_COMPETITOR_VALUE decimal(19, 2),
    EQUIPMENT_UTILIZATION_ID uniqueidentifier not null,
    REVENUE_MODE nvarchar(50),
    ORDER_ integer,
    --
    primary key nonclustered (ID)
)^
-- end PFA_EQUIPMENT_UTILIZATION_DETAIL
-- begin PFA_SUPPLEMENTARY_DETAIL_TYPE
create table PFA_SUPPLEMENTARY_DETAIL_TYPE (
    ID uniqueidentifier,
    CREATE_TS datetime2,
    CREATED_BY nvarchar(50),
    DELETE_TS datetime2,
    DELETED_BY nvarchar(50),
    --
    NAME nvarchar(255),
    --
    primary key nonclustered (ID)
)^
-- end PFA_SUPPLEMENTARY_DETAIL_TYPE
-- begin PFA_SUPPLEMENTARY
create table PFA_SUPPLEMENTARY (
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
    --
    primary key nonclustered (ID)
)^
-- end PFA_SUPPLEMENTARY
-- begin PFA_SUPPLEMENTARY_DETAIL
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
)^
-- end PFA_SUPPLEMENTARY_DETAIL
-- begin PFA_COUNTRY_SETTING_ANALYTIC_DETAIL
create table PFA_COUNTRY_SETTING_ANALYTIC_DETAIL (
    ID uniqueidentifier,
    VERSION integer not null,
    CREATE_TS datetime2,
    CREATED_BY nvarchar(50),
    UPDATE_TS datetime2,
    UPDATED_BY nvarchar(50),
    DELETE_TS datetime2,
    DELETED_BY nvarchar(50),
    --
    COUNTRY_SETTING_ID uniqueidentifier not null,
    ANALYTIC_SET_ID uniqueidentifier,
    PRICE_LIST tinyint,
    ACTIVITY_PLAN tinyint,
    ORDER_ integer,
    --
    primary key nonclustered (ID)
)^
-- end PFA_COUNTRY_SETTING_ANALYTIC_DETAIL
-- begin PFA_COUNTRY_SETTING
create table PFA_COUNTRY_SETTING (
    ID uniqueidentifier,
    VERSION integer not null,
    CREATE_TS datetime2,
    CREATED_BY nvarchar(50),
    UPDATE_TS datetime2,
    UPDATED_BY nvarchar(50),
    DELETE_TS datetime2,
    DELETED_BY nvarchar(50),
    --
    COUNTRY_ID uniqueidentifier,
    --
    primary key nonclustered (ID)
)^
-- end PFA_COUNTRY_SETTING
-- begin PFA_PROJECT
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
)^
-- end PFA_PROJECT
