package lewik.vr

import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap

@Service
class SpeedCalculator(
    private val uiGateway: UiGateway
) {
    private val entries = ConcurrentHashMap<Long, Int>()

    fun handle(data: ByteArray) {
        entries[System.currentTimeMillis()] = data.size
        calculate() //TODO : make scheduler per second ?
    }

    fun calculate() {
        val calculateRange = 10000

        val toDelete = entries.keys
            .filter { it < System.currentTimeMillis() - calculateRange }
        toDelete.forEach { entries.remove(it) }

        val speed = entries.values.sum() / calculateRange
        uiGateway.send(Speed(speed))
    }
}