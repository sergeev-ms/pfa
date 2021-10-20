package com.borets.pfa.core.secgroup.country.us

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
import com.haulmont.cuba.security.app.group.annotation.SessionAttribute
import com.haulmont.cuba.security.group.ConstraintsContainer
import java.io.Serializable

@AccessGroup(name = "us", parent = RootGroup::class)
class UsGroup : AnnotatedAccessGroupDefinition() {

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

    @SessionAttribute(name = "countryCode", value = "US", javaClass = String::class)
    override fun sessionAttributes(): MutableMap<String, Serializable> {
        return super.sessionAttributes()
    }

}