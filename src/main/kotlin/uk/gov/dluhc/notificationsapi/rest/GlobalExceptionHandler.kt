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
import uk.gov.dluhc.notificationsapi.service.exception.GssCodeMismatchException
import javax.servlet.RequestDispatcher.ERROR_MESSAGE
import javax.servlet.RequestDispatcher.ERROR_STATUS_CODE

@ControllerAdvice
class GlobalExceptionHandler(
    private var errorAttributes: ApiRequestErrorAttributes
) : ResponseEntityExceptionHandler() {

    @ExceptionHandler(value = [GovNotifyApiNotFoundException::class])
    protected fun handleGovNotifyApiNotFoundException(
        e: GovNotifyApiNotFoundException,
        request: WebRequest
    ): ResponseEntity<Any>? {
        request.setAttribute(
            ERROR_MESSAGE,
            "Notification template not found for the given template type",
            SCOPE_REQUEST
        )
        return populateErrorResponseAndHandleExceptionInternal(e, NOT_FOUND, request)
    }

    @ExceptionHandler(
        value = [
            GovNotifyApiBadRequestException::class,
            GssCodeMismatchException::class,
        ]
    )
    protected fun handleBadRequestException(
        e: Exception,
        request: WebRequest
    ): ResponseEntity<Any>? {
        return populateErrorResponseAndHandleExceptionInternal(e, BAD_REQUEST, request)
    }

    override fun handleHttpMessageNotReadable(
        e: HttpMessageNotReadableException,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> {
        return populateErrorResponseAndHandleExceptionInternal(e, status, request)
    }

    override fun handleMethodArgumentNotValid(
        e: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> {
        return populateErrorResponseAndHandleExceptionInternal(e, status, request)
    }

    override fun handleTypeMismatch(
        ex: TypeMismatchException,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> {
        request.setAttribute(ERROR_MESSAGE, ex.cause?.message ?: "", SCOPE_REQUEST)
        return populateErrorResponseAndHandleExceptionInternal(ex, status, request)
    }

    private fun populateErrorResponseAndHandleExceptionInternal(
        exception: Exception,
        status: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> {
        request.setAttribute(ERROR_STATUS_CODE, status.value(), SCOPE_REQUEST)
        val body = errorAttributes.getErrorResponse(request)
        return handleExceptionInternal(exception, body, HttpHeaders(), status, request)
    }
}
