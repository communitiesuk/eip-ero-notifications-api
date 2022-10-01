package uk.gov.dluhc.notificationsapi.testsupport.testdata

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import uk.gov.dluhc.notificationsapi.testsupport.RsaKeyPair
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Date
import java.util.UUID

fun buildAccessToken(
    email: String = "an-ero-user@a-council.gov.uk",
    groups: List<String> = with(aValidRandomEroId()) {
        listOf("ero-$this", "ero-vc-admin-$this")
    }
): String =
    Jwts.builder()
        .setSubject(UUID.randomUUID().toString())
        .setClaims(
            mapOf(
                "cognito:groups" to groups,
                "email" to email
            )
        )
        .setIssuedAt(Date.from(Instant.now()))
        .setExpiration(Date.from(Instant.now().plus(1, ChronoUnit.HOURS)))
        .signWith(RsaKeyPair.privateKey, SignatureAlgorithm.RS256)
        .compact()
