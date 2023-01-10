package uk.gov.dluhc.notificationsapi.database.mapper

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.given
import org.mockito.kotlin.verify
import uk.gov.dluhc.notificationsapi.database.entity.NotificationType
import uk.gov.dluhc.notificationsapi.dto.NotificationDestinationDto
import uk.gov.dluhc.notificationsapi.dto.NotificationType.PHOTO_RESUBMISSION
import uk.gov.dluhc.notificationsapi.dto.PostalAddress
import uk.gov.dluhc.notificationsapi.dto.SendNotificationResponseDto
import uk.gov.dluhc.notificationsapi.dto.SourceType
import uk.gov.dluhc.notificationsapi.mapper.NotificationTypeMapper
import uk.gov.dluhc.notificationsapi.mapper.SourceTypeMapper
import uk.gov.dluhc.notificationsapi.testsupport.testdata.DataFaker.Companion.faker
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aGssCode
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aLocalDateTime
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aNotificationId
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aRequestor
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aSourceReference
import uk.gov.dluhc.notificationsapi.testsupport.testdata.anEmailAddress
import uk.gov.dluhc.notificationsapi.testsupport.testdata.database.entity.aNotifyDetails
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.aNotifySendSuccessResponseBody
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.aNotifySendSuccessResponseSubject
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.aNotifySendSuccessResponseTemplateUri
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.aNotifySendSuccessResponseTemplateVersion
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.aSendNotificationDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.aTemplateId
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildPhotoPersonalisationDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildPhotoPersonalisationMapFromDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildSendNotificationRequestDto
import uk.gov.dluhc.notificationsapi.database.entity.SourceType as SourceTypeEntityEnum

@ExtendWith(MockitoExtension::class)
internal class NotificationMapperTest {

    @InjectMocks
    private lateinit var mapper: NotificationMapperImpl

    @Mock
    private lateinit var notificationTypeMapper: NotificationTypeMapper

    @Mock
    private lateinit var sourceTypeMapper: SourceTypeMapper

    @Test
    fun `should map to NotifyDetails`() {
        // Given
        val notificationId = aNotificationId()
        val reference = aSourceReference()
        val templateId = aTemplateId()
        val templateVersion = aNotifySendSuccessResponseTemplateVersion()
        val templateUri = aNotifySendSuccessResponseTemplateUri(templateId)
        val body = aNotifySendSuccessResponseBody()
        val subject = aNotifySendSuccessResponseSubject()
        val fromEmail = anEmailAddress()

        val sendNotificationResponseDto = SendNotificationResponseDto(
            notificationId,
            reference,
            templateId,
            templateVersion,
            templateUri,
            body,
            subject,
            fromEmail
        )

        // When
        val actual = mapper.toNotifyDetails(sendNotificationResponseDto)

        // Then
        assertThat(actual.notificationId).isEqualTo(notificationId)
        assertThat(actual.reference).isEqualTo(reference)
        assertThat(actual.templateId).isEqualTo(templateId)
        assertThat(actual.templateVersion).isEqualTo(templateVersion)
        assertThat(actual.templateUri).isEqualTo(templateUri)
        assertThat(actual.body).isEqualTo(body)
        assertThat(actual.subject).isEqualTo(subject)
        assertThat(actual.fromEmail).isEqualTo(fromEmail)
    }

    @Test
    fun `should map to PostalAddress`() {
        // Given
        val addressee = faker.name().firstName()
        val property = faker.address().buildingNumber()
        val street = faker.address().streetName()
        val town = faker.address().streetName()
        val area = faker.address().city()
        val locality = faker.address().state()
        val postcode = faker.address().postcode()

        val postalAddressDto = PostalAddress(
            addressee = addressee,
            property = property,
            street = street,
            town = town,
            area = area,
            locality = locality,
            postcode = postcode,
        )

        // When
        val actual = mapper.toPostalAddress(postalAddressDto)

        // Then
        assertThat(actual.addressee).isEqualTo(addressee)
        assertThat(actual.property).isEqualTo(property)
        assertThat(actual.street).isEqualTo(street)
        assertThat(actual.town).isEqualTo(town)
        assertThat(actual.area).isEqualTo(area)
        assertThat(actual.locality).isEqualTo(locality)
        assertThat(actual.postcode).isEqualTo(postcode)
    }

    @Test
    fun `should create notification`() {
        // Given
        val notificationId = aNotificationId()
        val gssCode = aGssCode()
        val requestor = aRequestor()
        val sourceType = SourceType.VOTER_CARD
        val expectedSourceType = SourceTypeEntityEnum.VOTER_CARD
        val sourceReference = aSourceReference()
        val emailAddress = anEmailAddress()
        val notificationType = PHOTO_RESUBMISSION
        val expectedNotificationType = NotificationType.PHOTO_RESUBMISSION
        val personalisationDto = buildPhotoPersonalisationDto()
        val request = buildSendNotificationRequestDto(
            gssCode = gssCode,
            requestor = requestor,
            sourceType = sourceType,
            sourceReference = sourceReference,
            toAddress = NotificationDestinationDto(emailAddress = anEmailAddress(), postalAddress = null),
            notificationType = notificationType,
        )
        val personalisationMap = buildPhotoPersonalisationMapFromDto(personalisationDto)
        val sendNotificationResponseDto = aSendNotificationDto()
        val expectedNotifyDetails = aNotifyDetails(sendNotificationResponseDto)
        val sentAt = aLocalDateTime()

        given(notificationTypeMapper.toNotificationTypeEntity(any())).willReturn(NotificationType.PHOTO_RESUBMISSION)
        given(sourceTypeMapper.toSourceTypeEntity(any())).willReturn(expectedSourceType)

        // When
        val notification = mapper.createNotification(notificationId, request, personalisationMap, sendNotificationResponseDto, sentAt)

        // Then
        assertThat(notification.id).isEqualTo(notificationId)
        assertThat(notification.type).isEqualTo(expectedNotificationType)
        assertThat(notification.gssCode).isEqualTo(gssCode)
        assertThat(notification.requestor).isEqualTo(requestor)
        assertThat(notification.sourceType).isEqualTo(expectedSourceType)
        assertThat(notification.sourceReference).isEqualTo(sourceReference)
        assertThat(notification.toEmail).isEqualTo(emailAddress)
        assertThat(notification.personalisation).isEqualTo(personalisationMap)
        assertThat(notification.notifyDetails).isEqualTo(expectedNotifyDetails)
        assertThat(notification.sentAt).isEqualTo(sentAt)

        verify(notificationTypeMapper).toNotificationTypeEntity(PHOTO_RESUBMISSION)
        verify(sourceTypeMapper).toSourceTypeEntity(SourceType.VOTER_CARD)
    }
}
