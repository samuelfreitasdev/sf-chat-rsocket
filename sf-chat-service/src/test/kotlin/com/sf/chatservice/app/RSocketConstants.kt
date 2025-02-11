package com.sf.chatservice.app

import io.rsocket.metadata.WellKnownMimeType
import org.springframework.util.MimeType
import org.springframework.util.MimeTypeUtils

object RSocketConstants {

    val SIMPLE_AUTH: MimeType = MimeTypeUtils.parseMimeType(WellKnownMimeType.MESSAGE_RSOCKET_AUTHENTICATION.string)

}