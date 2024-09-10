package uk.gov.dluhc.notificationsapi.database.repository

import software.amazon.awssdk.enhanced.dynamodb.Expression
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import uk.gov.dluhc.notificationsapi.database.entity.SourceType

fun queryRequest(sourceReference: String, sourceType: SourceType, gssCodes: List<String>): QueryEnhancedRequest.Builder =
    QueryEnhancedRequest.builder()
        .queryConditional(QueryConditional.keyEqualTo(key(sourceReference)))
        .filterExpression(
            sourceTypeFilterExpression(sourceType)
                .and(gssCodesFilterExpression(gssCodes)),
        )

fun queryRequestWithoutGssCodes(sourceReference: String, sourceType: SourceType): QueryEnhancedRequest.Builder =
    QueryEnhancedRequest.builder()
        .queryConditional(QueryConditional.keyEqualTo(key(sourceReference)))
        .filterExpression(sourceTypeFilterExpression(sourceType))

fun queryRequestWithNotificationId(notificationId: String, sourceReference: String, sourceType: SourceType, gssCodes: List<String>): QueryEnhancedRequest.Builder =
    queryRequest(sourceReference, sourceType, gssCodes)
        .filterExpression(notificationIdFilterExpression(notificationId))

fun sourceTypeFilterExpression(sourceType: SourceType): Expression =
    Expression.builder()
        .expression("#sourceType = :sourceType")
        .putExpressionName("#sourceType", "sourceType")
        .putExpressionValue(":sourceType", AttributeValue.fromS(sourceType.name))
        .build()

fun gssCodesFilterExpression(gssCodes: List<String>): Expression =
    Expression.builder()
        .expression("#gssCode IN (${List(gssCodes.size) { index -> ":gssCode_$index" }.joinToString(",")})")
        .putExpressionName("#gssCode", "gssCode")
        .also { filterExpression ->
            gssCodes.onEachIndexed { index, gssCode ->
                filterExpression.putExpressionValue(":gssCode_$index", AttributeValue.fromS(gssCode))
            }
        }.build()

fun notificationIdFilterExpression(notificationId: String): Expression =
    Expression.builder()
        .expression("#id = :notificationId")
        .putExpressionName("#id", "id")
        .putExpressionValue(":notificationId", AttributeValue.fromS(notificationId))
        .build()

fun key(partitionValue: String): Key =
    Key.builder().partitionValue(partitionValue).build()
