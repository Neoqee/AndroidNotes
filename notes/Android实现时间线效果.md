# 实现时间线效果

参考：掘金社区 作者：成活 的文章

 [又来新需求了，急，Android怎么实现时间线效果（上）？](https://juejin.im/post/5e8187b86fb9a03c5b2fb197)

 [又来新需求了，急，Android怎么实现时间线效果（下）？](https://juejin.im/post/5e843c716fb9a03c786ef9cb#heading-0)

GitHub：https://github.com/ddancn/TimelineDemo

## 实现方式

通过RecyclerView.ItemDecoration来实现，通过重写onDraw方法，实现画圆和线，之后通过getItemOffsets方法设置偏移量。

### onDraw

```java
@Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDraw(c, parent, state);

        int count = parent.getChildCount();
        for (int i = 0; i < count; i++) {
            View itemView = parent.getChildAt(i);
            float xPosition = radius + paddingLeft;
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
```

### getItemOffsets

```java
public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.left = (int) radius * 2 + paddingLeft + paddingRight;
    }
```

