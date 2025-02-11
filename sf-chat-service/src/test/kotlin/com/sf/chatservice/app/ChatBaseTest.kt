package com.sf.chatservice.app

import com.sf.chatservice.ChatApplication
import com.sf.chatservice.app.RSocketConstants.SIMPLE_AUTH
import com.sf.chatservice.chats.repository.UserChatsPort
import com.sf.chatservice.keycloak.KeyCloakAccess
import com.sf.chatservice.keycloak.KeyCloakContainer
import com.sf.chatservice.keycloak.KeyCloakProperties
import com.sf.chatservice.keycloak.KeycloakInitializers
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.rsocket.server.LocalRSocketServerPort
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.messaging.rsocket.RSocketStrategies
import org.springframework.security.rsocket.metadata.BearerTokenAuthenticationEncoder
import org.springframework.security.rsocket.metadata.BearerTokenMetadata
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import java.net.URI

@SpringBootTest(
    classes = [ChatApplication::class],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(properties = ["spring.rsocket.server.port=0"])
@DirtiesContext
@ActiveProfiles(value = ["test"])
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
        requesterUser1!!.dispose()
        requesterUser2!!.dispose()
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
        return setupRequesterFor(KEY_CLOAK_ACCESS!!.getToken1())
    }

    private fun setupUser2Requester(): RSocketRequester {
        return setupRequesterFor(KEY_CLOAK_ACCESS!!.getToken2())
    }

    private fun setupRequesterFor(token: String): RSocketRequester {
        return builder
            .setupMetadata(BearerTokenMetadata(token), SIMPLE_AUTH)
            .rsocketStrategies { v: RSocketStrategies.Builder -> v.encoder(BearerTokenAuthenticationEncoder()) }
            .websocket(URI.create("ws://localhost:$port"))
    }

    companion object {
        private val KEY_CLOAK_PROPERTIES: KeyCloakProperties = KeycloakInitializers.keyCloakProperties()
        private val KEY_CLOAK_CONTAINER: KeyCloakContainer = KeyCloakContainer(KEY_CLOAK_PROPERTIES.adminUser)
        private var KEY_CLOAK_ACCESS: KeyCloakAccess? = null

        @JvmStatic
        @BeforeAll
        fun beforeAll() {
            KEY_CLOAK_CONTAINER.start()
            KeycloakInitializers.setupKeycloak(KEY_CLOAK_PROPERTIES, KEY_CLOAK_CONTAINER.firstMappedPort)
            KEY_CLOAK_ACCESS = KeycloakInitializers.keycloak(KEY_CLOAK_PROPERTIES, KEY_CLOAK_CONTAINER)
        }
    }
}