package com.sf.chatservice.chats.repository

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

interface UserChatsPort {

    fun insertUserOnChat(username: Mono<String>, chatId: UUID): Mono<Boolean>

    fun findUserChatRooms(username: Mono<String>): Mono<Set<UUID>>

    fun clear(): Flux<UserChats>

}