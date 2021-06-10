package com.borets.pfa.entity.account.system.classification

import javax.persistence.Entity

@Entity(name = "pfa_OtherMaterials")
open class OtherMaterials : Materials() {
    companion object {
        private const val serialVersionUID = -2823037827559115872L
    }
}