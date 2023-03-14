package uk.gov.dluhc.notificationsapi.database.repository

import mu.KotlinLogging
import org.springframework.stereotype.Repository
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import uk.gov.dluhc.notificationsapi.config.DynamoDbConfiguration
import uk.gov.dluhc.notificationsapi.database.entity.COMMUNICATION_CONFIRMATION_SOURCE_REFERENCE_INDEX_NAME
import uk.gov.dluhc.notificationsapi.database.entity.CommunicationConfirmation
import uk.gov.dluhc.notificationsapi.database.entity.SourceType

private val logger = KotlinLogging.logger {}

@Repository
class CommunicationConfirmationRepository(client: DynamoDbEnhancedClient, tableConfig: DynamoDbConfiguration) {

    companion object {
        private val COMMUNICATION_CONFIRMATIONS_TABLE_FULL_SCHEMA = TableSchema.fromBean(CommunicationConfirmation::class.java)
    }

    private val communicationConfirmationTableFull = client.table(tableConfig.communicationConfirmationsTableName, COMMUNICATION_CONFIRMATIONS_TABLE_FULL_SCHEMA)

    fun saveCommunicationConfirmation(communicationConfirmation: CommunicationConfirmation) {
        logger.debug { "Saving communication confirmation for reason [${communicationConfirmation.reason}], channel: [${communicationConfirmation.channel}]" }
        communicationConfirmationTableFull.putItem(communicationConfirmation)
    }

    fun getBySourceReferenceAndTypeAndGssCodes(sourceReference: String, sourceType: SourceType, gssCodes: List<String>): List<CommunicationConfirmation> {
        logger.debug("Fetching communication confirmation for sourceReference [$sourceReference], sourceType: [$sourceType], gssCodes: [$gssCodes]")
        val queryRequest = queryRequest(sourceReference, sourceType, gssCodes)
            .build()

        val index = communicationConfirmationTableFull.index(COMMUNICATION_CONFIRMATION_SOURCE_REFERENCE_INDEX_NAME)
        return index.query(queryRequest).flatMap { it.items() }
    }
}
