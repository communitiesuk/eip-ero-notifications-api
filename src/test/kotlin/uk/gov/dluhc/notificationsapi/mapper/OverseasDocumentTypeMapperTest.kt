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
            "QUALIFYING_MINUS_ADDRESS, QUALIFYING_ADDRESS",
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
            "QUALIFYING_ADDRESS, REJECTED_QUALIFYING_ADDRESS"
        ]
    )
    fun `should map from overseas document type DTO to notification type`(
        overseasDocumentTypeDto: OverseasDocumentTypeDto,
        expected: NotificationType
    ) {
        // Given

        // When
        val actual = mapper.fromOverseasDocumentTypeDtoToNotificationTypeDto(overseasDocumentTypeDto)

        // Then
        assertThat(actual).isEqualTo(expected)
    }
}
