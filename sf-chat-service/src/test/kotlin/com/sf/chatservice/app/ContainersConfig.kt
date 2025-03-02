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
        val uri = "${keycloak.authServerUrl}/realms/${KeycloakBootstrapHelper.DEFAULT_REALM_NAME}"
        return DynamicPropertyRegistrar { registry: DynamicPropertyRegistry ->
            registry.add("spring.security.oauth2.resourceserver.jwt.issuer-uri") { uri }
        }
    }

    @Bean
    fun keycloak(registry: DynamicPropertyRegistry): KeycloakContainer {
        return KeycloakContainer(KeycloakConstants.KEYCLOAK_IMAGE)
            .withReuse(true)
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
    }

    object MongoDbConstants {
        const val MONGODB_IMAGE: String = "mongo:latest"
    }

}