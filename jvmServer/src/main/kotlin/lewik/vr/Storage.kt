package lewik.vr

import org.springframework.stereotype.Service

@Service
class Storage {
    /**
     * key - sessionUuid
     * Map<Int, Map<Int, PartFrame>> - its a screen: x -> y -> PartFrame
     */
    private val frames = mutableMapOf<String, MutableMap<Int, MutableMap<Int, PartFrame>>>()
    /**
     * key - sessionUuid
     */
    private val mousePositions = mutableMapOf<String, MousePosition>()

    private val sessionLastUpdates = mutableMapOf<String, Long>()

    @Synchronized
    fun addPartFrame(partFrame: PartFrame) {
        updateLastUpdateTime(partFrame)
        @Suppress("ReplacePutWithAssignment")
        frames
            .getOrPut(partFrame.sessionUuid) { mutableMapOf() }
            .getOrPut(partFrame.startX) { mutableMapOf() }
            .put(partFrame.startY, partFrame)

        val dsa = frames.values.flatMap { it.values.flatMap { it.values } }.map { it.createTime }.max()
        clearExcessScreens()
    }

    @Synchronized
    fun addMousePosition(mousePosition: MousePosition) {
        updateLastUpdateTime(mousePosition)
        mousePositions[mousePosition.sessionUuid] = mousePosition
        clearExcessScreens()
    }

    @Synchronized
    fun getSessionUpdates(sessionUuid: String, requestedLastUpdateTime: Long): SessionUpdate {
        val sessionLastUpdate = sessionLastUpdates[sessionUuid]
        return if (sessionLastUpdate != null) {

            val maxFrameTime = frames
                .getValue(sessionUuid)
                .flatMap { it.value.values }
                .map { it.createTime }
                .max() ?: requestedLastUpdateTime

            val partFrames = frames
                .getValue(sessionUuid)
                .flatMap { it.value.values }
                .filter { it.createTime > requestedLastUpdateTime }

            val mousePosition = mousePositions
                .getValue(sessionUuid)
                .takeIf { it.createTime > requestedLastUpdateTime }

            val newFrames = maxFrameTime > requestedLastUpdateTime

            SessionUpdate(
                partFrames = partFrames,
                mousePosition = mousePosition,
                sessionLastUpdate = maxFrameTime
            )
        } else {
            SessionUpdate(
                partFrames = emptyList(),
                mousePosition = null,
                sessionLastUpdate = requestedLastUpdateTime
            )
        }
    }

    private fun clearExcessScreens() {
        if (sessionLastUpdates.size > 10) {
            val sessionUuid = sessionLastUpdates.minBy { it.value }?.key
            if (sessionUuid != null) {
                frames.remove(sessionUuid)
                mousePositions.remove(sessionUuid)
                sessionLastUpdates.remove(sessionUuid)
            }
        }
    }

    private fun updateLastUpdateTime(uiPacket: UiPacket) {
        val lastUpdate = sessionLastUpdates[uiPacket.sessionUuid]
        if (lastUpdate == null || lastUpdate < uiPacket.createTime) {
            sessionLastUpdates[uiPacket.sessionUuid] = uiPacket.createTime
        }
    }
}
