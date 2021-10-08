package com.borets.pfa.web.beans

import com.borets.addon.country.entity.Country
import com.borets.pfa.entity.account.appdata.EquipmentType
import com.borets.pfa.entity.analytic.AnalyticSet
import com.borets.pfa.entity.price.RevenueType
import com.borets.pfa.entity.setting.CountrySettingAnalyticDetail
import com.borets.pfa.entity.setting.CountrySettingEquipmentType
import com.borets.pfa.entity.setting.CountrySettingRevenueType
import com.haulmont.cuba.core.global.DataManager
import com.haulmont.cuba.core.global.View
import org.springframework.stereotype.Component
import javax.inject.Inject

@Component(CountrySettingsBean.NAME)
class CountrySettingsBean {
    @Inject
    private lateinit var dataManager: DataManager

    companion object {
        const val NAME = "pfa_CountrySettings"
    }

    fun getAnalyticSets(country : Country) : List<AnalyticSet> {
        return dataManager.load(CountrySettingAnalyticDetail::class.java)
            .query("""where e.countrySetting.country = :country
                |order by e.order""".trimMargin())
            .parameter("country", country)
            .view { it.add("analyticSet", View.LOCAL) }
            .list()
            .map { it.analyticSet!! }
    }

    fun getPriceAnalyticSets(country : Country) : List<AnalyticSet> {
        return dataManager.load(CountrySettingAnalyticDetail::class.java)
            .query("""where e.countrySetting.country = :country
                |and e.priceList = 1
                |order by e.order""".trimMargin())
            .parameter("country", country)
            .view { it.add("analyticSet", View.LOCAL) }
            .list()
            .map { it.analyticSet!! }
    }

    fun getActivityAnalyticSets(country : Country) : List<AnalyticSet> {
        return dataManager.load(CountrySettingAnalyticDetail::class.java)
            .query("""where e.countrySetting.country = :country
                |and e.activityPlan = 1
                |order by e.order""".trimMargin())
            .parameter("country", country)
            .view { it.add("analyticSet", View.LOCAL) }
            .list()
            .map { it.analyticSet!! }
    }


    fun getRevenueTypes(country : Country) : List<RevenueType> {
        return dataManager.load(CountrySettingRevenueType::class.java)
            .query("""where e.countrySetting.country = :country
                |order by e.revenueType.order, e.revenueType.name""".trimMargin())
            .parameter("country", country)
            .view { it.add("revenueType", View.LOCAL) }
            .list()
            .map { it.revenueType!! }
    }

    fun getEquipmentTypesForUtilizationModel(country : Country) : List<EquipmentType> {
        return dataManager.load(CountrySettingEquipmentType::class.java)
            .query("""where e.countrySetting.country = :country
                |and e.showInUtilModel = 1
                |order by e.order""".trimMargin())
            .parameter("country", country)
            .view { it.add("equipmentType", View.LOCAL) }
            .list()
            .map { it.equipmentType!! }
    }

}