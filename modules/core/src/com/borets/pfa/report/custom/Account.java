package com.borets.pfa.report.custom;

import java.util.Objects;

public class Account {

    private final String type;

    private final String parent;

    private final String customer;

    private final String businessModel;

    private final String applicationType;

    private final String accountManager;

    private final int order;

    public Account(String type, String parent, String customer, String accountManager, String businessModel, String applicationType, int order) {
        this.type = Objects.requireNonNull(type);
        this.parent = parent;
        this.customer = Objects.requireNonNull(customer);
        this.accountManager = accountManager;
        this.businessModel = Objects.requireNonNull(businessModel);
        this.applicationType = Objects.requireNonNull(applicationType);
        this.order = order;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return order == account.order && type.equals(account.type) && Objects.equals(parent, account.parent)
                && customer.equals(account.customer) && Objects.equals(accountManager, account.accountManager)
                && businessModel.equals(account.businessModel) && applicationType.equals(account.applicationType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, parent, accountManager, customer, businessModel, applicationType, order);
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
                '}';
    }
}
