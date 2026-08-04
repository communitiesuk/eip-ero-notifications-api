package uk.gov.dluhc.notificationsapi.service

import mu.KotlinLogging
import org.springframework.stereotype.Service
import uk.gov.dluhc.notificationsapi.client.ElectoralRegistrationOfficeManagementApiClient
import uk.gov.dluhc.notificationsapi.client.ElectoralRegistrationOfficeManagementApiException
import uk.gov.dluhc.notificationsapi.exception.GssCodeMismatchException

private val logger = KotlinLogging.logger {}

@Service
class EroService(private val electoralRegistrationOfficeManagementApiClient: ElectoralRegistrationOfficeManagementApiClient) {

    fun lookupGssCodesForEro(eroId: String): List<String> =
        try {
            electoralRegistrationOfficeManagementApiClient.getElectoralRegistrationOfficeGssCodes(eroId)
        } catch (ex: ElectoralRegistrationOfficeManagementApiException) {
            logger.info { "Error ${ex.message} returned whilst looking up the gssCodes for ERO $eroId" }
            throw ex
        }

    fun validateGssCodeAssociatedWithEro(eroId: String, gssCode: String) {
        with(lookupGssCodesForEro(eroId)) {
            validateGssCodeAssociatedWithErosGssCodes(eroId, this, gssCode)
        }
    }

    private fun validateGssCodeAssociatedWithErosGssCodes(eroId: String, erosGssCodes: List<String>, gssCode: String) {
        if (gssCode !in erosGssCodes) {
            logger.warn { "ero '$eroId' is associated with gssCodes: $erosGssCodes but not [$gssCode]" }
            throw GssCodeMismatchException(
                eroId = eroId,
                requestGssCode = gssCode,
            )
        }
    }
}
