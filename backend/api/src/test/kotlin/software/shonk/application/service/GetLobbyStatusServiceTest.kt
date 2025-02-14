package software.shonk.application.service

import io.mockk.spyk
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import software.shonk.adapters.outgoing.MemoryLobbyManager
import software.shonk.application.port.incoming.GetLobbyStatusQuery
import software.shonk.application.port.outgoing.LoadLobbyPort
import software.shonk.application.port.outgoing.SaveLobbyPort
import software.shonk.domain.Lobby
import software.shonk.interpreter.MockShork

class GetLobbyStatusServiceTest {

    private lateinit var getLobbyStatusQuery: GetLobbyStatusQuery
    private lateinit var loadLobbyPort: LoadLobbyPort
    private lateinit var saveLobbyPort: SaveLobbyPort

    // The in-memory lobby management also serves as a kind of mock here.
    @BeforeEach
    fun setUp() {
        val lobbyManager = spyk<MemoryLobbyManager>()
        loadLobbyPort = lobbyManager
        saveLobbyPort = lobbyManager
        getLobbyStatusQuery = GetLobbyStatusService(loadLobbyPort)
    }

    @Test
    fun `get status for the lobby fails if lobby does not exist`() {
        val result = getLobbyStatusQuery.getLobbyStatus(0L)

        assertEquals(true, result.isFailure)
        assertEquals("No lobby with that id", result.exceptionOrNull()?.message)
    }

    @Test
    fun `get lobby status with visualization data`() {
        // Given...
        val lobbyId = 0L
        val lobby =
            Lobby(
                lobbyId,
                hashMapOf("playerA" to "MOV 0, 1", "playerB" to "MOV 0, 1"),
                MockShork(),
                joinedPlayers = mutableListOf("playerA", "playerB"),
            )
        lobby.run()
        saveLobbyPort.saveLobby(lobby)

        // When...
        val result = getLobbyStatusQuery.getLobbyStatus(lobbyId).getOrThrow()

        // Then...
        assertTrue(result.visualizationData.isNotEmpty())
    }

    @Test
    fun `get lobby status without visualization data`() {
        // Given...
        val lobbyId = 0L
        val lobby =
            Lobby(
                lobbyId,
                hashMapOf("playerA" to "MOV 0, 1", "playerB" to "MOV 0, 1"),
                MockShork(),
                joinedPlayers = mutableListOf("playerA", "playerB"),
            )
        lobby.run()
        saveLobbyPort.saveLobby(lobby)

        // When...
        val result =
            getLobbyStatusQuery
                .getLobbyStatus(lobbyId, includeRoundInformation = false)
                .getOrThrow()

        // Then...
        assertTrue(result.visualizationData.isEmpty())
    }
}
