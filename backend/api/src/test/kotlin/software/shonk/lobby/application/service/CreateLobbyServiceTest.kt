package software.shonk.lobby.application.service

import io.mockk.slot
import io.mockk.spyk
import io.mockk.verify
import io.mockk.verifySequence
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import software.shonk.interpreter.MockShork
import software.shonk.lobby.adapters.incoming.CreateLobbyCommand
import software.shonk.lobby.adapters.outgoing.MemoryLobbyManager
import software.shonk.lobby.application.port.outgoing.SaveLobbyPort
import software.shonk.lobby.domain.Lobby

class CreateLobbyServiceTest {

    private lateinit var createLobbyService: CreateLobbyService
    // todo the stuff we do here with saveLobbyPort should be testhelpers that directly access the
    // MemoryLobbyManager stuff or something similar
    private lateinit var saveLobbyPort: SaveLobbyPort

    // The in-memory lobby management also serves as a kind of mock here.
    @BeforeEach
    fun setUp() {
        val lobbyManager = spyk<MemoryLobbyManager>()
        saveLobbyPort = lobbyManager
        createLobbyService = CreateLobbyService(MockShork(), saveLobbyPort)
    }

    @Test
    fun `create lobby containing playnerame when valid playername is given`() {
        val validPlayername = "playerA"
        createLobbyService.createLobby(CreateLobbyCommand(validPlayername))

        verify(exactly = 1) {
            saveLobbyPort.saveLobby(match { it -> it.joinedPlayers.contains(validPlayername) })
        }
    }

    @Test
    fun `two lobbies created after one another have different ids`() {
        val lobbySlot1 = slot<Lobby>()
        val lobbySlot2 = slot<Lobby>()

        createLobbyService.createLobby(CreateLobbyCommand("playerA"))
        createLobbyService.createLobby(CreateLobbyCommand("playerB"))

        verifySequence {
            saveLobbyPort.saveLobby(capture(lobbySlot1))
            saveLobbyPort.saveLobby(capture(lobbySlot2))
        }

        assertNotEquals(lobbySlot1.captured.id, lobbySlot2.captured.id)
    }
}
