package uk.gov.dluhc.notificationsapi.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.given
import uk.gov.dluhc.notificationsapi.client.GovNotifyApiClient
import uk.gov.dluhc.notificationsapi.client.mapper.NotificationTemplateMapper
import uk.gov.dluhc.notificationsapi.dto.NotificationType
import uk.gov.dluhc.notificationsapi.dto.api.NotifyTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.mapper.CommonTemplatePreviewDtoMapper
import uk.gov.dluhc.notificationsapi.models.CommunicationChannel
import uk.gov.dluhc.notificationsapi.models.GenerateTemplatePreviewResponse
import uk.gov.dluhc.notificationsapi.models.Language
import uk.gov.dluhc.notificationsapi.models.SourceType
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildCommonTemplatePreviewDto

@ExtendWith(MockitoExtension::class)
class CommonTemplateServiceTest {

    @Mock
    private lateinit var govNotifyApiClient: GovNotifyApiClient

    @Mock
    private lateinit var notificationTemplateMapper: NotificationTemplateMapper

    @Mock
    private lateinit var commonTemplatePreviewDtoMapper: CommonTemplatePreviewDtoMapper

    @InjectMocks
    private lateinit var commonTemplateService: CommonTemplateService

    @Test
    fun `should generate template preview`() {
        // Given
        val personalisation = mapOf<String, Any>(Pair("string", 3))
        val getTemplatePersonalisation = { -> personalisation }
        val commonTemplatePreviewDto = buildCommonTemplatePreviewDto()
        val notifyTemplatePreviewDto = NotifyTemplatePreviewDto(
            text = "text",
            subject = "subject",
            html = "html",
        )

        given(
            commonTemplatePreviewDtoMapper.toCommonTemplatePreviewDto(
                CommunicationChannel.EMAIL,
                SourceType.POSTAL,
                Language.EN,
                NotificationType.SIGNATURE_RESUBMISSION,
            ),
        ).willReturn(commonTemplatePreviewDto)

        given(notificationTemplateMapper.fromNotificationTypeForChannelInLanguage(commonTemplatePreviewDto)).willReturn("COMMUNICATION")

        given(govNotifyApiClient.generateTemplatePreview("COMMUNICATION", personalisation)).willReturn(notifyTemplatePreviewDto)

        // When
        val preview = commonTemplateService.generateTemplatePreview(
            CommunicationChannel.EMAIL,
            SourceType.POSTAL,
            Language.EN,
            NotificationType.SIGNATURE_RESUBMISSION,
            getTemplatePersonalisation,
        )

        // Then
        assertThat(preview).isEqualTo(
            GenerateTemplatePreviewResponse("text", "subject", "html"),
        )
    }
}
