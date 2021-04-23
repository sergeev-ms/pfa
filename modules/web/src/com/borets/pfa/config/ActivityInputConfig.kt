package com.borets.pfa.config

import com.haulmont.cuba.core.config.Config
import com.haulmont.cuba.core.config.Property
import com.haulmont.cuba.core.config.Source
import com.haulmont.cuba.core.config.SourceType
import com.haulmont.cuba.core.config.defaults.DefaultInt

@Source(type = SourceType.DATABASE)
interface ActivityInputConfig : Config {

    @DefaultInt(14)
    @Property("pfa.activityInput.defaultMonthQty")
    fun getDefaultMonthQty(): Int
    fun setDefaultMonthQty()
}