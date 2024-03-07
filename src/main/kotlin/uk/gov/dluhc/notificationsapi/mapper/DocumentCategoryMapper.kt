package uk.gov.dluhc.notificationsapi.mapper

import org.mapstruct.Mapper
import org.mapstruct.ValueMapping
import uk.gov.dluhc.notificationsapi.dto.DocumentCategoryDto
import uk.gov.dluhc.notificationsapi.dto.NotificationType
import uk.gov.dluhc.notificationsapi.models.DocumentCategory

@Mapper
interface DocumentCategoryMapper {

    @ValueMapping(source = "PARENT_MINUS_GUARDIAN", target = "PARENT_GUARDIAN")
    @ValueMapping(source = "PREVIOUS_MINUS_ADDRESS", target = "PREVIOUS_ADDRESS")
    fun fromApiToDto(documentCategory: DocumentCategory): DocumentCategoryDto

    @ValueMapping(source = "IDENTITY", target = "REJECTED_DOCUMENT")
    @ValueMapping(source = "PARENT_GUARDIAN", target = "REJECTED_PARENT_GUARDIAN")
    @ValueMapping(source = "PREVIOUS_ADDRESS", target = "REJECTED_PREVIOUS_ADDRESS")
    fun fromRejectedDocumentCategoryDtoToNotificationTypeDto(documentCategory: DocumentCategoryDto): NotificationType

    @ValueMapping(source = "IDENTITY", target = "NINO_NOT_MATCHED")
    @ValueMapping(source = "PARENT_GUARDIAN", target = "PARENT_GUARDIAN_PROOF_REQUIRED")
    @ValueMapping(source = "PREVIOUS_ADDRESS", target = "PREVIOUS_ADDRESS_DOCUMENT_REQUIRED")
    fun fromRequiredDocumentCategoryDtoToNotificationTypeDto(documentCategory: DocumentCategoryDto): NotificationType
}
