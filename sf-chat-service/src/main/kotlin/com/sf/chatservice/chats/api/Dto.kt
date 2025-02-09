package com.sf.chatservice.chats.api

import java.util.*

// API DATA CLASSES

data class ChatCreatedResponse(val chatId: UUID)
data class JoinChatRequest(val chatId: UUID)
