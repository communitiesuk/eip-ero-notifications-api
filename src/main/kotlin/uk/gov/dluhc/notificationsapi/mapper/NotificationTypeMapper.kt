package uk.gov.dluhc.notificationsapi.mapper

import org.mapstruct.Mapper
import org.mapstruct.MappingConstants
import org.mapstruct.ValueMapping
import uk.gov.dluhc.notificationsapi.dto.NotificationType
import uk.gov.dluhc.notificationsapi.messaging.models.MessageType
import uk.gov.dluhc.notificationsapi.models.TemplateType
import uk.gov.dluhc.notificationsapi.database.entity.NotificationType as NotificationTypeEntity

@Mapper
interface NotificationTypeMapper {

    @ValueMapping(source = "APPLICATION_MINUS_RECEIVED", target = "APPLICATION_RECEIVED")
    @ValueMapping(source = "APPLICATION_MINUS_APPROVED", target = "APPLICATION_APPROVED")
    @ValueMapping(source = "APPLICATION_MINUS_REJECTED", target = "APPLICATION_REJECTED")
    @ValueMapping(source = "PHOTO_MINUS_RESUBMISSION", target = "PHOTO_RESUBMISSION")
    @ValueMapping(source = "ID_MINUS_DOCUMENT_MINUS_RESUBMISSION", target = "ID_DOCUMENT_RESUBMISSION")
    @ValueMapping(source = "ID_MINUS_DOCUMENT_MINUS_REQUIRED", target = "ID_DOCUMENT_REQUIRED")
    @ValueMapping(source = "REJECTED_MINUS_SIGNATURE", target = "REJECTED_SIGNATURE")
    fun mapMessageTypeToNotificationType(messageType: MessageType): NotificationType

    // PHOTO_RESUBMISSION_WITH_REASONS is an implementation detail and not a "business" notification type
    // Therefore it should be saved to the database as PHOTO_RESUBMISSION
    @ValueMapping(source = "PHOTO_RESUBMISSION_WITH_REASONS", target = "PHOTO_RESUBMISSION")
    // ID_DOCUMENT_RESUBMISSION_WITH_REASONS is an implementation detail and not a "business" notification type
    // Therefore it should be saved to the database as ID_DOCUMENT_RESUBMISSION
    @ValueMapping(source = "ID_DOCUMENT_RESUBMISSION_WITH_REASONS", target = "ID_DOCUMENT_RESUBMISSION")
    fun toNotificationTypeEntity(notificationType: NotificationType): NotificationTypeEntity

    fun toNotificationTypeDto(notificationTypeEntity: NotificationTypeEntity): NotificationType

    @ValueMapping(source = "APPLICATION_RECEIVED", target = "APPLICATION_MINUS_RECEIVED")
    @ValueMapping(source = "APPLICATION_APPROVED", target = "APPLICATION_MINUS_APPROVED")
    @ValueMapping(source = "APPLICATION_REJECTED", target = "APPLICATION_MINUS_REJECTED")
    @ValueMapping(source = "PHOTO_RESUBMISSION", target = "PHOTO_MINUS_RESUBMISSION")
    @ValueMapping(source = "ID_DOCUMENT_RESUBMISSION", target = "ID_MINUS_DOCUMENT_MINUS_RESUBMISSION")
    @ValueMapping(source = "ID_DOCUMENT_REQUIRED", target = "ID_MINUS_DOCUMENT_MINUS_REQUIRED")
    @ValueMapping(source = "REJECTED_DOCUMENT", target = "REJECTED_MINUS_DOCUMENT")
    @ValueMapping(source = "REJECTED_SIGNATURE", target = "REJECTED_MINUS_SIGNATURE")
    // Mapping NotificationType.PHOTO_RESUBMISSION_WITH_REASONS or NotificationType.ID_DOCUMENT_RESUBMISSION_WITH_REASONS
    // to the REST API (TemplateType) is not supported and will never happen because PHOTO_RESUBMISSION_WITH_REASONS
    // and ID_DOCUMENT_RESUBMISSION_WITH_REASONS are not saved as database enums, so they will never be presented in
    // this method call. MapStruct does not know this though, so makes us handle the scenario
    @ValueMapping(source = "PHOTO_RESUBMISSION_WITH_REASONS", target = MappingConstants.THROW_EXCEPTION)
    @ValueMapping(source = "ID_DOCUMENT_RESUBMISSION_WITH_REASONS", target = MappingConstants.THROW_EXCEPTION)
    fun fromNotificationTypeDtoToTemplateTypeApi(notificationType: NotificationType): TemplateType
}
