package com.android.study.lczv.customviewwave

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class WavySeekBar:View,View.OnTouchListener{

    private var viewWidth = 0
    private var viewHeight = 0

    private var waveColor = Color.LTGRAY
    private var buttonColor = Color.BLACK

    private var tickness = 20f
    private var buttonRadius = 32f
    var waveLength = 20f
    var waveAmplitude = 40f

    private var maxValue = 100

    val path = Path()

    private val buttonPosition = PointF(0f,viewHeight/2f)

    private var touchCallBack : (Int) -> Unit = {}
    private val paint = Paint()

    fun Float.clamp(min:Float, max:Float):Float{
        if (this < min) return min
        else if(this > max) return max
        else return this
    }

    constructor(context: Context?) : super(context){
        init(null)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs){
        init(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr){
        init(attrs)
    }

    fun init(attrs: AttributeSet?){

        val attrsArray = context.obtainStyledAttributes(attrs,R.styleable.WavySeekBar)

        try {
            waveColor = attrsArray.getColor(R.styleable.WavySeekBar_wave_color,waveColor)
            buttonColor = attrsArray.getColor(R.styleable.WavySeekBar_button_color,buttonColor)
            tickness = attrsArray.getFloat(R.styleable.WavySeekBar_tickness,tickness)
            maxValue = attrsArray.getInt(R.styleable.WavySeekBar_max_value,maxValue)
            buttonRadius = attrsArray.getFloat(R.styleable.WavySeekBar_button_radius,buttonRadius)
            waveAmplitude = attrsArray.getFloat(R.styleable.WavySeekBar_wave_amplitude,waveAmplitude)
            waveLength = attrsArray.getFloat(R.styleable.WavySeekBar_wave_length,waveLength)
        }finally {
            attrsArray.recycle()
        }

        setLayerType(LAYER_TYPE_SOFTWARE,null)
        paint.strokeCap = Paint.Cap.ROUND
        paint.strokeJoin = Paint.Join.ROUND
        paint.strokeWidth = tickness

        setOnTouchListener(this)

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        viewWidth = MeasureSpec.getSize(widthMeasureSpec)
        viewHeight = MeasureSpec.getSize(heightMeasureSpec)

        setMeasuredDimension(viewWidth,viewHeight)

        buttonPosition.y = viewHeight/2f


    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        paint.style = Paint.Style.STROKE
        paint.color = waveColor
        path.reset()

        // Initial position of the drawing
        path.moveTo(0f,viewHeight/2f)

        for(i in 0..viewWidth/waveLength.toInt()){
            path.lineTo(i.toFloat()*waveLength,Math.sin(i.toDouble()).toFloat()*waveAmplitude+viewHeight/2f)
            canvas?.drawPath(path,paint)
        }

        paint.style = Paint.Style.FILL
        paint.color = buttonColor
        canvas?.drawCircle(buttonPosition.x,buttonPosition.y,buttonRadius,paint)
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {

        buttonPosition.x = event!!.x.clamp(0.toFloat(),viewWidth.toFloat())
        buttonPosition.y = Math.sin(event.x.clamp(0.toFloat(),viewWidth.toFloat()).toDouble()/waveLength).toFloat()*waveAmplitude+viewHeight/2f

        val intervalSize = viewWidth/maxValue.toFloat()
        val x = Math.ceil(buttonPosition.x/intervalSize.toDouble())
        touchCallBack(x.toInt())

        invalidate()

        return true
    }

    fun setTouchCallback(touchCallBack:(Int)->Unit){
        this.touchCallBack = touchCallBack
    }
}