package com.borets.pfa.web.screens.account

import com.haulmont.cuba.gui.screen.*
import com.borets.pfa.entity.account.Account
import com.borets.pfa.entity.account.AccountRevision
import com.haulmont.cuba.gui.ScreenBuilders
import com.haulmont.cuba.gui.components.Button
import com.haulmont.cuba.gui.model.DataContext
import com.haulmont.cuba.gui.model.InstancePropertyContainer
import javax.inject.Inject

@UiController("pfa_Account.edit")
@UiDescriptor("account-edit.xml")
@EditedEntityContainer("accountDc")
@LoadDataBeforeShow
class AccountEdit : StandardEditor<Account>() {
    @Inject
    private lateinit var dataContext: DataContext

    @Inject
    private lateinit var screenBuilders: ScreenBuilders

    @Inject
    private lateinit var actualRevisionDc: InstancePropertyContainer<AccountRevision>

    @Subscribe("createRevisionBtn")
    private fun onCreateRevisionBtnClick(event: Button.ClickEvent) {

        screenBuilders.editor(AccountRevision::class.java, this)
            .newEntity()
            .withParentDataContext(dataContext)
            .withInitializer {
                it.account = editedEntity
            }
            .withOpenMode(OpenMode.DIALOG)
            .build()
            .also {
                it.addAfterCloseListener { event ->
                    if (event.closeAction == WINDOW_COMMIT_AND_CLOSE_ACTION) {
                        @Suppress("UNCHECKED_CAST")
                        actualRevisionDc.setItem((event.screen as StandardEditor<AccountRevision>).editedEntity)
                    }
                }
            }.show()
    }

    @Subscribe("showRevisionsBtn")
    private fun onShowRevisionsBtnClick(event: Button.ClickEvent) {
        screenBuilders.lookup(AccountRevision::class.java, this)
            .withOptions(MapScreenOptions(mutableMapOf(Pair("account", editedEntity)) as Map<String, Any>))
            .show()
    }
}