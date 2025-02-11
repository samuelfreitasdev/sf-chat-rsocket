package com.sf.chatservice.keycloak

import org.keycloak.admin.client.Keycloak

data class KeyCloakProperties(
    val client: KeycloakClient,
    val admin: KeycloakClient,
    val user1: KeycloakUser,
    val user2: KeycloakUser,
    val adminUser: KeycloakUser,
    val realm: String,
) {
    companion object {
        val ADMIN_CLI_CLIENT = KeycloakClient("admin", "admin")
    }
}

data class KeycloakClient(
    val clientId: String,
    val clientSecret: String,
)

data class KeycloakUser(
    val username: String, val password: String
)

data class KeyCloakAccess(
    val keycloakAdminAccess: Keycloak, val user1Access: Keycloak, val user2Access: Keycloak
) {

    fun getToken1() = getToken(user1Access)
    fun getToken2() = getToken(user2Access)

    private fun getToken(access: Keycloak) = access.tokenManager().accessToken.token
}