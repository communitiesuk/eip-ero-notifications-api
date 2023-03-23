package uk.gov.dluhc.notificationsapi.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import uk.gov.dluhc.notificationsapi.dto.SourceType
import uk.gov.service.notify.NotificationClient

@Configuration
class NotifyClientConfiguration {

    @Bean
    fun notificationClient(@Value("\${api.notify.api-key}") apiKey: String) = NotificationClient(apiKey)

    @Bean
    fun notifyEmailTemplateConfiguration(
        voterCard: VoterCardNotifyEmailTemplateConfiguration,
        postal: PostalNotifyEmailTemplateConfiguration,
        proxy: ProxyNotifyEmailTemplateConfiguration,
        overseas: OverseasNotifyEmailTemplateConfiguration,
    ) = NotifyEmailTemplateConfiguration(voterCard, postal, proxy, overseas)
}

data class NotifyEmailTemplateConfiguration(
    val voterCard: VoterCardNotifyEmailTemplateConfiguration,
    val postal: PostalNotifyEmailTemplateConfiguration,
    val proxy: ProxyNotifyEmailTemplateConfiguration,
    val overseas: OverseasNotifyEmailTemplateConfiguration,
)

@ConfigurationProperties(prefix = "api.notify.template.postal.email", ignoreUnknownFields = false)
@ConstructorBinding
class PostalNotifyEmailTemplateConfiguration(
    sourceType: SourceType = SourceType.POSTAL,
    receivedEnglish: String,
    receivedWelsh: String,
) : AbstractNotifyEmailTemplateConfiguration(
    sourceType = sourceType,
    receivedEnglish = receivedEnglish,
    receivedWelsh = receivedWelsh,
    approvedEnglish = null,
    approvedWelsh = null,
    photoResubmissionEnglish = null,
    photoResubmissionWelsh = null,
    idDocumentResubmissionEnglish = null,
    idDocumentResubmissionWelsh = null,
    idDocumentRequiredEnglish = null,
    idDocumentRequiredWelsh = null
)

@ConfigurationProperties(prefix = "api.notify.template.proxy.email", ignoreUnknownFields = false)
@ConstructorBinding
class ProxyNotifyEmailTemplateConfiguration(
    sourceType: SourceType = SourceType.PROXY,
    receivedEnglish: String,
    receivedWelsh: String,
) : AbstractNotifyEmailTemplateConfiguration(
    sourceType = sourceType,
    receivedEnglish = receivedEnglish,
    receivedWelsh = receivedWelsh,
    approvedEnglish = null,
    approvedWelsh = null,
    photoResubmissionEnglish = null,
    photoResubmissionWelsh = null,
    idDocumentResubmissionEnglish = null,
    idDocumentResubmissionWelsh = null,
    idDocumentRequiredEnglish = null,
    idDocumentRequiredWelsh = null
)

@ConfigurationProperties(prefix = "api.notify.template.overseas.email", ignoreUnknownFields = false)
@ConstructorBinding
class OverseasNotifyEmailTemplateConfiguration(
    sourceType: SourceType = SourceType.OVERSEAS,
    receivedEnglish: String,
    receivedWelsh: String,
) : AbstractNotifyEmailTemplateConfiguration(
    sourceType = sourceType,
    receivedEnglish = receivedEnglish,
    receivedWelsh = receivedWelsh,
    approvedEnglish = null,
    approvedWelsh = null,
    photoResubmissionEnglish = null,
    photoResubmissionWelsh = null,
    idDocumentResubmissionEnglish = null,
    idDocumentResubmissionWelsh = null,
    idDocumentRequiredEnglish = null,
    idDocumentRequiredWelsh = null
)

@ConfigurationProperties(prefix = "api.notify.template.voter-card.email", ignoreUnknownFields = false)
@ConstructorBinding
class VoterCardNotifyEmailTemplateConfiguration(
    sourceType: SourceType = SourceType.VOTER_CARD,
    receivedEnglish: String? = null,
    receivedWelsh: String? = null,
    approvedEnglish: String,
    approvedWelsh: String,
    photoResubmissionEnglish: String,
    photoResubmissionWelsh: String,
    idDocumentResubmissionEnglish: String,
    idDocumentResubmissionWelsh: String,
    idDocumentRequiredEnglish: String,
    idDocumentRequiredWelsh: String
) : AbstractNotifyEmailTemplateConfiguration(
    sourceType = sourceType,
    receivedEnglish = receivedEnglish,
    receivedWelsh = receivedWelsh,
    approvedEnglish = approvedEnglish,
    approvedWelsh = approvedWelsh,
    photoResubmissionEnglish = photoResubmissionEnglish,
    photoResubmissionWelsh = photoResubmissionWelsh,
    idDocumentResubmissionEnglish = idDocumentResubmissionEnglish,
    idDocumentResubmissionWelsh = idDocumentResubmissionWelsh,
    idDocumentRequiredEnglish = idDocumentRequiredEnglish,
    idDocumentRequiredWelsh = idDocumentRequiredWelsh
)

abstract class AbstractNotifyEmailTemplateConfiguration(
    val sourceType: SourceType,
    val receivedEnglish: String?,
    val receivedWelsh: String?,
    val approvedEnglish: String?,
    val approvedWelsh: String?,
    val photoResubmissionEnglish: String?,
    val photoResubmissionWelsh: String?,
    val idDocumentResubmissionEnglish: String?,
    val idDocumentResubmissionWelsh: String?,
    val idDocumentRequiredEnglish: String?,
    val idDocumentRequiredWelsh: String?
)

@ConfigurationProperties(prefix = "api.notify.template.voter-card.letter", ignoreUnknownFields = false)
@ConstructorBinding
data class NotifyLetterTemplateConfiguration(
    val receivedEnglish: String? = null,
    val receivedWelsh: String? = null,
    val rejectedEnglish: String,
    val rejectedWelsh: String,
    val photoResubmissionEnglish: String,
    val photoResubmissionWelsh: String,
    val idDocumentResubmissionEnglish: String,
    val idDocumentResubmissionWelsh: String,
    val idDocumentRequiredEnglish: String,
    val idDocumentRequiredWelsh: String
)
