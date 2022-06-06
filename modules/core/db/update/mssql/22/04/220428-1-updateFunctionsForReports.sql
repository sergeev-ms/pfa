IF object_id('report_PriceDetails', 'IF') IS NOT NULL
    BEGIN
        exec sp_rename 'report_PriceDetails', report_internal_PriceDetails, 'OBJECT'
    END^

CREATE OR ALTER FUNCTION report_internal_PriceDetails (
    @threshold_date Datetime2 = NULL,
    @query_mode varchar(50) = 'FC'
)

    RETURNS TABLE
        AS
        RETURN
        WITH PLD AS (
            SELECT PLD.ID,
                   PLD.CREATE_TS AS DETAIL_CREATE_TS,
                   PLD.PRICE_LIST_ID,
                   PL.ACCOUNT_ID,
                   PL.RECORD_TYPE,
                   PL.YEAR_,
                   PL.MONTH_,
                   PL.CREATE_TS AS PRICE_LIST_CREATE_TS,
                   PLD.ANALYTIC_ID,
                   PLD.REVENUE_TYPE_ID,
                   PLD.VALUE_

            FROM PFA_PRICE_LIST_DETAIL PLD
                     JOIN PFA_PRICE_LIST PL ON PLD.PRICE_LIST_ID = PL.ID AND PL.DELETE_TS IS NULL

            WHERE PL.DELETE_TS IS NULL AND PLD.DELETE_TS IS NULL
              AND PL.CREATE_TS < COALESCE(@threshold_date, GETDATE())
              AND dbo.calcRecordTypeWeight(PL.RECORD_TYPE) <= dbo.calcRecordTypeWeight(@query_mode)
        ),
             NUNBERED_DETAILS AS (
                 SELECT *,
                        ROW_NUMBER() OVER (
                            PARTITION BY ACCOUNT_ID, ANALYTIC_ID, REVENUE_TYPE_ID, PLD.YEAR_, PLD.MONTH_
                            ORDER BY PLD.YEAR_ DESC, PLD.MONTH_ DESC, PLD.PRICE_LIST_CREATE_TS DESC
                            ) RN
                 FROM PLD
             )
        SELECT ACCOUNT_ID,
               ANALYTIC_ID,
               REVENUE_TYPE_ID,
               PRICE_LIST_CREATE_TS AS DETAIL_CREATE_TS,
               RECORD_TYPE,
               YEAR_,
               MONTH_,
               DATEFROMPARTS(YEAR_, MONTH_, 1) AS VALID_FROM,
               VALUE_
        FROM NUNBERED_DETAILS ND
        WHERE RN = 1^

CREATE OR ALTER FUNCTION report_PriceDetailsByMonths(
    @threshold_date Datetime2 = NULL,
    @query_mode varchar(50) = 'FC',
    @start_period DATETIME2 = '2021-01-01',
    @end_period DATETIME2 = '2021-01-01'
)
    RETURNS @result TABLE
                    (
                        ACCOUNT_ID           uniqueidentifier,
                        ACCOUNT_NAME         nvarchar(255),
                        ACTIVE               tinyint,
                        PARENT_ACCOUNT_NAME  nvarchar(255),
                        ACCOUNT_TIER         nvarchar(255),
                        ACCOUNT_ORDER        int,
                        CONTRACT_TYPE        nvarchar(255),
                        APPLICATION_TYPE     nvarchar(255),
                        MANAGER_NAME         nvarchar(255),
                        ANALYTIC_ID          uniqueidentifier,
                        ANALYTIC_TITLE       nvarchar(255),
                        ANALYTIC_ORDER       int,
                        REVENUE_TYPE_ID      uniqueidentifier,
                        REVENUE_TYPE_NAME    nvarchar(255),
                        REVENUE_TYPE_ORDER   int,
                        PRICE_LIST_CREATE_TS datetime2,
                        RECORD_TYPE          nvarchar(255),
                        VALID_FROM           datetime2,
                        VALUE_               decimal(19, 2),
                        YEAR_MONTH           datetime2
                    ) AS
BEGIN
    SELECT @start_period = DATEFROMPARTS(DATEPART(year, @start_period), DATEPART(month, @start_period), 1)
    SELECT @end_period = DATEFROMPARTS(DATEPART(year, @end_period), DATEPART(month, @end_period), 1)

    WHILE @start_period <= @end_period
        BEGIN
            WITH SETTINGS AS (
                SELECT PCSAD.ANALYTIC_SET_ID,
                       PCS.COUNTRY_ID,
                       PCSAD.ORDER_
                FROM PFA_COUNTRY_SETTING PCS
                         JOIN PFA_COUNTRY_SETTING_ANALYTIC_DETAIL PCSAD ON PCS.ID = PCSAD.COUNTRY_SETTING_ID
            ),
                 PRICE_WITH_RN AS (
                     SELECT RPD.*,
                            @start_period                       AS YEAR_MONTH,
                            ROW_NUMBER() over (
                                PARTITION BY ACCOUNT_ID, ANALYTIC_ID, REVENUE_TYPE_ID
                                ORDER BY DETAIL_CREATE_TS DESC) AS RN
                     FROM report_internal_PriceDetails(@threshold_date, @query_mode) RPD

                     WHERE VALID_FROM <= @start_period
                 )
            INSERT
            @result
            SELECT RES.ACCOUNT_ID,
                   ACC.NAME                          AS ACCOUNT_NAME,
                   AR.ACTIVE,
                   PA.NAME                           AS PARENT_ACCOUNT_NAME,
                   dbo.getAccountTypeTitle(AR.TYPE_) AS ACCOUNT_TIER,
                   dbo.getAccountTypeOrder(AR.TYPE_) AS ACCOUNT_ORDER,
                   dbo.getContractTypeTitle(ACC.CONTRACT_TYPE),
                   dbo.getAccountApplicationTypeTitle(ACC.APPLICATION_TYPE),
                   PE.NAME                           AS MANAGER_NAME,
                   RES.ANALYTIC_ID,
                   dbo.getAnalyticTitle(PAS.JOB_TYPE, PAS.WELL_EQUIP, PAS.WELL_TAG)
                                                     AS ANALYTIC_TITLE,
                   S.ORDER_                          AS ANALYTIC_ORDER,
                   RES.REVENUE_TYPE_ID,
                   RT.FULL_NAME                      AS REVENUE_TYPE_NAME,
                   RT.ORDER_                         AS REVENUE_TYPE_ORDER,
                   RES.DETAIL_CREATE_TS,
                   RES.RECORD_TYPE,
                   RES.VALID_FROM,
                   RES.VALUE_,
                   RES.YEAR_MONTH
            FROM PRICE_WITH_RN AS RES
                     JOIN PFA_ACCOUNT ACC on ACC.ID = RES.ACCOUNT_ID AND ACC.DELETE_TS IS NULL
                     LEFT JOIN SETTINGS S ON S.ANALYTIC_SET_ID = RES.ANALYTIC_ID AND S.COUNTRY_ID = ACC.COUNTRY_ID
                     LEFT JOIN PFA_ACCOUNT_REVISION AR ON AR.ID = ACC.ACTUAL_REVISION_ID
                     LEFT JOIN PFA_ANALYTIC_SET PAS on RES.ANALYTIC_ID = PAS.ID
                     LEFT JOIN PFA_REVENUE_TYPE RT ON RT.ID = RES.REVENUE_TYPE_ID
                     LEFT JOIN PFA_ACCOUNT PA ON PA.ID = ACC.PARENT_ID
                     LEFT JOIN PFA_EMPLOYEE PE on AR.MANAGER_ID = PE.ID
            WHERE RES.RN = 1

            SELECT @start_period = DATEADD(month, 1, @start_period)
        END
    RETURN
END^

CREATE OR ALTER FUNCTION report_Revenue (
    @threshold_date Datetime2 = NULL,
    @query_mode varchar(50) = 'FC',
    @start_period DATETIME2 = '0001-01-01',
    @end_period DATETIME2 = '9999-12-31'
)

    RETURNS TABLE
        AS
        RETURN
        WITH
            ACTIVITY_DETAILS AS (
                SELECT ACCOUNT_ID,
                       ACCOUNT_NAME,
                       ACCOUNT_ORDER,
                       ACCOUNT_TIER,
                       CONTRACT_TYPE,
                       APPLICATION_TYPE,
                       PARENT_ACCOUNT_NAME,
                       MANAGER_NAME,
                       ANALYTIC_ID,
                       ANALYTIC_TITLE,
                       YEAR_,
                       MONTH_,
                       VALUE_
                FROM report_ActivityPlan(@threshold_date, @query_mode, @start_period, @end_period)
--                 FROM report_ActivityPlan(DEFAULT, DEFAULT, DEFAULT, DEFAULT)
            ),
            PRICE_DETAIL_WITH_RULES AS (
                SELECT APD.ACCOUNT_ID,
                       APD.REVENUE_TYPE_ID,
                       APD.ANALYTIC_ID AS PRICE_ANALYTIC_ID,
                       RCR.ANALYTIC_SET_ID AS RULE_ANALYTIC_ID,
                       APD.DETAIL_CREATE_TS,
                       APD.YEAR_ AS PRICE_VALID_YEAR,
                       APD.MONTH_ AS PRICE_VALID_MONTH,
                       APD.VALUE_
                FROM dbo.report_internal_PriceDetails (@threshold_date, @query_mode) APD
--                 FROM dbo.report_PriceDetails(DEFAULT, DEFAULT) APD
                         JOIN REVENUE_CALC_RULES RCR on APD.REVENUE_TYPE_ID = RCR.REVENUE_TYPE_ID
                WHERE APD.ANALYTIC_ID = RCR.ANALYTIC_SET_ID
            ),
            PRICE_DETAILS_WITH_RN AS (
                SELECT AD.*,
                       PDSD.REVENUE_TYPE_ID,
                       PDSD.PRICE_VALID_YEAR,
                       PDSD.PRICE_VALID_MONTH,
                       ROW_NUMBER() over (
                           PARTITION BY AD.ACCOUNT_ID, PDSD.REVENUE_TYPE_ID, PDSD.PRICE_ANALYTIC_ID, AD.YEAR_, AD.MONTH_--, PDSD.PRICE_VALID_YEAR, PDSD.PRICE_VALID_MONTH
                           ORDER BY PDSD.PRICE_VALID_YEAR DESC , PDSD.PRICE_VALID_MONTH DESC, PDSD.DETAIL_CREATE_TS DESC
                           ) AS RN,
                       PDSD.VALUE_ AS PRICE_VALUE,
                       AD.VALUE_ AS ACTIVITY_VALUE,
                       AD.VALUE_ * PDSD.VALUE_ AS COST
                FROM ACTIVITY_DETAILS AD
                         JOIN PRICE_DETAIL_WITH_RULES PDSD ON PDSD.ACCOUNT_ID = AD.ACCOUNT_ID AND
                                                              PDSD.PRICE_ANALYTIC_ID = AD.ANALYTIC_ID AND
                                                              AD.YEAR_ >= PDSD.PRICE_VALID_YEAR AND
                                                              AD.MONTH_ >= PDSD.PRICE_VALID_MONTH
            ),
            PRICE_DETAILS_WITH_LATEST_PL AS (
                SELECT ACCOUNT_ID,
                       ACCOUNT_NAME,
                       ACCOUNT_ORDER,
                       ACCOUNT_TIER,
                       CONTRACT_TYPE,
                       APPLICATION_TYPE,
                       PARENT_ACCOUNT_NAME,
                       MANAGER_NAME,
                       REVENUE_TYPE_ID,
                       YEAR_,
                       MONTH_,
                       ACTIVITY_VALUE,
                       PRICE_VALUE,
                       COST,
--                REVENUE,
                       SUM (ACTIVITY_VALUE * PRICE_VALUE) OVER ( PARTITION BY ACCOUNT_ID, REVENUE_TYPE_ID, YEAR_, MONTH_) AS REVENUE,
                       RN
                FROM PRICE_DETAILS_WITH_RN

                WHERE RN = 1
            )


        SELECT DISTINCT
            ACCOUNT_ID,
            ACCOUNT_NAME,
            ACCOUNT_ORDER,
            ACCOUNT_TIER,
            CONTRACT_TYPE,
            APPLICATION_TYPE,
            PARENT_ACCOUNT_NAME,
            MANAGER_NAME,
            REVENUE_TYPE_ID,
            RT.NAME AS REVENUE_TYPE_NAME,
            RT.ORDER_ AS REVENUE_TYPE_ORDER,
            YEAR_,
            MONTH_,
            DATEFROMPARTS(YEAR_, MONTH_, 1) AS YEAR_MONTH,
            REVENUE
        FROM PRICE_DETAILS_WITH_LATEST_PL
                 LEFT JOIN PFA_REVENUE_TYPE RT ON RT.ID = PRICE_DETAILS_WITH_LATEST_PL.REVENUE_TYPE_ID