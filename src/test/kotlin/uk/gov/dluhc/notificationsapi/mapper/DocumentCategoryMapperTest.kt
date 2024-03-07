package uk.gov.dluhc.notificationsapi.mapper

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import uk.gov.dluhc.notificationsapi.dto.DocumentCategoryDto
import uk.gov.dluhc.notificationsapi.dto.NotificationType
import uk.gov.dluhc.notificationsapi.models.DocumentCategory

class DocumentCategoryMapperTest {

    private val mapper = DocumentCategoryMapperImpl()

    @ParameterizedTest
    @CsvSource(
        value = [
            "PARENT_MINUS_GUARDIAN, PARENT_GUARDIAN",
            "PREVIOUS_MINUS_ADDRESS, PREVIOUS_ADDRESS",
            "IDENTITY, IDENTITY"
        ]
    )
    fun `should map from overseas document type API to overseas document type DTO`(
        documentCategoryApi: DocumentCategory,
        expected: DocumentCategoryDto
    ) {
        // Given

        // When
        val actual = mapper.fromApiToDto(documentCategoryApi)

        // Then
        assertThat(actual).isEqualTo(expected)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "IDENTITY, REJECTED_DOCUMENT",
            "PARENT_GUARDIAN, REJECTED_PARENT_GUARDIAN",
            "PREVIOUS_ADDRESS, REJECTED_PREVIOUS_ADDRESS"
        ]
    )
    fun `should map rejected overseas document to notification type`(
        documentCategoryDto: DocumentCategoryDto,
        expected: NotificationType
    ) {
        // Given

        // When
        val actual = mapper.fromRejectedOverseasDocumentCategoryDtoToNotificationTypeDto(documentCategoryDto)

        // Then
        assertThat(actual).isEqualTo(expected)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "IDENTITY, NINO_NOT_MATCHED",
            "PARENT_GUARDIAN, PARENT_GUARDIAN_PROOF_REQUIRED",
            "PREVIOUS_ADDRESS, PREVIOUS_ADDRESS_DOCUMENT_REQUIRED"
        ]
    )
    fun `should map required overseas document to notification type`(
        documentCategoryDto: DocumentCategoryDto,
        expected: NotificationType
    ) {
        // Given

        // When
        val actual = mapper.fromRequiredOverseasDocumentCategoryDtoToNotificationTypeDto(documentCategoryDto)

        // Then
        assertThat(actual).isEqualTo(expected)
    }
}
