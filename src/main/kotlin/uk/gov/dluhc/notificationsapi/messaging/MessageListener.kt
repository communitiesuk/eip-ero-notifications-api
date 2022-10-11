package uk.gov.dluhc.notificationsapi.messaging

interface MessageListener<PAYLOAD> {
    fun handleMessage(payload: PAYLOAD)
}
