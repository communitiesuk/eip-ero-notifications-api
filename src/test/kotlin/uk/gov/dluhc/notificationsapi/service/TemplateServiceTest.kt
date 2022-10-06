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
import uk.gov.dluhc.notificationsapi.client.GovNotifyApiClient
import uk.gov.dluhc.notificationsapi.dto.GenerateTemplatePreviewRequestDto
import uk.gov.dluhc.notificationsapi.dto.api.NotifyTemplatePreviewDto

@ExtendWith(MockitoExtension::class)
class TemplateServiceTest {
    @InjectMocks
    private lateinit var templateService: TemplateService

    @Mock
    private lateinit var govNotifyApiClient: GovNotifyApiClient

    @Test
    fun `should return template preview`() {
        // Given
        val templateId = "20210eee-4592-11ed-b878-0242ac120002"
        val personalisation = mapOf(
            "subject_param" to "test subject",
            "name_param" to "John",
            "custom_title" to "Resubmitting photo",
        )
        val request = GenerateTemplatePreviewRequestDto(templateId, personalisation)
        val expected = NotifyTemplatePreviewDto(text = "body", subject = "subject", html = "<p>body</p>")
        given(govNotifyApiClient.generateTemplatePreview(any(), any())).willReturn(expected)

        // When

        val actual = templateService.generateTemplatePreview(request)

        // Then
        assertThat(actual).isEqualTo(expected)
        verify(govNotifyApiClient).generateTemplatePreview(templateId, personalisation)
    }
}
