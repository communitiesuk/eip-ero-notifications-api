package uk.gov.dluhc.notificationsapi.mapper

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import uk.gov.dluhc.eromanagementapi.models.ContactDetails
import uk.gov.dluhc.eromanagementapi.models.LocalAuthorityResponse
import uk.gov.dluhc.notificationsapi.dto.ContactDetailsDto
import uk.gov.dluhc.notificationsapi.dto.EroContactDetailsDto
import uk.gov.dluhc.notificationsapi.dto.EroDto
import uk.gov.dluhc.notificationsapi.models.ContactDetails as ContactDetailsApi

@Mapper
abstract class EroDtoMapper {

    @Mapping(
        target = "englishContactDetails",
        expression = "java(toEroContactDetailsDto( localAuthority.getName(), localAuthority.getContactDetailsEnglish() ))",
    )
    @Mapping(
        target = "welshContactDetails",
        expression = "java(toNullEroContactDetailsDto( localAuthority.getName(), localAuthority.getContactDetailsWelsh() ))",
    )
    abstract fun toEroDto(localAuthority: LocalAuthorityResponse): EroDto

    fun toNullEroContactDetailsDto(name: String, contactDetails: ContactDetails?): EroContactDetailsDto? {
        return if (contactDetails == null) {
            null
        } else {
            toEroContactDetailsDto(name, contactDetails)
        }
    }

    @Mapping(target = "emailAddress", source = "contactDetails.email")
    @Mapping(target = "phoneNumber", source = "contactDetails.phone")
    abstract fun toEroContactDetailsDto(name: String, contactDetails: ContactDetails): EroContactDetailsDto

    abstract fun toContactDetailsDto(contactDetails: ContactDetailsApi): ContactDetailsDto
}
