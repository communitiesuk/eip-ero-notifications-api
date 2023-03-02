package uk.gov.dluhc.notificationsapi.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.given
import org.mockito.kotlin.verify
import uk.gov.dluhc.notificationsapi.database.entity.SourceType
import uk.gov.dluhc.notificationsapi.database.repository.NotificationRepository
import uk.gov.dluhc.notificationsapi.mapper.NotificationSummaryMapper
import uk.gov.dluhc.notificationsapi.mapper.SourceTypeMapper
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aGssCode
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aRandomNotificationId
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aSourceReference
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aSourceType
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aValidKnownEroId
import uk.gov.dluhc.notificationsapi.testsupport.testdata.anotherGssCode
import uk.gov.dluhc.notificationsapi.testsupport.testdata.database.entity.aNotificationSummaryBuilder
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.aNotificationSummaryDtoBuilder

@ExtendWith(MockitoExtension::class)
class SentNotificationsServiceTest {

    @InjectMocks
    private lateinit var service: SentNotificationsService

    @Mock
    private lateinit var eroService: EroService

    @Mock
    private lateinit var notificationRepository: NotificationRepository

    @Mock
    private lateinit var sourceTypeMapper: SourceTypeMapper

    @Mock
    private lateinit var notificationSummaryMapper: NotificationSummaryMapper

    @Test
    fun `should get Notification Summaries for application`() {
        // Given
        val sourceReference = aSourceReference()
        val eroId = aValidKnownEroId()
        val sourceType = aSourceType()

        val gssCodes = listOf(aGssCode(), anotherGssCode())
        given(eroService.lookupGssCodesForEro(any())).willReturn(gssCodes)
        given(sourceTypeMapper.fromDtoToEntity(any())).willReturn(SourceType.VOTER_CARD)

        val notificationSummaryEntity1 = aNotificationSummaryBuilder(
            id = aRandomNotificationId(),
            gssCode = gssCodes[0],
            requestor = "vc-admin-1@some-ero.gov.uk"
        )
        val notificationSummaryEntity2 = aNotificationSummaryBuilder(
            id = aRandomNotificationId(),
            gssCode = gssCodes[0],
            requestor = "vc-admin-2@some-ero.gov.uk"
        )
        given(notificationRepository.getNotificationSummariesBySourceReference(any(), any(), any())).willReturn(
            listOf(notificationSummaryEntity1, notificationSummaryEntity2)
        )

        val notificationSummaryDto1 = aNotificationSummaryDtoBuilder(
            gssCode = gssCodes[0],
            requestor = "vc-admin-1@some-ero.gov.uk"
        )
        val notificationSummaryDto2 = aNotificationSummaryDtoBuilder(
            gssCode = gssCodes[0],
            requestor = "vc-admin-2@some-ero.gov.uk"
        )
        given(notificationSummaryMapper.toNotificationSummaryDto(any())).willReturn(
            notificationSummaryDto1, notificationSummaryDto2
        )

        val expected = listOf(notificationSummaryDto1, notificationSummaryDto2)

        // When
        val actual = service.getNotificationSummariesForApplication(sourceReference, eroId, sourceType)

        // Then
        assertThat(actual).isEqualTo(expected)
        verify(eroService).lookupGssCodesForEro(eroId)
        verify(sourceTypeMapper).fromDtoToEntity(sourceType)
        verify(notificationRepository).getNotificationSummariesBySourceReference(sourceReference, SourceType.VOTER_CARD, gssCodes)
        verify(notificationSummaryMapper).toNotificationSummaryDto(notificationSummaryEntity1)
        verify(notificationSummaryMapper).toNotificationSummaryDto(notificationSummaryEntity2)
    }
}
