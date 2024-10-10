package uk.gov.dluhc.notificationsapi.exception

class InvalidSourceTypeException(
    sourceType: String,
) : RuntimeException(
    "Source type :[$sourceType] is not a valid source type",
)
