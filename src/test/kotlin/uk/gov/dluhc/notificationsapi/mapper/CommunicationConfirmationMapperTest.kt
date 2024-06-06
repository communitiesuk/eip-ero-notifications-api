package uk.gov.dluhc.notificationsapi.mapper

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Spy
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.given
import org.mockito.kotlin.verify
import uk.gov.dluhc.notificationsapi.database.entity.CommunicationConfirmation
import uk.gov.dluhc.notificationsapi.database.entity.CommunicationConfirmationChannel
import uk.gov.dluhc.notificationsapi.database.entity.CommunicationConfirmationReason
import uk.gov.dluhc.notificationsapi.database.entity.SourceType
import uk.gov.dluhc.notificationsapi.dto.CommunicationConfirmationChannelDto
import uk.gov.dluhc.notificationsapi.dto.CommunicationConfirmationDto
import uk.gov.dluhc.notificationsapi.dto.CommunicationConfirmationReasonDto
import uk.gov.dluhc.notificationsapi.models.OfflineCommunicationChannel
import uk.gov.dluhc.notificationsapi.models.OfflineCommunicationReason
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aGssCode
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aLocalDateTime
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aSourceReference
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aValidRandomEroId
import uk.gov.dluhc.notificationsapi.testsupport.testdata.anEmailAddress
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.aCommunicationConfirmationDtoBuilder
import uk.gov.dluhc.notificationsapi.testsupport.testdata.models.aCommunicationConfirmationHistoryEntryBuilder
import uk.gov.dluhc.notificationsapi.testsupport.testdata.models.aCreateOfflineCommunicationConfirmationRequestBuilder
import java.time.Clock
import java.time.Instant
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID
import uk.gov.dluhc.notificationsapi.database.entity.CommunicationConfirmationChannel as OfflineCommunicationChannelEntity
import uk.gov.dluhc.notificationsapi.database.entity.CommunicationConfirmationReason as OfflineCommunicationReasonEntity
import uk.gov.dluhc.notificationsapi.database.entity.SourceType as SourceTypeEntity
import uk.gov.dluhc.notificationsapi.dto.SourceType as SourceTypeDto
import uk.gov.dluhc.notificationsapi.models.OfflineCommunicationChannel as OfflineCommunicationChannelApi
import uk.gov.dluhc.notificationsapi.models.OfflineCommunicationReason as OfflineCommunicationReasonApi

@ExtendWith(MockitoExtension::class)
class CommunicationConfirmationMapperTest {
    companion object {
        private val FIXED_TIME = Instant.parse("2022-10-18T11:22:32.123Z")
        private val FIXED_CLOCK = Clock.fixed(FIXED_TIME, ZoneOffset.UTC)
    }

    @InjectMocks
    private lateinit var mapper: CommunicationConfirmationMapperImpl

    @Mock
    private lateinit var sourceTypeMapper: SourceTypeMapper

    @Mock
    private lateinit var communicationConfirmationReasonMapper: CommunicationConfirmationReasonMapper

    @Mock
    private lateinit var communicationConfirmationChannelMapper: CommunicationConfirmationChannelMapper

    @Mock
    private lateinit var dateTimeMapper: DateTimeMapper

    @Spy
    private val clock: Clock = FIXED_CLOCK

    @Test
    fun `should map from api to dto`() {
        // Given
        val eroId: String = aValidRandomEroId()
        val sourceReference: String = aSourceReference()
        val requestor: String = anEmailAddress()
        val gssCode: String = aGssCode()
        val reason = OfflineCommunicationReasonApi.PHOTO_MINUS_REJECTED
        val channel = OfflineCommunicationChannelApi.LETTER
        val fixedLocalDateTime = LocalDateTime.now(FIXED_CLOCK)

        val request = aCreateOfflineCommunicationConfirmationRequestBuilder(
            gssCode = gssCode,
            reason = reason,
            channel = channel,
        )

        val expectedDto = aCommunicationConfirmationDtoBuilder(
            id = null,
            eroId = eroId,
            sourceReference = sourceReference,
            sourceType = SourceTypeDto.ANONYMOUS_ELECTOR_DOCUMENT,
            requestor = requestor,
            gssCode = gssCode,
            reason = CommunicationConfirmationReasonDto.PHOTO_REJECTED,
            channel = CommunicationConfirmationChannelDto.LETTER,
            sentAt = fixedLocalDateTime,
        )

        given(communicationConfirmationReasonMapper.fromApiToDto(any()))
            .willReturn(CommunicationConfirmationReasonDto.PHOTO_REJECTED)
        given(communicationConfirmationChannelMapper.fromApiToDto(any()))
            .willReturn(CommunicationConfirmationChannelDto.LETTER)

        // When
        val actualDto: CommunicationConfirmationDto = mapper.fromApiToDto(eroId, sourceReference, requestor, request)

        // Then
        assertThat(actualDto).isEqualTo(expectedDto)
        verify(communicationConfirmationReasonMapper).fromApiToDto(reason)
        verify(communicationConfirmationChannelMapper).fromApiToDto(channel)
    }

    @Test
    fun `should map from dtos to apis`() {
        // Given
        val id = UUID.randomUUID()
        val eroId: String = aValidRandomEroId()
        val sourceReference: String = aSourceReference()
        val requestor: String = anEmailAddress()
        val gssCode: String = aGssCode()
        val reason = CommunicationConfirmationReasonDto.PHOTO_REJECTED
        val channel = CommunicationConfirmationChannelDto.EMAIL

        val oneMinAgoLocal = LocalDateTime.now().minusMinutes(1)
        val oneMinAgoOffset = OffsetDateTime.of(oneMinAgoLocal, ZoneOffset.UTC)

        val dto1 = aCommunicationConfirmationDtoBuilder(
            id = id,
            eroId = eroId,
            sourceReference = sourceReference,
            sourceType = SourceTypeDto.ANONYMOUS_ELECTOR_DOCUMENT,
            requestor = requestor,
            gssCode = gssCode,
            reason = reason,
            channel = channel,
            sentAt = oneMinAgoLocal,
        )

        val expectedApi = listOf(
            aCommunicationConfirmationHistoryEntryBuilder(
                id = id,
                gssCode = gssCode,
                reason = OfflineCommunicationReason.PHOTO_MINUS_REJECTED,
                channel = OfflineCommunicationChannel.EMAIL,
                requestor = requestor,
                timestamp = oneMinAgoOffset,
            ),
        )

        given(communicationConfirmationReasonMapper.fromDtoToApi(CommunicationConfirmationReasonDto.PHOTO_REJECTED))
            .willReturn(OfflineCommunicationReasonApi.PHOTO_MINUS_REJECTED)
        given(communicationConfirmationChannelMapper.fromDtoToApi(CommunicationConfirmationChannelDto.EMAIL))
            .willReturn(OfflineCommunicationChannelApi.EMAIL)
        given(dateTimeMapper.toUtcOffset(oneMinAgoLocal)).willReturn(oneMinAgoOffset)

        // When
        val actualApiEntry = mapper.fromDtosToApis(listOf(dto1))

        // Then
        assertThat(actualApiEntry).isEqualTo(expectedApi)
        verify(communicationConfirmationReasonMapper).fromDtoToApi(reason)
        verify(communicationConfirmationChannelMapper).fromDtoToApi(channel)
        verify(dateTimeMapper).toUtcOffset(oneMinAgoLocal)
    }

    @Test
    fun `should map from dto to entity`() {
        // Given
        val id = UUID.randomUUID()
        val eroId: String = aValidRandomEroId()
        val sourceReference: String = aSourceReference()
        val sourceType: SourceTypeDto = SourceTypeDto.ANONYMOUS_ELECTOR_DOCUMENT
        val requestor: String = anEmailAddress()
        val sentAt = aLocalDateTime()
        val gssCode: String = aGssCode()
        val reason: CommunicationConfirmationReasonDto = CommunicationConfirmationReasonDto.APPLICATION_REJECTED
        val channel: CommunicationConfirmationChannelDto = CommunicationConfirmationChannelDto.LETTER

        val dto = CommunicationConfirmationDto(
            eroId = eroId,
            gssCode = gssCode,
            sourceReference = sourceReference,
            sourceType = sourceType,
            reason = reason,
            channel = channel,
            requestor = requestor,
            sentAt = sentAt,
        )

        val expectedEntity = CommunicationConfirmation(
            id = id,
            gssCode = gssCode,
            sourceReference = sourceReference,
            sourceType = SourceType.ANONYMOUS_ELECTOR_DOCUMENT,
            reason = CommunicationConfirmationReason.APPLICATION_REJECTED,
            channel = CommunicationConfirmationChannel.LETTER,
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
        val actualEntity = Mockito.mockStatic(UUID::class.java).use { mockedUuid ->
            mockedUuid.`when`<UUID> { UUID.randomUUID() }.thenReturn(id)
            mapper.fromDtoToEntity(dto)
        }

        // Then
        assertThat(actualEntity).isEqualTo(expectedEntity)
        verify(sourceTypeMapper).fromDtoToEntity(sourceType)
        verify(communicationConfirmationReasonMapper).fromDtoToEntity(reason)
        verify(communicationConfirmationChannelMapper).fromDtoToEntity(channel)
    }

    @Test
    fun `should map from entities to dtos`() {
        // Given
        val id: UUID = UUID.randomUUID()
        val sourceReference: String = aSourceReference()
        val gssCode: String = aGssCode()
        val sourceType: SourceType = SourceType.ANONYMOUS_ELECTOR_DOCUMENT
        val reason: CommunicationConfirmationReason = CommunicationConfirmationReason.APPLICATION_REJECTED
        val channel: CommunicationConfirmationChannel = CommunicationConfirmationChannel.LETTER
        val requestor: String = anEmailAddress()
        val sentAt = aLocalDateTime()

        val entity = CommunicationConfirmation(
            id = id,
            gssCode = gssCode,
            sourceReference = sourceReference,
            sourceType = sourceType,
            reason = reason,
            channel = channel,
            requestor = requestor,
            sentAt = sentAt,
        )

        val expectedDto = listOf(
            CommunicationConfirmationDto(
                id = id,
                sourceReference = sourceReference,
                gssCode = gssCode,
                sourceType = SourceTypeDto.ANONYMOUS_ELECTOR_DOCUMENT,
                reason = CommunicationConfirmationReasonDto.APPLICATION_REJECTED,
                channel = CommunicationConfirmationChannelDto.LETTER,
                requestor = requestor,
                sentAt = sentAt,
            ),
        )

        given(sourceTypeMapper.fromEntityToDto(any()))
            .willReturn(SourceTypeDto.ANONYMOUS_ELECTOR_DOCUMENT)
        given(communicationConfirmationReasonMapper.fromEntityToDto(any()))
            .willReturn(CommunicationConfirmationReasonDto.APPLICATION_REJECTED)
        given(communicationConfirmationChannelMapper.fromEntityToDto(any()))
            .willReturn(CommunicationConfirmationChannelDto.LETTER)

        // When
        val actualDto = mapper.fromEntitiesToDtos(listOf(entity))

        // Then
        assertThat(actualDto).isEqualTo(expectedDto)
        verify(sourceTypeMapper).fromEntityToDto(SourceType.ANONYMOUS_ELECTOR_DOCUMENT)
        verify(communicationConfirmationReasonMapper).fromEntityToDto(CommunicationConfirmationReason.APPLICATION_REJECTED)
        verify(communicationConfirmationChannelMapper).fromEntityToDto(CommunicationConfirmationChannel.LETTER)
    }
}
