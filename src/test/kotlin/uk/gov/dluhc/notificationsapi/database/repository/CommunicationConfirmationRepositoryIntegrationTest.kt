package uk.gov.dluhc.notificationsapi.database.repository

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.gov.dluhc.notificationsapi.config.IntegrationTest
import uk.gov.dluhc.notificationsapi.database.entity.CommunicationConfirmation
import uk.gov.dluhc.notificationsapi.database.entity.CommunicationConfirmationChannel
import uk.gov.dluhc.notificationsapi.database.entity.CommunicationConfirmationReason
import uk.gov.dluhc.notificationsapi.database.entity.SourceType
import uk.gov.dluhc.notificationsapi.testsupport.assertj.assertions.entity.CommunicationConfirmationAssert
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aGssCode
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aLocalDateTime
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aRandomSourceReference
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aSourceReference
import uk.gov.dluhc.notificationsapi.testsupport.testdata.anEmailAddress
import uk.gov.dluhc.notificationsapi.testsupport.testdata.database.entity.aCommunicationConfirmationBuilder
import uk.gov.dluhc.notificationsapi.testsupport.testdata.database.entity.anEntitySourceType
import java.util.UUID

internal class CommunicationConfirmationRepositoryIntegrationTest : IntegrationTest() {

    @Nested
    inner class GetCommunicationConfirmationBySourceReference {

        @Test
        fun `should get communication confirmation by source reference`() {
            // Given
            val id: UUID = UUID.randomUUID()
            val sourceReference: String = aSourceReference()
            val sourceType: SourceType = SourceType.ANONYMOUS_ELECTOR_DOCUMENT
            val gssCode: String = aGssCode()
            val reason: CommunicationConfirmationReason = CommunicationConfirmationReason.APPLICATION_REJECTED
            val channel: CommunicationConfirmationChannel = CommunicationConfirmationChannel.LETTER
            val requestor: String = anEmailAddress()
            val sentAt = aLocalDateTime()

            val entity = CommunicationConfirmation(
                id = id,
                sourceReference = sourceReference,
                sourceType = sourceType,
                gssCode = gssCode,
                reason = reason,
                channel = channel,
                requestor = requestor,
                sentAt = sentAt,
            )

            communicationConfirmationRepository.saveCommunicationConfirmation(entity)

            // When
            val fetchedCommunicationConfirmationList =
                communicationConfirmationRepository.getBySourceReferenceAndTypeAndGssCodes(
                    sourceReference,
                    sourceType,
                    listOf(gssCode)
                )

            // Then
            assertThat(fetchedCommunicationConfirmationList).hasSize(1)
            val fetchedCommunicationConfirmation = fetchedCommunicationConfirmationList[0]
            CommunicationConfirmationAssert.assertThat(fetchedCommunicationConfirmation)
                .hasId(id)
                .hasGssCode(gssCode)
                .hasSourceReference(sourceReference)
                .hasSourceType(sourceType)
                .hasReason(reason)
                .hasChannel(channel)
                .hasRequestor(requestor)
                .hasSentAt(sentAt)
        }

        @Test
        fun `should find no communication confirmation for source reference that does not exist`() {
            // Given
            val gssCode = aGssCode()
            val sourceReference = aRandomSourceReference()
            val sourceType = anEntitySourceType()
            val entity = aCommunicationConfirmationBuilder(
                sourceReference = sourceReference,
                sourceType = sourceType,
                gssCode = gssCode,
            )
            communicationConfirmationRepository.saveCommunicationConfirmation(entity)

            val otherSourceReference = aRandomSourceReference()

            // When
            val fetchedCommunicationConfirmationList =
                communicationConfirmationRepository.getBySourceReferenceAndTypeAndGssCodes(
                    otherSourceReference,
                    sourceType,
                    listOf(gssCode)
                )

            // Then
            assertThat(fetchedCommunicationConfirmationList).isEmpty()
        }
    }
}
