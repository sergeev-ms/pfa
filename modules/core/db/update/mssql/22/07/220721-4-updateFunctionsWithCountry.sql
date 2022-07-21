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
                        SYSTEM_NO,
                        PART_NUMBER_ID,
                        EQUIPMENT_TYPE_ID,
                        COALESCE(LENGTH, EQUIPMENT_COUNT) AS QTY,
                        IIF(LENGTH IS NULL, 'ea', 'ft')  AS UOM,
                        RUN1,
                        RUN2
        FROM EQUIPMENT^

CREATE OR ALTER FUNCTION report_internal_EquipmentUtilization(
    @threshold_date DATETIME2 = NULL,
    @query_mode varchar(50) = 'FC',
    @start_period DATETIME2 = '0001-01-01',
    @end_period DATETIME2 = '9999-12-31',
    @country_id UNIQUEIDENTIFIER
)
    RETURNS TABLE
        AS
        RETURN
        WITH UTILIZATION AS (
            SELECT ID,
--            CREATE_TS,
--            YEAR_,
--            MONTH_,
--            RECORD_TYPE,
--            VALID_FROM,
                   ACCOUNT_ID,
                   ROW_NUMBER() over (PARTITION BY ACCOUNT_ID ORDER BY CREATE_TS DESC) AS RN
            FROM PFA_EQUIPMENT_UTILIZATION EU
            WHERE DELETE_TS IS NULL
              AND CREATE_TS < COALESCE(@threshold_date, GETDATE())
              AND dbo.calcRecordTypeWeight(RECORD_TYPE) <= dbo.calcRecordTypeWeight(@query_mode)
              AND COUNTRY_ID = @country_id
        ),
             UTIL_DETAILS AS (
                 SELECT U.ACCOUNT_ID,
                        EUD.ID,
                        EUD.EQUIPMENT_TYPE_ID,
                        EUD.ORDER_ AS EQUIPMENT_TYPE_ORDER,
                        EUD.REVENUE_MODE
                 FROM UTILIZATION U
                          JOIN PFA_EQUIPMENT_UTILIZATION_DETAIL EUD ON EUD.EQUIPMENT_UTILIZATION_ID = U.ID AND EUD.DELETE_TS IS NULL
                 WHERE U.RN = 1
             ),
             UTIL_DETAILS_VALUES AS (
                 SELECT UD.ACCOUNT_ID,
                        A.COUNTRY_ID,
                        UD.EQUIPMENT_TYPE_ID,
                        UD.REVENUE_MODE,
                        UD.EQUIPMENT_TYPE_ORDER,
                        EUDV.VALUE_TYPE_ID,
                        EUDV.VALUE_
                 FROM UTIL_DETAILS UD
                          JOIN PFA_EQUIPMENT_UTILIZATION_DETAIL_VALUE EUDV ON EUDV.DETAIL_ID = UD.ID AND EUDV.DELETE_TS IS NULL
                          JOIN PFA_ACCOUNT A ON A.ID = UD.ACCOUNT_ID
             ),
             UTIL_DETAILS_VALUES_ORDERED AS (
                 SELECT UDV.*,
                        S.ORDER_ AS VALUE_TYPE_ORDER
                 FROM UTIL_DETAILS_VALUES UDV
                          JOIN SETTING_UTILIZATION_VALUE_TYPE_vw S ON
                             S.COUNTRY_ID = UDV.COUNTRY_ID AND S.UTILIZATION_VALUE_TYPE_ID = UDV.VALUE_TYPE_ID
             )

        SELECT ACCOUNT_ID,
               COUNTRY_ID,
               EQUIPMENT_TYPE_ID,
               REVENUE_MODE,
               EQUIPMENT_TYPE_ORDER,
               VALUE_TYPE_ID,
               VALUE_TYPE_ORDER,
               VALUE_
        FROM UTIL_DETAILS_VALUES_ORDERED^


CREATE OR ALTER FUNCTION report_Systems(
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
               APP_DATA.SYSTEM_NO,
--        PART_NUMBER_ID,
               P.WT_PART_NUMBER                          AS PART_NUMBER,
               P.NAME                                    AS PART_DESCRIPTION,
               APP_DATA.QTY,
               APP_DATA.UOM,
               APP_DATA.RUN1,
               APP_DATA.RUN2,
               EQ_UTIL.REVENUE_MODE,
--        APP_DATA.EQUIPMENT_TYPE_ID AS EQUIPMENT_TYPE_ID,
--        ET.NAME AS EQUIPMENT_TYPE,
               EQ_UTIL.EQUIPMENT_TYPE_ORDER,
--        VALUE_TYPE_ID,
               EUVT.NAME                                 AS EQUIPMENT_UTILIZATION_VALUE_TYPE,
               EQ_UTIL.VALUE_TYPE_ORDER,
               EQ_UTIL.VALUE_
        FROM report_internal_ApplicationData(DEFAULT, DEFAULT, DEFAULT, DEFAULT, @country_id) APP_DATA
                 JOIN report_internal_EquipmentUtilization(DEFAULT, DEFAULT, DEFAULT, DEFAULT, @country_id) EQ_UTIL
                      ON EQ_UTIL.ACCOUNT_ID = APP_DATA.ACCOUNT_ID AND EQ_UTIL.EQUIPMENT_TYPE_ID = APP_DATA.EQUIPMENT_TYPE_ID
                 LEFT JOIN PFA_ACCOUNT A ON A.ID = APP_DATA.ACCOUNT_ID
                 LEFT JOIN PN_PART P ON P.ID = APP_DATA.PART_NUMBER_ID
                 LEFT JOIN PFA_EQUIPMENT_TYPE ET ON ET.ID = APP_DATA.EQUIPMENT_TYPE_ID
                 LEFT JOIN PFA_EQUIPMENT_UTILIZATION_VALUE_TYPE EUVT ON EUVT.ID = EQ_UTIL.VALUE_TYPE_ID
                 LEFT JOIN PFA_ACCOUNT_REVISION PAR on A.ID = PAR.ACCOUNT_ID^