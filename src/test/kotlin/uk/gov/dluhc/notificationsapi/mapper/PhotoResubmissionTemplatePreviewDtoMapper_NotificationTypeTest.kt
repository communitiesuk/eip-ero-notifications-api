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
import uk.gov.dluhc.notificationsapi.models.PhotoRejectionReason.EYES_MINUS_NOT_MINUS_OPEN_MINUS_OR_MINUS_VISIBLE_MINUS_OR_MINUS_HAIR_MINUS_IN_MINUS_FRONT_MINUS_FACE
import uk.gov.dluhc.notificationsapi.models.PhotoRejectionReason.NOT_MINUS_A_MINUS_PLAIN_MINUS_FACIAL_MINUS_EXPRESSION
import uk.gov.dluhc.notificationsapi.models.PhotoRejectionReason.NOT_MINUS_FACING_MINUS_FORWARDS_MINUS_OR_MINUS_LOOKING_MINUS_AT_MINUS_THE_MINUS_CAMERA
import uk.gov.dluhc.notificationsapi.models.PhotoRejectionReason.OTHER
import uk.gov.dluhc.notificationsapi.models.PhotoRejectionReason.OTHER_MINUS_OBJECTS_MINUS_OR_MINUS_PEOPLE_MINUS_IN_MINUS_PHOTO
import uk.gov.dluhc.notificationsapi.models.PhotoRejectionReason.PHOTO_MINUS_HAS_MINUS_HEAD_MINUS_COVERING_MINUS_ASIDE_MINUS_FROM_MINUS_RELIGIOUS_MINUS_OR_MINUS_MEDICAL
import uk.gov.dluhc.notificationsapi.models.PhotoRejectionReason.PHOTO_MINUS_HAS_MINUS_RED_MINUS_EYE_MINUS_GLARE_MINUS_OR_MINUS_SHADOWS_MINUS_OVER_MINUS_FACE
import uk.gov.dluhc.notificationsapi.models.PhotoRejectionReason.PHOTO_MINUS_NOT_MINUS_IN_MINUS_COLOUR_MINUS_DISTORTED_MINUS_OR_MINUS_TOO_MINUS_DARK
import uk.gov.dluhc.notificationsapi.models.PhotoRejectionReason.WEARING_MINUS_SUNGLASSES_MINUS_OR_MINUS_TINTED_MINUS_GLASSES
import uk.gov.dluhc.notificationsapi.models.SourceType.VOTER_MINUS_CARD
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aNotificationChannel
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aSourceType
import uk.gov.dluhc.notificationsapi.testsupport.testdata.api.buildGeneratePhotoResubmissionTemplatePreviewRequest
import uk.gov.dluhc.notificationsapi.testsupport.testdata.api.buildPhotoResubmissionPersonalisationRequest
import java.util.stream.Stream

@ExtendWith(MockitoExtension::class)
class PhotoResubmissionTemplatePreviewDtoMapper_NotificationTypeTest {

    @InjectMocks
    private lateinit var mapper: PhotoResubmissionTemplatePreviewDtoMapperImpl

    @Mock
    private lateinit var languageMapper: LanguageMapper

    @Mock
    private lateinit var channelMapper: NotificationChannelMapper

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
                        NOT_MINUS_FACING_MINUS_FORWARDS_MINUS_OR_MINUS_LOOKING_MINUS_AT_MINUS_THE_MINUS_CAMERA,
                    ),
                    PHOTO_RESUBMISSION_WITH_REASONS,
                ),
                Arguments.of(
                    listOf(PHOTO_MINUS_NOT_MINUS_IN_MINUS_COLOUR_MINUS_DISTORTED_MINUS_OR_MINUS_TOO_MINUS_DARK),
                    PHOTO_RESUBMISSION_WITH_REASONS,
                ),
                Arguments.of(
                    listOf(OTHER_MINUS_OBJECTS_MINUS_OR_MINUS_PEOPLE_MINUS_IN_MINUS_PHOTO),
                    PHOTO_RESUBMISSION_WITH_REASONS,
                ),
                Arguments.of(
                    listOf(NOT_MINUS_A_MINUS_PLAIN_MINUS_FACIAL_MINUS_EXPRESSION),
                    PHOTO_RESUBMISSION_WITH_REASONS,
                ),
                Arguments.of(
                    listOf(
                        EYES_MINUS_NOT_MINUS_OPEN_MINUS_OR_MINUS_VISIBLE_MINUS_OR_MINUS_HAIR_MINUS_IN_MINUS_FRONT_MINUS_FACE,
                    ),
                    PHOTO_RESUBMISSION_WITH_REASONS,
                ),
                Arguments.of(
                    listOf(WEARING_MINUS_SUNGLASSES_MINUS_OR_MINUS_TINTED_MINUS_GLASSES),
                    PHOTO_RESUBMISSION_WITH_REASONS,
                ),
                Arguments.of(
                    listOf(
                        PHOTO_MINUS_HAS_MINUS_HEAD_MINUS_COVERING_MINUS_ASIDE_MINUS_FROM_MINUS_RELIGIOUS_MINUS_OR_MINUS_MEDICAL,
                    ),
                    PHOTO_RESUBMISSION_WITH_REASONS,
                ),
                Arguments.of(
                    listOf(
                        PHOTO_MINUS_HAS_MINUS_RED_MINUS_EYE_MINUS_GLARE_MINUS_OR_MINUS_SHADOWS_MINUS_OVER_MINUS_FACE,
                    ),
                    PHOTO_RESUBMISSION_WITH_REASONS,
                ),

                Arguments.of(
                    listOf(
                        OTHER,
                        NOT_MINUS_FACING_MINUS_FORWARDS_MINUS_OR_MINUS_LOOKING_MINUS_AT_MINUS_THE_MINUS_CAMERA,
                    ),
                    PHOTO_RESUBMISSION_WITH_REASONS,
                ),
                Arguments.of(
                    listOf(
                        OTHER,
                        PHOTO_MINUS_NOT_MINUS_IN_MINUS_COLOUR_MINUS_DISTORTED_MINUS_OR_MINUS_TOO_MINUS_DARK,
                    ),
                    PHOTO_RESUBMISSION_WITH_REASONS,
                ),
                Arguments.of(
                    listOf(
                        OTHER,
                        OTHER_MINUS_OBJECTS_MINUS_OR_MINUS_PEOPLE_MINUS_IN_MINUS_PHOTO,
                    ),
                    PHOTO_RESUBMISSION_WITH_REASONS,
                ),
                Arguments.of(
                    listOf(
                        OTHER,
                        NOT_MINUS_A_MINUS_PLAIN_MINUS_FACIAL_MINUS_EXPRESSION,
                    ),
                    PHOTO_RESUBMISSION_WITH_REASONS,
                ),
                Arguments.of(
                    listOf(
                        OTHER,
                        EYES_MINUS_NOT_MINUS_OPEN_MINUS_OR_MINUS_VISIBLE_MINUS_OR_MINUS_HAIR_MINUS_IN_MINUS_FRONT_MINUS_FACE,
                    ),
                    PHOTO_RESUBMISSION_WITH_REASONS,
                ),
                Arguments.of(
                    listOf(
                        OTHER,
                        WEARING_MINUS_SUNGLASSES_MINUS_OR_MINUS_TINTED_MINUS_GLASSES,
                    ),
                    PHOTO_RESUBMISSION_WITH_REASONS,
                ),
                Arguments.of(
                    listOf(
                        OTHER,
                        PHOTO_MINUS_HAS_MINUS_HEAD_MINUS_COVERING_MINUS_ASIDE_MINUS_FROM_MINUS_RELIGIOUS_MINUS_OR_MINUS_MEDICAL,
                    ),
                    PHOTO_RESUBMISSION_WITH_REASONS,
                ),
                Arguments.of(
                    listOf(
                        OTHER,
                        PHOTO_MINUS_HAS_MINUS_RED_MINUS_EYE_MINUS_GLARE_MINUS_OR_MINUS_SHADOWS_MINUS_OVER_MINUS_FACE,
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
            sourceType = VOTER_MINUS_CARD,
            personalisation = buildPhotoResubmissionPersonalisationRequest(
                photoRejectionReasons = photoRejectionReasons,
                photoRejectionNotes = null,
            ),
        )

        given(languageMapper.fromApiToDto(any())).willReturn(LanguageDto.ENGLISH)
        given(channelMapper.fromApiToDto(any())).willReturn(aNotificationChannel())
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
            sourceType = VOTER_MINUS_CARD,
            personalisation = buildPhotoResubmissionPersonalisationRequest(
                photoRejectionReasons = emptyList(),
                photoRejectionNotes = photoRejectionNotes,
            ),
        )

        given(languageMapper.fromApiToDto(any())).willReturn(LanguageDto.ENGLISH)
        given(channelMapper.fromApiToDto(any())).willReturn(aNotificationChannel())
        given(sourceTypeMapper.fromApiToDto(any())).willReturn(aSourceType())

        // When
        val actual = mapper.toPhotoResubmissionTemplatePreviewDto(request)

        // Then
        assertThat(actual.notificationType).isEqualTo(expectedNotificationType)
    }
}
