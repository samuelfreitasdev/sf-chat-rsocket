package com.sf.chatservice.domain.message

import com.sf.chatservice.common.JwtUtil
import com.sf.chatservice.domain.chats.repository.UserChatsPort
import com.sf.chatservice.domain.message.api.InputMessage
import com.sf.chatservice.domain.message.api.Message
import com.sf.chatservice.domain.message.repository.MessageRepository
import com.sf.chatservice.domain.message.repository.model.MessageDocument
import com.sf.chatservice.domain.message.service.NewMessageWatcher
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Controller
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.time.Clock

private val logger = KotlinLogging.logger {}

@Controller
class MessageController(
    private val clock: Clock,
    private val userChatsPort: UserChatsPort,
    private val messageRepository: MessageRepository,
    private val messageWatcher: NewMessageWatcher
) {

    @MessageMapping("chat-channel")
    fun handleMessage(
        incomingMessages: Flux<InputMessage>,
        @AuthenticationPrincipal jwtMono: Mono<Jwt>
    ): Flux<Message> {
        val messages = incomingMessages.map { it.toMessageDocument(clock) }

        val incomingMessagesSubscription = messageRepository.saveAll(messages)
            .then()
            .subscribeOn(Schedulers.boundedElastic())
            .subscribe()

        val userNameMono = jwtMono.map(JwtUtil::extractUserName)
        val userChats = userChatsPort.findUserChatRooms(userNameMono)

        return messageWatcher.newMessagesForChats(userChats, userNameMono)
            .doOnNext { logger.info { "New messages for user $it" } }
            .doOnCancel { incomingMessagesSubscription.dispose() }
            .doOnError { logger.error(it) { "Error while fetching messages for user $it" } }
    }

    @MessageMapping("send-message")
    fun handle(inputMessage: InputMessage, @AuthenticationPrincipal jwtMono: Mono<Jwt>): Mono<Message> {
        val messageDocument = MessageDocument(
            inputMessage.usernameFrom,
            inputMessage.content,
            inputMessage.chatRoomId,
            clock.instant()
        )

        return messageRepository.save(messageDocument)
            .map(Message::from)
    }

    @MessageMapping("messages-stream")
    fun messagesStream(@AuthenticationPrincipal jwtMono: Mono<Jwt>): Flux<Message> {
        val userNameMono = jwtMono.map(JwtUtil::extractUserName)
        val userChats = userChatsPort.findUserChatRooms(userNameMono)

        return messageWatcher.newMessagesForChats(userChats, userNameMono)
            .doOnNext { logger.info { "New messages for user $it" } }
            .doOnError { logger.error(it) { "Error while fetching messages for user $it" } }
    }

}