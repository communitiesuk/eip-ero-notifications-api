package uk.gov.dluhc.notificationsapi.mapper

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.junit.jupiter.MockitoExtension
import uk.gov.dluhc.notificationsapi.models.SentCommunicationResponse
import uk.gov.dluhc.notificationsapi.testsupport.testdata.database.entity.aNotificationBuilder
import uk.gov.dluhc.notificationsapi.testsupport.testdata.database.entity.buildNotifyDetails

@ExtendWith(MockitoExtension::class)
class NotificationApiMapperTest {

    @InjectMocks
    private lateinit var mapper: NotificationApiMapperImpl

    @Test
    fun `should map Notification DTO to SentCommunicationResponse`() {
        // Given
        val subject = "subject"
        val body = "body"

        val notifyDetails = buildNotifyDetails(
            subject = subject,
            body = body,
        )

        val notification = aNotificationBuilder(
            notifyDetails = notifyDetails,
        )

        val expected = SentCommunicationResponse(
            subject = subject,
            body = body,
        )

        // When
        val actual = mapper.toSentCommunicationsApi(notification)

        // Then
        assertThat(actual).isEqualTo(expected)
    }
}
