package uk.gov.dluhc.notificationsapi.rest

import org.springframework.web.bind.WebDataBinder
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.InitBinder
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import uk.gov.dluhc.notificationsapi.config.TemplateTypeEditor
import uk.gov.dluhc.notificationsapi.models.GenerateTemplatePreviewRequest
import uk.gov.dluhc.notificationsapi.models.TemplateType
import javax.validation.Valid

@RestController
@CrossOrigin
class TemplateController {
    @InitBinder
    fun initBinder(dataBinder: WebDataBinder) {
        dataBinder.registerCustomEditor(TemplateType::class.java, TemplateTypeEditor())
    }

    @PostMapping("/templates/{templateType}/preview")
    fun generateTemplatePreview(@PathVariable templateType: TemplateType, @Valid @RequestBody request: GenerateTemplatePreviewRequest): String {
        return ""
    }
}
