package com.borets.pfa.entity.account.system.classification

import javax.persistence.Entity

@Entity(name = "pfa_PumpMaterials")
open class PumpMaterials : Materials() {
    companion object {
        private const val serialVersionUID = 3676960981107072887L
    }
}