package com.sf.chatservice.keycloak

import dasniko.testcontainers.keycloak.KeycloakContainer
import org.keycloak.admin.client.KeycloakBuilder
import org.keycloak.representations.idm.ClientRepresentation
import org.keycloak.representations.idm.CredentialRepresentation
import org.keycloak.representations.idm.RealmRepresentation
import org.keycloak.representations.idm.UserRepresentation

class KeycloakBootstrapHelper(
    private val keycloakContainer: KeycloakContainer,
    private val realm: String = DEFAULT_REALM_NAME,
    private val defaultClient: KeycloakClient = DEFAULT_CLI_CLIENT,
) {

    private val keycloakAdminClient = keycloakContainer.keycloakAdminClient

    init {
        createRealm(realm)
            .also { keycloakAdminClient.realms().create(it) }

        createClient(defaultClient)
            .also { keycloakAdminClient.realms().realm(realm).clients().create(it) }
    }

    fun createUser(username: String, password: String): KeycloakUser {

        val credential = CredentialRepresentation()
            .apply {
                type = CredentialRepresentation.PASSWORD
                value = password
            }

        val user = UserRepresentation()
            .apply {
                this.username = username
                firstName = username
                lastName = username
                email = "$username@example.com"
                credentials = listOf(credential)
                isEnabled = true
                realmRoles = listOf("admin")
            }

        keycloakAdminClient.realm(realm).users().create(user)

        val token = KeycloakBuilder
            .builder()
            .serverUrl(keycloakContainer.authServerUrl)
            .realm(realm)
            .clientId(defaultClient.clientId)
            .clientSecret(defaultClient.clientSecret)
            .username(username)
            .password(password)
            .build()
            .tokenManager()
            .accessToken
            .token

        return KeycloakUser(username, password, token)
    }

    private fun createRealm(realm: String): RealmRepresentation {
        val rep = RealmRepresentation()
        rep.realm = realm
        rep.isEnabled = true
        return rep
    }

    private fun createClient(
        client: KeycloakClient
    ): ClientRepresentation {
        val clientRepresentation = ClientRepresentation()
        clientRepresentation.clientId = client.clientId
        clientRepresentation.secret = client.clientSecret
        clientRepresentation.redirectUris = mutableListOf("*")
        clientRepresentation.isDirectAccessGrantsEnabled = true
        clientRepresentation.isStandardFlowEnabled = true
        clientRepresentation.isServiceAccountsEnabled = true
        return clientRepresentation
    }

    companion object {
        val DEFAULT_CLI_CLIENT = KeycloakClient("test-client", "secret")

        const val DEFAULT_REALM_NAME = "realm"
    }
}