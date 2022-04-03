package experiment
import java.util.*

import simulation.Runner
import RUNS
import data.DataLoader
import model.Date
import output.FourTypesChartDrawer
import simulation.SimulationOutcome
import simulation.SingularSimulationOutcome
import metaheuristic.*

class FourTypesExperiment : Experiment("four_types") {

    private val genericHvName = "$name-generic-hv"
    private val genericNsgaName = "$name-generic-nsga2"
    private val coevolutionHvName = "$name-coevolution-hv"
    private val coevolutionNsgaName = "$name-coevolution-nsga2"
    private val genericGeneticAlgorithmHvPareto = GenericGeneticAlgorithm(
        genericHvName,
        "${logString}No coevolution, HV-Pareto",
        listOf(Pair(Date(1, 1, 1988), Date(1, 1, 2018))),
        360,
        true,
        SelectionMethod.HV_PARETO,
        DataLoader.loadDevelopedData(),
        DataLoader.loadEmergingData(),
        DataLoader.loadCrbAndOilData(),
        DataLoader.loadGoldUsdData(),
        DataLoader.loadShillerPESP500Ratio(),
        DataLoader.loadDowToGoldData()
    )
    private val genericGeneticAlgorithmNsgaII = GenericGeneticAlgorithm(
        genericNsgaName,
        "${logString}No coevolution, NSGA-II",
        listOf(Pair(Date(1, 1, 1988), Date(1, 1, 2018))),
        360,
        true,
        SelectionMethod.NSGA_II,
        DataLoader.loadDevelopedData(),
        DataLoader.loadEmergingData(),
        DataLoader.loadCrbAndOilData(),
        DataLoader.loadGoldUsdData(),
        DataLoader.loadShillerPESP500Ratio(),
        DataLoader.loadDowToGoldData()
    )
    private val coevolutionGeneticAlgorithmHvPareto = CoevolutionGeneticAlgorithm(
        coevolutionHvName,
        "${logString}Coevolution, HV-Pareto",
        listOf(Pair(Date(1, 1, 1988), Date(1, 1, 2018))),
        360,
        true,
        SelectionMethod.HV_PARETO,
        DataLoader.loadDevelopedData(),
        DataLoader.loadEmergingData(),
        DataLoader.loadCrbAndOilData(),
        DataLoader.loadGoldUsdData(),
        DataLoader.loadShillerPESP500Ratio(),
        DataLoader.loadDowToGoldData()
    )
    private val coevolutionGeneticAlgorithmNsgaII = CoevolutionGeneticAlgorithm(
        coevolutionNsgaName,
        "${logString}Coevolution, NSGA-II",
        listOf(Pair(Date(1, 1, 1988), Date(1, 1, 2018))),
        360,
        true,
        SelectionMethod.NSGA_II,
        DataLoader.loadDevelopedData(),
        DataLoader.loadEmergingData(),
        DataLoader.loadCrbAndOilData(),
        DataLoader.loadGoldUsdData(),
        DataLoader.loadShillerPESP500Ratio(),
        DataLoader.loadDowToGoldData()
    )
    private lateinit var purityValues: List<Double>

    override fun run() {
        val genericGeneticAlgorithmHvParetoState =
            (Runner.runCombining(genericGeneticAlgorithmHvPareto, RUNS) as GenericGeneticAlgorithmState)
        val genericGeneticAlgorithmNsgaIIState =
            (Runner.runCombining(genericGeneticAlgorithmNsgaII, RUNS) as GenericGeneticAlgorithmState)
        val coevolutionGeneticAlgorithmHvParetoState =
            (Runner.runCombining(coevolutionGeneticAlgorithmHvPareto, RUNS) as CoevolutionGeneticAlgorithmState)
        val coevolutionGeneticAlgorithmNsgaIIState =
            (Runner.runCombining(coevolutionGeneticAlgorithmNsgaII, RUNS) as CoevolutionGeneticAlgorithmState)
        genericGeneticAlgorithmHvParetoState.save(genericHvName)
        genericGeneticAlgorithmNsgaIIState.save(genericNsgaName)
        coevolutionGeneticAlgorithmHvParetoState.save(coevolutionHvName)
        coevolutionGeneticAlgorithmNsgaIIState.save(coevolutionNsgaName)
    }

    @Suppress("UNCHECKED_CAST")
    override fun calculateAndPrintOutcomes(): List<List<SingularSimulationOutcome>> {
        val state = loadState()
        val geneticAlgorithmOutcomeHv = state[0] as GenericGeneticAlgorithmState
        val geneticAlgorithmOutcomeNsga = state[1] as GenericGeneticAlgorithmState
        val coevolutionGeneticAlgorithmOutcomeHv = state[2] as CoevolutionGeneticAlgorithmState
        val coevolutionGeneticAlgorithmOutcomeNsga = state[3] as CoevolutionGeneticAlgorithmState

        val outcomes = mutableListOf(
            geneticAlgorithmOutcomeHv.archive.toList(),
            geneticAlgorithmOutcomeNsga.archive.toList(),
            coevolutionGeneticAlgorithmOutcomeHv.archive.map {
                val output = SingularSimulationOutcome(it.profitsWithDefensiveGenome!!, it.riskWithDefensiveGenome!!)
                output
            }.toList(),
            coevolutionGeneticAlgorithmOutcomeNsga.archive.map {
                val output = SingularSimulationOutcome(it.profitsWithDefensiveGenome!!, it.riskWithDefensiveGenome!!)
                output
            }.toList(),
        )

        val purityValues = mutableListOf<Double>()
        val mainFront = outcomes[0] + outcomes[1] + outcomes[2] + outcomes[3]
        val evaluatedMainFront = paretoEvaluate(mainFront) as List<SingularSimulationOutcome>
        for (i in 0 until 4) {
            purityValues += calculateParetoPurity(outcomes[i], evaluatedMainFront)
        }
        this.purityValues = purityValues

        return outcomes
    }

    override fun drawChart(outcomes: List<List<SimulationOutcome>>) {
        FourTypesChartDrawer(purityValues.toTypedArray()).drawChart(outcomes)
    }

    override fun saveChart(runId: Int, outcomes: List<List<SimulationOutcome>>) {
        FourTypesChartDrawer(purityValues.toTypedArray()).saveChart("four_types_run_$runId", outcomes)
    }

    private fun loadState(): List<GeneticAlgorithmState> {
        var genericGeneticAlgorithmStateHv = GenericGeneticAlgorithmState.load(genericHvName)
        if (genericGeneticAlgorithmStateHv == null) {
            genericGeneticAlgorithmStateHv = genericGeneticAlgorithmHvPareto.getEmptyState()
        }
        var genericGeneticAlgorithmStateNsga = GenericGeneticAlgorithmState.load(genericNsgaName)
        if (genericGeneticAlgorithmStateNsga == null) {
            genericGeneticAlgorithmStateNsga = genericGeneticAlgorithmNsgaII.getEmptyState()
        }
        var coevolutionGeneticAlgorithmStateHv = CoevolutionGeneticAlgorithmState.load(coevolutionHvName)
        if (coevolutionGeneticAlgorithmStateHv == null) {
            coevolutionGeneticAlgorithmStateHv = coevolutionGeneticAlgorithmHvPareto.getEmptyState()
        }
        var coevolutionGeneticAlgorithmStateNsga = CoevolutionGeneticAlgorithmState.load(coevolutionNsgaName)
        if (coevolutionGeneticAlgorithmStateNsga == null) {
            coevolutionGeneticAlgorithmStateNsga = coevolutionGeneticAlgorithmNsgaII.getEmptyState()
        }
        return listOf(
            genericGeneticAlgorithmStateHv,
            genericGeneticAlgorithmStateNsga,
            coevolutionGeneticAlgorithmStateHv,
            coevolutionGeneticAlgorithmStateNsga
        )
    }

    companion object {
        const val logString = "Simulation for four types - "
    }
}
