CREATE OR ALTER FUNCTION report_ActivityPlan(
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
               A.NAME                                                           AS ACCOUNT_NAME,
               PAR.ACTIVE,
               dbo.getContractTypeTitle(A.CONTRACT_TYPE)                        AS CONTRACT_TYPE,
               dbo.getAccountApplicationTypeTitle(A.APPLICATION_TYPE)           AS APPLICATION_TYPE,
               PARA.NAME                                                        AS PARENT_ACCOUNT_NAME,
               A.DELETE_TS                                                      AS ACCOUNT_DELETE_TS,
               dbo.getAccountTypeTitle(PAR.TYPE_)                               AS ACCOUNT_TIER,
               dbo.getAccountTypeOrder(PAR.TYPE_)                               AS ACCOUNT_ORDER,
               E.NAME                                                           AS MANAGER_NAME,
               ND.ANALYTIC_ID,
               S.ORDER_                                                         AS ANALYTIC_ORDER,
               dbo.getAnalyticTitle(PAS.JOB_TYPE, PAS.WELL_EQUIP, PAS.WELL_TAG) AS ANALYTIC_TITLE,
               ND.ACTIVITY_CREATE_TS,
               ND.RECORD_TYPE,
               ND.YEAR_MONTH,
               ND.VALUE_
        FROM NUNBERED_DETAILS ND
                 JOIN PFA_ACCOUNT A ON A.ID = ND.ACCOUNT_ID
                 JOIN PFA_ANALYTIC_SET PAS on PAS.ID = ND.ANALYTIC_ID AND PAS.DELETE_TS IS NULL
                 LEFT JOIN PFA_ACCOUNT PARA ON A.PARENT_ID = PARA.ID
                 LEFT JOIN PFA_ACCOUNT_REVISION PAR ON A.ACTUAL_REVISION_ID = PAR.ID
                 LEFT JOIN PFA_EMPLOYEE E ON PAR.MANAGER_ID = E.ID
                 LEFT JOIN SETTING_ANALYTIC_ORDER_vw S
                           ON S.ANALYTIC_SET_ID = ND.ANALYTIC_ID AND S.COUNTRY_ID = A.COUNTRY_ID
        WHERE R_NUMBER = 1^

CREATE OR ALTER FUNCTION report_internal_PriceDetails (
    @threshold_date Datetime2 = NULL,
    @query_mode varchar(50) = 'FC',
    @country_id UNIQUEIDENTIFIER
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
              AND PL.COUNTRY_ID = @country_id
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
    @end_period DATETIME2 = '2021-01-01',
    @country_id UNIQUEIDENTIFIER
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
                     FROM report_internal_PriceDetails(@threshold_date, @query_mode, @country_id) RPD
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
    @end_period DATETIME2 = '9999-12-31',
    @country_id UNIQUEIDENTIFIER
)
    RETURNS TABLE
        AS
        RETURN
        WITH
            PRICE_DETAIL_WITH_RULES AS (
                SELECT APD.ACCOUNT_ID,
                       APD.REVENUE_TYPE_ID,
                       APD.ANALYTIC_ID AS PRICE_ANALYTIC_ID,
                       RCR.ANALYTIC_SET_ID AS RULE_ANALYTIC_ID,
                       APD.DETAIL_CREATE_TS,
                       APD.VALID_FROM AS PRICE_VALID_FROM,
                       APD.VALUE_
                FROM dbo.report_internal_PriceDetails (@threshold_date, @query_mode, @country_id) APD
                         JOIN REVENUE_CALC_RULES RCR on APD.REVENUE_TYPE_ID = RCR.REVENUE_TYPE_ID
                WHERE APD.ANALYTIC_ID = RCR.ANALYTIC_SET_ID
            ),
            PRICE_DETAILS_WITH_RN AS (
                SELECT AD.ACCOUNT_ID,
                       AD.ACCOUNT_NAME,
                       AD.CONTRACT_TYPE,
                       AD.APPLICATION_TYPE,
                       AD.PARENT_ACCOUNT_NAME,
                       AD.ACCOUNT_TIER,
                       AD.ACCOUNT_ORDER,
                       AD.MANAGER_NAME,
                       AD.ANALYTIC_ID,
                       AD.ANALYTIC_ORDER,
                       AD.ANALYTIC_TITLE,
                       AD.YEAR_MONTH,
                       PDSD.REVENUE_TYPE_ID,
                       PDSD.PRICE_VALID_FROM,
                       ROW_NUMBER() OVER (
                           PARTITION BY AD.ACCOUNT_ID, PDSD.REVENUE_TYPE_ID, PDSD.PRICE_ANALYTIC_ID, AD.YEAR_MONTH
                           ORDER BY PDSD.PRICE_VALID_FROM DESC, PDSD.DETAIL_CREATE_TS DESC
                           ) AS RN,
                       AD.VALUE_ AS ACTIVITY_VALUE,
                       PDSD.VALUE_ AS PRICE_VALUE,
                       AD.VALUE_ * PDSD.VALUE_ AS COST
                FROM report_ActivityPlan(@threshold_date, @query_mode, @start_period, @end_period, @country_id) AD
                         JOIN PRICE_DETAIL_WITH_RULES PDSD ON PDSD.ACCOUNT_ID = AD.ACCOUNT_ID AND
                                                              PDSD.PRICE_ANALYTIC_ID = AD.ANALYTIC_ID AND
                                                              AD.YEAR_MONTH >= PDSD.PRICE_VALID_FROM
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
                       YEAR_MONTH,
                       SUM(COST) OVER (PARTITION BY ACCOUNT_ID, REVENUE_TYPE_ID, YEAR_MONTH) AS REVENUE
                FROM PRICE_DETAILS_WITH_RN
                WHERE RN = 1
                UNION ALL
                SELECT ACCOUNT_ID,
                       ACCOUNT_NAME,
                       ACCOUNT_ORDER,
                       ACCOUNT_TIER,
                       CONTRACT_TYPE,
                       APPLICATION_TYPE,
                       PARENT_ACCOUNT_NAME,
                       MANAGER_NAME,
                       REVENUE_TYPE_ID,
                       YEAR_MONTH,
                       REVENUE
                FROM dbo.report_internal_DirectSales(@threshold_date, @query_mode, @start_period, @end_period)
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
            YEAR_MONTH,
            REVENUE
        FROM PRICE_DETAILS_WITH_LATEST_PL
                 LEFT JOIN PFA_REVENUE_TYPE RT ON RT.ID = PRICE_DETAILS_WITH_LATEST_PL.REVENUE_TYPE_ID^
