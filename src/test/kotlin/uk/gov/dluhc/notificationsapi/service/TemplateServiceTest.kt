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
import uk.gov.dluhc.notificationsapi.dto.CommunicationChannel
import uk.gov.dluhc.notificationsapi.dto.DocumentCategoryDto
import uk.gov.dluhc.notificationsapi.dto.LanguageDto
import uk.gov.dluhc.notificationsapi.dto.NotificationType
import uk.gov.dluhc.notificationsapi.dto.NotificationType.ID_DOCUMENT_RESUBMISSION
import uk.gov.dluhc.notificationsapi.dto.NotificationType.PHOTO_RESUBMISSION
import uk.gov.dluhc.notificationsapi.dto.SourceType
import uk.gov.dluhc.notificationsapi.dto.SourceType.OVERSEAS
import uk.gov.dluhc.notificationsapi.dto.SourceType.VOTER_CARD
import uk.gov.dluhc.notificationsapi.dto.api.NotifyTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.mapper.DocumentCategoryMapper
import uk.gov.dluhc.notificationsapi.mapper.TemplatePersonalisationDtoMapper
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildApplicationRejectedPersonalisationMapFromDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildApplicationRejectedTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildBespokeCommPersonalisationMapFromDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildBespokeCommTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildGenerateApplicationApprovedTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildGenerateApplicationReceivedTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildGenerateIdDocumentRequiredTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildGenerateIdDocumentResubmissionTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildGeneratePhotoResubmissionTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildGenerateRejectedSignatureTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildGenerateRequestedSignatureTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildIdDocumentRequiredPersonalisationMapFromDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildInviteToRegisterPersonalisationMapFromDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildInviteToRegisterTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildNinoNotMatchedPersonalisationMapFromDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildNinoNotMatchedTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildRejectedDocumentPersonalisationMapFromDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildRejectedDocumentTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildRejectedOverseasDocumentPersonalisationMapFromDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildRejectedOverseasDocumentTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildRejectedSignaturePersonalisationMapFromDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildRequestedSignaturePersonalisationMapFromDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildRequiredOverseasDocumentPersonalisationMapFromDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildRequiredOverseasDocumentTemplatePreviewDto
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

    @Mock
    private lateinit var documentCategoryMapper: DocumentCategoryMapper

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
            val channel = CommunicationChannel.EMAIL
            val sourceType = VOTER_CARD
            val request = buildGeneratePhotoResubmissionTemplatePreviewDto(
                language = language,
                channel = channel,
                sourceType = sourceType,
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
            val channel = CommunicationChannel.EMAIL
            val sourceType = VOTER_CARD
            val request = buildGenerateIdDocumentResubmissionTemplatePreviewDto(
                language = language,
                channel = channel,
                sourceType = sourceType,
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
                    dto.language,
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
            sourceType: SourceType,
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
                sourceType = sourceType,
            )
            val expected = NotifyTemplatePreviewDto(text = "body", subject = "subject", html = "<p>body</p>")
            given(govNotifyApiClient.generateTemplatePreview(any(), any())).willReturn(expected)
            given(
                notificationTemplateMapper.fromNotificationTypeForChannelInLanguage(
                    any(),
                    any(),
                    any(),
                    any(),
                ),
            ).willReturn(templateId)
            given(templatePersonalisationDtoMapper.toApplicationReceivedTemplatePersonalisationMap(any())).willReturn(
                personalisation,
            )

            // When

            val actual = templateService.generateApplicationReceivedTemplatePreview(request)

            // Then
            assertThat(actual).isEqualTo(expected)
            verify(govNotifyApiClient).generateTemplatePreview(templateId, personalisation)
            verify(notificationTemplateMapper).fromNotificationTypeForChannelInLanguage(
                sourceType,
                NotificationType.APPLICATION_RECEIVED,
                CommunicationChannel.EMAIL,
                language,
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
                sourceType = sourceType,
            )
            val expected = NotifyTemplatePreviewDto(text = "body", subject = "subject", html = "<p>body</p>")
            given(govNotifyApiClient.generateTemplatePreview(any(), any())).willReturn(expected)
            given(
                notificationTemplateMapper.fromNotificationTypeForChannelInLanguage(
                    any(),
                    any(),
                    any(),
                    any(),
                ),
            ).willReturn(templateId)
            given(templatePersonalisationDtoMapper.toApplicationApprovedTemplatePersonalisationMap(any())).willReturn(
                personalisation,
            )

            // When

            val actual = templateService.generateApplicationApprovedTemplatePreview(request)

            // Then
            assertThat(actual).isEqualTo(expected)
            verify(govNotifyApiClient).generateTemplatePreview(templateId, personalisation)
            verify(notificationTemplateMapper).fromNotificationTypeForChannelInLanguage(
                VOTER_CARD,
                NotificationType.APPLICATION_APPROVED,
                CommunicationChannel.EMAIL,
                language,
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
                    dto.language,
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
            sourceType: SourceType,
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
                    dto.language,
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
                    dto.language,
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
                    dto.language,
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
            sourceType: SourceType,
        ) {
            // Given
            val dto = buildNinoNotMatchedTemplatePreviewDto(sourceType)
            val templateId = "80210eee-4592-11ed-b878-0242ac120005"
            val personalisationMap = buildNinoNotMatchedPersonalisationMapFromDto(dto.personalisation)
            val previewDto = NotifyTemplatePreviewDto(text = "body", subject = "subject", html = "<p>body</p>")
            given(notificationTemplateMapper.fromNotificationTypeForChannelInLanguage(any(), any(), any(), any()))
                .willReturn(templateId)
            given(templatePersonalisationDtoMapper.toRequiredDocumentTemplatePersonalisationMap(any(), any()))
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
                    dto.language,
                )
            verify(templatePersonalisationDtoMapper).toRequiredDocumentTemplatePersonalisationMap(
                dto.personalisation,
                dto.sourceType,
            )
            verifyNoMoreInteractions(govNotifyApiClient, notificationTemplateMapper, templatePersonalisationDtoMapper)
        }
    }

    @Nested
    inner class GenerateBespokeCommTemplatePreview {
        @ParameterizedTest
        @EnumSource(value = SourceType::class, names = ["POSTAL", "PROXY", "OVERSEAS", "VOTER_CARD"])
        fun `should return bespoke comm template preview`(
            sourceType: SourceType,
        ) {
            // Given
            val dto = buildBespokeCommTemplatePreviewDto(sourceType)
            val templateId = "80210eee-4592-11ed-b878-0242ac120005"
            val personalisationMap = buildBespokeCommPersonalisationMapFromDto(dto.personalisation)
            val previewDto = NotifyTemplatePreviewDto(text = "body", subject = "subject", html = "<p>body</p>")
            given(notificationTemplateMapper.fromNotificationTypeForChannelInLanguage(any(), any(), any(), any()))
                .willReturn(templateId)
            given(templatePersonalisationDtoMapper.toBespokeCommTemplatePersonalisationMap(any(), any()))
                .willReturn(personalisationMap)
            given(govNotifyApiClient.generateTemplatePreview(any(), any())).willReturn(previewDto)

            // When
            val actual = templateService.generateBespokeCommTemplatePreview(dto)

            // Then
            assertThat(actual).isEqualTo(previewDto)
            verify(govNotifyApiClient).generateTemplatePreview(templateId, personalisationMap)
            verify(notificationTemplateMapper)
                .fromNotificationTypeForChannelInLanguage(
                    dto.sourceType,
                    dto.notificationType,
                    dto.channel,
                    dto.language,
                )
            verify(templatePersonalisationDtoMapper).toBespokeCommTemplatePersonalisationMap(
                dto.personalisation,
                dto.language,
            )
            verifyNoMoreInteractions(govNotifyApiClient, notificationTemplateMapper, templatePersonalisationDtoMapper)
        }
    }

    @Nested
    inner class GenerateInviteToRegisterTemplatePreview {
        @ParameterizedTest
        @EnumSource(value = SourceType::class, names = ["POSTAL", "PROXY", "VOTER_CARD"])
        fun `should return invite to register template preview`(
            sourceType: SourceType,
        ) {
            // Given
            val dto = buildInviteToRegisterTemplatePreviewDto(sourceType)
            val templateId = "80210eee-4592-11ed-b878-0242ac120005"
            val personalisationMap = buildInviteToRegisterPersonalisationMapFromDto(dto.personalisation)
            val previewDto = NotifyTemplatePreviewDto(text = "body", subject = "subject", html = "<p>body</p>")
            given(notificationTemplateMapper.fromNotificationTypeForChannelInLanguage(any(), any(), any(), any()))
                    .willReturn(templateId)
            given(templatePersonalisationDtoMapper.toInviteToRegisterTemplatePersonalisationMap(any(), any()))
                    .willReturn(personalisationMap)
            given(govNotifyApiClient.generateTemplatePreview(any(), any())).willReturn(previewDto)

            // When
            val actual = templateService.generateInviteToRegisterTemplatePreview(dto)

            // Then
            assertThat(actual).isEqualTo(previewDto)
            verify(govNotifyApiClient).generateTemplatePreview(templateId, personalisationMap)
            verify(notificationTemplateMapper)
                    .fromNotificationTypeForChannelInLanguage(
                            dto.sourceType,
                            dto.notificationType,
                            dto.channel,
                            dto.language,
                    )
            verify(templatePersonalisationDtoMapper).toInviteToRegisterTemplatePersonalisationMap(
                    dto.personalisation,
                    dto.language,
            )
            verifyNoMoreInteractions(govNotifyApiClient, notificationTemplateMapper, templatePersonalisationDtoMapper)
        }
    }

    @Nested
    inner class GenerateRejectedOverseasDocumentTemplatePreview {
        @ParameterizedTest
        @CsvSource(
            "PARENT_GUARDIAN, REJECTED_PARENT_GUARDIAN, 90d605be-36bb-4cea-b453-aaf16ca249b3, EMAIL, ENGLISH",
            "PARENT_GUARDIAN, REJECTED_PARENT_GUARDIAN, 63b51e19-fdcb-470d-b62a-f96b076bc9ba, EMAIL, WELSH",
            "PARENT_GUARDIAN, REJECTED_PARENT_GUARDIAN, 1d4330c0-d636-4357-8cfe-5317aef17ebb, LETTER, ENGLISH",
            "PARENT_GUARDIAN, REJECTED_PARENT_GUARDIAN, 071cda09-08eb-48ff-8fcc-911b7e9ffc03, LETTER, WELSH",
            "PREVIOUS_ADDRESS, REJECTED_PREVIOUS_ADDRESS, b6406aa6-c762-49a6-912f-7e0437800cec, EMAIL, ENGLISH",
            "PREVIOUS_ADDRESS, REJECTED_PREVIOUS_ADDRESS, 10752429-44ee-429f-85bb-57ade4f62e5f, EMAIL, WELSH",
            "PREVIOUS_ADDRESS, REJECTED_PREVIOUS_ADDRESS, 8975fe83-7e55-422c-b041-84607336b19e, LETTER, ENGLISH",
            "PREVIOUS_ADDRESS, REJECTED_PREVIOUS_ADDRESS, 23827953-f350-425b-b563-2a7e2835b469, LETTER, WELSH",
            "IDENTITY, REJECTED_DOCUMENT, 4c4e0daa-fc8c-49fd-be74-10dccd5de80e, EMAIL, ENGLISH",
            "IDENTITY, REJECTED_DOCUMENT, 664ed443-f1a6-48d4-b066-b6c0f1e0953a, EMAIL, WELSH",
            "IDENTITY, REJECTED_DOCUMENT, ee06830e-25d1-4e54-adbc-aa79d5aef1fc, LETTER, ENGLISH",
            "IDENTITY, REJECTED_DOCUMENT, 664ed443-f1a6-48d4-b066-b6c0f1e0953a, LETTER, WELSH",
        )
        fun `should return rejected overseas document template preview`(
            documentCategory: DocumentCategoryDto,
            notificationType: NotificationType,
            templateId: String,
            communicationChannel: CommunicationChannel,
            language: LanguageDto,
        ) {
            // Given
            val dto =
                buildRejectedOverseasDocumentTemplatePreviewDto(
                    language = language,
                    channel = communicationChannel,
                    documentCategory = documentCategory,
                )
            val personalisationMap = buildRejectedOverseasDocumentPersonalisationMapFromDto(dto.personalisation)
            val previewDto = NotifyTemplatePreviewDto(text = "body", subject = "subject", html = "<p>body</p>")

            given(notificationTemplateMapper.fromNotificationTypeForChannelInLanguage(any(), any(), any(), any()))
                .willReturn(templateId)
            given(templatePersonalisationDtoMapper.toRejectedOverseasDocumentTemplatePersonalisationMap(any()))
                .willReturn(personalisationMap)
            given(govNotifyApiClient.generateTemplatePreview(any(), any())).willReturn(previewDto)
            given(documentCategoryMapper.fromRejectedDocumentCategoryDtoToNotificationTypeDto(any())).willReturn(
                notificationType,
            )

            // When
            val actual = templateService.generateRejectedOverseasDocumentTemplatePreview(dto)

            // Then
            assertThat(actual).isEqualTo(previewDto)
            verify(govNotifyApiClient).generateTemplatePreview(templateId, personalisationMap)
            verify(notificationTemplateMapper)
                .fromNotificationTypeForChannelInLanguage(
                    OVERSEAS,
                    notificationType,
                    dto.channel,
                    dto.language,
                )
            verify(templatePersonalisationDtoMapper).toRejectedOverseasDocumentTemplatePersonalisationMap(dto.personalisation)
            verifyNoMoreInteractions(
                govNotifyApiClient,
                notificationTemplateMapper,
                templatePersonalisationDtoMapper,
                documentCategoryMapper,
            )
        }
    }

    @Nested
    inner class GenerateRequiredOverseasDocumentTemplatePreview {
        @ParameterizedTest
        @CsvSource(
            "PARENT_GUARDIAN, PARENT_GUARDIAN_PROOF_REQUIRED, 206e2ea4-c2d4-412c-8abb-59bc9d57445b, EMAIL, ENGLISH",
            "PARENT_GUARDIAN, PARENT_GUARDIAN_PROOF_REQUIRED, 450f8f7a-5821-4b71-a6ab-372e48b086e2, EMAIL, WELSH",
            "PARENT_GUARDIAN, PARENT_GUARDIAN_PROOF_REQUIRED, 273febb3-fe97-4ae5-a4d6-dfd57cc8c6d8, LETTER, ENGLISH",
            "PARENT_GUARDIAN, PARENT_GUARDIAN_PROOF_REQUIRED, 20f8f805-fac0-453c-871e-41f1d9e0eb29, LETTER, WELSH",
            "PREVIOUS_ADDRESS, PREVIOUS_ADDRESS_DOCUMENT_REQUIRED, 00dd5dc0-9573-41ae-a3ac-2bd678f1c84a, EMAIL, ENGLISH",
            "PREVIOUS_ADDRESS, PREVIOUS_ADDRESS_DOCUMENT_REQUIRED, 9b6d00f8-d0f9-4921-9523-f69681f2b70b, EMAIL, WELSH",
            "PREVIOUS_ADDRESS, PREVIOUS_ADDRESS_DOCUMENT_REQUIRED, 8110954f-72d3-49ce-bbd1-fdfc22e7bde7, LETTER, ENGLISH",
            "PREVIOUS_ADDRESS, PREVIOUS_ADDRESS_DOCUMENT_REQUIRED, 9a207ce7-150c-425d-beac-89c39c2bd689, LETTER, WELSH",
            "IDENTITY, NINO_NOT_MATCHED, cea6ecec-7627-4322-b220-ee70a69f014d, EMAIL, ENGLISH",
            "IDENTITY, NINO_NOT_MATCHED, 1f922de4-1e70-4aaf-9b76-7ab789fe7d1a, EMAIL, WELSH",
            "IDENTITY, NINO_NOT_MATCHED, 0305c716-cfbc-401d-b95b-a6aeda741ba6, LETTER, ENGLISH",
            "IDENTITY, NINO_NOT_MATCHED, abd343c5-edab-4e58-82b4-293736a464d0, LETTER, WELSH",
        )
        fun `should return rejected overseas document template preview`(
            documentCategory: DocumentCategoryDto,
            notificationType: NotificationType,
            templateId: String,
            communicationChannel: CommunicationChannel,
            language: LanguageDto,
        ) {
            // Given
            val dto =
                buildRequiredOverseasDocumentTemplatePreviewDto(
                    language = language,
                    channel = communicationChannel,
                    documentCategory = documentCategory,
                )
            val personalisationMap = buildRequiredOverseasDocumentPersonalisationMapFromDto(dto.personalisation)
            val previewDto = NotifyTemplatePreviewDto(text = "body", subject = "subject", html = "<p>body</p>")

            given(notificationTemplateMapper.fromNotificationTypeForChannelInLanguage(any(), any(), any(), any()))
                .willReturn(templateId)
            given(templatePersonalisationDtoMapper.toRequiredOverseasDocumentTemplatePersonalisationMap(any()))
                .willReturn(personalisationMap)
            given(govNotifyApiClient.generateTemplatePreview(any(), any())).willReturn(previewDto)
            given(documentCategoryMapper.fromRequiredDocumentCategoryDtoToNotificationTypeDto(any())).willReturn(
                notificationType,
            )

            // When
            val actual = templateService.generateRequiredOverseasDocumentTemplatePreview(dto)

            // Then
            assertThat(actual).isEqualTo(previewDto)
            verify(govNotifyApiClient).generateTemplatePreview(templateId, personalisationMap)
            verify(notificationTemplateMapper)
                .fromNotificationTypeForChannelInLanguage(
                    OVERSEAS,
                    notificationType,
                    dto.channel,
                    dto.language,
                )
            verify(templatePersonalisationDtoMapper).toRequiredOverseasDocumentTemplatePersonalisationMap(dto.personalisation)
            verifyNoMoreInteractions(
                govNotifyApiClient,
                notificationTemplateMapper,
                templatePersonalisationDtoMapper,
                documentCategoryMapper,
            )
        }
    }
}
