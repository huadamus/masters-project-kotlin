package simulation

import model.Genome
import model.OffensiveGenome
import model.StrategyDetails

class CombinedSimulationOutcome(
    val offensiveGenome: OffensiveGenome,
    val defensiveGenome: Genome,
    profits: Double,
    risk: Double,
    val strategyDetails: List<StrategyDetails>,
) : SimulationOutcome(profits, risk) {

    override fun equals(other: Any?): Boolean {
        if (other !is CombinedSimulationOutcome) {
            return false
        }
        return offensiveGenome == other.offensiveGenome && defensiveGenome == other.defensiveGenome
                && profits == other.profits && risk == other.risk && strategyDetails == other.strategyDetails
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + offensiveGenome.hashCode()
        result = 31 * result + defensiveGenome.hashCode()
        result = 31 * result + profits.hashCode()
        result = 31 * result + risk.hashCode()
        result = 31 * result + strategyDetails.hashCode()
        return result
    }

    override fun clone(): CombinedSimulationOutcome {
        return CombinedSimulationOutcome(
            offensiveGenome.clone(),
            defensiveGenome.clone(),
            profits,
            risk,
            strategyDetails
        )
    }
}
