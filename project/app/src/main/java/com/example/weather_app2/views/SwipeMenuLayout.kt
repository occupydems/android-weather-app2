package com.example.weather_app2.views

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import java.util.concurrent.atomic.AtomicBoolean

class SwipeMenuLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    companion object {
        private var currentlyOpenMenu: SwipeMenuLayout? = null
        private const val FLING_THRESHOLD = 800f
        private const val SNAP_THRESHOLD_FRACTION = 0.4f
        private const val ANIMATION_DURATION = 250L

        fun closeCurrentMenu() {
            currentlyOpenMenu?.smoothClose()
            currentlyOpenMenu = null
        }
    }

    private val menuWidthPx = (88 * resources.displayMetrics.density).toInt()
    private val cornerRadiusPx = 16 * resources.displayMetrics.density
    private val isMenuOpen = AtomicBoolean(false)
    private val isAnimating = AtomicBoolean(false)

    private var contentView: View? = null
    private var velocityTracker: VelocityTracker? = null

    private var downX = 0f
    private var downY = 0f
    private var lastX = 0f
    private var currentSwipeOffset = 0f
    private var isDragging = false
    private val touchSlop = android.view.ViewConfiguration.get(context).scaledTouchSlop

    private val deletePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#FF3B30")
    }

    private val deleteTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 15 * resources.displayMetrics.density
        textAlign = Paint.Align.CENTER
    }

    private val deleteRect = RectF()

    init {
        setWillNotDraw(false)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        if (childCount > 0) {
            contentView = getChildAt(0)
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        contentView?.let { child ->
            val childWidth = child.measuredWidth
            val childHeight = child.measuredHeight
            val offset = currentSwipeOffset.toInt()
            child.layout(-offset, 0, childWidth - offset, childHeight)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (currentSwipeOffset > 0) {
            val right = width.toFloat()
            val left = right - currentSwipeOffset
            deleteRect.set(left, 0f, right, height.toFloat())

            canvas.drawRoundRect(deleteRect, cornerRadiusPx, cornerRadiusPx, deletePaint)

            val deleteText = "Delete"
            val textX = left + (right - left) / 2
            val textY = height / 2f - (deleteTextPaint.descent() + deleteTextPaint.ascent()) / 2
            canvas.drawText(deleteText, textX, textY, deleteTextPaint)
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        when (ev.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                downX = ev.rawX
                downY = ev.rawY
                lastX = ev.rawX
                isDragging = false

                if (currentlyOpenMenu != null && currentlyOpenMenu != this) {
                    closeCurrentMenu()
                    return true
                }

                velocityTracker?.recycle()
                velocityTracker = VelocityTracker.obtain()
                velocityTracker?.addMovement(ev)
            }
            MotionEvent.ACTION_MOVE -> {
                velocityTracker?.addMovement(ev)
                val dx = ev.rawX - downX
                val dy = ev.rawY - downY
                if (!isDragging && Math.abs(dx) > touchSlop && Math.abs(dx) > Math.abs(dy) * 1.5f) {
                    isDragging = true
                    parent?.requestDisallowInterceptTouchEvent(true)
                    return true
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                isDragging = false
            }
        }
        return isDragging
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        velocityTracker?.addMovement(ev)

        when (ev.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                downX = ev.rawX
                downY = ev.rawY
                lastX = ev.rawX
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                val deltaX = lastX - ev.rawX
                lastX = ev.rawX

                val newOffset = (currentSwipeOffset + deltaX).coerceIn(0f, menuWidthPx.toFloat())
                currentSwipeOffset = newOffset

                contentView?.translationX = -currentSwipeOffset
                invalidate()
                return true
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                parent?.requestDisallowInterceptTouchEvent(false)
                isDragging = false

                velocityTracker?.computeCurrentVelocity(1000)
                val velocityX = velocityTracker?.xVelocity ?: 0f
                velocityTracker?.recycle()
                velocityTracker = null

                if (Math.abs(velocityX) > FLING_THRESHOLD) {
                    if (velocityX < 0) {
                        smoothOpen()
                    } else {
                        smoothClose()
                    }
                } else {
                    if (currentSwipeOffset > menuWidthPx * SNAP_THRESHOLD_FRACTION) {
                        smoothOpen()
                    } else {
                        smoothClose()
                    }
                }

                if (ev.actionMasked == MotionEvent.ACTION_UP && isMenuOpen.get()) {
                    val touchX = ev.x
                    if (touchX > width - currentSwipeOffset) {
                        performDeleteClick()
                    }
                }

                return true
            }
        }
        return super.onTouchEvent(ev)
    }

    fun smoothOpen() {
        if (isAnimating.get()) return
        isAnimating.set(true)

        if (currentlyOpenMenu != null && currentlyOpenMenu != this) {
            currentlyOpenMenu?.smoothClose()
        }

        val animator = ValueAnimator.ofFloat(currentSwipeOffset, menuWidthPx.toFloat())
        animator.duration = ANIMATION_DURATION
        animator.interpolator = DecelerateInterpolator()
        animator.addUpdateListener { animation ->
            currentSwipeOffset = animation.animatedValue as Float
            contentView?.translationX = -currentSwipeOffset
            invalidate()
        }
        animator.addListener(object : android.animation.AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: android.animation.Animator) {
                isMenuOpen.set(true)
                isAnimating.set(false)
                currentlyOpenMenu = this@SwipeMenuLayout
            }
        })
        animator.start()
    }

    fun smoothClose() {
        if (isAnimating.get()) return
        isAnimating.set(true)

        val animator = ValueAnimator.ofFloat(currentSwipeOffset, 0f)
        animator.duration = ANIMATION_DURATION
        animator.interpolator = DecelerateInterpolator()
        animator.addUpdateListener { animation ->
            currentSwipeOffset = animation.animatedValue as Float
            contentView?.translationX = -currentSwipeOffset
            invalidate()
        }
        animator.addListener(object : android.animation.AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: android.animation.Animator) {
                isMenuOpen.set(false)
                isAnimating.set(false)
                if (currentlyOpenMenu == this@SwipeMenuLayout) {
                    currentlyOpenMenu = null
                }
            }
        })
        animator.start()
    }

    fun isOpen(): Boolean = isMenuOpen.get()

    private var onDeleteClickListener: (() -> Unit)? = null

    fun setOnDeleteClickListener(listener: () -> Unit) {
        onDeleteClickListener = listener
    }

    private fun performDeleteClick() {
        onDeleteClickListener?.invoke()
    }

    fun resetMenu() {
        currentSwipeOffset = 0f
        contentView?.translationX = 0f
        isMenuOpen.set(false)
        if (currentlyOpenMenu == this) {
            currentlyOpenMenu = null
        }
        invalidate()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (currentlyOpenMenu == this) {
            currentlyOpenMenu = null
        }
        velocityTracker?.recycle()
        velocityTracker = null
    }
}
