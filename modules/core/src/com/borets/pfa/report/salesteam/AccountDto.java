package com.borets.pfa.report.salesteam;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AccountDto {

    private static final String COLUMN_ACCOUNT_NAME = "ACCOUNT_NAME";
    private static final String COLUMN_ACCOUNT_TYPE = "ACCOUNT_TYPE";
    private static final String COLUMN_ACCOUNT_MANAGER = "ACCOUNT_MANAGER";
    private static final String COLUMN_BUSINESS_MODEL = "BUSINESS_MODEL";
    private static final String COLUMN_AREA_BASIN = "AREA_BASIN";
    private static final String COLUMN_REGION = "REGION";
    private static final String COLUMN_IS_DELETED = "IS_DELETED";

    private final List<PeriodDto> periods = new ArrayList<>();

    private String accountType;
    private String customer;
    private String accountManager;
    private String businessModel;
    private String areaBasin;
    private String region;
    private boolean isDeleted;

    public static AccountDto createFromMap(Map<String, Object> kv) {
        AccountDto accountDto = new AccountDto();
        accountDto.setAccountType(getValue(kv, COLUMN_ACCOUNT_TYPE));
        accountDto.setAccountManager(getValue(kv, COLUMN_ACCOUNT_MANAGER));
        accountDto.setBusinessModel(getValue(kv, COLUMN_BUSINESS_MODEL));
        accountDto.setCustomer(getValue(kv, COLUMN_ACCOUNT_NAME)); // deliberately set account name as customer
        accountDto.setAreaBasin(getValue(kv, COLUMN_AREA_BASIN));
        accountDto.setRegion(getValue(kv, COLUMN_REGION));
        accountDto.setDeleted((int) kv.getOrDefault(COLUMN_IS_DELETED, 0) > 0);
        return accountDto;
    }

    private static String getValue(Map<String, Object> kv, String columnName) {
        String value = null;
        if (kv.containsKey(columnName) && kv.get(columnName) != null) {
            value = (String) kv.get(columnName);
        }
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccountDto that = (AccountDto) o;
        return isDeleted == that.isDeleted && Objects.equals(accountType, that.accountType)
                && Objects.equals(customer, that.customer) && Objects.equals(accountManager, that.accountManager)
                && Objects.equals(businessModel, that.businessModel) && Objects.equals(areaBasin, that.areaBasin)
                && Objects.equals(region, that.region);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountType, customer, accountManager, businessModel, areaBasin, region, isDeleted);
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getAccountManager() {
        return accountManager;
    }

    public void setAccountManager(String accountManager) {
        this.accountManager = accountManager;
    }

    public String getBusinessModel() {
        return businessModel;
    }

    public void setBusinessModel(String businessModel) {
        this.businessModel = businessModel;
    }

    public String getAreaBasin() {
        return areaBasin;
    }

    public void setAreaBasin(String areaBasin) {
        this.areaBasin = areaBasin;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public List<PeriodDto> getPeriods() {
        return periods;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }
}
