package experiment

import simulation.SimulationOutcome

abstract class Experiment(protected val name: String) {

    abstract fun run()

    fun showResults() {
        val outcomes = calculateAndPrintOutcomes()
        drawChart(outcomes)
    }

    abstract fun calculateAndPrintOutcomes(): List<List<SimulationOutcome>>

    protected abstract fun drawChart(outcomes: List<List<SimulationOutcome>>)

    abstract fun saveChart(runId: Int, outcomes: List<List<SimulationOutcome>>)
}
