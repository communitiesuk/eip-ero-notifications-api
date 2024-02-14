package uk.gov.dluhc.notificationsapi.mapper

import org.springframework.stereotype.Component
import uk.gov.dluhc.notificationsapi.dto.*

@Component
class TemplatePersonalisationDtoMapper {

    fun toPhotoResubmissionTemplatePersonalisationMap(dto: PhotoPersonalisationDto): Map<String, Any> {
        return with(dto) {
            getBasicContactDetailsPersonalisationMap(this)
                .plus(
                    mapOf(
                        "photoRejectionReasons" to photoRejectionReasons,
                        "photoRejectionNotes" to getSafeValue(photoRejectionNotes),
                        "photoRequestFreeText" to photoRequestFreeText,
                        "uploadPhotoLink" to uploadPhotoLink,
                    )
                )
        }
    }

    fun toIdDocumentResubmissionTemplatePersonalisationMap(dto: IdDocumentPersonalisationDto): Map<String, String> {
        val personalisation = getBasicContactDetailsPersonalisationMap(dto)

        with(dto) {
            personalisation["documentRequestFreeText"] = idDocumentRequestFreeText
            if (!documentRejectionText.isNullOrEmpty()) {
                personalisation["documentRejectionText"] = documentRejectionText
            }
        }
        return personalisation
    }

    fun toIdDocumentRequiredTemplatePersonalisationMap(dto: IdDocumentRequiredPersonalisationDto): Map<String, String> {
        val personalisation = mutableMapOf<String, String>()

        with(dto) {
            personalisation["applicationReference"] = applicationReference
            personalisation["firstName"] = firstName
            personalisation["ninoFailFreeText"] = idDocumentRequiredFreeText
            with(mutableMapOf<String, String>()) {
                eroContactDetails.mapEroContactFields(this)
                personalisation.putAll(this)
            }
        }

        return personalisation
    }

    fun toApplicationReceivedTemplatePersonalisationMap(dto: ApplicationReceivedPersonalisationDto): Map<String, Any> {
        val personalisation = mutableMapOf<String, Any>()

        with(dto) {
            personalisation["applicationReference"] = applicationReference
            personalisation["firstName"] = firstName
            with(mutableMapOf<String, String>()) {
                eroContactDetails.mapEroContactFields(this)
                personalisation.putAll(this)
            }
            personalisation["sourceType"] = sourceType
        }
        return personalisation
    }

    fun toApplicationApprovedTemplatePersonalisationMap(dto: ApplicationApprovedPersonalisationDto): Map<String, String> {
        return getBasicContactDetailsPersonalisationMap(dto)
    }

    fun toApplicationRejectedTemplatePersonalisationMap(dto: ApplicationRejectedPersonalisationDto): Map<String, Any> {
        val personalisation = mutableMapOf<String, Any>()

        with(dto) {
            personalisation["applicationReference"] = applicationReference
            personalisation["firstName"] = firstName
            personalisation["rejectionReasonList"] = rejectionReasonList
            personalisation["rejectionReasonMessage"] = getSafeValue(rejectionReasonMessage)
            with(mutableMapOf<String, String>()) {
                eroContactDetails.mapEroContactFields(this)
                personalisation.putAll(this)
            }
        }
        return personalisation
    }

    fun toRejectedDocumentTemplatePersonalisationMap(dto: RejectedDocumentPersonalisationDto): Map<String, Any> {
        val personalisation = mutableMapOf<String, Any>()

        with(dto) {
            personalisation["applicationReference"] = applicationReference
            personalisation["firstName"] = firstName
            personalisation["rejectedDocuments"] = documents
            personalisation["rejectionMessage"] = getSafeValue(rejectedDocumentFreeText)
            with(mutableMapOf<String, String>()) {
                eroContactDetails.mapEroContactFields(this)
                personalisation.putAll(this)
            }
            personalisation["sourceType"] = sourceType
        }
        return personalisation
    }

    fun toRejectedSignatureTemplatePersonalisationMap(dto: RejectedSignaturePersonalisationDto): Map<String, Any> {
        val personalisation = mutableMapOf<String, Any>()

        with(dto) {
            personalisation["applicationReference"] = applicationReference
            personalisation["firstName"] = firstName
            personalisation["rejectionNotes"] = getSafeValue(rejectionNotes)
            personalisation["rejectionReasons"] = rejectionReasons
            personalisation["rejectionFreeText"] = getSafeValue(rejectionFreeText)
            with(mutableMapOf<String, String>()) {
                eroContactDetails.mapEroContactFields(this)
                personalisation.putAll(this)
            }
            personalisation["sourceType"] = sourceType
        }
        return personalisation
    }

    fun toRequestedSignatureTemplatePersonalisationMap(dto: RequestedSignaturePersonalisationDto): Map<String, Any> {
        val personalisation = mutableMapOf<String, Any>()

        with(dto) {
            personalisation["applicationReference"] = applicationReference
            personalisation["firstName"] = firstName
            personalisation["freeText"] = getSafeValue(freeText)
            with(mutableMapOf<String, String>()) {
                eroContactDetails.mapEroContactFields(this)
                personalisation.putAll(this)
            }
            personalisation["sourceType"] = sourceType
        }
        return personalisation
    }

    fun toNinoNotMatchedTemplatePersonalisationMap(dto: NinoNotMatchedPersonalisationDto): Map<String, Any> {
        val personalisation = mutableMapOf<String, Any>()

        with(dto) {
            personalisation["applicationReference"] = applicationReference
            personalisation["firstName"] = firstName
            personalisation["additionalNotes"] = getSafeValue(additionalNotes)
            with(mutableMapOf<String, String>()) {
                eroContactDetails.mapEroContactFields(this)
                personalisation.putAll(this)
            }
            personalisation["sourceType"] = sourceType
        }
        return personalisation
    }

    fun toParentGuardianRequiredTemplatePersonalisationMap(dto: ParentGuardianPersonalisationDto): Map<String, Any> {
        val personalisation = mutableMapOf<String, Any>()

        with(dto) {
            personalisation["applicationReference"] = applicationReference
            personalisation["firstName"] = firstName
            personalisation["freeText"] = getSafeValue(freeText)
            with(mutableMapOf<String, String>()) {
                eroContactDetails.mapEroContactFields(this)
                personalisation.putAll(this)
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
            personalisation["eroAddressLine1"] = getSafeValue(property)
            personalisation["eroAddressLine2"] = street
            personalisation["eroAddressLine3"] = getSafeValue(town)
            personalisation["eroAddressLine4"] = getSafeValue(area)
            personalisation["eroAddressLine5"] = getSafeValue(locality)
            personalisation["eroPostcode"] = postcode
        }
    }

    private fun getBasicContactDetailsPersonalisationMap(dto: BaseTemplatePersonalisationDto): MutableMap<String, String> {
        val personalisation = mutableMapOf<String, String>()
        with(dto) {
            personalisation["applicationReference"] = applicationReference
            personalisation["firstName"] = firstName
            with(eroContactDetails) {
                mapEroContactFields(personalisation)
            }
        }
        return personalisation
    }

    private fun getSafeValue(input: String?): String = input ?: ""
}
