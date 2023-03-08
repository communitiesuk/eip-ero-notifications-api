package uk.gov.dluhc.notificationsapi.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
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
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildGenerateIdDocumentResubmissionTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildGeneratePhotoResubmissionTemplatePreviewDto

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
    inner class GenerateApplicationReceivedTemplatePreview {
        @Test
        fun `should return application received template preview`() {
            // Given
            val templateId = "6d0490ee-e004-402e-808f-5791e8336ddb"
            val personalisation = mapOf(
                "subject_param" to "test subject",
                "name_param" to "John",
                "custom_title" to "Resubmitting photo",
            )
            val language = LanguageDto.ENGLISH
            val sourceType = SourceType.POSTAL
            val request = buildGenerateApplicationReceivedTemplatePreviewDto(
                language = language,
                sourceType = sourceType
            )
            val expected = NotifyTemplatePreviewDto(text = "body", subject = "subject", html = "<p>body</p>")
            given(govNotifyApiClient.generateTemplatePreview(any(), any())).willReturn(expected)
            given(notificationTemplateMapper.fromNotificationTypeForChannelInLanguage(any(), any(), any(), any())).willReturn(templateId)
            given(templatePersonalisationDtoMapper.toApplicationReceivedTemplatePersonalisationMap(any())).willReturn(personalisation)

            // When

            val actual = templateService.generateApplicationReceivedTemplatePreview(request)

            // Then
            assertThat(actual).isEqualTo(expected)
            verify(govNotifyApiClient).generateTemplatePreview(templateId, personalisation)
            verify(notificationTemplateMapper).fromNotificationTypeForChannelInLanguage(SourceType.POSTAL, NotificationType.APPLICATION_RECEIVED, NotificationChannel.EMAIL, language)
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
            given(notificationTemplateMapper.fromNotificationTypeForChannelInLanguage(any(), any(), any(), any())).willReturn(templateId)
            given(templatePersonalisationDtoMapper.toApplicationApprovedTemplatePersonalisationMap(any())).willReturn(personalisation)

            // When

            val actual = templateService.generateApplicationApprovedTemplatePreview(request)

            // Then
            assertThat(actual).isEqualTo(expected)
            verify(govNotifyApiClient).generateTemplatePreview(templateId, personalisation)
            verify(notificationTemplateMapper).fromNotificationTypeForChannelInLanguage(VOTER_CARD, NotificationType.APPLICATION_APPROVED, NotificationChannel.EMAIL, language)
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
                .fromNotificationTypeForChannelInLanguage(dto.sourceType, dto.notificationType, dto.channel, dto.language)
            verify(templatePersonalisationDtoMapper).toApplicationRejectedTemplatePersonalisationMap(dto.personalisation)
            verifyNoMoreInteractions(govNotifyApiClient, notificationTemplateMapper, templatePersonalisationDtoMapper)
        }
    }
}
