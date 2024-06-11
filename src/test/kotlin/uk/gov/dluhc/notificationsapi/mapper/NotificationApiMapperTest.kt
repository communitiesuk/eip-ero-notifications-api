package uk.gov.dluhc.notificationsapi.mapper

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.given
import org.mockito.kotlin.verify
import uk.gov.dluhc.notificationsapi.models.SentCommunicationResponse
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aGssCode
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aLocalDateTime
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aNotificationId
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aNotificationPersonalisationMap
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aRequestor
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aSourceReference
import uk.gov.dluhc.notificationsapi.testsupport.testdata.anEmailAddress
import uk.gov.dluhc.notificationsapi.testsupport.testdata.database.entity.aNotificationBuilder
import uk.gov.dluhc.notificationsapi.testsupport.testdata.database.entity.aNotifyDetails
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildNotificationDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.aNotifyDetailsDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildNotifyDetailsDto
import uk.gov.dluhc.notificationsapi.database.entity.Channel as NotificationChannelEntity
import uk.gov.dluhc.notificationsapi.database.entity.NotificationType as NotificationTypeEntity
import uk.gov.dluhc.notificationsapi.database.entity.SourceType as SourceTypeEntity
import uk.gov.dluhc.notificationsapi.dto.CommunicationChannel as CommunicationChannelDto
import uk.gov.dluhc.notificationsapi.dto.NotificationType as NotificationTypeDto
import uk.gov.dluhc.notificationsapi.dto.SourceType as SourceTypeDto

@ExtendWith(MockitoExtension::class)
class NotificationApiMapperTest {

    @InjectMocks
    private lateinit var mapper: NotificationApiMapperImpl

    @Mock
    private lateinit var notificationTypeMapper: NotificationTypeMapper

    @Mock
    private lateinit var notificationChannelMapper: CommunicationChannelMapper

    @Mock
    private lateinit var notifyDetailsMapper: NotifyDetailsMapper

    @Mock
    private lateinit var sourceTypeMapper: SourceTypeMapper

    @Test
    fun `should map Notification entity to DTO`() {
        // Given
        val id = aNotificationId()
        val gssCode = aGssCode()
        val requestor = aRequestor()
        val sentAt = aLocalDateTime()
        val sourceReference = aSourceReference()
        val toEmail = anEmailAddress()
        val personalisation = aNotificationPersonalisationMap()
        val notifyDetails = aNotifyDetails()

        val entity = aNotificationBuilder(
            id = id,
            gssCode = gssCode,
            requestor = requestor,
            sourceType = SourceTypeEntity.VOTER_CARD,
            sourceReference = sourceReference,
            toEmail = toEmail,
            type = NotificationTypeEntity.APPLICATION_APPROVED,
            channel = NotificationChannelEntity.EMAIL,
            personalisation = personalisation,
            sentAt = sentAt,
            notifyDetails = notifyDetails,
        )

        val notifyDetailsDto = aNotifyDetailsDto()

        given(sourceTypeMapper.fromEntityToDto(any())).willReturn(SourceTypeDto.VOTER_CARD)
        given(notificationTypeMapper.toNotificationTypeDto(any())).willReturn(NotificationTypeDto.APPLICATION_APPROVED)
        given(notificationChannelMapper.fromEntityToDto(any())).willReturn(CommunicationChannelDto.EMAIL)
        given(notifyDetailsMapper.fromEntityToDto(any())).willReturn(notifyDetailsDto)

        val expected = buildNotificationDto(
            id = id,
            sourceReference = sourceReference,
            gssCode = gssCode,
            sourceType = SourceTypeDto.VOTER_CARD,
            type = NotificationTypeDto.APPLICATION_APPROVED,
            channel = CommunicationChannelDto.EMAIL,
            toEmail = toEmail,
            toPostalAddress = null,
            requestor = requestor,
            sentAt = sentAt,
            personalisation = personalisation,
            notifyDetailsDto = notifyDetailsDto,
        )

        // When
        val actual = mapper.toNotificationDto(entity)

        // Then
        verify(sourceTypeMapper).fromEntityToDto(SourceTypeEntity.VOTER_CARD)
        verify(notificationTypeMapper).toNotificationTypeDto(NotificationTypeEntity.APPLICATION_APPROVED)
        verify(notificationChannelMapper).fromEntityToDto(NotificationChannelEntity.EMAIL)
        verify(notifyDetailsMapper).fromEntityToDto(notifyDetails)
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `should map Notification DTO to SentCommunicationResponse`() {
        // Given
        val subject = "subject"
        val body = "body"

        val notifyDetailsDto = buildNotifyDetailsDto(
            subject = subject,
            body = body,
        )

        val dto = buildNotificationDto(
            notifyDetailsDto = notifyDetailsDto,
        )

        val expected = SentCommunicationResponse(
            subject = subject,
            body = body,
        )

        // When
        val actual = mapper.toSentCommunicationsApi(dto)

        // Then
        assertThat(actual).isEqualTo(expected)
    }
}
