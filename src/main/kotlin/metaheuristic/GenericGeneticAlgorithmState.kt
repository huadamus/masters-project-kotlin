package metaheuristic

import model.CombinedGenome
import simulation.SingularSimulationOutcome
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream


@Suppress("UNCHECKED_CAST")
data class GenericGeneticAlgorithmState(
    val population: List<CombinedGenome>,
    val archive: Set<SingularSimulationOutcome>,
) : GeneticAlgorithmState {

    fun save(experimentName: String) {
        println("Saving...")
        val fileOutputStream = FileOutputStream(PATH + experimentName + PATH_ADDENDUM)
        val oos = ObjectOutputStream(fileOutputStream)
        oos.writeObject(population)
        oos.writeObject(archive)
        oos.close()
    }

    override fun combineArchiveWith(geneticAlgorithmState: GeneticAlgorithmState): GenericGeneticAlgorithmState {
        val genericGeneticAlgorithmState = geneticAlgorithmState as GenericGeneticAlgorithmState
        val combinedArchive = paretoEvaluate(archive.toList() + genericGeneticAlgorithmState.archive)
        return GenericGeneticAlgorithmState(population, combinedArchive.toSet() as Set<SingularSimulationOutcome>)
    }

    companion object {
        const val PATH = "results/"
        private const val PATH_ADDENDUM = ".dat"

        fun load(experimentName: String): GenericGeneticAlgorithmState? {
            lateinit var output: GenericGeneticAlgorithmState
            try {
                val streamIn = FileInputStream(PATH + experimentName + PATH_ADDENDUM)
                val objectInputStream = ObjectInputStream(streamIn)
                val population = objectInputStream.readObject() as List<CombinedGenome>
                val archive = objectInputStream.readObject() as Set<SingularSimulationOutcome>
                output = GenericGeneticAlgorithmState(population, archive)
                objectInputStream.close()
            } catch (e: Exception) {
                println("Could not load Genetic Algorithm State ($experimentName)")
                return null
            }
            println("Successfully loaded Genetic Algorithm State ($experimentName)")
            return output
        }
    }
}
