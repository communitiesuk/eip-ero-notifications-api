package uk.gov.dluhc.notificationsapi.mapper

import org.springframework.stereotype.Component
import uk.gov.dluhc.notificationsapi.dto.ApplicationApprovedPersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.ApplicationReceivedPersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.ApplicationRejectedPersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.BaseTemplatePersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.BespokeCommPersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.IdDocumentPersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.IdDocumentRequiredPersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.LanguageDto
import uk.gov.dluhc.notificationsapi.dto.NotRegisteredToVotePersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.PhotoPersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.RejectedDocumentPersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.RejectedOverseasDocumentPersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.RejectedSignaturePersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.RequestedSignaturePersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.RequiredDocumentPersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.RequiredOverseasDocumentPersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.SignatureResubmissionPersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.SourceType
import uk.gov.dluhc.notificationsapi.dto.mapEroContactFields

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
                    ),
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
            personalisation["sourceType"] = personalisationSourceTypeString
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
            personalisation["sourceType"] = personalisationSourceTypeString
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
            personalisation["sourceType"] = personalisationSourceTypeString
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
            personalisation["sourceType"] = personalisationSourceTypeString
        }
        return personalisation
    }

    fun toRequiredDocumentTemplatePersonalisationMap(
        dto: RequiredDocumentPersonalisationDto,
        sourceTypeDto: SourceType,
    ): Map<String, Any> {
        val personalisation = mutableMapOf<String, Any>()

        with(dto) {
            personalisation["applicationReference"] = applicationReference
            personalisation["firstName"] = firstName
            if (sourceTypeDto == SourceType.OVERSEAS) {
                personalisation["freeText"] = getSafeValue(additionalNotes)
            } else {
                personalisation["additionalNotes"] = getSafeValue(additionalNotes)
            }
            with(mutableMapOf<String, String>()) {
                eroContactDetails.mapEroContactFields(this)
                personalisation.putAll(this)
            }
            personalisation["sourceType"] = personalisationSourceTypeString
        }
        return personalisation
    }

    fun toBespokeCommTemplatePersonalisationMap(
        dto: BespokeCommPersonalisationDto,
        language: LanguageDto,
    ): Map<String, Any> {
        val personalisation = mutableMapOf<String, Any>()

        with(dto) {
            personalisation["applicationReference"] = applicationReference
            personalisation["firstName"] = firstName
            personalisation["subjectHeader"] = subjectHeader
            personalisation["giveDetailsFreeText"] = details
            personalisation["explainFreeText"] = getSafeValue(whatToDo)
            with(mutableMapOf<String, String>()) {
                eroContactDetails.mapEroContactFields(this)
                personalisation.putAll(this)
            }
            personalisation["an"] = personalisationFullSourceTypeString == "overseas vote"
            personalisation["sourceType"] = personalisationFullSourceTypeString
            personalisation["deadline"] = getSafeValue(deadline)
            personalisation["whatYouNeedToDo"] = deadline != null || whatToDo != null
        }

        return personalisation
    }

    fun toSignatureResubmissionTemplatePersonalisationMap(
        dto: SignatureResubmissionPersonalisationDto,
        language: LanguageDto,
    ): Map<String, Any> {
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
            personalisation["sourceType"] = personalisationSourceTypeString
            personalisation["deadline"] = getSafeValue(deadline)
            personalisation["uploadSignatureLink"] = uploadSignatureLink
        }

        return personalisation
    }

    fun toNotRegisteredToVoteTemplatePersonalisationMap(
        dto: NotRegisteredToVotePersonalisationDto,
        language: LanguageDto,
    ): Map<String, Any> {
        val personalisation = mutableMapOf<String, Any>()

        with(dto) {
            personalisation["applicationReference"] = applicationReference
            personalisation["firstName"] = firstName
            personalisation["freeText"] = getSafeValue(freeText)
            personalisation["property"] = getSafeValue(property)
            personalisation["street"] = getSafeValue(street)
            personalisation["town"] = getSafeValue(town)
            personalisation["area"] = getSafeValue(area)
            personalisation["locality"] = getSafeValue(locality)
            personalisation["postcode"] = getSafeValue(postcode)
            with(mutableMapOf<String, String>()) {
                eroContactDetails.mapEroContactFields(this)
                personalisation.putAll(this)
            }
            personalisation["sourceType"] = personalisationFullSourceTypeString
            personalisation["deadline"] = getSafeValue(deadline)
        }

        return personalisation
    }

    fun toRejectedOverseasDocumentTemplatePersonalisationMap(dto: RejectedOverseasDocumentPersonalisationDto): Map<String, Any> {
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
        }
        return personalisation
    }

    fun toRequiredOverseasDocumentTemplatePersonalisationMap(dto: RequiredOverseasDocumentPersonalisationDto): Map<String, Any> {
        val personalisation = mutableMapOf<String, Any>()

        with(dto) {
            personalisation["applicationReference"] = applicationReference
            personalisation["firstName"] = firstName
            personalisation["freeText"] = getSafeValue(requiredDocumentFreeText)
            with(mutableMapOf<String, String>()) {
                eroContactDetails.mapEroContactFields(this)
                personalisation.putAll(this)
            }
        }
        return personalisation
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

    fun getSafeValue(input: String?): String = input ?: ""
}
