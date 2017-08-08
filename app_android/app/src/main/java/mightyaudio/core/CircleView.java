package mightyaudio.core;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import mighty.audio.R;

import static mightyaudio.activity.MusicActivity.displayMetrics;
import static mightyaudio.fragment.YourMusicFragment.memory_level;


public class CircleView extends View{
    private String TAG = CircleView.class.getSimpleName();
    public static final int MIN_VALUE = 50;
    public static final int MAX_VALUE = 285;
    private float SINE_PAD_VALUE  ;
    private Path pink_segement=new Path();
    private Path segment = new Path();
    private final int default_max = 100;
    private int finished_color;
    public float level;
    public float top;
    public float left;
    public float right;
    public float bottom;
    public Paint paint = new Paint();
    private Paint stroke_paint = new Paint();
    int color;
    public float value;
    private float stroke_width;
    private Path path=new Path();
    private Bitmap image;
    private Paint pink_paint=new Paint();
    private float OFFSET_VALUE;
  //  GlobalClass globalClass = GlobalClass.getInstance();
   // public float memory_level = (float) globalClass.memory.getStorageFullPercent();
    //public float memory_level;



    private void initbyAttributes(TypedArray attributes) {
        finished_color = attributes.getColor(R.styleable.CircleProgress_finishedColor, Color.parseColor("#00C2F3"));
        stroke_width = attributes.getFloat(R.styleable.CircleProgress_strokewidth, 8f);
    }

    private void adjustValue(int value) {
        this.value = Math.min(MAX_VALUE, Math.max(MIN_VALUE, value));
    }






    public CircleView(Context context) {
        this(context, null);
    }

    public CircleView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CircleProgress, 0, 0);
        initbyAttributes(attributes);
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        paint.setAntiAlias(true);
    }

    public void setValue(float value) {
        this.value = value;
        //invalidate();
    }

    public void setLevel(float level) {
        this.level = level;
        //invalidate();
    }
    public void setColor(int color){
        this.color=color;
    }

    public void setPath(){
        float multiplier=0;
        invalidate();
        switch(displayMetrics.densityDpi){
            case DisplayMetrics.DENSITY_LOW:
                System.out.println("case low selected");
                break;
            case DisplayMetrics.DENSITY_HIGH:

                if ((memory_level>90)||(memory_level<10)){
                    multiplier=0.545f;
                    OFFSET_VALUE=19f;
                    SINE_PAD_VALUE=10;
                }else{
                    multiplier=0.80f;
                    SINE_PAD_VALUE=12;
                }
                break;
            case DisplayMetrics.DENSITY_MEDIUM:
             //   System.out.println("case selected is medium");
                break;
            case DisplayMetrics.DENSITY_XHIGH:

                if ((memory_level>90)||(memory_level<10)){
                    multiplier=0.805f;
                    SINE_PAD_VALUE=13f;
                    OFFSET_VALUE=20.5f;
                }else{
                    multiplier=1.22f;
                    SINE_PAD_VALUE=20;
                    OFFSET_VALUE=-2;
                }
              //  Log.e(TAG,"case selected is X_high ");
                break;
            case DisplayMetrics.DENSITY_XXHIGH:
                multiplier=1.85f;
                OFFSET_VALUE=3;
                SINE_PAD_VALUE=24;
                if ((memory_level>90)||(memory_level<10)){
                    multiplier=1.12f;
                    SINE_PAD_VALUE=18f;
                    OFFSET_VALUE=34.5f;
                }
            //    Log.e(TAG,"case selected is XX_high ");
              //  System.out.println("case selected is XXhigh");
                break;

            default:

                if ((memory_level>90)||(memory_level<10)){
                    multiplier=1.08f;
                    OFFSET_VALUE=38f;
                    SINE_PAD_VALUE=20;
                }else{
                    multiplier=1.70f;
                    SINE_PAD_VALUE=24;
                    OFFSET_VALUE=-2;
                }
                break;
        }
        if (memory_level != 0.0 ) {
            path.moveTo(0, 0);

            path.quadTo(multiplier * 15, multiplier * (-30), multiplier * 30, multiplier * -15);
            path.quadTo(multiplier * 45, multiplier * 0, multiplier * 60, multiplier * -15);
            path.quadTo(multiplier * 75, multiplier * (-30), multiplier * 90, multiplier * -15);
            path.quadTo(multiplier * 105, multiplier * 0, multiplier * 120, multiplier * -15);
            path.quadTo(multiplier * 135, multiplier * -30, multiplier * 150, multiplier * -0);
            path.close();
        }else{
            path.reset();
            segment.reset();
//            invalidate();
        }
    }



    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);
        segment.reset();
        path.reset();
        setPath();
        //Log.e(TAG,"memory_blue "+memory_level);
        //invalidate();
     //   System.out.println("========entering On draw of stroage color=============");
        //canvas.drawColor(Color.WHITE);
        segment.addRect(this.left, this.top+SINE_PAD_VALUE, this.right, this.bottom, Path.Direction.CCW);
        //canvas.translate(this.left, value-level);
        canvas.drawPath(segment, paint);
        canvas.translate(this.left+OFFSET_VALUE, this.top+SINE_PAD_VALUE);
        canvas.drawPath(path, paint);
    }
}
