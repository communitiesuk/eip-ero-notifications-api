package uk.gov.dluhc.notificationsapi.service

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
import uk.gov.dluhc.notificationsapi.dto.OfflineCommunicationChannelDto
import uk.gov.dluhc.notificationsapi.dto.OfflineCommunicationReasonDto
import uk.gov.dluhc.notificationsapi.dto.SourceType
import uk.gov.dluhc.notificationsapi.mapper.CommunicationConfirmationMapper
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aGssCode
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aLocalDateTime
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aSourceReference
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aValidKnownEroId
import uk.gov.dluhc.notificationsapi.testsupport.testdata.anEmailAddress
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.aCreateOfflineCommunicationConfirmationDtoBuilder
import uk.gov.dluhc.notificationsapi.testsupport.testdata.models.aCommunicationConfirmationBuilder
import java.time.LocalDateTime

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

    @Test
    fun `should save a communication confirmation`() {
        // Given
        val eroId: String = aValidKnownEroId()
        val sourceReference: String = aSourceReference()
        val sourceType: SourceType = SourceType.ANONYMOUS_ELECTOR_DOCUMENT
        val gssCode: String = aGssCode()
        val reason: OfflineCommunicationReasonDto = OfflineCommunicationReasonDto.APPLICATION_REJECTED
        val channel: OfflineCommunicationChannelDto = OfflineCommunicationChannelDto.LETTER
        val requestor: String = anEmailAddress()
        val sentAt: LocalDateTime = aLocalDateTime()

        val dto = aCreateOfflineCommunicationConfirmationDtoBuilder(
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
}
