package uk.gov.dluhc.notificationsapi.service

import mu.KotlinLogging
import org.springframework.stereotype.Service
import uk.gov.dluhc.notificationsapi.database.repository.CommunicationConfirmationRepository
import uk.gov.dluhc.notificationsapi.dto.CreateOfflineCommunicationConfirmationDto
import uk.gov.dluhc.notificationsapi.mapper.CommunicationConfirmationMapper

private val logger = KotlinLogging.logger {}

/**
 * Class exposing service methods relating to Communication Confirmations sent for applications.
 */
@Service
class CommunicationConfirmationsService(
    private val eroService: EroService,
    private val communicationConfirmationRepository: CommunicationConfirmationRepository,
    private val communicationConfirmationMapper: CommunicationConfirmationMapper,
) {
    fun saveCommunicationConfirmation(dto: CreateOfflineCommunicationConfirmationDto) =
        with(dto) {
            logger.debug { "Saving communication confirmation for $sourceType application $sourceReference" }

            eroService.validateGssCodeAssociatedWithEro(eroId = eroId, gssCode = gssCode)
            val entity = communicationConfirmationMapper.fromDtoToEntity(dto)
            communicationConfirmationRepository.saveCommunicationConfirmation(entity)
        }
}
