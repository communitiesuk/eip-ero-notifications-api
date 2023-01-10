package uk.gov.dluhc.notificationsapi.service

import ch.qos.logback.classic.Level
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.given
import org.mockito.kotlin.verify
import software.amazon.awssdk.core.exception.SdkClientException
import software.amazon.awssdk.core.exception.SdkServiceException
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
    fun `should not remove application notifications given SdkClientException`() {
        // Given
        val removeNotificationsDto = aRemoveNotificationsDto()
        given(notificationRepository.removeBySourceReference(any(), any())).willThrow(SdkClientException.create("Client exception"))

        // When
        removeNotificationsService.remove(removeNotificationsDto)

        // Then
        assertThat(hasLog("Client error attempting to remove notifications: software.amazon.awssdk.core.exception.SdkClientException: Client exception", Level.ERROR)).isTrue
    }

    @Test
    fun `should not remove application notifications given SdkServiceException`() {
        // Given
        val removeNotificationsDto = aRemoveNotificationsDto()
        given(notificationRepository.removeBySourceReference(any(), any())).willThrow(SdkServiceException.builder().message("Service exception").build())

        // When
        removeNotificationsService.remove(removeNotificationsDto)

        // Then
        assertThat(hasLog("Service error attempting to remove notifications: software.amazon.awssdk.core.exception.SdkServiceException: Service exception", Level.ERROR)).isTrue
    }
}
