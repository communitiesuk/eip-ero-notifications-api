package uk.gov.dluhc.notificationsapi.mapper

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.given
import uk.gov.dluhc.notificationsapi.dto.LanguageDto
import uk.gov.dluhc.notificationsapi.dto.NotificationType
import uk.gov.dluhc.notificationsapi.models.SignatureRejectionReason
import uk.gov.dluhc.notificationsapi.models.SourceType
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aCommunicationChannel
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aSourceType
import uk.gov.dluhc.notificationsapi.testsupport.testdata.api.buildGenerateRejectedSignatureTemplatePreviewRequest
import uk.gov.dluhc.notificationsapi.testsupport.testdata.api.buildRejectedSignaturePersonalisation
import java.util.stream.Stream

@ExtendWith(MockitoExtension::class)
class RejectedSignatureTemplatePreviewDtoMapper_NotificationTypeTest {

    @InjectMocks
    private lateinit var mapper: RejectedSignatureTemplatePreviewDtoMapperImpl

    @Mock
    private lateinit var languageMapper: LanguageMapper

    @Mock
    private lateinit var communicationChannelMapper: CommunicationChannelMapper

    @Mock
    private lateinit var sourceTypeMapper: SourceTypeMapper

    @Mock
    private lateinit var signatureRejectionReasonMapper: SignatureRejectionReasonMapper

    companion object {
        @JvmStatic
        fun rejectionReasons_to_NotificationType(): Stream<Arguments> {
            val parameters = Stream.builder<Arguments>()

            listOf(SourceType.POSTAL, SourceType.PROXY).forEach { sourceType ->
                parameters
                    .add(Arguments.of(emptyList<SignatureRejectionReason>(), NotificationType.REJECTED_SIGNATURE, sourceType))
                    .add(Arguments.of(listOf(SignatureRejectionReason.OTHER), NotificationType.REJECTED_SIGNATURE, sourceType))
                    .add(Arguments.of(listOf(SignatureRejectionReason.OTHER), NotificationType.REJECTED_SIGNATURE, sourceType))

                SignatureRejectionReason.values()
                    .filterNot { reason -> reason == SignatureRejectionReason.OTHER }
                    .forEach { rejectionReason ->
                        parameters.add(
                            Arguments.of(
                                listOf(rejectionReason),
                                NotificationType.REJECTED_SIGNATURE_WITH_REASONS,
                                sourceType,
                            ),
                        )
                        parameters.add(
                            Arguments.of(
                                listOf(rejectionReason, SignatureRejectionReason.OTHER),
                                NotificationType.REJECTED_SIGNATURE_WITH_REASONS,
                                sourceType,
                            ),
                        )
                    }
            }
            return parameters.build()
        }
    }

    @ParameterizedTest
    @MethodSource("rejectionReasons_to_NotificationType")
    fun `should map rejected signature template request to dto with correct NotificationType mapping given rejection reasons and no rejection notes`(
        rejectionReasons: List<SignatureRejectionReason>,
        expectedNotificationType: NotificationType,
        sourceType: SourceType,
    ) {
        // Given
        val request = buildGenerateRejectedSignatureTemplatePreviewRequest(
            sourceType = sourceType,
            personalisation = buildRejectedSignaturePersonalisation(
                rejectionReasons = rejectionReasons,
                rejectionNotes = null,
            ),
        )

        given(languageMapper.fromApiToDto(any())).willReturn(LanguageDto.ENGLISH)
        given(communicationChannelMapper.fromApiToDto(any())).willReturn(aCommunicationChannel())
        given(sourceTypeMapper.fromApiToDto(any())).willReturn(aSourceType())
        given(sourceTypeMapper.toSourceTypeString(sourceType, LanguageDto.ENGLISH)).willReturn("Mapped source type")

        // When
        val actual = mapper.toRejectedSignatureTemplatePreviewDto(request)

        // Then
        Assertions.assertThat(actual.notificationType).isEqualTo(expectedNotificationType)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            ", REJECTED_SIGNATURE,POSTAL",
            "'', REJECTED_SIGNATURE,POSTAL",
            "'Some rejection reason notes', REJECTED_SIGNATURE_WITH_REASONS,POSTAL",
            ", REJECTED_SIGNATURE,PROXY",
            "'', REJECTED_SIGNATURE,PROXY",
            "'Some rejection reason notes', REJECTED_SIGNATURE_WITH_REASONS,PROXY",
        ],
    )
    fun `should map rejected signature template request to dto with correct NotificationType mapping given no rejection reasons and rejection notes`(
        rejectionNotes: String?,
        expectedNotificationType: NotificationType,
        sourceType: SourceType,
    ) {
        // Given
        val request = buildGenerateRejectedSignatureTemplatePreviewRequest(
            sourceType = sourceType,
            personalisation = buildRejectedSignaturePersonalisation(
                rejectionReasons = emptyList(),
                rejectionNotes = rejectionNotes,
            ),
        )

        given(languageMapper.fromApiToDto(any())).willReturn(LanguageDto.ENGLISH)
        given(communicationChannelMapper.fromApiToDto(any())).willReturn(aCommunicationChannel())
        given(sourceTypeMapper.fromApiToDto(any())).willReturn(aSourceType())
        given(sourceTypeMapper.toSourceTypeString(sourceType, LanguageDto.ENGLISH)).willReturn("Mapped source type")

        // When
        val actual = mapper.toRejectedSignatureTemplatePreviewDto(request)

        // Then
        Assertions.assertThat(actual.notificationType).isEqualTo(expectedNotificationType)
    }
}
