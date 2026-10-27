package uk.gov.dluhc.notificationsapi.mapper

import org.assertj.core.api.Assertions.assertThat
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
import uk.gov.dluhc.notificationsapi.dto.NotificationType.PHOTO_RESUBMISSION
import uk.gov.dluhc.notificationsapi.dto.NotificationType.PHOTO_RESUBMISSION_WITH_REASONS
import uk.gov.dluhc.notificationsapi.models.PhotoRejectionReason
import uk.gov.dluhc.notificationsapi.models.PhotoRejectionReason.EYES_NOT_OPEN_OR_VISIBLE_OR_HAIR_IN_FRONT_FACE
import uk.gov.dluhc.notificationsapi.models.PhotoRejectionReason.NOT_A_PLAIN_FACIAL_EXPRESSION
import uk.gov.dluhc.notificationsapi.models.PhotoRejectionReason.NOT_FACING_FORWARDS_OR_LOOKING_AT_THE_CAMERA
import uk.gov.dluhc.notificationsapi.models.PhotoRejectionReason.OTHER
import uk.gov.dluhc.notificationsapi.models.PhotoRejectionReason.OTHER_OBJECTS_OR_PEOPLE_IN_PHOTO
import uk.gov.dluhc.notificationsapi.models.PhotoRejectionReason.PHOTO_HAS_HEAD_COVERING_ASIDE_FROM_RELIGIOUS_OR_MEDICAL
import uk.gov.dluhc.notificationsapi.models.PhotoRejectionReason.PHOTO_HAS_RED_EYE_GLARE_OR_SHADOWS_OVER_FACE
import uk.gov.dluhc.notificationsapi.models.PhotoRejectionReason.PHOTO_NOT_IN_COLOUR_DISTORTED_OR_TOO_DARK
import uk.gov.dluhc.notificationsapi.models.PhotoRejectionReason.WEARING_SUNGLASSES_OR_TINTED_GLASSES
import uk.gov.dluhc.notificationsapi.models.SourceType.VOTER_CARD
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aCommunicationChannel
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aSourceType
import uk.gov.dluhc.notificationsapi.testsupport.testdata.api.buildGeneratePhotoResubmissionTemplatePreviewRequest
import uk.gov.dluhc.notificationsapi.testsupport.testdata.api.buildPhotoResubmissionPersonalisationRequest
import java.util.stream.Stream

@ExtendWith(MockitoExtension::class)
class PhotoResubmissionTemplatePreviewDtoMapperNotificationTypeTest {

    @InjectMocks
    private lateinit var mapper: PhotoResubmissionTemplatePreviewDtoMapperImpl

    @Mock
    private lateinit var languageMapper: LanguageMapper

    @Mock
    private lateinit var channelMapper: CommunicationChannelMapper

    @Mock
    private lateinit var sourceTypeMapper: SourceTypeMapper

    @Mock
    private lateinit var photoRejectionReasonMapper: PhotoRejectionReasonMapper

    companion object {
        @JvmStatic
        fun photoRejectionReasons_to_NotificationType(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(emptyList<PhotoRejectionReason>(), PHOTO_RESUBMISSION),
                Arguments.of(listOf(OTHER), PHOTO_RESUBMISSION),
                Arguments.of(listOf(OTHER), PHOTO_RESUBMISSION),

                Arguments.of(
                    listOf(
                        NOT_FACING_FORWARDS_OR_LOOKING_AT_THE_CAMERA,
                    ),
                    PHOTO_RESUBMISSION_WITH_REASONS,
                ),
                Arguments.of(
                    listOf(PHOTO_NOT_IN_COLOUR_DISTORTED_OR_TOO_DARK),
                    PHOTO_RESUBMISSION_WITH_REASONS,
                ),
                Arguments.of(
                    listOf(OTHER_OBJECTS_OR_PEOPLE_IN_PHOTO),
                    PHOTO_RESUBMISSION_WITH_REASONS,
                ),
                Arguments.of(
                    listOf(NOT_A_PLAIN_FACIAL_EXPRESSION),
                    PHOTO_RESUBMISSION_WITH_REASONS,
                ),
                Arguments.of(
                    listOf(
                        EYES_NOT_OPEN_OR_VISIBLE_OR_HAIR_IN_FRONT_FACE,
                    ),
                    PHOTO_RESUBMISSION_WITH_REASONS,
                ),
                Arguments.of(
                    listOf(WEARING_SUNGLASSES_OR_TINTED_GLASSES),
                    PHOTO_RESUBMISSION_WITH_REASONS,
                ),
                Arguments.of(
                    listOf(
                        PHOTO_HAS_HEAD_COVERING_ASIDE_FROM_RELIGIOUS_OR_MEDICAL,
                    ),
                    PHOTO_RESUBMISSION_WITH_REASONS,
                ),
                Arguments.of(
                    listOf(
                        PHOTO_HAS_RED_EYE_GLARE_OR_SHADOWS_OVER_FACE,
                    ),
                    PHOTO_RESUBMISSION_WITH_REASONS,
                ),

                Arguments.of(
                    listOf(
                        OTHER,
                        NOT_FACING_FORWARDS_OR_LOOKING_AT_THE_CAMERA,
                    ),
                    PHOTO_RESUBMISSION_WITH_REASONS,
                ),
                Arguments.of(
                    listOf(
                        OTHER,
                        PHOTO_NOT_IN_COLOUR_DISTORTED_OR_TOO_DARK,
                    ),
                    PHOTO_RESUBMISSION_WITH_REASONS,
                ),
                Arguments.of(
                    listOf(
                        OTHER,
                        OTHER_OBJECTS_OR_PEOPLE_IN_PHOTO,
                    ),
                    PHOTO_RESUBMISSION_WITH_REASONS,
                ),
                Arguments.of(
                    listOf(
                        OTHER,
                        NOT_A_PLAIN_FACIAL_EXPRESSION,
                    ),
                    PHOTO_RESUBMISSION_WITH_REASONS,
                ),
                Arguments.of(
                    listOf(
                        OTHER,
                        EYES_NOT_OPEN_OR_VISIBLE_OR_HAIR_IN_FRONT_FACE,
                    ),
                    PHOTO_RESUBMISSION_WITH_REASONS,
                ),
                Arguments.of(
                    listOf(
                        OTHER,
                        WEARING_SUNGLASSES_OR_TINTED_GLASSES,
                    ),
                    PHOTO_RESUBMISSION_WITH_REASONS,
                ),
                Arguments.of(
                    listOf(
                        OTHER,
                        PHOTO_HAS_HEAD_COVERING_ASIDE_FROM_RELIGIOUS_OR_MEDICAL,
                    ),
                    PHOTO_RESUBMISSION_WITH_REASONS,
                ),
                Arguments.of(
                    listOf(
                        OTHER,
                        PHOTO_HAS_RED_EYE_GLARE_OR_SHADOWS_OVER_FACE,
                    ),
                    PHOTO_RESUBMISSION_WITH_REASONS,
                ),
            )
        }
    }

    @ParameterizedTest
    @MethodSource("photoRejectionReasons_to_NotificationType")
    fun `should map photo template request to dto with correct NotificationType mapping given rejection reasons and no rejection notes`(
        photoRejectionReasons: List<PhotoRejectionReason>,
        expectedNotificationType: NotificationType,
    ) {
        // Given
        val request = buildGeneratePhotoResubmissionTemplatePreviewRequest(
            sourceType = VOTER_CARD,
            personalisation = buildPhotoResubmissionPersonalisationRequest(
                photoRejectionReasons = photoRejectionReasons,
                photoRejectionNotes = null,
            ),
        )

        given(languageMapper.fromApiToDto(any())).willReturn(LanguageDto.ENGLISH)
        given(channelMapper.fromApiToDto(any())).willReturn(aCommunicationChannel())
        given(sourceTypeMapper.fromApiToDto(any())).willReturn(aSourceType())

        // When
        val actual = mapper.toPhotoResubmissionTemplatePreviewDto(request)

        // Then
        assertThat(actual.notificationType).isEqualTo(expectedNotificationType)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            ", PHOTO_RESUBMISSION",
            "'', PHOTO_RESUBMISSION",
            "'Some rejection reason notes', PHOTO_RESUBMISSION_WITH_REASONS",
        ],
    )
    fun `should map photo template request to dto with correct NotificationType mapping given no rejection reasons and rejection notes`(
        photoRejectionNotes: String?,
        expectedNotificationType: NotificationType,
    ) {
        // Given
        val request = buildGeneratePhotoResubmissionTemplatePreviewRequest(
            sourceType = VOTER_CARD,
            personalisation = buildPhotoResubmissionPersonalisationRequest(
                photoRejectionReasons = emptyList(),
                photoRejectionNotes = photoRejectionNotes,
            ),
        )

        given(languageMapper.fromApiToDto(any())).willReturn(LanguageDto.ENGLISH)
        given(channelMapper.fromApiToDto(any())).willReturn(aCommunicationChannel())
        given(sourceTypeMapper.fromApiToDto(any())).willReturn(aSourceType())

        // When
        val actual = mapper.toPhotoResubmissionTemplatePreviewDto(request)

        // Then
        assertThat(actual.notificationType).isEqualTo(expectedNotificationType)
    }
}
