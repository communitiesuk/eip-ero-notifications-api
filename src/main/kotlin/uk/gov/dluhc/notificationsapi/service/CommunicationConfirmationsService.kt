package uk.gov.dluhc.notificationsapi.service

import mu.KotlinLogging
import org.springframework.stereotype.Service
import uk.gov.dluhc.notificationsapi.database.entity.CommunicationConfirmation
import uk.gov.dluhc.notificationsapi.database.repository.CommunicationConfirmationRepository
import uk.gov.dluhc.notificationsapi.dto.CommunicationConfirmationDto
import uk.gov.dluhc.notificationsapi.dto.SourceType
import uk.gov.dluhc.notificationsapi.mapper.CommunicationConfirmationMapper
import uk.gov.dluhc.notificationsapi.mapper.SourceTypeMapper

private val logger = KotlinLogging.logger {}

/**
 * Class exposing service methods relating to Communication Confirmations sent for applications.
 */
@Service
class CommunicationConfirmationsService(
    private val eroService: EroService,
    private val communicationConfirmationRepository: CommunicationConfirmationRepository,
    private val communicationConfirmationMapper: CommunicationConfirmationMapper,
    private val sourceTypeMapper: SourceTypeMapper,
) {
    fun saveCommunicationConfirmation(dto: CommunicationConfirmationDto) =
        with(dto) {
            logger.debug { "Saving communication confirmation for $sourceType application $sourceReference" }

            eroService.validateGssCodeAssociatedWithEro(eroId = eroId!!, gssCode = gssCode)
            val entity = communicationConfirmationMapper.fromDtoToEntity(dto)
            communicationConfirmationRepository.saveCommunicationConfirmation(entity)
        }

    /**
     * Returns a list of [CommunicationConfirmationDto]s for the application identified by the specified
     * sourceReference, ERO ID and sourceType.
     */
    fun getCommunicationConfirmationsForApplication(
        sourceReference: String,
        eroId: String,
        sourceType: SourceType,
    ): List<CommunicationConfirmationDto> {
        val gssCodesForEro = eroService.lookupGssCodesForEro(eroId)
        val sortedCommunicationConfirmations: List<CommunicationConfirmation> =
            communicationConfirmationRepository.getBySourceReferenceAndTypeAndGssCodes(
                sourceReference = sourceReference,
                sourceType = sourceTypeMapper.fromDtoToEntity(sourceType),
                gssCodes = gssCodesForEro,
            ).sortedByDescending { it.sentAt }

        logger.debug { "Returning ${sortedCommunicationConfirmations.size} CommunicationConfirmations for $sourceType application $sourceReference" }

        return communicationConfirmationMapper.fromEntitiesToDtos(sortedCommunicationConfirmations)
    }
}
