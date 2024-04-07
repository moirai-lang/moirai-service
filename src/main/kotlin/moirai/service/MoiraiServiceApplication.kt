package moirai.service

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MoiraiServiceApplication

fun main(args: Array<String>) {
	runApplication<MoiraiServiceApplication>(*args)
}
