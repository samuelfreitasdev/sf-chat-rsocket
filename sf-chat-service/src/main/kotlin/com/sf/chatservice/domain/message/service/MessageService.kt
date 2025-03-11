package com.sf.chatservice.domain.message.service

import com.sf.chatservice.domain.message.api.Message
import com.sf.chatservice.domain.message.repository.MessageRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import java.util.*

@Service
class MessageService(private val repository: MessageRepository) {

    fun findByChatId(chatId: UUID): Flux<Message> =
        repository.findByChatRoomId(chatId, byTimestampDesc())
            .map(Message.Companion::from)

    fun findByChatId(chatId: UUID, page: PageRequest): Flux<Message> =
        repository.findByChatRoomId(chatId, page.withSort(byTimestampDesc()))
            .map(Message.Companion::from)

    private fun byTimestampDesc() = Sort.by(Sort.Direction.DESC, "timestamp")

}