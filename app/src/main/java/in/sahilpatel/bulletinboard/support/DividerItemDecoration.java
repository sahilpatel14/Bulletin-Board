package in.sahilpatel.bulletinboard.support;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Adds a line divider between RecyclerView items.
 * We need to extend ItemDecoration class to override onDrawOver method
 * and getItemOffsets method
 */

public class DividerItemDecoration extends RecyclerView.ItemDecoration {

        //  Defining the attribute that we need.
        private static final int[] ATTRS = new int[]{
                android.R.attr.listDivider
        };

        //  These help in ensuring the orientation of the screen
        public static final int HORIZONTAL_LIST = LinearLayoutManager.HORIZONTAL;
        public  static final int VERTICAL_LIST = LinearLayoutManager.VERTICAL;

        private Drawable mDivider;      //  Our divider will go here

        private int mOrientation;       //  Storing the orientation of screen


    /**
     * Obtain attributes of a divider using ATTRS we defined earlier. use those attributes
     * to get a divider drawable
     * set orientation
     * @param context
     * @param orientation
     */

    public DividerItemDecoration(Context context, int orientation) {

        final TypedArray a = context.obtainStyledAttributes(ATTRS);
        mDivider = a.getDrawable(0);
        a.recycle();
        setOrientation(orientation);

    }

    /**
     * Check if orientation is horizontal or vertical. Any other value raises
     * IllegalArgumentException. Save orientation locally
     * @param orientation
     */
    private void setOrientation(int orientation) {

        if(orientation != HORIZONTAL_LIST && orientation != VERTICAL_LIST) {
            throw new IllegalArgumentException("DividerItemDecorator : invalid operation");
        }
        mOrientation = orientation;
    }

    /**
     * This method will be called when RecyclerView is to be drawm. Check the orientation of
     * the screen, based on the orientation, call the respective method.
     * @param c
     * @param parent
     * @param state
     */
    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        if(mOrientation == VERTICAL_LIST){
            drawVertical(c, parent);
        }
        else{
            drawHorizontal(c,parent);
        }
    }

    /**
     * Draws a vertical divider on the parent RecycleView.
     * Get leftmost and rightmost points of the parent and count of child componenets inside.
     *
     * For all the children of parent
     * Get the child, Get LayoutParams of child
     *
     *Find top and bottom of child. Bottom Should include height of divider as well
     *
     * Draw the divider using setBounds and draw.
     * @param c
     * @param parent
     */

    public void drawVertical(Canvas c, RecyclerView parent) {

        final int left = parent.getPaddingLeft();
        final int right = parent.getWidth() - parent.getPaddingRight();
        final int childCount = parent.getChildCount();

        for(int i = 0; i < childCount; i++) {

            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams)child.getLayoutParams();

            final int top = child.getBottom() + params.bottomMargin;
            final int bottom = top + mDivider.getIntrinsicHeight();
            mDivider.setBounds(left,top,right,bottom);
            mDivider.draw(c);
        }
    }

    /**
     * Draws a Horizontal divider on the parent RecycleView.
     * Get top and bottom points of the parent along with children count.
     *
     * For all the children of parent
     * Get the child, Get LayoutParams of child
     *
     * Find left and right points of the child
     *
     * Draw the divider using setBounds and draw.
     * @param c
     * @param parent
     */

        public void drawHorizontal(Canvas c, RecyclerView parent) {
            final int top = parent.getPaddingTop();
            final int bottom = parent.getHeight() - parent.getPaddingBottom();

            final int childCount = parent.getChildCount();

            for(int i = 0; i < childCount; i++) {

                final View child = parent.getChildAt(i);
                final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams)child.getLayoutParams();

                final int left = child.getRight() + params.rightMargin;
                final int right = left + mDivider.getIntrinsicHeight();
                mDivider.setBounds(left,top,right,bottom);
                mDivider.draw(c);
            }
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            if(mOrientation == HORIZONTAL_LIST){
                outRect.set(0,0,0,mDivider.getIntrinsicHeight());
            }
            else{
                outRect.set(0,0,mDivider.getIntrinsicHeight(),0);
            }
        }
}
