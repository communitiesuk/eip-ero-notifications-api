package uk.gov.dluhc.notificationsapi.client.mapper

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Named
import uk.gov.dluhc.notificationsapi.dto.SendNotificationResponseDto
import uk.gov.service.notify.SendEmailResponse
import uk.gov.service.notify.SendLetterResponse
import java.util.Optional

@Mapper
abstract class SendNotificationResponseMapper {

    @Mapping(source = "reference", target = "reference", qualifiedByName = ["unwrapString"])
    @Mapping(source = "fromEmail", target = "fromEmail", qualifiedByName = ["unwrapString"])
    abstract fun toSendNotificationResponse(sendEmailResponse: SendEmailResponse): SendNotificationResponseDto

    @Mapping(source = "reference", target = "reference", qualifiedByName = ["unwrapString"])
    abstract fun toSendNotificationResponse(sendLetterResponse: SendLetterResponse): SendNotificationResponseDto

    @Named("unwrapString")
    protected fun unwrapString(optional: Optional<String>): String? {
        return optional.orElse(null)
    }
}
