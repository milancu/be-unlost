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

    @ExceptionHandler(value = [DocumentNotFoundException::class])
    fun handleDocumentNotFoundException(e: RuntimeException): ResponseEntity<String?>? {
        logger.error { "Not found exception: $e" }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
    }

    @ExceptionHandler(value = [IllegalStateException::class])
    fun handleIllegalStateException(e: RuntimeException): ResponseEntity<String?>? {
        logger.error { "Illegal state exception: $e" }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
    }

    @ExceptionHandler(value = [RuntimeException::class])
    fun handleInternalException(e: RuntimeException): ResponseEntity<String?>? {
        logger.error { "RuntimeException: $e" }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.message)
    }

    @ExceptionHandler(value = [NoSuchElementException::class])
    fun handleNoSuchElementException(e: RuntimeException): ResponseEntity<String?>? {
        logger.error { "NoSuchElementException: $e" }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.message)
    }
}