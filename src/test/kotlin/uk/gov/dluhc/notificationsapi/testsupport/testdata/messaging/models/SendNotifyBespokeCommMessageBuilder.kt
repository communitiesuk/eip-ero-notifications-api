package uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models

import uk.gov.dluhc.notificationsapi.messaging.models.*
import uk.gov.dluhc.notificationsapi.testsupport.testdata.*
import uk.gov.dluhc.notificationsapi.testsupport.testdata.DataFaker.Companion.faker
import java.time.LocalDate

fun buildSendNotifyBespokeCommMessage(
    language: Language = Language.EN,
    sourceType: SourceType = SourceType.POSTAL,
    sourceReference: String = aSourceReference(),
    gssCode: String = aGssCode(),
    requestor: String = aRequestor(),
    channel: CommunicationChannel = CommunicationChannel.EMAIL,
    toAddress: MessageAddress = aMessageAddress(),
    personalisation: BespokeCommPersonalisation = buildBespokeCommPersonalisation()
): SendNotifyBespokeCommMessage =
    SendNotifyBespokeCommMessage(
        language = language,
        sourceType = sourceType,
        sourceReference = sourceReference,
        gssCode = gssCode,
        requestor = requestor,
        messageType = MessageType.BESPOKE_MINUS_COMM,
        personalisation = personalisation,
        channel = channel,
        toAddress = toAddress,
    )

fun buildBespokeCommPersonalisation(
    applicationReference: String = aValidApplicationReference(),
    firstName: String = faker.name().firstName(),
    eroContactDetails: ContactDetails = buildContactDetailsMessage(),
    subjectHeader: String = faker.yoda().quote(),
    details: String = faker.yoda().quote(),
    whatToDo: String? = faker.yoda().quote(),
    deadlineDate: LocalDate? = LocalDate.now().plusMonths(3),
    deadlineTime: String? = "17:00"
): BespokeCommPersonalisation =
    BespokeCommPersonalisation(
        applicationReference = applicationReference,
        firstName = firstName,
        eroContactDetails = eroContactDetails,
        subjectHeader = subjectHeader,
        details = details,
        whatToDo = whatToDo,
        deadlineDate = deadlineDate,
        deadlineTime = deadlineTime
    )
