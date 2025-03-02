package com.sf.chatservice.app.message

import com.sf.chatservice.app.ChatBaseTest
import com.sf.chatservice.chats.api.ChatCreatedResponse
import org.junit.jupiter.api.Test
import reactor.test.StepVerifier
import kotlin.test.assertNotNull

class MessageControllerTest : ChatBaseTest() {

    @Test
    fun userCanCreateChat() {
        val result = requesterUser1!!
            .route("create-chat")
            .data("create")
            .retrieveMono(ChatCreatedResponse::class.java)

        StepVerifier
            .create(result)
            .assertNext { assertNotNull(it.chatId) }
            .verifyComplete()
    }

}