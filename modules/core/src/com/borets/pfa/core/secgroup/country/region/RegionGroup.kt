package com.borets.pfa.core.secgroup.country.region

import com.borets.addon.country.entity.Country
import com.borets.pfa.core.secgroup.RootGroup
import com.borets.pfa.entity.account.Account
import com.borets.pfa.entity.activity.Activity
import com.borets.pfa.entity.price.PriceList
import com.borets.pfa.entity.setting.CountrySetting
import com.haulmont.cuba.security.app.group.AnnotatedAccessGroupDefinition
import com.haulmont.cuba.security.app.group.annotation.AccessGroup
import com.haulmont.cuba.security.app.group.annotation.JpqlConstraint
import com.haulmont.cuba.security.app.group.annotation.JpqlConstraintContainer
import com.haulmont.cuba.security.group.ConstraintsContainer

@AccessGroup(name = "region", parent = RootGroup::class)
class RegionGroup : AnnotatedAccessGroupDefinition() {

    @JpqlConstraintContainer(
        JpqlConstraint(target = Country::class, where = "{E}.iso = :session\$countryCode"),
        JpqlConstraint(target = Account::class, where = "{E}.country.iso = :session\$countryCode"),
        JpqlConstraint(target = PriceList::class, where = "{E}.account.country.iso = :session\$countryCode"),
        JpqlConstraint(target = Activity::class, where = "{E}.account.country.iso = :session\$countryCode"),
        JpqlConstraint(target = CountrySetting::class, where = "{E}.country.iso = :session\$countryCode")
    )
    override fun accessConstraints(): ConstraintsContainer {
        return super.accessConstraints()
    }
}