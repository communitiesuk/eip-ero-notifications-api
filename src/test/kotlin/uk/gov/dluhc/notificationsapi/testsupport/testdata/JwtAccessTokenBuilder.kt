package uk.gov.dluhc.notificationsapi.testsupport.testdata

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import uk.gov.dluhc.eromanagementapi.models.EroGroup
import uk.gov.dluhc.notificationsapi.config.IntegrationTest.Companion.ERO_ID
import uk.gov.dluhc.notificationsapi.testsupport.RsaKeyPair
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Date
import java.util.UUID

const val UNAUTHORIZED_BEARER_TOKEN: String =
    "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyQHdpbHRzaGlyZS5nb3YudWsiLCJpYXQiOjE1MTYyMzkwMjIsImF1dGhvcml0aWVzIjpbImVyby1hZG1pbiJdfQ.-pxW8z2xb-AzNLTRP_YRnm9fQDcK6CLt6HimtS8VcDY"

fun getBearerToken(
    eroId: String = aValidRandomEroId(),
    email: String = "an-ero-user@$eroId.gov.uk",
    groups: List<String> = listOf("ero-$eroId", "ero-vc-admin-$eroId"),
): String =
    "Bearer ${buildAccessToken(eroId, email, groups)}"

fun getBearerTokenWithAllRolesExcept(
    eroId: String = aValidRandomEroId(),
    email: String = "an-ero-user@$eroId.gov.uk",
    excludedRoles: List<String> = listOf("ero-vc-admin"),
): String {
    val excludedGroups = excludedRoles.map { "$it-$eroId" }.toSet()
    val allGroupEroNames = EroGroup.values().map { it.value }.map { "ero-$it-$eroId" }.toSet()
    val requiredGroups = allGroupEroNames - excludedGroups
    return getBearerToken(eroId, email, requiredGroups.toList())
}

fun getVCAnonymousAdminBearerToken(eroId: String = ERO_ID, userName: String = "an-ero-user1@$eroId.gov.uk") =
    getBearerToken(groups = listOf("ero-$eroId", "ero-vc-anonymous-admin-$eroId"), email = userName)

fun getVCAdminBearerToken(eroId: String = ERO_ID, userName: String = "an-ero-user2@$eroId.gov.uk") =
    getBearerToken(groups = listOf("ero-$eroId", "ero-vc-admin-$eroId"), email = userName)

fun buildAccessToken(
    eroId: String = aValidRandomEroId(),
    email: String = "an-ero-user@$eroId.gov.uk",
    groups: List<String> = listOf("ero-$eroId", "ero-vc-admin-$eroId"),
): String =
    Jwts.builder()
        .setSubject(UUID.randomUUID().toString())
        .setClaims(
            mapOf(
                "cognito:groups" to groups,
                "email" to email,
            ),
        )
        .setIssuedAt(Date.from(Instant.now()))
        .setExpiration(Date.from(Instant.now().plus(1, ChronoUnit.HOURS)))
        .signWith(RsaKeyPair.privateKey, SignatureAlgorithm.RS256)
        .compact()
