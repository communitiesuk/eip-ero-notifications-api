package uk.gov.dluhc.notificationsapi.mapper

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import uk.gov.dluhc.notificationsapi.database.entity.Channel as ChannelEntity
import uk.gov.dluhc.notificationsapi.dto.CommunicationChannel as CommunicationChannelDto
import uk.gov.dluhc.notificationsapi.messaging.models.CommunicationChannel as CommunicationChannelMessagingApi
import uk.gov.dluhc.notificationsapi.models.CommunicationChannel as CommunicationChannelApi

class CommunicationChannelMapperTest {

    private val mapper = CommunicationChannelMapperImpl()

    @ParameterizedTest
    @CsvSource(
        value = [
            "EMAIL, EMAIL",
            "LETTER, LETTER",
        ],
    )
    fun `should map API notification channel to DTO notification channel`(
        apiCommunicationChannel: CommunicationChannelApi,
        expected: CommunicationChannelDto,
    ) {
        // Given

        // When
        val actual = mapper.fromApiToDto(apiCommunicationChannel)

        // Then
        assertThat(actual).isEqualTo(expected)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "EMAIL, EMAIL",
            "LETTER, LETTER",
        ],
    )
    fun `should map DTO notification channel to API notification channel`(
        communicationChannelDto: CommunicationChannelDto,
        expected: CommunicationChannelApi,
    ) {
        // Given

        // When
        val actual = mapper.fromDtoToApi(communicationChannelDto)

        // Then
        assertThat(actual).isEqualTo(expected)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "EMAIL, EMAIL",
            "LETTER, LETTER",
        ],
    )
    fun `should map Entity notification channel to DTO notification channel`(
        entityCommunicationChannel: ChannelEntity,
        expected: CommunicationChannelDto,
    ) {
        // Given

        // When
        val actual = mapper.fromEntityToDto(entityCommunicationChannel)

        // Then
        assertThat(actual).isEqualTo(expected)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "EMAIL, EMAIL",
            "LETTER, LETTER",
        ],
    )
    fun `should messaging map API notification channel to DTO notification channel`(
        messagingApiCommunicationChannel: CommunicationChannelMessagingApi,
        expected: CommunicationChannelDto,
    ) {
        // Given

        // When
        val actual = mapper.fromMessagingApiToDto(messagingApiCommunicationChannel)

        // Then
        assertThat(actual).isEqualTo(expected)
    }
}
