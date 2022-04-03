package output

import model.Date
import simulation.SimulationOutcome
import org.jfree.chart.JFreeChart
import org.jfree.chart.plot.XYPlot
import org.jfree.data.xy.XYDataset
import org.jfree.data.xy.XYSeries
import org.jfree.data.xy.XYSeriesCollection
import org.jfree.util.ShapeUtilities
import java.awt.Color
import java.awt.Rectangle
import java.awt.geom.Ellipse2D

class CrossValidationChartDrawer(private val periods: List<List<Pair<Date, Date>>>) :
    ChartDrawer("Cross-validation results") {

    override fun createDataset(algorithmOutcomes: List<List<SimulationOutcome>>): XYDataset {
        val dataset = XYSeriesCollection()
        for (i in periods.indices) {
            val series = XYSeries("${periods[i][0].first}-${periods[i][periods[i].size - 1].second} GA")
            for (outcome in algorithmOutcomes[i * 3]) {
                series.add(outcome.risk, outcome.profits)
            }
            dataset.addSeries(series)

            val buyAndHoldSeries = XYSeries("B&H")
            buyAndHoldSeries.add(algorithmOutcomes[i * 3 + 1][0].risk,
                algorithmOutcomes[i * 3 + 1][0].profits)
            dataset.addSeries(buyAndHoldSeries)

            val activeManagement = XYSeries("AM")
            activeManagement.add(algorithmOutcomes[i * 3 + 2][0].risk,
                algorithmOutcomes[i * 3 + 2][0].profits)
            dataset.addSeries(activeManagement)
        }
        return dataset
    }

    override fun setSeriesRendering(chart: JFreeChart, plot: XYPlot) {
        val colors = listOf(Color.BLACK,
            Color.BLUE,
            Color.RED,
            Color.GREEN,
            Color.ORANGE,
            Color.MAGENTA,
            Color.YELLOW,
            Color.PINK,
            Color.CYAN,
            Color.GRAY)
        val shapes = listOf(
            Ellipse2D.Double(-3.0, -3.0, 6.0, 6.0),
            Rectangle(-3, -3, 6, 6),
            ShapeUtilities.createDiagonalCross(3.0f, 1.0f),
            Rectangle(-5, -2, 10, 4),
            Rectangle(-2, -5, 4, 10),
        )
        for (i in 0 until plot.getDataset(0).seriesCount step 3) {
            plot.getRendererForDataset(plot.getDataset(0)).setSeriesPaint(i, colors[i / 3])
            plot.getRenderer(0).setSeriesShape(i, shapes[0])
            plot.getRendererForDataset(plot.getDataset(0)).setSeriesPaint(i + 1, colors[i / 3])
            plot.getRenderer(0).setSeriesShape(i + 1, shapes[1])
            plot.getRendererForDataset(plot.getDataset(0)).setSeriesPaint(i + 2, colors[i / 3])
            plot.getRenderer(0).setSeriesShape(i + 2, shapes[2])
        }
    }
}
