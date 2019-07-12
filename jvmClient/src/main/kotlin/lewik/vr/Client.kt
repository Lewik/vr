package lewik.vr

import org.springframework.beans.factory.getBean
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener


@SpringBootApplication
class Client {

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplicationBuilder(Client::class.java).headless(false).run(*args)
        }
    }

    @EventListener(ApplicationReadyEvent::class)
    fun doSomethingAfterStartup(event: ApplicationReadyEvent) {
        println("========================================================= S T A R T E D =====================================================================================================")
        val uiController = event.applicationContext.getBean<UiController>()
        val ui = Ui(uiController)
        uiController.ui = ui
    }
}