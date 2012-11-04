package com.dhgg.appusagemonitor;


import java.util.ArrayList;

import android.content.ClipData.Item;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;


public class PieChart extends View 
{
	public boolean mShowText;
	public int mTextPos;
    public Paint mTextPaint;
    public int mTextColor;
    
    public float mTextHeight = 10;
    public float mTextWidth = 10;
    
    public Paint mPiePaint;    
    public Paint mShadowPaint;
    public RectF mShadowBounds;
    
    public String aLabel = "some label";
    public float mTextX = 10;
    public float mTextY = 10;
    
    public float mPointerX = 20;
    public float mPointerY = 20;
    public float mPointerSize = 20;
    
    public Context mContext;
    
    
    
    public PieChart(Context ctx, AttributeSet attrs) 
    {
        super(ctx, attrs);
        
        TypedArray a = ctx.getTheme().obtainStyledAttributes(
                attrs, R.styleable.PieChart, 0, 0);
        
        try 
        {
            mShowText = a.getBoolean(R.styleable.PieChart_showText, false);
            mTextPos = a.getInteger(R.styleable.PieChart_labelPosition, 0);
        } 
        finally { a.recycle(); }
       
        init();
    }
    
    private void init() 
    {
       mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
       mTextPaint.setColor(mTextColor);
       if (mTextHeight == 0) {
    	   mTextHeight = mTextPaint.getTextSize();
       } else {
    	   mTextPaint.setTextSize(mTextHeight);
       }

       mPiePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
       mPiePaint.setStyle(Paint.Style.FILL);
       mPiePaint.setTextSize(mTextHeight);

       mShadowPaint = new Paint(0);
       mShadowPaint.setColor(0xff101010);
       mShadowPaint.setMaskFilter(new BlurMaskFilter(8, BlurMaskFilter.Blur.NORMAL));
   	}

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) 
    {
       // Try for a width based on our minimum
       int minw = getPaddingLeft() + getPaddingRight() + getSuggestedMinimumWidth();
       int w = resolveSizeAndState(minw, widthMeasureSpec, 1);

       // Whatever the width ends up being, ask for a height that would let the pie
       // get as big as it can
       int minh = MeasureSpec.getSize(w) - (int)mTextWidth + getPaddingBottom() + getPaddingTop();
       int h = resolveSizeAndState(MeasureSpec.getSize(w) - (int)mTextWidth, heightMeasureSpec, 0);

       setMeasuredDimension(w, h);
    }
    
    protected void onDraw(Canvas canvas) 
    {
    	super.onDraw(canvas);

    	System.out.println(" --------- onDraw ---------------");
    	
    	// Draw the shadow
    	//canvas.drawOval( mShadowBounds, mShadowPaint );

    	// Draw the label text
    	//canvas.drawText(mData.get(mCurrentItem).mLabel, mTextX, mTextY, mTextPaint);
    	canvas.drawText(aLabel, mTextX, mTextY, mTextPaint);

    	/*
    	// Get data for slices
		Db_handler db_handler = new Db_handler( mContext );
		ArrayList<Data_value> data = db_handler.getData( "s_h_p_today" );
		
		Data_value[] data_arr = data.toArray(new Data_value[data.size()]);
		Data_value_adapter adapter = new Data_value_adapter(  mContext,
				R.layout.name_value_row, data_arr);
    	
    	// Draw the pie slices
    	for (int i = 0; i < mData.size(); ++i) 
    	{
    	   Item it = mData.get(i);
    	   mPiePaint.setShader(it.mShader);
    	   canvas.drawArc( mBounds, 360 - it.mEndAngle, 
    			           it.mEndAngle - it.mStartAngle,
                           true, mPiePaint );
    	}
		*/
    	// Draw the pointer
    	//canvas.drawLine(mTextX, mPointerY, mPointerX, mPointerY, mTextPaint);
    	//canvas.drawCircle(mPointerX, mPointerY, mPointerSize, mTextPaint);
    }
    
    // Getters / Setters	
    public boolean isShowText() 
    {
    	return mShowText;
    }

    public void setShowText(boolean showText) 
    {
       mShowText = showText;
       invalidate();
       requestLayout();
    }
    	
    public void setContext( Context context )
    {
    	mContext = context;
    }
    
}

