package uk.gov.dluhc.notificationsapi.service

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowableOfType
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.given
import org.mockito.kotlin.verify
import uk.gov.dluhc.notificationsapi.client.ElectoralRegistrationOfficeGeneralException
import uk.gov.dluhc.notificationsapi.client.ElectoralRegistrationOfficeManagementApiClient
import uk.gov.dluhc.notificationsapi.client.ElectoralRegistrationOfficeNotFoundException
import uk.gov.dluhc.notificationsapi.exception.GssCodeMismatchException
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aGssCode
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aValidRandomEroId
import uk.gov.dluhc.notificationsapi.testsupport.testdata.anotherGssCode

@ExtendWith(MockitoExtension::class)
internal class EroServiceTest {

    @Mock
    private lateinit var electoralRegistrationOfficeManagementApiClient: ElectoralRegistrationOfficeManagementApiClient

    @InjectMocks
    private lateinit var eroService: EroService

    @Nested
    inner class LookupGssCodesForEro {
        @Test
        fun `should lookup and return gssCodes`() {
            // Given
            val eroId = aValidRandomEroId()

            val expectedGssCodes = listOf("E123456789", "E987654321")
            given(electoralRegistrationOfficeManagementApiClient.getElectoralRegistrationOfficeGssCodes(any()))
                .willReturn(expectedGssCodes)

            // When
            val gssCodes = eroService.lookupGssCodesForEro(eroId)

            // Then
            assertThat(gssCodes).usingRecursiveComparison().ignoringCollectionOrder().isEqualTo(expectedGssCodes)
            verify(electoralRegistrationOfficeManagementApiClient).getElectoralRegistrationOfficeGssCodes(eroId)
        }

        @Test
        fun `should not return gssCodes given API client throws ERO not found exception`() {
            // Given
            val eroId = aValidRandomEroId()

            val expected = ElectoralRegistrationOfficeNotFoundException(mapOf("eroId" to eroId))
            given(electoralRegistrationOfficeManagementApiClient.getElectoralRegistrationOfficeGssCodes(any())).willThrow(expected)

            // When
            val ex = catchThrowableOfType(
                { eroService.lookupGssCodesForEro(eroId) },
                ElectoralRegistrationOfficeNotFoundException::class.java,
            )

            // Then
            assertThat(ex).isEqualTo(expected)
        }

        @Test
        fun `should not return gssCodes given API client throws general exception`() {
            // Given
            val eroId = aValidRandomEroId()

            val expected = ElectoralRegistrationOfficeGeneralException("error", mapOf("eroId" to eroId))
            given(electoralRegistrationOfficeManagementApiClient.getElectoralRegistrationOfficeGssCodes(any())).willThrow(expected)

            // When
            val ex = catchThrowableOfType(
                { eroService.lookupGssCodesForEro(eroId) },
                ElectoralRegistrationOfficeGeneralException::class.java,
            )

            // Then
            assertThat(ex).isEqualTo(expected)
        }
    }

    @Nested
    inner class ValidateGssCodeAssociatedWithEro {
        @Test
        fun `should validate a gssCode associated with an eroId`() {
            // Given
            val eroId = aValidRandomEroId()
            val gssCode = aGssCode()

            val erosGssCodes = listOf(gssCode, anotherGssCode())
            given(electoralRegistrationOfficeManagementApiClient.getElectoralRegistrationOfficeGssCodes(any()))
                .willReturn(erosGssCodes)

            // When
            eroService.validateGssCodeAssociatedWithEro(eroId, gssCode)

            // Then
            verify(electoralRegistrationOfficeManagementApiClient).getElectoralRegistrationOfficeGssCodes(eroId)
        }

        @Test
        fun `should throw an exception when validating a gssCode not associated with an eroId`() {
            // Given
            val eroId = aValidRandomEroId()
            val gssCode = aGssCode()

            val erosGssCodes = listOf(anotherGssCode())
            given(electoralRegistrationOfficeManagementApiClient.getElectoralRegistrationOfficeGssCodes(any()))
                .willReturn(erosGssCodes)

            // When
            val ex = catchThrowableOfType(
                { eroService.validateGssCodeAssociatedWithEro(eroId, gssCode) },
                GssCodeMismatchException::class.java,
            )

            // Then
            assertThat(ex).hasMessage("Request gssCode:[$gssCode] does not belong to eroId:[$eroId]")
            verify(electoralRegistrationOfficeManagementApiClient).getElectoralRegistrationOfficeGssCodes(eroId)
        }
    }
}
