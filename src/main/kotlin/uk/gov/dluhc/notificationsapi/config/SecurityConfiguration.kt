package uk.gov.dluhc.notificationsapi.config

import com.nimbusds.jose.PlainHeader
import com.nimbusds.jose.proc.BadJOSEException
import com.nimbusds.jose.proc.SecurityContext
import com.nimbusds.jwt.JWT
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.PlainJWT
import com.nimbusds.jwt.SignedJWT
import com.nimbusds.jwt.proc.BadJWTException
import com.nimbusds.jwt.proc.DefaultJWTProcessor
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpMethod.OPTIONS
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.http.HttpStatus.UNAUTHORIZED
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.web.SecurityFilterChain
import java.text.ParseException

@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfiguration(
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
                        response.status = UNAUTHORIZED.value()
                    }
                    it.accessDeniedHandler { _, response, _ ->
                        response.status = FORBIDDEN.value()
                    }
                }
                .cors { }
                .formLogin { it.disable() }
                .httpBasic { it.disable() }
                .authorizeRequests {
                    it.antMatchers(OPTIONS).permitAll()
                    it.antMatchers("/actuator/**").permitAll()
                    it.anyRequest().authenticated()
                }
                .oauth2ResourceServer { oAuth2ResourceServerConfigurer ->
                    oAuth2ResourceServerConfigurer.jwt {
                        it.jwtAuthenticationConverter(cognitoJwtAuthenticationConverter)
                        it.decoder(NimbusJwtDecoder(SignatureNonValidatingJWTProcessor()))
                    }
                }
        }.build()
}

/**
 * Subclass of [DefaultJWTProcessor] to change the behaviour regarding signed JWTs.
 *
 * Signed JWTs are converted into plain JWTs (no signature) so that the JWT Processor
 * does not try to validate the signature or verify the claims against the signature.
 *
 * The method to process plain JWTs is implemented, as it is left unimplemented in the
 * [DefaultJWTProcessor].
 *
 * This change of behaviour is deliberate and by design. Access Token JWTs are issued to
 * the UI by Cognito. The UI presents the JWT to the REST APIs via API Gateway. API Gateway
 * validates the signature and verifies the claims before forwarding the request to the
 * microservice. Therefore the bearer token (JWT) presented to the APIs of this microservice
 * can be considered trusted, and the default behaviour of validating the signature and
 * verifying the claims is an inefficiency.
 *
 * This approach relies on the network security in respect of VPC and Security Group configuration
 * and does not protect against a man-in-the middle attack (assuming traffic between API Gateway and
 * this microservice could be intercepted and manipulated)
 */
class SignatureNonValidatingJWTProcessor : DefaultJWTProcessor<SecurityContext>() {

    override fun process(jwt: JWT, context: SecurityContext?): JWTClaimsSet =
        super.process(jwt.convertSignedJwtToPlainJwt(), context)

    override fun process(plainJWT: PlainJWT, context: SecurityContext?): JWTClaimsSet {
        if (jwsTypeVerifier == null) {
            throw BadJOSEException("Plain JWT rejected: No JWS header typ (type) verifier is configured")
        }
        jwsTypeVerifier.verify(plainJWT.header.type, context)

        return extractJWTClaimsSet(plainJWT)
    }

    private fun extractJWTClaimsSet(jwt: JWT): JWTClaimsSet =
        try {
            jwt.jwtClaimsSet
        } catch (e: ParseException) {
            // Payload not a JSON object
            throw BadJWTException(e.message, e)
        }

    private fun JWT.convertSignedJwtToPlainJwt(): JWT =
        if (this is SignedJWT) {
            PlainJWT(PlainHeader(), this.jwtClaimsSet)
        } else {
            this
        }
}
