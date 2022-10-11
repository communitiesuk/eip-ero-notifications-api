package uk.gov.dluhc.notificationsapi.mapper

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.given
import org.mockito.kotlin.verify
import uk.gov.dluhc.notificationsapi.client.mapper.NotificationTemplateMapper
import uk.gov.dluhc.notificationsapi.dto.GenerateTemplatePreviewRequestDto
import uk.gov.dluhc.notificationsapi.dto.NotificationType
import uk.gov.dluhc.notificationsapi.models.GenerateTemplatePreviewRequest
import uk.gov.dluhc.notificationsapi.models.TemplateType

@ExtendWith(MockitoExtension::class)
class GenerateTemplateRequestDtoMapperTest {
    @InjectMocks
    private lateinit var mapper: GenerateTemplatePreviewRequestDtoMapperImpl

    @Mock
    private lateinit var notificationTemplateMapper: NotificationTemplateMapper

    @Mock
    private lateinit var notificationTypeMapper: NotificationTypeMapper

    @ParameterizedTest
    @EnumSource(TemplateType::class)
    fun `should map to dto`(templateType: TemplateType) {
        // Given
        val personalisation = mapOf(
            "subject_param" to "test subject",
            "name_param" to "John",
            "custom_title" to "Resubmitting photo",
        )
        val request = GenerateTemplatePreviewRequest(personalisation)
        val templateId = "20210eee-4592-11ed-b878-0242ac120002"
        val expectedNotificationType = NotificationType.PHOTO_RESUBMISSION
        given(notificationTypeMapper.toNotificationType(any())).willReturn(expectedNotificationType)
        given(notificationTemplateMapper.fromNotificationType(any())).willReturn(templateId)
        val expected = GenerateTemplatePreviewRequestDto(templateId, personalisation)

        // When

        val actual = mapper.toGenerateTemplatePreviewRequestDto(templateType, request)

        // Then
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected)
        verify(notificationTypeMapper).toNotificationType(templateType)
        verify(notificationTemplateMapper).fromNotificationType(expectedNotificationType)
    }
}
