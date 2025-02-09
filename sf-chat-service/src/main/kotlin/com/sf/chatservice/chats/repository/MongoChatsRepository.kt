package com.sf.chatservice.chats.repository

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.context.annotation.Profile
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*
import java.util.stream.Collectors

@Profile("default")
@Repository
class MongoChatsRepository(
    private val mongoTemplate: ReactiveMongoTemplate,
) : UserChatsPort {

    private val logger = KotlinLogging.logger {}

    override fun insertUserOnChat(username: Mono<String>, chatId: UUID): Mono<Boolean> {
        return username.flatMap { _username ->
            val query = Query.query(Criteria.where("username").`is`(_username))

            val document = mongoTemplate
                .findOne(query, UserChats::class.java)
                .defaultIfEmpty(UserChats(username = _username, chatId))

            return@flatMap mongoTemplate.save(document)
                .doOnNext { logger.info("Saving document $_username:$chatId") }
                .map { true }
        }
    }

    override fun findUserChatRooms(username: Mono<String>): Mono<Set<UUID>> {
        return username
            .map { Query.query(Criteria.where("username").`is`(it)) }
            .flatMap { query ->
                mongoTemplate
                    .findOne(query, UserChats::class.java)
                    .doOnNext { logger.info("Found user [${it.id}:${it.username}]") }
                    .flatMapIterable { it.chats }
                    .collect(Collectors.toSet())
                    .defaultIfEmpty(emptySet())
                    .doOnNext { chats -> logger.info("Chats size ${chats.size}") }
            }
    }

    override fun clear(): Flux<UserChats> {
        return mongoTemplate
            .remove(UserChats::class.java)
            .findAndRemove()
    }
}