package com.borets.pfa.web.screens.account

import com.haulmont.cuba.gui.screen.*
import com.borets.pfa.entity.account.Account
import com.borets.pfa.entity.account.AccountRevision
import com.borets.pfa.entity.account.appdata.ApplicationData
import com.borets.pfa.entity.account.marketdata.MarketData
import com.haulmont.cuba.core.global.DatatypeFormatter
import com.haulmont.cuba.core.global.EntityStates
import com.haulmont.cuba.core.global.MetadataTools
import com.haulmont.cuba.gui.ScreenBuilders
import com.haulmont.cuba.gui.components.Button
import com.haulmont.cuba.gui.components.GroupBoxLayout
import com.haulmont.cuba.gui.model.DataContext
import com.haulmont.cuba.gui.model.InstanceContainer
import com.haulmont.cuba.gui.model.InstancePropertyContainer
import com.haulmont.cuba.gui.screen.Target
import com.haulmont.cuba.security.global.UserSession
import java.time.format.DateTimeFormatter
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
    private lateinit var entityStates: EntityStates
    @Inject
    private lateinit var metadataTools: MetadataTools
    @Inject
    private lateinit var userSession: UserSession
    @Inject
    private lateinit var datatypeFormatter: DatatypeFormatter

    @Inject
    private lateinit var actualRevisionDc: InstancePropertyContainer<AccountRevision>
    @Inject
    private lateinit var actualMarketDataDc: InstancePropertyContainer<MarketData>
    @Inject
    private lateinit var actualAppDetailDc: InstancePropertyContainer<ApplicationData>

    @Inject
    private lateinit var marketDataGb: GroupBoxLayout
    @Inject
    private lateinit var accountDataGb: GroupBoxLayout
    @Inject
    private lateinit var appDataGb: GroupBoxLayout


    @Subscribe
    private fun onAfterShow(event: AfterShowEvent) {
        setWindowCaption()
    }


    @Subscribe("createRevisionBtn")
    private fun onCreateRevisionBtnClick(event: Button.ClickEvent) {
        screenBuilders.editor(AccountRevision::class.java, this)
            .newEntity()
            .withParentDataContext(dataContext)
            .withInitializer {
                it.account = editedEntity
                it.manager = editedEntity.actualRevision?.manager
                it.setType(editedEntity.actualRevision?.getType())
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

    @Subscribe("createMarketDataBtn")
    private fun onCreateMarketDataBtnClick(event: Button.ClickEvent) {
        screenBuilders.editor(MarketData::class.java, this)
            .newEntity()
            .withParentDataContext(dataContext)
            .withInitializer { new ->
                new.account = editedEntity
                editedEntity.actualMarketDetail?.let { actual ->
                    new.copyFrom(actual)
                }
            }
            .withOpenMode(OpenMode.NEW_TAB)
            .build()
            .also {
                it.addAfterCloseListener { event ->
                    if (event.closeAction == WINDOW_COMMIT_AND_CLOSE_ACTION) {
                        @Suppress("UNCHECKED_CAST")
                        actualMarketDataDc.setItem((event.screen as StandardEditor<MarketData>).editedEntity)
                    }
                }
            }.show()
    }

    @Subscribe("showMarketDetailsBtn")
    private fun onShowMarketDetailsBtnClick(event: Button.ClickEvent) {
        screenBuilders.lookup(MarketData::class.java, this)
            .withOptions(MapScreenOptions(mutableMapOf(Pair("account", editedEntity)) as Map<String, Any>))
            .show()
    }

    @Subscribe(id = "actualRevisionDc", target = Target.DATA_CONTAINER)
    private fun onActualRevisionDcItemChange(event: InstanceContainer.ItemChangeEvent<AccountRevision>) {
        event.item?.let {
            accountDataGb.caption = accountDataGb.caption?.format(
                it.getYearMonth()?.format(DateTimeFormatter.ofPattern("MMM yyyy", userSession.locale)))
            accountDataGb.contextHelpText = "Updated by ${it.createdBy} at ${datatypeFormatter.formatDateTime(it.createTs)}"
        }
    }

    @Subscribe(id = "actualMarketDataDc", target = Target.DATA_CONTAINER)
    private fun onActualMarketDataDcItemChange(event: InstanceContainer.ItemChangeEvent<MarketData>) {
        event.item?.let {
            marketDataGb.caption = marketDataGb.caption?.format(
                it.getYearMonth()?.format(DateTimeFormatter.ofPattern("MMM yyyy", userSession.locale)))
            marketDataGb.contextHelpText = "Updated by ${it.createdBy} at ${datatypeFormatter.formatDateTime(it.createTs)}"
        }
    }

    @Subscribe(id = "actualAppDetailDc", target = Target.DATA_CONTAINER)
    private fun onActualAppDetailDcItemChange(event: InstanceContainer.ItemChangeEvent<ApplicationData>) {
        event.item?.let {
            appDataGb.caption = appDataGb.caption?.format(
                it.getYearMonth()?.format(DateTimeFormatter.ofPattern("MMM yyyy", userSession.locale)))
            appDataGb.contextHelpText = "Updated by ${it.createdBy} at ${datatypeFormatter.formatDateTime(it.createTs)}"
        }
    }


    @Subscribe("createAppDataBtn")
    private fun onCreateAppDataBtnClick(event: Button.ClickEvent) {
        screenBuilders.editor(ApplicationData::class.java, this)
            .newEntity()
            .withParentDataContext(dataContext)
            .withInitializer {
                it.account = editedEntity
            }
            .withOpenMode(OpenMode.NEW_TAB)
            .build()
            .also {
                it.addAfterCloseListener { event ->
                    if (event.closeAction == WINDOW_COMMIT_AND_CLOSE_ACTION) {
                        @Suppress("UNCHECKED_CAST")
                        actualAppDetailDc.setItem((event.screen as StandardEditor<ApplicationData>).editedEntity)
                    }
                }
            }.show()
    }

    @Subscribe("showAppDetailsBtn")
    private fun onShowAppDetailsBtnClick(event: Button.ClickEvent) {
        screenBuilders.lookup(ApplicationData::class.java, this)
            .withOptions(MapScreenOptions(mutableMapOf(Pair("account", editedEntity)) as Map<String, Any>))
            .show()
    }

    private fun setWindowCaption() {
        if (!entityStates.isNew(editedEntity)) {
            window.caption = metadataTools.getInstanceName(editedEntity)
        }
    }

    private inline fun MarketData.copyFrom(other : MarketData) {
        this.setContractType(other.getContractType())
        this.setApplicationType(other.getApplicationType())
        this.setFieldType(other.getFieldType())
        this.trl = other.trl
        this.arl = other.arl
        this.setRunsNumber(other.getRunsNumber())
        this.firstRunDuration = other.firstRunDuration
        this.secondRunDuration = other.secondRunDuration
        this.thirdRunDuration = other.thirdRunDuration
        this.thirdPlusRunDuration = other.thirdPlusRunDuration
        this.wellCount = other.wellCount
        this.conversionRate = other.conversionRate
        this.oilPermits = other.oilPermits
        this.rigQty = other.rigQty
        this.ducQty = other.ducQty
        this.completion = other.completion
        this.activityRate = other.activityRate
        this.budget = other.budget
        this.bShare = other.bShare
        this.wellMonitorQty = other.wellMonitorQty
        this.bWellCount = other.bWellCount
        this.rentalCapex = other.rentalCapex
    }
}