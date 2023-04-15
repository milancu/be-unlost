package cz.milancu.app.beunlost

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableAsync


@SpringBootApplication
@EnableAsync
class BeUnlostApplication

fun main(args: Array<String>) {
    runApplication<BeUnlostApplication>(*args)
}