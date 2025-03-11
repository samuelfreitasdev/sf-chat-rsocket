package com.sf.chatservice.domain.message.api

import com.sf.chatservice.domain.message.repository.model.MessageDocument
import java.time.Clock
import java.time.Instant
import java.util.*

data class InputMessage(
    val usernameFrom: String,
    val content: String,
    val chatRoomId: UUID
) {
    fun toMessageDocument(clock: Clock): MessageDocument {
        return MessageDocument(
            id = UUID.randomUUID(),
            usernameFrom = usernameFrom,
            content = content,
            chatRoomId = chatRoomId,
            timestamp = clock.instant()
        )
    }
}

data class Message(
    val usernameFrom: String,
    val content: String,
    val chatRoomId: UUID,
    val time: Instant
) {
    companion object {
        fun from(messageDocument: MessageDocument): Message {
            return Message(
                messageDocument.usernameFrom,
                messageDocument.content,
                messageDocument.chatRoomId,
                messageDocument.timestamp
            )
        }
    }
}

data class Page(
    val pageNumber: Int,
    val pageSize: Int
)