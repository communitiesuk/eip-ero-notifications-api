package uk.gov.dluhc.notificationsapi.mapper

import org.springframework.stereotype.Component
import uk.gov.dluhc.notificationsapi.dto.PhotoResubmissionPersonalisationDto

@Component
class PhotoResubmissionPersonalisationMapper {

    fun toTemplatePersonalisationMap(dto: PhotoResubmissionPersonalisationDto): Map<String, String> {
        val personalisation = mutableMapOf<String, String>()

        with(dto) {
            personalisation["applicationReference"] = applicationReference
            personalisation["firstName"] = firstName
            personalisation["photoRequestFreeText"] = photoRequestFreeText
            personalisation["uploadPhotoLink"] = uploadPhotoLink
            with(eroContactDetails) {
                personalisation["LAName"] = localAuthorityName
                personalisation["eroPhone"] = phone
                personalisation["eroWebsite"] = website
                personalisation["eroEmail"] = email
                with(address) {
                    personalisation["eroAddressLine1"] = street
                    personalisation["eroAddressLine2"] = `property`
                    personalisation["eroAddressLine3"] = locality
                    personalisation["eroAddressLine4"] = town
                    personalisation["eroAddressLine5"] = area // TODO are these address mappings correct?
                    personalisation["eroPostcode"] = postcode
                }
            }
        }
        return personalisation
    }
}
