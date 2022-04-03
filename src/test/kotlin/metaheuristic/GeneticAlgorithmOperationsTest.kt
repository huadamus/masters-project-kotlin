package metaheuristic

import simulation.SingularSimulationOutcome
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class GeneticAlgorithmOperationsTest {

    @Test
    fun paretoEvaluate() {
        val data = listOf(
            SingularSimulationOutcome(-14.0, 8.0),
            SingularSimulationOutcome(14.0, 8.0),
            SingularSimulationOutcome(14.0, 8.5),
            SingularSimulationOutcome(12.5, 7.0),
            SingularSimulationOutcome(-14.0, 7.0),
            SingularSimulationOutcome(15.0, 8.0),
        )
        val output = paretoEvaluate(data)
        assertEquals(listOf(
            SingularSimulationOutcome(15.0, 8.0),
            SingularSimulationOutcome(12.5, 7.0)
        ).toSet(),
            output.toSet())
    }

    @Test
    fun calculateParetoPurity() {
        val betterFront = listOf(
            SingularSimulationOutcome(2.0, 1.0),
            SingularSimulationOutcome(3.0, 3.0),
            SingularSimulationOutcome(4.0, 4.0),
        )
        val worseFront = listOf(
            SingularSimulationOutcome(2.0, 4.0),
            SingularSimulationOutcome(3.0, 5.0),
        )
        val mainFront = betterFront + worseFront
        val evaluatedMainFront = paretoEvaluate(mainFront) as List<SingularSimulationOutcome>
        assertEquals(1.0, calculateParetoPurity(betterFront, evaluatedMainFront))
        assertEquals(0.0, calculateParetoPurity(worseFront, evaluatedMainFront))
    }
}