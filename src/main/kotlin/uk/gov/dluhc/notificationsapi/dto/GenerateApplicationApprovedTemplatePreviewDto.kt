package uk.gov.dluhc.notificationsapi.dto

class GenerateApplicationApprovedTemplatePreviewDto(
    val language: LanguageDto,
    val personalisation: ApplicationApprovedPersonalisationDto,
    val sourceType: SourceType,
) {
    val channel: NotificationChannel = NotificationChannel.EMAIL
    val notificationType: NotificationType = NotificationType.APPLICATION_APPROVED
}
