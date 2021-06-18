package com.borets.pfa.listeners

import com.borets.pfa.entity.account.Account
import com.haulmont.cuba.core.TransactionalDataManager
import com.haulmont.cuba.core.app.events.EntityChangedEvent
import com.haulmont.cuba.core.global.View
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener
import java.util.*
import javax.inject.Inject

@Component("pfa_AccountChangedListener")
open class AccountChangedListener {
    @Inject
    private lateinit var transactionalDataManager: TransactionalDataManager

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    open fun beforeCommit(event: EntityChangedEvent<Account, UUID>) {
        val account = transactionalDataManager.load(event.entityId)
            .view {
                it.addAll("name", "clientCard")
                    .add("parent") {parentVb -> parentVb.addView(View.MINIMAL)
                        .add("name")
                    }
            }
            .one()
        if (account.parent != null) {
            account.clientCard = "${account.name} \u2014 ${account.parent!!.name}"
        } else {
            account.clientCard = account.name
        }
        transactionalDataManager.save(account)
    }
}
