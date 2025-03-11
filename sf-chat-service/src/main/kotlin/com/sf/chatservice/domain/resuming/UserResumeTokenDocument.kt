package com.sf.chatservice.domain.resuming

import org.bson.BsonTimestamp
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant
import java.util.*

@Document(collection = "user_resume_tokens")
data class UserResumeTokenDocument(
    @Id val uuid: UUID,
    @Indexed(unique = true) val username: String,
    val tokenTimestamp: BsonTimestamp
) {
    constructor(username: String) : this(
        UUID.randomUUID(),
        username,
        BsonTimestamp(Instant.now().epochSecond.toInt(), 0)
    )
}