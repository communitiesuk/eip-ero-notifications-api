package uk.gov.dluhc.notificationsapi.mapper

import org.mapstruct.Mapper
import org.mapstruct.ValueMapping
import uk.gov.dluhc.notificationsapi.models.ApplicationRejectionReason

@Mapper
interface ApplicationRejectionReasonMapper {
    @ValueMapping(source = "INCOMPLETE_MINUS_APPLICATION", target = "Application is incomplete")
    @ValueMapping(source = "INACCURATE_MINUS_INFORMATION", target = "Application contains inaccurate information")
    @ValueMapping(source = "PHOTO_MINUS_IS_MINUS_NOT_MINUS_ACCEPTABLE", target = "Photo does not meet criteria")
    @ValueMapping(
        source = "NO_MINUS_RESPONSE_MINUS_FROM_MINUS_APPLICANT",
        target = "Applicant has not responded to requests for information"
    )
    @ValueMapping(source = "FRAUDULENT_MINUS_APPLICATION", target = "Suspected fraudulent application")
    @ValueMapping(source = "NOT_MINUS_REGISTERED_MINUS_TO_MINUS_VOTE", target = "Applicant is not registered to vote")
    @ValueMapping(source = "OTHER", target = "Other")
    fun toApplicationRejectionReasonMessage(rejectionReason: ApplicationRejectionReason): String
}
