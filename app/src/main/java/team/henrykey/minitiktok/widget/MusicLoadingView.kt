package team.henrykey.minitiktok.widget

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Point
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator

import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import team.henrykey.minitiktok.R

class MusicLoadingView : View {
    internal var context: Context


    private val monitorAnimator = ValueAnimator.ofFloat(1.0f, 1.1f)
    private val translateAnimator = ValueAnimator.ofFloat(0.0f, 1.0f)
    private val notesAnimator = ValueAnimator.ofFloat(0.0f, 1.0f)
    private var monitorDrawable: Drawable? = null
    private var diskDrawable: Drawable? = null
    private var notesDrawable: Drawable? = null
    private val degree = 0
    private var diskWidth = 0
    private var diskHeight = 0
    private var diskCenter: Point? = null
    internal var bitmap: Bitmap? = null // bitmap for disk drawable
    internal var matrix = Matrix()
    internal var animatorSet: AnimatorSet? = null

    internal var monitorUpdateListener = ValueAnimator.AnimatorUpdateListener { animation ->
        val af = monitorAnimator.animatedValue as Float
        if (monitorDrawable != null) {
            monitorDrawable!!.bounds = buildRect(viewCenter,
                (monitorDrawable!!.intrinsicWidth * af).toInt(),
                (monitorDrawable!!.intrinsicHeight * af).toInt())
            invalidate()
        }
    }

    internal var diskUpdateListener = ValueAnimator.AnimatorUpdateListener { animation ->
        val af = translateAnimator.animatedValue as Float
        val y = (top + bottom) / 2
        if (diskDrawable != null) {
            diskCenter = Point(-diskDrawable!!.intrinsicWidth / 2 + (af * (width / 2 + diskDrawable!!.intrinsicWidth / 2)).toInt(), y)
            invalidate()
        }
    }

    internal var notesUpdateListener = ValueAnimator.AnimatorUpdateListener { animation ->
        val af = animation.getAnimatedValue() as Float
        val y = (top + bottom) / 2
        if (notesDrawable != null) {
            val p = Point(((af + 1) * (width / 2)).toInt(), y)
            notesDrawable!!.bounds = buildRect(p, notesDrawable!!)
            //LogUtils.d("LOADING: Point " + p.x + " " + p.y);
            invalidate()
        }
    }

    private//LogUtils.d("LOADING: Center " + point.x + " " + point.y);
    val viewCenter: Point
        get() = Point((left + right) / 2, (top + bottom) / 2)

    /**
     * Simple constructor to use when creating a view from code.
     *
     * @param context The Context the view is running in, through which it can
     * access the current theme, resources, etc.
     */
    constructor(context: Context) : super(context) {
        this.context = context
    }

    /**
     * Constructor that is called when inflating a view from XML. This is called
     * when a view is being constructed from an XML file, supplying attributes
     * that were specified in the XML file. This version uses a default style of
     * 0, so the only attribute values applied are those in the Context's Theme
     * and the given AttributeSet.
     *
     *
     *
     * The method onFinishInflate() will be called after all children have been
     * added.
     *
     * @param context The Context the view is running in, through which it can
     * access the current theme, resources, etc.
     * @param attrs   The attributes of the XML tag that is inflating the view.
     */
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        this.context = context
    }

    /**
     * Perform inflation from XML and apply a class-specific base style from a
     * theme attribute. This constructor of View allows subclasses to use their
     * own base style when they are inflating. For example, a Button class's
     * constructor would call this version of the super class constructor and
     * supply `R.attr.buttonStyle` for <var>defStyleAttr</var>; this
     * allows the theme's button style to modify all of the base view attributes
     * (in particular its background) as well as the Button class's attributes.
     *
     * @param context      The Context the view is running in, through which it can
     * access the current theme, resources, etc.
     * @param attrs        The attributes of the XML tag that is inflating the view.
     * @param defStyleAttr An attribute in the current theme that contains a
     * reference to a style resource that supplies default values for
     * the view. Can be 0 to not look for defaults.
     */
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        this.context = context
    }

    /**
     * Perform inflation from XML and apply a class-specific base style from a
     * theme attribute or style resource. This constructor of View allows
     * subclasses to use their own base style when they are inflating.
     *
     *
     * When determining the final value of a particular attribute, there are
     * four inputs that come into play:
     *
     *  1. Any attribute values in the given AttributeSet.
     *  1. The style resource specified in the AttributeSet (named "style").
     *  1. The default style specified by <var>defStyleAttr</var>.
     *  1. The default style specified by <var>defStyleRes</var>.
     *  1. The base values in this theme.
     *
     *
     *
     * Each of these inputs is considered in-order, with the first listed taking
     * precedence over the following ones. In other words, if in the
     * AttributeSet you have supplied `<Button * textColor="#ff000000">`
     * , then the button's text will *always* be black, regardless of
     * what is specified in any of the styles.
     *
     * @param context      The Context the view is running in, through which it can
     * access the current theme, resources, etc.
     * @param attrs        The attributes of the XML tag that is inflating the view.
     * @param defStyleAttr An attribute in the current theme that contains a
     * reference to a style resource that supplies default values for
     * the view. Can be 0 to not look for defaults.
     * @param defStyleRes  A resource identifier of a style resource that
     * supplies default values for the view, used only if
     * defStyleAttr is 0 or can not be found in the theme. Can be 0
     * to not look for defaults.
     */
    @RequiresApi(21)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        this.context = context
    }

    /**
     * Implement this to do your drawing.
     *
     * @param canvas the canvas on which the background will be drawn
     */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawDrawable(canvas, notesDrawable)
        drawBitmap(canvas, bitmap, diskCenter)
        drawDrawable(canvas, monitorDrawable)
    }

    /**
     * This is called when the view is attached to a window.  At this point it
     * has a Surface and will start drawing.  Note that this function is
     * guaranteed to be called before [.onDraw],
     * however it may be called any time before the first onDraw -- including
     * before or after [.onMeasure].
     *
     * @see .onDetachedFromWindow
     */
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        loadDrawables()
        initMonitorAnimator()
        initDiskAnimator()
        initNotesAnimator()
        animatorSet = AnimatorSet() // use different interpolator
        animatorSet!!.addListener(object : AnimatorListenerAdapter() {
            private var mCanceled: Boolean = false

            /**
             * {@inheritDoc}
             *
             * @param animation
             */
            override fun onAnimationCancel(animation: Animator) {
                mCanceled = true
            }

            /**
             * {@inheritDoc}
             *
             * @param animation
             */
            override fun onAnimationEnd(animation: Animator) {
                if (!mCanceled) {
                    post { animatorSet!!.start() }
                }
            }

            /**
             * {@inheritDoc}
             *
             * @param animation
             */
            override fun onAnimationStart(animation: Animator) {
                mCanceled = false
            }
        })
        animatorSet!!.play(translateAnimator).with(monitorAnimator)
        animatorSet!!.play(notesAnimator).after(translateAnimator)
        animatorSet!!.start()
    }

    /**
     * This is called when the view is detached from a window.  At this point it
     * no longer has a surface for drawing.
     *
     * @see .onAttachedToWindow
     */
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        animatorSet!!.cancel()
    }

    fun cancelAnimation() {
        if (animatorSet != null && animatorSet!!.isStarted) {
            animatorSet!!.cancel()
        }
    }

    private fun initMonitorAnimator() {
        //monitorAnimator.setRepeatCount(ValueAnimator.INFINITE);
        monitorAnimator.duration = 2400.toLong()
        monitorAnimator.interpolator = Interpolator { t ->
            // over shoot
            val T = 2.0f
            val stt = 0.45f
            val inDuration = 0.075f
            val pauseDuration = 0.13f
            val outDuration = 0.075f
            val edt = stt + inDuration + pauseDuration + outDuration
            if (0.0f <= t && t < stt) {
                return@Interpolator 0f
            } else if (stt <= t && t < stt + inDuration) {
                val t2 = (t - stt) / inDuration
                return@Interpolator((T + 1) * Math.pow(t2 - 1.0, 3.0) + Math.pow(T * (t2 - 1.0), 2.0) + 1.0).toFloat()
            } else if (stt + inDuration <= t && t < stt + inDuration + pauseDuration) {
                return@Interpolator 1f
            } else if (stt + inDuration + pauseDuration <= t && t < edt) {
                val t2 = (t - stt - pauseDuration - inDuration) / outDuration
                return@Interpolator(-((T + 1) * Math.pow(t2 - 1.0, 3.0) + Math.pow(T * (t2 - 1.0), 2.0))).toFloat()
            } else {
                return@Interpolator 0f
            }
        } as Interpolator
        monitorAnimator.addUpdateListener(monitorUpdateListener)
    }

    private fun initDiskAnimator() {
        //translateAnimator.setRepeatCount(1);
        translateAnimator.duration = 1200.toLong()
        translateAnimator.interpolator = LinearInterpolator()
        translateAnimator.addUpdateListener(diskUpdateListener)
    }

    private fun initNotesAnimator() {
        //notesAnimator.setRepeatCount(1);
        notesAnimator.duration = 1200.toLong()
        notesAnimator.interpolator = LinearInterpolator()
        notesAnimator.addUpdateListener(notesUpdateListener)
    }


    // load the needed drawables
    private fun loadDrawables() {
        monitorDrawable = ContextCompat.getDrawable(context, R.drawable.ic_music_loading_computer)
        diskDrawable = ContextCompat.getDrawable(context, R.drawable.ic_music_loading_disk_v2)
            ?.apply {
                diskHeight = intrinsicHeight
                diskWidth = intrinsicWidth
                bitmap = drawableToBitmap(this)
            }

        notesDrawable = ContextCompat.getDrawable(context, R.drawable.ic_music_loading_music)
    }


    private fun buildRect(center: Point, width: Int, height: Int): Rect {
//LogUtils.d("LOADING: RECT " + rect.left + " " + rect.top + " " + rect.right + " " + rect.bottom);
        //LogUtils.d("LOADING: RECT " + getLeft() + " " + getTop() + " " + getRight() + " " + getBottom());
        return Rect(center.x - width / 2,
            center.y - height / 2,
            center.x + width / 2,
            center.y + height / 2)
    }

    private fun buildRect(center: Point, drawable: Drawable): Rect {
        val intrinsicWidth = drawable.intrinsicWidth
        val intrinsicHeight = drawable.intrinsicHeight
        return buildRect(center, intrinsicWidth, intrinsicHeight)
    }

    private fun drawDrawable(canvas: Canvas, drawable: Drawable?) {
        if (drawable != null && isViewValid(getDrawableCenter(drawable))) {
            drawable.draw(canvas)
        }
    }

    private fun getDrawableCenter(drawable: Drawable): Point {
        val rect = drawable.bounds
        return Point(rect.centerX(), rect.centerY())
    }

    private fun drawBitmap(canvas: Canvas, bitmap: Bitmap?, p: Point?) {
        var bitmap = bitmap
        if (bitmap != null && isViewValid(p)) {
            matrix.postRotate(8.0f)
            val rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
            //Bitmap temp = bitmap;
            bitmap = rotatedBitmap
            //temp.recycle();
            val source = buildRect(p!!, bitmap!!.width, bitmap.height)
            canvas.drawBitmap(bitmap, null, source, null)
            //            LogUtils.d("LOADING: bitmap" + source.left + " " + source.top + " " + source.right + " " + source.bottom);
        }
    }

    private fun isViewValid(p: Point?): Boolean {
        return p?.run {
            visibility == VISIBLE && y > 100
        } ?: false
    }

    companion object {

        fun drawableToBitmap(drawable: Drawable): Bitmap {

            if (drawable is BitmapDrawable) {
                return drawable.bitmap
            }

            val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)

            return bitmap
        }
    }
}
