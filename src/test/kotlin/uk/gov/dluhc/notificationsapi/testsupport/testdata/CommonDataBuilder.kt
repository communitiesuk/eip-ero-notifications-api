package uk.gov.dluhc.notificationsapi.testsupport.testdata

import uk.gov.dluhc.notificationsapi.domain.NotificationType
import uk.gov.dluhc.notificationsapi.domain.SourceType
import java.time.LocalDateTime
import java.util.UUID

fun anEmailAddress() = "user@email.com"

fun aRequestor() = "user-id"

fun aGssCode() = "E99999999"

fun aSourceReference() = "fea5d37b-5c4a-445c-b428-7dc799be1d8e"

fun aSourceType() = SourceType.VOTER_CARD

fun aNotificationId(): UUID = UUID.fromString("3efbf304-1c47-453d-87f7-6bf5efe0495f")

fun aNotificationType() = NotificationType.APPLICATION_APPROVED

fun aNotificationPersonalisationMap(): Map<String, String> = mapOf(
    "subject_param" to "test subject",
    "name_param" to "John",
    "custom_title" to "Resubmitting photo",
    "date" to "15/Oct/2022"
)

fun aLocalDateTime(): LocalDateTime = LocalDateTime.of(2022, 10, 6, 9, 58, 24)
