package com.sentinal.app.util.pairing
import com.sentinal.app.util.model.PairingState
interface PairingManager {
    fun isOnHomeWifi(): Boolean
    fun current(): PairingState
}
class PairingManagerStub : PairingManager {
    override fun isOnHomeWifi() = false
    override fun current() = PairingState()
}
