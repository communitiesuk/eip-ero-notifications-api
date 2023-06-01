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

    @Bean
    fun notifyLetterTemplateConfiguration(
        voterCard: VoterCardNotifyLetterTemplateConfiguration,
        postal: PostalNotifyLetterTemplateConfiguration,
        proxy: ProxyNotifyLetterTemplateConfiguration
    ) = NotifyLetterTemplateConfiguration(voterCard, postal, proxy)
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
    rejectedDocumentEnglish: String,
    rejectedDocumentWelsh: String,
    ninoNotMatchedEnglish: String,
    ninoNotMatchedWelsh: String,
) : AbstractNotifyEmailTemplateConfiguration(
    sourceType = sourceType,
    receivedEnglish = receivedEnglish,
    receivedWelsh = receivedWelsh,
    approvedEnglish = null,
    approvedWelsh = null,
    photoResubmissionEnglish = null,
    photoResubmissionWelsh = null,
    photoResubmissionWithReasonsEnglish = null,
    photoResubmissionWithReasonsWelsh = null,
    idDocumentResubmissionEnglish = null,
    idDocumentResubmissionWelsh = null,
    idDocumentResubmissionWithReasonsEnglish = null,
    idDocumentResubmissionWithReasonsWelsh = null,
    idDocumentRequiredEnglish = null,
    idDocumentRequiredWelsh = null,
    rejectedDocumentEnglish = rejectedDocumentEnglish,
    rejectedDocumentWelsh = rejectedDocumentWelsh,
    rejectedSignatureEnglish = null,
    rejectedSignatureWelsh = null,
    ninoNotMatchedEnglish = ninoNotMatchedEnglish,
    ninoNotMatchedWelsh = ninoNotMatchedWelsh,
)

@ConfigurationProperties(prefix = "api.notify.template.proxy.email", ignoreUnknownFields = false)
@ConstructorBinding
class ProxyNotifyEmailTemplateConfiguration(
    sourceType: SourceType = SourceType.PROXY,
    receivedEnglish: String,
    receivedWelsh: String,
    rejectedSignatureEnglish: String,
    rejectedSignatureWelsh: String,
    rejectedDocumentEnglish: String,
    rejectedDocumentWelsh: String,
    ninoNotMatchedEnglish: String,
    ninoNotMatchedWelsh: String,
) : AbstractNotifyEmailTemplateConfiguration(
    sourceType = sourceType,
    receivedEnglish = receivedEnglish,
    receivedWelsh = receivedWelsh,
    approvedEnglish = null,
    approvedWelsh = null,
    photoResubmissionEnglish = null,
    photoResubmissionWelsh = null,
    photoResubmissionWithReasonsEnglish = null,
    photoResubmissionWithReasonsWelsh = null,
    idDocumentResubmissionEnglish = null,
    idDocumentResubmissionWelsh = null,
    idDocumentResubmissionWithReasonsEnglish = null,
    idDocumentResubmissionWithReasonsWelsh = null,
    idDocumentRequiredEnglish = null,
    idDocumentRequiredWelsh = null,
    rejectedDocumentEnglish = rejectedDocumentEnglish,
    rejectedDocumentWelsh = rejectedDocumentWelsh,
    rejectedSignatureEnglish = rejectedSignatureEnglish,
    rejectedSignatureWelsh = rejectedSignatureWelsh,
    ninoNotMatchedEnglish = ninoNotMatchedEnglish,
    ninoNotMatchedWelsh = ninoNotMatchedWelsh,
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
    photoResubmissionWithReasonsEnglish = null,
    photoResubmissionWithReasonsWelsh = null,
    idDocumentResubmissionEnglish = null,
    idDocumentResubmissionWelsh = null,
    idDocumentResubmissionWithReasonsEnglish = null,
    idDocumentResubmissionWithReasonsWelsh = null,
    idDocumentRequiredEnglish = null,
    idDocumentRequiredWelsh = null,
    rejectedDocumentEnglish = null,
    rejectedDocumentWelsh = null,
    rejectedSignatureEnglish = null,
    rejectedSignatureWelsh = null,
    ninoNotMatchedWelsh = null,
    ninoNotMatchedEnglish = null,
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
    photoResubmissionWithReasonsEnglish: String,
    photoResubmissionWithReasonsWelsh: String,
    idDocumentResubmissionEnglish: String,
    idDocumentResubmissionWelsh: String,
    idDocumentResubmissionWithReasonsEnglish: String,
    idDocumentResubmissionWithReasonsWelsh: String,
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
    photoResubmissionWithReasonsEnglish = photoResubmissionWithReasonsEnglish,
    photoResubmissionWithReasonsWelsh = photoResubmissionWithReasonsWelsh,
    idDocumentResubmissionEnglish = idDocumentResubmissionEnglish,
    idDocumentResubmissionWelsh = idDocumentResubmissionWelsh,
    idDocumentResubmissionWithReasonsEnglish = idDocumentResubmissionWithReasonsEnglish,
    idDocumentResubmissionWithReasonsWelsh = idDocumentResubmissionWithReasonsWelsh,
    idDocumentRequiredEnglish = idDocumentRequiredEnglish,
    idDocumentRequiredWelsh = idDocumentRequiredWelsh,
    rejectedDocumentEnglish = null,
    rejectedDocumentWelsh = null,
    rejectedSignatureEnglish = null,
    rejectedSignatureWelsh = null,
    ninoNotMatchedEnglish = null,
    ninoNotMatchedWelsh = null,
)

abstract class AbstractNotifyEmailTemplateConfiguration(
    val sourceType: SourceType,
    val receivedEnglish: String?,
    val receivedWelsh: String?,
    val approvedEnglish: String?,
    val approvedWelsh: String?,
    val photoResubmissionEnglish: String?,
    val photoResubmissionWelsh: String?,
    val photoResubmissionWithReasonsEnglish: String?,
    val photoResubmissionWithReasonsWelsh: String?,
    val idDocumentResubmissionEnglish: String?,
    val idDocumentResubmissionWelsh: String?,
    val idDocumentResubmissionWithReasonsEnglish: String?,
    val idDocumentResubmissionWithReasonsWelsh: String?,
    val idDocumentRequiredEnglish: String?,
    val idDocumentRequiredWelsh: String?,
    val rejectedDocumentEnglish: String?,
    val rejectedDocumentWelsh: String?,
    val rejectedSignatureEnglish: String?,
    val rejectedSignatureWelsh: String?,
    val ninoNotMatchedEnglish: String?,
    val ninoNotMatchedWelsh: String?,
)

data class NotifyLetterTemplateConfiguration(
    val voterCard: VoterCardNotifyLetterTemplateConfiguration,
    val postal: PostalNotifyLetterTemplateConfiguration,
    val proxy: ProxyNotifyLetterTemplateConfiguration
)

@ConfigurationProperties(prefix = "api.notify.template.voter-card.letter", ignoreUnknownFields = false)
@ConstructorBinding
class VoterCardNotifyLetterTemplateConfiguration(
    sourceType: SourceType = SourceType.VOTER_CARD,
    rejectedEnglish: String,
    rejectedWelsh: String,
    photoResubmissionEnglish: String,
    photoResubmissionWelsh: String,
    photoResubmissionWithReasonsEnglish: String,
    photoResubmissionWithReasonsWelsh: String,
    idDocumentResubmissionEnglish: String,
    idDocumentResubmissionWelsh: String,
    idDocumentResubmissionWithReasonsEnglish: String,
    idDocumentResubmissionWithReasonsWelsh: String,
    idDocumentRequiredEnglish: String,
    idDocumentRequiredWelsh: String
) : AbstractNotifyLetterTemplateConfiguration(
    sourceType = sourceType,
    receivedEnglish = null,
    receivedWelsh = null,
    rejectedEnglish = rejectedEnglish,
    rejectedWelsh = rejectedWelsh,
    photoResubmissionEnglish = photoResubmissionEnglish,
    photoResubmissionWelsh = photoResubmissionWelsh,
    photoResubmissionWithReasonsEnglish = photoResubmissionWithReasonsEnglish,
    photoResubmissionWithReasonsWelsh = photoResubmissionWithReasonsWelsh,
    idDocumentResubmissionEnglish = idDocumentResubmissionEnglish,
    idDocumentResubmissionWelsh = idDocumentResubmissionWelsh,
    idDocumentResubmissionWithReasonsEnglish = idDocumentResubmissionWithReasonsEnglish,
    idDocumentResubmissionWithReasonsWelsh = idDocumentResubmissionWithReasonsWelsh,
    idDocumentRequiredEnglish = idDocumentRequiredEnglish,
    idDocumentRequiredWelsh = idDocumentRequiredWelsh,
    rejectedDocumentEnglish = null,
    rejectedDocumentWelsh = null,
    rejectedSignatureEnglish = null,
    rejectedSignatureWelsh = null,
    ninoNotMatchedWelsh = null,
    ninoNotMatchedEnglish = null,
)

@ConfigurationProperties(prefix = "api.notify.template.postal.letter", ignoreUnknownFields = false)
@ConstructorBinding
class PostalNotifyLetterTemplateConfiguration(
    sourceType: SourceType = SourceType.POSTAL,
    rejectedDocumentEnglish: String,
    rejectedDocumentWelsh: String,
    ninoNotMatchedEnglish: String?,
    ninoNotMatchedWelsh: String?
) : AbstractNotifyLetterTemplateConfiguration(
    sourceType = sourceType,
    receivedEnglish = null,
    receivedWelsh = null,
    rejectedEnglish = null,
    rejectedWelsh = null,
    photoResubmissionEnglish = null,
    photoResubmissionWelsh = null,
    photoResubmissionWithReasonsEnglish = null,
    photoResubmissionWithReasonsWelsh = null,
    idDocumentResubmissionEnglish = null,
    idDocumentResubmissionWelsh = null,
    idDocumentResubmissionWithReasonsEnglish = null,
    idDocumentResubmissionWithReasonsWelsh = null,
    idDocumentRequiredEnglish = null,
    idDocumentRequiredWelsh = null,
    rejectedDocumentEnglish = rejectedDocumentEnglish,
    rejectedDocumentWelsh = rejectedDocumentWelsh,
    rejectedSignatureEnglish = null,
    rejectedSignatureWelsh = null,
    ninoNotMatchedEnglish = ninoNotMatchedEnglish,
    ninoNotMatchedWelsh = ninoNotMatchedWelsh,
)

abstract class AbstractNotifyLetterTemplateConfiguration(
    val sourceType: SourceType,
    val receivedEnglish: String?,
    val receivedWelsh: String?,
    val rejectedEnglish: String?,
    val rejectedWelsh: String?,
    val photoResubmissionEnglish: String?,
    val photoResubmissionWelsh: String?,
    val photoResubmissionWithReasonsEnglish: String?,
    val photoResubmissionWithReasonsWelsh: String?,
    val idDocumentResubmissionEnglish: String?,
    val idDocumentResubmissionWelsh: String?,
    val idDocumentResubmissionWithReasonsEnglish: String?,
    val idDocumentResubmissionWithReasonsWelsh: String?,
    val idDocumentRequiredEnglish: String?,
    val idDocumentRequiredWelsh: String?,
    val rejectedDocumentEnglish: String?,
    val rejectedDocumentWelsh: String?,
    val rejectedSignatureEnglish: String?,
    val rejectedSignatureWelsh: String?,
    val ninoNotMatchedEnglish: String?,
    val ninoNotMatchedWelsh: String?,
)

@ConfigurationProperties(prefix = "api.notify.template.proxy.letter", ignoreUnknownFields = false)
@ConstructorBinding
class ProxyNotifyLetterTemplateConfiguration(
    sourceType: SourceType = SourceType.PROXY,
    rejectedSignatureEnglish: String,
    rejectedSignatureWelsh: String,
    rejectedDocumentEnglish: String,
    rejectedDocumentWelsh: String,
    ninoNotMatchedEnglish: String,
    ninoNotMatchedWelsh: String,
) : AbstractNotifyLetterTemplateConfiguration(
    sourceType = sourceType,
    receivedEnglish = null,
    receivedWelsh = null,
    rejectedEnglish = null,
    rejectedWelsh = null,
    photoResubmissionEnglish = null,
    photoResubmissionWelsh = null,
    idDocumentResubmissionEnglish = null,
    idDocumentResubmissionWelsh = null,
    idDocumentRequiredEnglish = null,
    idDocumentResubmissionWithReasonsEnglish = null,
    idDocumentResubmissionWithReasonsWelsh = null,
    idDocumentRequiredWelsh = null,
    rejectedDocumentEnglish = rejectedDocumentEnglish,
    rejectedDocumentWelsh = rejectedDocumentWelsh,
    photoResubmissionWithReasonsEnglish = null,
    photoResubmissionWithReasonsWelsh = null,
    rejectedSignatureEnglish = rejectedSignatureEnglish,
    rejectedSignatureWelsh = rejectedSignatureWelsh,
    ninoNotMatchedEnglish = ninoNotMatchedEnglish,
    ninoNotMatchedWelsh = ninoNotMatchedWelsh,
)
