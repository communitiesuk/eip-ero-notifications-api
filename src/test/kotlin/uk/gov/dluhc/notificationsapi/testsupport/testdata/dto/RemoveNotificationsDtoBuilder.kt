package uk.gov.dluhc.notificationsapi.testsupport.testdata.dto

import uk.gov.dluhc.notificationsapi.dto.RemoveNotificationsDto
import uk.gov.dluhc.notificationsapi.dto.SourceType
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aGssCode
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aSourceReference
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aSourceType

fun buildRemoveNotificationsDto(
    gssCode: String = aGssCode(),
    sourceType: SourceType = aSourceType(),
    sourceReference: String = aSourceReference()
): RemoveNotificationsDto =
    RemoveNotificationsDto(
        gssCode = gssCode,
        sourceType = sourceType,
        sourceReference = sourceReference
    )

fun aRemoveNotificationsDto() = buildRemoveNotificationsDto()
