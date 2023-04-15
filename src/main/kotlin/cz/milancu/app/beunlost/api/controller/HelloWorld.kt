package cz.milancu.app.beunlost.api.controller

import cz.milancu.app.beunlost.service.UserService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HelloWorld(
    private val userService: UserService
) {

    @GetMapping("/hello")
    fun sayHello(): String {
        return "Ahoj ${userService.getCurrentUser().firstname}"
    }
}