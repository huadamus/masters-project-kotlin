package output

import model.OffensiveGenome
import model.StrategyDetails
import org.jfree.chart.JFreeChart
import org.jfree.chart.plot.XYPlot
import org.jfree.data.xy.XYDataset
import org.jfree.data.xy.XYSeries
import org.jfree.data.xy.XYSeriesCollection
import simulation.SimulationOutcome
import simulation.Simulator
import simulation.portfolio.Portfolio

class CrossValidation18to21ChartDrawer : ChartDrawer("Validation") {

    override fun createDataset(algorithmOutcomes: List<List<SimulationOutcome>>): XYDataset {
        val dataset = XYSeriesCollection()

        val buyAndHoldSeries = XYSeries("B&H")
        buyAndHoldSeries.add(algorithmOutcomes[0][0].risk, algorithmOutcomes[0][0].profits)
        dataset.addSeries(buyAndHoldSeries)

        val activeManagement = XYSeries("AM")
        activeManagement.add(algorithmOutcomes[1][0].risk, algorithmOutcomes[1][0].profits)
        dataset.addSeries(activeManagement)

        val iterations = listOf("Tested in 1988-1994", "Tested in 1994-2000", "Tested in 2000-2006",
            "Tested in 2006-2012", "Tested in 2012-2018")

        for (i in 2 until algorithmOutcomes.size) {
            val series = XYSeries(iterations[i - 2])
            for (outcome in algorithmOutcomes[i]) {
                series.add(outcome.risk, calculateProfits((outcome.genome as OffensiveGenome).strategyDetailsWithDefensiveGenome!![0]))
            }
            dataset.addSeries(series)
        }
        return dataset
    }

    override fun setSeriesRendering(chart: JFreeChart, plot: XYPlot) {
        plot.getRendererForDataset(plot.getDataset(0)).setSeriesPaint(0, classicalStrategiesColor)
        plot.getRenderer(0).setSeriesShape(0, shapes[2])
        //(plot.getRenderer(0) as XYLineAndShapeRenderer).setSeriesLinesVisible(2, false)
        //plot.getRenderer(0).setSeriesVisibleInLegend(seriesIndex, Boolean.FALSE, true)
        plot.getRendererForDataset(plot.getDataset(0)).setSeriesPaint(1, classicalStrategiesColor)
        plot.getRenderer(0).setSeriesShape(1, shapes[3])
        for (i in 2 until plot.getDataset(0).seriesCount) {
            plot.getRendererForDataset(plot.getDataset(0)).setSeriesPaint(i, seriesColors[i - 2])
            plot.getRenderer(0).setSeriesShape(i, shapes[0])
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
