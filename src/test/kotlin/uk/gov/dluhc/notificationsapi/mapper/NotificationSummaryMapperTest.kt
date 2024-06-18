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
import uk.gov.dluhc.notificationsapi.models.CommunicationChannel
import uk.gov.dluhc.notificationsapi.models.TemplateType
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aGssCode
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aLocalDateTime
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aNotificationId
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aRequestor
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aSourceReference
import uk.gov.dluhc.notificationsapi.testsupport.testdata.database.entity.aNotificationSummaryBuilder
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildNotificationSummaryDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.models.aCommunicationsSummaryBuilder
import java.time.OffsetDateTime
import java.time.ZoneOffset
import uk.gov.dluhc.notificationsapi.database.entity.Channel as CommunicationChannelEntity
import uk.gov.dluhc.notificationsapi.database.entity.NotificationType as NotificationTypeEntity
import uk.gov.dluhc.notificationsapi.database.entity.SourceType as SourceTypeEntity
import uk.gov.dluhc.notificationsapi.dto.CommunicationChannel as CommunicationChannelDto
import uk.gov.dluhc.notificationsapi.dto.NotificationType as NotificationTypeDto
import uk.gov.dluhc.notificationsapi.dto.SourceType as SourceTypeDto

@ExtendWith(MockitoExtension::class)
class NotificationSummaryMapperTest {

    @InjectMocks
    private lateinit var mapper: NotificationSummaryMapperImpl

    @Mock
    private lateinit var notificationTypeMapper: NotificationTypeMapper

    @Mock
    private lateinit var communicationChannelMapper: CommunicationChannelMapper

    @Mock
    private lateinit var sourceTypeMapper: SourceTypeMapper

    @Test
    fun `should map NotificationSummary entity to DTO`() {
        // Given
        val id = aNotificationId()
        val gssCode = aGssCode()
        val requestor = aRequestor()
        val sentAt = aLocalDateTime()
        val sourceReference = aSourceReference()

        val entity = aNotificationSummaryBuilder(
            id = id,
            gssCode = gssCode,
            requestor = requestor,
            sourceReference = sourceReference,
            sourceType = SourceTypeEntity.VOTER_CARD,
            type = NotificationTypeEntity.APPLICATION_APPROVED,
            channel = CommunicationChannelEntity.EMAIL,
            sentAt = sentAt,
        )

        given(sourceTypeMapper.fromEntityToDto(any())).willReturn(SourceTypeDto.VOTER_CARD)
        given(notificationTypeMapper.toNotificationTypeDto(any())).willReturn(NotificationTypeDto.APPLICATION_APPROVED)
        given(communicationChannelMapper.fromEntityToDto(any())).willReturn(CommunicationChannelDto.EMAIL)

        val expected = buildNotificationSummaryDto(
            id = id,
            gssCode = gssCode,
            requestor = requestor,
            sourceReference = sourceReference,
            sourceType = SourceTypeDto.VOTER_CARD,
            type = NotificationTypeDto.APPLICATION_APPROVED,
            channel = CommunicationChannelDto.EMAIL,
            sentAt = sentAt,
        )

        // When
        val actual = mapper.toNotificationSummaryDto(entity)

        // Then
        verify(sourceTypeMapper).fromEntityToDto(SourceTypeEntity.VOTER_CARD)
        verify(notificationTypeMapper).toNotificationTypeDto(NotificationTypeEntity.APPLICATION_APPROVED)
        verify(communicationChannelMapper).fromEntityToDto(CommunicationChannelEntity.EMAIL)
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `should map NotificationSummary DTO to CommunicationsSummary API`() {
        // Given
        val id = aNotificationId()
        val gssCode = aGssCode()
        val requestor = aRequestor()
        val sentAt = aLocalDateTime()
        val sourceReference = aSourceReference()

        val dto = buildNotificationSummaryDto(
            id = id,
            gssCode = gssCode,
            requestor = requestor,
            sourceReference = sourceReference,
            sourceType = SourceTypeDto.VOTER_CARD,
            type = NotificationTypeDto.APPLICATION_APPROVED,
            channel = CommunicationChannelDto.EMAIL,
            sentAt = sentAt,
        )

        given(communicationChannelMapper.fromDtoToApi(any())).willReturn(CommunicationChannel.EMAIL)
        given(notificationTypeMapper.fromNotificationTypeDtoToTemplateTypeApi(any())).willReturn(TemplateType.APPLICATION_MINUS_APPROVED)

        val expected = aCommunicationsSummaryBuilder(
            id = id,
            requestor = requestor,
            channel = CommunicationChannel.EMAIL,
            templateType = TemplateType.APPLICATION_MINUS_APPROVED,
            timestamp = OffsetDateTime.of(sentAt, ZoneOffset.UTC),
        )

        // When
        val actual = mapper.toCommunicationsSummaryApi(dto)

        // Then
        verify(communicationChannelMapper).fromDtoToApi(CommunicationChannelDto.EMAIL)
        verify(notificationTypeMapper).fromNotificationTypeDtoToTemplateTypeApi(NotificationTypeDto.APPLICATION_APPROVED)
        assertThat(actual).isEqualTo(expected)
    }
}
