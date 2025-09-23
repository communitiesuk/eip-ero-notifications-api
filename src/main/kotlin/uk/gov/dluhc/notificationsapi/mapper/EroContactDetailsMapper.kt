package uk.gov.dluhc.notificationsapi.mapper

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import uk.gov.dluhc.notificationsapi.dto.ContactDetailsDto
import uk.gov.dluhc.notificationsapi.messaging.models.ContactDetails as ContactDetailsMessage
import uk.gov.dluhc.notificationsapi.models.ContactDetails as ContactDetailsApi

@Component
class EroContactDetailsMapper {

    @Autowired
    private lateinit var addressMapper: AddressMapper

    fun fromApiToDto(
        api: ContactDetailsApi,
    ): ContactDetailsDto = with(api) {
        ContactDetailsDto(
            localAuthorityName = localAuthorityName,
            website = website,
            email = email,
            phone = phone,
            address = addressMapper.fromApiToDto(address),
        )
    }

    fun fromSqsToDto(
        message: ContactDetailsMessage,
    ): ContactDetailsDto = with(message) {
        ContactDetailsDto(
            localAuthorityName = localAuthorityName,
            website = website,
            email = email,
            phone = phone,
            address = addressMapper.fromSqsToDto(address),
        )
    }
}
