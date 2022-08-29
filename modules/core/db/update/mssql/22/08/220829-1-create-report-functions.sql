CREATE OR ALTER FUNCTION report_internal_ApplicationData(
    @threshold_date DATETIME2 = NULL,
    @query_mode varchar(50) = 'FC',
    @start_period DATETIME2 = '0001-01-01',
    @end_period DATETIME2 = '9999-12-31',
    @country_id UNIQUEIDENTIFIER
)
    RETURNS TABLE
        AS
        RETURN
        WITH APP_DATA AS (
            SELECT ID,
                   CREATE_TS AS APP_DATA_CREATE_TS,
--            YEAR_,
--            MONTH_,
--            RECORD_TYPE,
                   ACCOUNT_ID,
                   ROW_NUMBER() over (PARTITION BY ACCOUNT_ID ORDER BY CREATE_TS DESC) AS RN
            FROM PFA_APPLICATION_DATA
            WHERE DELETE_TS IS NULL
              AND CREATE_TS < COALESCE(@threshold_date, GETDATE())
              AND dbo.calcRecordTypeWeight(RECORD_TYPE) <= dbo.calcRecordTypeWeight(@query_mode)
              AND COUNTRY_ID = @country_id
        ),
             SYSTEM_ALLOC AS (
                 SELECT AD.ID AS APPLICATION_DATA_ID,
                        AD.APP_DATA_CREATE_TS,
                        AD.ACCOUNT_ID,
                        SA.CREATE_TS AS SYSTEM_ALLOC_CREATE_TS,
                        SA.SYSTEM_ID,
                        ROW_NUMBER() over (PARTITION BY ACCOUNT_ID, AD.ID ORDER BY SA.CREATE_TS DESC) AS SYSTEM_NO,
                        SA.RUN1,
                        SA.RUN2
                 FROM APP_DATA AS AD
                          JOIN PFA_SYSTEM_ALLOCATION SA ON SA.APPLICATION_DATA_ID = AD.ID
                 WHERE SA.DELETE_TS IS NULL
                   AND AD.RN = 1
             ),
             EQUIPMENT AS (
                 SELECT SYSTEM_ALLOC.APPLICATION_DATA_ID,
                        SYSTEM_ALLOC.APP_DATA_CREATE_TS,
                        SYSTEM_ALLOC.ACCOUNT_ID,
                        SYSTEM_ALLOC.SYSTEM_ID,
                        SYSTEM_ALLOC.SYSTEM_NO,
                        SYSTEM_ALLOC.RUN1,
                        SYSTEM_ALLOC.RUN2,
                        PSD.PART_NUMBER_ID,
                        PSD.EQUIPMENT_TYPE_ID,
                        PSD.LENGTH / 0.3048 AS LENGTH, -- hack to convert meters to foots
                        COUNT(*) over (
                            PARTITION BY SYS.ID, PART_NUMBER_ID, EQUIPMENT_TYPE_ID
                            ) AS EQUIPMENT_COUNT
                 FROM SYSTEM_ALLOC
                          JOIN PFA_SYSTEM_STD SYS ON SYS.ID = SYSTEM_ALLOC.SYSTEM_ID
                          JOIN PFA_SYSTEM_DETAIL PSD ON SYS.ID = PSD.SYSTEM_ID AND PSD.DELETE_TS IS NULL
             )
        SELECT DISTINCT APPLICATION_DATA_ID,
                        APP_DATA_CREATE_TS,
                        ACCOUNT_ID,
                        SYSTEM_ID,
                        SYSTEM_NO,
                        PART_NUMBER_ID,
                        EQUIPMENT_TYPE_ID,
                        COALESCE(LENGTH, EQUIPMENT_COUNT) AS QTY,
                        IIF(LENGTH IS NULL, 'ea', 'ft')  AS UOM,
                        RUN1,
                        RUN2
        FROM EQUIPMENT^

CREATE OR ALTER FUNCTION report_SystemsOnly(
    @threshold_date DATETIME2 = NULL,
    @query_mode varchar(50) = 'FC',
    @start_period DATETIME2 = '0001-01-01',
    @end_period DATETIME2 = '9999-12-31',
    @country_id UNIQUEIDENTIFIER
)
    RETURNS TABLE
        AS
        RETURN
        SELECT APP_DATA.ACCOUNT_ID,
               A.NAME                                    AS ACCOUNT_NAME,
               dbo.getAccountTypeOrder(PAR.TYPE_)        AS ACCOUNT_ORDER,
               dbo.getContractTypeTitle(A.CONTRACT_TYPE) AS CONTRACT_TYPE,
               PA.NAME AS PARENT_ACCOUNT_NAME,
               APP_DATA.APPLICATION_DATA_ID,
               APP_DATA.SYSTEM_ID,
               APP_DATA.SYSTEM_NO,
               P.WT_PART_NUMBER                          AS PART_NUMBER,
               P.NAME                                    AS PART_DESCRIPTION,
               APP_DATA.QTY,
               APP_DATA.UOM,
               APP_DATA.RUN1,
               APP_DATA.RUN2,
               APP_DATA.EQUIPMENT_TYPE_ID,
               ET.NAME AS EQUIPMENT_TYPE_NAME,
               ET.ORDER_ AS EQUIPMENT_TYPE_ORDER --replace with order from country-settings
        FROM report_internal_ApplicationData(@threshold_date, @query_mode, @start_period, @end_period, @country_id) APP_DATA
                 LEFT JOIN PFA_ACCOUNT A ON A.ID = APP_DATA.ACCOUNT_ID
                 LEFT JOIN PN_PART P ON P.ID = APP_DATA.PART_NUMBER_ID
                 LEFT JOIN PFA_EQUIPMENT_TYPE ET ON ET.ID = APP_DATA.EQUIPMENT_TYPE_ID
                 LEFT JOIN PFA_ACCOUNT_REVISION PAR ON A.ID = PAR.ACCOUNT_ID
                 LEFT JOIN PFA_ACCOUNT PA ON PA.ID = A.PARENT_ID^