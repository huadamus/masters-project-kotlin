package output

import org.jfree.chart.JFreeChart
import org.jfree.chart.plot.XYPlot
import org.jfree.data.xy.XYDataset
import org.jfree.data.xy.XYSeries
import org.jfree.data.xy.XYSeriesCollection
import simulation.SimulationOutcome

class CrossValidation18to21ChartDrawer : ChartDrawer("Znalezione strategie w okresie walidacyjnym") {

    override fun createDataset(algorithmOutcomes: List<List<SimulationOutcome>>): XYDataset {
        val dataset = XYSeriesCollection()

        val buyAndHoldSeries = XYSeries("B&H")
        buyAndHoldSeries.add(algorithmOutcomes[0][0].risk, algorithmOutcomes[0][0].profits)
        dataset.addSeries(buyAndHoldSeries)

        val activeManagement = XYSeries("AM")
        activeManagement.add(algorithmOutcomes[1][0].risk, algorithmOutcomes[1][0].profits)
        dataset.addSeries(activeManagement)

        val iterations = listOf("Testowane w 1988-1994", "Testowane w 1994-2000", "Testowane w 2000-2006",
            "Testowane w 2006-2012", "Testowane w 2012-2018")

        for (i in 2 until algorithmOutcomes.size) {
            val series = XYSeries(iterations[i - 2])
            for (outcome in algorithmOutcomes[i]) {
                series.add(outcome.risk, outcome.profits)
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
}
