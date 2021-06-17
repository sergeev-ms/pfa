import com.borets.pfa.entity.account.Account
import com.haulmont.cuba.core.global.AppBeans
import com.haulmont.cuba.core.global.DataManager

postUpdate.add {
    log.info('Executing post update action using fully functioning server')
    log.info('Recommit Accounts to setup clientCard attribute with listener')

    def dataManager = AppBeans.get(DataManager.class)
    def accounts = dataManager.load(Account.class)
            .view {it.add("updateTs")}
            .list()
    accounts.each {
        it.updateTs = new Date()
        dataManager.commit(it)
    }
}