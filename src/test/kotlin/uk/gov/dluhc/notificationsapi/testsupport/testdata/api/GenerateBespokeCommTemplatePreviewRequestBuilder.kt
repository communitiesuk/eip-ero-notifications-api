package uk.gov.dluhc.notificationsapi.testsupport.testdata.api

import uk.gov.dluhc.notificationsapi.models.BespokeCommPersonalisation
import uk.gov.dluhc.notificationsapi.models.CommunicationChannel
import uk.gov.dluhc.notificationsapi.models.ContactDetails
import uk.gov.dluhc.notificationsapi.models.GenerateBespokeCommTemplatePreviewRequest
import uk.gov.dluhc.notificationsapi.models.Language
import uk.gov.dluhc.notificationsapi.models.SourceType
import uk.gov.dluhc.notificationsapi.testsupport.testdata.DataFaker
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aValidApplicationReference
import uk.gov.dluhc.notificationsapi.testsupport.testdata.models.buildEroContactDetails
import java.time.LocalDate

fun buildGenerateBespokeCommTemplatePreviewRequest(
    sourceType: SourceType = SourceType.POSTAL,
    channel: CommunicationChannel = CommunicationChannel.EMAIL,
    personalisation: BespokeCommPersonalisation = buildBespokeCommPersonalisation(),
    language: Language? = Language.EN,
) = GenerateBespokeCommTemplatePreviewRequest(
    channel = channel,
    sourceType = sourceType,
    language = language,
    personalisation = personalisation,
)

fun buildBespokeCommPersonalisation(
    applicationReference: String = aValidApplicationReference(),
    firstName: String = DataFaker.faker.name().firstName(),
    eroContactDetails: ContactDetails = buildEroContactDetails(),
    subjectHeader: String = DataFaker.faker.yoda().quote(),
    details: String = DataFaker.faker.yoda().quote(),
    whatToDo: String? = DataFaker.faker.yoda().quote(),
    deadlineDate: LocalDate? = LocalDate.now().plusMonths(3),
    deadlineTime: String? = "17:00",
): BespokeCommPersonalisation =
    BespokeCommPersonalisation(
        applicationReference = applicationReference,
        firstName = firstName,
        eroContactDetails = eroContactDetails,
        subjectHeader = subjectHeader,
        details = details,
        whatToDo = whatToDo,
        deadlineDate = deadlineDate,
        deadlineTime = deadlineTime,
    )
