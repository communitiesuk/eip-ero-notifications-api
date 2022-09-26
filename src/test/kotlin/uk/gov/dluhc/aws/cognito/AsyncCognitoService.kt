package uk.gov.dluhc.aws.cognito

import mu.KotlinLogging
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderAsyncClient
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminAddUserToGroupRequest
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminAddUserToGroupResponse
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminCreateUserRequest
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminCreateUserResponse
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminGetUserRequest
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminGetUserResponse
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminListGroupsForUserRequest
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminListGroupsForUserResponse
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminRemoveUserFromGroupRequest
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminRemoveUserFromGroupResponse
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminSetUserPasswordRequest
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminSetUserPasswordResponse
import software.amazon.awssdk.services.cognitoidentityprovider.model.AttributeType
import software.amazon.awssdk.services.cognitoidentityprovider.model.CreateGroupRequest
import software.amazon.awssdk.services.cognitoidentityprovider.model.DeliveryMediumType
import software.amazon.awssdk.services.cognitoidentityprovider.model.GetGroupRequest
import software.amazon.awssdk.services.cognitoidentityprovider.model.GetGroupResponse
import software.amazon.awssdk.services.cognitoidentityprovider.model.ListUsersInGroupRequest
import software.amazon.awssdk.services.cognitoidentityprovider.model.ListUsersInGroupResponse
import software.amazon.awssdk.services.cognitoidentityprovider.model.MessageActionType
import software.amazon.awssdk.services.cognitoidentityprovider.model.ResourceNotFoundException
import java.util.concurrent.CompletableFuture

private val logger = KotlinLogging.logger { }

/**
 * Class exposing cognito operations using the AWS asynchronous client and APIs
 */
class AsyncCognitoService(private val cognitoAsyncClient: CognitoIdentityProviderAsyncClient) {

    /**
     * Create a user.
     *
     * @param userPoolId - the cognito user pool that the new user should be created in
     * @param email - the email address of the new user, which will be used as their username
     * @param firstName - the user's first name. Defaults to null and is only set as a user attribute if specified
     * @param surname - the user's surname. Defaults to null and is only set as a user attribute if specified
     * @param phoneNumber - the user's phone number. Defaults to null and is only set as a user attribute if specified
     * @param messageAction - the message action type that cognito should use for the created user. Default is null
     * @param desiredDeliveryMediums - the delivery medium(s) used to send the generated password to the new user. Default is email
     */
    fun createUser(
        userPoolId: String,
        email: String,
        firstName: String? = null,
        surname: String? = null,
        phoneNumber: String? = null,
        messageAction: MessageActionType? = null,
        desiredDeliveryMediums: Collection<DeliveryMediumType> = setOf(DeliveryMediumType.EMAIL)
    ): CompletableFuture<AdminCreateUserResponse> =
        cognitoAsyncClient.adminCreateUser(
            AdminCreateUserRequest.builder()
                .userPoolId(userPoolId)
                .username(email)
                .userAttributes(userAttributesForNewUser(email, firstName, surname, phoneNumber))
                .messageAction(messageAction)
                .desiredDeliveryMediums(desiredDeliveryMediums)
                .build()
        )

    /**
     * Retrieves the user with the given username
     *
     * @param userPoolId - the cognito user pool that the user will be retrieved from
     * @param username - the email address of the user to be retrieved
     */
    fun getUser(
        userPoolId: String,
        username: String
    ): CompletableFuture<AdminGetUserResponse> =
        cognitoAsyncClient.adminGetUser(
            AdminGetUserRequest.builder()
                .userPoolId(userPoolId)
                .username(username)
                .build()
        )

    /**
     * Sets/changes a user's password.
     *
     * @param userPoolId - the cognito user pool that the user exists in
     * @param username - the username whose password should be changed
     * @param password - the user's new password
     * @param changePasswordAtLogin - whether the password is pre-expired forcing the user to change their password at login. Default is true
     */
    fun setUserPassword(
        userPoolId: String,
        username: String,
        password: String,
        changePasswordAtLogin: Boolean = true
    ): CompletableFuture<AdminSetUserPasswordResponse> =
        cognitoAsyncClient.adminSetUserPassword(
            AdminSetUserPasswordRequest.builder()
                .userPoolId(userPoolId)
                .username(username)
                .password(password)
                .permanent(!changePasswordAtLogin)
                .build()
        )

    /**
     * Creates a Cognito group that can subsequently be assigned to a user.
     *
     * @param userPoolId - the cognito user pool to add the group to
     * @param groupName - the group name to create
     */
    fun createGroup(userPoolId: String, groupName: String): CompletableFuture<Void> =
        cognitoAsyncClient.getGroup(
            GetGroupRequest.builder()
                .userPoolId(userPoolId)
                .groupName(groupName)
                .build()
        ).exceptionally {
            if (it.cause is ResourceNotFoundException)
            // The group does not exist so create it. This is not an exceptional situation so it not an error - it' s the only way we can tell if the group exists beforehand
                cognitoAsyncClient.createGroup(
                    CreateGroupRequest.builder()
                        .userPoolId(userPoolId)
                        .groupName(groupName)
                        .build()
                ).thenApply { GetGroupResponse.builder().build() }.get()
            else
                throw it
        }
            .thenApply { null }

    /**
     * Creates Cognito groups that can subsequently be assigned to a user.
     *
     * @param userPoolId - the cognito user pool to add the groups to
     * @param groupNames - the group names to create
     */
    fun createGroups(userPoolId: String, groupNames: Set<String>): CompletableFuture<Void> {
        val completableFutures = groupNames.map { createGroup(userPoolId, it) }.toTypedArray()
        return CompletableFuture.allOf(*completableFutures)
    }

    /**
     * Adds a user to a group.
     *
     * @param userPoolId - the cognito user pool that the user and group exist in
     * @param username - the username to add to the group
     * @param groupName - the group to add the user to
     */
    fun addUserToGroup(
        userPoolId: String,
        username: String,
        groupName: String
    ): CompletableFuture<AdminAddUserToGroupResponse> =
        cognitoAsyncClient.adminAddUserToGroup(
            AdminAddUserToGroupRequest.builder()
                .userPoolId(userPoolId)
                .username(username)
                .groupName(groupName)
                .build()
        )

    /**
     * Adds a user to several groups.
     *
     * @param userPoolId - the cognito user pool that the user and group exist in
     * @param username - the username to add to the group
     * @param groups - the groups to add the user to
     */
    fun addUserToGroups(userPoolId: String, username: String, groups: Set<String>): CompletableFuture<Void> {
        val completableFutures = groups.map { addUserToGroup(userPoolId, username, it) }.toTypedArray()
        return CompletableFuture.allOf(*completableFutures)
    }

    /**
     * Removes a user from a group.
     *
     * @param userPoolId - the cognito user pool that the user and group exist in
     * @param username - the username of the user to remove from the group
     * @param groupName - the group to remove the user from
     */
    fun removeUserFromGroup(
        userPoolId: String,
        username: String,
        groupName: String
    ): CompletableFuture<AdminRemoveUserFromGroupResponse> =
        cognitoAsyncClient.adminRemoveUserFromGroup(
            AdminRemoveUserFromGroupRequest.builder()
                .userPoolId(userPoolId)
                .username(username)
                .groupName(groupName)
                .build()
        )

    /**
     * Removes a user from several groups.
     *
     * @param userPoolId - the cognito user pool that the user and group exist in
     * @param username - the username of the user to remove from the group
     * @param groups - the groups to remove the user from
     */
    fun removeUserFromGroups(userPoolId: String, username: String, groups: Set<String>): CompletableFuture<Void> {
        val completableFutures = groups.map { removeUserFromGroup(userPoolId, username, it) }.toTypedArray()
        return CompletableFuture.allOf(*completableFutures)
    }

    /**
     * Lists the users in a group.
     *
     * @param userPoolId - the cognito user pool to query for the users
     * @param groupName - the group name to query for users
     */
    fun listUsersInGroup(userPoolId: String, groupName: String): CompletableFuture<ListUsersInGroupResponse> =
        cognitoAsyncClient.listUsersInGroup(
            ListUsersInGroupRequest.builder()
                .userPoolId(userPoolId)
                .groupName(groupName)
                .build()
        ).whenComplete { listUsersInGroupResponse, _ ->
            listUsersInGroupResponse?.nextToken()?.also {
                logger.warn("AWS Cognito listUsersInGroup operation returned a nextToken. This might suggest there are more users in the group than we are returning.")
            }
        }

    /**
     * Lists the groups a user belongs to.
     *
     * @param userPoolId - the cognito user pool to query for the user
     * @param username - the username (email address) of the user concerned
     */
    fun listGroupsForUser(userPoolId: String, username: String): CompletableFuture<AdminListGroupsForUserResponse> =
        cognitoAsyncClient.adminListGroupsForUser(
            AdminListGroupsForUserRequest.builder()
                .userPoolId(userPoolId)
                .username(username)
                .build()
        ).whenComplete { groupsForUserResponse, _ ->
            groupsForUserResponse?.nextToken()?.also {
                logger.warn("AWS Cognito adminListGroupsForUser operation returned a nextToken. This might suggest there are more groups in the group than we are returning.")
            }
        }

    private fun userAttributesForNewUser(
        email: String,
        firstName: String?,
        surname: String?,
        phoneNumber: String?
    ): Collection<AttributeType> =
        mutableSetOf(
            AttributeType.builder().name("email").value(email).build()
        ).apply {
            if (firstName != null) {
                add(AttributeType.builder().name("given_name").value(firstName).build())
            }
            if (surname != null) {
                add(AttributeType.builder().name("family_name").value(surname).build())
            }
            if (phoneNumber != null) {
                add(AttributeType.builder().name("phone_number").value(phoneNumber).build())
            }
        }.toSet()
}
