package com.borets.pfa.core.secgroup.country.region.omn

import com.borets.pfa.core.secgroup.country.region.RegionGroup
import com.haulmont.cuba.security.app.group.AnnotatedAccessGroupDefinition
import com.haulmont.cuba.security.app.group.annotation.AccessGroup
import com.haulmont.cuba.security.app.group.annotation.SessionAttribute
import java.io.Serializable

@AccessGroup(name = "Oman", parent = RegionGroup::class)
class OmanGroup : AnnotatedAccessGroupDefinition() {

    @SessionAttribute(name = "countryCode", value = "OM", javaClass = String::class)
    override fun sessionAttributes(): MutableMap<String, Serializable> {
        return super.sessionAttributes()
    }

}