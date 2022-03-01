package com.borets.pfa.config

import com.haulmont.cuba.core.config.Config
import com.haulmont.cuba.core.config.Property
import com.haulmont.cuba.core.config.Source
import com.haulmont.cuba.core.config.SourceType
import com.haulmont.cuba.core.config.defaults.DefaultBoolean
import com.haulmont.cuba.core.config.defaults.DefaultInt

@Source(type = SourceType.DATABASE)
interface PriceInputConfig : Config {

    @DefaultBoolean(false)
    @Property("pfa.priceInput.autoFillFromPrev")
    fun getAutoFillFromPrev(): Boolean
    fun setAutoFillFromPrev(value : Boolean)
}