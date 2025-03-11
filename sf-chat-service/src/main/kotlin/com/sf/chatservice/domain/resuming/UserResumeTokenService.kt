package com.sf.chatservice.domain.resuming

import io.github.oshai.kotlinlogging.KotlinLogging
import org.bson.BsonTimestamp
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.time.Clock
import java.util.*

private val logger = KotlinLogging.logger {}

@Component
class UserResumeTokenService(
    private val userResumeTokenRepository: UserResumeTokenRepository, private val clock: Clock
) {

    fun saveAndGenerateNewTokenFor(username: String) {
        val userToken = userResumeTokenRepository.findByUsername(username)
            .defaultIfEmpty(UserResumeTokenDocument(UUID.randomUUID(), username, generateToken()))
            .map { it.copy(tokenTimestamp = generateToken()) }

        userResumeTokenRepository.saveAll(userToken).subscribeOn(Schedulers.boundedElastic())
            .doOnComplete { logger.info { "User $username resume token saved" } }.subscribe()
    }

    private fun generateToken(): BsonTimestamp {
        val epoch = clock.instant().epochSecond.toInt()
        return BsonTimestamp(epoch, 0)
    }

    fun getResumeTimestampFor(usernameMono: Mono<String>): Mono<BsonTimestamp> {
        return usernameMono.flatMap(userResumeTokenRepository::findByUsername)
            .map(UserResumeTokenDocument::tokenTimestamp).defaultIfEmpty(generateToken())
    }

    fun deleteTokenFor(usernameMono: Mono<String>): Mono<Boolean> {
        return usernameMono
            .flatMap(userResumeTokenRepository::deleteByUsername)
            .map { true }
            .doOnSuccess { logger.info { "User $it resume token deleted" } }
    }
}