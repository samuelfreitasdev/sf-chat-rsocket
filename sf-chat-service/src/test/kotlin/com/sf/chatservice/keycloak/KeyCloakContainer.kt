package com.sf.chatservice.keycloak

import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.Wait

class KeyCloakContainer(private val admin: KeycloakUser) : GenericContainer<KeyCloakContainer>(CONTAINER_LABEL) {

    override fun configure() {
        super.configure()
        withEnv("KEYCLOAK_HTTP_PORT", PORT.toString())
        withEnv("KEYCLOAK_USER", admin.username)
        withEnv("KEYCLOAK_PASSWORD", admin.password)
        withExposedPorts(PORT)
        waitingFor(Wait.forListeningPort())
    }

    companion object {
        private const val CONTAINER_LABEL = "jboss/keycloak:16.1.0"
        private const val PORT: Int = 8080
    }
}