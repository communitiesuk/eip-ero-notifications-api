package uk.gov.dluhc.notificationsapi.dto

data class EroDto(
    val englishContactDetails: EroContactDetailsDto,
    val welshContactDetails: EroContactDetailsDto? = null
)
