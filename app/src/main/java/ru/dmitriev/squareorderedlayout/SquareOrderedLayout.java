package ru.dmitriev.squareorderedlayout;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

public class SquareOrderedLayout extends ViewGroup {

    private static final String LOG_TAG = "SquareOrderedLayout";

    public SquareOrderedLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.d(LOG_TAG, "onMeasure widthMeasureSpec=" + widthMeasureSpec
                + " heightMeasureSpec=" + heightMeasureSpec);
        int count = getChildCount();
        int childState = 0;
        int maxHeight = 0;
        int maxWidth = 0;

        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
                final LayoutParams lp = (LayoutParams) child.getLayoutParams();
                maxWidth = Math.max(maxWidth,
                        child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin);
                maxHeight = Math.max(maxHeight,
                        child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin);
                Log.d(LOG_TAG, "childState=" + childState);
                Log.d(LOG_TAG, "MeasuredState=" + child.getMeasuredState());
                childState = combineMeasuredStates(childState, child.getMeasuredState());
            }
        }

        maxHeight += getPaddingBottom() + getPaddingTop();
        maxWidth += getPaddingLeft() + getPaddingTop();

        maxHeight = Math.max(maxHeight, getSuggestedMinimumHeight());
        maxWidth = Math.max(maxWidth, getSuggestedMinimumWidth());

        setMeasuredDimension(resolveSizeAndState(maxWidth, widthMeasureSpec, childState),
                resolveSizeAndState(maxHeight, heightMeasureSpec,
                        childState << MEASURED_HEIGHT_STATE_SHIFT));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        final int count = getChildCount();
        Log.d(LOG_TAG, "onLayout left=" + left + " top=" + top + " right=" + right + " bottom=" + bottom);

        // TODO: the child view is larger than its parent. how should it handle the case?
        final int parentLeft = left + getPaddingLeft();
        final int parentTop = top + getPaddingTop();

        int childLeft;
        int childTop = parentTop;
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                LayoutParams lp = (LayoutParams) child.getLayoutParams();
                childLeft = parentLeft + lp.leftMargin;
                childTop += lp.topMargin;
                final int childBottom = childTop + child.getMeasuredHeight(); // TODO: padding Bottom?
                final int childRight = childLeft + child.getMeasuredWidth();
                Log.d(LOG_TAG, "child.layout childLeft=" + childLeft + " childTop=" + childTop
                        + " childRight=" + childRight + " childBottom=" + childBottom);
                child.layout(childLeft,
                        childTop,
                        childRight,
                        childBottom);
                childTop += child.getMeasuredHeight();
            }
        }
    }


    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    public static class LayoutParams extends MarginLayoutParams {

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }
}
