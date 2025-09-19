package uk.gov.dluhc.notificationsapi.mapper

import org.springframework.stereotype.Component
import uk.gov.dluhc.notificationsapi.dto.AddressDto
import uk.gov.dluhc.notificationsapi.models.Address

@Component
class AddressMapper {
    fun fromApiToDto(
        api: Address,
    ): AddressDto = with(api) {
        AddressDto(
            street = street,
            property = property?.ifBlank { null },
            locality = locality?.ifBlank { null },
            town = town?.ifBlank { null },
            area = area?.ifBlank { null },
            postcode = postcode,
        )
    }
}
