package com.sf.chatservice.domain.resuming

import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.util.*

@Repository
interface UserResumeTokenRepository : ReactiveMongoRepository<UserResumeTokenDocument, UUID> {

    fun findByUsername(username: String): Mono<UserResumeTokenDocument>
    fun deleteByUsername(username: String): Mono<Long>

}