package uk.gov.dluhc.notificationsapi.testsupport.model

/**
 * Based on uk.gov.service.notify.SendLetterResponse constructor JSON string parsing.
 */

data class NotifySendLetterSuccessResponse(
    val id: String = "ad0bf394-9000-4c9b-8a0c-734fef454ee4",
    var reference: String? = "Our reference",
    var postage: String? = "second",
    val content: LetterContent = LetterContent(),
    val template: LetterTemplate = LetterTemplate(),
)

data class LetterContent(
    val body: String = "Body content of letter",
    val subject: String = "Subject of letter",
)

data class LetterTemplate(
    val id: String = "e6de9bf4-3757-4e5f-a9a4-a449616ec6d2",
    val version: String = "1",
    val uri: String = "https://www.notifications.service.gov.uk/services/137e13d7-6acd-4449-815e-de0eb0c083ba/templates/e6de9bf4-3757-4e5f-a9a4-a449616ec6d2/1",
)
