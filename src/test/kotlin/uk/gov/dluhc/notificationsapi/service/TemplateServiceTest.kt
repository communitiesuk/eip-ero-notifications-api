package uk.gov.dluhc.notificationsapi.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.EnumSource
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.given
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import uk.gov.dluhc.notificationsapi.client.GovNotifyApiClient
import uk.gov.dluhc.notificationsapi.client.mapper.NotificationTemplateMapper
import uk.gov.dluhc.notificationsapi.dto.LanguageDto
import uk.gov.dluhc.notificationsapi.dto.NotificationChannel
import uk.gov.dluhc.notificationsapi.dto.NotificationType
import uk.gov.dluhc.notificationsapi.dto.NotificationType.ID_DOCUMENT_RESUBMISSION
import uk.gov.dluhc.notificationsapi.dto.NotificationType.PHOTO_RESUBMISSION
import uk.gov.dluhc.notificationsapi.dto.SourceType
import uk.gov.dluhc.notificationsapi.dto.SourceType.VOTER_CARD
import uk.gov.dluhc.notificationsapi.dto.api.NotifyTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.mapper.TemplatePersonalisationDtoMapper
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildApplicationRejectedPersonalisationMapFromDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildApplicationRejectedTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildGenerateApplicationApprovedTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildGenerateApplicationReceivedTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildGenerateIdDocumentRequiredTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildGenerateIdDocumentResubmissionTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildGeneratePhotoResubmissionTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildGenerateRejectedSignatureTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildGenerateRequestedSignatureTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildIdDocumentRequiredPersonalisationMapFromDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildNinoNotMatchedPersonalisationMapFromDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildNinoNotMatchedTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildParentGauardianRequiredTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildParentGuardianRequiredPersonalisationMapFromDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildQualifyingAddressRequiredPersonalisationMapFromDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildQualifyingAddressRequiredTemplateDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildRejectedDocumentPersonalisationMapFromDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildRejectedDocumentTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildRejectedParentGuardianPersonalisationMapFromDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildRejectedParentGuardianTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildRejectedSignaturePersonalisationMapFromDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildRequestedSignaturePersonalisationMapFromDto
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class TemplateServiceTest {
    @InjectMocks
    private lateinit var templateService: TemplateService

    @Mock
    private lateinit var govNotifyApiClient: GovNotifyApiClient

    @Mock
    private lateinit var notificationTemplateMapper: NotificationTemplateMapper

    @Mock
    private lateinit var templatePersonalisationDtoMapper: TemplatePersonalisationDtoMapper

    @Nested
    inner class GeneratePhotoResubmissionTemplatePreview {
        @Test
        fun `should return photo resubmission template preview`() {
            // Given
            val templateId = "20210eee-4592-11ed-b878-0242ac120002"
            val personalisation = mapOf(
                "subject_param" to "test subject",
                "name_param" to "John",
                "custom_title" to "Resubmitting photo",
            )
            val language = LanguageDto.ENGLISH
            val channel = NotificationChannel.EMAIL
            val sourceType = VOTER_CARD
            val request = buildGeneratePhotoResubmissionTemplatePreviewDto(
                language = language,
                channel = channel,
                sourceType = sourceType
            )
            val expected = NotifyTemplatePreviewDto(text = "body", subject = "subject", html = "<p>body</p>")
            given(govNotifyApiClient.generateTemplatePreview(any(), any())).willReturn(expected)
            given(notificationTemplateMapper.fromNotificationTypeForChannelInLanguage(any(), any(), any(), any()))
                .willReturn(templateId)
            given(templatePersonalisationDtoMapper.toPhotoResubmissionTemplatePersonalisationMap(any()))
                .willReturn(personalisation)

            // When

            val actual = templateService.generatePhotoResubmissionTemplatePreview(request)

            // Then
            assertThat(actual).isEqualTo(expected)
            verify(govNotifyApiClient).generateTemplatePreview(templateId, personalisation)
            verify(notificationTemplateMapper)
                .fromNotificationTypeForChannelInLanguage(VOTER_CARD, PHOTO_RESUBMISSION, channel, language)
            verify(templatePersonalisationDtoMapper).toPhotoResubmissionTemplatePersonalisationMap(request.personalisation)
            verifyNoMoreInteractions(govNotifyApiClient, notificationTemplateMapper, templatePersonalisationDtoMapper)
        }
    }

    @Nested
    inner class GenerateIdDocumentResubmissionTemplatePreview {
        @Test
        fun `should return id document resubmission template preview`() {
            // Given
            val templateId = "50210eee-4592-11ed-b878-0242ac120005"
            val personalisation = mapOf(
                "subject_param" to "test subject",
                "name_param" to "John",
                "custom_title" to "Resubmitting photo",
            )
            val language = LanguageDto.ENGLISH
            val channel = NotificationChannel.EMAIL
            val sourceType = VOTER_CARD
            val request = buildGenerateIdDocumentResubmissionTemplatePreviewDto(
                language = language,
                channel = channel,
                sourceType = sourceType
            )
            val expected = NotifyTemplatePreviewDto(text = "body", subject = "subject", html = "<p>body</p>")
            given(govNotifyApiClient.generateTemplatePreview(any(), any())).willReturn(expected)
            given(notificationTemplateMapper.fromNotificationTypeForChannelInLanguage(any(), any(), any(), any()))
                .willReturn(templateId)
            given(templatePersonalisationDtoMapper.toIdDocumentResubmissionTemplatePersonalisationMap(any()))
                .willReturn(personalisation)

            // When

            val actual = templateService.generateIdDocumentResubmissionTemplatePreview(request)

            // Then
            assertThat(actual).isEqualTo(expected)
            verify(govNotifyApiClient).generateTemplatePreview(templateId, personalisation)
            verify(notificationTemplateMapper)
                .fromNotificationTypeForChannelInLanguage(VOTER_CARD, ID_DOCUMENT_RESUBMISSION, channel, language)
            verify(templatePersonalisationDtoMapper).toIdDocumentResubmissionTemplatePersonalisationMap(request.personalisation)
            verifyNoMoreInteractions(govNotifyApiClient, notificationTemplateMapper, templatePersonalisationDtoMapper)
        }
    }

    @Nested
    inner class GenerateIdDocumentRequiredTemplatePreview {
        @Test
        fun `should return id document required template preview`() {
            // Given
            val dto = buildGenerateIdDocumentRequiredTemplatePreviewDto()
            val templateId = UUID.randomUUID().toString()
            val personalisationMap = buildIdDocumentRequiredPersonalisationMapFromDto(dto.personalisation)
            val previewDto = NotifyTemplatePreviewDto(text = "body", subject = "subject", html = "<p>body</p>")
            given(notificationTemplateMapper.fromNotificationTypeForChannelInLanguage(any(), any(), any(), any()))
                .willReturn(templateId)
            given(templatePersonalisationDtoMapper.toIdDocumentRequiredTemplatePersonalisationMap(any()))
                .willReturn(personalisationMap)
            given(govNotifyApiClient.generateTemplatePreview(any(), any())).willReturn(previewDto)

            // When
            val actual = templateService.generateIdDocumentRequiredTemplatePreview(dto)

            // Then
            assertThat(actual).isEqualTo(previewDto)
            verify(govNotifyApiClient).generateTemplatePreview(templateId, personalisationMap)
            verify(notificationTemplateMapper)
                .fromNotificationTypeForChannelInLanguage(
                    dto.sourceType,
                    dto.notificationType,
                    dto.channel,
                    dto.language
                )
            verify(templatePersonalisationDtoMapper).toIdDocumentRequiredTemplatePersonalisationMap(dto.personalisation)
            verifyNoMoreInteractions(govNotifyApiClient, notificationTemplateMapper, templatePersonalisationDtoMapper)
        }
    }

    @Nested
    inner class GenerateApplicationReceivedTemplatePreview {
        @ParameterizedTest
        @EnumSource(value = SourceType::class, names = ["POSTAL", "PROXY"])
        fun `should return application received template preview`(
            sourceType: SourceType
        ) {
            // Given
            val templateId = "6d0490ee-e004-402e-808f-5791e8336ddb"
            val personalisation = mapOf(
                "subject_param" to "test subject",
                "name_param" to "John",
                "custom_title" to "Resubmitting photo",
            )
            val language = LanguageDto.ENGLISH
            val request = buildGenerateApplicationReceivedTemplatePreviewDto(
                language = language,
                sourceType = sourceType
            )
            val expected = NotifyTemplatePreviewDto(text = "body", subject = "subject", html = "<p>body</p>")
            given(govNotifyApiClient.generateTemplatePreview(any(), any())).willReturn(expected)
            given(
                notificationTemplateMapper.fromNotificationTypeForChannelInLanguage(
                    any(),
                    any(),
                    any(),
                    any()
                )
            ).willReturn(templateId)
            given(templatePersonalisationDtoMapper.toApplicationReceivedTemplatePersonalisationMap(any())).willReturn(
                personalisation
            )

            // When

            val actual = templateService.generateApplicationReceivedTemplatePreview(request)

            // Then
            assertThat(actual).isEqualTo(expected)
            verify(govNotifyApiClient).generateTemplatePreview(templateId, personalisation)
            verify(notificationTemplateMapper).fromNotificationTypeForChannelInLanguage(
                sourceType,
                NotificationType.APPLICATION_RECEIVED,
                NotificationChannel.EMAIL,
                language
            )
            verify(templatePersonalisationDtoMapper).toApplicationReceivedTemplatePersonalisationMap(request.personalisation)
            verifyNoMoreInteractions(govNotifyApiClient, notificationTemplateMapper, templatePersonalisationDtoMapper)
        }
    }

    @Nested
    inner class GenerateApplicationApprovedTemplatePreview {
        @Test
        fun `should return application approved template preview`() {
            // Given
            val templateId = "50210eee-4592-11ed-b878-0242ac120005"
            val personalisation = mapOf(
                "subject_param" to "test subject",
                "name_param" to "John",
                "custom_title" to "Resubmitting photo",
            )
            val language = LanguageDto.ENGLISH
            val sourceType = VOTER_CARD
            val request = buildGenerateApplicationApprovedTemplatePreviewDto(
                language = language,
                sourceType = sourceType
            )
            val expected = NotifyTemplatePreviewDto(text = "body", subject = "subject", html = "<p>body</p>")
            given(govNotifyApiClient.generateTemplatePreview(any(), any())).willReturn(expected)
            given(
                notificationTemplateMapper.fromNotificationTypeForChannelInLanguage(
                    any(),
                    any(),
                    any(),
                    any()
                )
            ).willReturn(templateId)
            given(templatePersonalisationDtoMapper.toApplicationApprovedTemplatePersonalisationMap(any())).willReturn(
                personalisation
            )

            // When

            val actual = templateService.generateApplicationApprovedTemplatePreview(request)

            // Then
            assertThat(actual).isEqualTo(expected)
            verify(govNotifyApiClient).generateTemplatePreview(templateId, personalisation)
            verify(notificationTemplateMapper).fromNotificationTypeForChannelInLanguage(
                VOTER_CARD,
                NotificationType.APPLICATION_APPROVED,
                NotificationChannel.EMAIL,
                language
            )
            verify(templatePersonalisationDtoMapper).toApplicationApprovedTemplatePersonalisationMap(request.personalisation)
            verifyNoMoreInteractions(govNotifyApiClient, notificationTemplateMapper, templatePersonalisationDtoMapper)
        }
    }

    @Nested
    inner class GenerateApplicationRejectedTemplatePreview {
        @Test
        fun `should return application rejected template preview`() {
            // Given
            val dto = buildApplicationRejectedTemplatePreviewDto(VOTER_CARD)
            val templateId = "50210eee-4592-11ed-b878-0242ac120005"
            val personalisationMap = buildApplicationRejectedPersonalisationMapFromDto(dto.personalisation)
            val previewDto = NotifyTemplatePreviewDto(text = "body", subject = "subject", html = "<p>body</p>")
            given(notificationTemplateMapper.fromNotificationTypeForChannelInLanguage(any(), any(), any(), any()))
                .willReturn(templateId)
            given(templatePersonalisationDtoMapper.toApplicationRejectedTemplatePersonalisationMap(any()))
                .willReturn(personalisationMap)
            given(govNotifyApiClient.generateTemplatePreview(any(), any())).willReturn(previewDto)

            // When
            val actual = templateService.generateApplicationRejectedTemplatePreview(dto)

            // Then
            assertThat(actual).isEqualTo(previewDto)
            verify(govNotifyApiClient).generateTemplatePreview(templateId, personalisationMap)
            verify(notificationTemplateMapper)
                .fromNotificationTypeForChannelInLanguage(
                    dto.sourceType,
                    dto.notificationType,
                    dto.channel,
                    dto.language
                )
            verify(templatePersonalisationDtoMapper).toApplicationRejectedTemplatePersonalisationMap(dto.personalisation)
            verifyNoMoreInteractions(govNotifyApiClient, notificationTemplateMapper, templatePersonalisationDtoMapper)
        }
    }

    @Nested
    inner class GenerateRejectedDocumentTemplatePreview {
        @ParameterizedTest
        @EnumSource(value = SourceType::class, names = ["POSTAL", "PROXY"])
        fun `should return rejected document template preview`(
            sourceType: SourceType
        ) {
            // Given
            val dto = buildRejectedDocumentTemplatePreviewDto(sourceType)
            val templateId = "50210eee-4592-11ed-b878-0242ac120005"
            val personalisationMap = buildRejectedDocumentPersonalisationMapFromDto(dto.personalisation)
            val previewDto = NotifyTemplatePreviewDto(text = "body", subject = "subject", html = "<p>body</p>")
            given(notificationTemplateMapper.fromNotificationTypeForChannelInLanguage(any(), any(), any(), any()))
                .willReturn(templateId)
            given(templatePersonalisationDtoMapper.toRejectedDocumentTemplatePersonalisationMap(any()))
                .willReturn(personalisationMap)
            given(govNotifyApiClient.generateTemplatePreview(any(), any())).willReturn(previewDto)

            // When
            val actual = templateService.generateRejectedDocumentTemplatePreview(dto)

            // Then
            assertThat(actual).isEqualTo(previewDto)
            verify(govNotifyApiClient).generateTemplatePreview(templateId, personalisationMap)
            verify(notificationTemplateMapper)
                .fromNotificationTypeForChannelInLanguage(
                    dto.sourceType,
                    dto.notificationType,
                    dto.channel,
                    dto.language
                )
            verify(templatePersonalisationDtoMapper).toRejectedDocumentTemplatePersonalisationMap(dto.personalisation)
            verifyNoMoreInteractions(govNotifyApiClient, notificationTemplateMapper, templatePersonalisationDtoMapper)
        }
    }

    @Nested
    inner class GenerateRejectedSignatureTemplatePreview {
        @Test
        fun `should return rejected signature template preview`() {
            // Given
            val dto = buildGenerateRejectedSignatureTemplatePreviewDto()
            val templateId = "7fa64777-222f-45e9-937b-6236359b79df"
            val personalisationMap = buildRejectedSignaturePersonalisationMapFromDto(dto.personalisation)
            val previewDto = NotifyTemplatePreviewDto(text = "body", subject = "subject", html = "<p>body</p>")
            given(notificationTemplateMapper.fromNotificationTypeForChannelInLanguage(any(), any(), any(), any()))
                .willReturn(templateId)
            given(templatePersonalisationDtoMapper.toRejectedSignatureTemplatePersonalisationMap(dto.personalisation))
                .willReturn(personalisationMap)
            given(govNotifyApiClient.generateTemplatePreview(any(), any())).willReturn(previewDto)

            // When
            val actual = templateService.generateRejectedSignatureTemplatePreview(dto)

            // Then
            assertThat(actual).isEqualTo(previewDto)
            verify(govNotifyApiClient).generateTemplatePreview(templateId, personalisationMap)
            verify(notificationTemplateMapper)
                .fromNotificationTypeForChannelInLanguage(
                    dto.sourceType,
                    dto.notificationType,
                    dto.channel,
                    dto.language
                )
            verify(templatePersonalisationDtoMapper).toRejectedSignatureTemplatePersonalisationMap(dto.personalisation)
            verifyNoMoreInteractions(govNotifyApiClient, notificationTemplateMapper, templatePersonalisationDtoMapper)
        }
    }

    @Nested
    inner class GenerateRequestedSignatureTemplatePreview {
        @Test
        fun `should return requested signature template preview`() {
            // Given
            val dto = buildGenerateRequestedSignatureTemplatePreviewDto()
            val templateId = "0fdb9022-db38-4f08-8bc4-04c0237097dc"
            val personalisationMap = buildRequestedSignaturePersonalisationMapFromDto(dto.personalisation)
            val previewDto = NotifyTemplatePreviewDto(text = "body", subject = "subject", html = "<p>body</p>")
            given(notificationTemplateMapper.fromNotificationTypeForChannelInLanguage(any(), any(), any(), any()))
                .willReturn(templateId)
            given(templatePersonalisationDtoMapper.toRequestedSignatureTemplatePersonalisationMap(dto.personalisation))
                .willReturn(personalisationMap)
            given(govNotifyApiClient.generateTemplatePreview(any(), any())).willReturn(previewDto)

            // When
            val actual = templateService.generateRequestedSignatureTemplatePreview(dto)

            // Then
            assertThat(actual).isEqualTo(previewDto)
            verify(govNotifyApiClient).generateTemplatePreview(templateId, personalisationMap)
            verify(notificationTemplateMapper)
                .fromNotificationTypeForChannelInLanguage(
                    dto.sourceType,
                    dto.notificationType,
                    dto.channel,
                    dto.language
                )
            verify(templatePersonalisationDtoMapper).toRequestedSignatureTemplatePersonalisationMap(dto.personalisation)
            verifyNoMoreInteractions(govNotifyApiClient, notificationTemplateMapper, templatePersonalisationDtoMapper)
        }
    }

    @Nested
    inner class GenerateNinoNotMatchedTemplatePreview {
        @ParameterizedTest
        @EnumSource(value = SourceType::class, names = ["POSTAL"])
        fun `should return nino not matched template preview`(
            sourceType: SourceType
        ) {
            // Given
            val dto = buildNinoNotMatchedTemplatePreviewDto(sourceType)
            val templateId = "80210eee-4592-11ed-b878-0242ac120005"
            val personalisationMap = buildNinoNotMatchedPersonalisationMapFromDto(dto.personalisation)
            val previewDto = NotifyTemplatePreviewDto(text = "body", subject = "subject", html = "<p>body</p>")
            given(notificationTemplateMapper.fromNotificationTypeForChannelInLanguage(any(), any(), any(), any()))
                .willReturn(templateId)
            given(templatePersonalisationDtoMapper.toNinoNotMatchedTemplatePersonalisationMap(any()))
                .willReturn(personalisationMap)
            given(govNotifyApiClient.generateTemplatePreview(any(), any())).willReturn(previewDto)

            // When
            val actual = templateService.generateNinoNotMatchedTemplatePreview(dto)

            // Then
            assertThat(actual).isEqualTo(previewDto)
            verify(govNotifyApiClient).generateTemplatePreview(templateId, personalisationMap)
            verify(notificationTemplateMapper)
                .fromNotificationTypeForChannelInLanguage(
                    dto.sourceType,
                    dto.notificationType,
                    dto.channel,
                    dto.language
                )
            verify(templatePersonalisationDtoMapper).toNinoNotMatchedTemplatePersonalisationMap(dto.personalisation)
            verifyNoMoreInteractions(govNotifyApiClient, notificationTemplateMapper, templatePersonalisationDtoMapper)
        }
    }

    @Nested
    inner class GenerateParentGuardianRequiredTemplatePreview {
        @ParameterizedTest
        @CsvSource(
            "206e2ea4-c2d4-412c-8abb-59bc9d57445b, EMAIL, ENGLISH",
            "450f8f7a-5821-4b71-a6ab-372e48b086e2, EMAIL, WELSH",
            "273febb3-fe97-4ae5-a4d6-dfd57cc8c6d8, LETTER, ENGLISH",
            "20f8f805-fac0-453c-871e-41f1d9e0eb29, LETTER, WELSH",
        )
        fun `should return parent guardian required template preview`(
            templateId: String,
            notificationChannel: NotificationChannel,
            language: LanguageDto
        ) {
            // Given
            val dto =
                buildParentGauardianRequiredTemplatePreviewDto(
                    language = language,
                    channel = notificationChannel
                )
            val personalisationMap = buildParentGuardianRequiredPersonalisationMapFromDto(dto.personalisation)
            val previewDto = NotifyTemplatePreviewDto(text = "body", subject = "subject", html = "<p>body</p>")
            given(notificationTemplateMapper.fromNotificationTypeForChannelInLanguage(any(), any(), any(), any()))
                .willReturn(templateId)
            given(templatePersonalisationDtoMapper.toParentGuardianRequiredTemplatePersonalisationMap(any()))
                .willReturn(personalisationMap)
            given(govNotifyApiClient.generateTemplatePreview(any(), any())).willReturn(previewDto)

            // When
            val actual = templateService.generateParentGuardianRequiredTemplatePreview(dto)

            // Then
            assertThat(actual).isEqualTo(previewDto)
            verify(govNotifyApiClient).generateTemplatePreview(templateId, personalisationMap)
            verify(notificationTemplateMapper)
                .fromNotificationTypeForChannelInLanguage(
                    dto.sourceType,
                    dto.notificationType,
                    dto.channel,
                    dto.language
                )
            verify(templatePersonalisationDtoMapper).toParentGuardianRequiredTemplatePersonalisationMap(dto.personalisation)
            verifyNoMoreInteractions(govNotifyApiClient, notificationTemplateMapper, templatePersonalisationDtoMapper)
        }
    }

    @Nested
    inner class GenerateQualifyingAddressRequiredTemplatePreview {
        @ParameterizedTest
        @CsvSource(
            "00dd5dc0-9573-41ae-a3ac-2bd678f1c84a, EMAIL, ENGLISH",
            "9b6d00f8-d0f9-4921-9523-f69681f2b70b, EMAIL, WELSH",
            "8110954f-72d3-49ce-bbd1-fdfc22e7bde7, LETTER, ENGLISH",
            "9a207ce7-150c-425d-beac-89c39c2bd689, LETTER, WELSH",
        )
        fun `should return qualifying address required template preview`(
            templateId: String,
            notificationChannel: NotificationChannel,
            language: LanguageDto
        ) {
            // Given
            val dto =
                buildQualifyingAddressRequiredTemplateDto(
                    language = language,
                    channel = notificationChannel
                )
            val personalisationMap = buildQualifyingAddressRequiredPersonalisationMapFromDto(dto.personalisation)
            val previewDto = NotifyTemplatePreviewDto(text = "body", subject = "subject", html = "<p>body</p>")
            given(notificationTemplateMapper.fromNotificationTypeForChannelInLanguage(any(), any(), any(), any()))
                .willReturn(templateId)
            given(templatePersonalisationDtoMapper.toQualifyingAddressRequiredTemplatePersonalisationMap(any()))
                .willReturn(personalisationMap)
            given(govNotifyApiClient.generateTemplatePreview(any(), any())).willReturn(previewDto)

            // When
            val actual = templateService.generateQualifyingAddressRequiredTemplatePreview(dto)

            // Then
            assertThat(actual).isEqualTo(previewDto)
            verify(govNotifyApiClient).generateTemplatePreview(templateId, personalisationMap)
            verify(notificationTemplateMapper)
                .fromNotificationTypeForChannelInLanguage(
                    dto.sourceType,
                    dto.notificationType,
                    dto.channel,
                    dto.language
                )
            verify(templatePersonalisationDtoMapper).toQualifyingAddressRequiredTemplatePersonalisationMap(dto.personalisation)
            verifyNoMoreInteractions(govNotifyApiClient, notificationTemplateMapper, templatePersonalisationDtoMapper)
        }
    }

    @Nested
    inner class GenerateRejectedParentGuardianTemplatePreview {
        @ParameterizedTest
        @CsvSource(
            "90d605be-36bb-4cea-b453-aaf16ca249b3, EMAIL, ENGLISH",
            "63b51e19-fdcb-470d-b62a-f96b076bc9ba, EMAIL, WELSH",
            "1d4330c0-d636-4357-8cfe-5317aef17ebb, LETTER, ENGLISH",
            "071cda09-08eb-48ff-8fcc-911b7e9ffc03, LETTER, WELSH",
        )
        fun `should return rejected parent guardian template preview`(
            templateId: String,
            notificationChannel: NotificationChannel,
            language: LanguageDto
        ) {
            // Given
            val dto =
                buildRejectedParentGuardianTemplatePreviewDto(
                    language = language,
                    channel = notificationChannel
                )
            val personalisationMap = buildRejectedParentGuardianPersonalisationMapFromDto(dto.personalisation)
            val previewDto = NotifyTemplatePreviewDto(text = "body", subject = "subject", html = "<p>body</p>")
            given(notificationTemplateMapper.fromNotificationTypeForChannelInLanguage(any(), any(), any(), any()))
                .willReturn(templateId)
            given(templatePersonalisationDtoMapper.toRejectedParentGuardianTemplatePersonalisationMap(any()))
                .willReturn(personalisationMap)
            given(govNotifyApiClient.generateTemplatePreview(any(), any())).willReturn(previewDto)

            // When
            val actual = templateService.generateRejectedParentGuardianTemplatePreview(dto)

            // Then
            assertThat(actual).isEqualTo(previewDto)
            verify(govNotifyApiClient).generateTemplatePreview(templateId, personalisationMap)
            verify(notificationTemplateMapper)
                .fromNotificationTypeForChannelInLanguage(
                    dto.sourceType,
                    dto.notificationType,
                    dto.channel,
                    dto.language
                )
            verify(templatePersonalisationDtoMapper).toRejectedParentGuardianTemplatePersonalisationMap(dto.personalisation)
            verifyNoMoreInteractions(govNotifyApiClient, notificationTemplateMapper, templatePersonalisationDtoMapper)
        }
    }
}
