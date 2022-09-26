package uk.gov.dluhc.notificationsapi.testsupport

import org.apache.commons.lang3.RandomStringUtils
import org.springframework.stereotype.Service
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderAsyncClient
import software.amazon.awssdk.services.cognitoidentityprovider.model.AuthFlowType
import uk.gov.dluhc.aws.cognito.AsyncCognitoService
import uk.gov.dluhc.notificationsapi.config.LocalStackContainerSettings
import java.util.UUID
import java.util.concurrent.CompletableFuture

/**
 * Supports integration tests that delegate to [AsyncCognitoService] to perform cognito functions against cognito in localstack.
 */
@Service
class LocalStackCognitoUserService(
    private val asyncCognitoService: AsyncCognitoService,
    private val cognitoAsyncClient: CognitoIdentityProviderAsyncClient,
    private val localStackContainerSettings: LocalStackContainerSettings,
) {

    /**
     * Create a cognito user in the ERO user pool.
     * If password is not specified a random (UUID) password will be assigned.
     * If groups are not specified the user will not be assigned to any groups. Any groups that are specified need to have been previously created
     * via [LocalStackCognitoUserService.createGroups] or [LocalStackCognitoUserService.createGroup].
     *
     * @return a [CognitoUser] representing the created user, their password, and assigned groups.
     */
    fun createEroUser(
        username: String,
        firstName: String = RandomStringUtils.randomAlphabetic(20),
        surname: String = RandomStringUtils.randomAlphabetic(20),
        phoneNumber: String = "+44${RandomStringUtils.randomNumeric(10)}",
        password: String = UUID.randomUUID().toString(),
        groups: Set<String> = emptySet()
    ): CompletableFuture<CognitoUser> {
        val userPoolId = localStackContainerSettings.cognito.userPoolId
        return asyncCognitoService.createUser(userPoolId, username, firstName, surname, phoneNumber)
            .thenCompose {
                asyncCognitoService.setUserPassword(userPoolId, username, password, false)
            }.thenCompose {
                asyncCognitoService.addUserToGroups(userPoolId, username, groups)
            }.thenCompose {
                CompletableFuture.completedStage(CognitoUser(username, password, groups))
            }
    }

    /**
     * Authenticate a ERO user against the DLUHC user pool client.
     */
    fun authenticateUser(cognitoUser: CognitoUser): CompletableFuture<String> =
        authenticateUser(cognitoUser.username, cognitoUser.password)

    /**
     * Authenticate a ERO user against the DLUHC user pool client.
     */
    fun authenticateUser(username: String, password: String): CompletableFuture<String> =
        cognitoAsyncClient.initiateAuth {
            it.clientId(localStackContainerSettings.cognito.clientId)
                .authFlow(AuthFlowType.USER_PASSWORD_AUTH)
                .authParameters(
                    mapOf(
                        "USERNAME" to username,
                        "PASSWORD" to password
                    )
                )
        }.thenApply { it.authenticationResult().accessToken() }

    /**
     * Creates Cognito groups in the ERO user pool that can subsequently be used as part of creating a user.
     */
    fun createGroups(groups: Set<String>): CompletableFuture<Void> {
        val completableFutures = groups.map { createGroup(it) }.toTypedArray()
        return CompletableFuture.allOf(*completableFutures)
    }

    /**
     * Creates a Cognito group in the ERO user pool  that can subsequently be used as part of creating a user.
     */
    fun createGroup(groupName: String): CompletableFuture<Void> =
        asyncCognitoService.createGroup(localStackContainerSettings.cognito.userPoolId, groupName)

    fun getBearerTokenForUser(cognitoUser: CognitoUser): String {
        val bearerToken: String = authenticateUser(cognitoUser).get()
        return "Bearer $bearerToken"
    }

    fun getBearerTokenForUserWithGroups(username: String, groups: Set<String>): String {
        createGroups(groups).get()
        val cognitoUser = createEroUser(
            username = username,
            groups = groups
        ).get()
        return getBearerTokenForUser(cognitoUser)
    }
}

/**
 * Simple class encapsulating the username, password and assigned groups of a Cognito user.
 */
data class CognitoUser(
    val username: String,
    val password: String,
    val groups: Set<String> = emptySet()
)
