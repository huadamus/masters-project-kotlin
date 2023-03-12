package experiment

import DRAW_CHART
import simulation.SimulationOutcome

abstract class Experiment(protected val name: String) {

    abstract fun run()

    fun showResults() {
        val outcomes = calculateAndPrintOutcomes()
        if(DRAW_CHART) {
            drawChart(outcomes)
        }
        //saveChart(outcomes)
    }

    abstract fun calculateAndPrintOutcomes(): List<List<SimulationOutcome>>

    protected abstract fun drawChart(outcomes: List<List<SimulationOutcome>>)

    abstract fun saveChart(outcomes: List<List<SimulationOutcome>>)
}
