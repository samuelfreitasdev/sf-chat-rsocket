package com.sf.chatservice.app

import com.sf.chatservice.ChatApplication
import com.sf.chatservice.chats.repository.UserChatsPort
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.rsocket.server.LocalRSocketServerPort
import org.springframework.context.annotation.Import
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.messaging.rsocket.RSocketStrategies
import org.springframework.security.rsocket.metadata.BearerTokenAuthenticationEncoder
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import org.testcontainers.junit.jupiter.Testcontainers
import java.net.URI


@SpringBootTest(
    classes = [ChatApplication::class],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(properties = ["spring.rsocket.server.port=0"])
@DirtiesContext
@ActiveProfiles(value = ["test"])
@Testcontainers
@Import(ContainersConfig::class)
class ChatBaseTest {

    @LocalRSocketServerPort
    protected var port: Int? = 0

    @Autowired
    protected lateinit var builder: RSocketRequester.Builder

    @Autowired
    protected lateinit var userChatsRepository: UserChatsPort

    protected var requesterUser1: RSocketRequester? = null
    protected var requesterUser2: RSocketRequester? = null

    @BeforeAll
    fun setupOnce() {
        requesterUser1 = setupUser1Requester()
        requesterUser2 = setupUser2Requester()
    }

    @AfterAll
    fun afterAllTearDown() {
//        requesterUser1!!.dispose()
//        requesterUser2!!.dispose()
    }

    @AfterEach
    fun tearDown() {
        userChatsRepository.clear().blockLast()

//        val clearUserChatMappings: Unit = chatRoomUserMappings.clear()
//        val deleteMessages: Unit = messageRepository.deleteAll()
//        val clearUser1Token: Unit = userResumeTokenService.deleteTokenForUser(USER_1)
//        val clearUser2Token: Unit = userResumeTokenService.deleteTokenForUser(USER_2)

//        Mono.`when`(clearUserChatMappings, deleteMessages, clearUser1Token, clearUser2Token).block()
    }

    private fun setupUser1Requester(): RSocketRequester {
//        return setupRequesterFor(KEY_CLOAK_ACCESS!!.getToken1())
        return setupRequesterFor("")
    }

    private fun setupUser2Requester(): RSocketRequester {
//        return setupRequesterFor(KEY_CLOAK_ACCESS!!.getToken2())
        return setupRequesterFor("")
    }

    private fun setupRequesterFor(token: String): RSocketRequester {
        return builder
//            .setupMetadata(BearerTokenMetadata(token), SIMPLE_AUTH)
            .rsocketStrategies { v: RSocketStrategies.Builder -> v.encoder(BearerTokenAuthenticationEncoder()) }
            .websocket(URI.create("ws://localhost:$port"))
    }

    /*companion object {

        private const val REALM = "sf-chat"

        @Container
        @JvmStatic
        val keycloakContainer = KeycloakContainer("quay.i`o/keycloak/keycloak:latest")
            .withAdminUsername("admin")
            .withAdminPassword("admin")
            .withRealmImportFile("sf-chat-realm.json")
            .withExposedPorts(8080, 9000)
            .waitingFor(HostPortWaitStrategy())
            .apply { portBindings = listOf("8081", "8080") }

//        private val KEY_CLOAK_PROPERTIES = KeycloakInitializers.keyCloakProperties()

        @JvmStatic
        @BeforeAll
        fun beforeAll() {
//            KeycloakInitializers.setupKeycloak(KEY_CLOAK_CONTAINER, KEY_CLOAK_PROPERTIES)
\aZ
            val rep = RealmRepresentation()
            rep.realm = "master"
            rep.isEnabled = ```

//            KeycloakBuilder.builder()
//                .serverUrl(KEY_CLOAK_CONTAINER.authServerUrl)
//                .realm(KeycloakContainer.MASTER_REALM)
//                .clientId(KeycloakContainer.ADMIN_CLI_CLIENT)
//                .username(KEY_CLOAK_CONTAINER.adminUsername)
//                .password(KEY_CLOAK_CONTAINER.adminPassword)
//                .build()
//                .realms()
//                .create(rep)

//            keycloakContainer.keycloakAdminClient.realms().create(rep)

//            KEY_CLOAK_ACCESS = KeycloakInitializers.keycloak(KEY_CLOAK_PROPERTIES, KEY_CLOAK_CONTAINER)
        }

        @DynamicPropertySource
        @JvmStatic
        fun setDatasourceProperties(registry: DynamicPropertyRegistry) {
            val issuer = "http://localhost:8081/auth/realms/$REALM"
            registry.add("spring.security.oauth2.resourceserver.jwt.issuer-uri") { issuer }
        }
    }*/
}