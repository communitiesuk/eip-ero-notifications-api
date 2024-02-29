package uk.gov.dluhc.notificationsapi.mapper

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import uk.gov.dluhc.notificationsapi.dto.NotificationType
import uk.gov.dluhc.notificationsapi.dto.OverseasDocumentTypeDto
import uk.gov.dluhc.notificationsapi.models.OverseasDocumentType

class OverseasDocumentTypeMapperTest {

    private val mapper = OverseasDocumentTypeMapperImpl()

    @ParameterizedTest
    @CsvSource(
        value = [
            "PARENT_MINUS_GUARDIAN, PARENT_GUARDIAN",
            "PREVIOUS_MINUS_ADDRESS, PREVIOUS_ADDRESS",
            "IDENTITY, IDENTITY"
        ]
    )
    fun `should map from overseas document type API to overseas document type DTO`(
        overseasDocumentTypeApi: OverseasDocumentType,
        expected: OverseasDocumentTypeDto
    ) {
        // Given

        // When
        val actual = mapper.fromApiToDto(overseasDocumentTypeApi)

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
        overseasDocumentTypeDto: OverseasDocumentTypeDto,
        expected: NotificationType
    ) {
        // Given

        // When
        val actual = mapper.fromRejectedOverseasDocumentTypeDtoToNotificationTypeDto(overseasDocumentTypeDto)

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
        overseasDocumentTypeDto: OverseasDocumentTypeDto,
        expected: NotificationType
    ) {
        // Given

        // When
        val actual = mapper.fromRequiredOverseasDocumentTypeDtoToNotificationTypeDto(overseasDocumentTypeDto)

        // Then
        assertThat(actual).isEqualTo(expected)
    }
}
