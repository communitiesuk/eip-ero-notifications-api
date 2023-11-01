package uk.gov.dluhc.notificationsapi.testsupport.testdata.api

import uk.gov.dluhc.notificationsapi.models.ContactDetails
import uk.gov.dluhc.notificationsapi.models.GenerateNinoNotMatchedTemplatePreviewRequest
import uk.gov.dluhc.notificationsapi.models.Language
import uk.gov.dluhc.notificationsapi.models.NinoNotMatchedPersonalisation
import uk.gov.dluhc.notificationsapi.models.NotificationChannel
import uk.gov.dluhc.notificationsapi.models.SourceType
import uk.gov.dluhc.notificationsapi.testsupport.testdata.DataFaker
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aValidApplicationReference
import uk.gov.dluhc.notificationsapi.testsupport.testdata.models.buildEroContactDetails

fun buildGenerateNinoNotMatchedTemplatePreviewRequest(
    sourceType: SourceType = SourceType.POSTAL,
    channel: NotificationChannel = NotificationChannel.EMAIL,
    personalisation: NinoNotMatchedPersonalisation = buildNinoNotMatchedPersonalisation(),
    language: Language? = Language.EN,
    isSpecialCategoryElector: Boolean = false,
) = GenerateNinoNotMatchedTemplatePreviewRequest(
    channel = channel,
    sourceType = sourceType,
    language = language,
    personalisation = personalisation,
    isSpecialCategoryElector = isSpecialCategoryElector,
)

fun buildNinoNotMatchedPersonalisation(
    applicationReference: String = aValidApplicationReference(),
    firstName: String = DataFaker.faker.name().firstName(),
    eroContactDetails: ContactDetails = buildEroContactDetails(),
    additionalNotes: String? = null
) = NinoNotMatchedPersonalisation(
    applicationReference = applicationReference,
    firstName = firstName,
    eroContactDetails = eroContactDetails,
    additionalNotes = additionalNotes
)
