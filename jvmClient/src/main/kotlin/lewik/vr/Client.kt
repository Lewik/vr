package lewik.vr

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder


@SpringBootApplication
class Client {

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplicationBuilder(Client::class.java).headless(false).run(*args)
        }
    }
}