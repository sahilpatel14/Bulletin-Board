package in.sahilpatel.bulletinboard.support;


import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Here we are creating a custom layout for our images. The
 * images needs to be square shaped. Therefore we will be extending
 * the RelativeLayout, creating necessary constructors, overriding necessary methods.
 *
 * Turns out we only need to override one method.
 */

public class SquareLayout extends RelativeLayout{

    public SquareLayout(Context context) {
        super(context);
    }

    public SquareLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SquareLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //  Setting a square layout
        /*  height equals width */
          super.onMeasure(widthMeasureSpec,widthMeasureSpec);
       // super.onMeasure(widthMeasureSpec,heightMeasureSpec);
    }
}
