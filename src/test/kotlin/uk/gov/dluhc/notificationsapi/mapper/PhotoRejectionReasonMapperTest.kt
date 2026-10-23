package uk.gov.dluhc.notificationsapi.mapper

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.context.support.ResourceBundleMessageSource
import uk.gov.dluhc.notificationsapi.dto.LanguageDto
import uk.gov.dluhc.notificationsapi.messaging.models.PhotoRejectionReason as PhotoRejectionReasonMessageEnum
import uk.gov.dluhc.notificationsapi.models.PhotoRejectionReason as PhotoRejectionReasonApiEnum

class PhotoRejectionReasonMapperTest {

    private val messageSource = ResourceBundleMessageSource().apply {
        setBasenames("messages")
        setDefaultEncoding("UTF-8")
        setFallbackToSystemLocale(true)
    }
    private val mapper = PhotoRejectionReasonMapper(messageSource)

    @Nested
    inner class ToPhotoRejectionReasonStringFromApiEnum {
        @ParameterizedTest
        @CsvSource(
            value = [
                "NOT_FACING_FORWARDS_OR_LOOKING_AT_THE_CAMERA, 'Not facing forwards or looking straight at the camera'",
                "PHOTO_NOT_IN_COLOUR_DISTORTED_OR_TOO_DARK, 'The photo is not in colour, is distorted or is too dark'",
                "OTHER_OBJECTS_OR_PEOPLE_IN_PHOTO, 'There are other people or objects in the photo'",
                "NOT_A_PLAIN_FACIAL_EXPRESSION, 'Not a plain facial expression'",
                "EYES_NOT_OPEN_OR_VISIBLE_OR_HAIR_IN_FRONT_FACE, 'Eyes are not open, visible or there is hair in front of the face'",
                "WEARING_SUNGLASSES_OR_TINTED_GLASSES, 'Wearing sunglasses, or tinted glasses'",
                "PHOTO_HAS_HEAD_COVERING_ASIDE_FROM_RELIGIOUS_OR_MEDICAL, 'The photo has a head covering (aside from religious or medical)'",
                "PHOTO_HAS_RED_EYE_GLARE_OR_SHADOWS_OVER_FACE, 'The photo has ''red-eye'', glare or shadows over face'",
            ],
        )
        fun `should map enums to human readable messages in English`(
            rejectionReason: PhotoRejectionReasonApiEnum,
            expected: String,
        ) {
            // Given

            // When
            val actual = mapper.toPhotoRejectionReasonString(rejectionReason, LanguageDto.ENGLISH)

            // Then
            assertThat(actual).isEqualTo(expected)
        }

        @ParameterizedTest
        @CsvSource(
            value = [
                "NOT_FACING_FORWARDS_OR_LOOKING_AT_THE_CAMERA, 'Nid yw''n gwynebu ymlaen nac edrych yn syth ar y camera'",
                "PHOTO_NOT_IN_COLOUR_DISTORTED_OR_TOO_DARK, 'Nid yw''r llun mewn lliw, mae''n ystumiedig neu''n rhy dywyll'",
                "OTHER_OBJECTS_OR_PEOPLE_IN_PHOTO, 'Mae yna bobl neu wrthrychau eraill yn y llun'",
                "NOT_A_PLAIN_FACIAL_EXPRESSION, 'Nid yw''r mynegiant wyneb yn blaen'",
                "EYES_NOT_OPEN_OR_VISIBLE_OR_HAIR_IN_FRONT_FACE, 'Nid yw''r llygaid yn agored, yn weladwy neu mae gwallt o flaen yr wyneb'",
                "WEARING_SUNGLASSES_OR_TINTED_GLASSES, 'Gwisgo sbectol haul, neu sbectol arlliw'",
                "PHOTO_HAS_HEAD_COVERING_ASIDE_FROM_RELIGIOUS_OR_MEDICAL, 'Mae gan y llun orchudd pen (ar wahân i orchudd crefyddol neu feddygol)'",
                "PHOTO_HAS_RED_EYE_GLARE_OR_SHADOWS_OVER_FACE, 'Mae gan y llun ''lygad coch'', llacharedd neu gysgodion dros yr wyneb'",
            ],
        )
        fun `should map enums to human readable messages in Welsh`(
            rejectionReason: PhotoRejectionReasonApiEnum,
            expected: String,
        ) {
            // Given

            // When
            val actual = mapper.toPhotoRejectionReasonString(rejectionReason, LanguageDto.WELSH)

            // Then
            assertThat(actual).isEqualTo(expected)
        }
    }

    @Nested
    inner class ToPhotoRejectionReasonStringFromMessageEnum {
        @ParameterizedTest
        @CsvSource(
            value = [
                "NOT_FACING_FORWARDS_OR_LOOKING_AT_THE_CAMERA, 'Not facing forwards or looking straight at the camera'",
                "PHOTO_NOT_IN_COLOUR_DISTORTED_OR_TOO_DARK, 'The photo is not in colour, is distorted or is too dark'",
                "OTHER_OBJECTS_OR_PEOPLE_IN_PHOTO, 'There are other people or objects in the photo'",
                "NOT_A_PLAIN_FACIAL_EXPRESSION, 'Not a plain facial expression'",
                "EYES_NOT_OPEN_OR_VISIBLE_OR_HAIR_IN_FRONT_FACE, 'Eyes are not open, visible or there is hair in front of the face'",
                "WEARING_SUNGLASSES_OR_TINTED_GLASSES, 'Wearing sunglasses, or tinted glasses'",
                "PHOTO_HAS_HEAD_COVERING_ASIDE_FROM_RELIGIOUS_OR_MEDICAL, 'The photo has a head covering (aside from religious or medical)'",
                "PHOTO_HAS_RED_EYE_GLARE_OR_SHADOWS_OVER_FACE, 'The photo has ''red-eye'', glare or shadows over face'",
            ],
        )
        fun `should map enums to human readable messages in English`(
            rejectionReason: PhotoRejectionReasonMessageEnum,
            expected: String,
        ) {
            // Given

            // When
            val actual = mapper.toPhotoRejectionReasonString(rejectionReason, LanguageDto.ENGLISH)

            // Then
            assertThat(actual).isEqualTo(expected)
        }

        @ParameterizedTest
        @CsvSource(
            value = [
                "NOT_FACING_FORWARDS_OR_LOOKING_AT_THE_CAMERA, 'Nid yw''n gwynebu ymlaen nac edrych yn syth ar y camera'",
                "PHOTO_NOT_IN_COLOUR_DISTORTED_OR_TOO_DARK, 'Nid yw''r llun mewn lliw, mae''n ystumiedig neu''n rhy dywyll'",
                "OTHER_OBJECTS_OR_PEOPLE_IN_PHOTO, 'Mae yna bobl neu wrthrychau eraill yn y llun'",
                "NOT_A_PLAIN_FACIAL_EXPRESSION, 'Nid yw''r mynegiant wyneb yn blaen'",
                "EYES_NOT_OPEN_OR_VISIBLE_OR_HAIR_IN_FRONT_FACE, 'Nid yw''r llygaid yn agored, yn weladwy neu mae gwallt o flaen yr wyneb'",
                "WEARING_SUNGLASSES_OR_TINTED_GLASSES, 'Gwisgo sbectol haul, neu sbectol arlliw'",
                "PHOTO_HAS_HEAD_COVERING_ASIDE_FROM_RELIGIOUS_OR_MEDICAL, 'Mae gan y llun orchudd pen (ar wahân i orchudd crefyddol neu feddygol)'",
                "PHOTO_HAS_RED_EYE_GLARE_OR_SHADOWS_OVER_FACE, 'Mae gan y llun ''lygad coch'', llacharedd neu gysgodion dros yr wyneb'",
            ],
        )
        fun `should map enums to human readable messages in Welsh`(
            rejectionReason: PhotoRejectionReasonMessageEnum,
            expected: String,
        ) {
            // Given

            // When
            val actual = mapper.toPhotoRejectionReasonString(rejectionReason, LanguageDto.WELSH)

            // Then
            assertThat(actual).isEqualTo(expected)
        }
    }
}
