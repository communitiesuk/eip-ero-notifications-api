package uk.gov.dluhc.notificationsapi.testsupport.model

/**
 * Based on uk.gov.service.notify.SendEmailResponse constructor JSON string parsing.
 */
data class NotifySendEmailSuccessResponse(
    val id: String = "ad0bf394-9000-4c9b-8a0c-734fef454ee4",
    var reference: String? = "Our reference",
    val content: Content = Content(),
    val template: Template = Template()
)

data class Content(
    val body: String = "body content",
    var from_email: String? = "Could be null or actual from email address",
    val subject: String = "Email subject"
)

data class Template(
    val id: String = "e6de9bf4-3757-4e5f-a9a4-a449616ec6d2",
    val version: String = "1",
    val uri: String = "https://www.notifications.service.gov.uk/services/137e13d7-6acd-4449-815e-de0eb0c083ba/templates/e6de9bf4-3757-4e5f-a9a4-a449616ec6d2/1"
)
