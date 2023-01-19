package uk.gov.dluhc.notificationsapi.messaging.mapper

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.given
import org.mockito.kotlin.verify
import uk.gov.dluhc.notificationsapi.dto.SourceType
import uk.gov.dluhc.notificationsapi.mapper.SourceTypeMapper
import uk.gov.dluhc.notificationsapi.messaging.models.RemoveApplicationNotificationsMessage
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aSourceReference
import uk.gov.dluhc.notificationsapi.messaging.models.SourceType as SqsSourceType

@ExtendWith(MockitoExtension::class)
internal class RemoveNotificationsMapperTest {

    @Mock
    private lateinit var sourceTypeMapper: SourceTypeMapper

    @InjectMocks
    private lateinit var removeNotificationsMapper: RemoveNotificationsMapperImpl

    @Test
    fun `should map SQS RemoveApplicationNotificationsMessage to RemoveNotificationsDto`() {
        val sourceReference = aSourceReference()
        val sourceType = SqsSourceType.VOTER_MINUS_CARD
        val request = RemoveApplicationNotificationsMessage(sourceReference = sourceReference, sourceType = sourceType)
        val expectedSourceType = SourceType.VOTER_CARD
        given(sourceTypeMapper.toSourceTypeDto(any())).willReturn(expectedSourceType)

        val removeNotificationsDto = removeNotificationsMapper.toRemoveNotificationsDto(request)

        assertThat(removeNotificationsDto.sourceType).isEqualTo(expectedSourceType)
        assertThat(removeNotificationsDto.sourceReference).isEqualTo(sourceReference)
        verify(sourceTypeMapper).toSourceTypeDto(SqsSourceType.VOTER_MINUS_CARD)
    }
}
