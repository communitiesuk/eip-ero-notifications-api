package uk.gov.dluhc.notificationsapi.client.mapper

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Named
import uk.gov.dluhc.notificationsapi.domain.SendNotificationResponse
import uk.gov.service.notify.SendEmailResponse
import java.util.Optional

@Mapper
abstract class SendNotificationResponseMapper {

    @Mapping(source = "reference", target = "reference", qualifiedByName = ["unwrapString"])
    @Mapping(source = "fromEmail", target = "fromEmail", qualifiedByName = ["unwrapString"])
    abstract fun toSendNotificationResponse(sendEmailResponse: SendEmailResponse): SendNotificationResponse

    @Named("unwrapString")
    protected fun unwrapString(optional: Optional<String>): String? {
        return optional.orElse(null)
    }
}
