package com.borets.pfa.web.screens.account.directsale.directsale

import com.borets.addon.mu.datatypes.Length
import com.borets.addon.mu.entity.MeasurementUnit
import com.borets.addon.mu.entity.MuType
import com.borets.addon.mu.service.MeasurementService
import com.borets.addon.pn.entity.Part
import com.borets.addon.pn.entity.PartCable
import com.borets.pfa.entity.account.directsale.DirectSale
import com.borets.pfa.entity.account.directsale.DirectSaleDetail
import com.haulmont.chile.core.datatypes.DatatypeRegistry
import com.haulmont.cuba.core.global.DataManager
import com.haulmont.cuba.gui.UiComponents
import com.haulmont.cuba.gui.components.*
import com.haulmont.cuba.gui.components.data.value.ContainerValueSource
import com.haulmont.cuba.gui.model.CollectionPropertyContainer
import com.haulmont.cuba.gui.model.DataContext
import com.haulmont.cuba.gui.screen.*
import com.haulmont.cuba.gui.screen.Target
import javax.inject.Inject

@UiController("pfa_DirectSale.edit")
@UiDescriptor("direct-sale-edit.xml")
@EditedEntityContainer("directSaleDc")
@LoadDataBeforeShow
class DirectSaleEdit : StandardEditor<DirectSale>() {
    @Inject
    private lateinit var dataContext: DataContext
    @Inject
    private lateinit var uiComponents: UiComponents
    @Inject
    private lateinit var dataManager: DataManager
    @Inject
    private lateinit var datatypeRegistry: DatatypeRegistry
    @Inject
    private lateinit var measurementService: MeasurementService

    @Inject
    private lateinit var detailsDc: CollectionPropertyContainer<DirectSaleDetail>

    @Inject
    private lateinit var detailsTable: Table<DirectSaleDetail>

    @Subscribe
    private fun onAfterShow(event: AfterShowEvent) {
        setCaptions()
    }


    @Subscribe("detailsTable.create")
    private fun onDetailsTableCreate(event: Action.ActionPerformedEvent) {
        dataContext.create(DirectSaleDetail::class.java).apply {
            directSale = editedEntity
        }.let {
            detailsDc.mutableItems.add(it)
            detailsDc.setItem(it)
        }
    }

    @Install(to = "detailsTable.part", subject = "columnGenerator", target = Target.COMPONENT,
        type = Any::class, required = true)
    private fun detailsTablePartColumnGenerator(directSaleDetail: DirectSaleDetail): Component {
        @Suppress("UnstableApiUsage")
        return uiComponents.create(SuggestionField.of(Part::class.java)).apply {
            setWidthFull()
            minSearchStringLength = 4
            setSearchExecutor { searchString, _ ->
                return@setSearchExecutor dataManager.load(PartCable::class.java)
                    .query("where e.wtPartNumber like :pnSearchString")
                    .parameter("pnSearchString", "${searchString}%")
                    .list() as List<Any>
            }
            valueSource = ContainerValueSource(detailsTable.getInstanceContainer(directSaleDetail), "part")
        }
    }

    @Install(to = "detailsTable.price", subject = "columnGenerator", target = Target.COMPONENT,
        type = Any::class, required = true)
    private fun detailsTablePriceColumnGenerator(directSaleDetail: DirectSaleDetail): Component {
        @Suppress("UnstableApiUsage")
        return uiComponents.create(CurrencyField.TYPE_BIGDECIMAL).apply {
            setWidthFull()
            currency = "$"
            valueSource = ContainerValueSource(detailsTable.getInstanceContainer(directSaleDetail), "price")
        }
    }

    @Install(to = "detailsTable.length", subject = "columnGenerator", target = Target.COMPONENT,
        type = Any::class, required = true)
    private fun detailsTableLengthColumnGenerator(directSaleDetail: DirectSaleDetail): Component {
        @Suppress("UnstableApiUsage")
        return uiComponents.create(TextField::class.java).apply {
            setWidthFull()
            datatype = datatypeRegistry.get(Length.NAME)
            valueSource = ContainerValueSource(detailsTable.getInstanceContainer(directSaleDetail), "length")
        }
    }

    private fun setCaptions() {
        val measurementUnit: MeasurementUnit? = measurementService.getMeasurementUnit(MuType.LENGTH)
        val lengthColumn = detailsTable.getColumn("length")
        lengthColumn.caption = lengthColumn.caption?.format(measurementUnit?.name)
    }
}