package com.sf.chatservice.chats.repository

import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@Profile("test")
@Repository
class InMemoryUserChatsRepository(
    private val userChats: ConcurrentHashMap<String, Set<UUID>> = ConcurrentHashMap<String, Set<UUID>>()
) : UserChatsPort {

    override fun insertUserOnChat(username: Mono<String>, chatId: UUID): Mono<Boolean> {
        return username.mapNotNull { userChats.putIfAbsent(it, setOf(chatId)) }
            .log()
            .map { true }
    }

    override fun findUserChatRooms(username: Mono<String>): Mono<Set<UUID>> {
        return username
            .map(userChats::get)
    }

    override fun clear(): Flux<UserChats> {
        userChats.clear()
        return Flux.empty()
    }

}