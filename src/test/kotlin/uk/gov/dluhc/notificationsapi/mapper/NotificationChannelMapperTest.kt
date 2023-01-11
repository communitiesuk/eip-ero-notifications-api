package uk.gov.dluhc.notificationsapi.mapper

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import uk.gov.dluhc.notificationsapi.dto.NotificationChannel as NotificationChannelDto
import uk.gov.dluhc.notificationsapi.models.NotificationChannel as NotificationChannelApi

class NotificationChannelMapperTest {

    private val mapper = NotificationChannelMapperImpl()

    @ParameterizedTest
    @CsvSource(
        value = [
            "EMAIL, EMAIL",
            "LETTER, LETTER",
        ]
    )
    fun `should map API notification channel to DTO notification channel`(
        apiNotificationChannel: NotificationChannelApi,
        expected: NotificationChannelDto
    ) {
        // Given

        // When
        val actual = mapper.fromApiToDto(apiNotificationChannel)

        // Then
        assertThat(actual).isEqualTo(expected)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "EMAIL, EMAIL",
            "LETTER, LETTER",
        ]
    )
    fun `should map DTO notification channel to API notification channel`(
        apiNotificationChannel: NotificationChannelDto,
        expected: NotificationChannelApi
    ) {
        // Given

        // When
        val actual = mapper.fromMessageToDto(apiNotificationChannel)

        // Then
        assertThat(actual).isEqualTo(expected)
    }
}