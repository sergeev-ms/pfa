package com.borets.pfa.web.screens.account.accountrevision

import com.haulmont.cuba.gui.screen.*
import com.borets.pfa.entity.account.AccountRevision
import com.haulmont.cuba.gui.model.CollectionLoader
import javax.inject.Inject

@UiController("pfa_AccountRevision.browse")
@UiDescriptor("account-revision-browse.xml")
@LookupComponent("accountRevisionsTable")
@LoadDataBeforeShow
class AccountRevisionBrowse : StandardLookup<AccountRevision>() {
    @Inject
    private lateinit var accountRevisionsDl: CollectionLoader<AccountRevision>

    @Subscribe
    private fun onAfterInit(event: AfterInitEvent) {
        val account = (event.options as? MapScreenOptions)?.params?.get("account")
        accountRevisionsDl.setParameter("account", account)
    }

}