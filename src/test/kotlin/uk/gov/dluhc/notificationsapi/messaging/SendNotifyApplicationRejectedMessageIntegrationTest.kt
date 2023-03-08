package uk.gov.dluhc.notificationsapi.messaging

import org.assertj.core.api.Assertions.assertThat
import org.awaitility.kotlin.await
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import uk.gov.dluhc.notificationsapi.config.IntegrationTest
import uk.gov.dluhc.notificationsapi.database.entity.Channel
import uk.gov.dluhc.notificationsapi.database.entity.Notification
import uk.gov.dluhc.notificationsapi.database.entity.NotifyDetails
import uk.gov.dluhc.notificationsapi.database.entity.SourceType.VOTER_CARD
import uk.gov.dluhc.notificationsapi.dto.LanguageDto
import uk.gov.dluhc.notificationsapi.dto.NotificationChannel
import uk.gov.dluhc.notificationsapi.dto.NotificationDestinationDto
import uk.gov.dluhc.notificationsapi.dto.NotificationType
import uk.gov.dluhc.notificationsapi.dto.PostalAddress
import uk.gov.dluhc.notificationsapi.dto.SendNotificationRequestDto
import uk.gov.dluhc.notificationsapi.dto.SourceType
import uk.gov.dluhc.notificationsapi.messaging.models.Language
import uk.gov.dluhc.notificationsapi.messaging.models.Language.CY
import uk.gov.dluhc.notificationsapi.messaging.models.SendNotifyApplicationRejectedMessage
import uk.gov.dluhc.notificationsapi.testsupport.model.LetterTemplate
import uk.gov.dluhc.notificationsapi.testsupport.model.NotifySendLetterSuccessResponse
import uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models.buildSendNotifyApplicationRejectedMessage
import java.time.Duration
import java.util.UUID
import java.util.concurrent.TimeUnit

internal class SendNotifyApplicationRejectedMessageIntegrationTest : IntegrationTest() {
    @ParameterizedTest
    @CsvSource(
        value = [
            "CY, WELSH",
            "EN, ENGLISH"
        ]
    )
    fun `should process rejected message`(language: Language, languageDto: LanguageDto) {
        // Given
        val templateId =
            if (language == CY) applicationRejectedLetterWelshTemplateId else applicationRejectedLetterEnglishTemplateId

        val payload = buildSendNotifyApplicationRejectedMessage(language = language)
        val expectedRequest = getExpectedRequest(payload, languageDto)
        val expectedPersonalisation = getExpectedPersonalisationMap(payload)

        // When
        sqsMessagingTemplate.convertAndSend(sendUkGovNotifyApplicationRejectedQueueName, payload)
        val notifyResponse = NotifySendLetterSuccessResponse(template = LetterTemplate(id = templateId))
        wireMockService.stubNotifySendLetterResponse(notifyResponse)

        // Then
        await.pollDelay(Duration.ofMillis(500)).atMost(5, TimeUnit.SECONDS).untilAsserted {
            wireMockService.verifyNotifySendLetter(templateId, expectedRequest, expectedPersonalisation)
            assertNotificationPersisted(payload, expectedPersonalisation, notifyResponse)
        }
    }

    private fun getExpectedRequest(
        payload: SendNotifyApplicationRejectedMessage,
        languageDto: LanguageDto
    ) = with(payload) {
        SendNotificationRequestDto(
            channel = NotificationChannel.LETTER,
            notificationType = NotificationType.APPLICATION_REJECTED,
            language = languageDto,
            gssCode = gssCode,
            requestor = requestor,
            sourceType = SourceType.VOTER_CARD,
            sourceReference = sourceReference,
            toAddress = with(toAddress) {
                NotificationDestinationDto(
                    emailAddress = emailAddress,
                    postalAddress = with(postalAddress!!.address!!) {
                        PostalAddress(
                            addressee = payload.toAddress.postalAddress!!.addressee!!,
                            property = property,
                            street = street,
                            town = town,
                            area = area,
                            locality = locality,
                            postcode = postcode
                        )
                    }
                )
            }
        )
    }

    private fun getExpectedPersonalisationMap(payload: SendNotifyApplicationRejectedMessage): Map<String, Any> =
        with(payload.personalisation) {
            mapOf(
                "applicationReference" to applicationReference,
                "firstName" to firstName,
                "rejectionReasonList" to mutableListOf(
                    "Your application was incomplete",
                    "You did not respond to our requests for information within the timeframe we gave you",
                    "Other"
                ),
                "rejectionReasonMessage" to (rejectionReasonMessage ?: ""),
                "LAName" to eroContactDetails.localAuthorityName,
                "eroWebsite" to eroContactDetails.website,
                "eroEmail" to eroContactDetails.email,
                "eroPhone" to eroContactDetails.phone,
                "eroAddressLine1" to (eroContactDetails.address.property ?: ""),
                "eroAddressLine2" to eroContactDetails.address.street,
                "eroAddressLine3" to (eroContactDetails.address.town ?: ""),
                "eroAddressLine4" to (eroContactDetails.address.area ?: ""),
                "eroAddressLine5" to (eroContactDetails.address.locality ?: ""),
                "eroPostcode" to eroContactDetails.address.postcode
            )
        }

    private fun assertNotificationPersisted(
        payload: SendNotifyApplicationRejectedMessage,
        expectedPersonalisation: Map<String, Any>,
        notifyResponse: NotifySendLetterSuccessResponse
    ) {
        val actualEntity = notificationRepository.getBySourceReferenceAndGssCode(
            payload.sourceReference,
            VOTER_CARD,
            listOf(payload.gssCode)
        )

        assertThat(actualEntity).hasSize(1)
        assertNotificationDetails(actualEntity[0], payload, expectedPersonalisation, notifyResponse)
    }

    private fun assertNotificationDetails(
        actual: Notification,
        payload: SendNotifyApplicationRejectedMessage,
        expectedPersonalisation: Map<String, Any>,
        notifyResponse: NotifySendLetterSuccessResponse
    ) {
        assertThat(actual.id).isNotNull
        assertThat(actual.sourceReference).isEqualTo(payload.sourceReference)
        assertThat(actual.gssCode).isEqualTo(payload.gssCode)
        assertThat(actual.type.toString()).isEqualTo(payload.messageType.toString().replace("_MINUS", ""))
        assertThat(actual.channel).isEqualTo(Channel.LETTER)
        assertThat(actual.toEmail).isEqualTo(payload.toAddress.emailAddress)
        assertThat(actual.requestor).isEqualTo(payload.requestor)
        assertPostalAddress(actual, payload)
        assertPersonalisation(actual, expectedPersonalisation)
        assertNotifyDetails(actual.notifyDetails!!, notifyResponse)
    }

    private fun assertPostalAddress(
        actualEntity: Notification,
        payload: SendNotifyApplicationRejectedMessage
    ) {
        assertThat(actualEntity.toPostalAddress).usingRecursiveComparison().ignoringFields("addressee")
            .isEqualTo(payload.toAddress.postalAddress!!.address)
        assertThat(actualEntity.toPostalAddress!!.addressee).isEqualTo(payload.toAddress.postalAddress!!.addressee)
    }

    private fun assertPersonalisation(
        actualEntity: Notification,
        expectedPersonalisation: Map<String, Any>
    ) {
        assertThat(actualEntity.personalisation)
            .usingRecursiveComparison().ignoringCollectionOrder().isEqualTo(expectedPersonalisation)
    }

    private fun assertNotifyDetails(actual: NotifyDetails, notifyResponse: NotifySendLetterSuccessResponse) {
        assertThat(actual.notificationId).isNotNull

        with(notifyResponse) {
            assertThat(actual.reference).isEqualTo(reference)
            assertThat(actual.body).isEqualTo(content.body)
            assertThat(actual.subject).isEqualTo(content.subject)
            assertThat(actual.templateId).isEqualTo(UUID.fromString(template.id))
            assertThat(actual.templateVersion.toString()).isEqualTo(template.version)
            assertThat(actual.templateUri).isEqualTo(template.uri)
        }
    }
}
