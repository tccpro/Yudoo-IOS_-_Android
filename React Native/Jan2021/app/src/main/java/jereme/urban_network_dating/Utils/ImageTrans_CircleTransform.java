package jereme.urban_network_dating.Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;

import com.squareup.picasso.Transformation;

public class ImageTrans_CircleTransform implements Transformation {
    @Override
    public Bitmap transform(Bitmap source) {
        if (source == null || source.isRecycled()) {
            return null;
        }
        int borderwidth =0;

        final int width = source.getWidth() + borderwidth;
        final int height = source.getHeight() + borderwidth;

        Bitmap canvasBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        BitmapShader shader = new BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(shader);

        Canvas canvas = new Canvas(canvasBitmap);
        float radius = width > height ? ((float) height) / 2f : ((float) width) / 2f;
        canvas.drawCircle(width / 2, height / 2, radius, paint);

        //border code
        paint.setShader(null);
        paint.setStyle(Paint.Style.STROKE);

        paint.setStrokeWidth(borderwidth);
        canvas.drawCircle(width / 2, height / 2, radius - borderwidth / 2, paint);
        //--------------------------------------

        if (canvasBitmap != source) {
            source.recycle();
        }

        return canvasBitmap;
    }
    @Override
    public String key() {
        return "circle";
    }
}