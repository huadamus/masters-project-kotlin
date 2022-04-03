package metaheuristic

import simulation.SimulationOutcome
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class GeneticAlgorithmOperationsTest {

    @Test
    fun paretoEvaluate() {
        val data = listOf(
            SimulationOutcome(-14.0, 8.0),
            SimulationOutcome(14.0, 8.0),
            SimulationOutcome(14.0, 8.5),
            SimulationOutcome(12.5, 7.0),
            SimulationOutcome(-14.0, 7.0),
            SimulationOutcome(15.0, 8.0),
        )
        val output = paretoEvaluate(data)
        assertEquals(listOf(
            SimulationOutcome(15.0, 8.0),
            SimulationOutcome(12.5, 7.0)
        ).toSet(),
            output.toSet())
    }

    @Test
    fun calculateParetoPurity() {
        val betterFront = listOf(
            SimulationOutcome(2.0, 1.0),
            SimulationOutcome(3.0, 3.0),
            SimulationOutcome(4.0, 4.0),
        )
        val worseFront = listOf(
            SimulationOutcome(2.0, 4.0),
            SimulationOutcome(3.0, 5.0),
        )
        val mainFront = betterFront + worseFront
        val evaluatedMainFront = paretoEvaluate(mainFront)
        assertEquals(1.0, calculateParetoPurity(betterFront, evaluatedMainFront))
        assertEquals(0.0, calculateParetoPurity(worseFront, evaluatedMainFront))
    }
}