package uk.gov.dluhc.notificationsapi.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import uk.gov.dluhc.notificationsapi.dto.SourceType
import uk.gov.service.notify.NotificationClient

@Configuration
class NotifyClientConfiguration {

    @Bean
    fun notificationClient(
        @Value("\${api.notify.api-key}") apiKey: String,
        @Value("\${api.notify.base-url}") baseUrl: String,
    ) = NotificationClient(apiKey, baseUrl)

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
        proxy: ProxyNotifyLetterTemplateConfiguration,
        overseas: OverseasNotifyLetterTemplateConfiguration,
    ) = NotifyLetterTemplateConfiguration(voterCard, postal, proxy, overseas)
}

data class NotifyEmailTemplateConfiguration(
    val voterCard: VoterCardNotifyEmailTemplateConfiguration,
    val postal: PostalNotifyEmailTemplateConfiguration,
    val proxy: ProxyNotifyEmailTemplateConfiguration,
    val overseas: OverseasNotifyEmailTemplateConfiguration,
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
    val rejectedSignatureWithReasonsEnglish: String?,
    val rejectedSignatureWithReasonsWelsh: String?,
    val requestedSignatureEnglish: String?,
    val requestedSignatureWelsh: String?,
    val ninoNotMatchedEnglish: String?,
    val ninoNotMatchedWelsh: String?,
    val ninoNotMatchedRestrictedDocumentsListEnglish: String?,
    val ninoNotMatchedRestrictedDocumentsListWelsh: String?,
    val rejectedParentGuardianEnglish: String?,
    val rejectedParentGuardianWelsh: String?,
    val rejectedPreviousAddressEnglish: String?,
    val rejectedPreviousAddressWelsh: String?,
    val parentGuardianProofRequiredEnglish: String?,
    val parentGuardianProofRequiredWelsh: String?,
    val previousAddressDocumentRequiredEnglish: String?,
    val previousAddressDocumentRequiredWelsh: String?,
    val bespokeCommEnglish: String?,
    val bespokeCommWelsh: String?,
    val notRegisteredToVoteEnglish: String?,
    val notRegisteredToVoteWelsh: String?,

)

@ConfigurationProperties(prefix = "api.notify.template.postal.email", ignoreUnknownFields = false)
class PostalNotifyEmailTemplateConfiguration(
    sourceType: SourceType = SourceType.POSTAL,
    receivedEnglish: String,
    receivedWelsh: String,
    rejectedDocumentEnglish: String,
    rejectedDocumentWelsh: String,
    rejectedSignatureEnglish: String,
    rejectedSignatureWelsh: String,
    rejectedSignatureWithReasonsEnglish: String,
    rejectedSignatureWithReasonsWelsh: String,
    requestedSignatureEnglish: String,
    requestedSignatureWelsh: String,
    ninoNotMatchedEnglish: String,
    ninoNotMatchedWelsh: String,
    ninoNotMatchedRestrictedDocumentsListEnglish: String,
    ninoNotMatchedRestrictedDocumentsListWelsh: String,
    bespokeCommEnglish: String,
    bespokeCommWelsh: String,
    notRegisteredToVoteEnglish: String,
    notRegisteredToVoteWelsh: String,
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
    rejectedSignatureWithReasonsEnglish = rejectedSignatureWithReasonsEnglish,
    rejectedSignatureWithReasonsWelsh = rejectedSignatureWithReasonsWelsh,
    requestedSignatureEnglish = requestedSignatureEnglish,
    requestedSignatureWelsh = requestedSignatureWelsh,
    ninoNotMatchedEnglish = ninoNotMatchedEnglish,
    ninoNotMatchedWelsh = ninoNotMatchedWelsh,
    ninoNotMatchedRestrictedDocumentsListEnglish = ninoNotMatchedRestrictedDocumentsListEnglish,
    ninoNotMatchedRestrictedDocumentsListWelsh = ninoNotMatchedRestrictedDocumentsListWelsh,
    rejectedParentGuardianEnglish = null,
    rejectedParentGuardianWelsh = null,
    rejectedPreviousAddressEnglish = null,
    rejectedPreviousAddressWelsh = null,
    parentGuardianProofRequiredEnglish = null,
    parentGuardianProofRequiredWelsh = null,
    previousAddressDocumentRequiredEnglish = null,
    previousAddressDocumentRequiredWelsh = null,
    bespokeCommEnglish = bespokeCommEnglish,
    bespokeCommWelsh = bespokeCommWelsh,
    notRegisteredToVoteEnglish = notRegisteredToVoteEnglish,
    notRegisteredToVoteWelsh = notRegisteredToVoteWelsh,
)

@ConfigurationProperties(prefix = "api.notify.template.proxy.email", ignoreUnknownFields = false)
class ProxyNotifyEmailTemplateConfiguration(
    sourceType: SourceType = SourceType.PROXY,
    receivedEnglish: String,
    receivedWelsh: String,
    rejectedSignatureEnglish: String,
    rejectedSignatureWelsh: String,
    rejectedSignatureWithReasonsEnglish: String,
    rejectedSignatureWithReasonsWelsh: String,
    requestedSignatureEnglish: String,
    requestedSignatureWelsh: String,
    rejectedDocumentEnglish: String,
    rejectedDocumentWelsh: String,
    ninoNotMatchedEnglish: String,
    ninoNotMatchedWelsh: String,
    ninoNotMatchedRestrictedDocumentsListEnglish: String,
    ninoNotMatchedRestrictedDocumentsListWelsh: String,
    bespokeCommEnglish: String,
    bespokeCommWelsh: String,
    notRegisteredToVoteEnglish: String,
    notRegisteredToVoteWelsh: String,
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
    rejectedSignatureWithReasonsEnglish = rejectedSignatureWithReasonsEnglish,
    rejectedSignatureWithReasonsWelsh = rejectedSignatureWithReasonsWelsh,
    requestedSignatureEnglish = requestedSignatureEnglish,
    requestedSignatureWelsh = requestedSignatureWelsh,
    ninoNotMatchedEnglish = ninoNotMatchedEnglish,
    ninoNotMatchedWelsh = ninoNotMatchedWelsh,
    ninoNotMatchedRestrictedDocumentsListEnglish = ninoNotMatchedRestrictedDocumentsListEnglish,
    ninoNotMatchedRestrictedDocumentsListWelsh = ninoNotMatchedRestrictedDocumentsListWelsh,
    rejectedParentGuardianEnglish = null,
    rejectedParentGuardianWelsh = null,
    rejectedPreviousAddressEnglish = null,
    rejectedPreviousAddressWelsh = null,
    parentGuardianProofRequiredEnglish = null,
    parentGuardianProofRequiredWelsh = null,
    previousAddressDocumentRequiredEnglish = null,
    previousAddressDocumentRequiredWelsh = null,
    bespokeCommEnglish = bespokeCommEnglish,
    bespokeCommWelsh = bespokeCommWelsh,
    notRegisteredToVoteEnglish = notRegisteredToVoteEnglish,
    notRegisteredToVoteWelsh = notRegisteredToVoteWelsh,
)

@ConfigurationProperties(prefix = "api.notify.template.overseas.email", ignoreUnknownFields = false)
class OverseasNotifyEmailTemplateConfiguration(
    sourceType: SourceType = SourceType.OVERSEAS,
    receivedEnglish: String,
    receivedWelsh: String,
    rejectedParentGuardianEnglish: String?,
    rejectedParentGuardianWelsh: String?,
    rejectedPreviousAddressEnglish: String?,
    rejectedPreviousAddressWelsh: String?,
    rejectedDocumentEnglish: String?,
    rejectedDocumentWelsh: String?,
    parentGuardianProofRequiredEnglish: String?,
    parentGuardianProofRequiredWelsh: String?,
    previousAddressDocumentRequiredEnglish: String?,
    previousAddressDocumentRequiredWelsh: String?,
    ninoNotMatchedEnglish: String,
    ninoNotMatchedWelsh: String,
    bespokeCommEnglish: String,
    bespokeCommWelsh: String,
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
    rejectedSignatureWithReasonsEnglish = null,
    rejectedSignatureWithReasonsWelsh = null,
    requestedSignatureEnglish = null,
    requestedSignatureWelsh = null,
    ninoNotMatchedWelsh = ninoNotMatchedWelsh,
    ninoNotMatchedEnglish = ninoNotMatchedEnglish,
    ninoNotMatchedRestrictedDocumentsListEnglish = null,
    ninoNotMatchedRestrictedDocumentsListWelsh = null,
    rejectedParentGuardianEnglish = rejectedParentGuardianEnglish,
    rejectedParentGuardianWelsh = rejectedParentGuardianWelsh,
    rejectedPreviousAddressEnglish = rejectedPreviousAddressEnglish,
    rejectedPreviousAddressWelsh = rejectedPreviousAddressWelsh,
    parentGuardianProofRequiredEnglish = parentGuardianProofRequiredEnglish,
    parentGuardianProofRequiredWelsh = parentGuardianProofRequiredWelsh,
    previousAddressDocumentRequiredEnglish = previousAddressDocumentRequiredEnglish,
    previousAddressDocumentRequiredWelsh = previousAddressDocumentRequiredWelsh,
    bespokeCommEnglish = bespokeCommEnglish,
    bespokeCommWelsh = bespokeCommWelsh,
    notRegisteredToVoteEnglish = null,
    notRegisteredToVoteWelsh = null,
)

@ConfigurationProperties(prefix = "api.notify.template.voter-card.email", ignoreUnknownFields = false)
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
    idDocumentRequiredWelsh: String,
    bespokeCommEnglish: String,
    bespokeCommWelsh: String,
    notRegisteredToVoteEnglish: String,
    notRegisteredToVoteWelsh: String,
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
    rejectedSignatureWithReasonsEnglish = null,
    rejectedSignatureWithReasonsWelsh = null,
    requestedSignatureEnglish = null,
    requestedSignatureWelsh = null,
    ninoNotMatchedEnglish = null,
    ninoNotMatchedWelsh = null,
    ninoNotMatchedRestrictedDocumentsListEnglish = null,
    ninoNotMatchedRestrictedDocumentsListWelsh = null,
    rejectedParentGuardianEnglish = null,
    rejectedParentGuardianWelsh = null,
    rejectedPreviousAddressEnglish = null,
    rejectedPreviousAddressWelsh = null,
    parentGuardianProofRequiredEnglish = null,
    parentGuardianProofRequiredWelsh = null,
    previousAddressDocumentRequiredEnglish = null,
    previousAddressDocumentRequiredWelsh = null,
    bespokeCommEnglish = bespokeCommEnglish,
    bespokeCommWelsh = bespokeCommWelsh,
    notRegisteredToVoteEnglish = notRegisteredToVoteEnglish,
    notRegisteredToVoteWelsh = notRegisteredToVoteWelsh,
)

data class NotifyLetterTemplateConfiguration(
    val voterCard: VoterCardNotifyLetterTemplateConfiguration,
    val postal: PostalNotifyLetterTemplateConfiguration,
    val proxy: ProxyNotifyLetterTemplateConfiguration,
    val overseas: OverseasNotifyLetterTemplateConfiguration,
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
    val rejectedSignatureWithReasonsEnglish: String?,
    val rejectedSignatureWithReasonsWelsh: String?,
    val requestedSignatureEnglish: String?,
    val requestedSignatureWelsh: String?,
    val ninoNotMatchedEnglish: String?,
    val ninoNotMatchedWelsh: String?,
    val ninoNotMatchedRestrictedDocumentsListEnglish: String?,
    val ninoNotMatchedRestrictedDocumentsListWelsh: String?,
    val rejectedParentGuardianEnglish: String?,
    val rejectedParentGuardianWelsh: String?,
    val rejectedPreviousAddressEnglish: String?,
    val rejectedPreviousAddressWelsh: String?,
    val parentGuardianProofRequiredEnglish: String?,
    val parentGuardianProofRequiredWelsh: String?,
    val previousAddressDocumentRequiredEnglish: String?,
    val previousAddressDocumentRequiredWelsh: String?,
    val bespokeCommEnglish: String?,
    val bespokeCommWelsh: String?,
    val notRegisteredToVoteEnglish: String?,
    val notRegisteredToVoteWelsh: String?,
)

@ConfigurationProperties(prefix = "api.notify.template.voter-card.letter", ignoreUnknownFields = false)
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
    idDocumentRequiredWelsh: String,
    bespokeCommEnglish: String,
    bespokeCommWelsh: String,
    notRegisteredToVoteEnglish: String,
    notRegisteredToVoteWelsh: String,
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
    rejectedSignatureWithReasonsEnglish = null,
    rejectedSignatureWithReasonsWelsh = null,
    requestedSignatureEnglish = null,
    requestedSignatureWelsh = null,
    ninoNotMatchedWelsh = null,
    ninoNotMatchedEnglish = null,
    ninoNotMatchedRestrictedDocumentsListEnglish = null,
    ninoNotMatchedRestrictedDocumentsListWelsh = null,
    rejectedParentGuardianEnglish = null,
    rejectedParentGuardianWelsh = null,
    rejectedPreviousAddressEnglish = null,
    rejectedPreviousAddressWelsh = null,
    parentGuardianProofRequiredEnglish = null,
    parentGuardianProofRequiredWelsh = null,
    previousAddressDocumentRequiredEnglish = null,
    previousAddressDocumentRequiredWelsh = null,
    bespokeCommEnglish = bespokeCommEnglish,
    bespokeCommWelsh = bespokeCommWelsh,
    notRegisteredToVoteEnglish = notRegisteredToVoteEnglish,
    notRegisteredToVoteWelsh = notRegisteredToVoteWelsh,
)

@ConfigurationProperties(prefix = "api.notify.template.postal.letter", ignoreUnknownFields = false)
class PostalNotifyLetterTemplateConfiguration(
    sourceType: SourceType = SourceType.POSTAL,
    rejectedDocumentEnglish: String,
    rejectedDocumentWelsh: String,
    rejectedSignatureEnglish: String,
    rejectedSignatureWelsh: String,
    rejectedSignatureWithReasonsEnglish: String,
    rejectedSignatureWithReasonsWelsh: String,
    requestedSignatureEnglish: String,
    requestedSignatureWelsh: String,
    ninoNotMatchedEnglish: String?,
    ninoNotMatchedWelsh: String?,
    ninoNotMatchedRestrictedDocumentsListEnglish: String,
    ninoNotMatchedRestrictedDocumentsListWelsh: String,
    bespokeCommEnglish: String,
    bespokeCommWelsh: String,
    notRegisteredToVoteEnglish: String,
    notRegisteredToVoteWelsh: String,
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
    rejectedSignatureEnglish = rejectedSignatureEnglish,
    rejectedSignatureWelsh = rejectedSignatureWelsh,
    rejectedSignatureWithReasonsEnglish = rejectedSignatureWithReasonsEnglish,
    rejectedSignatureWithReasonsWelsh = rejectedSignatureWithReasonsWelsh,
    requestedSignatureEnglish = requestedSignatureEnglish,
    requestedSignatureWelsh = requestedSignatureWelsh,
    ninoNotMatchedEnglish = ninoNotMatchedEnglish,
    ninoNotMatchedWelsh = ninoNotMatchedWelsh,
    ninoNotMatchedRestrictedDocumentsListEnglish = ninoNotMatchedRestrictedDocumentsListEnglish,
    ninoNotMatchedRestrictedDocumentsListWelsh = ninoNotMatchedRestrictedDocumentsListWelsh,
    rejectedParentGuardianEnglish = null,
    rejectedParentGuardianWelsh = null,
    rejectedPreviousAddressEnglish = null,
    rejectedPreviousAddressWelsh = null,
    parentGuardianProofRequiredEnglish = null,
    parentGuardianProofRequiredWelsh = null,
    previousAddressDocumentRequiredEnglish = null,
    previousAddressDocumentRequiredWelsh = null,
    bespokeCommEnglish = bespokeCommEnglish,
    bespokeCommWelsh = bespokeCommWelsh,
    notRegisteredToVoteEnglish = notRegisteredToVoteEnglish,
    notRegisteredToVoteWelsh = notRegisteredToVoteWelsh,
)

@ConfigurationProperties(prefix = "api.notify.template.proxy.letter", ignoreUnknownFields = false)
class ProxyNotifyLetterTemplateConfiguration(
    sourceType: SourceType = SourceType.PROXY,
    rejectedSignatureEnglish: String,
    rejectedSignatureWelsh: String,
    rejectedSignatureWithReasonsEnglish: String,
    rejectedSignatureWithReasonsWelsh: String,
    requestedSignatureEnglish: String,
    requestedSignatureWelsh: String,
    rejectedDocumentEnglish: String,
    rejectedDocumentWelsh: String,
    ninoNotMatchedWelsh: String?,
    ninoNotMatchedEnglish: String?,
    ninoNotMatchedRestrictedDocumentsListEnglish: String,
    ninoNotMatchedRestrictedDocumentsListWelsh: String,
    bespokeCommEnglish: String,
    bespokeCommWelsh: String,
    notRegisteredToVoteEnglish: String,
    notRegisteredToVoteWelsh: String,
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
    rejectedSignatureWithReasonsEnglish = rejectedSignatureWithReasonsEnglish,
    rejectedSignatureWithReasonsWelsh = rejectedSignatureWithReasonsWelsh,
    requestedSignatureEnglish = requestedSignatureEnglish,
    requestedSignatureWelsh = requestedSignatureWelsh,
    ninoNotMatchedEnglish = ninoNotMatchedEnglish,
    ninoNotMatchedWelsh = ninoNotMatchedWelsh,
    ninoNotMatchedRestrictedDocumentsListEnglish = ninoNotMatchedRestrictedDocumentsListEnglish,
    ninoNotMatchedRestrictedDocumentsListWelsh = ninoNotMatchedRestrictedDocumentsListWelsh,
    rejectedParentGuardianEnglish = null,
    rejectedParentGuardianWelsh = null,
    rejectedPreviousAddressEnglish = null,
    rejectedPreviousAddressWelsh = null,
    parentGuardianProofRequiredEnglish = null,
    parentGuardianProofRequiredWelsh = null,
    previousAddressDocumentRequiredEnglish = null,
    previousAddressDocumentRequiredWelsh = null,
    bespokeCommEnglish = bespokeCommEnglish,
    bespokeCommWelsh = bespokeCommWelsh,
    notRegisteredToVoteEnglish = notRegisteredToVoteEnglish,
    notRegisteredToVoteWelsh = notRegisteredToVoteWelsh,
)

@ConfigurationProperties(prefix = "api.notify.template.overseas.letter", ignoreUnknownFields = false)
class OverseasNotifyLetterTemplateConfiguration(
    sourceType: SourceType = SourceType.OVERSEAS,
    rejectedParentGuardianEnglish: String?,
    rejectedParentGuardianWelsh: String?,
    rejectedPreviousAddressEnglish: String?,
    rejectedPreviousAddressWelsh: String?,
    rejectedDocumentEnglish: String?,
    rejectedDocumentWelsh: String?,
    parentGuardianProofRequiredEnglish: String?,
    parentGuardianProofRequiredWelsh: String?,
    previousAddressDocumentRequiredEnglish: String?,
    previousAddressDocumentRequiredWelsh: String?,
    ninoNotMatchedEnglish: String,
    ninoNotMatchedWelsh: String,
    bespokeCommEnglish: String,
    bespokeCommWelsh: String,
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
    rejectedSignatureWithReasonsEnglish = null,
    rejectedSignatureWithReasonsWelsh = null,
    requestedSignatureEnglish = null,
    requestedSignatureWelsh = null,
    ninoNotMatchedWelsh = ninoNotMatchedWelsh,
    ninoNotMatchedEnglish = ninoNotMatchedEnglish,
    ninoNotMatchedRestrictedDocumentsListEnglish = null,
    ninoNotMatchedRestrictedDocumentsListWelsh = null,
    rejectedParentGuardianEnglish = rejectedParentGuardianEnglish,
    rejectedParentGuardianWelsh = rejectedParentGuardianWelsh,
    rejectedPreviousAddressEnglish = rejectedPreviousAddressEnglish,
    rejectedPreviousAddressWelsh = rejectedPreviousAddressWelsh,
    parentGuardianProofRequiredEnglish = parentGuardianProofRequiredEnglish,
    parentGuardianProofRequiredWelsh = parentGuardianProofRequiredWelsh,
    previousAddressDocumentRequiredEnglish = previousAddressDocumentRequiredEnglish,
    previousAddressDocumentRequiredWelsh = previousAddressDocumentRequiredWelsh,
    bespokeCommEnglish = bespokeCommEnglish,
    bespokeCommWelsh = bespokeCommWelsh,
    notRegisteredToVoteEnglish = null,
    notRegisteredToVoteWelsh = null,
)
