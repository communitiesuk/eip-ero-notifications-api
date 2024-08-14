package uk.gov.dluhc.notificationsapi.testsupport.testdata.api

import uk.gov.dluhc.notificationsapi.models.CommunicationChannel
import uk.gov.dluhc.notificationsapi.models.ContactDetails
import uk.gov.dluhc.notificationsapi.models.GenerateInviteToRegisterTemplatePreviewRequest
import uk.gov.dluhc.notificationsapi.models.InviteToRegisterPersonalisation
import uk.gov.dluhc.notificationsapi.models.Language
import uk.gov.dluhc.notificationsapi.models.SourceType
import uk.gov.dluhc.notificationsapi.testsupport.testdata.DataFaker
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aValidApplicationReference
import uk.gov.dluhc.notificationsapi.testsupport.testdata.models.buildEroContactDetails

fun buildGenerateInviteToRegisterTemplatePreviewRequest(
    sourceType: SourceType = SourceType.POSTAL,
    channel: CommunicationChannel = CommunicationChannel.EMAIL,
    personalisation: InviteToRegisterPersonalisation = buildInviteToRegisterPersonalisation(),
    language: Language? = Language.EN,
) = GenerateInviteToRegisterTemplatePreviewRequest(
    channel = channel,
    sourceType = sourceType,
    language = language,
    personalisation = personalisation,
)

fun buildInviteToRegisterPersonalisation(
    applicationReference: String = aValidApplicationReference(),
    firstName: String = DataFaker.faker.name().firstName(),
    eroContactDetails: ContactDetails = buildEroContactDetails(),
    freeText: String? = DataFaker.faker.yoda().quote(),
): InviteToRegisterPersonalisation =
    InviteToRegisterPersonalisation(
        applicationReference = applicationReference,
        firstName = firstName,
        eroContactDetails = eroContactDetails,
        freeText = freeText
    )
