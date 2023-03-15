package uk.gov.dluhc.notificationsapi.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.given
import org.mockito.kotlin.verify
import uk.gov.dluhc.notificationsapi.database.entity.CommunicationConfirmation
import uk.gov.dluhc.notificationsapi.database.repository.CommunicationConfirmationRepository
import uk.gov.dluhc.notificationsapi.dto.CommunicationConfirmationChannelDto
import uk.gov.dluhc.notificationsapi.dto.CommunicationConfirmationReasonDto
import uk.gov.dluhc.notificationsapi.dto.SourceType
import uk.gov.dluhc.notificationsapi.mapper.CommunicationConfirmationMapper
import uk.gov.dluhc.notificationsapi.mapper.SourceTypeMapper
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aGssCode
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aLocalDateTime
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aSourceReference
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aValidKnownEroId
import uk.gov.dluhc.notificationsapi.testsupport.testdata.anEmailAddress
import uk.gov.dluhc.notificationsapi.testsupport.testdata.database.entity.aCommunicationConfirmationBuilder
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.aCommunicationConfirmationDtoBuilder
import java.time.LocalDateTime
import uk.gov.dluhc.notificationsapi.database.entity.SourceType as SourceTypeEntity

@ExtendWith(MockitoExtension::class)
class CommunicationConfirmationsServiceTest {

    @InjectMocks
    private lateinit var communicationConfirmationsService: CommunicationConfirmationsService

    @Mock
    private lateinit var eroService: EroService

    @Mock
    private lateinit var communicationConfirmationMapper: CommunicationConfirmationMapper

    @Mock
    private lateinit var communicationConfirmationRepository: CommunicationConfirmationRepository

    @Mock
    private lateinit var sourceTypeMapper: SourceTypeMapper

    @Test
    fun `should save a communication confirmation`() {
        // Given
        val eroId: String = aValidKnownEroId()
        val sourceReference: String = aSourceReference()
        val sourceType: SourceType = SourceType.ANONYMOUS_ELECTOR_DOCUMENT
        val gssCode: String = aGssCode()
        val reason: CommunicationConfirmationReasonDto = CommunicationConfirmationReasonDto.APPLICATION_REJECTED
        val channel: CommunicationConfirmationChannelDto = CommunicationConfirmationChannelDto.LETTER
        val requestor: String = anEmailAddress()
        val sentAt: LocalDateTime = aLocalDateTime()

        val dto = aCommunicationConfirmationDtoBuilder(
            eroId = eroId,
            sourceReference = sourceReference,
            sourceType = sourceType,
            gssCode = gssCode,
            reason = reason,
            channel = channel,
            requestor = requestor,
            sentAt = sentAt
        )

        val expectedEntity: CommunicationConfirmation = aCommunicationConfirmationBuilder()
        given(communicationConfirmationMapper.fromDtoToEntity(any())).willReturn(expectedEntity)

        // When
        communicationConfirmationsService.saveCommunicationConfirmation(dto)

        // Then
        verify(eroService).validateGssCodeAssociatedWithEro(eroId, gssCode)
        verify(communicationConfirmationMapper).fromDtoToEntity(dto)
        verify(communicationConfirmationRepository).saveCommunicationConfirmation(expectedEntity)
    }

    @Test
    fun `should fetch communication confirmations`() {
        // Given
        val eroId: String = aValidKnownEroId()
        val sourceReference: String = aSourceReference()
        val sourceType: SourceType = SourceType.ANONYMOUS_ELECTOR_DOCUMENT
        val gssCodes = listOf(aGssCode())

        val entities = listOf(aCommunicationConfirmationBuilder())

        val dtos = listOf(aCommunicationConfirmationDtoBuilder())

        given(eroService.lookupGssCodesForEro(any())).willReturn(gssCodes)
        given(communicationConfirmationRepository.getBySourceReferenceAndTypeAndGssCodes(any(), any(), any()))
            .willReturn(entities)
        given(sourceTypeMapper.fromDtoToEntity(any())).willReturn(SourceTypeEntity.ANONYMOUS_ELECTOR_DOCUMENT)
        given(communicationConfirmationMapper.fromEntitiesToDtos(any())).willReturn(dtos)

        // When
        val actualDtos =
            communicationConfirmationsService.getCommunicationConfirmationsForApplication(
                sourceReference,
                eroId,
                sourceType
            )

        // Then
        assertThat(actualDtos).isEqualTo(dtos)
        verify(eroService).lookupGssCodesForEro(eroId)
        verify(communicationConfirmationRepository).getBySourceReferenceAndTypeAndGssCodes(sourceReference, SourceTypeEntity.ANONYMOUS_ELECTOR_DOCUMENT, gssCodes)
        verify(sourceTypeMapper).fromDtoToEntity(SourceType.ANONYMOUS_ELECTOR_DOCUMENT)
        verify(communicationConfirmationMapper).fromEntitiesToDtos(entities)
    }
}
