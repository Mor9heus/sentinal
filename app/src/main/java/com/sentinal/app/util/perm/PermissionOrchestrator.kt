package com.sentinal.app.util.perm
interface PermissionOrchestrator { fun requestAll() }
class PermissionOrchestratorStub : PermissionOrchestrator { override fun requestAll() {} }
