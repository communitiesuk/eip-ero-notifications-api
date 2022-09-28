package uk.gov.dluhc.notificationsapi.config

import org.springframework.context.annotation.Bean
import org.springframework.core.env.Environment
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain

@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfiguration(
    private val environment: Environment,
    private val cognitoJwtAuthenticationConverter: CognitoJwtAuthenticationConverter
) {

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain =
        http.also { httpSecurity ->
            httpSecurity
                .sessionManagement {
                    it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                }
                .exceptionHandling {
                    it.authenticationEntryPoint { _, response, _ ->
                        response.status = HttpStatus.UNAUTHORIZED.value()
                    }
                    it.accessDeniedHandler { _, response, _ ->
                        response.status = HttpStatus.FORBIDDEN.value()
                    }
                }
                .cors { }
                .formLogin { it.disable() }
                .httpBasic { it.disable() }
                .authorizeRequests {
                    it.antMatchers(HttpMethod.OPTIONS).permitAll()
                    it.antMatchers("/actuator/**").permitAll()
                    it.anyRequest().authenticated()
                }
                // Remove this next section so that the microservice does not verify the bearer token
                .oauth2ResourceServer { oAuth2ResourceServerConfigurer ->
                    oAuth2ResourceServerConfigurer.jwt {
                        it.jwtAuthenticationConverter(cognitoJwtAuthenticationConverter)
                        it.jwkSetUri(environment.getProperty("spring.security.oauth2.resourceserver.jwt.issuer-uri"))
                    }
                }
        }.build()
}
