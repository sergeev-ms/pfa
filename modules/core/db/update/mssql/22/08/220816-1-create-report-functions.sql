CREATE OR ALTER FUNCTION report_internal_ActivityPlan(
    @threshold_date DATETIME2 = NULL,
    @query_mode varchar(50) = 'FC',
    @start_period DATETIME2 = '0001-01-01',
    @end_period DATETIME2 = '9999-12-31',
    @country_id UNIQUEIDENTIFIER
)
    RETURNS TABLE
        AS
        RETURN
        WITH NUNBERED_DETAILS AS (
            SELECT PA.ACCOUNT_ID,
                   PAD.ANALYTIC_ID,
                   PA.CREATE_TS                            AS ACTIVITY_CREATE_TS,
                   PA.RECORD_TYPE,
                   DATEFROMPARTS(PAD.YEAR_, PAD.MONTH_, 1) AS YEAR_MONTH,
                   PAD.VALUE_,
                   ROW_NUMBER() OVER (PARTITION BY ACCOUNT_ID, ANALYTIC_ID, DATEFROMPARTS(PAD.YEAR_, PAD.MONTH_, 1)
                       ORDER BY PA.CREATE_TS DESC)         AS R_NUMBER
            FROM PFA_ACTIVITY_DETAIL PAD
                     JOIN PFA_ACTIVITY PA on PAD.ACTIVITY_ID = PA.ID
            WHERE PAD.DELETE_TS IS NULL
              AND PAD.CREATE_TS < COALESCE(@threshold_date, GETDATE())
              AND DATEFROMPARTS(PAD.YEAR_, PAD.MONTH_, 1) >= @start_period
              AND DATEFROMPARTS(PAD.YEAR_, PAD.MONTH_, 1) <= @end_period
              AND dbo.calcRecordTypeWeight(PA.RECORD_TYPE) <= dbo.calcRecordTypeWeight(@query_mode)
              AND PA.COUNTRY_ID = @country_id
        )
        SELECT ND.ACCOUNT_ID,
               ND.ANALYTIC_ID,
               ND.ACTIVITY_CREATE_TS,
               ND.RECORD_TYPE,
               ND.YEAR_MONTH,
               ND.VALUE_
        FROM NUNBERED_DETAILS ND
                 JOIN PFA_ANALYTIC_SET PAS on PAS.ID = ND.ANALYTIC_ID AND PAS.DELETE_TS IS NULL
        WHERE R_NUMBER = 1^

CREATE FUNCTION report_SystemsExt(
    @threshold_date DATETIME2 = NULL,
    @query_mode varchar(50) = 'FC',
    @start_period DATETIME2 = '0001-01-01',
    @end_period DATETIME2 = '9999-12-31',
    @country_id UNIQUEIDENTIFIER
)
    RETURNS TABLE
        AS
        RETURN
        WITH AGGREGATED_INSTALLS AS (
            SELECT AP.ACCOUNT_ID,
                   AP.ANALYTIC_ID,
                   PAS.JOB_TYPE,
                   PAS.WELL_TAG,
                   AP.YEAR_MONTH,
                   AP.VALUE_,
--            SUM(AP.VALUE_) OVER ( PARTITION BY AP.ACCOUNT_ID, AP.ANALYTIC_ID) AS SUM_OVER_ANALYTIC,
--            SUM(AP.VALUE_) OVER ( PARTITION BY AP.ACCOUNT_ID, PAS.WELL_TAG) AS SUM_OVER_WELL_TAG,
                   SUM(IIF(PAS.WELL_TAG = 'F', AP.VALUE_, 0)) OVER ( PARTITION BY AP.ACCOUNT_ID) AS SUM_ONLY_FIRST,
                   SUM(IIF(PAS.WELL_TAG = 'S', AP.VALUE_, 0)) OVER ( PARTITION BY AP.ACCOUNT_ID) AS SUM_ONLY_SEQUENT,
                   SUM(AP.VALUE_) OVER ( PARTITION BY AP.ACCOUNT_ID)                                                 AS SUM_TOTAL

            FROM dbo.report_internal_ActivityPlan(@threshold_date, @query_mode, @start_period, @end_period, @country_id) AP
                     JOIN PFA_ANALYTIC_SET PAS ON PAS.ID = AP.ANALYTIC_ID
            WHERE PAS.JOB_TYPE = 'I' --ONLY INSTALLS
        ),
             DISCTINCT_AI AS (
                 SELECT DISTINCT ACCOUNT_ID,
                                 --ANALYTIC_ID,
                                 --WELL_TAG,
                                 SUM_ONLY_FIRST,
                                 SUM_ONLY_SEQUENT,
                                 SUM_TOTAL,
                                 SUM_ONLY_FIRST * 1.0 / SUM_TOTAL RATION_FIRST,
                                 SUM_ONLY_SEQUENT * 1.0 / SUM_TOTAL RATION_SEQUENT
                 FROM AGGREGATED_INSTALLS
                 WHERE SUM_TOTAL > 0 -- ASSUME IF THERE IS NO INSTALLS THEN THERE IS NO PULLS
             ),
             CALCULATED AS (
                 SELECT APP_DATA.*,
                        APP_DATA.RUN2 AS RUN1_PLUS_COMPETITOR,
                        DAI.RATION_FIRST,
                        DAI.RATION_SEQUENT,
                        APP_DATA.RUN1 * DAI.RATION_FIRST AS PULL_FIRST,
                        APP_DATA.RUN2 * DAI.RATION_SEQUENT AS PULL_SEQUENT
                 FROM report_internal_ApplicationData(@threshold_date, @query_mode, @start_period, @end_period, @country_id) APP_DATA
                          JOIN DISCTINCT_AI DAI ON DAI.ACCOUNT_ID = APP_DATA.ACCOUNT_ID
             )

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
               APP_DATA.RUN2 AS RUN1_PLUS_COMPETITOR,
               --DAI.RATION_FIRST,
               --DAI.RATION_SEQUENT,
               APP_DATA.RUN1 * DAI.RATION_FIRST AS PULL_FIRST,
               APP_DATA.RUN2 * DAI.RATION_SEQUENT AS PULL_SEQUENT,
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
                 JOIN DISCTINCT_AI DAI ON DAI.ACCOUNT_ID = APP_DATA.ACCOUNT_ID
                 JOIN report_internal_EquipmentUtilization(@threshold_date, @query_mode, @start_period, @end_period, @country_id) EQ_UTIL
                      ON EQ_UTIL.ACCOUNT_ID = APP_DATA.ACCOUNT_ID AND EQ_UTIL.EQUIPMENT_TYPE_ID = APP_DATA.EQUIPMENT_TYPE_ID
                 LEFT JOIN PFA_ACCOUNT A ON A.ID = APP_DATA.ACCOUNT_ID
                 LEFT JOIN PN_PART P ON P.ID = APP_DATA.PART_NUMBER_ID
                 LEFT JOIN PFA_EQUIPMENT_TYPE ET ON ET.ID = APP_DATA.EQUIPMENT_TYPE_ID
                 LEFT JOIN PFA_EQUIPMENT_UTILIZATION_VALUE_TYPE EUVT ON EUVT.ID = EQ_UTIL.VALUE_TYPE_ID
                 LEFT JOIN PFA_ACCOUNT_REVISION PAR ON A.ID = PAR.ACCOUNT_ID
                 LEFT JOIN PFA_ACCOUNT PA ON PA.ID = A.PARENT_ID^