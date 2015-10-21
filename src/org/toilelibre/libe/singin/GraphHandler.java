package org.toilelibre.libe.singin;

import android.os.Handler;
import android.os.Message;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.Series;

class GraphHandler extends Handler { // Handler of incoming messages from clients.
    private final GraphView graphView;

    public GraphHandler (final GraphView graphView) {
        this.graphView = graphView;
    }

    @Override
    public void handleMessage (final Message msg) {
        this.graphView.getViewport ().setMinX (0);
        this.graphView.getViewport ().setMaxX ( ((Series<?>) msg.obj).getHighestValueX ());
        this.graphView.addSeries ((Series<?>) msg.obj);
    }
}