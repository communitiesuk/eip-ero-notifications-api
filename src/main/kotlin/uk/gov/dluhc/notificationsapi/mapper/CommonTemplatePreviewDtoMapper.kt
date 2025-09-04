package uk.gov.dluhc.notificationsapi.mapper

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import uk.gov.dluhc.notificationsapi.dto.CommonTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.dto.NotificationType
import uk.gov.dluhc.notificationsapi.models.CommunicationChannel
import uk.gov.dluhc.notificationsapi.models.Language
import uk.gov.dluhc.notificationsapi.models.SourceType

@Component
class CommonTemplatePreviewDtoMapper {

    @Autowired
    private lateinit var languageMapper: LanguageMapper

    @Autowired
    private lateinit var communicationChannelMapper: CommunicationChannelMapper

    @Autowired
    private lateinit var sourceTypeMapper: SourceTypeMapper

    fun toCommonTemplatePreviewDto(
        channel: CommunicationChannel,
        sourceType: SourceType,
        language: Language?,
        notificationTypeDto: NotificationType,
    ): CommonTemplatePreviewDto {
        return CommonTemplatePreviewDto(
                sourceType = sourceType.let(sourceTypeMapper::fromApiToDto),
                channel = channel.let(communicationChannelMapper::fromApiToDto),
                language = language!!.let(languageMapper::fromApiToDto),
                notificationType = notificationTypeDto,
            )

    }
}
