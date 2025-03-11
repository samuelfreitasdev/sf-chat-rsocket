package com.sf.chatservice.domain.message.repository

import com.sf.chatservice.domain.message.repository.model.MessageDocument
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import java.util.*

@Repository
interface MessageRepository : ReactiveMongoRepository<MessageDocument, UUID> {

    fun findByChatRoomId(chatRoomId: UUID, pageable: Pageable): Flux<MessageDocument>
    fun findByChatRoomId(chatRoomId: UUID, sort: Sort): Flux<MessageDocument>

}