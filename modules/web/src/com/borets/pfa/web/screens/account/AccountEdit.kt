package com.borets.pfa.web.screens.account

import com.haulmont.cuba.gui.screen.*
import com.borets.pfa.entity.Account

@UiController("pfa_Account.edit")
@UiDescriptor("account-edit.xml")
@EditedEntityContainer("accountDc")
@LoadDataBeforeShow
class AccountEdit : StandardEditor<Account>()