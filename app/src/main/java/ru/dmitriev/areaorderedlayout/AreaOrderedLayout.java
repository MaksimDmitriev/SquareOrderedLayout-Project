package ru.dmitriev.areaorderedlayout;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.Arrays;

public class AreaOrderedLayout extends ViewGroup {

    private static final String LOG_TAG = "AreaOrderedLayout";

    public AreaOrderedLayout(Context context, AttributeSet attrs) {
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
                final LayoutParams childParams = (LayoutParams) child.getLayoutParams();
                maxWidth += child.getMeasuredWidth() + childParams.leftMargin + childParams.rightMargin;
                maxHeight += child.getMeasuredHeight() + childParams.topMargin + childParams.bottomMargin;

                final int childMeasuredState = child.getMeasuredState();
                Log.d(LOG_TAG, "childState=" + childState);
                Log.d(LOG_TAG, "childMeasuredState=" + childMeasuredState);
                childState = combineMeasuredStates(childState, childMeasuredState);
            }
        }

        maxHeight += getPaddingBottom() + getPaddingTop();
        maxWidth += getPaddingLeft() + getPaddingRight();

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

        ChildTuple[] childTuples = new ChildTuple[count];
        for (int i = 0; i < count; i++) {
            childTuples[i] = new ChildTuple(
                    i,
                    getChildAt(i).getMeasuredHeight() * getChildAt(i).getMeasuredWidth());
        }
        Arrays.sort(childTuples);

        int childTop = getPaddingTop();
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(childTuples[i].mIndex);
            if (child.getVisibility() != GONE) {
                LayoutParams childParams = (LayoutParams) child.getLayoutParams();
                int childLeft = getPaddingLeft() + childParams.leftMargin;
                childTop += childParams.topMargin;
                final int childBottom = childTop + child.getMeasuredHeight();
                final int childRight = childLeft + child.getMeasuredWidth();
                Log.d(LOG_TAG, "onLayout left=" + childLeft + " childTop=" + childTop
                        + " childRight=" + childRight + " childBottom=" + childBottom);
                child.layout(childLeft,
                        childTop,
                        childRight,
                        childBottom);
                childTop += (child.getMeasuredHeight() + childParams.bottomMargin);
            } else {
                // TODO:
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

    private static final class ChildTuple implements Comparable<ChildTuple> {

        final int mIndex;
        final int mArea;

        ChildTuple(int index, int area) {
            mIndex = index;
            mArea = area;
        }

        @Override
        public int compareTo(ChildTuple o) {
            return o.mArea - mArea;
        }
    }
}
