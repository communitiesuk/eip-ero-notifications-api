package uk.gov.dluhc.notificationsapi.mapper

import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.within
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.given
import uk.gov.dluhc.notificationsapi.database.entity.CommunicationConfirmation
import uk.gov.dluhc.notificationsapi.dto.CreateOfflineCommunicationConfirmationDto
import uk.gov.dluhc.notificationsapi.dto.OfflineCommunicationChannelDto
import uk.gov.dluhc.notificationsapi.dto.OfflineCommunicationReasonDto
import uk.gov.dluhc.notificationsapi.models.CreateOfflineCommunicationConfirmationRequest
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aGssCode
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aLocalDateTime
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aSourceReference
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aValidRandomEroId
import uk.gov.dluhc.notificationsapi.testsupport.testdata.anEmailAddress
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import uk.gov.dluhc.notificationsapi.database.entity.CommunicationConfirmationChannel as OfflineCommunicationChannelEntity
import uk.gov.dluhc.notificationsapi.database.entity.CommunicationConfirmationReason as OfflineCommunicationReasonEntity
import uk.gov.dluhc.notificationsapi.database.entity.SourceType as SourceTypeEntity
import uk.gov.dluhc.notificationsapi.dto.SourceType as SourceTypeDto
import uk.gov.dluhc.notificationsapi.models.OfflineCommunicationChannel as OfflineCommunicationChannelApi
import uk.gov.dluhc.notificationsapi.models.OfflineCommunicationReason as OfflineCommunicationReasonApi

@ExtendWith(MockitoExtension::class)
class CommunicationConfirmationMapperTest {
    @InjectMocks
    private lateinit var mapper: CommunicationConfirmationMapperImpl

    @Mock
    private lateinit var sourceTypeMapper: SourceTypeMapper

    @Mock
    private lateinit var communicationConfirmationReasonMapper: CommunicationConfirmationReasonMapper

    @Mock
    private lateinit var communicationConfirmationChannelMapper: CommunicationConfirmationChannelMapper

    @Test
    fun `should map from dto to entity`() {
        // Given
        val eroId: String = aValidRandomEroId()
        val sourceReference: String = aSourceReference()
        val sourceType: SourceTypeDto = SourceTypeDto.ANONYMOUS_ELECTOR_DOCUMENT
        val requestor: String = anEmailAddress()
        val sentAt = aLocalDateTime()
        val gssCode: String = aGssCode()
        val reason: OfflineCommunicationReasonDto = OfflineCommunicationReasonDto.APPLICATION_REJECTED
        val channel: OfflineCommunicationChannelDto = OfflineCommunicationChannelDto.LETTER

        val dto = CreateOfflineCommunicationConfirmationDto(
            eroId = eroId,
            gssCode = gssCode,
            sourceReference = sourceReference,
            sourceType = sourceType,
            reason = reason,
            channel = channel,
            requestor = requestor,
            sentAt = sentAt,
        )

        given(sourceTypeMapper.fromDtoToEntity(any()))
            .willReturn(SourceTypeEntity.ANONYMOUS_ELECTOR_DOCUMENT)
        given(communicationConfirmationReasonMapper.fromDtoToEntity(any()))
            .willReturn(OfflineCommunicationReasonEntity.APPLICATION_REJECTED)
        given(communicationConfirmationChannelMapper.fromDtoToEntity(any()))
            .willReturn(OfflineCommunicationChannelEntity.LETTER)

        // When
        val actualEntity: CommunicationConfirmation = mapper.fromDtoToEntity(dto)

        // Then
        Assertions.assertThat(actualEntity.id).isNotNull
        Assertions.assertThat(actualEntity.gssCode).isEqualTo(gssCode)
        Assertions.assertThat(actualEntity.sourceReference).isEqualTo(sourceReference)
        Assertions.assertThat(actualEntity.sourceType).isEqualTo(SourceTypeEntity.ANONYMOUS_ELECTOR_DOCUMENT)
        Assertions.assertThat(actualEntity.reason).isEqualTo(OfflineCommunicationReasonEntity.APPLICATION_REJECTED)
        Assertions.assertThat(actualEntity.channel).isEqualTo(OfflineCommunicationChannelEntity.LETTER)
        Assertions.assertThat(actualEntity.requestor).isEqualTo(requestor)
        Assertions.assertThat(actualEntity.sentAt).isEqualTo(sentAt)
    }

    @Test
    fun `should map from api to dto`() {
        // Given
        val eroId: String = aValidRandomEroId()
        val sourceReference: String = aSourceReference()
        val sourceType: SourceTypeDto = SourceTypeDto.ANONYMOUS_ELECTOR_DOCUMENT
        val requestor: String = anEmailAddress()
        val sentAt = LocalDateTime.now()

        val gssCode: String = aGssCode()
        val reason = OfflineCommunicationReasonApi.APPLICATION_MINUS_REJECTED
        val channel = OfflineCommunicationChannelApi.LETTER

        val request = CreateOfflineCommunicationConfirmationRequest(
            gssCode = gssCode,
            reason = reason,
            channel = channel,
        )

        given(communicationConfirmationReasonMapper.fromApiToDto(any()))
            .willReturn(OfflineCommunicationReasonDto.APPLICATION_REJECTED)
        given(communicationConfirmationChannelMapper.fromApiToDto(any()))
            .willReturn(OfflineCommunicationChannelDto.LETTER)

        // When
        val actualDto: CreateOfflineCommunicationConfirmationDto =
            mapper.fromApiToDto(eroId, sourceReference, sourceType, requestor, sentAt, request)

        // Then
        Assertions.assertThat(actualDto.eroId).isEqualTo(eroId)
        Assertions.assertThat(actualDto.sourceReference).isEqualTo(sourceReference)
        Assertions.assertThat(actualDto.sourceType).isEqualTo(SourceTypeDto.ANONYMOUS_ELECTOR_DOCUMENT)
        Assertions.assertThat(actualDto.gssCode).isEqualTo(gssCode)
        Assertions.assertThat(actualDto.reason).isEqualTo(OfflineCommunicationReasonDto.APPLICATION_REJECTED)
        Assertions.assertThat(actualDto.channel).isEqualTo(OfflineCommunicationChannelDto.LETTER)
        Assertions.assertThat(actualDto.requestor).isEqualTo(requestor)
        Assertions.assertThat(actualDto.sentAt).isCloseToUtcNow(within(1, ChronoUnit.SECONDS))
    }
}
