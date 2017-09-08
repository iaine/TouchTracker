package uk.ac.ox.oerc.glam.touchtracker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by iain.emsley on 08/09/2017.
 */

public class TouchTrackView extends View {
    private static final int SIZE = 60;

    private SparseArray<PointF> mActivePointers;
    private Paint mPaint;
    private int[] colors = { Color.BLUE, Color.GREEN, Color.MAGENTA,
            Color.BLACK, Color.CYAN, Color.GRAY, Color.RED, Color.DKGRAY,
            Color.LTGRAY, Color.YELLOW };

    private Paint textPaint;

    public TouchTrackView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        mActivePointers = new SparseArray<PointF>();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        // set painter color to a color you like
        mPaint.setColor(Color.BLUE);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(20);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        try {
            // get pointer index from the event object
            int pointerIndex = event.getActionIndex();

            // get pointer ID
            int pointerId = event.getPointerId(pointerIndex);

            // get masked (not specific to a pointer) action
            int maskedAction = event.getActionMasked();

            switch (maskedAction) {

                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_POINTER_DOWN: {
                    // We have a new pointer. Lets add it to the list of pointers
                    PointF f = new PointF();
                    f.x = this.getXPosition(event, pointerId);
                    f.y = this.getYPosition(event, pointerId);
                    mActivePointers.put(pointerId, f);
                    writeData(event, pointerId);
                    break;
                }
                case MotionEvent.ACTION_MOVE: { // a pointer was moved
                    for (int size = event.getPointerCount(), i = 0; i < size; i++) {
                        PointF point = mActivePointers.get(event.getPointerId(i));
                        if (point != null) {
                            point.x = this.getXPosition(event, pointerId);
                            point.y = this.getYPosition(event, pointerId);
                            writeData(event, pointerId);
                        }
                    }
                    break;
                }
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_POINTER_UP:
                case MotionEvent.ACTION_CANCEL: {
                    mActivePointers.remove(pointerId);
                    break;
                }
            }
        } catch (Exception e) {
            Log.d("ERR", e.getStackTrace().toString());
        }
        invalidate();

        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // draw all pointers
        for (int size = mActivePointers.size(), i = 0; i < size; i++) {
            PointF point = mActivePointers.valueAt(i);
            if (point != null)
                mPaint.setColor(colors[i % 9]);
            canvas.drawCircle(point.x, point.y, SIZE, mPaint);
        }
        canvas.drawText("Total pointers: " + mActivePointers.size(), 10, 40 , textPaint);
    }

    public void writeData(MotionEvent event, int pointer) {
        try {
            String data = this.buildParams(this.getTime(event),this.getXPosition(event, pointer),
                    this.getYPosition(event, pointer), this.getPressure(event, pointer), pointer);
            new FileConnection().execute(data);
        }  catch (Exception e ) {
            System.out.print(e);
        }
    }

    public void dataSend(MotionEvent event, int pointer) {

        StringBuilder result = new StringBuilder();
        DataConnection dc = new DataConnection();
        try {
            String data = this.buildParams(this.getTime(event),this.getXPosition(event, pointer),
                    this.getYPosition(event, pointer), this.getPressure(event, pointer), pointer);
            dc.sendData(data);

        }  catch (Exception e ) {
            System.out.print(e);
        }

    }

    public long getTime(MotionEvent event) {
        Long x = event.getEventTime();
        return x;
    }

    public Float getXPosition(MotionEvent event, int pointer) {
        Float x = event.getX(pointer);
        return x;
    }

    public Float getYPosition(MotionEvent event, int pointer) {
        Float y  = event.getY(pointer);
        return y;
    }

    public Float getPressure(MotionEvent event, int pointer) {
        Float pressure = event.getPressure(pointer);
        return pressure;
    }

    private String buildParams(Long t, Float x, Float y, Float pressure, int pointer) {
        StringBuilder result = new StringBuilder();
        try {
            result.append(URLEncoder.encode("time", "UTF-8"));
            result.append("=");
            result.append(t.toString());
            result.append("&");
            result.append(URLEncoder.encode("x", "UTF-8"));
            result.append("=");
            result.append(x.toString());
            result.append("&");
            result.append(URLEncoder.encode("y", "UTF-8"));
            result.append("=");
            result.append(y.toString());
            result.append("&");
            result.append(URLEncoder.encode("pressure", "UTF-8"));
            result.append("=");
            result.append(pressure.toString());
            result.append(URLEncoder.encode("pointer", "UTF-8"));
            result.append("=");
            result.append(Integer.toString(pointer));
            result.append("\n");
        } catch (UnsupportedEncodingException ue) {
            Log.d("ENCODE", ue.getMessage());
        }
        return result.toString();
    }
}
