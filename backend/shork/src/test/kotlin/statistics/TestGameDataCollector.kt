package statistics

import mocks.KillProgramInstruction
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import software.shonk.interpreter.Settings
import software.shonk.interpreter.internal.InternalShork
import software.shonk.interpreter.internal.program.Program
import software.shonk.interpreter.internal.statistics.GameDataCollector
import software.shonk.interpreter.internal.statistics.IGameDataCollector
import software.shonk.interpreter.internal.statistics.RoundInformation

internal class TestGameDataCollector {
    /*
     * Single method tests:
     */

    val settings = Settings().toInternalSettings().getOrThrow()
    var shork = InternalShork(settings)

    @BeforeEach
    fun setupShork() {
        shork = InternalShork(settings)
    }

    @Test
    fun `test collectMemoryRead`() {
        val collector: IGameDataCollector = GameDataCollector()

        val shorky = Program("shorky", shork)
        shorky.createProcessAt(0)

        collector.startRoundForProgram(shorky)
        collector.collectMemoryRead(42)
        collector.collectMemoryRead(1337)
        collector.collectMemoryRead(1234)
        collector.endRoundForProgram(shorky)

        val memoryReads = collector.getGameStatistics().map { it.memoryReads }.flatten()
        assertEquals(listOf(42, 1337, 1234), memoryReads)
    }

    @Test
    fun `test collectMemoryWrite`() {
        val collector: IGameDataCollector = GameDataCollector()

        val shorky = Program("shorky", shork)
        shorky.createProcessAt(0)

        collector.startRoundForProgram(shorky)
        collector.collectMemoryWrite(42, KillProgramInstruction())
        collector.collectMemoryWrite(1337, KillProgramInstruction())
        collector.collectMemoryWrite(1234, KillProgramInstruction())
        collector.endRoundForProgram(shorky)

        val memoryWrites = collector.getGameStatistics().map { it.memoryWrites }.flatten()
        assertEquals(listOf(42, 1337, 1234), memoryWrites)
    }

    @Test
    fun `test collect process data before tick`() {
        val collector: IGameDataCollector = GameDataCollector()

        val shorky = Program("shorky", shork)
        val shorkysProcess =
            shorky.let {
                it.createProcessAt(0)
                it.processes.last()
            }

        collector.startRoundForProgram(shorky)
        collector.collectProcessDataBeforeTick(shorkysProcess)
        shorkysProcess.programCounter = 42
        collector.endRoundForProgram(shorky)

        val programCounterBefore =
            collector.getGameStatistics().map { it.programCounterBefore }.first()
        assertEquals(0, programCounterBefore)
    }

    @Test
    fun `test collect process data after tick`() {
        val collector: IGameDataCollector = GameDataCollector()

        val shorky = Program("shorky", shork)
        val shorkysProcess =
            shorky.let {
                it.createProcessAt(0)
                it.processes.last()
            }

        collector.startRoundForProgram(shorky)
        shorkysProcess.programCounter = 42
        collector.collectProcessDataAfterTick(shorkysProcess)
        collector.endRoundForProgram(shorky)

        val programCounterAfter =
            collector.getGameStatistics().map { it.programCounterAfter }.first()
        assertEquals(42, programCounterAfter)
    }

    @Test
    fun `test other program counter of other processes`() {
        val collector: IGameDataCollector = GameDataCollector()

        val shorky = Program("shorky", shork)
        val shorkysProcess =
            shorky.let {
                it.createProcessAt(0)
                it.processes.last()
            }

        // Second process
        shorky.createProcessAt(1000)

        // Third process
        shorky.createProcessAt(4200)

        collector.startRoundForProgram(shorky)
        collector.collectProcessDataBeforeTick(shorkysProcess)
        shorkysProcess.programCounter = 42
        collector.collectProcessDataAfterTick(shorkysProcess)
        collector.endRoundForProgram(shorky)

        val programCountersOfOtherProcesses =
            collector.getGameStatistics().map { it.programCountersOfOtherProcesses }.flatten()
        assertEquals(listOf(1000, 4200), programCountersOfOtherProcesses)
    }

    @Test
    fun `test process died`() {
        val collector: IGameDataCollector = GameDataCollector()

        val shorky = Program("shorky", shork)
        val shorkysProcess =
            shorky.let {
                it.createProcessAt(0)
                it.processes.last()
            }

        collector.startRoundForProgram(shorky)
        shorkysProcess.programCounter = 42
        shorkysProcess.tick()
        collector.collectProcessDataAfterTick(shorkysProcess)
        collector.endRoundForProgram(shorky)

        val processDied = collector.getGameStatistics().map { it.processDied }.first()
        assertEquals(true, processDied)
    }

    /*
     * Complete round tests:
     */

    @Test
    fun `test single player single round, single process`() {
        val collector: IGameDataCollector = GameDataCollector()

        val shorky = Program("shorky", shork)
        val shorkysProcess =
            shorky.let {
                it.createProcessAt(0)
                it.processes.last()
            }

        collector.startRoundForProgram(shorky)
        collector.collectProcessDataBeforeTick(shorkysProcess)
        collector.collectMemoryRead(420)
        collector.collectMemoryWrite(1337, KillProgramInstruction())
        collector.collectMemoryRead(1234)
        collector.collectMemoryWrite(4321, KillProgramInstruction())
        shorkysProcess.programCounter = 42
        collector.collectProcessDataAfterTick(shorkysProcess)
        collector.endRoundForProgram(shorky)

        val stats = collector.getGameStatistics()
        assert(stats.size == 1)
        assertEquals(
            RoundInformation(
                playerId = "shorky",
                programCounterBefore = 0,
                programCounterAfter = 42,
                programCountersOfOtherProcesses = emptyList(),
                memoryReads = listOf(420, 1234),
                memoryWrites = listOf(1337, 4321),
                processDied = false,
            ),
            stats[0],
        )
    }

    @Test
    fun `test single player multiple rounds, single process`() {
        val collector: IGameDataCollector = GameDataCollector()

        val shorky = Program("shorky", shork)
        val shorkysProcess =
            shorky.let {
                it.createProcessAt(0)
                it.processes.last()
            }

        // First round
        collector.startRoundForProgram(shorky)
        collector.collectProcessDataBeforeTick(shorkysProcess)
        collector.collectMemoryRead(420)
        collector.collectMemoryWrite(1337, KillProgramInstruction())
        collector.collectMemoryRead(1234)
        shorkysProcess.programCounter = 42
        collector.collectProcessDataAfterTick(shorkysProcess)
        collector.endRoundForProgram(shorky)

        // Second round
        collector.startRoundForProgram(shorky)
        collector.collectProcessDataBeforeTick(shorkysProcess)
        collector.collectMemoryRead(1337)
        collector.collectMemoryWrite(420, KillProgramInstruction())
        collector.collectMemoryRead(4321)
        shorkysProcess.programCounter = 1337
        collector.collectProcessDataAfterTick(shorkysProcess)
        collector.endRoundForProgram(shorky)

        val stats = collector.getGameStatistics()
        assert(stats.size == 2)
        assertEquals(
            RoundInformation(
                playerId = "shorky",
                programCounterBefore = 0,
                programCounterAfter = 42,
                programCountersOfOtherProcesses = emptyList(),
                memoryReads = listOf(420, 1234),
                memoryWrites = listOf(1337),
                processDied = false,
            ),
            stats[0],
        )
        assertEquals(
            RoundInformation(
                playerId = "shorky",
                programCounterBefore = 42,
                programCounterAfter = 1337,
                programCountersOfOtherProcesses = emptyList(),
                memoryReads = listOf(1337, 4321),
                memoryWrites = listOf(420),
                processDied = false,
            ),
            stats[1],
        )
    }

    @Test
    fun `test multiple players single round, single process`() {
        val collector: IGameDataCollector = GameDataCollector()

        val shorky = Program("shorky", shork)
        val shorkysProcess =
            shorky.let {
                it.createProcessAt(0)
                it.processes.last()
            }

        val evilShorky = Program("evilShorky", shork)
        val evilShorkysProcess =
            evilShorky.let {
                it.createProcessAt(0)
                it.processes.last()
            }

        collector.startRoundForProgram(shorky)
        collector.collectProcessDataBeforeTick(shorkysProcess)
        collector.collectMemoryRead(10)
        collector.collectMemoryWrite(20, KillProgramInstruction())
        collector.collectMemoryRead(11)
        collector.collectMemoryWrite(21, KillProgramInstruction())
        shorkysProcess.programCounter = 42
        collector.collectProcessDataAfterTick(shorkysProcess)
        collector.endRoundForProgram(shorky)

        collector.startRoundForProgram(evilShorky)
        collector.collectProcessDataBeforeTick(evilShorkysProcess)
        collector.collectMemoryRead(30)
        collector.collectMemoryWrite(40, KillProgramInstruction())
        collector.collectMemoryRead(31)
        collector.collectMemoryWrite(41, KillProgramInstruction())
        evilShorkysProcess.programCounter = 42
        collector.collectProcessDataAfterTick(evilShorkysProcess)
        collector.endRoundForProgram(evilShorky)

        val stats = collector.getGameStatistics()
        assert(stats.size == 2)
        assertEquals(
            RoundInformation(
                playerId = "shorky",
                programCounterBefore = 0,
                programCounterAfter = 42,
                programCountersOfOtherProcesses = emptyList(),
                memoryReads = listOf(10, 11),
                memoryWrites = listOf(20, 21),
                processDied = false,
            ),
            stats[0],
        )
        assertEquals(
            RoundInformation(
                playerId = "evilShorky",
                programCounterBefore = 0,
                programCounterAfter = 42,
                programCountersOfOtherProcesses = emptyList(),
                memoryReads = listOf(30, 31),
                memoryWrites = listOf(40, 41),
                processDied = false,
            ),
            stats[1],
        )
    }

    @Test
    fun `test multiple players multiple rounds, single process`() {
        val collector: IGameDataCollector = GameDataCollector()

        val shorky = Program("shorky", shork)
        val shorkysProcess =
            shorky.let {
                it.createProcessAt(0)
                it.processes.last()
            }

        val evilShorky = Program("evilShorky", shork)
        val evilShorkysProcess =
            evilShorky.let {
                it.createProcessAt(0)
                it.processes.last()
            }

        // First round
        collector.startRoundForProgram(shorky)
        collector.collectProcessDataBeforeTick(shorkysProcess)
        collector.collectMemoryRead(10)
        collector.collectMemoryWrite(20, KillProgramInstruction())
        collector.collectMemoryRead(11)
        collector.collectMemoryWrite(21, KillProgramInstruction())
        shorkysProcess.programCounter = 42
        collector.collectProcessDataAfterTick(shorkysProcess)
        collector.endRoundForProgram(shorky)

        collector.startRoundForProgram(evilShorky)
        collector.collectProcessDataBeforeTick(evilShorkysProcess)
        collector.collectMemoryRead(30)
        collector.collectMemoryWrite(40, KillProgramInstruction())
        collector.collectMemoryRead(31)
        collector.collectMemoryWrite(41, KillProgramInstruction())
        evilShorkysProcess.programCounter = 1337
        collector.collectProcessDataAfterTick(evilShorkysProcess)
        collector.endRoundForProgram(evilShorky)

        // Second round
        collector.startRoundForProgram(shorky)
        collector.collectProcessDataBeforeTick(shorkysProcess)
        collector.collectMemoryRead(100)
        collector.collectMemoryWrite(200, KillProgramInstruction())
        collector.collectMemoryRead(110)
        collector.collectMemoryWrite(210, KillProgramInstruction())
        shorkysProcess.programCounter = 24
        collector.collectProcessDataAfterTick(shorkysProcess)
        collector.endRoundForProgram(shorky)

        collector.startRoundForProgram(evilShorky)
        collector.collectProcessDataBeforeTick(evilShorkysProcess)
        collector.collectMemoryRead(300)
        collector.collectMemoryWrite(400, KillProgramInstruction())
        collector.collectMemoryRead(310)
        collector.collectMemoryWrite(410, KillProgramInstruction())
        evilShorkysProcess.programCounter = 7331
        collector.collectProcessDataAfterTick(evilShorkysProcess)
        collector.endRoundForProgram(evilShorky)

        val stats = collector.getGameStatistics()
        assert(stats.size == 4)
        // First round
        assertEquals(
            RoundInformation(
                playerId = "shorky",
                programCounterBefore = 0,
                programCounterAfter = 42,
                programCountersOfOtherProcesses = emptyList(),
                memoryReads = listOf(10, 11),
                memoryWrites = listOf(20, 21),
                processDied = false,
            ),
            stats[0],
        )
        assertEquals(
            RoundInformation(
                playerId = "evilShorky",
                programCounterBefore = 0,
                programCounterAfter = 1337,
                programCountersOfOtherProcesses = emptyList(),
                memoryReads = listOf(30, 31),
                memoryWrites = listOf(40, 41),
                processDied = false,
            ),
            stats[1],
        )
        // Second round
        assertEquals(
            RoundInformation(
                playerId = "shorky",
                programCounterBefore = 42,
                programCounterAfter = 24,
                programCountersOfOtherProcesses = emptyList(),
                memoryReads = listOf(100, 110),
                memoryWrites = listOf(200, 210),
                processDied = false,
            ),
            stats[2],
        )
        assertEquals(
            RoundInformation(
                playerId = "evilShorky",
                programCounterBefore = 1337,
                programCounterAfter = 7331,
                programCountersOfOtherProcesses = emptyList(),
                memoryReads = listOf(300, 310),
                memoryWrites = listOf(400, 410),
                processDied = false,
            ),
            stats[3],
        )
    }

    @Test
    fun `test single player single round, multiple processes`() {
        val collector: IGameDataCollector = GameDataCollector()

        val shorky = Program("shorky", shork)
        val shorkysProcess =
            shorky.let {
                it.createProcessAt(0)
                it.processes.last()
            }
        val shorkysProcess2 =
            shorky.let {
                it.createProcessAt(1000)
                it.processes.last()
            }

        // First round
        collector.startRoundForProgram(shorky)
        collector.collectProcessDataBeforeTick(shorkysProcess)
        collector.collectMemoryRead(42)
        collector.collectMemoryWrite(1337, KillProgramInstruction())
        collector.collectMemoryRead(1234)
        collector.collectMemoryWrite(4321, KillProgramInstruction())
        shorkysProcess.programCounter = 43
        collector.collectProcessDataAfterTick(shorkysProcess)
        collector.endRoundForProgram(shorky)

        // Second round
        collector.startRoundForProgram(shorky)
        collector.collectProcessDataBeforeTick(shorkysProcess2)
        collector.collectMemoryRead(420)
        collector.collectMemoryWrite(13370, KillProgramInstruction())
        collector.collectMemoryRead(12340)
        collector.collectMemoryWrite(43210, KillProgramInstruction())
        shorkysProcess2.programCounter = 1001
        collector.collectProcessDataAfterTick(shorkysProcess2)
        collector.endRoundForProgram(shorky)

        val stats = collector.getGameStatistics()
        assert(stats.size == 2)
        assertEquals(
            RoundInformation(
                playerId = "shorky",
                programCounterBefore = 0,
                programCounterAfter = 43,
                programCountersOfOtherProcesses = listOf(1000),
                memoryReads = listOf(42, 1234),
                memoryWrites = listOf(1337, 4321),
                processDied = false,
            ),
            stats[0],
        )
        assertEquals(
            RoundInformation(
                playerId = "shorky",
                programCounterBefore = 1000,
                programCounterAfter = 1001,
                programCountersOfOtherProcesses = listOf(43),
                memoryReads = listOf(420, 12340),
                memoryWrites = listOf(13370, 43210),
                processDied = false,
            ),
            stats[1],
        )
    }

    @Test
    fun `test multiple players multiple rounds, multiple processes`() {
        val collector: IGameDataCollector = GameDataCollector()

        val shorky = Program("shorky", shork)
        val shorkysProcess =
            shorky.let {
                it.createProcessAt(0)
                it.processes.last()
            }
        val shorkysProcess2 =
            shorky.let {
                it.createProcessAt(1000)
                it.processes.last()
            }

        val evilShorky = Program("evilShorky", shork)
        val evilShorkysProcess =
            evilShorky.let {
                it.createProcessAt(2000)
                it.processes.last()
            }
        val evilShorkysProcess2 =
            evilShorky.let {
                it.createProcessAt(3000)
                it.processes.last()
            }

        // First round
        collector.startRoundForProgram(shorky)
        collector.collectProcessDataBeforeTick(shorkysProcess)
        collector.collectMemoryRead(10)
        collector.collectMemoryWrite(20, KillProgramInstruction())
        collector.collectMemoryRead(11)
        collector.collectMemoryWrite(21, KillProgramInstruction())
        shorkysProcess.programCounter = 42
        collector.collectProcessDataAfterTick(shorkysProcess)
        collector.endRoundForProgram(shorky)

        collector.startRoundForProgram(evilShorky)
        collector.collectProcessDataBeforeTick(evilShorkysProcess)
        collector.collectMemoryRead(1100)
        collector.collectMemoryWrite(1200, KillProgramInstruction())
        collector.collectMemoryRead(1101)
        collector.collectMemoryWrite(1202, KillProgramInstruction())
        evilShorkysProcess.programCounter = 43
        collector.collectProcessDataAfterTick(evilShorkysProcess)
        collector.endRoundForProgram(evilShorky)

        // Second round
        collector.startRoundForProgram(shorky)
        collector.collectProcessDataBeforeTick(shorkysProcess2)
        collector.collectMemoryRead(2100)
        collector.collectMemoryWrite(2200, KillProgramInstruction())
        collector.collectMemoryRead(2101)
        collector.collectMemoryWrite(2201, KillProgramInstruction())
        shorkysProcess2.programCounter = 4200
        collector.collectProcessDataAfterTick(shorkysProcess2)
        collector.endRoundForProgram(shorky)

        collector.startRoundForProgram(evilShorky)
        collector.collectProcessDataBeforeTick(evilShorkysProcess2)
        collector.collectMemoryRead(3100)
        collector.collectMemoryWrite(3200, KillProgramInstruction())
        collector.collectMemoryRead(3101)
        collector.collectMemoryWrite(3202, KillProgramInstruction())
        evilShorkysProcess2.programCounter = 4300
        collector.collectProcessDataAfterTick(evilShorkysProcess2)
        collector.endRoundForProgram(evilShorky)

        val stats = collector.getGameStatistics()
        assert(stats.size == 4)
        assertEquals(
            RoundInformation(
                playerId = "shorky",
                programCounterBefore = 0,
                programCounterAfter = 42,
                programCountersOfOtherProcesses = listOf(1000),
                memoryReads = listOf(10, 11),
                memoryWrites = listOf(20, 21),
                processDied = false,
            ),
            stats[0],
        )
        assertEquals(
            RoundInformation(
                playerId = "evilShorky",
                programCounterBefore = 2000,
                programCounterAfter = 43,
                programCountersOfOtherProcesses = listOf(3000),
                memoryReads = listOf(1100, 1101),
                memoryWrites = listOf(1200, 1202),
                processDied = false,
            ),
            stats[1],
        )
        assertEquals(
            RoundInformation(
                playerId = "shorky",
                programCounterBefore = 1000,
                programCounterAfter = 4200,
                programCountersOfOtherProcesses = listOf(42),
                memoryReads = listOf(2100, 2101),
                memoryWrites = listOf(2200, 2201),
                processDied = false,
            ),
            stats[2],
        )
        assertEquals(
            RoundInformation(
                playerId = "evilShorky",
                programCounterBefore = 3000,
                programCounterAfter = 4300,
                programCountersOfOtherProcesses = listOf(43),
                memoryReads = listOf(3100, 3101),
                memoryWrites = listOf(3200, 3202),
                processDied = false,
            ),
            stats[3],
        )
    }
}
