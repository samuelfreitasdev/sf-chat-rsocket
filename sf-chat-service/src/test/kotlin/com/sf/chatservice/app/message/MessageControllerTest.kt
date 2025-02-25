package com.sf.chatservice.app.message

import com.sf.chatservice.app.ChatBaseTest
import com.sf.chatservice.chats.api.ChatCreatedResponse
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import reactor.test.StepVerifier

class MessageControllerTest : ChatBaseTest() {

    @Test
    fun userCanCreateChat() {
        val result = requesterUser1!!
            .route("create-chat")
            .data("create")
            .retrieveMono(ChatCreatedResponse::class.java)
//            .timeout(Duration.ofSeconds(5))

        StepVerifier
            .create(result, 1)
            .consumeNextWith {
                assertThat(it.chatId).isNotNull()
            }
            .verifyComplete()
    }

}