package uk.gov.dluhc.notificationsapi.rest

import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import uk.gov.dluhc.notificationsapi.mapper.*
import uk.gov.dluhc.notificationsapi.models.*
import uk.gov.dluhc.notificationsapi.service.TemplateService
import javax.validation.Valid

@RestController
@CrossOrigin
class TemplateController(
    private val templateService: TemplateService,
    private val identityDocumentResubmissionTemplatePreviewDtoMapper: IdentityDocumentResubmissionTemplatePreviewDtoMapper,
    private val photoResubmissionTemplatePreviewDtoMapper: PhotoResubmissionTemplatePreviewDtoMapper,
    private val generateIdDocumentRequiredTemplatePreviewDtoMapper: GenerateIdDocumentRequiredTemplatePreviewDtoMapper,
    private val applicationReceivedTemplatePreviewDtoMapper: ApplicationReceivedTemplatePreviewDtoMapper,
    private val applicationApprovedTemplatePreviewDtoMapper: GenerateApplicationApprovedTemplatePreviewDtoMapper,
    private val applicationRejectedTemplatePreviewDtoMapper: ApplicationRejectedTemplatePreviewDtoMapper,
    private val rejectedDocumentTemplatePreviewDtoMapper: RejectedDocumentTemplatePreviewDtoMapper,
    private val rejectedSignatureTemplatePreviewDtoMapper: RejectedSignatureTemplatePreviewDtoMapper,
    private val requestedSignatureTemplatePreviewDtoMapper: RequestedSignatureTemplatePreviewDtoMapper,
    private val ninoNotMatchedTemplatePreviewDtoMapper: NinoNotMatchedTemplatePreviewDtoMapper,
    private val parentGuardianRequiredTemplateDtoMapper: ParentGuardianRequiredTemplatePreviewDtoMapper
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

    @PostMapping("/templates/id-document-resubmission/preview")
    fun generateIdDocumentResubmissionTemplatePreview(
        @Valid @RequestBody request: GenerateIdDocumentResubmissionTemplatePreviewRequest
    ): GenerateTemplatePreviewResponse {
        return with(
            templateService.generateIdDocumentResubmissionTemplatePreview(
                identityDocumentResubmissionTemplatePreviewDtoMapper.toIdDocumentResubmissionTemplatePreviewDto(
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

    @PostMapping("/templates/rejected-signature/preview")
    fun generateRejectedSignatureTemplatePreview(@Valid @RequestBody request: GenerateRejectedSignatureTemplatePreviewRequest): GenerateTemplatePreviewResponse {
        return with(
            templateService.generateRejectedSignatureTemplatePreview(
                rejectedSignatureTemplatePreviewDtoMapper.toRejectedSignatureTemplatePreviewDto(request)
            )
        ) {
            GenerateTemplatePreviewResponse(text, subject, html)
        }
    }

    @PostMapping("/templates/requested-signature/preview")
    fun generateRequestedSignatureTemplatePreview(@Valid @RequestBody request: GenerateRequestedSignatureTemplatePreviewRequest): GenerateTemplatePreviewResponse {
        return with(
            templateService.generateRequestedSignatureTemplatePreview(
                requestedSignatureTemplatePreviewDtoMapper.toRequestedSignatureTemplatePreviewDto(request)
            )
        ) {
            GenerateTemplatePreviewResponse(text, subject, html)
        }
    }

    @PostMapping("/templates/nino-not-matched/preview")
    fun generateNinoNotMatchedTemplatePreview(@Valid @RequestBody request: GenerateNinoNotMatchedTemplatePreviewRequest): GenerateTemplatePreviewResponse {
        return templateService.generateNinoNotMatchedTemplatePreview(
            ninoNotMatchedTemplatePreviewDtoMapper.toDto(
                request
            )
        )
            .let { GenerateTemplatePreviewResponse(it.text, it.subject, it.html) }
    }

    @PostMapping("/templates/parent-guardian-required/preview")
    fun generateParentGuardianRequiredTemplatePreview(@Valid @RequestBody request: GenerateParentGuardianRequiredTemplatePreviewRequest): GenerateTemplatePreviewResponse {
        return with(
            templateService.generateParentGuardianRequiredTemplatePreview(
                parentGuardianRequiredTemplateDtoMapper.toParentGuardianRequiredTemplatePreviewDto(
                    request
                )
            )
        ) {
            GenerateTemplatePreviewResponse(text, subject, html)
        }
    }
}
