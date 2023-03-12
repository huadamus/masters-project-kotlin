package output

import model.Date
import model.OffensiveGenome
import model.StrategyDetails
import simulation.SimulationOutcome
import org.jfree.chart.JFreeChart
import org.jfree.chart.plot.XYPlot
import org.jfree.data.xy.XYDataset
import org.jfree.data.xy.XYSeries
import org.jfree.data.xy.XYSeriesCollection
import simulation.Simulator
import simulation.portfolio.Portfolio

class CrossValidationChartDrawer(private val periods: List<List<Pair<Date, Date>>>) :
    ChartDrawer("Cross-validation") {

    override fun createDataset(algorithmOutcomes: List<List<SimulationOutcome>>): XYDataset {
        val dataset = XYSeriesCollection()
        for (i in periods.indices) {
            val series = XYSeries("${periods[i][0].first}-${periods[i][periods[i].size - 1].second} AG")
            for (outcome in algorithmOutcomes[i * 3]) {
                series.add(outcome.risk, calculateProfits((outcome.genome as OffensiveGenome).strategyDetailsWithDefensiveGenome!![0]))
            }
            dataset.addSeries(series)

            val buyAndHoldSeries = XYSeries("B&H")
            buyAndHoldSeries.add(
                algorithmOutcomes[i * 3 + 1][0].risk,
                algorithmOutcomes[i * 3 + 1][0].profits
            )
            dataset.addSeries(buyAndHoldSeries)

            val activeManagement = XYSeries("AM")
            activeManagement.add(
                algorithmOutcomes[i * 3 + 2][0].risk,
                algorithmOutcomes[i * 3 + 2][0].profits
            )
            dataset.addSeries(activeManagement)
        }
        return dataset
    }

    override fun setSeriesRendering(chart: JFreeChart, plot: XYPlot) {
        for (i in 0 until plot.getDataset(0).seriesCount step 3) {
            plot.getRendererForDataset(plot.getDataset(0)).setSeriesPaint(i, seriesColors[i / 3])
            plot.getRenderer(0).setSeriesShape(i, shapes[0])
            plot.getRendererForDataset(plot.getDataset(0)).setSeriesPaint(i + 1, seriesColors[i / 3])
            plot.getRenderer(0).setSeriesShape(i + 1, shapes[3])
            plot.getRendererForDataset(plot.getDataset(0)).setSeriesPaint(i + 2, seriesColors[i / 3])
            plot.getRenderer(0).setSeriesShape(i + 2, shapes[2])
        }
    }

    fun calculateProfits(strategyDetails: StrategyDetails): Double {
        val beginningDate = strategyDetails.period.first
        val endDate = strategyDetails.period.second
        return 100 *
                ((strategyDetails.cashStatus.last().second * (Simulator.getInflation(
                    endDate,
                    beginningDate
                )) / Portfolio.INITIAL_CASH) - 1.0) /
                (beginningDate.getMonthsBetween(endDate) / 12)
    }
}
