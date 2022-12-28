package uk.gov.dluhc.notificationsapi.mapper

import org.springframework.stereotype.Component
import uk.gov.dluhc.notificationsapi.dto.ContactDetailsDto
import uk.gov.dluhc.notificationsapi.dto.IdDocumentResubmissionPersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.PhotoResubmissionPersonalisationDto

@Component
class TemplatePersonalisationDtoMapper {

    fun toPhotoResubmissionTemplatePersonalisationMap(dto: PhotoResubmissionPersonalisationDto): Map<String, String> {
        val personalisation = mutableMapOf<String, String>()

        with(dto) {
            personalisation["applicationReference"] = applicationReference
            personalisation["firstName"] = firstName
            personalisation["photoRequestFreeText"] = photoRequestFreeText
            personalisation["uploadPhotoLink"] = uploadPhotoLink
            with(eroContactDetails) {
                mapEroContactFields(personalisation)
            }
        }
        return personalisation
    }

    fun toIdDocumentResubmissionTemplatePersonalisationMap(dto: IdDocumentResubmissionPersonalisationDto): Map<String, String> {
        val personalisation = mutableMapOf<String, String>()

        with(dto) {
            personalisation["applicationReference"] = applicationReference
            personalisation["firstName"] = firstName
            personalisation["documentRequestFreeText"] = idDocumentRequestFreeText
            with(eroContactDetails) {
                mapEroContactFields(personalisation)
            }
        }
        return personalisation
    }

    private fun ContactDetailsDto.mapEroContactFields(personalisation: MutableMap<String, String>) {
        personalisation["LAName"] = localAuthorityName
        personalisation["eroPhone"] = phone
        personalisation["eroWebsite"] = website
        personalisation["eroEmail"] = email
        with(address) {
            personalisation["eroAddressLine1"] = property ?: ""
            personalisation["eroAddressLine2"] = street
            personalisation["eroAddressLine3"] = town ?: ""
            personalisation["eroAddressLine4"] = area ?: ""
            personalisation["eroAddressLine5"] = locality ?: ""
            personalisation["eroPostcode"] = postcode
        }
    }
}
