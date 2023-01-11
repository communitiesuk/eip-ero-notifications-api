package uk.gov.dluhc.notificationsapi.service

import ch.qos.logback.classic.Level
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowableOfType
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.given
import org.mockito.kotlin.verify
import software.amazon.awssdk.core.exception.SdkClientException
import uk.gov.dluhc.notificationsapi.database.repository.NotificationRepository
import uk.gov.dluhc.notificationsapi.testsupport.TestLogAppender.Companion.hasLog
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.aRemoveNotificationsDto

@ExtendWith(MockitoExtension::class)
internal class RemoveNotificationsServiceTest {

    @Mock
    private lateinit var notificationRepository: NotificationRepository

    @InjectMocks
    private lateinit var removeNotificationsService: RemoveNotificationsService

    @Test
    fun `should remove application notifications`() {
        // Given
        val removeNotificationsDto = aRemoveNotificationsDto()

        // When
        removeNotificationsService.remove(removeNotificationsDto)

        // Then
        verify(notificationRepository).removeBySourceReference(removeNotificationsDto.sourceReference, removeNotificationsDto.gssCode)
    }

    @Test
    fun `should not remove application notifications given SdkException`() {
        // Given
        val removeNotificationsDto = aRemoveNotificationsDto()
        given(notificationRepository.removeBySourceReference(any(), any())).willThrow(SdkClientException.create("SDK exception"))

        // When
        val ex = catchThrowableOfType(
            { removeNotificationsService.remove(removeNotificationsDto) },
            SdkClientException::class.java
        )

        // Then
        assertThat(ex).isNotNull
            .isInstanceOf(SdkClientException::class.java)
            .hasMessage("SDK exception")
        assertThat(hasLog("Error attempting to remove notifications: software.amazon.awssdk.core.exception.SdkClientException: SDK exception", Level.ERROR)).isTrue
    }
}
