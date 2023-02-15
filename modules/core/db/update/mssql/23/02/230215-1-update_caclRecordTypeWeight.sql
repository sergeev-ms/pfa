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
                ELSE 0
                END
    -- Return the result of the function
    RETURN @result
END