package uk.gov.dluhc.notificationsapi.messaging.mapper

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import uk.gov.dluhc.notificationsapi.dto.NotificationDestinationDto
import uk.gov.dluhc.notificationsapi.messaging.models.MessageAddress

@Mapper
interface NotificationDestinationDtoMapper {

    @Mapping(target = "emailAddress", source = "emailAddress")
    @Mapping(target = "postalAddress.addressee", source = "postalAddress.addressee")
    @Mapping(target = "postalAddress.property", source = "postalAddress.address.property")
    @Mapping(target = "postalAddress.street", source = "postalAddress.address.street")
    @Mapping(target = "postalAddress.town", source = "postalAddress.address.town")
    @Mapping(target = "postalAddress.area", source = "postalAddress.address.area")
    @Mapping(target = "postalAddress.locality", source = "postalAddress.address.locality")
    @Mapping(target = "postalAddress.postcode", source = "postalAddress.address.postcode")
    fun toNotificationDestinationDto(toAddress: MessageAddress): NotificationDestinationDto
}
