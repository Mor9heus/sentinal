package com.sentinal.app.util.alert
interface AlertManager { fun smsContact1(msg: String) }
class AlertManagerStub : AlertManager { override fun smsContact1(msg: String) {} }
