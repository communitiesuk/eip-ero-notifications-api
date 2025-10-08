package uk.gov.dluhc.notificationsapi.mapper

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.mockito.InjectMocks
import org.mockito.junit.jupiter.MockitoExtension
import uk.gov.dluhc.notificationsapi.dto.NotificationType
import uk.gov.dluhc.notificationsapi.models.SignatureRejectionReason
import uk.gov.dluhc.notificationsapi.models.SourceType
import uk.gov.dluhc.notificationsapi.testsupport.testdata.models.buildGenerateSignatureResubmissionTemplatePreviewRequest
import uk.gov.dluhc.notificationsapi.testsupport.testdata.models.buildSignatureResubmissionPersonalisation

@ExtendWith(MockitoExtension::class)
internal class SignatureResubmissionTemplatePreviewDtoMapperTest {

    @InjectMocks
    private lateinit var mapper: SignatureResubmissionTemplatePreviewDtoMapper

    @ParameterizedTest
    @CsvSource(
        value = ["true,SIGNATURE_RESUBMISSION_WITH_REASONS", "false,SIGNATURE_RESUBMISSION"],
    )
    fun `should map to correct notification type given rejection reasons`(
        shouldHaveRejectionReasons: Boolean,
        expectedNotificationType: NotificationType,
    ) {
        // Given
        val request = buildGenerateSignatureResubmissionTemplatePreviewRequest(
            personalisation = buildSignatureResubmissionPersonalisation(
                rejectionReasons = if (shouldHaveRejectionReasons) listOf(SignatureRejectionReason.WRONG_MINUS_SIZE) else emptyList(),
                rejectionNotes = if (shouldHaveRejectionReasons) "Rejection notes" else null,
            ),
            sourceType = SourceType.POSTAL,
        )

        // Then
        assertEquals(expectedNotificationType, mapper.signatureResubmissionNotificationType(request))
    }
}
