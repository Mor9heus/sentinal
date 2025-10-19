package com.sentinal.app.util.curfew
import com.sentinal.app.util.model.CurfewPolicy
interface CurfewManager { fun schedule(policy: CurfewPolicy) }
class CurfewManagerStub : CurfewManager { override fun schedule(policy: CurfewPolicy) {} }
