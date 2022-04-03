package metaheuristic

import model.Genome
import model.OffensiveGenome
import java.io.*

data class CoevolutionGeneticAlgorithmState(
    val offensiveGenomesPopulation: List<OffensiveGenome>,
    val defensiveGenomesPopulation: List<Genome>,
    val archive: Set<OffensiveGenome>,
) : GeneticAlgorithmState, Serializable {

    fun save(experimentName: String) {
        println("Saving...")
        val fileOutputStream = FileOutputStream(PATH + experimentName + PATH_ADDENDUM)
        val oos = ObjectOutputStream(fileOutputStream)
        oos.writeObject(this)
    }

    override fun combineArchiveWith(geneticAlgorithmState: GeneticAlgorithmState): CoevolutionGeneticAlgorithmState {
        val coevolutionGeneticAlgorithmState = geneticAlgorithmState as CoevolutionGeneticAlgorithmState
        val combinedArchive =
            paretoEvaluateOffensiveGenomes(archive.toList() + coevolutionGeneticAlgorithmState.archive)
        return CoevolutionGeneticAlgorithmState(
            offensiveGenomesPopulation,
            defensiveGenomesPopulation,
            combinedArchive.toSet()
        )
    }

    companion object {
        private const val PATH = "results/"
        private const val PATH_ADDENDUM = "_coevolution.dat"

        fun load(experimentName: String): CoevolutionGeneticAlgorithmState? {
            lateinit var output: CoevolutionGeneticAlgorithmState
            try {
                val streamIn = FileInputStream(PATH + experimentName + PATH_ADDENDUM)
                val objectInputStream = ObjectInputStream(streamIn)
                output = objectInputStream.readObject() as CoevolutionGeneticAlgorithmState
                objectInputStream.close()
            } catch (e: Exception) {
                println("Could not load Coevolution Genetic Algorithm State ($experimentName)")
                return null
            }
            println("Successfully loaded Coevolution Genetic Algorithm State ($experimentName)")
            return output
        }
    }
}
