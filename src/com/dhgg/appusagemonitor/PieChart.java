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
    
    private static Paint mPiePaint;    
    public Paint mPiePaint2;    
    public Paint mPiePaint3;    
    public Paint mShadowPaint;
    public RectF mShadowBounds;
    
    private static Paint[] m_paint_arcs;
    private static RectF[] m_rects;
    
    public float mPointerX = 20;
    public float mPointerY = 20;
    public float mPointerSize = 20;    
    public Context mContext;

	public Db_handler m_db_handler;
	
    public int m_colors[] = { 
        Color.BLUE, 
        Color.RED,
        Color.DKGRAY,
        Color.GRAY,
        Color.MAGENTA,
        Color.GREEN,
        Color.CYAN,
        Color.YELLOW,
        Color.LTGRAY,
        Color.BLACK,
        Color.WHITE, };
    public int m_num_arcs = 10;
    
    Data_value[] m_data_arr ;
    float m_max;
    int m_num_slices;
    
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
       if (mTextHeight == 0) {
    	   mTextHeight = mTextPaint.getTextSize();
       } else {
    	   mTextPaint.setTextSize(mTextHeight);
       }

       m_paint_arcs = new Paint[ m_num_arcs ];
       m_rects = new RectF[ m_num_arcs ];
       for (int i = 0; i < m_num_arcs; i++)
       {
    	   m_paint_arcs[i] = new Paint();
    	   
    	   m_paint_arcs[ i ].setAntiAlias(true);
    	   m_paint_arcs[ i ].setStyle(Paint.Style.FILL);
    	   m_paint_arcs[ i ].setStrokeWidth(0.0f);
    	   m_paint_arcs[ i ].setColor( m_colors[i] );    	   
    	
    	   m_rects[ i ] = new RectF( );
     	   m_rects[ i ].set(100,100,400,400);
    	   System.out.println(i+" color:"+m_colors[i]);
       } 
       
       mPiePaint = new Paint();
       mPiePaint.setAntiAlias(true);
       mPiePaint.setStyle(Paint.Style.FILL);
       mPiePaint.setStrokeWidth(0.5f);
       
       mShadowBounds = new RectF( 100.0f, 100.0f, 400.0f, 400.0f);
       
       mShadowPaint = new Paint(0);
       mShadowPaint.setColor(0xff101010);
       mShadowPaint.setMaskFilter(new BlurMaskFilter(8, BlurMaskFilter.Blur.NORMAL));
       
       m_db_handler = new Db_handler( context );
       set_data();
   	}

    private void set_data()
    {
		// Get data for slices
		ArrayList<Data_value> data = m_db_handler.getData( "s_h_p_today" );
		m_data_arr = data.toArray(new Data_value[data.size()]);		
		m_max = 0;
		m_num_slices = data.size();
		for ( int i = 0; i < m_num_slices; i++ )
		{
			m_max += m_data_arr[i].value;
		}
    }
    
    protected void onDraw(Canvas canvas) 
    {
    	super.onDraw(canvas);
    	System.out.println(" --------- onDraw ---------------");
    	
    	// Draw the label text
    	canvas.drawText(aLabel, mTextX, mTextY, mTextPaint);
    	
    	// Draw the pie slices
		float start_angle = 0.1f;
		float end_angle = 1;
		int time_to_go = 5;
    	for (int i = 0; i < m_num_slices; i++) 
    	{	
    		//canvas.save();
     	    float arc_size = (m_data_arr[i].value / m_max) * 360;     	    
    		end_angle = start_angle + arc_size;
    		
    	    if ( i == time_to_go ) ;//end_angle = 360;
    		
    		mPiePaint.setColor( m_colors[i] );
     	    canvas.drawArc( mShadowBounds, // m_rects[ i%m_num_arcs ], 
			                360 - end_angle, 
			                end_angle - start_angle,
			                true, 
			                mPiePaint ); //m_paint_arcs[ i%m_num_arcs ] );
		    
		    System.out.println( (i%m_num_arcs)+" "+start_angle+" "+end_angle);		    
    	    start_angle += arc_size;

    	    if ( i == time_to_go ) break;
    	}
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
    
}

