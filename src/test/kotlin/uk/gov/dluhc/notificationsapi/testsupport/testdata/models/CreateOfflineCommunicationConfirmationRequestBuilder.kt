package uk.gov.dluhc.notificationsapi.testsupport.testdata.models

import uk.gov.dluhc.notificationsapi.models.CreateOfflineCommunicationConfirmationRequest
import uk.gov.dluhc.notificationsapi.models.OfflineCommunicationChannel
import uk.gov.dluhc.notificationsapi.models.OfflineCommunicationReason

fun aCreateOfflineCommunicationConfirmationRequestBuilder(
    gssCode: String,
    reason: OfflineCommunicationReason = OfflineCommunicationReason.APPLICATION_MINUS_REJECTED,
    channel: OfflineCommunicationChannel = OfflineCommunicationChannel.EMAIL,
): CreateOfflineCommunicationConfirmationRequest =
    CreateOfflineCommunicationConfirmationRequest(
        gssCode = gssCode,
        reason = reason,
        channel = channel,
    )
