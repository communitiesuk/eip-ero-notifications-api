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
    @Mapping(target = "overseasElectorAddress.addressee", source = "overseasElectorAddress.address.addressee")
    @Mapping(target = "overseasElectorAddress.addressLine1", source = "overseasElectorAddress.address.addressLine1")
    @Mapping(target = "overseasElectorAddress.addressLine2", source = "overseasElectorAddress.address.addressLine2")
    @Mapping(target = "overseasElectorAddress.addressLine3", source = "overseasElectorAddress.address.addressLine3")
    @Mapping(target = "overseasElectorAddress.addressLine4", source = "overseasElectorAddress.address.addressLine4")
    @Mapping(target = "overseasElectorAddress.addressLine5", source = "overseasElectorAddress.address.addressLine5")
    @Mapping(target = "overseasElectorAddress.country", source = "overseasElectorAddress.address.country")
    fun toNotificationDestinationDto(toAddress: MessageAddress): NotificationDestinationDto
}
