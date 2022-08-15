CREATE OR ALTER  FUNCTION report_Systems(
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
               APP_DATA.SYSTEM_NO,
--        PART_NUMBER_ID,
               P.WT_PART_NUMBER                          AS PART_NUMBER,
               P.NAME                                    AS PART_DESCRIPTION,
               APP_DATA.QTY,
               APP_DATA.UOM,
               APP_DATA.RUN1,
               APP_DATA.RUN2,
               EQ_UTIL.REVENUE_MODE,
--                APP_DATA.EQUIPMENT_TYPE_ID AS EQUIPMENT_TYPE_ID,
--        ET.NAME AS EQUIPMENT_TYPE,
               ET.NAME AS EQUIPMENT_TYPE_NAME,
               EQ_UTIL.EQUIPMENT_TYPE_ORDER,
--        VALUE_TYPE_ID,
               EUVT.NAME                                 AS EQUIPMENT_UTILIZATION_VALUE_TYPE,
               EQ_UTIL.VALUE_TYPE_ORDER,
               EQ_UTIL.VALUE_
        FROM report_internal_ApplicationData(@threshold_date, @query_mode, @start_period, @end_period, @country_id) APP_DATA
                 JOIN report_internal_EquipmentUtilization(@threshold_date, @query_mode, @start_period, @end_period, @country_id) EQ_UTIL
                      ON EQ_UTIL.ACCOUNT_ID = APP_DATA.ACCOUNT_ID AND EQ_UTIL.EQUIPMENT_TYPE_ID = APP_DATA.EQUIPMENT_TYPE_ID
                 LEFT JOIN PFA_ACCOUNT A ON A.ID = APP_DATA.ACCOUNT_ID
                 LEFT JOIN PN_PART P ON P.ID = APP_DATA.PART_NUMBER_ID
                 LEFT JOIN PFA_EQUIPMENT_TYPE ET ON ET.ID = APP_DATA.EQUIPMENT_TYPE_ID
                 LEFT JOIN PFA_EQUIPMENT_UTILIZATION_VALUE_TYPE EUVT ON EUVT.ID = EQ_UTIL.VALUE_TYPE_ID
                 LEFT JOIN PFA_ACCOUNT_REVISION PAR ON A.ID = PAR.ACCOUNT_ID
                 LEFT JOIN PFA_ACCOUNT PA ON PA.ID = A.PARENT_ID

