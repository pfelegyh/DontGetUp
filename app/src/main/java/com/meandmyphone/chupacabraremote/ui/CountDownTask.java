package com.meandmyphone.chupacabraremote.ui;

import android.graphics.Color;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.meandmyphone.shared.InterpolationData;
import com.meandmyphone.util.Ease;
import com.meandmyphone.util.Interpolation;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Updates the underlying View with the time left until countdown is finished.
 */
public class CountDownTask implements Runnable {

    private final AtomicBoolean stopped = new AtomicBoolean(false);
    private TextView shutdownCounter;
    private LineChart lineChart;
    private final InterpolationData interpolationData;
    private final long startTime;
    private final long waitUntil;
    private final long duration;
    private final long secondsToWait;

    public CountDownTask(InterpolationData interpolationData, long startTime, long waitUntil) {
        this.interpolationData = interpolationData;
        this.startTime = startTime;
        this.waitUntil = waitUntil;
        this.duration = waitUntil - startTime;
        this.secondsToWait = (waitUntil - startTime) / 1000;
    }

    public void registerLinechart(LineChart lineChart) {
        this.lineChart = lineChart;
    }

    public void registerShutdownCounter(TextView shutdownCounter) {
        this.shutdownCounter = shutdownCounter;
    }

    @Override
    public void run() {

        long now = System.currentTimeMillis();
        if (!stopped.get() && now < waitUntil) {

            final long elapsedTime = System.currentTimeMillis() - startTime;

            updateChartHighlight(elapsedTime);
            updateCountdownText(elapsedTime);
        }
    }

    private void updateCountdownText(long elapsedTime) {
        if (shutdownCounter != null) {
            shutdownCounter.post(() -> {

                long hoursLeft = (secondsToWait - elapsedTime / 1000) / 60 / 60;
                long minutesLeft = (secondsToWait - elapsedTime / 1000) / 60 - hoursLeft * 60;
                long secondsLeft = (secondsToWait - elapsedTime / 1000) % 60;

                if (secondsToWait < 3600) {
                    shutdownCounter.setText(String.format(Locale.ROOT, "Time left until shutdown:\n%02d:%02d",
                            minutesLeft,
                            secondsLeft));
                } else {
                    shutdownCounter.setText(String.format(Locale.ROOT, "Time left until shutdown:\n%02d:%02d:%02d",
                            hoursLeft,
                            minutesLeft,
                            secondsLeft));
                }
            });
        }
    }

    private void updateChartHighlight(long elapsedTime) {
        if (lineChart != null) {
            if (interpolationData != null && elapsedTime > 0) {
                float highlightValue = 20.0f * (float) elapsedTime / (float) duration;
                if (lineChart.getData() == null) {
                    int minutesToWait = (int) duration / 1000 / 60;
                    float step = minutesToWait / 20.0f;
                    lineChart.post(() -> populateLineChart(
                            minutesToWait,
                            step,
                            interpolationData.getStartValue(),
                            interpolationData.getEndValue(),
                            Interpolation.forValue(interpolationData.getInterpolationType())
                    ));
                }
                lineChart.post(() -> lineChart.highlightValue(highlightValue, 0, false));
            }
        }
    }

    public synchronized void stop() {
        stopped.set(true);

    }

    private void populateLineChart(int minutesToWait, float step, int startValue, int endValue, Interpolation interpolation) {
        List<Entry> yVals = new ArrayList<>();

        for (float i = 0.0f; i <= minutesToWait; i += step) {
            float value = Ease.calculateFloat(interpolation.getIntegerValue(), i, startValue, (float) endValue - startValue, minutesToWait);
            int index = yVals.size();
            yVals.add(new Entry(index, value));
        }

        LineDataSet selectedInterpolationChartDataSet = new LineDataSet(yVals, "Volume");
        selectedInterpolationChartDataSet.setDrawCircles(false);
        selectedInterpolationChartDataSet.setLabel("Volume");
        LineData data = new LineData(selectedInterpolationChartDataSet);
        data.setDrawValues(false);
        lineChart.setData(data);
        lineChart.getDescription().setText("");

        lineChart.getLegend().setEnabled(false);
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.getXAxis().setValueFormatter((value, axis) -> {
            DateTime val = DateTime.now().plusMinutes((int) (value * step));
            return val.toString("hh:mm", Locale.ROOT);
        });
        lineChart.getAxisRight().setEnabled(false);
        lineChart.getAxisLeft().setValueFormatter((value, axis) -> {
            float percentage = 100 * (value / 65535);
            return String.format(Locale.ROOT, "%.0f%%", percentage);
        });

        lineChart.setHighlightPerDragEnabled(false);
        lineChart.setHighlightPerTapEnabled(false);

        selectedInterpolationChartDataSet.setHighLightColor(Color.RED);

    }

}
