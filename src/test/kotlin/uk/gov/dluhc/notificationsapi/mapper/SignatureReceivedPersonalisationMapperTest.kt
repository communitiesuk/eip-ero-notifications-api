package uk.gov.dluhc.notificationsapi.mapper

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.given
import uk.gov.dluhc.notificationsapi.dto.LanguageDto.ENGLISH
import uk.gov.dluhc.notificationsapi.messaging.models.SourceType
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildContactDetailsDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models.buildBasePersonalisation
import uk.gov.dluhc.notificationsapi.messaging.models.Language as LanguageMessage

@ExtendWith(MockitoExtension::class)
internal class SignatureReceivedPersonalisationMapperTest {

    @InjectMocks
    private lateinit var personalisationMapper: SignatureReceivedPersonalisationMapper

    @Mock
    private lateinit var languageMapper: LanguageMapper

    @Mock
    private lateinit var sourceTypeMapper: SourceTypeMapper

    @Mock
    private lateinit var eroContactDetailsMapper: EroContactDetailsMapper

    @Test
    fun `should map to personalisation map`() {
        // Given
        val personalisationMessage = buildBasePersonalisation()

        val dtoContactDetails = buildContactDetailsDto()

        given(languageMapper.fromMessageToDto(LanguageMessage.EN)).willReturn(ENGLISH)
        given(sourceTypeMapper.toFullSourceTypeString(SourceType.POSTAL, ENGLISH)).willReturn("Full String")
        given(eroContactDetailsMapper.fromSqsToDto(personalisationMessage.eroContactDetails)).willReturn(dtoContactDetails)

        val expectedPersonalisationMap = mapOf(
            "applicationReference" to personalisationMessage.applicationReference,
            "firstName" to personalisationMessage.firstName,
            "LAName" to dtoContactDetails.localAuthorityName,
            "eroPhone" to dtoContactDetails.phone,
            "eroWebsite" to dtoContactDetails.website,
            "eroEmail" to dtoContactDetails.email,
            "eroAddressLine1" to dtoContactDetails.address.property,
            "eroAddressLine2" to dtoContactDetails.address.street,
            "eroAddressLine3" to dtoContactDetails.address.town,
            "eroAddressLine4" to dtoContactDetails.address.area,
            "eroAddressLine5" to dtoContactDetails.address.locality,
            "eroPostcode" to dtoContactDetails.address.postcode,
            "fullSourceType" to "Full String",
        )

        // When
        val actual = personalisationMapper.fromMessagePersonalisationToBasePersonalisationMap(personalisationMessage, SourceType.POSTAL, LanguageMessage.EN)

        // Then
        assertEquals(expectedPersonalisationMap, actual)
    }
}
