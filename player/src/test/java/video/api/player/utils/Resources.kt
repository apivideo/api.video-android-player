package video.api.player.utils

import java.io.FileNotFoundException
import java.nio.file.Files
import java.nio.file.Paths

object Resources {
    fun readFile(path: String): String {
        val resource = this.javaClass.getResource(path) ?: throw FileNotFoundException(path)
        return String(Files.readAllBytes(Paths.get(resource.toURI())))
    }
}