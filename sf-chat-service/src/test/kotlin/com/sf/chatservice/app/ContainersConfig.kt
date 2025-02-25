package com.sf.chatservice.app

import com.sf.chatservice.keycloak.KeycloakBootstrapHelper
import dasniko.testcontainers.keycloak.KeycloakContainer
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Bean
import org.springframework.test.context.DynamicPropertyRegistrar
import org.springframework.test.context.DynamicPropertyRegistry
import org.testcontainers.containers.MongoDBContainer


@TestConfiguration(proxyBeanMethods = false)
class ContainersConfig {

    @Bean
    fun keycloakRegistrar(keycloak: KeycloakContainer): DynamicPropertyRegistrar {
        val uri = "${keycloak.authServerUrl}/realms/${keycloak.keycloakAdminClient.realms().findAll().first().realm}"
        return DynamicPropertyRegistrar { registry: DynamicPropertyRegistry ->
            registry.add("spring.security.oauth2.resourceserver.jwt.issuer-uri") { uri }
        }
    }

    @Bean
    fun keycloak(registry: DynamicPropertyRegistry): KeycloakContainer {
        return KeycloakContainer(KeycloakConstants.KEYCLOAK_IMAGE)
    }

    @Bean
    fun keycloakBootstrapHelper(keycloakContainer: KeycloakContainer): KeycloakBootstrapHelper {
        return KeycloakBootstrapHelper(keycloakContainer)
    }

    @Bean
    @ServiceConnection
    fun mongodb(): MongoDBContainer {
        val mongo = MongoDBContainer(MongoDbConstants.MONGODB_IMAGE)
        return mongo
    }

    object KeycloakConstants {
        const val KEYCLOAK_IMAGE = "quay.io/keycloak/keycloak:latest"
        const val ADMIN_USERNAME = "admin"
        const val ADMIN_PASS = "admin"
        const val REALM_IMPORT_FILE = "./sf-chat-realm.json"
        const val REALM_NAME = "sf-chat"
    }

    object MongoDbConstants {
        const val MONGODB_IMAGE: String = "mongo:latest"
        const val BITNAMI_MONGODB_IMAGE: String = "bitnami/mongodb:latest"
        const val USERNAME: String = "user"
        const val PASSWORD: String = "password"
        const val DATABASE: String = "db"
        const val PORT: Int = 27017
    }

}