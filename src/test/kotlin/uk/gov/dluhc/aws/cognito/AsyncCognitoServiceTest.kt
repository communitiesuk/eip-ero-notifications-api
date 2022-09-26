package uk.gov.dluhc.aws.cognito

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.any
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.never
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
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
import software.amazon.awssdk.services.cognitoidentityprovider.model.CreateGroupResponse
import software.amazon.awssdk.services.cognitoidentityprovider.model.DeliveryMediumType
import software.amazon.awssdk.services.cognitoidentityprovider.model.GetGroupRequest
import software.amazon.awssdk.services.cognitoidentityprovider.model.GetGroupResponse
import software.amazon.awssdk.services.cognitoidentityprovider.model.ListUsersInGroupRequest
import software.amazon.awssdk.services.cognitoidentityprovider.model.ListUsersInGroupResponse
import software.amazon.awssdk.services.cognitoidentityprovider.model.MessageActionType
import software.amazon.awssdk.services.cognitoidentityprovider.model.ResourceNotFoundException
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionException
import java.util.function.Consumer

@ExtendWith(MockitoExtension::class)
internal class AsyncCognitoServiceTest {

    @Mock
    private lateinit var cognitoAsyncClient: CognitoIdentityProviderAsyncClient

    @InjectMocks
    private lateinit var cognitoService: AsyncCognitoService

    @Nested
    inner class CreateUser {
        @Test
        fun `should create user with all attributes`() {
            // Given
            val userPoolId = "user-pool"
            val email = "new-user@avon.gov.uk"
            val firstName = "Fred"
            val surname = "Bloggs"
            val phoneNumber = "+44 01234 567890"
            val expectedResponse = AdminCreateUserResponse.builder().build()
            given(cognitoAsyncClient.adminCreateUser(any<AdminCreateUserRequest>()))
                .willReturn(CompletableFuture.completedFuture(expectedResponse))

            // When
            val response = cognitoService.createUser(userPoolId, email, firstName, surname, phoneNumber)

            // Then
            assertThat(response.get()).isEqualTo(expectedResponse)
            verify(cognitoAsyncClient).adminCreateUser(
                AdminCreateUserRequest.builder()
                    .userPoolId(userPoolId)
                    .username(email)
                    .userAttributes(expectedAttributes(email, firstName, surname, phoneNumber))
                    .desiredDeliveryMediums(DeliveryMediumType.EMAIL)
                    .build()
            )
        }

        @Test
        fun `should create user given a message action`() {
            // Given
            val userPoolId = "user-pool"
            val email = "new-user@avon.gov.uk"
            val messageAction = MessageActionType.SUPPRESS
            val expectedResponse = AdminCreateUserResponse.builder().build()
            given(cognitoAsyncClient.adminCreateUser(any<AdminCreateUserRequest>()))
                .willReturn(CompletableFuture.completedFuture(expectedResponse))

            // When
            val response = cognitoService.createUser(userPoolId, email, messageAction = messageAction)

            // Then
            assertThat(response.get()).isEqualTo(expectedResponse)
            verify(cognitoAsyncClient).adminCreateUser(
                AdminCreateUserRequest.builder()
                    .userPoolId(userPoolId)
                    .username(email)
                    .userAttributes(expectedAttributes(email))
                    .messageAction(messageAction)
                    .desiredDeliveryMediums(setOf(DeliveryMediumType.EMAIL))
                    .build()
            )
        }

        @Test
        fun `should create user given delivery mediums`() {
            // Given
            val userPoolId = "user-pool"
            val email = "new-user@avon.gov.uk"
            val expectedResponse = AdminCreateUserResponse.builder().build()
            given(cognitoAsyncClient.adminCreateUser(any<AdminCreateUserRequest>()))
                .willReturn(CompletableFuture.completedFuture(expectedResponse))

            val deliveryMediums = setOf(DeliveryMediumType.SMS, DeliveryMediumType.EMAIL)

            // When
            val response = cognitoService.createUser(userPoolId, email, desiredDeliveryMediums = deliveryMediums)

            // Then
            assertThat(response.get()).isEqualTo(expectedResponse)
            verify(cognitoAsyncClient).adminCreateUser(
                AdminCreateUserRequest.builder()
                    .userPoolId(userPoolId)
                    .username(email)
                    .userAttributes(expectedAttributes(email))
                    .desiredDeliveryMediums(deliveryMediums)
                    .build()
            )
        }
    }

    @Nested
    inner class GetUser {
        @Test
        fun `should return user given userPoolId and username`() {
            // Given
            val userPoolId = "user-pool"
            val username = "new-user@avon.gov.uk"
            val expectedResponse = AdminGetUserResponse.builder().build()
            given(cognitoAsyncClient.adminGetUser(any<AdminGetUserRequest>())).willReturn(
                CompletableFuture.completedFuture(expectedResponse)
            )

            // When
            val actual = cognitoService.getUser(userPoolId, username)

            // Then
            verify(cognitoAsyncClient).adminGetUser(
                AdminGetUserRequest.builder()
                    .userPoolId(userPoolId)
                    .username(username)
                    .build()
            )
            assertThat(actual.get()).isSameAs(expectedResponse)
        }
    }

    @Nested
    inner class SetUserPassword {
        @Test
        fun `should set user password`() {
            // Given
            val userPoolId = "user-pool"
            val username = "a-user@avon.gov.uk"
            val password = "new-password"
            val expectedResponse = AdminSetUserPasswordResponse.builder().build()
            given(cognitoAsyncClient.adminSetUserPassword(any<AdminSetUserPasswordRequest>()))
                .willReturn(CompletableFuture.completedFuture(expectedResponse))

            // When
            val response = cognitoService.setUserPassword(userPoolId, username, password)

            // Then
            assertThat(response.get()).isEqualTo(expectedResponse)
            verify(cognitoAsyncClient).adminSetUserPassword(
                AdminSetUserPasswordRequest.builder()
                    .userPoolId(userPoolId)
                    .username(username)
                    .password(password)
                    .permanent(false)
                    .build()
            )
        }

        @Test
        fun `should set user password given user should not change password at next login`() {
            // Given
            val userPoolId = "user-pool"
            val username = "a-user@avon.gov.uk"
            val password = "new-password"
            val changePasswordAtLogin = false
            val expectedResponse = AdminSetUserPasswordResponse.builder().build()
            given(cognitoAsyncClient.adminSetUserPassword(any<AdminSetUserPasswordRequest>()))
                .willReturn(CompletableFuture.completedFuture(expectedResponse))

            // When
            val response = cognitoService.setUserPassword(userPoolId, username, password, changePasswordAtLogin)

            // Then
            assertThat(response.get()).isEqualTo(expectedResponse)
            verify(cognitoAsyncClient).adminSetUserPassword(
                AdminSetUserPasswordRequest.builder()
                    .userPoolId(userPoolId)
                    .username(username)
                    .password(password)
                    .permanent(true)
                    .build()
            )
        }
    }

    @Nested
    inner class CreateGroup {
        @Test
        fun `should create group given group does not already exist`() {
            // Given
            val userPoolId = "user-pool"
            val groupName = "new-group-1"
            val getGroupResponse = CompletionException(ResourceNotFoundException.builder().build())
            given(cognitoAsyncClient.getGroup(any<GetGroupRequest>()))
                .willReturn(CompletableFuture.failedFuture(getGroupResponse))
            given(cognitoAsyncClient.createGroup(any<CreateGroupRequest>()))
                .willReturn(CompletableFuture.completedFuture(CreateGroupResponse.builder().build()))

            // When
            val response = cognitoService.createGroup(userPoolId, groupName)

            // Then
            assertThat(response.get()).isNull()
            verify(cognitoAsyncClient).createGroup(
                CreateGroupRequest.builder()
                    .userPoolId(userPoolId)
                    .groupName(groupName)
                    .build()
            )
        }

        @Test
        fun `should create group given group already exists`() {
            // Given
            val userPoolId = "user-pool"
            val groupName = "new-group-1"
            val getGroupResponse = GetGroupResponse.builder().build()
            given(cognitoAsyncClient.getGroup(any<GetGroupRequest>()))
                .willReturn(CompletableFuture.completedFuture(getGroupResponse))

            // When
            val response = cognitoService.createGroup(userPoolId, groupName)

            // Then
            assertThat(response.get()).isNull()
            verify(cognitoAsyncClient, never()).createGroup(any<Consumer<CreateGroupRequest.Builder>>())
        }

        @Test
        fun `should create groups`() {
            // Given
            val userPoolId = "user-pool"
            val groupNames = setOf("new-group-1", "new-group-2")
            val getGroupResponse = CompletionException(ResourceNotFoundException.builder().build())
            given(cognitoAsyncClient.getGroup(any<GetGroupRequest>()))
                .willReturn(CompletableFuture.failedFuture(getGroupResponse))
            given(cognitoAsyncClient.createGroup(any<CreateGroupRequest>()))
                .willReturn(CompletableFuture.completedFuture(CreateGroupResponse.builder().build()))

            // When
            val response = cognitoService.createGroups(userPoolId, groupNames)

            // Then
            assertThat(response.get()).isNull()
            verify(cognitoAsyncClient, times(2)).createGroup(any<CreateGroupRequest>())
        }
    }

    @Nested
    inner class AddUserToGroup {
        @Test
        fun `should add user to group`() {
            // Given
            val userPoolId = "user-pool"
            val username = "a-user@avon.gov.uk"
            val groupName = "group-1"
            val expectedResponse = AdminAddUserToGroupResponse.builder().build()
            given(cognitoAsyncClient.adminAddUserToGroup(any<AdminAddUserToGroupRequest>()))
                .willReturn(CompletableFuture.completedFuture(expectedResponse))

            // When
            val response = cognitoService.addUserToGroup(userPoolId, username, groupName)

            // Then
            assertThat(response.get()).isEqualTo(expectedResponse)
            verify(cognitoAsyncClient).adminAddUserToGroup(
                AdminAddUserToGroupRequest.builder()
                    .userPoolId(userPoolId)
                    .username(username)
                    .groupName(groupName)
                    .build()
            )
        }

        @Test
        fun `should add user to groups`() {
            // Given
            val userPoolId = "user-pool"
            val username = "a-user@avon.gov.uk"
            val groupNames = setOf("group-1", "group-2")

            val expectedResponse = AdminAddUserToGroupResponse.builder().build()
            given(cognitoAsyncClient.adminAddUserToGroup(any<AdminAddUserToGroupRequest>()))
                .willReturn(CompletableFuture.completedFuture(expectedResponse))

            // When
            val response = cognitoService.addUserToGroups(userPoolId, username, groupNames)

            // Then
            assertThat(response.get()).isNull()
            verify(cognitoAsyncClient, times(2)).adminAddUserToGroup(any<AdminAddUserToGroupRequest>())
        }
    }

    @Nested
    inner class ListUsersInGroup {
        @Test
        fun `should list users in group`() {
            // Given
            val userPoolId = "user-pool"
            val groupName = "group-1"

            val expectedResponse = ListUsersInGroupResponse.builder().build()
            given(cognitoAsyncClient.listUsersInGroup(any<ListUsersInGroupRequest>()))
                .willReturn(CompletableFuture.completedFuture(expectedResponse))

            // When
            val response = cognitoService.listUsersInGroup(userPoolId, groupName)

            // Then
            assertThat(response.get()).isEqualTo(expectedResponse)
            verify(cognitoAsyncClient).listUsersInGroup(
                ListUsersInGroupRequest.builder()
                    .userPoolId(userPoolId)
                    .groupName(groupName)
                    .nextToken(null)
                    .build()
            )
        }
    }

    @Nested
    inner class RemoveUserFromGroup {
        @Test
        fun `should remove user from group`() {
            // Given
            val userPoolId = "user-pool"
            val username = "existing-user@gov.uk"
            val groupName = "new-group"
            val expectedResponse = AdminRemoveUserFromGroupResponse.builder().build()
            given(cognitoAsyncClient.adminRemoveUserFromGroup(any<AdminRemoveUserFromGroupRequest>()))
                .willReturn(CompletableFuture.completedFuture(expectedResponse))

            // When
            val response = cognitoService.removeUserFromGroup(userPoolId, username, groupName)

            // Then
            assertThat(response.get()).isEqualTo(expectedResponse)
            verify(cognitoAsyncClient).adminRemoveUserFromGroup(
                AdminRemoveUserFromGroupRequest.builder()
                    .userPoolId(userPoolId)
                    .username(username)
                    .groupName(groupName)
                    .build()
            )
        }

        @Test
        fun `should remove user from groups`() {
            // Given
            val userPoolId = "user-pool"
            val username = "existing-user@gov.uk"
            val groupNames = setOf("new-group-1", "new-group-2")
            val expectedResponse = AdminRemoveUserFromGroupResponse.builder().build()
            given(cognitoAsyncClient.adminRemoveUserFromGroup(any<AdminRemoveUserFromGroupRequest>()))
                .willReturn(CompletableFuture.completedFuture(expectedResponse))

            // When
            val response = cognitoService.removeUserFromGroups(userPoolId, username, groupNames)

            // Then
            assertThat(response.get()).isNull()
            verify(cognitoAsyncClient, times(2)).adminRemoveUserFromGroup(any<AdminRemoveUserFromGroupRequest>())
        }

        @Test
        fun `should not remove user from groups given no groups supplied`() {
            // Given
            val userPoolId = "user-pool"
            val username = "existing-user@gov.uk"
            val groupNames = emptySet<String>()

            // When
            val response = cognitoService.removeUserFromGroups(userPoolId, username, groupNames)

            // Then
            assertThat(response.get()).isNull()
            verify(cognitoAsyncClient, never()).adminRemoveUserFromGroup(any<AdminRemoveUserFromGroupRequest>())
        }
    }

    @Nested
    inner class ListGroupsForUser {
        @Test
        fun `should list groups for user`() {
            // Given
            val userPoolId = "user-pool"
            val username = "existing-user@gov.uk"
            val expectedResponse = AdminListGroupsForUserResponse.builder().build()
            given(cognitoAsyncClient.adminListGroupsForUser(any<AdminListGroupsForUserRequest>()))
                .willReturn(CompletableFuture.completedFuture(expectedResponse))

            // When
            val response = cognitoService.listGroupsForUser(userPoolId, username)

            // Then
            assertThat(response.get()).isEqualTo(expectedResponse)
            verify(cognitoAsyncClient).adminListGroupsForUser(
                AdminListGroupsForUserRequest.builder()
                    .userPoolId(userPoolId)
                    .username(username)
                    .nextToken(null)
                    .build()
            )
        }
    }

    private fun expectedAttributes(
        email: String,
        firstName: String? = null,
        surname: String? = null,
        phoneNumber: String? = null
    ): MutableSet<AttributeType> {
        val attributes = mutableSetOf(
            AttributeType.builder().name("email").value(email).build()
        )
        if (firstName != null) {
            attributes.add(AttributeType.builder().name("given_name").value(firstName).build())
        }
        if (surname != null) {
            attributes.add(AttributeType.builder().name("family_name").value(surname).build())
        }
        if (phoneNumber != null) {
            attributes.add(AttributeType.builder().name("phone_number").value(phoneNumber).build())
        }
        return attributes
    }
}
