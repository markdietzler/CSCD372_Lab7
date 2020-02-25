package mark.dietzler.mdietzlerlab7;

import android.animation.TimeAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.graphics.Canvas;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Observable;

public class Clock extends View implements TimeAnimator.TimeListener {

    final static float overallWidth = 100.0f, overallHeight = 100.0f, clockRadius = 50.0f, handLength = 40.0f, tailLength = 10.0f;
    final static float ASPECT_RATIO = 1f;
    TimeAnimator mTimer = new TimeAnimator();
    final static float[] minuteHand = new float[]{-02.5f, 0, 0, 40.25f, 02.5f, 0, 0, -02.5f, -02.5f, 0};
    final static float[] hourHand = new float[]{-05f, 0, 0, 30.5f, 05f, 0, 0, -05f, -05f, 0};
    int mWidth, mHeight;
    Boolean hourFormat = Boolean.FALSE, partialSeconds = Boolean.FALSE ;
    private String clockFace = "";
    private static final float mSECOND_DEGREES = 360/-60;

    public HourMinSec hourMinSec = new HourMinSec();

    public String GetClockFace() {
        return clockFace;
    }

    public void SetClockFace(String newClockFace) {
        clockFace = newClockFace;
    }

    public Clock(Context context) {
        super(context);
        initializeMe();
    }

    public Clock(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeMe();
    }

    public Clock(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeMe();
    }

    private void initializeMe(){
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        startTimer();
    }

    class HourMinSec extends Observable{
        int mHour, mMin, mSec;

        public int getHour() {
            return mHour;
        }

        public int getMin() {
            return mMin;
        }
        public int getSec() {
            return mSec;
        }

        public void setHMS(int h, int m, int s){
            mHour = h;
            mMin = m;
            mSec = s;
            setChanged();
            notifyObservers("Clock");
        }
    }

    @Override
    public void onDraw(Canvas canvas){

        //pick a clock face back ground image
        if(GetClockFace().equalsIgnoreCase("regular numerals")) {
            Resources resources = getResources();
            Bitmap modernBitmap = BitmapFactory.decodeResource(resources, R.drawable.modern);
            Rect dst = new Rect(0,0,mWidth,mHeight);
            Rect src = new Rect(0,0, modernBitmap.getWidth(), modernBitmap.getHeight());
            canvas.drawBitmap(modernBitmap,null, dst, null);
        }
        else if(GetClockFace().equalsIgnoreCase("roman numerals")){
            Resources resources = getResources();
            Bitmap romanBitmap = BitmapFactory.decodeResource(resources, R.drawable.roman);
            Rect dst = new Rect(0,0,mWidth,mHeight);
            Rect src = new Rect(0,0,romanBitmap.getWidth(), romanBitmap.getHeight());
            canvas.drawBitmap(romanBitmap,null, dst, null);
        }
        else if(GetClockFace().equalsIgnoreCase("crazy face")){
            Resources resources = getResources();
            Bitmap crazyBitmap = BitmapFactory.decodeResource(resources, R.drawable.crazy);
            //Rect dst = new Rect((int)(-overallWidth/2),(int)(overallHeight/2), (int)(overallWidth/2), (int)(overallHeight/2));
            Rect dst = new Rect(0,0, mWidth, mHeight);
            Rect src = new Rect(0,0,crazyBitmap.getWidth(),crazyBitmap.getHeight());
            canvas.drawBitmap(crazyBitmap,null, dst, null);
        }
        else if(GetClockFace().equalsIgnoreCase("math face")){
            Resources resources = getResources();
            Bitmap mathBitmap = BitmapFactory.decodeResource(resources, R.drawable.math);
            Rect dst = new Rect(0,0,mWidth,mHeight);
            Rect src = new Rect(0,0, mathBitmap.getWidth(), mathBitmap.getHeight());
            canvas.drawBitmap(mathBitmap,null,dst,null);
        }


        float width = getWidth();
        float height = getHeight();
        float scaleX = (width/overallWidth);
        float scaleY = (height/overallHeight);
        canvas.save();

        canvas.scale(scaleX , -scaleY);
        canvas.translate(overallWidth/2.0f, -overallHeight/2.0f);
        Paint paint = new Paint();

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(1f);
        paint.setColor(Color.RED);


        canvas.save();
        GregorianCalendar gregorianCalendar = new GregorianCalendar();

        int hour = gregorianCalendar.get(Calendar.HOUR);
        int minute = gregorianCalendar.get(Calendar.MINUTE);
        int second = gregorianCalendar.get(Calendar.SECOND);
        int millisecond = gregorianCalendar.get(Calendar.MILLISECOND);

        float hourTick = 30/60 * minute;

        //draw hour hand
        canvas.save();
        paint.setColor(Color.RED);
        Path hourPath = new Path();
        hourPath.moveTo(hourHand[0], hourHand[1]);
        hourPath.lineTo(hourHand[2], hourHand[3]);
        hourPath.lineTo(hourHand[4], hourHand[5]);
        hourPath.lineTo(hourHand[6], hourHand[7]);
        hourPath.lineTo(hourHand[8], hourHand[9]);
        hourPath.close();
        canvas.rotate((mSECOND_DEGREES * 5) * hour + hourTick);
        canvas.drawPath(hourPath, paint);
        canvas.restore();

        //draw minute hand
        canvas.save();
        paint.setColor(Color.BLACK);
        Path minutePath = new Path();
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        minutePath.moveTo(minuteHand[0], minuteHand[1]);
        minutePath.lineTo(minuteHand[2],minuteHand[3]);
        minutePath.lineTo(minuteHand[4], minuteHand[5]);
        minutePath.lineTo(minuteHand[6], minuteHand[7]);
        minutePath.lineTo(minuteHand[8], minuteHand[9]);
        minutePath.close();
        canvas.rotate(mSECOND_DEGREES * minute);
        canvas.drawPath(minutePath, paint);
        canvas.restore();

        //draw second hand
        paint.setColor((Color.RED));
        double scaledMillisecond = (second + (millisecond/1000.0));

        if(partialSeconds){
            canvas.rotate((float)scaledMillisecond * mSECOND_DEGREES);
        }
        else {
            canvas.rotate(second * mSECOND_DEGREES);
        }
        canvas.drawLine(0, 0,0,handLength,paint);
        canvas.restore();

        //draw big circle ticks
        for(int x=0;x<=12;x++){
            paint.setColor(Color.BLACK);
            canvas.save();
            canvas.rotate(x*30);
            canvas.drawLine(0,40,0,50, paint);
            canvas.restore();
        }

        //draw little circle ticks
        paint.setStrokeWidth(0.5f);
        paint.setColor((Color.BLACK));
        for(int i=0;i<=60;i++){
            canvas.save();
            canvas.rotate(i*6);
            canvas.drawLine(0,45, 0, 50, paint);
            canvas.restore();
        }


        canvas.restore();

        if(hourFormat) {
            hourMinSec.setHMS(hour, minute, second);
        }
        else{
            hourMinSec.setHMS(gregorianCalendar.get(Calendar.HOUR),minute,second);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);
        float wSize = View.MeasureSpec.getSize(widthMeasureSpec);
        float hSize = View.MeasureSpec.getSize(heightMeasureSpec);
        float width, height;
        height = wSize / ASPECT_RATIO;
        width = hSize * ASPECT_RATIO;
        if(height>hSize){
            this.setMeasuredDimension((int)width, (int)hSize);
        }
        else {

            this.setMeasuredDimension((int) wSize, (int) height);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh){
        super.onSizeChanged(w, h, oldw, oldh);
        mHeight = h;
        mWidth = w;
    }

    public void startTimer() {
        mTimer.setTimeListener(new TimeAnimator.TimeListener() {
            @Override
            public void onTimeUpdate(TimeAnimator animation, long totalTime, long deltaTime) {

                invalidate();
            }

        });
        mTimer.start();
    }


    @Override
    public void onTimeUpdate(TimeAnimator animation, long totalTime, long deltaTime) {
        invalidate();
        if(!partialSeconds) {
            //TODO make second hand "click" noise here
        }
    }
}
