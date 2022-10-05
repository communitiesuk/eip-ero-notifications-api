package uk.gov.dluhc.notificationsapi.rest

import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import uk.gov.dluhc.notificationsapi.models.TemplateType

@RestController
@CrossOrigin
class TemplateController {
    @PostMapping("/templates/{templateType}/preview")
    fun generateTemplatePreview(@PathVariable templateType: TemplateType): String {
        return ""
    }
}
