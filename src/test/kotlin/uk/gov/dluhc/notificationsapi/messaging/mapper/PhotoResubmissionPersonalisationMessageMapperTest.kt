package uk.gov.dluhc.notificationsapi.messaging.mapper

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildPhotoResubmissionPersonalisationDtoFromMessage
import uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models.buildPhotoResubmissionPersonalisationMessage

internal class PhotoResubmissionPersonalisationMessageMapperTest {

    private val mapper = PhotoResubmissionPersonalisationMessageMapperImpl()

    @Test
    fun `should map SQS PhotoResubmissionPersonalisation to PhotoResubmissionPersonalisationDto`() {
        // Given
        val personalisationMessage = buildPhotoResubmissionPersonalisationMessage()
        val expectedPersonalisationDto = buildPhotoResubmissionPersonalisationDtoFromMessage(personalisationMessage)

        // When
        val actualPhotoResubmissionDto = mapper.toPhotoResubmissionPersonalisationDto(personalisationMessage)

        // Then
        assertThat(actualPhotoResubmissionDto).isEqualTo(expectedPersonalisationDto)
    }
}
