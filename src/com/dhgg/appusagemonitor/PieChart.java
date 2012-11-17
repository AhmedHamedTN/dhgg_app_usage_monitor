package com.dhgg.appusagemonitor;



import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;


public class PieChart extends View 
{    
    public int m_width = 0;
    public int m_height = 0;
    
    private static Paint mPiePaint;
    public RectF mShadowBounds;

	int m_max_arcs = 11;
	public int m_colors[] = { 
	        Color.CYAN,    //
	        Color.RED, 
	        Color.LTGRAY,
	        Color.DKGRAY,
	        Color.BLUE,
	        Color.GREEN,
	        Color.GRAY,
	        Color.YELLOW,
	        Color.BLACK,
	        Color.MAGENTA,
	        Color.WHITE };
	
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
        } 
        finally { a.recycle(); }
       
        init(ctx);
    }
    
    private void init( Context context ) 
    {          
       mPiePaint = new Paint();
       mPiePaint.setAntiAlias(true);
       mPiePaint.setStyle(Paint.Style.FILL);
       mPiePaint.setStrokeWidth(0.5f);
       
       mShadowBounds = new RectF( );
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
		
		invalidate();
	    requestLayout();	    
    }
    
    protected void onDraw(Canvas canvas) 
    {
    	super.onDraw(canvas);
    	
    	// Update the rectangle bounds to fit on the screen nicely.
    	//System.out.println("+++ PieChart::onDraw +++ "+m_width+" "+m_height);
    	
    	float vertical_border = 0;
    	float horizontal_border = 0;
    	
    	int rect_size = m_width;

		// in landscape mode
    	if ( rect_size > m_height )
    	{
    		rect_size = m_height;
    		
    		horizontal_border = (m_width - m_height ) / 2.0f;
    		vertical_border = m_height * .05f;
    	}
    	else
    	{
    		horizontal_border = m_width * .05f;
    		vertical_border = (m_height - m_width ) / 2.0f;
    	}
    	float diameter = rect_size * 0.9f;
    	
    	//System.out.println( horizontal_border +" " + vertical_border+" " +diameter +"----------");

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
    	//System.out.print("+++ PieChart::onMeasure +++");
    	
       // Try for a width based on our minimum
       int minw = getPaddingLeft() + getPaddingRight() + getSuggestedMinimumWidth();
       m_width = resolveSize(minw, widthMeasureSpec);

       // Whatever the width ends up being, ask for a height that would let the pie
       // get as big as it can
       m_height = resolveSize(MeasureSpec.getSize(m_width), heightMeasureSpec);

       //System.out.println(m_width+" "+m_height);
       
       setMeasuredDimension(m_width, m_height);
    }

    
    
}

