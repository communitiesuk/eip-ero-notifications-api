package uk.gov.dluhc.notificationsapi.messaging.mapper

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildPhotoPersonalisationDtoFromMessage
import uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models.buildPhotoPersonalisationMessage

internal class TemplatePersonalisationMessageMapperTest {

    private val mapper = TemplatePersonalisationMessageMapperImpl()

    @Test
    fun `should map SQS PhotoResubmissionPersonalisation to PhotoPersonalisationDto`() {
        // Given
        val personalisationMessage = buildPhotoPersonalisationMessage()
        val expectedPersonalisationDto = buildPhotoPersonalisationDtoFromMessage(personalisationMessage)

        // When
        val actual = mapper.toPhotoPersonalisationDto(personalisationMessage)

        // Then
        assertThat(actual).usingRecursiveComparison().isEqualTo(expectedPersonalisationDto)
    }
}
