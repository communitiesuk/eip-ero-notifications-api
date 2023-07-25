package uk.gov.dluhc.notificationsapi.database.entity

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import java.util.UUID

@DynamoDbBean
data class NotificationAuditTemplateDetails(
    var templateId: UUID? = null,
    var templateVersion: Int? = null,
    var templateUri: String? = null,
)
