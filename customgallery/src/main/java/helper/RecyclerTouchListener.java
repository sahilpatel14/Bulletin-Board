package helper;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import adapters.GalleryAdapter;


public class RecyclerTouchListener implements RecyclerView.OnItemTouchListener{

    private GestureDetector gestureDetector;
    private GalleryAdapter.ClickListener clickListener;

    public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final GalleryAdapter.ClickListener clickListener) {
        this.clickListener = clickListener;
        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener(){

            @Override
            public boolean onSingleTapUp(MotionEvent e){
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                View child = recyclerView.findChildViewUnder(e.getX(),e.getY());
                if(child != null && clickListener != null){
                    clickListener.onLongClick(child,recyclerView.getChildAdapterPosition(child));
                }
            }
        });
    }
        /*
         * This method JUST determines whether we want to intercept the motion.
         * If we return true, onTouchEvent will be called and we do the actual
         * scrolling there.
         *
         * If onInterceptTouchEvent() returns true, the MotionEvent is intercepted,
          * meaning it will be not be passed on to the child, but rather to the onTouchEvent()
          *
          * The onInterceptTouchEvent() method gives a parent the chance to see any touch event before its children do.
          * If you return true from onInterceptTouchEvent(), the child view that was previously handling touch events
          * receives an ACTION_CANCEL, and the events from that point forward are sent to the parent's onTouchEvent()
           * method for the usual handling.
         */
    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

        View child = rv.findChildViewUnder(e.getX(),e.getY());
        if(child != null && clickListener != null && gestureDetector.onTouchEvent(e)){
            clickListener.onClick(child,rv.getChildAdapterPosition(child));
        }
        return false;
    }

    /*  Useless for now.    */

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        // Here we actually handle the touch event (e.g. if the action is ACTION_MOVE,
        // scroll this container).
        // This method will only be called if the touch event was intercepted in
        // onInterceptTouchEvent

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }
}
