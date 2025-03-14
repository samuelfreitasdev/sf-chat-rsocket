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
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.messaging.handler.invocation.reactive.AuthenticationPrincipalArgumentResolver
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoders
import org.springframework.security.rsocket.core.PayloadSocketAcceptorInterceptor

@Configuration
@EnableRSocketSecurity
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
class RSocketSecurityConfig(
    @Value("\${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private val issuer: String
) {

    @Bean
    fun messageHandler(strategies: RSocketStrategies): RSocketMessageHandler {
        return RSocketMessageHandler()
            .apply { argumentResolverConfigurer.addCustomResolver(AuthenticationPrincipalArgumentResolver()) }
            .apply { setRSocketStrategies(strategies) }
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

    @Bean
    fun jwtDecoder(): ReactiveJwtDecoder = ReactiveJwtDecoders.fromIssuerLocation(issuer)

}