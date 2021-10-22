package com.borets.pfa

import com.haulmont.cuba.testsupport.TestContainer
import org.junit.jupiter.api.extension.ExtensionContext

open class PfaTestContainer : TestContainer() {

    init {
        appComponents = listOf(
            "com.haulmont.cuba",
            "com.haulmont.addon.helium",
            "com.haulmont.addon.admintools",
            "com.haulmont.addon.ldap",
            "com.borets.addon.pn",
            "com.borets.addon.mu",
            "com.borets.attachments",
            "com.borets.addon.country",
            "com.haulmont.reports"
        )
        appPropertiesFiles = mutableListOf(
                // List the files defined in your web.xml
                // in appPropertiesConfig context parameter of the core module
                "com/borets/pfa/app.properties",
                // Add this file which is located in CUBA and defines some properties
                // specifically for test environment. You can replace it with your own
                // or add another one in the end.
                "com/borets/pfa/test-app.properties")

        this.autoConfigureDataSource()
    }

    class Common private constructor() : PfaTestContainer() {

        @Throws(Throwable::class)
        override fun beforeAll(extensionContext: ExtensionContext) {
            if (!initialized) {
                super.beforeAll(extensionContext)
                initialized = true
            }
            setupContext()
        }

        override fun afterAll(extensionContext: ExtensionContext) {
            cleanupContext()
            // never stops - do not call super
        }

        companion object {

            val INSTANCE = Common()

            @Volatile
            private var initialized: Boolean = false
        }
    }
}