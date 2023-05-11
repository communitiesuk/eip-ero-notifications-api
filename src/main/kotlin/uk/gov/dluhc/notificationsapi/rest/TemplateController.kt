package uk.gov.dluhc.notificationsapi.rest

import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import uk.gov.dluhc.notificationsapi.mapper.ApplicationReceivedTemplatePreviewDtoMapper
import uk.gov.dluhc.notificationsapi.mapper.ApplicationRejectedTemplatePreviewDtoMapper
import uk.gov.dluhc.notificationsapi.mapper.GenerateApplicationApprovedTemplatePreviewDtoMapper
import uk.gov.dluhc.notificationsapi.mapper.GenerateIdDocumentRequiredTemplatePreviewDtoMapper
import uk.gov.dluhc.notificationsapi.mapper.RejectedDocumentTemplatePreviewDtoMapper
import uk.gov.dluhc.notificationsapi.mapper.ResubmissionTemplatePreviewDtoMapper
import uk.gov.dluhc.notificationsapi.models.GenerateApplicationApprovedTemplatePreviewRequest
import uk.gov.dluhc.notificationsapi.models.GenerateApplicationReceivedTemplatePreviewRequest
import uk.gov.dluhc.notificationsapi.models.GenerateApplicationRejectedTemplatePreviewRequest
import uk.gov.dluhc.notificationsapi.models.GenerateIdDocumentRequiredTemplatePreviewRequest
import uk.gov.dluhc.notificationsapi.models.GenerateIdDocumentResubmissionTemplatePreviewRequest
import uk.gov.dluhc.notificationsapi.models.GeneratePhotoResubmissionTemplatePreviewRequest
import uk.gov.dluhc.notificationsapi.models.GenerateRejectedDocumentTemplatePreviewRequest
import uk.gov.dluhc.notificationsapi.models.GenerateTemplatePreviewResponse
import uk.gov.dluhc.notificationsapi.service.TemplateService
import javax.validation.Valid

@RestController
@CrossOrigin
class TemplateController(
    private val templateService: TemplateService,
    private val resubmissionTemplatePreviewDtoMapper: ResubmissionTemplatePreviewDtoMapper,
    private val generateIdDocumentRequiredTemplatePreviewDtoMapper: GenerateIdDocumentRequiredTemplatePreviewDtoMapper,
    private val applicationReceivedTemplatePreviewDtoMapper: ApplicationReceivedTemplatePreviewDtoMapper,
    private val applicationApprovedTemplatePreviewDtoMapper: GenerateApplicationApprovedTemplatePreviewDtoMapper,
    private val applicationRejectedTemplatePreviewDtoMapper: ApplicationRejectedTemplatePreviewDtoMapper,
    private val rejectedDocumentTemplatePreviewDtoMapper: RejectedDocumentTemplatePreviewDtoMapper
) {

    @PostMapping("/templates/photo-resubmission/preview")
    fun generatePhotoResubmissionTemplatePreview(
        @Valid @RequestBody request: GeneratePhotoResubmissionTemplatePreviewRequest
    ): GenerateTemplatePreviewResponse {
        return with(
            templateService.generatePhotoResubmissionTemplatePreview(
                resubmissionTemplatePreviewDtoMapper.toPhotoResubmissionTemplatePreviewDto(
                    request
                )
            )
        ) {
            GenerateTemplatePreviewResponse(text, subject, html)
        }
    }

    @PostMapping("/templates/id-document-resubmission/preview")
    fun generateIdDocumentResubmissionTemplatePreview(
        @Valid @RequestBody request: GenerateIdDocumentResubmissionTemplatePreviewRequest
    ): GenerateTemplatePreviewResponse {
        return with(
            templateService.generateIdDocumentResubmissionTemplatePreview(
                resubmissionTemplatePreviewDtoMapper.toIdDocumentResubmissionTemplatePreviewDto(
                    request
                )
            )
        ) {
            GenerateTemplatePreviewResponse(text, subject, html)
        }
    }

    @PostMapping("/templates/id-document-required/preview")
    fun generateIdDocumentRequiredTemplatePreview(
        @Valid @RequestBody request: GenerateIdDocumentRequiredTemplatePreviewRequest
    ): GenerateTemplatePreviewResponse =
        with(
            templateService.generateIdDocumentRequiredTemplatePreview(
                generateIdDocumentRequiredTemplatePreviewDtoMapper.toGenerateIdDocumentRequiredTemplatePreviewDto(
                    request
                )
            )
        ) {
            GenerateTemplatePreviewResponse(text, subject, html)
        }

    @PostMapping("/templates/application-received/preview")
    fun generateApplicationReceivedTemplatePreview(
        @Valid @RequestBody request: GenerateApplicationReceivedTemplatePreviewRequest
    ): GenerateTemplatePreviewResponse {
        return with(
            templateService.generateApplicationReceivedTemplatePreview(
                applicationReceivedTemplatePreviewDtoMapper.toApplicationReceivedTemplatePreviewDto(
                    request
                )
            )
        ) {
            GenerateTemplatePreviewResponse(text, subject, html)
        }
    }

    @PostMapping("/templates/application-approved/preview")
    fun generateApplicationApprovedTemplatePreview(
        @Valid @RequestBody request: GenerateApplicationApprovedTemplatePreviewRequest
    ): GenerateTemplatePreviewResponse {
        return with(
            templateService.generateApplicationApprovedTemplatePreview(
                applicationApprovedTemplatePreviewDtoMapper.toApplicationApprovedTemplatePreviewDto(
                    request
                )
            )
        ) {
            GenerateTemplatePreviewResponse(text, subject, html)
        }
    }

    @PostMapping("/templates/application-rejected/preview")
    fun generateApplicationRejectedTemplatePreview(@Valid @RequestBody request: GenerateApplicationRejectedTemplatePreviewRequest): GenerateTemplatePreviewResponse {
        return with(
            templateService.generateApplicationRejectedTemplatePreview(
                applicationRejectedTemplatePreviewDtoMapper.toApplicationRejectedTemplatePreviewDto(request)
            )
        ) {
            GenerateTemplatePreviewResponse(text, subject, html)
        }
    }

    @PostMapping("/templates/rejected-document/preview")
    fun generateRejectedDocumentTemplatePreview(@Valid @RequestBody request: GenerateRejectedDocumentTemplatePreviewRequest): GenerateTemplatePreviewResponse {
        return with(
            templateService.generateRejectedDocumentTemplatePreview(
                rejectedDocumentTemplatePreviewDtoMapper.toRejectedDocumentTemplatePreviewDto(request)
            )
        ) {
            GenerateTemplatePreviewResponse(text, subject, html)
        }
    }
}
