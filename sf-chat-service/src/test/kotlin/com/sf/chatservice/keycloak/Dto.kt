package com.sf.chatservice.keycloak

data class KeycloakClient(
    val clientId: String,
    val clientSecret: String,
)

data class KeycloakUser(
    val username: String, val password: String, val token: String
)