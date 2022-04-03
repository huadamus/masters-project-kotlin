package simulation

import model.Genome
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable

class SingularSimulationOutcome(
    profits: Double,
    risk: Double,
) : SimulationOutcome(profits, risk), Serializable {
    lateinit var genome: Genome

    private fun writeObject(oos: ObjectOutputStream) {
        oos.writeObject(genome)
        oos.writeDouble(profits)
        oos.writeDouble(risk)
    }

    private fun readObject(ois: ObjectInputStream) {
        genome = ois.readObject() as Genome
        profits = ois.readDouble()
        risk = ois.readDouble()
    }

    override fun equals(other: Any?): Boolean {
        if (other !is SingularSimulationOutcome) {
            return false
        }
        return if(this::genome.isInitialized) {
            if(other::genome.isInitialized) {
                genome == other.genome && profits == other.profits && risk == other.risk
            } else {
                false
            }
        } else {
            if(!other::genome.isInitialized) {
                profits == other.profits && risk == other.risk
            } else {
                false
            }
        }
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        if(this::genome.isInitialized) {
            result = 31 * result + genome.hashCode()
        }
        result = 31 * result + profits.hashCode()
        result = 31 * result + risk.hashCode()
        return result
    }

    override fun clone(): SingularSimulationOutcome {
        val output = SingularSimulationOutcome(profits, risk)
        if(this::genome.isInitialized) {
            output.genome = genome.clone()
        }
        return output
    }
}
