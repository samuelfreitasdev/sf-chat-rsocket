package com.sf.chatservice

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.rsocket.server.LocalRSocketServerPort
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.TestPropertySource
import java.net.URI


@SpringBootTest(
    classes = [ChatApplication::class],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(properties = ["spring.rsocket.server.port=0"])
@DirtiesContext
class ChatBaseTest {

    @LocalRSocketServerPort
    protected var port: Int? = 0

    @Autowired
    protected lateinit var builder: RSocketRequester.Builder

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
//        val clearUserChatMappings: Unit = chatRoomUserMappings.clear()
//        val deleteMessages: Unit = messageRepository.deleteAll()
//        val clearUser1Token: Unit = userResumeTokenService.deleteTokenForUser(USER_1)
//        val clearUser2Token: Unit = userResumeTokenService.deleteTokenForUser(USER_2)

//        Mono.`when`(clearUserChatMappings, deleteMessages, clearUser1Token, clearUser2Token).block()
    }

    private fun setupUser1Requester(): RSocketRequester {
        return setupRequesterFor("user2")
    }

    private fun setupUser2Requester(): RSocketRequester {
        return setupRequesterFor("user1")
    }

    private fun setupRequesterFor(token: String): RSocketRequester {
        return builder
            .tcp("localhost", port!!)
//        return builder
//            .rsocketStrategies()
//            .websocket(URI.create("ws://localhost:$port"))
    }

}