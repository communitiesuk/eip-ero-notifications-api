package uk.gov.dluhc.notificationsapi.rest

import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import uk.gov.dluhc.notificationsapi.mapper.PhotoResubmissionTemplatePreviewDtoMapper
import uk.gov.dluhc.notificationsapi.models.GeneratePhotoResubmissionTemplatePreviewRequest
import uk.gov.dluhc.notificationsapi.models.GenerateTemplatePreviewResponse
import uk.gov.dluhc.notificationsapi.service.TemplateService
import javax.validation.Valid

@RestController
@CrossOrigin
class TemplateController(
    private val templateService: TemplateService,
    private val photoResubmissionTemplatePreviewDtoMapper: PhotoResubmissionTemplatePreviewDtoMapper
) {

    @PostMapping("/templates/photo-resubmission/preview")
    fun generatePhotoResubmissionTemplatePreview(
        @Valid @RequestBody request: GeneratePhotoResubmissionTemplatePreviewRequest
    ): GenerateTemplatePreviewResponse {
        return with(
            templateService.generatePhotoResubmissionTemplatePreview(
                photoResubmissionTemplatePreviewDtoMapper.toPhotoResubmissionTemplatePreviewDto(
                    request
                )
            )
        ) {
            GenerateTemplatePreviewResponse(text, subject, html)
        }
    }
}
