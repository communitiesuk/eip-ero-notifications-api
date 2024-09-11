package uk.gov.dluhc.notificationsapi.testsupport.testdata.api

import uk.gov.dluhc.notificationsapi.models.CommunicationChannel
import uk.gov.dluhc.notificationsapi.models.ContactDetails
import uk.gov.dluhc.notificationsapi.models.GenerateNotRegisteredToVoteTemplatePreviewRequest
import uk.gov.dluhc.notificationsapi.models.Language
import uk.gov.dluhc.notificationsapi.models.NotRegisteredToVotePersonalisation
import uk.gov.dluhc.notificationsapi.models.SourceType
import uk.gov.dluhc.notificationsapi.testsupport.testdata.DataFaker
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aValidApplicationReference
import uk.gov.dluhc.notificationsapi.testsupport.testdata.models.buildEroContactDetails

fun buildGenerateNotRegisteredToVoteTemplatePreviewRequest(
    sourceType: SourceType = SourceType.POSTAL,
    channel: CommunicationChannel = CommunicationChannel.EMAIL,
    personalisation: NotRegisteredToVotePersonalisation = buildNotRegisteredToVotePersonalisation(),
    language: Language? = Language.EN,
) = GenerateNotRegisteredToVoteTemplatePreviewRequest(
    channel = channel,
    sourceType = sourceType,
    language = language,
    personalisation = personalisation,
)

fun buildNotRegisteredToVotePersonalisation(
    applicationReference: String = aValidApplicationReference(),
    firstName: String = DataFaker.faker.name().firstName(),
    eroContactDetails: ContactDetails = buildEroContactDetails(),
    freeText: String? = DataFaker.faker.yoda().quote(),
    property: String? = DataFaker.faker.address().buildingNumber(),
    street: String? = DataFaker.faker.address().streetName(),
    town: String? = DataFaker.faker.address().city(),
    area: String? = DataFaker.faker.address().city(),
    locality: String? = DataFaker.faker.address().city(),
    postcode: String? = DataFaker.faker.address().postcode(),
): NotRegisteredToVotePersonalisation =
    NotRegisteredToVotePersonalisation(
        applicationReference = applicationReference,
        firstName = firstName,
        eroContactDetails = eroContactDetails,
        freeText = freeText,
        property = property,
        street = street,
        town = town,
        area = area,
        locality = locality,
        postcode = postcode,
    )
