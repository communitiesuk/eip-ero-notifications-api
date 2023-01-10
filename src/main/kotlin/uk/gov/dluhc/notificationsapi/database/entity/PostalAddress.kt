package uk.gov.dluhc.notificationsapi.database.entity

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean

@DynamoDbBean
data class PostalAddress(
    var addressee: String? = null,
    var `property`: String? = null,
    var street: String? = null,
    var town: String? = null,
    var area: String? = null,
    var locality: String? = null,
    var postcode: String? = null,
)
