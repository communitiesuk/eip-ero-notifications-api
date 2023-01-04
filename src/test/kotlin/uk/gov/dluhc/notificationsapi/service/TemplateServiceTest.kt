package uk.gov.dluhc.notificationsapi.service

import org.assertj.core.api.Assertions.assertThat
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
import uk.gov.dluhc.notificationsapi.dto.TemplateType
import uk.gov.dluhc.notificationsapi.dto.api.NotifyTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.mapper.TemplatePersonalisationDtoMapper
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
        val request = buildGeneratePhotoResubmissionTemplatePreviewDto(
            language = language,
            channel = channel
        )
        val expected = NotifyTemplatePreviewDto(text = "body", subject = "subject", html = "<p>body</p>")
        given(govNotifyApiClient.generateTemplatePreview(any(), any())).willReturn(expected)
        given(notificationTemplateMapper.fromTemplateTypeForChannelAndLanguage(any(), any(), any())).willReturn(templateId)
        given(templatePersonalisationDtoMapper.toPhotoResubmissionTemplatePersonalisationMap(any())).willReturn(personalisation)

        // When

        val actual = templateService.generatePhotoResubmissionTemplatePreview(request)

        // Then
        assertThat(actual).isEqualTo(expected)
        verify(govNotifyApiClient).generateTemplatePreview(templateId, personalisation)
        verify(notificationTemplateMapper).fromTemplateTypeForChannelAndLanguage(TemplateType.PHOTO_RESUBMISSION, channel, language)
        verify(templatePersonalisationDtoMapper).toPhotoResubmissionTemplatePersonalisationMap(request.personalisation)
        verifyNoMoreInteractions(govNotifyApiClient, notificationTemplateMapper, templatePersonalisationDtoMapper)
    }

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
        val request = buildGenerateIdDocumentResubmissionTemplatePreviewDto(
            language = language,
            channel = channel
        )
        val expected = NotifyTemplatePreviewDto(text = "body", subject = "subject", html = "<p>body</p>")
        given(govNotifyApiClient.generateTemplatePreview(any(), any())).willReturn(expected)
        given(notificationTemplateMapper.fromTemplateTypeForChannelAndLanguage(any(), any(), any())).willReturn(templateId)
        given(templatePersonalisationDtoMapper.toIdDocumentResubmissionTemplatePersonalisationMap(any())).willReturn(personalisation)

        // When

        val actual = templateService.generateIdDocumentResubmissionTemplatePreview(request)

        // Then
        assertThat(actual).isEqualTo(expected)
        verify(govNotifyApiClient).generateTemplatePreview(templateId, personalisation)
        verify(notificationTemplateMapper).fromTemplateTypeForChannelAndLanguage(TemplateType.ID_DOCUMENT_RESUBMISSION, channel, language)
        verify(templatePersonalisationDtoMapper).toIdDocumentResubmissionTemplatePersonalisationMap(request.personalisation)
        verifyNoMoreInteractions(govNotifyApiClient, notificationTemplateMapper, templatePersonalisationDtoMapper)
    }
}
