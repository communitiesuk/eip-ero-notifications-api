package uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models

import uk.gov.dluhc.notificationsapi.messaging.models.RemoveApplicationNotificationsMessage
import uk.gov.dluhc.notificationsapi.messaging.models.SourceType
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aGssCode
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aSourceReference

fun buildRemoveApplicationNotificationsMessage(
    sourceType: SourceType = SourceType.VOTER_MINUS_CARD,
    sourceReference: String = aSourceReference(),
    gssCode: String = aGssCode()
): RemoveApplicationNotificationsMessage =
    RemoveApplicationNotificationsMessage(
        sourceType = sourceType,
        sourceReference = sourceReference,
        gssCode = gssCode
    )
