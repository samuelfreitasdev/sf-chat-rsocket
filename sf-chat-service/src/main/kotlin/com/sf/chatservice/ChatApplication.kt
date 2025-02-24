package com.sf.chatservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import reactor.tools.agent.ReactorDebugAgent

@SpringBootApplication
class ChatApplication

fun main(args: Array<String>) {
    ReactorDebugAgent.init()
    runApplication<ChatApplication>(*args)
}
