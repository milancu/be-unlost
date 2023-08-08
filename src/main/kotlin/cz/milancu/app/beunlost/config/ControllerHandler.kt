package cz.milancu.app.beunlost.config

import cz.milancu.app.beunlost.service.exceptions.DocumentNotFoundException
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ControllerHandler {

    private val logger = KotlinLogging.logger { }

    /**
     * Handles the DocumentNotFoundException.
     *
     * @param e The DocumentNotFoundException instance.
     * @return The ResponseEntity containing the error message.
     */
    @ExceptionHandler(value = [DocumentNotFoundException::class])
    fun handleDocumentNotFoundException(e: RuntimeException): ResponseEntity<String?>? {
        logger.error { "Not found exception: $e" }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
    }

    /**
     * Handles the illegal state exception.
     *
     * @param e The runtime exception representing the illegal state.
     * @return The ResponseEntity containing the error message and status code.
     */
    @ExceptionHandler(value = [IllegalStateException::class])
    fun handleIllegalStateException(e: RuntimeException): ResponseEntity<String?>? {
        logger.error { "Illegal state exception: $e" }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
    }

    /**
     * Handles internal exceptions and returns an appropriate response entity.
     *
     * @param e the RuntimeException that occurred
     * @return the response entity with appropriate status code and error message
     */
    @ExceptionHandler(value = [RuntimeException::class])
    fun handleInternalException(e: RuntimeException): ResponseEntity<String?>? {
        logger.error { "RuntimeException: $e" }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.message)
    }

    /**
     * Handles a NoSuchElement Exception and logs the error.
     *
     * @param e The exception that was thrown.
     * @return A ResponseEntity object with an HTTP status of INTERNAL_SERVER_ERROR and the exception message as the body.
     */
    @ExceptionHandler(value = [NoSuchElementException::class])
    fun handleNoSuchElementException(e: RuntimeException): ResponseEntity<String?>? {
        logger.error { "NoSuchElementException: $e" }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.message)
    }
}