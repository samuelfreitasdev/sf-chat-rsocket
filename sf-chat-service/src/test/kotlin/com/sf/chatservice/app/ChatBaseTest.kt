package com.sf.chatservice.app

import com.sf.chatservice.ChatApplication
import com.sf.chatservice.chats.repository.UserChatsPort
import com.sf.chatservice.keycloak.KeycloakBootstrapHelper
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.rsocket.server.LocalRSocketServerPort
import org.springframework.context.annotation.Import
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.security.rsocket.metadata.BearerTokenAuthenticationEncoder
import org.springframework.security.rsocket.metadata.BearerTokenMetadata
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.testcontainers.junit.jupiter.Testcontainers
import java.net.URI

@SpringBootTest(
    classes = [ChatApplication::class],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
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

    @Autowired
    protected lateinit var keycloakHelper: KeycloakBootstrapHelper

    protected var requesterUser1: RSocketRequester? = null
    protected var requesterUser2: RSocketRequester? = null

    @BeforeAll
    fun setupOnce() {
        val user1 = keycloakHelper.createUser("user1", "user1")
        val user2 = keycloakHelper.createUser("user2", "user2")

        requesterUser1 = setupRequesterFor(user1.token)
        requesterUser2 = setupRequesterFor(user2.token)
    }

    @AfterAll
    fun afterAllTearDown() {
        requesterUser1?.dispose()
        requesterUser2?.dispose()
    }

    @AfterEach
    fun tearDown() {
        userChatsRepository.clear().blockLast()
    }

    private fun setupRequesterFor(token: String): RSocketRequester {
        return builder
            .setupMetadata(BearerTokenMetadata(token), RSocketConstants.SIMPLE_AUTH)
            .rsocketStrategies { it.encoder(BearerTokenAuthenticationEncoder()) }
            .websocket(URI.create("ws://localhost:$port"))
    }

}