package com.dhgg.appusagemonitor;


import java.util.ArrayList;

import android.content.ClipData.Item;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;


public class PieChart extends View 
{
	// Can be set in the attrs.xml
	public boolean mShowText = true;;
	public int mTextPos = 0;
	
	
    public Paint mTextPaint;
    public String aLabel = "Charting usage ...";
    public int mTextColor = Color.BLACK;
    public float mTextX = 20;
    public float mTextY = 50;
    public float mTextHeight = 40;
        
    public float mTextWidth = 10;
    
    public Paint mPiePaint;    
    public Paint mShadowPaint;
    public RectF mShadowBounds;
    
    public float mPointerX = 20;
    public float mPointerY = 20;
    public float mPointerSize = 20;    
    public Context mContext;

	Db_handler m_db_handler;
    
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
       
        init(ctx);
    }
    
    private void init( Context context ) 
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

       
       mShadowBounds = new RectF( 100, 100, 400, 400);
       mShadowPaint = new Paint(0);
       mShadowPaint.setColor(0xff101010);
       mShadowPaint.setMaskFilter(new BlurMaskFilter(8, BlurMaskFilter.Blur.NORMAL));
       

		m_db_handler = new Db_handler( context );
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
    	canvas.drawOval( mShadowBounds, mShadowPaint );

    	// Draw the label text
    	//canvas.drawText(mData.get(mCurrentItem).mLabel, mTextX, mTextY, mTextPaint);
    	canvas.drawText(aLabel, mTextX, mTextY, mTextPaint);

    	// Get data for slices
		ArrayList<Data_value> data = m_db_handler.getData( "s_h_p_today" );
		Data_value[] data_arr = data.toArray(new Data_value[data.size()]);
		int max = 0;
    	for ( int i = 0; i < data.size(); i++ )
    	{
    		max += data_arr[i].value;
    	}
    	
    	
    	// Draw the pie slices
		Shader shader = null;
    	for (int i = 0; i < data.size(); ++i) 
    	{
    	   mPiePaint.setShader( shader );
    	   
    	   int value = (i + (data_arr[i].value / max )  * 360 * 100 );
    	   canvas.drawArc( mShadowBounds, 0, value, true, mPiePaint );
    	   System.out.println( i + " , " + value + " --------------");
    	}
		
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
    
}

