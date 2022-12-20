package uk.gov.dluhc.notificationsapi.mapper

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildPhotoResubmissionPersonalisationDto

class PhotoResubmissionPersonalisationMapperTest {

    private val mapper = PhotoResubmissionPersonalisationMapper()

    @Test
    fun `should map dto to personalisation map`() {
        // Given
        val personalisationDto = buildPhotoResubmissionPersonalisationDto()
        val expected = mutableMapOf<String, String>()
        with(personalisationDto) {
            expected["applicationReference"] = applicationReference
            expected["firstName"] = firstName
            expected["photoRequestFreeText"] = photoRequestFreeText
            expected["uploadPhotoLink"] = uploadPhotoLink
            with(eroContactDetails) {
                expected["LAName"] = localAuthorityName
                expected["eroPhone"] = phone
                expected["eroWebsite"] = website
                expected["eroEmail"] = email
                with(address) {
                    expected["eroAddressLine1"] = `property`
                    expected["eroAddressLine2"] = street
                    expected["eroAddressLine3"] = town
                    expected["eroAddressLine4"] = area
                    expected["eroAddressLine5"] = locality
                    expected["eroPostcode"] = postcode
                }
            }
        }

        // When
        val actual = mapper.toTemplatePersonalisationMap(personalisationDto)

        // Then
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected)
    }
}
