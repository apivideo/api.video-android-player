package video.api.player.models

import org.junit.Assert.assertEquals
import org.junit.Test
import video.api.player.utils.Resources

class PlayerJsonTest {
    @Test
    fun `parse valid player json`() {
        val playerResourceJson = Resources.readFile("/assets/player.json")
        val playerJson = PlayerJson.from(playerResourceJson)

        assertEquals("vi216aJPRd2WaCRBC6PVh2Ij", playerJson.id)
    }
}