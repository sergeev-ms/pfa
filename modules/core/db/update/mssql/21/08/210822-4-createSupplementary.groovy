import com.borets.pfa.entity.account.Account
import com.borets.pfa.entity.account.supplementary.Supplementary
import com.haulmont.cuba.core.global.AppBeans
import com.haulmont.cuba.core.global.DataManager

postUpdate.add({
    log.info('Executing actions in postUpdate phase')

    DataManager dataManager = AppBeans.get(DataManager.NAME)

    dataManager.load(Account.class)
            .view({it.add("supplementary")})
            .list()
            .stream()
            .filter({it.supplementary == null})
            .forEach({
                def supplementary = dataManager.create(Supplementary.class)
                supplementary.account = it
                dataManager.commit(supplementary)
            })
})