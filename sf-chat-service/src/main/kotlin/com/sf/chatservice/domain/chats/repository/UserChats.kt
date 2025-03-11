package com.sf.chatservice.domain.chats.repository

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document(collection = "UserChats")
data class UserChats(
    @Id val id: UUID = UUID.randomUUID(),
    @Indexed(unique = true) val username: String,
    val chats: MutableSet<UUID> = mutableSetOf()
) {
    constructor(username: String, chat: UUID) : this(
        id = UUID.randomUUID(),
        username,
        chats = mutableSetOf(chat)
    )
}