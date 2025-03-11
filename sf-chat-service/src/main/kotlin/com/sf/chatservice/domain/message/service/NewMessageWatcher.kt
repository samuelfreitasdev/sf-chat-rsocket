package com.sf.chatservice.domain.message.service

import com.mongodb.client.model.changestream.OperationType
import com.sf.chatservice.domain.message.api.Message
import com.sf.chatservice.domain.message.repository.model.MessageDocument
import com.sf.chatservice.domain.resuming.UserResumeTokenService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.bson.BsonTimestamp
import org.springframework.data.mongodb.core.ChangeStreamEvent
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

private val logger = KotlinLogging.logger {}

@Component
class NewMessageWatcher(
    private val resumeTokenService: UserResumeTokenService, private val reactiveMongoTemplate: ReactiveMongoTemplate
) {

    fun newMessagesForChats(chats: Mono<Set<UUID>>, usernameMono: Mono<String>): Flux<Message> {
        return usernameMono.flatMapMany { username ->
            resumeTokenService.getResumeTimestampFor(usernameMono)
                .flatMapMany { timestamp -> changeStream(username, chats, timestamp) }
                .doOnCancel { resumeTokenService.saveAndGenerateNewTokenFor(username) }
        }
    }

    private fun changeStream(username: String, chats: Mono<Set<UUID>>, timestamp: BsonTimestamp): Flux<Message> {
        return reactiveMongoTemplate
            .changeStream(MessageDocument::class.java)
            .watchCollection("messages")
            .resumeAt(timestamp)
            .listen()
            .doOnNext { logger.info { "New messages for chat $username: $it" } }
            .filter { it.operationType == OperationType.INSERT }
            .mapNotNull(ChangeStreamEvent<MessageDocument>::getBody)
            .doOnNext { logger.info { "New message: $it" } }
            .filter { it != null }
            .filter { it!!.isNotFromUser(username) }
            .filterWhen { message -> chats.map { it.contains(message!!.chatRoomId) } }
            .map { Message.from(it!!) }
    }

}