package com.sf.chatservice.message

import com.sf.chatservice.ChatBaseTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import reactor.core.publisher.Mono
import reactor.test.StepVerifier


@Testcontainers
class ChatControllerTest : ChatBaseTest() {

    @Test
    fun userCanCreateChat() {
        val result: Mono<String> = requesterUser1!!
            .route("create-chat")
            .data("create")
            .retrieveMono(String::class.java)

        StepVerifier
            .create(result, 1)
            .consumeNextWith { message: String ->
                assertThat(message).isNotNull()
            }
            .verifyComplete()
    }

    companion object {

        @Container
        @ServiceConnection
        @JvmStatic
        val MONGO_DB_CONTAINER = MongoDBContainer(MongoTestConstants.BITNAMI_MONGODB_IMAGE)
    }

}