package com.borets.pfa.entity.account.supplementary

import com.haulmont.chile.core.annotations.NamePattern
import com.haulmont.cuba.core.entity.BaseUuidEntity
import com.haulmont.cuba.core.entity.Creatable
import com.haulmont.cuba.core.entity.SoftDelete
import java.util.*
import javax.persistence.Column
import javax.persistence.Table

@NamePattern(value = "%s|name")
@Table(name = "PFA_SUPPLEMENTARY_DETAIL_TYPE")
@javax.persistence.Entity(name = "pfa_SupplementaryDetailType")
open class SupplementaryDetailType : BaseUuidEntity(), Creatable, SoftDelete {
    @Column(name = "NAME")
    var name: String? = null

    @Column(name = "CREATE_TS")
    private var createTs: Date? = null

    @Column(name = "CREATED_BY", length = 50)
    private var createdBy: String? = null

    @Column(name = "DELETE_TS")
    private var deleteTs: Date? = null

    @Column(name = "DELETED_BY", length = 50)
    private var deletedBy: String? = null

    override fun isDeleted(): Boolean? = deleteTs != null

    override fun setDeletedBy(deletedBy: String?) {
        this.deletedBy = deletedBy
    }

    override fun getDeletedBy(): String? = deletedBy

    override fun setDeleteTs(deleteTs: Date?) {
        this.deleteTs = deleteTs
    }

    override fun getDeleteTs(): Date? = deleteTs

    override fun setCreatedBy(createdBy: String?) {
        this.createdBy = createdBy
    }

    override fun getCreatedBy(): String? = createdBy

    override fun setCreateTs(createTs: Date?) {
        this.createTs = createTs
    }

    override fun getCreateTs(): Date? = createTs

    companion object {
        private const val serialVersionUID = 1491683225863434873L
    }
}