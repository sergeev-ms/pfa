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
                 LEFT JOIN PFA_ACCOUNT_REVISION PAR ON PAR.ID = A.ACTUAL_REVISION_ID
                 LEFT JOIN PFA_ACCOUNT PA ON PA.ID = A.PARENT_ID