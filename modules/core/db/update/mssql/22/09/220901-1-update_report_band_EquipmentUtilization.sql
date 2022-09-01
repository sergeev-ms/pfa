CREATE OR ALTER FUNCTION report_band_EquipmentUtilization(
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
               PEUVT.NAME AS VALUE_TYPE_NAME,
               PEUVT.UTILIZATION_VARIABLE_NAME AS VARIABLE_NAME,
               VALUE_TYPE_ORDER,
               VALUE_
        FROM UTIL_DETAILS_VALUES_ORDERED UDVO
                 JOIN PFA_EQUIPMENT_UTILIZATION_VALUE_TYPE PEUVT ON PEUVT.ID = UDVO.VALUE_TYPE_ID