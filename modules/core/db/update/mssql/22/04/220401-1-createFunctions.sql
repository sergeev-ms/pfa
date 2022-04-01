CREATE OR ALTER FUNCTION getWellTagTitle(@wellTagId varchar(50))
    RETURNS varchar(255)
AS
BEGIN
    -- Declare the return variable here
    DECLARE @result varchar(max)

    -- Add the T-SQL statements to compute the return value here
    SET @result =
            CASE @wellTagId
                WHEN 'F' THEN 'Newly drilled'
                WHEN 'S' THEN 'Run>1'
--                 ELSE ''
                END

    -- Return the result of the function
    RETURN @result
END^

CREATE OR ALTER FUNCTION getWellEqiupTitle(@wellEquipId varchar(50))
    RETURNS varchar(255)
AS
BEGIN
    -- Declare the return variable here
    DECLARE @result varchar(max)

    -- Add the T-SQL statements to compute the return value here
    SET @result =
            CASE @wellEquipId
                WHEN 'B' THEN 'BoretsWell'
                WHEN 'C' THEN 'CompetitorWell'
                WHEN 'N' THEN 'None'
                END

    -- Return the result of the function
    RETURN @result
END^

CREATE OR ALTER FUNCTION getJobTypeTitle(@jobTypeId varchar(50))
    RETURNS varchar(255)
AS
BEGIN
    -- Declare the return variable here
    DECLARE @result varchar(max)

    -- Add the T-SQL statements to compute the return value here
    SET @result =
            CASE @jobTypeId
                WHEN 'I' THEN 'Install'
                WHEN 'P' THEN 'Pull'
                WHEN 'AW' THEN 'ActiveWells'
                WHEN 'WCH' THEN 'WellCheck'
                WHEN 'WM' THEN 'WellMonitoring'
                END

    -- Return the result of the function
    RETURN @result
END^

CREATE OR ALTER FUNCTION getContractTypeTitle(@contractTypeId varchar(50))
    RETURNS varchar(255)
AS
BEGIN
    -- Declare the return variable here
    DECLARE @result varchar(255)

    -- Add the T-SQL statements to compute the return value here
    SET @result =
            CASE @contractTypeId
                WHEN 'S' THEN 'Sale'
                WHEN 'R' THEN 'Rental'
                ELSE 'undefined'
                END

    -- Return the result of the function
    RETURN @result
END^

CREATE OR ALTER FUNCTION getAnalyticTitle(@jobTypeId varchar(50), @wellEquipId varchar(50), @wellTagId varchar(50))
    RETURNS varchar(255)
AS
BEGIN
    -- Declare the return variable here
    DECLARE @result varchar(max)

    -- Add the T-SQL statements to compute the return value here
    SET @result =
                COALESCE(dbo.getJobTypeTitle(@jobTypeId), '')
                + COALESCE('-' + dbo.getWellEqiupTitle(@wellEquipId), '')
                + COALESCE('-' + dbo.getWellTagTitle(@wellTagId), '')

    -- Return the result of the function
    RETURN @result
END^

CREATE OR ALTER FUNCTION getAccountTypeTitle(@accountTypeId varchar(50))
    RETURNS varchar(255)
AS
BEGIN
    -- Declare the return variable here
    DECLARE @result varchar(max)

    -- Add the T-SQL statements to compute the return value here
    SET @result =
            CASE @accountTypeId
                WHEN 'S' THEN 'Strategic'
                WHEN 'K' THEN 'Key'
                WHEN 'O' THEN 'Other'
                ELSE 'Inactive'
                END

    -- Return the result of the function
    RETURN @result
END^

CREATE OR ALTER FUNCTION getAccountApplicationTypeTitle(@accountApplicationTypeId varchar(50))
    RETURNS varchar(255)
AS
BEGIN
    -- Declare the return variable here
    DECLARE @result varchar(max)

    -- Add the T-SQL statements to compute the return value here
    SET @result =
            CASE @accountApplicationTypeId
                WHEN 'C' THEN 'Conventional'
                WHEN 'U' THEN 'Unconventional'
                WHEN 'W' THEN 'Water Production'
                WHEN 'CO2' THEN 'CO2'
                WHEN 'S' THEN 'SAGD'
                ELSE 'Undefined'
                END

    -- Return the result of the function
    RETURN @result
END^

CREATE OR ALTER FUNCTION getAccountTypeOrder(@accountTypeId varchar(50))
    RETURNS int
AS
BEGIN
    -- Declare the return variable here
    DECLARE @result int

    -- Add the T-SQL statements to compute the return value here
    SET @result =
            CASE @accountTypeId
                WHEN 'S' THEN 10
                WHEN 'K' THEN 20
                WHEN 'O' THEN 30
                ELSE 100
                END

    -- Return the result of the function
    RETURN @result
END^

CREATE OR ALTER FUNCTION calcRecordTypeWeight(@recordTypeId varchar(50))
    RETURNS int
    WITH SCHEMABINDING
AS
BEGIN
    -- Declare the return variable here
    DECLARE @result varchar(max)

    -- Add the T-SQL statements to compute the return value here
    SET @result =
            CASE @recordTypeId
                WHEN 'KPI' THEN 10
                WHEN 'Q1' THEN 20
                WHEN 'Q2' THEN 30
                WHEN 'Q3' THEN 40
                WHEN 'Q4' THEN 50
                WHEN 'FC' THEN 60
                END

    -- Return the result of the function
    RETURN @result

END^

CREATE OR ALTER FUNCTION getActivityDetails (
    @threshold_date Datetime2,
    @query_mode varchar(50) = 'FC'
)

    RETURNS TABLE
        AS
        RETURN
        WITH
            SETTINGS AS (
                SELECT PCSAD.ANALYTIC_SET_ID,
                       PCS.COUNTRY_ID,
                       PCSAD.ORDER_
                FROM PFA_COUNTRY_SETTING PCS
                         JOIN PFA_COUNTRY_SETTING_ANALYTIC_DETAIL PCSAD ON PCS.ID = PCSAD.COUNTRY_SETTING_ID
            ),
            DETAILS AS (
                SELECT PA.ACCOUNT_ID,
                       A.NAME AS ACCOUNT_NAME,
                       A.CONTRACT_TYPE,
                       A.APPLICATION_TYPE,
                       A.DELETE_TS AS ACCOUNT_DELETE_TS,
                       PAR.TYPE_ AS ACCOUNT_TIER,
                       PAR.ACTIVE,
                       E.NAME AS MANAGER_NAME,
                       PARA.NAME AS PARENT_ACCOUNT_NAME,
                       PAD.ANALYTIC_ID,
                       PAS.JOB_TYPE,
                       PAS.WELL_EQUIP,
                       PAS.WELL_TAG,
                       S.ORDER_ AS ANALYTIC_ORDER,
                       PAD.CREATE_TS,
                       PA.RECORD_TYPE,
                       PAD.YEAR_,
                       PAD.MONTH_,
                       PAD.VALUE_
                FROM PFA_ACTIVITY_DETAIL PAD
                         JOIN PFA_ACTIVITY PA on PAD.ACTIVITY_ID = PA.ID
                         JOIN PFA_ACCOUNT A ON PA.ACCOUNT_ID = A.ID
                         LEFT JOIN PFA_ACCOUNT PARA ON A.PARENT_ID = PARA.ID
                         LEFT JOIN PFA_ACCOUNT_REVISION PAR ON A.ACTUAL_REVISION_ID = PAR.ID
                         LEFT JOIN PFA_EMPLOYEE E ON PAR.MANAGER_ID = E.ID
                         JOIN PFA_ANALYTIC_SET PAS on PAD.ANALYTIC_ID = PAS.ID AND PAS.DELETE_TS IS NULL
                         LEFT JOIN SETTINGS S ON S.ANALYTIC_SET_ID = PAD.ANALYTIC_ID AND S.COUNTRY_ID = A.COUNTRY_ID
                WHERE PAD.DELETE_TS IS NULL
                  AND PAD.CREATE_TS < COALESCE(@threshold_date, GETDATE())
                  AND dbo.calcRecordTypeWeight(PA.RECORD_TYPE) <= dbo.calcRecordTypeWeight(@query_mode)

            ),
            NUNBERED_DETAILS AS (
                SELECT *,
                       ROW_NUMBER() OVER (PARTITION BY D.ACCOUNT_ID, D.ANALYTIC_ID, D.YEAR_, D.MONTH_ ORDER BY D.CREATE_TS DESC) AS R_NUMBER
                FROM DETAILS D
            )

        SELECT ACCOUNT_ID,
               ACCOUNT_NAME,
               ACTIVE,
               dbo.getContractTypeTitle(CONTRACT_TYPE) AS CONTRACT_TYPE,
               dbo.getAccountApplicationTypeTitle(APPLICATION_TYPE) AS APPLICATION_TYPE,
               PARENT_ACCOUNT_NAME,
               ACCOUNT_DELETE_TS,
               dbo.getAccountTypeTitle(ACCOUNT_TIER) AS ACCOUNT_TIER,
               dbo.getAccountTypeOrder(ACCOUNT_TIER) AS ACCOUNT_ORDER,
               MANAGER_NAME,
               ANALYTIC_ID,
               ANALYTIC_ORDER,
               COALESCE(dbo.getJobTypeTitle(JOB_TYPE), '')
                   + COALESCE('-' + dbo.getWellEqiupTitle(WELL_EQUIP), '')
                   + COALESCE('-' + dbo.getWellTagTitle(WELL_TAG), '') AS ANALYTIC_TITLE,
               CREATE_TS,
               RECORD_TYPE,
               YEAR_,
               MONTH_,
               VALUE_
        FROM NUNBERED_DETAILS
        WHERE R_NUMBER = 1^

