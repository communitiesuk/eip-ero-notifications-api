package uk.gov.dluhc.notificationsapi.database.repository

import mu.KotlinLogging
import org.springframework.stereotype.Repository
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.Expression
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
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

    private fun queryRequest(sourceReference: String, sourceType: SourceType, gssCodes: List<String>): QueryEnhancedRequest.Builder =
        QueryEnhancedRequest.builder()
            .queryConditional(QueryConditional.keyEqualTo(key(sourceReference)))
            .filterExpression(
                sourceTypeFilterExpression(sourceType)
                    .and(gssCodesFilterExpression(gssCodes))
            )

    private fun sourceTypeFilterExpression(sourceType: SourceType): Expression =
        Expression.builder()
            .expression("#sourceType = :sourceType")
            .putExpressionName("#sourceType", "sourceType")
            .putExpressionValue(":sourceType", AttributeValue.fromS(sourceType.name))
            .build()

    private fun gssCodesFilterExpression(gssCodes: List<String>): Expression =
        Expression.builder()
            .expression("#gssCode IN (${List(gssCodes.size) { index -> ":gssCode_$index" }.joinToString(",")})")
            .putExpressionName("#gssCode", "gssCode")
            .also { filterExpression ->
                gssCodes.onEachIndexed { index, gssCode ->
                    filterExpression.putExpressionValue(":gssCode_$index", AttributeValue.fromS(gssCode))
                }
            }.build()

    private fun key(partitionValue: String): Key =
        Key.builder().partitionValue(partitionValue).build()
}
