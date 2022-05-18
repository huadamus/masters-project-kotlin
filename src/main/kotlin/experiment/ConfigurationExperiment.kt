package experiment

import RUNS
import data.DataLoader
import metaheuristic.*
import model.Date
import output.ConfigurationChartDrawer
import simulation.Runner
import simulation.SimulationOutcome

class ConfigurationExperiment : Experiment("configuration") {

    private val genericHvName = "$name-generic-hv"
    private val genericNsgaName = "$name-generic-nsga2"
    private val genericSpeaName = "$name-generic-spea2"
    private val coevolutionHvName = "$name-coevolution-hv"
    private val coevolutionNsgaName = "$name-coevolution-nsga2"
    private val coevolutionSpeaName = "$name-coevolution-spea2"
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
    private val genericGeneticAlgorithmSpea2 = GenericGeneticAlgorithm(
        genericSpeaName,
        "${logString}No coevolution, SPEA2",
        listOf(Pair(Date(1, 1, 1988), Date(1, 1, 2018))),
        360,
        true,
        SelectionMethod.SPEA2,
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
    private val coevolutionGeneticAlgorithmSpea2 = CoevolutionGeneticAlgorithm(
        coevolutionNsgaName,
        "${logString}Coevolution, SPEA2",
        listOf(Pair(Date(1, 1, 1988), Date(1, 1, 2018))),
        360,
        true,
        SelectionMethod.SPEA2,
        DataLoader.loadDevelopedData(),
        DataLoader.loadEmergingData(),
        DataLoader.loadCrbAndOilData(),
        DataLoader.loadGoldUsdData(),
        DataLoader.loadShillerPESP500Ratio(),
        DataLoader.loadDowToGoldData()
    )
    private lateinit var purityValues: List<Double>

    override fun run() {
        val state = loadState()
        val genericGeneticAlgorithmHvParetoState =
            (Runner.runCombining(genericGeneticAlgorithmHvPareto, state[0], RUNS) as GenericGeneticAlgorithmState)
        val genericGeneticAlgorithmNsgaIIState =
            (Runner.runCombining(genericGeneticAlgorithmNsgaII, state[1], RUNS) as GenericGeneticAlgorithmState)
        val genericGeneticAlgorithmSpea2State =
            (Runner.runCombining(genericGeneticAlgorithmSpea2, state[2], RUNS) as GenericGeneticAlgorithmState)
        val coevolutionGeneticAlgorithmHvParetoState =
            (Runner.runCombining(
                coevolutionGeneticAlgorithmHvPareto, state[3], RUNS
            ) as CoevolutionGeneticAlgorithmState)
        val coevolutionGeneticAlgorithmNsgaIIState =
            (Runner.runCombining(coevolutionGeneticAlgorithmNsgaII, state[4], RUNS) as CoevolutionGeneticAlgorithmState)
        val coevolutionGeneticAlgorithmSpea2State =
            (Runner.runCombining(coevolutionGeneticAlgorithmSpea2, state[5], RUNS) as CoevolutionGeneticAlgorithmState)
        genericGeneticAlgorithmHvParetoState.save(genericHvName)
        genericGeneticAlgorithmNsgaIIState.save(genericNsgaName)
        genericGeneticAlgorithmSpea2State.save(genericSpeaName)
        coevolutionGeneticAlgorithmHvParetoState.save(coevolutionHvName)
        coevolutionGeneticAlgorithmNsgaIIState.save(coevolutionNsgaName)
        coevolutionGeneticAlgorithmSpea2State.save(coevolutionSpeaName)
    }

    @Suppress("UNCHECKED_CAST")
    override fun calculateAndPrintOutcomes(): List<List<SimulationOutcome>> {
        val state = loadState()
        val geneticAlgorithmOutcomeHv = state[0] as GenericGeneticAlgorithmState
        val geneticAlgorithmOutcomeNsga = state[1] as GenericGeneticAlgorithmState
        val geneticAlgorithmOutcomeSpea2 = state[2] as GenericGeneticAlgorithmState
        val coevolutionGeneticAlgorithmOutcomeHv = state[3] as CoevolutionGeneticAlgorithmState
        val coevolutionGeneticAlgorithmOutcomeNsga = state[4] as CoevolutionGeneticAlgorithmState
        val coevolutionGeneticAlgorithmOutcomeSpea2 = state[5] as CoevolutionGeneticAlgorithmState

        val outcomes = mutableListOf(
            geneticAlgorithmOutcomeHv.archive.toList(),
            geneticAlgorithmOutcomeNsga.archive.toList(),
            geneticAlgorithmOutcomeSpea2.archive.toList(),
            coevolutionGeneticAlgorithmOutcomeHv.archive.toList(),
            coevolutionGeneticAlgorithmOutcomeNsga.archive.toList(),
            coevolutionGeneticAlgorithmOutcomeSpea2.archive.toList()
        )

        val purityValues = mutableListOf<Double>()
        val mainFront = outcomes[0] + outcomes[1] + outcomes[2] + outcomes[3] + outcomes[4] + outcomes[5]
        val evaluatedMainFront = paretoEvaluateOffensiveGenomes(mainFront).map {
            SimulationOutcome(
                it.profitsWithDefensiveGenome!!,
                it.riskWithDefensiveGenome!!
            )
        }
        for (i in 0 until 6) {
            purityValues += calculateParetoPurity(outcomes[i].map {
                SimulationOutcome(
                    it.profitsWithDefensiveGenome!!,
                    it.riskWithDefensiveGenome!!
                )
            }, evaluatedMainFront)
        }
        this.purityValues = purityValues

        return outcomes.map {
            it.map { offensiveGenome ->
                SimulationOutcome(
                    offensiveGenome.profitsWithDefensiveGenome!!,
                    offensiveGenome.riskWithDefensiveGenome!!
                )
            }
        }
    }

    override fun drawChart(outcomes: List<List<SimulationOutcome>>) {
        ConfigurationChartDrawer(purityValues.toTypedArray()).drawChart(outcomes)
    }

    override fun saveChart(runId: Int, outcomes: List<List<SimulationOutcome>>) {
        ConfigurationChartDrawer(purityValues.toTypedArray()).saveChart("configuration_run_$runId", outcomes)
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
        var genericGeneticAlgorithmStateSpea = GenericGeneticAlgorithmState.load(genericSpeaName)
        if (genericGeneticAlgorithmStateSpea == null) {
            genericGeneticAlgorithmStateSpea = genericGeneticAlgorithmSpea2.getEmptyState()
        }
        var coevolutionGeneticAlgorithmStateHv = CoevolutionGeneticAlgorithmState.load(coevolutionHvName)
        if (coevolutionGeneticAlgorithmStateHv == null) {
            coevolutionGeneticAlgorithmStateHv = coevolutionGeneticAlgorithmHvPareto.getEmptyState()
        }
        var coevolutionGeneticAlgorithmStateNsga = CoevolutionGeneticAlgorithmState.load(coevolutionNsgaName)
        if (coevolutionGeneticAlgorithmStateNsga == null) {
            coevolutionGeneticAlgorithmStateNsga = coevolutionGeneticAlgorithmNsgaII.getEmptyState()
        }
        var coevolutionGeneticAlgorithmStateSpea = CoevolutionGeneticAlgorithmState.load(coevolutionSpeaName)
        if (coevolutionGeneticAlgorithmStateSpea == null) {
            coevolutionGeneticAlgorithmStateSpea = coevolutionGeneticAlgorithmSpea2.getEmptyState()
        }
        return listOf(
            genericGeneticAlgorithmStateHv,
            genericGeneticAlgorithmStateNsga,
            genericGeneticAlgorithmStateSpea,
            coevolutionGeneticAlgorithmStateHv,
            coevolutionGeneticAlgorithmStateNsga,
            coevolutionGeneticAlgorithmStateSpea,
        )
    }

    companion object {
        const val logString = "Simulation for configurations - "
    }
}
