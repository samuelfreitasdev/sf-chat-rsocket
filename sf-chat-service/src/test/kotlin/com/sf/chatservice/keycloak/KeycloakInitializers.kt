package com.sf.chatservice.keycloak

import dasniko.testcontainers.keycloak.KeycloakContainer
import org.keycloak.admin.client.KeycloakBuilder
import org.keycloak.representations.idm.ClientRepresentation
import org.keycloak.representations.idm.CredentialRepresentation
import org.keycloak.representations.idm.RealmRepresentation
import org.keycloak.representations.idm.UserRepresentation

object KeycloakInitializers {

    fun keyCloakProperties(): KeyCloakProperties {
        val client = KeycloakClient("test-client", "secret")

        val user1 = KeycloakUser("user1", "test")
        val user2 = KeycloakUser("user2", "test")
        val admin = KeycloakUser("admin", "admin")

        return KeyCloakProperties(client, KeyCloakProperties.ADMIN_CLI_CLIENT, user1, user2, admin, "realm")
    }

    fun keycloak(keyCloakProperties: KeyCloakProperties, keyCloakContainer: KeycloakContainer): KeyCloakAccess {
        val serverUrl = "http://localhost:${keyCloakContainer.firstMappedPort}/auth"

        val adminAccess = KeycloakBuilder.builder()
            .serverUrl(serverUrl)
            .realm("master")
            .clientId(keyCloakProperties.admin.clientId)
            .username(keyCloakProperties.adminUser.username)
            .password(keyCloakProperties.adminUser.password)
            .build()

        val user1access = KeycloakBuilder.builder()
            .serverUrl(serverUrl)
            .realm(keyCloakProperties.realm)
            .clientId(keyCloakProperties.client.clientId)
            .clientSecret(keyCloakProperties.client.clientSecret)
            .username(keyCloakProperties.user1.username)
            .password(keyCloakProperties.user1.password)
            .build()
        val user2Access = KeycloakBuilder.builder()
            .serverUrl(serverUrl)
            .realm(keyCloakProperties.realm)
            .clientId(keyCloakProperties.client.clientId)
            .clientSecret(keyCloakProperties.client.clientSecret)
            .username(keyCloakProperties.user2.username)
            .password(keyCloakProperties.user2.password)
            .build()

        return KeyCloakAccess(adminAccess, user1access, user2Access)
    }

    fun setupKeycloak(
        keyCloakContainer: KeycloakContainer,
        keyCloakProperties: KeyCloakProperties,
    ) {
        keyCloakContainer.authServerUrl

        val keycloak = KeycloakBuilder.builder()
            .serverUrl(keyCloakContainer.authServerUrl)
            .realm(KeycloakContainer.MASTER_REALM)
//            .clientId(keyCloakProperties.admin.clientId)
            .clientId(KeycloakContainer.ADMIN_CLI_CLIENT)
            .username(keyCloakProperties.adminUser.username)
            .password(keyCloakProperties.adminUser.password)
            .build()

        val realm: RealmRepresentation = testRealm(keyCloakProperties.realm)
        keycloak.realms().create(realm)

        val clientRepresentation: ClientRepresentation = testClient(keyCloakProperties.client)
        keycloak.realm(keyCloakProperties.realm).clients().create(clientRepresentation)

        val user1: UserRepresentation = testUser(keyCloakProperties.user1)
        keycloak.realm(keyCloakProperties.realm).users().create(user1)

        val user2: UserRepresentation = testUser(keyCloakProperties.user2)
        keycloak.realm(keyCloakProperties.realm).users().create(user2)
    }

    private fun testUser(keycloakUser: KeycloakUser): UserRepresentation {
        val credential = CredentialRepresentation()
        credential.type = CredentialRepresentation.PASSWORD
        credential.value = keycloakUser.password

        val user = UserRepresentation()
        user.username = keycloakUser.username
        user.firstName = "test"
        user.lastName = "test"
        user.email = "email@example.com"
        user.credentials = listOf(credential)
        user.isEnabled = true
        user.realmRoles = listOf("admin")
        return user
    }

    private fun testRealm(realm: String): RealmRepresentation {
        val rep = RealmRepresentation()
        rep.realm = realm
        rep.isEnabled = true
        return rep
    }

    private fun testClient(keycloakClient: KeycloakClient): ClientRepresentation {
        val clientRepresentation = ClientRepresentation()
        clientRepresentation.clientId = keycloakClient.clientId
        clientRepresentation.secret = keycloakClient.clientSecret
        clientRepresentation.redirectUris = mutableListOf("*")
        clientRepresentation.isDirectAccessGrantsEnabled = true
        clientRepresentation.isStandardFlowEnabled = true
        clientRepresentation.isServiceAccountsEnabled = true
        return clientRepresentation
    }
}