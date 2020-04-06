package com.neoqee.timelinedemo;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FirstVerTimeLine extends RecyclerView.ItemDecoration {

    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private float radius = 16f;
    private int offset = 15;
    private int paddingLeft = 24;
    private int paddingRight = 24;

    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDraw(c, parent, state);

        int count = parent.getChildCount();
        for (int i = 0; i < count; i++) {
            View itemView = parent.getChildAt(i);
            float xPosition = radius + paddingLeft;
//            if (i != 0){
//                c.drawLine(xPosition,(float) itemView.getTop(),
//                        xPosition,(float) itemView.getTop() + offset,paint);
//            }
            //设置线的大小
            paint.setStrokeWidth(5);
            //画线 圆往下
            if (i != count - 1){
                c.drawLine(xPosition, itemView.getTop()  + radius * 2 + offset + 8,
                        xPosition, (float) itemView.getBottom(),paint);
            }
            //画圆 设置为空心圆
            paint.setStyle(Paint.Style.STROKE);
            c.drawCircle(xPosition, itemView.getTop() + offset + radius, radius, paint);
        }
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.left = (int) radius * 2 + paddingLeft + paddingRight;
    }
}
