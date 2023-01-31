package uk.gov.dluhc.notificationsapi.messaging.mapper

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildApplicationApprovedPersonalisationDtoFromMessage
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildIdDocumentPersonalisationDtoFromMessage
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildPhotoPersonalisationDtoFromMessage
import uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models.buildApplicationApprovedPersonalisation
import uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models.buildIdDocumentPersonalisationMessage
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

    @Test
    fun `should map SQS IdDocumentResubmissionPersonalisation to IdDocumentPersonalisationDto`() {
        // Given
        val personalisationMessage = buildIdDocumentPersonalisationMessage()
        val expectedPersonalisationDto = buildIdDocumentPersonalisationDtoFromMessage(personalisationMessage)

        // When
        val actual = mapper.toIdDocumentPersonalisationDto(personalisationMessage)

        // Then
        assertThat(actual).usingRecursiveComparison().isEqualTo(expectedPersonalisationDto)
    }

    @Test
    fun `should map SQS Application Approved Personalisation to ApplicationApprovedPersonalisationDto`() {
        // Given
        val personalisationMessage = buildApplicationApprovedPersonalisation()
        val expectedPersonalisationDto = buildApplicationApprovedPersonalisationDtoFromMessage(personalisationMessage)

        // When
        val actual = mapper.toApprovedPersonalisationDto(personalisationMessage)

        // Then
        assertThat(actual).usingRecursiveComparison().isEqualTo(expectedPersonalisationDto)
    }
}
