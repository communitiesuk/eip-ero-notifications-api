package uk.gov.dluhc.notificationsapi.dto

data class SendNotificationRequestDto(
    val channel: CommunicationChannel,
    val language: LanguageDto,
    val gssCode: String,
    val requestor: String,
    val sourceType: SourceType,
    val sourceReference: String,
    val toAddress: NotificationDestinationDto,
    val notificationType: NotificationType,
)
