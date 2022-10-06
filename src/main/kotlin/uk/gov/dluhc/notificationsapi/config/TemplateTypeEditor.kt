package uk.gov.dluhc.notificationsapi.config

import uk.gov.dluhc.notificationsapi.models.TemplateType
import java.beans.PropertyEditorSupport

class TemplateTypeEditor : PropertyEditorSupport() {
    override fun setAsText(text: String) {
        value = TemplateType.values().find { it.value == text }
            ?: throw IllegalArgumentException("Error on templateType path value: rejected value [$text]")
    }
}
