package uk.gov.dluhc.notificationsapi.rest

import org.springframework.beans.TypeMismatchException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import uk.gov.dluhc.notificationsapi.client.GovNotifyApiBadRequestException
import uk.gov.dluhc.notificationsapi.client.GovNotifyApiNotFoundException
import uk.gov.dluhc.notificationsapi.config.ApiRequestErrorAttributes
import uk.gov.dluhc.notificationsapi.exception.CountryNotFoundException
import uk.gov.dluhc.notificationsapi.exception.GssCodeMismatchException
import uk.gov.dluhc.notificationsapi.exception.NotificationTemplateNotFoundException
import uk.gov.dluhc.notificationsapi.models.ErrorResponse
import javax.servlet.RequestDispatcher.ERROR_MESSAGE
import javax.servlet.RequestDispatcher.ERROR_STATUS_CODE

/**
 * Global Exception Handler. Handles specific exceptions thrown by the application by returning a suitable [ErrorResponse]
 * response entity.
 *
 * Our standard pattern here is to return an [ErrorResponse]. Please think carefully about writing a response handler
 * method that does not follow this pattern. Please try not to use [handleExceptionInternal] as this returns a response
 * body of a simple string rather than a structured response body.
 *
 * Our preferred approach is to use the method [populateErrorResponseAndHandleExceptionInternal] which builds and returns
 * the [ErrorResponse] complete with correctly populated status code field. This method also populates the message field
 * of [ErrorResponse] from the exception. In the case that the exception message is not suitable for exposing through
 * the REST API, this can be overridden by manually setting the message on the request attribute. eg:
 *
 * ```
 *     request.setAttribute(ERROR_MESSAGE, "A simpler error message that does not expose internal detail", SCOPE_REQUEST)
 * ```
 *
 */
@ControllerAdvice
class GlobalExceptionHandler(
    private var errorAttributes: ApiRequestErrorAttributes
) : ResponseEntityExceptionHandler() {

    /**
     * Exception handler to return a 400 Bad Request ErrorResponse, specifically for a GovNotifyApiNotFoundException.
     *
     * This is because the message property of GovNotifyApiNotFoundException contains too much detail of the nature of
     * the error, and we would be exposing details of the internals of the system. Therefore we manually set the error
     * message request attribute to something more suitable before using it to create the ErrorResponse which is
     * rendered through the REST API. The actual exception detail is logged at the service/client layer.
     */
    @ExceptionHandler(
        value = [
            GovNotifyApiNotFoundException::class,
        ]
    )
    protected fun handleGovNotifyApiNotFoundException(
        e: GovNotifyApiNotFoundException,
        request: WebRequest,
    ): ResponseEntity<Any> {
        request.setAttribute(
            ERROR_MESSAGE,
            "Notification template not found for the given template type",
            SCOPE_REQUEST
        )
        return populateErrorResponseAndHandleExceptionInternal(e, NOT_FOUND, request)
    }

    /**
     * Exception handler to return a 400 Bad Request ErrorResponse
     */
    @ExceptionHandler(
        value = [
            GovNotifyApiBadRequestException::class,
            GssCodeMismatchException::class,
            NotificationTemplateNotFoundException::class,
            CountryNotFoundException::class
        ]
    )
    protected fun handleExceptionReturnBadRequestErrorResponse(
        e: Exception,
        request: WebRequest
    ): ResponseEntity<Any> {
        return populateErrorResponseAndHandleExceptionInternal(e, BAD_REQUEST, request)
    }

    /**
     * Overrides the HttpMessageNotReadableException exception handler to return a 400 Bad Request ErrorResponse
     */
    override fun handleHttpMessageNotReadable(
        e: HttpMessageNotReadableException,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest,
    ): ResponseEntity<Any> {
        return populateErrorResponseAndHandleExceptionInternal(e, BAD_REQUEST, request)
    }

    /**
     * Overrides the MethodArgumentNotValidException exception handler to return a 400 Bad Request ErrorResponse
     */
    override fun handleMethodArgumentNotValid(
        e: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest,
    ): ResponseEntity<Any> {
        return populateErrorResponseAndHandleExceptionInternal(e, BAD_REQUEST, request)
    }

    /**
     * Overrides the TypeMismatchException exception handler to return a 400 Bad Request ErrorResponse
     */
    override fun handleTypeMismatch(
        ex: TypeMismatchException,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> {
        request.setAttribute(ERROR_MESSAGE, ex.cause?.message ?: "", SCOPE_REQUEST)
        return populateErrorResponseAndHandleExceptionInternal(ex, BAD_REQUEST, request)
    }

    private fun populateErrorResponseAndHandleExceptionInternal(
        exception: Exception,
        status: HttpStatus,
        request: WebRequest,
    ): ResponseEntity<Any> {
        request.setAttribute(ERROR_STATUS_CODE, status.value(), SCOPE_REQUEST)
        val body = errorAttributes.getErrorResponse(request)
        return handleExceptionInternal(exception, body, HttpHeaders(), status, request)
    }
}
