package com.borets.pfa.report.custom;

import com.haulmont.yarg.structure.BandData;

import java.util.Map;
import java.util.Objects;

public class Account {

    public static final String ACCOUNT_TYPE_FIELD = "ACCOUNT_TYPE";
    public static final String ACCOUNT_ORDER_FIELD = "ACCOUNT_ORDER";
    public static final String ACCOUNT_PARENT_FIELD = "PARENT_ACCOUNT_NAME";
    public static final String ACCOUNT_CUSTOMER_FIELD = "ACCOUNT_NAME";
    public static final String ACCOUNT_BUSINESS_MODEL_FIELD = "BUSINESS_MODEL";
    public static final String ACCOUNT_APPLICATION_TYPE_FIELD = "APPLICATION_TYPE";
    public static final String ACCOUNT_MANAGER_FIELD = "ACCOUNT_MANAGER";
    public static final String ACCOUNT_ACTIVE_FIELD = "ACTIVE";
    public static final String ACCOUNT_IS_DELETED_FIELD = "IS_DELETED";

    private final String type;

    private final String parent;

    private final String customer;

    private final String businessModel;

    private final String applicationType;

    private final String accountManager;

    private final int order;

    private final boolean active;

    private final boolean isDeleted;

    public static Account from(BandData dataElement) {
        Map<String, Object> data = dataElement.getData();
        return new Account(
                (String) data.getOrDefault(ACCOUNT_TYPE_FIELD, "---"),
                (String) data.get(ACCOUNT_PARENT_FIELD),
                (String) data.getOrDefault(ACCOUNT_CUSTOMER_FIELD, "---"),
                (String) data.getOrDefault(ACCOUNT_MANAGER_FIELD, ""),
                (String) data.getOrDefault(ACCOUNT_BUSINESS_MODEL_FIELD, "n/a"),
                (String) data.getOrDefault(ACCOUNT_APPLICATION_TYPE_FIELD, "n/a"),
                (Integer) data.getOrDefault(ACCOUNT_ORDER_FIELD, 0),
                (int) data.getOrDefault(ACCOUNT_ACTIVE_FIELD, 0) > 0,
                (int) data.getOrDefault(ACCOUNT_IS_DELETED_FIELD, 0) > 0
        );
    }

    private Account(String type, String parent, String customer, String accountManager, String businessModel, String applicationType, int order, boolean active, boolean isDeleted) {
        this.type = Objects.requireNonNull(type);
        this.parent = parent;
        this.customer = Objects.requireNonNull(customer);
        this.accountManager = accountManager;
        this.businessModel = Objects.requireNonNull(businessModel);
        this.applicationType = Objects.requireNonNull(applicationType);
        this.order = order;
        this.active = active;
        this.isDeleted = isDeleted;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return order == account.order && type.equals(account.type) && Objects.equals(parent, account.parent)
                && customer.equals(account.customer) && Objects.equals(accountManager, account.accountManager)
                && businessModel.equals(account.businessModel) && applicationType.equals(account.applicationType)
                && active == account.active && isDeleted == account.isDeleted;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, parent, accountManager, customer, businessModel, applicationType, order, active, isDeleted);
    }

    public String getType() {
        return type;
    }

    public String getParent() {
        return parent;
    }

    public String getCustomer() {
        return customer;
    }

    public String getAccountManager() {
        return accountManager;
    }

    public String getBusinessModel() {
        return businessModel;
    }

    public String getApplicationType() {
        return applicationType;
    }

    public int getOrder() {
        return order;
    }

    public boolean isActive() {
        return active;
    }

    public String getActiveStr() {
        return isActive() ? "Active" : "Inactive";
    }

    public boolean isDeleted() {
        return isDeleted;
    }
    @Override
    public String toString() {
        return "Account{" +
                "type='" + type + '\'' +
                ", parent='" + parent + '\'' +
                ", customer='" + customer + '\'' +
                ", accountManager='" + accountManager + '\'' +
                ", businessModel='" + businessModel + '\'' +
                ", applicationType='" + applicationType + '\'' +
                ", order=" + order +
                ", active=" + active +
                ", isDeleted=" + isDeleted +
                '}';
    }
}
