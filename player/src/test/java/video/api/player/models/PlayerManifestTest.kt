package video.api.player.models

import org.junit.Assert.assertEquals
import org.junit.Test
import video.api.player.utils.Resources

class PlayerManifestTest {
    @Test
    fun `parse valid player json`() {
        val playerResourceJson = Resources.readFile("/assets/valid_player.json")
        val playerManifest = PlayerManifest.from(playerResourceJson)

        assertEquals("vi216aJPRd2WaCRBC6PVh2Ij", playerManifest.id)
    }

    @Test
    fun `parse player json with unknown key`() {
        val playerResourceJson = Resources.readFile("/assets/player_with_unknown_key.json")
        val playerManifest = PlayerManifest.from(playerResourceJson)

        assertEquals("vi216aJPRd2WaCRBC6PVh2Ij", playerManifest.id)
    }
}