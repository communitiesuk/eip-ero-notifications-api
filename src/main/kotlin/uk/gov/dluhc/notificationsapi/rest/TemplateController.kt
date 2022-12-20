package uk.gov.dluhc.notificationsapi.rest

import org.springframework.web.bind.WebDataBinder
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.InitBinder
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import uk.gov.dluhc.notificationsapi.config.TemplateTypeEditor
import uk.gov.dluhc.notificationsapi.mapper.GenerateTemplatePreviewRequestDtoMapper
import uk.gov.dluhc.notificationsapi.models.GenerateTemplatePreviewRequest
import uk.gov.dluhc.notificationsapi.models.GenerateTemplatePreviewResponse
import uk.gov.dluhc.notificationsapi.models.TemplateType
import uk.gov.dluhc.notificationsapi.service.TemplateService
import javax.validation.Valid

@RestController
@CrossOrigin
class TemplateController(
    private val templateService: TemplateService,
    private val generateTemplatePreviewRequestDtoMapper: GenerateTemplatePreviewRequestDtoMapper,
) {
    @InitBinder
    fun initBinder(dataBinder: WebDataBinder) {
        dataBinder.registerCustomEditor(TemplateType::class.java, TemplateTypeEditor())
    }

    @PostMapping("/deprecated/templates/{templateType}/preview")
    @Deprecated(message = "Use template specific method")
    fun generateTemplatePreview(
        @PathVariable templateType: TemplateType,
        @Valid @RequestBody request: GenerateTemplatePreviewRequest
    ): GenerateTemplatePreviewResponse {
        return with(
            templateService.generateTemplatePreview(
                generateTemplatePreviewRequestDtoMapper.toGenerateTemplatePreviewRequestDto(
                    templateType,
                    request
                )
            )
        ) {
            GenerateTemplatePreviewResponse(text, subject, html)
        }
    }
}
