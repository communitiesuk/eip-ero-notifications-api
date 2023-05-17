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
                "NOT_MINUS_FACING_MINUS_FORWARDS_MINUS_OR_MINUS_LOOKING_MINUS_AT_MINUS_THE_MINUS_CAMERA, 'Not facing forwards or looking straight at the camera'",
                "PHOTO_MINUS_NOT_MINUS_IN_MINUS_COLOUR_MINUS_DISTORTED_MINUS_OR_MINUS_TOO_MINUS_DARK, 'The photo is not in colour, is distorted or is too dark'",
                "OTHER_MINUS_OBJECTS_MINUS_OR_MINUS_PEOPLE_MINUS_IN_MINUS_PHOTO, 'There are other people or objects in the photo'",
                "NOT_MINUS_A_MINUS_PLAIN_MINUS_FACIAL_MINUS_EXPRESSION, 'Not a plain facial expression'",
                "EYES_MINUS_NOT_MINUS_OPEN_MINUS_OR_MINUS_VISIBLE_MINUS_OR_MINUS_HAIR_MINUS_IN_MINUS_FRONT_MINUS_FACE, 'Eyes are not open, visible or there is hair in front of the face'",
                "WEARING_MINUS_SUNGLASSES_MINUS_OR_MINUS_TINTED_MINUS_GLASSES, 'Wearing sunglasses, or tinted glasses'",
                "PHOTO_MINUS_HAS_MINUS_HEAD_MINUS_COVERING_MINUS_ASIDE_MINUS_FROM_MINUS_RELIGIOUS_MINUS_OR_MINUS_MEDICAL, 'The photo has a head covering (aside from religious or medical)'",
                "PHOTO_MINUS_HAS_MINUS_RED_MINUS_EYE_MINUS_GLARE_MINUS_OR_MINUS_SHADOWS_MINUS_OVER_MINUS_FACE, 'The photo has ''red-eye'', glare or shadows over face'",
            ]
        )
        fun `should map enums to human readable messages in English`(
            rejectionReason: PhotoRejectionReasonApiEnum,
            expected: String
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
                "NOT_MINUS_FACING_MINUS_FORWARDS_MINUS_OR_MINUS_LOOKING_MINUS_AT_MINUS_THE_MINUS_CAMERA, 'Nid yw''n gwynebu ymlaen nac edrych yn syth ar y camera'",
                "PHOTO_MINUS_NOT_MINUS_IN_MINUS_COLOUR_MINUS_DISTORTED_MINUS_OR_MINUS_TOO_MINUS_DARK, 'Nid yw''r llun mewn lliw, mae''n ystumiedig neu''n rhy dywyll'",
                "OTHER_MINUS_OBJECTS_MINUS_OR_MINUS_PEOPLE_MINUS_IN_MINUS_PHOTO, 'Mae yna bobl neu wrthrychau eraill yn y llun'",
                "NOT_MINUS_A_MINUS_PLAIN_MINUS_FACIAL_MINUS_EXPRESSION, 'Nid yw''r mynegiant wyneb yn blaen'",
                "EYES_MINUS_NOT_MINUS_OPEN_MINUS_OR_MINUS_VISIBLE_MINUS_OR_MINUS_HAIR_MINUS_IN_MINUS_FRONT_MINUS_FACE, 'Nid yw''r llygaid yn agored, yn weladwy neu mae gwallt o flaen yr wyneb'",
                "WEARING_MINUS_SUNGLASSES_MINUS_OR_MINUS_TINTED_MINUS_GLASSES, 'Gwisgo sbectol haul, neu sbectol arlliw'",
                "PHOTO_MINUS_HAS_MINUS_HEAD_MINUS_COVERING_MINUS_ASIDE_MINUS_FROM_MINUS_RELIGIOUS_MINUS_OR_MINUS_MEDICAL, 'Mae gan y llun orchudd pen (ar wahân i orchudd crefyddol neu feddygol)'",
                "PHOTO_MINUS_HAS_MINUS_RED_MINUS_EYE_MINUS_GLARE_MINUS_OR_MINUS_SHADOWS_MINUS_OVER_MINUS_FACE, 'Mae gan y llun ''lygad coch'', llacharedd neu gysgodion dros yr wyneb'",
            ]
        )
        fun `should map enums to human readable messages in Welsh`(
            rejectionReason: PhotoRejectionReasonApiEnum,
            expected: String
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
                "NOT_MINUS_FACING_MINUS_FORWARDS_MINUS_OR_MINUS_LOOKING_MINUS_AT_MINUS_THE_MINUS_CAMERA, 'Not facing forwards or looking straight at the camera'",
                "PHOTO_MINUS_NOT_MINUS_IN_MINUS_COLOUR_MINUS_DISTORTED_MINUS_OR_MINUS_TOO_MINUS_DARK, 'The photo is not in colour, is distorted or is too dark'",
                "OTHER_MINUS_OBJECTS_MINUS_OR_MINUS_PEOPLE_MINUS_IN_MINUS_PHOTO, 'There are other people or objects in the photo'",
                "NOT_MINUS_A_MINUS_PLAIN_MINUS_FACIAL_MINUS_EXPRESSION, 'Not a plain facial expression'",
                "EYES_MINUS_NOT_MINUS_OPEN_MINUS_OR_MINUS_VISIBLE_MINUS_OR_MINUS_HAIR_MINUS_IN_MINUS_FRONT_MINUS_FACE, 'Eyes are not open, visible or there is hair in front of the face'",
                "WEARING_MINUS_SUNGLASSES_MINUS_OR_MINUS_TINTED_MINUS_GLASSES, 'Wearing sunglasses, or tinted glasses'",
                "PHOTO_MINUS_HAS_MINUS_HEAD_MINUS_COVERING_MINUS_ASIDE_MINUS_FROM_MINUS_RELIGIOUS_MINUS_OR_MINUS_MEDICAL, 'The photo has a head covering (aside from religious or medical)'",
                "PHOTO_MINUS_HAS_MINUS_RED_MINUS_EYE_MINUS_GLARE_MINUS_OR_MINUS_SHADOWS_MINUS_OVER_MINUS_FACE, 'The photo has ''red-eye'', glare or shadows over face'",
            ]
        )
        fun `should map enums to human readable messages in English`(
            rejectionReason: PhotoRejectionReasonMessageEnum,
            expected: String
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
                "NOT_MINUS_FACING_MINUS_FORWARDS_MINUS_OR_MINUS_LOOKING_MINUS_AT_MINUS_THE_MINUS_CAMERA, 'Nid yw''n gwynebu ymlaen nac edrych yn syth ar y camera'",
                "PHOTO_MINUS_NOT_MINUS_IN_MINUS_COLOUR_MINUS_DISTORTED_MINUS_OR_MINUS_TOO_MINUS_DARK, 'Nid yw''r llun mewn lliw, mae''n ystumiedig neu''n rhy dywyll'",
                "OTHER_MINUS_OBJECTS_MINUS_OR_MINUS_PEOPLE_MINUS_IN_MINUS_PHOTO, 'Mae yna bobl neu wrthrychau eraill yn y llun'",
                "NOT_MINUS_A_MINUS_PLAIN_MINUS_FACIAL_MINUS_EXPRESSION, 'Nid yw''r mynegiant wyneb yn blaen'",
                "EYES_MINUS_NOT_MINUS_OPEN_MINUS_OR_MINUS_VISIBLE_MINUS_OR_MINUS_HAIR_MINUS_IN_MINUS_FRONT_MINUS_FACE, 'Nid yw''r llygaid yn agored, yn weladwy neu mae gwallt o flaen yr wyneb'",
                "WEARING_MINUS_SUNGLASSES_MINUS_OR_MINUS_TINTED_MINUS_GLASSES, 'Gwisgo sbectol haul, neu sbectol arlliw'",
                "PHOTO_MINUS_HAS_MINUS_HEAD_MINUS_COVERING_MINUS_ASIDE_MINUS_FROM_MINUS_RELIGIOUS_MINUS_OR_MINUS_MEDICAL, 'Mae gan y llun orchudd pen (ar wahân i orchudd crefyddol neu feddygol)'",
                "PHOTO_MINUS_HAS_MINUS_RED_MINUS_EYE_MINUS_GLARE_MINUS_OR_MINUS_SHADOWS_MINUS_OVER_MINUS_FACE, 'Mae gan y llun ''lygad coch'', llacharedd neu gysgodion dros yr wyneb'",
            ]
        )
        fun `should map enums to human readable messages in Welsh`(
            rejectionReason: PhotoRejectionReasonMessageEnum,
            expected: String
        ) {
            // Given

            // When
            val actual = mapper.toPhotoRejectionReasonString(rejectionReason, LanguageDto.WELSH)

            // Then
            assertThat(actual).isEqualTo(expected)
        }
    }
}
