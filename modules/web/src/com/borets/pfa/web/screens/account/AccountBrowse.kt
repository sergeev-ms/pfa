package com.borets.pfa.web.screens.account

import com.haulmont.cuba.gui.screen.*
import com.borets.pfa.entity.account.Account
import com.haulmont.cuba.gui.components.Action
import com.haulmont.cuba.gui.components.TreeTable
import javax.inject.Inject

@UiController("pfa_Account.browse")
@UiDescriptor("account-browse.xml")
@LookupComponent("accountsTable")
@LoadDataBeforeShow
class AccountBrowse : StandardLookup<Account>() {
    @Inject
    private lateinit var accountsTable: TreeTable<Account>

    @Subscribe("accountsTable.expandAll")
    private fun onAccountsTableExpandAll(@Suppress("UNUSED_PARAMETER") event: Action.ActionPerformedEvent) {
        accountsTable.expandAll()
    }

    @Subscribe("accountsTable.collapseAll")
    private fun onAccountsTableCollapseAll(@Suppress("UNUSED_PARAMETER") event: Action.ActionPerformedEvent) {
        accountsTable.collapseAll()
    }
}