package uk.gov.dluhc.notificationsapi.domain

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import java.util.UUID

@DynamoDbBean
data class SendNotificationResponse(
    var notificationId: UUID? = null,
    var reference: String? = null,
    var templateId: UUID? = null,
    var templateVersion: Int? = null,
    var templateUri: String? = null,
    var body: String? = null,
    var subject: String? = null,
    var fromEmail: String? = null,
)
