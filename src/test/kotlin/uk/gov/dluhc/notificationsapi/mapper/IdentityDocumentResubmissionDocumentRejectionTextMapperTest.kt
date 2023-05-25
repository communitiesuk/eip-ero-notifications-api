package uk.gov.dluhc.notificationsapi.mapper

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.given
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import uk.gov.dluhc.notificationsapi.dto.LanguageDto.ENGLISH
import uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models.buildIdDocumentPersonalisationMessage
import uk.gov.dluhc.notificationsapi.testsupport.testdata.models.buildIdDocumentPersonalisation
import uk.gov.dluhc.notificationsapi.testsupport.testdata.models.buildRejectedDocument
import uk.gov.dluhc.notificationsapi.messaging.models.DocumentRejectionReason as DocumentRejectionReasonMessaging
import uk.gov.dluhc.notificationsapi.messaging.models.DocumentType as DocumentTypeMessaging
import uk.gov.dluhc.notificationsapi.models.DocumentRejectionReason as DocumentRejectionReasonApi
import uk.gov.dluhc.notificationsapi.models.DocumentType as DocumentTypeApi
import uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models.buildRejectedDocument as buildRejectedDocumentMessage

@ExtendWith(MockitoExtension::class)
class IdentityDocumentResubmissionDocumentRejectionTextMapperTest {

    @InjectMocks
    private lateinit var rejectionTextMapper: IdentityDocumentResubmissionDocumentRejectionTextMapper

    @Mock
    private lateinit var rejectedDocumentReasonMapper: RejectedDocumentReasonMapper

    @Nested
    inner class ToApplicationRejectionReasonStringFromApiEnum {
        @Test
        fun `should map to document rejection text given 1 rejected document with 1 reason and no notes`() {
            // Given
            val personalisation = buildIdDocumentPersonalisation(
                rejectedDocuments = listOf(
                    buildRejectedDocument(
                        documentType = DocumentTypeApi.BIRTH_MINUS_CERTIFICATE,
                        rejectionReasons = listOf(DocumentRejectionReasonApi.UNREADABLE_MINUS_DOCUMENT),
                        rejectionNotes = null
                    )
                )
            )

            given(rejectedDocumentReasonMapper.toDocumentRejectionReasonString(any<DocumentRejectionReasonApi>(), any()))
                .willReturn("We were unable to read the document provided because it was not clear or not showing the information we needed")

            val expected = """
                BIRTH_MINUS_CERTIFICATE
                
                * We were unable to read the document provided because it was not clear or not showing the information we needed
                
                ----
                
                
            """.trimIndent()

            // When
            val actual = rejectionTextMapper.toDocumentRejectionText(ENGLISH, personalisation)

            // Then
            assertThat(actual).isEqualTo(expected)
            verify(rejectedDocumentReasonMapper).toDocumentRejectionReasonString(DocumentRejectionReasonApi.UNREADABLE_MINUS_DOCUMENT, ENGLISH)
        }

        @Test
        fun `should map to document rejection text given 1 rejected document with no reasons and some notes`() {
            // Given
            val personalisation = buildIdDocumentPersonalisation(
                rejectedDocuments = listOf(
                    buildRejectedDocument(
                        documentType = DocumentTypeApi.BIRTH_MINUS_CERTIFICATE,
                        rejectionReasons = emptyList(),
                        rejectionNotes = "This birth certificate is not yours and is someone else's name. You must provide your own documents only."
                    )
                )
            )

            val expected = """
                BIRTH_MINUS_CERTIFICATE
                
                This birth certificate is not yours and is someone else's name. You must provide your own documents only.
                
                ----
                
                
            """.trimIndent()

            // When
            val actual = rejectionTextMapper.toDocumentRejectionText(ENGLISH, personalisation)

            // Then
            assertThat(actual).isEqualTo(expected)
            verifyNoInteractions(rejectedDocumentReasonMapper)
        }

        @Test
        fun `should map to document rejection text given 1 rejected document with 2 reasons and no notes`() {
            // Given
            val personalisation = buildIdDocumentPersonalisation(
                rejectedDocuments = listOf(
                    buildRejectedDocument(
                        documentType = DocumentTypeApi.BIRTH_MINUS_CERTIFICATE,
                        rejectionReasons = listOf(
                            DocumentRejectionReasonApi.UNREADABLE_MINUS_DOCUMENT,
                            DocumentRejectionReasonApi.DUPLICATE_MINUS_DOCUMENT,
                        ),
                        rejectionNotes = null
                    )
                )
            )

            given(rejectedDocumentReasonMapper.toDocumentRejectionReasonString(any<DocumentRejectionReasonApi>(), any()))
                .willReturn(
                    "We were unable to read the document provided because it was not clear or not showing the information we needed",
                    "This was a duplicate of another document that you have provided"
                )

            val expected = """
                BIRTH_MINUS_CERTIFICATE
                
                * We were unable to read the document provided because it was not clear or not showing the information we needed
                * This was a duplicate of another document that you have provided
                
                ----
                
                
            """.trimIndent()

            // When
            val actual = rejectionTextMapper.toDocumentRejectionText(ENGLISH, personalisation)

            // Then
            assertThat(actual).isEqualTo(expected)
            verify(rejectedDocumentReasonMapper).toDocumentRejectionReasonString(DocumentRejectionReasonApi.UNREADABLE_MINUS_DOCUMENT, ENGLISH)
            verify(rejectedDocumentReasonMapper).toDocumentRejectionReasonString(DocumentRejectionReasonApi.DUPLICATE_MINUS_DOCUMENT, ENGLISH)
        }
        @Test
        fun `should map to document rejection text given 1 rejected document with 2 reasons and some notes`() {
            // Given
            val personalisation = buildIdDocumentPersonalisation(
                rejectedDocuments = listOf(
                    buildRejectedDocument(
                        documentType = DocumentTypeApi.BIRTH_MINUS_CERTIFICATE,
                        rejectionReasons = listOf(
                            DocumentRejectionReasonApi.UNREADABLE_MINUS_DOCUMENT,
                            DocumentRejectionReasonApi.DUPLICATE_MINUS_DOCUMENT,
                        ),
                        rejectionNotes = "You have already provided your birth certificate as part of this application. Please provide a different form of ID"
                    )
                )
            )

            given(rejectedDocumentReasonMapper.toDocumentRejectionReasonString(any<DocumentRejectionReasonApi>(), any()))
                .willReturn(
                    "We were unable to read the document provided because it was not clear or not showing the information we needed",
                    "This was a duplicate of another document that you have provided"
                )

            val expected = """
                BIRTH_MINUS_CERTIFICATE
                
                * We were unable to read the document provided because it was not clear or not showing the information we needed
                * This was a duplicate of another document that you have provided
                
                You have already provided your birth certificate as part of this application. Please provide a different form of ID
                
                ----
                
                
            """.trimIndent()

            // When
            val actual = rejectionTextMapper.toDocumentRejectionText(ENGLISH, personalisation)

            // Then
            assertThat(actual).isEqualTo(expected)
            verify(rejectedDocumentReasonMapper).toDocumentRejectionReasonString(DocumentRejectionReasonApi.UNREADABLE_MINUS_DOCUMENT, ENGLISH)
            verify(rejectedDocumentReasonMapper).toDocumentRejectionReasonString(DocumentRejectionReasonApi.DUPLICATE_MINUS_DOCUMENT, ENGLISH)
        }

        @Test
        fun `should map to document rejection text given 1 rejected document with no reasons and no notes`() {
            // Given
            val personalisation = buildIdDocumentPersonalisation(
                rejectedDocuments = listOf(
                    buildRejectedDocument(
                        documentType = DocumentTypeApi.ADOPTION_MINUS_CERTIFICATE,
                        rejectionReasons = emptyList(),
                        rejectionNotes = ""
                    )
                )
            )

            val expected = """
                ADOPTION_MINUS_CERTIFICATE

                ----
                
                
            """.trimIndent()

            // When
            val actual = rejectionTextMapper.toDocumentRejectionText(ENGLISH, personalisation)

            // Then
            assertThat(actual).isEqualTo(expected)
            verifyNoInteractions(rejectedDocumentReasonMapper)
        }

        @Test
        fun `should map to document rejection text given several rejected documents`() {
            // Given
            val personalisation = buildIdDocumentPersonalisation(
                rejectedDocuments = listOf(
                    buildRejectedDocument(
                        documentType = DocumentTypeApi.BIRTH_MINUS_CERTIFICATE,
                        rejectionReasons = listOf(
                            DocumentRejectionReasonApi.UNREADABLE_MINUS_DOCUMENT,
                            DocumentRejectionReasonApi.DUPLICATE_MINUS_DOCUMENT,
                        ),
                        rejectionNotes = null
                    ),
                    buildRejectedDocument(
                        documentType = DocumentTypeApi.FIREARMS_MINUS_CERTIFICATE,
                        rejectionReasons = emptyList(),
                        rejectionNotes = "Your firearms certificate is from your Scouts groups for your air rifle. It is not a formal certificate and is not an acceptable form of ID"
                    ),
                    buildRejectedDocument(
                        documentType = DocumentTypeApi.UTILITY_MINUS_BILL,
                        rejectionReasons = listOf(DocumentRejectionReasonApi.APPLICANT_MINUS_DETAILS_MINUS_NOT_MINUS_CLEAR),
                        rejectionNotes = "Your name and address are not clear on the Gas Bill that you provided. Please scan and upload it again ensuring your name and address is clearly visible."
                    ),
                    buildRejectedDocument(
                        documentType = DocumentTypeApi.ADOPTION_MINUS_CERTIFICATE,
                        rejectionReasons = emptyList(),
                        rejectionNotes = null
                    )
                )
            )

            given(rejectedDocumentReasonMapper.toDocumentRejectionReasonString(any<DocumentRejectionReasonApi>(), any()))
                .willReturn(
                    "We were unable to read the document provided because it was not clear or not showing the information we needed",
                    "This was a duplicate of another document that you have provided"
                )

            val expected = """
                BIRTH_MINUS_CERTIFICATE
                
                * We were unable to read the document provided because it was not clear or not showing the information we needed
                * This was a duplicate of another document that you have provided
                
                ----
                
                FIREARMS_MINUS_CERTIFICATE
                
                Your firearms certificate is from your Scouts groups for your air rifle. It is not a formal certificate and is not an acceptable form of ID
                
                ----
                
                UTILITY_MINUS_BILL
                
                * This was a duplicate of another document that you have provided
                
                Your name and address are not clear on the Gas Bill that you provided. Please scan and upload it again ensuring your name and address is clearly visible.
                
                ----
                
                ADOPTION_MINUS_CERTIFICATE

                ----
                
                
            """.trimIndent()

            // When
            val actual = rejectionTextMapper.toDocumentRejectionText(ENGLISH, personalisation)

            // Then
            assertThat(actual).isEqualTo(expected)
        }
    }

    @Nested
    inner class ToApplicationRejectionReasonStringFromMessagingEnum {
        @Test
        fun `should map to document rejection text given 1 rejected document with 1 reason and no notes`() {
            // Given
            val personalisation = buildIdDocumentPersonalisationMessage(
                rejectedDocuments = listOf(
                    buildRejectedDocumentMessage(
                        documentType = DocumentTypeMessaging.BIRTH_MINUS_CERTIFICATE,
                        rejectionReasons = listOf(DocumentRejectionReasonMessaging.UNREADABLE_MINUS_DOCUMENT),
                        rejectionNotes = null
                    )
                )
            )

            given(rejectedDocumentReasonMapper.toDocumentRejectionReasonString(any<DocumentRejectionReasonMessaging>(), any()))
                .willReturn("We were unable to read the document provided because it was not clear or not showing the information we needed")

            val expected = """
                BIRTH_MINUS_CERTIFICATE
                
                * We were unable to read the document provided because it was not clear or not showing the information we needed
                
                ----
                
                
            """.trimIndent()

            // When
            val actual = rejectionTextMapper.toDocumentRejectionText(ENGLISH, personalisation)

            // Then
            assertThat(actual).isEqualTo(expected)
            verify(rejectedDocumentReasonMapper).toDocumentRejectionReasonString(DocumentRejectionReasonMessaging.UNREADABLE_MINUS_DOCUMENT, ENGLISH)
        }

        @Test
        fun `should map to document rejection text given 1 rejected document with no reasons and some notes`() {
            // Given
            val personalisation = buildIdDocumentPersonalisationMessage(
                rejectedDocuments = listOf(
                    buildRejectedDocumentMessage(
                        documentType = DocumentTypeMessaging.BIRTH_MINUS_CERTIFICATE,
                        rejectionReasons = emptyList(),
                        rejectionNotes = "This birth certificate is not yours and is someone else's name. You must provide your own documents only."
                    )
                )
            )

            val expected = """
                BIRTH_MINUS_CERTIFICATE
                
                This birth certificate is not yours and is someone else's name. You must provide your own documents only.
                
                ----
                
                
            """.trimIndent()

            // When
            val actual = rejectionTextMapper.toDocumentRejectionText(ENGLISH, personalisation)

            // Then
            assertThat(actual).isEqualTo(expected)
            verifyNoInteractions(rejectedDocumentReasonMapper)
        }

        @Test
        fun `should map to document rejection text given 1 rejected document with 2 reasons and no notes`() {
            // Given
            val personalisation = buildIdDocumentPersonalisationMessage(
                rejectedDocuments = listOf(
                    buildRejectedDocumentMessage(
                        documentType = DocumentTypeMessaging.BIRTH_MINUS_CERTIFICATE,
                        rejectionReasons = listOf(
                            DocumentRejectionReasonMessaging.UNREADABLE_MINUS_DOCUMENT,
                            DocumentRejectionReasonMessaging.DUPLICATE_MINUS_DOCUMENT,
                        ),
                        rejectionNotes = null
                    )
                )
            )

            given(rejectedDocumentReasonMapper.toDocumentRejectionReasonString(any<DocumentRejectionReasonMessaging>(), any()))
                .willReturn(
                    "We were unable to read the document provided because it was not clear or not showing the information we needed",
                    "This was a duplicate of another document that you have provided"
                )

            val expected = """
                BIRTH_MINUS_CERTIFICATE
                
                * We were unable to read the document provided because it was not clear or not showing the information we needed
                * This was a duplicate of another document that you have provided
                
                ----
                
                
            """.trimIndent()

            // When
            val actual = rejectionTextMapper.toDocumentRejectionText(ENGLISH, personalisation)

            // Then
            assertThat(actual).isEqualTo(expected)
            verify(rejectedDocumentReasonMapper).toDocumentRejectionReasonString(DocumentRejectionReasonMessaging.UNREADABLE_MINUS_DOCUMENT, ENGLISH)
            verify(rejectedDocumentReasonMapper).toDocumentRejectionReasonString(DocumentRejectionReasonMessaging.DUPLICATE_MINUS_DOCUMENT, ENGLISH)
        }
        @Test
        fun `should map to document rejection text given 1 rejected document with 2 reasons and some notes`() {
            // Given
            val personalisation = buildIdDocumentPersonalisationMessage(
                rejectedDocuments = listOf(
                    buildRejectedDocumentMessage(
                        documentType = DocumentTypeMessaging.BIRTH_MINUS_CERTIFICATE,
                        rejectionReasons = listOf(
                            DocumentRejectionReasonMessaging.UNREADABLE_MINUS_DOCUMENT,
                            DocumentRejectionReasonMessaging.DUPLICATE_MINUS_DOCUMENT,
                        ),
                        rejectionNotes = "You have already provided your birth certificate as part of this application. Please provide a different form of ID"
                    )
                )
            )

            given(rejectedDocumentReasonMapper.toDocumentRejectionReasonString(any<DocumentRejectionReasonMessaging>(), any()))
                .willReturn(
                    "We were unable to read the document provided because it was not clear or not showing the information we needed",
                    "This was a duplicate of another document that you have provided"
                )

            val expected = """
                BIRTH_MINUS_CERTIFICATE
                
                * We were unable to read the document provided because it was not clear or not showing the information we needed
                * This was a duplicate of another document that you have provided
                
                You have already provided your birth certificate as part of this application. Please provide a different form of ID
                
                ----
                
                
            """.trimIndent()

            // When
            val actual = rejectionTextMapper.toDocumentRejectionText(ENGLISH, personalisation)

            // Then
            assertThat(actual).isEqualTo(expected)
            verify(rejectedDocumentReasonMapper).toDocumentRejectionReasonString(DocumentRejectionReasonMessaging.UNREADABLE_MINUS_DOCUMENT, ENGLISH)
            verify(rejectedDocumentReasonMapper).toDocumentRejectionReasonString(DocumentRejectionReasonMessaging.DUPLICATE_MINUS_DOCUMENT, ENGLISH)
        }

        @Test
        fun `should map to document rejection text given 1 rejected document with no reasons and no notes`() {
            // Given
            val personalisation = buildIdDocumentPersonalisationMessage(
                rejectedDocuments = listOf(
                    buildRejectedDocumentMessage(
                        documentType = DocumentTypeMessaging.ADOPTION_MINUS_CERTIFICATE,
                        rejectionReasons = emptyList(),
                        rejectionNotes = ""
                    )
                )
            )

            val expected = """
                ADOPTION_MINUS_CERTIFICATE

                ----
                
                
            """.trimIndent()

            // When
            val actual = rejectionTextMapper.toDocumentRejectionText(ENGLISH, personalisation)

            // Then
            assertThat(actual).isEqualTo(expected)
            verifyNoInteractions(rejectedDocumentReasonMapper)
        }

        @Test
        fun `should map to document rejection text given several rejected documents`() {
            // Given
            val personalisation = buildIdDocumentPersonalisationMessage(
                rejectedDocuments = listOf(
                    buildRejectedDocumentMessage(
                        documentType = DocumentTypeMessaging.BIRTH_MINUS_CERTIFICATE,
                        rejectionReasons = listOf(
                            DocumentRejectionReasonMessaging.UNREADABLE_MINUS_DOCUMENT,
                            DocumentRejectionReasonMessaging.DUPLICATE_MINUS_DOCUMENT,
                        ),
                        rejectionNotes = null
                    ),
                    buildRejectedDocumentMessage(
                        documentType = DocumentTypeMessaging.FIREARMS_MINUS_CERTIFICATE,
                        rejectionReasons = emptyList(),
                        rejectionNotes = "Your firearms certificate is from your Scouts groups for your air rifle. It is not a formal certificate and is not an acceptable form of ID"
                    ),
                    buildRejectedDocumentMessage(
                        documentType = DocumentTypeMessaging.UTILITY_MINUS_BILL,
                        rejectionReasons = listOf(DocumentRejectionReasonMessaging.APPLICANT_MINUS_DETAILS_MINUS_NOT_MINUS_CLEAR),
                        rejectionNotes = "Your name and address are not clear on the Gas Bill that you provided. Please scan and upload it again ensuring your name and address is clearly visible."
                    ),
                    buildRejectedDocumentMessage(
                        documentType = DocumentTypeMessaging.ADOPTION_MINUS_CERTIFICATE,
                        rejectionReasons = emptyList(),
                        rejectionNotes = null
                    )
                )
            )

            given(rejectedDocumentReasonMapper.toDocumentRejectionReasonString(any<DocumentRejectionReasonMessaging>(), any()))
                .willReturn(
                    "We were unable to read the document provided because it was not clear or not showing the information we needed",
                    "This was a duplicate of another document that you have provided"
                )

            val expected = """
                BIRTH_MINUS_CERTIFICATE
                
                * We were unable to read the document provided because it was not clear or not showing the information we needed
                * This was a duplicate of another document that you have provided
                
                ----
                
                FIREARMS_MINUS_CERTIFICATE
                
                Your firearms certificate is from your Scouts groups for your air rifle. It is not a formal certificate and is not an acceptable form of ID
                
                ----
                
                UTILITY_MINUS_BILL
                
                * This was a duplicate of another document that you have provided
                
                Your name and address are not clear on the Gas Bill that you provided. Please scan and upload it again ensuring your name and address is clearly visible.
                
                ----
                
                ADOPTION_MINUS_CERTIFICATE

                ----
                
                
            """.trimIndent()

            // When
            val actual = rejectionTextMapper.toDocumentRejectionText(ENGLISH, personalisation)

            // Then
            assertThat(actual).isEqualTo(expected)
        }
    }
}
