package org.toilelibre.libe.singin;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.PointsGraphSeries;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SoundFragment extends Fragment {

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate (R.layout.channels, container, false);
        GraphView graph = (GraphView) rootView.findViewById (R.id.graph1);
        PointsGraphSeries<DataPoint> series = new PointsGraphSeries<DataPoint> (new DataPoint [] { new DataPoint (0, -2), new DataPoint (1, 5), new DataPoint (2, 3), new DataPoint (3, 2),
                new DataPoint (4, 6) });
        graph.addSeries (series);
        series.setShape (PointsGraphSeries.Shape.POINT);
        PointsGraphSeries<DataPoint> series2 = new PointsGraphSeries<DataPoint> (new DataPoint [] { new DataPoint (0, -1), new DataPoint (1, 4), new DataPoint (2, 2), new DataPoint (3, 1),
                new DataPoint (4, 5) });
        graph.addSeries (series2);
        series2.setShape (PointsGraphSeries.Shape.RECTANGLE);
        series2.setColor (Color.RED);
        PointsGraphSeries<DataPoint> series3 = new PointsGraphSeries<DataPoint> (new DataPoint [] { new DataPoint (0, 0), new DataPoint (1, 3), new DataPoint (2, 1), new DataPoint (3, 0),
                new DataPoint (4, 4) });
        graph.addSeries (series3);
        series3.setShape (PointsGraphSeries.Shape.TRIANGLE);
        series3.setColor (Color.YELLOW);
        PointsGraphSeries<DataPoint> series4 = new PointsGraphSeries<DataPoint> (new DataPoint [] { new DataPoint (0, 1), new DataPoint (1, 2), new DataPoint (2, 0), new DataPoint (3, -1),
                new DataPoint (4, 3) });
        graph.addSeries (series4);
        series4.setColor (Color.GREEN);
        series4.setCustomShape (new PointsGraphSeries.CustomShape () {
            @Override
            public void draw (Canvas canvas, Paint paint, float x, float y) {
                paint.setStrokeWidth (10);
                canvas.drawLine (x - 20, y - 20, x + 20, y + 20, paint);
                canvas.drawLine (x + 20, y - 20, x - 20, y + 20, paint);
            }
        });
        return rootView;
    }

    @Override
    public void onAttach (Activity activity) {
        super.onAttach (activity);
    }

}
