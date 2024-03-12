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
    @Mapping(target = "overseasAddress.addressee", source = "overseasAddress.addressee")
    @Mapping(target = "overseasAddress.addressLine1", source = "overseasAddress.address.addressLine1")
    @Mapping(target = "overseasAddress.addressLine2", source = "overseasAddress.address.addressLine2")
    @Mapping(target = "overseasAddress.addressLine3", source = "overseasAddress.address.addressLine3")
    @Mapping(target = "overseasAddress.addressLine4", source = "overseasAddress.address.addressLine4")
    @Mapping(target = "overseasAddress.addressLine5", source = "overseasAddress.address.addressLine5")
    @Mapping(target = "overseasAddress.country", source = "overseasAddress.address.country")
    fun toNotificationDestinationDto(toAddress: MessageAddress): NotificationDestinationDto
}
