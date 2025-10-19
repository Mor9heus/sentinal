package com.sentinal.app.util.vault
interface VaultManager { fun ensureKeys() { /* generate when implemented */ } }
class VaultManagerStub : VaultManager
