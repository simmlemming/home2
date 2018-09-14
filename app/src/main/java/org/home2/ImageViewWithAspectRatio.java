package org.home2;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.View;

/**
 * Class the resizes to fit the height using the width to be a 4:3 aspect ration, or the one specified through XML. When setting a picasso drawable, the
 * background color is set to a simple average color of the bitmap
 *
 * @author mp mobile team
 */
public class ImageViewWithAspectRatio extends AppCompatImageView {
    float aspectRatio = 4.0f / 3.0f;
    public boolean shouldShrink = false;

    public ImageViewWithAspectRatio(Context context) {
        this(context, null);
    }

    public ImageViewWithAspectRatio(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageViewWithAspectRatio(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        int height;
        Drawable drawable = getDrawable();
        if (drawable != null) {
            height = (int) (width / aspectRatio);

            if (shouldShrink) {

                int imageWidth = drawable.getIntrinsicWidth();
                int neededHeight = (int) (imageWidth / aspectRatio);
                if (neededHeight > drawable.getIntrinsicHeight()) {
                    float newAspectRatio = drawable.getIntrinsicHeight() / (float) imageWidth;
                    height = (int) (width * newAspectRatio);
                    //when shrinking to fit, we set the background transparent to prevent 1 pixel borders
                    setBackgroundColor(Color.TRANSPARENT);
                }
            }
        } else {
            height = View.MeasureSpec.getSize(heightMeasureSpec);
        }
        setMeasuredDimension(width, height);
    }
}
