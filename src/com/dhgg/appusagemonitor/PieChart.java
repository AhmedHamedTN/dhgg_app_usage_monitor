package com.dhgg.appusagemonitor;


import java.util.ArrayList;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.graphics.RectF;
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
    
    public int m_minw = 0;
    public int m_minh = 0;
    public int m_width = 0;
    public int m_height = 0;
    
    private static Paint mPiePaint;
    public RectF mShadowBounds;

	int m_max_arcs = 10;
	
    public Context m_context;
	
    public int m_colors[] = { 
        Color.CYAN,
        Color.RED, 
        Color.LTGRAY,
        Color.DKGRAY,
        Color.BLUE,
        Color.GREEN,
        Color.GRAY,
        Color.YELLOW,
        Color.BLACK,
        Color.MAGENTA, 
        Color.WHITE,};
    
    Data_value[] m_data_arr ;
    float m_max;
    int m_num_slices;
    
    boolean m_show_chart = false;
    
    public PieChart(Context ctx, AttributeSet attrs) 
    {
        super(ctx, attrs);
        
        this.setBackgroundColor(Color.TRANSPARENT);
        
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
       mTextPaint.setTextSize(mTextHeight);
              
       mPiePaint = new Paint();
       mPiePaint.setAntiAlias(true);
       mPiePaint.setStyle(Paint.Style.FILL);
       mPiePaint.setStrokeWidth(0.5f);
       
       mShadowBounds = new RectF( );
 
       m_context = context;
   	}

    public void set_data( Data_value[] data_arr )
    {
		// Get data for slices
		m_data_arr = data_arr;		
		m_max = 0;
		m_num_slices = data_arr.length;
		for ( int i = 0; i < m_num_slices; i++ )
		{
			m_max += m_data_arr[i].value;
		}

    	System.out.println("+++ PieChart::set_data +++");
    }
    
    protected void onDraw(Canvas canvas) 
    {
    	super.onDraw(canvas);
    	
    	if ( m_show_chart )
    	{
    		return;
    	}

    	// Update the rectangle bounds to fit on the screen nicely.
    	int rect_size = m_width;
    	boolean in_portrait = true;
    	if ( rect_size > m_height )
    	{
    		// in landscape mode
    		in_portrait = false;
    		rect_size = m_height;
    	}
    	float diameter = rect_size * .8f;

    	float vertical_border = 0;
    	float horizontal_border = 0;
    	if ( in_portrait )
    	{
    		vertical_border = rect_size *.05f;
    		horizontal_border = rect_size * .1f;
    	}
    	else
    	{
    		vertical_border = rect_size *.1f;
    		horizontal_border = rect_size * .1f;
    	}
        mShadowBounds.set( horizontal_border, vertical_border, 
        		           diameter+horizontal_border, diameter+vertical_border);

    	// Draw the pie slices
		float start_angle = 0;
		float end_angle = 0;
    	for (int i = 0; i < m_num_slices; i++) 
    	{	
     	    float arc_size = (m_data_arr[i].value / m_max) * 360;     	    
    		end_angle = start_angle + arc_size;
    		
    	    if ( i == m_max_arcs ) 
    	    {
    	        end_angle = 360;
    	    }

            float first_angle = 360 - end_angle;
            float sweep_angle = end_angle - start_angle;
              
    		mPiePaint.setColor( m_colors[i] );
     	    canvas.drawArc( mShadowBounds, first_angle, sweep_angle, 
			                true, 
			                mPiePaint ); 
		    
     	    start_angle += arc_size;

    	    if ( i == m_max_arcs ) 
    	    {
    	    	break;
    	    }
    	}
 
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) 
    {    	
       // Try for a width based on our minimum
       m_minw = getPaddingLeft() + getPaddingRight() + getSuggestedMinimumWidth();
       m_width = resolveSizeAndState(m_minw, widthMeasureSpec, 1);

       // Whatever the width ends up being, ask for a height that would let the pie
       // get as big as it can
       m_minh = MeasureSpec.getSize(m_width) - (int)mTextWidth + getPaddingBottom() + getPaddingTop();
       m_height = resolveSizeAndState(MeasureSpec.getSize(m_width) - (int)mTextWidth, heightMeasureSpec, 0);

       setMeasuredDimension(m_width, m_height);
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

