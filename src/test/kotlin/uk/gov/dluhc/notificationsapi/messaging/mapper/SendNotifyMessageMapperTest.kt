package uk.gov.dluhc.notificationsapi.messaging.mapper

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import uk.gov.dluhc.notificationsapi.dto.NotificationChannel
import uk.gov.dluhc.notificationsapi.dto.NotificationType
import uk.gov.dluhc.notificationsapi.dto.SourceType
import uk.gov.dluhc.notificationsapi.messaging.models.MessageAddress
import uk.gov.dluhc.notificationsapi.messaging.models.MessageType
import uk.gov.dluhc.notificationsapi.messaging.models.SendNotifyMessage
import uk.gov.dluhc.notificationsapi.messaging.models.TemplatePersonalisationInner
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aGssCode
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aRequestor
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aSourceReference
import uk.gov.dluhc.notificationsapi.testsupport.testdata.anEmailAddress
import uk.gov.dluhc.notificationsapi.messaging.models.Channel as SqsChannel
import uk.gov.dluhc.notificationsapi.messaging.models.SourceType as SqsSourceType

internal class SendNotifyMessageMapperTest {
    private val mapper = SendNotifyMessageMapperImpl()

    @ParameterizedTest
    @CsvSource(
        value = [
            "VOTER_MINUS_CARD, VOTER_CARD",
        ]
    )
    fun `should map Source Type`(sourceType: SqsSourceType, expected: SourceType) {
        // Given

        // When
        val actual = mapper.map(sourceType)

        // Then
        assertThat(actual).isEqualTo(expected)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "APPLICATION_MINUS_RECEIVED, APPLICATION_RECEIVED",
            "APPLICATION_MINUS_APPROVED, APPLICATION_APPROVED",
            "APPLICATION_MINUS_REJECTED, APPLICATION_REJECTED",
            "PHOTO_MINUS_RESUBMISSION, PHOTO_RESUBMISSION",
        ]
    )
    fun `should map Message Type`(messageType: MessageType, expected: NotificationType) {
        // Given

        // When
        val actual = mapper.map(messageType)

        // Then
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `should create email notification`() {
        // Given
        val placeholders = listOf(
            TemplatePersonalisationInner(name = "subject", value = "test subject"),
            TemplatePersonalisationInner(name = "applicant_name", value = "John"),
            TemplatePersonalisationInner(name = "custom_title", value = "Resubmitting photo"),
            TemplatePersonalisationInner(name = "date", value = "15/Oct/2022")
        )
        val expected = mapOf(
            "subject" to "test subject",
            "applicant_name" to "John",
            "custom_title" to "Resubmitting photo",
            "date" to "15/Oct/2022"
        )

        // When
        val actual = mapper.map(placeholders)

        // Then
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `should map SQS SendNotifyMessage to SendNotificationRequestDto`() {
        // Given
        val gssCode = aGssCode()
        val requestor = aRequestor()
        val sourceReference = aSourceReference()
        val emailAddress = anEmailAddress()
        val expectedChannel = NotificationChannel.EMAIL
        val expectedSourceType = SourceType.VOTER_CARD
        val expectedNotificationType = NotificationType.APPLICATION_REJECTED
        val expectedPersonalisation = mapOf("applicant_name" to "John")

        val request = SendNotifyMessage(
            channel = SqsChannel.EMAIL,
            sourceType = SqsSourceType.VOTER_MINUS_CARD,
            sourceReference = sourceReference,
            gssCode = gssCode,
            requestor = requestor,
            messageType = MessageType.APPLICATION_MINUS_REJECTED,
            personalisation = listOf(TemplatePersonalisationInner(name = "applicant_name", value = "John")),
            toAddress = MessageAddress(emailAddress = emailAddress),
        )

        // When
        val notification = mapper.toSendNotificationRequestDto(request)

        // Then
        assertThat(notification.channel).isEqualTo(expectedChannel)
        assertThat(notification.sourceType).isEqualTo(expectedSourceType)
        assertThat(notification.sourceReference).isEqualTo(sourceReference)
        assertThat(notification.gssCode).isEqualTo(gssCode)
        assertThat(notification.requestor).isEqualTo(requestor)
        assertThat(notification.notificationType).isEqualTo(expectedNotificationType)
        assertThat(notification.personalisation).isEqualTo(expectedPersonalisation)
        assertThat(notification.emailAddress).isEqualTo(emailAddress)
    }
}
