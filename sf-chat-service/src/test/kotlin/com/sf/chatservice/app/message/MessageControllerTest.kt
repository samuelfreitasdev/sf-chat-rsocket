package com.sf.chatservice.app.message

import com.sf.chatservice.app.ChatBaseTest
import com.sf.chatservice.chats.api.ChatCreatedResponse
import com.sf.chatservice.chats.api.JoinChatRequest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.core.ParameterizedTypeReference
import reactor.test.StepVerifier
import java.util.*
import kotlin.test.assertNotNull

class MessageControllerTest : ChatBaseTest() {

    @Test
    fun `It a user should be able to create a chat`() {
        val result = requesterUser1!!
            .route("create-chat")
            .data("create")
            .retrieveMono(ChatCreatedResponse::class.java)

        StepVerifier
            .create(result)
            .assertNext { assertNotNull(it.chatId) }
            .verifyComplete()
    }

    @Test
    fun `It a user should be able to join an existing chat`() {
        val chatId = requesterUser1!!
            .route("create-chat")
            .data("create")
            .retrieveMono(ChatCreatedResponse::class.java)
            .map(ChatCreatedResponse::chatId)
            .block()!!

        val result = requesterUser2!!.route("join-chat")
            .data(JoinChatRequest(chatId))
            .retrieveMono(Boolean::class.java)

        StepVerifier
            .create(result)
            .assertNext { assertThat(it).isTrue() }
            .verifyComplete()
    }

    @Test
    fun `It a user should be able to get a list of chats`() {
        val chatId = requesterUser1!!
            .route("create-chat")
            .data("chat-name")
            .retrieveMono(ChatCreatedResponse::class.java)
            .map(ChatCreatedResponse::chatId)
            .block()!!

        val result = requesterUser1!!
            .route("get-user-chats")
            .retrieveMono(object : ParameterizedTypeReference<Set<UUID>>() {})

        StepVerifier
            .create(result)
            .assertNext { assertThat(it).contains(chatId) }
            .verifyComplete()
    }

    fun `It a user1 should be able to get messages from user2`() {
        val chatId = requesterUser1!!
            .route("create-chat")
            .data("chat-name")
            .retrieveMono(ChatCreatedResponse::class.java)
            .map(ChatCreatedResponse::chatId)
            .block()!!

        val joinChatResult = requesterUser2!!
            .route("join-chat")
            .data(JoinChatRequest(chatId))
            .retrieveMono(Boolean::class.java)
            .block()

        assertThat(joinChatResult).isTrue()

        //user1 wants to send this message
//        val messageFromUser1 = Flux.just(InputMessage("user1", "hello from user1 test1", chatId))
    }
}