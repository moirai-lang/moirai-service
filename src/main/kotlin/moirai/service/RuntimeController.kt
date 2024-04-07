package moirai.service

import org.springframework.web.bind.annotation.*

@RestController
class RuntimeController {
    @PostMapping("/execute")
    @CrossOrigin
    fun execute(@RequestBody body: String): String {
        return ""
    }
}