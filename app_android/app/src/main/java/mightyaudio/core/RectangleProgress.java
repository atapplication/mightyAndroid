package mightyaudio.core;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import mighty.audio.R;

import static mightyaudio.activity.MusicActivity.displayMetrics;


public class  RectangleProgress extends View {
    private String TAG = RectangleProgress.class.getSimpleName();
    public static final int MIN_VALUE = 50;
    public static final int MAX_VALUE = 285;
    public float right;
    public float bottom;
    public float left;
    private Path segment = new Path();
    private Paint strokePaint = new Paint();
    private Paint fillPaint = new Paint();
    private int fillColor;
    private int strokeColor;
    private float strokeWidth;
    public float value;
    public float top;
    public Path path = new Path();
    public boolean fill_image = false;
    public float level;
    public float OFFSET_VALUE=3.5f;
    private float SINE_PAD_VALUE;
//    Fragment inside;

    public  RectangleProgress(Context context) {
        this(context, null);
    }

    public  RectangleProgress(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.RectangleProgress,
                0, 0);

        try {   /*declared styleable resources in the attrs.xml*/
            fillColor = a.getColor(R.styleable.RectangleProgress_fillColor, Color.parseColor("#7ED321"));
            strokeColor = a.getColor(R.styleable.RectangleProgress_strokeColor, Color.BLACK);
            strokeWidth = a.getFloat(R.styleable.RectangleProgress_strokeWidth, 1f);
            value = a.getInteger(R.styleable.RectangleProgress_value, 0);
            adjustValue(value);
        } finally {
            a.recycle();
        }
//        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View inflate = inflater.inflate(R.layout.fragment_one, this, true);
        segment.reset();
        fillPaint.setColor(fillColor);
        strokePaint.setColor(strokeColor);
        strokePaint.setStrokeWidth(strokeWidth);
        fillPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        fillPaint.setAntiAlias(true);
        strokePaint.setStyle(Paint.Style.STROKE);
    }

    public void setValue(float value) {   /*The function is invoked in the main activity to set the level of that battery*/
        this.value = value;
        invalidate();
    }

    private void adjustValue(float value) {/*this function makes sure the battery level never crosses 100%*/
        this.value = Math.min(MAX_VALUE, Math.max(MIN_VALUE, value));
    }

    public void setPaths() {
        //reset_path();
        invalidate();
        float multiplier=0;
        Log.e(TAG,"density__" + displayMetrics.densityDpi);
        switch (displayMetrics.densityDpi){
            case DisplayMetrics.DENSITY_LOW:
                System.out.println("case low selected");
                break;

            case DisplayMetrics.DENSITY_MEDIUM:
                break;

            case DisplayMetrics.DENSITY_HIGH:
                multiplier = 0.50f;
                SINE_PAD_VALUE=14;
   //             System.out.println("case selected is high");
                break;

            case DisplayMetrics.DENSITY_XHIGH:
                multiplier=0.82f;
                SINE_PAD_VALUE=18;
                OFFSET_VALUE=0;
                break;
            case DisplayMetrics.DENSITY_XXHIGH:
    //            System.out.println("case selected is xxhigh");
                multiplier=1.17f;
                SINE_PAD_VALUE=25;
                break;
            case DisplayMetrics.DENSITY_XXXHIGH:
   //             Log.e(TAG,"case selected is XX_high ");
                multiplier=1.17f;
                SINE_PAD_VALUE=25;
                break;
            default:
                multiplier = 1.70f;
                SINE_PAD_VALUE = 30;
                break;
        }
        //top = value - level;
        path.moveTo(0, 0);

        path.quadTo(multiplier * 15, multiplier * (-30), multiplier * 30, multiplier * -15);
        path.quadTo(multiplier * 45, multiplier * 0, multiplier * 60, multiplier * -15);
        path.quadTo(multiplier * 75, multiplier * (-30), multiplier * 90, multiplier * -15);
        path.quadTo(multiplier * 105, multiplier * 0, multiplier * 120, multiplier * -15);
        path.quadTo(multiplier * 135, multiplier * -30, multiplier * 150, multiplier * -0);
        path.close();
        //segment.addRect(this.left, top=value - level, this.right, this.bottom, Path.Direction.CCW);
        System.out.println("Set paths called");
        System.out.println("left :" + left + " top :" + (value - level) + " right :" + right + " bottom :" + bottom);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        segment.reset();
        super.onDraw(canvas);
        segment.addRect(this.left,this.value-level+SINE_PAD_VALUE-2,this.right,this.bottom,Path.Direction.CCW);
        canvas.drawPath(segment, fillPaint);
        canvas.translate(this.left+OFFSET_VALUE, value - level+SINE_PAD_VALUE);
        canvas.drawPath(path, fillPaint);


    }
}
