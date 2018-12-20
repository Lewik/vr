package lewik.vr

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication


@SpringBootApplication
class Server

fun main(args: Array<String>) {
    runApplication<Server>(*args)
}

