package com.sf.chatservice.domain.message.repository.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant
import java.util.*

@Document(collection = "messages")
data class MessageDocument(
    @Id
    val id: UUID,
    val usernameFrom: String,
    val content: String,
    val chatRoomId: UUID,
    val timestamp: Instant
) {
    constructor(usernameFrom: String, content: String, chatRoomId: UUID, timestamp: Instant) : this(
        UUID.randomUUID(),
        usernameFrom,
        content,
        chatRoomId,
        timestamp
    )

    fun isNotFromUser(usernameFrom: String): Boolean {
        return this.usernameFrom != usernameFrom
    }
}
