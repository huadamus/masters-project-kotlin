package output

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

class CrossValidation18to21ChartDrawer : ChartDrawer("Cross-validation results in the '18-'21 period") {

    override fun createDataset(algorithmOutcomes: List<List<SimulationOutcome>>): XYDataset {
        val dataset = XYSeriesCollection()

        val buyAndHoldSeries = XYSeries("B&H")
        buyAndHoldSeries.add(algorithmOutcomes[0][0].risk, algorithmOutcomes[0][0].profits)
        dataset.addSeries(buyAndHoldSeries)

        val activeManagement = XYSeries("AM")
        activeManagement.add(algorithmOutcomes[1][0].risk, algorithmOutcomes[1][0].profits)
        dataset.addSeries(activeManagement)

        val iterations = listOf("Trained on 1988-1994", "Trained on 1994-2000", "Trained on 2000-2006",
            "Trained on 2006-2012", "Trained on 2012-2018")

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
            ShapeUtilities.createDiagonalCross(5.0f, 1.0f),
        )
        plot.getRendererForDataset(plot.getDataset(0)).setSeriesPaint(0, Color.MAGENTA)
        plot.getRenderer(0).setSeriesShape(0, shapes[0])
        plot.getRendererForDataset(plot.getDataset(0)).setSeriesPaint(1, Color.MAGENTA)
        plot.getRenderer(0).setSeriesShape(1, shapes[1])
        for (i in 2 until plot.getDataset(0).seriesCount) {
            plot.getRendererForDataset(plot.getDataset(0)).setSeriesPaint(i, colors[i - 2])
            plot.getRenderer(0).setSeriesShape(i, shapes[0])
        }
    }
}
