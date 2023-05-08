package cz.milancu.app.beunlost.service.exceptions

open class DocumentNotFoundException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)