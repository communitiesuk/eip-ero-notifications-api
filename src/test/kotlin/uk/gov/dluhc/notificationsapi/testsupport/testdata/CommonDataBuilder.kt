package uk.gov.dluhc.notificationsapi.testsupport.testdata

import org.apache.commons.lang3.RandomStringUtils
import uk.gov.dluhc.notificationsapi.dto.CommunicationChannel
import uk.gov.dluhc.notificationsapi.dto.NotificationType
import uk.gov.dluhc.notificationsapi.dto.OverseasElectorAddress
import uk.gov.dluhc.notificationsapi.dto.PostalAddress
import uk.gov.dluhc.notificationsapi.dto.SourceType
import uk.gov.dluhc.notificationsapi.testsupport.testdata.DataFaker.Companion.faker
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID

fun anEmailAddress() = "user@email.com"

fun aRequestor() = "user-id"

fun aGssCode() = "E99999999"

fun anotherGssCode() = "E88888888"

fun aSourceReference() = "fea5d37b-5c4a-445c-b428-7dc799be1d8e"

fun aRandomSourceReference() = UUID.randomUUID().toString()

fun aSourceType() = SourceType.VOTER_CARD

fun aNotificationAuditId(): UUID = UUID.randomUUID()

fun aNotificationId(): UUID = UUID.fromString("3efbf304-1c47-453d-87f7-6bf5efe0495f")

fun aRandomNotificationId(): UUID = UUID.randomUUID()

fun aNotificationType() = NotificationType.APPLICATION_APPROVED

fun aCommunicationChannel() = CommunicationChannel.EMAIL

fun aNotificationPersonalisationMap(): Map<String, String> = mapOf(
    "subject_param" to "test subject",
    "name_param" to "John",
    "custom_title" to "Resubmitting photo",
    "date" to "15/Oct/2022",
)

fun aCommunicationConfirmationId(): UUID = UUID.fromString("fa7d3cc2-2983-4d7e-b675-57d882003ecf")

fun aRandomCommunicationConfirmationId(): UUID = UUID.randomUUID()

fun aLocalDateTime(): LocalDateTime = LocalDateTime.of(2022, 10, 6, 9, 58, 24)

fun anOffsetDateTime(): OffsetDateTime = OffsetDateTime.of(2022, 10, 6, 9, 58, 24, 0, ZoneOffset.UTC)

fun aValidApplicationReference(): String = "V${RandomStringUtils.secure().nextAlphabetic(9).uppercase()}"

fun getAValidPostcode() = RandomStringUtils.secure().next(2, "ABEHW") + RandomStringUtils.secure().nextNumeric(2) + RandomStringUtils.secure().next(2, "ABEHW")

fun aPostalAddress(
    addressee: String = faker.name().firstName(),
    property: String? = faker.address().buildingNumber(),
    street: String = faker.address().streetName(),
    town: String? = faker.address().streetName(),
    area: String? = faker.address().city(),
    locality: String? = faker.address().state(),
    postcode: String = faker.address().postcode(),
) = PostalAddress(
    addressee = addressee,
    property = property,
    street = street,
    town = town,
    area = area,
    locality = locality,
    postcode = postcode,
)

fun anOverseasAddress(
    addressee: String = faker.name().firstName(),
    addressLine1: String = faker.address().streetName(),
    addressLine2: String? = faker.address().buildingNumber(),
    addressLine3: String? = faker.address().streetName(),
    addressLine4: String? = faker.address().city(),
    addressLine5: String? = faker.address().state(),
    country: String = faker.address().country(),
): OverseasElectorAddress = OverseasElectorAddress(
    addressee = addressee,
    addressLine1 = addressLine1,
    addressLine2 = addressLine2,
    addressLine3 = addressLine3,
    addressLine4 = addressLine4,
    addressLine5 = addressLine5,
    country = country,
)
