package uk.gov.dluhc.notificationsapi.testsupport

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder
import com.github.tomakehurst.wiremock.client.WireMock.post
import com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo
import com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching
import com.github.tomakehurst.wiremock.http.RequestMethod.POST
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder.newRequestPattern
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import uk.gov.dluhc.notificationsapi.testsupport.model.NotifySendEmailSuccessResponse

/**
 * Service class to provide support to tests with setting up and managing wiremock stubs
 */
@Service
class WiremockService (private val wireMockServer: WireMockServer) {

    @Autowired
    private lateinit var objectMapper: ObjectMapper
    private var baseUrl: String? = null

    private companion object {
        private const val NOTIFY_SEND_EMAIL_URL = "/v2/notifications/email"
    }

    fun resetAllStubsAndMappings() {
        wireMockServer.resetAll()
    }

    fun wiremockBaseUrl(): String {
        if (baseUrl == null) {
            baseUrl = "http://localhost:${wireMockServer.port()}"
        }
        return baseUrl!!
    }

    fun stubNotifySendEmailResponse(response: NotifySendEmailSuccessResponse) {
        wireMockServer.stubFor(
            post(urlPathMatching(NOTIFY_SEND_EMAIL_URL))
                .willReturn(
                    ResponseDefinitionBuilder.responseDefinition()
                        .withStatus(201)
                        .withBody(objectMapper.writeValueAsString(response))
                )
        )
    }

    fun verifyNotifySendEmailCalled() {
        wireMockServer.verify(newRequestPattern(POST, urlPathEqualTo(NOTIFY_SEND_EMAIL_URL)))
    }
}