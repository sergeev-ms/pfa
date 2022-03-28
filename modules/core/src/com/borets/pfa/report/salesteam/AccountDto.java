package com.borets.pfa.report.salesteam;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AccountDto {

    private static final String COLUMN_ACCOUNT_NAME = "ACCOUNT_NAME";
    private static final String COLUMN_ACCOUNT_TYPE = "ACCOUNT_TYPE";
    private static final String COLUMN_ACCOUNT_MANAGER = "ACCOUNT_MANAGER";
    private static final String COLUMN_BUSINESS_MODEL = "BUSINESS_MODEL";
    private static final String COLUMN_PARENT_ACCOUNT_NAME = "PARENT_ACCOUNT_NAME";
    private static final String COLUMN_ACCOUNT_ORDER = "ACCOUNT_ORDER";
    private static final String COLUMN_ACTIVE = "ACTIVE";
    private static final String COLUMN_APPLICATION_TYPE = "APPLICATION_TYPE";
    private static final String COLUMN_IS_DELETED = "IS_DELETED";

    private final List<PeriodDto> periods = new ArrayList<>();

    private String accountType;
    private String customer;
    private String accountManager;
    private String businessModel;
    private String parent;
    private boolean active;
    private String applicationType;
    private int accountOrder;
    private boolean isDeleted;

    public static AccountDto createFromMap(Map<String, Object> kv) {
        AccountDto accountDto = new AccountDto();
        accountDto.setAccountType(getValue(kv, COLUMN_ACCOUNT_TYPE));
        accountDto.setAccountManager(getValue(kv, COLUMN_ACCOUNT_MANAGER));
        accountDto.setBusinessModel(getValue(kv, COLUMN_BUSINESS_MODEL));
        accountDto.setCustomer(getValue(kv, COLUMN_ACCOUNT_NAME)); // deliberately set account name as customer
        accountDto.setAccountOrder((int) kv.getOrDefault(COLUMN_ACCOUNT_ORDER, 0));
        accountDto.setActive((int) kv.getOrDefault(COLUMN_ACTIVE, 0) > 0);
        accountDto.setApplicationType(getValue(kv, COLUMN_APPLICATION_TYPE));
        accountDto.setDeleted((int) kv.getOrDefault(COLUMN_IS_DELETED, 0) > 0);
        String parent = getValue(kv, COLUMN_PARENT_ACCOUNT_NAME);
        accountDto.setParent(StringUtils.isBlank(parent) ? null : parent);
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
                && Objects.equals(businessModel, that.businessModel) && Objects.equals(parent, that.parent)
                && Objects.equals(active, that.active) && Objects.equals(applicationType, that.applicationType)
                && Objects.equals(accountOrder, that.accountOrder);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountType, customer, accountManager, businessModel, parent, active,
                applicationType, accountOrder, isDeleted);
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

    public List<PeriodDto> getPeriods() {
        return periods;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getActiveStr() {
        return isActive() ? "Active" : "Inactive";
    }

    public String getApplicationType() {
        return applicationType;
    }

    public void setApplicationType(String applicationType) {
        this.applicationType = applicationType;
    }

    public int getAccountOrder() {
        return accountOrder;
    }

    public void setAccountOrder(int accountOrder) {
        this.accountOrder = accountOrder;
    }

}
