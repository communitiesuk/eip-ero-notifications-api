package uk.gov.dluhc.notificationsapi.dto

abstract class SendNotificationRequestDto(
    val channel: NotificationChannel,
    val language: LanguageDto,
    val gssCode: String,
    val requestor: String,
    val sourceType: SourceType,
    val sourceReference: String,
    val emailAddress: String,
    val notificationType: NotificationType,
)

class SendNotificationPhotoResubmissionRequestDto(
    val personalisation: PhotoResubmissionPersonalisationDto,
    channel: NotificationChannel,
    language: LanguageDto,
    gssCode: String,
    requestor: String,
    sourceType: SourceType,
    sourceReference: String,
    emailAddress: String,
    notificationType: NotificationType,
) : SendNotificationRequestDto(
    gssCode = gssCode,
    requestor = requestor,
    sourceType = sourceType,
    sourceReference = sourceReference,
    emailAddress = emailAddress,
    notificationType = notificationType,
    channel = channel,
    language = language,
)
