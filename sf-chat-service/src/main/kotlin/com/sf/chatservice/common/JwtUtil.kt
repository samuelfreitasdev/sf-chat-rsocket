package com.sf.chatservice.common

import org.springframework.security.oauth2.jwt.Jwt

object JwtUtil {

    fun extractUserName(jwt: Jwt): String {
        return jwt.getClaimAsString("preferred_username")
    }
}