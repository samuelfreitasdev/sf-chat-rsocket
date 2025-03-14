package com.sf.chatservice.domain.chats

import com.sf.chatservice.common.JwtUtil
import com.sf.chatservice.domain.chats.api.ChatCreatedResponse
import com.sf.chatservice.domain.chats.api.JoinChatRequest
import com.sf.chatservice.domain.chats.repository.UserChatsPort
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Controller
import reactor.core.publisher.Mono
import java.util.*

private val logger = KotlinLogging.logger {}

@Controller
class ChatController(private val userChatsRepository: UserChatsPort) {

    @MessageMapping("create-chat")
    fun createChat(join: String?, @AuthenticationPrincipal jwtMono: Mono<Jwt>): Mono<ChatCreatedResponse> {
        logger.info { "Creating new chat" }
        val chatId = UUID.randomUUID()
        val username = jwtUsername(jwtMono)

        return userChatsRepository
            .insertUserOnChat(username, chatId)
            .log()
            .map { ChatCreatedResponse(chatId) }
    }

    @MessageMapping("join-chat")
    fun joinChat(joinChatRequest: JoinChatRequest, @AuthenticationPrincipal jwtMono: Mono<Jwt>): Mono<Boolean> {
        val username = jwtUsername(jwtMono)
        return userChatsRepository.insertUserOnChat(username, joinChatRequest.chatId)
            .log()
    }

    @MessageMapping("get-user-chats")
    fun getUserChats(@AuthenticationPrincipal jwtMono: Mono<Jwt>): Mono<Set<UUID>> {
        val username = jwtUsername(jwtMono)
        return userChatsRepository.findUserChatRooms(username)
    }

    private fun jwtUsername(jwtMono: Mono<Jwt>) = jwtMono
        .map(JwtUtil::extractUserName)
}