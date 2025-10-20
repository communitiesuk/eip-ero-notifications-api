package uk.gov.dluhc.notificationsapi.mapper

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import uk.gov.dluhc.notificationsapi.dto.mapToPersonalisation
import uk.gov.dluhc.notificationsapi.messaging.models.BasePersonalisation
import uk.gov.dluhc.notificationsapi.messaging.models.Language
import uk.gov.dluhc.notificationsapi.messaging.models.SourceType

@Component
class SignatureReceivedPersonalisationMapper {

    @Autowired
    private lateinit var eroContactDetailsMapper: EroContactDetailsMapper

    @Autowired
    private lateinit var sourceTypeMapper: SourceTypeMapper

    @Autowired
    private lateinit var languageMapper: LanguageMapper

    fun fromMessagePersonalisationToBasePersonalisationMap(
        messagePersonalisation: BasePersonalisation,
        sourceType: SourceType,
        language: Language,
    ): Map<String, Any> {
        val personalisation = mutableMapOf<String, Any>()

        val languageDto = language.let(languageMapper::fromMessageToDto)
        val fullSourceTypeString = sourceTypeMapper.toFullSourceTypeString(
            sourceType,
            languageDto,
        )

        with(messagePersonalisation) {
            personalisation["applicationReference"] = applicationReference
            personalisation["firstName"] = firstName
            with(mutableMapOf<String, String>()) {
                eroContactDetails.let(eroContactDetailsMapper::fromSqsToDto).mapToPersonalisation(this)
                personalisation.putAll(this)
            }
            personalisation["fullSourceType"] = fullSourceTypeString
        }

        return personalisation
    }
}
