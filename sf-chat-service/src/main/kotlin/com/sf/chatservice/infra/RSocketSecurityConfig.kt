package com.sf.chatservice.infra

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.rsocket.RSocketStrategies
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.rsocket.EnableRSocketSecurity
import org.springframework.security.config.annotation.rsocket.RSocketSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.messaging.handler.invocation.reactive.AuthenticationPrincipalArgumentResolver
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoders
import org.springframework.security.rsocket.core.PayloadSocketAcceptorInterceptor

@Configuration
@EnableRSocketSecurity
@EnableWebSecurity
@EnableReactiveMethodSecurity
class RSocketSecurityConfig(
    @Value("\${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private val issuer: String
) {

    @Bean
    fun messageHandler(strategies: RSocketStrategies): RSocketMessageHandler {
        return RSocketMessageHandler()
            .apply { setRSocketStrategies(strategies) }
            .apply { argumentResolverConfigurer.addCustomResolver(AuthenticationPrincipalArgumentResolver()) }
    }

    @Bean
    fun authorization(security: RSocketSecurity): PayloadSocketAcceptorInterceptor {
        return security
            .authorizePayload { authorize ->
                authorize
                    .setup()
                    .permitAll()
                    .anyExchange().authenticated()
            }
            .jwt(Customizer.withDefaults())
            .build()
    }

    fun jwtDecoder(): JwtDecoder = ReactiveJwtDecoders.fromIssuerLocation(issuer)

}