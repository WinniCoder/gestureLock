package com.example.wuhui.gesturelock;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wuhui on 2016/12/15.
 */

public class GestureLockView extends View {

    private float width;
    private Paint initPaint, choosenPaint;
    private static final int STATUS_DEFAULT = 0;
    private static final int STATUS_DRAWING = 1;
    private static final int STATUS_PAS_RIGHT = 2;
    private static final int STATUS_PAS_WRONG = 3;
    public static final int STATUS_REINPUT_PAS = 4;
    private static int currentStatus = STATUS_DEFAULT;
    private List<Integer> list = new ArrayList<Integer>();
    private List<Integer> passList = new ArrayList<Integer>();
    private SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
    SharedPreferences.Editor editor = preferences.edit();
    private float x, y;
    private float offset;
    private float baseXPos;
    private float baseYPos = 200;
    Vibrator vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);

    public GestureLockView(Context context) {
        super(context);
    }

    public GestureLockView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        SetGestureActivity.statusChange.setStatus(currentStatus);

        passList = stringToList(preferences.getString("gesture", ""));

        initPaint = new Paint();
        initPaint.setStyle(Paint.Style.STROKE);
        initPaint.setStrokeWidth(10);
        initPaint.setAntiAlias(true);

        choosenPaint = new Paint();
        choosenPaint.setStyle(Paint.Style.STROKE);
        choosenPaint.setAntiAlias(true);
        choosenPaint.setStrokeWidth(10);


    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int mwidth = MeasureSpec.getSize(widthMeasureSpec);
        int mheight = mwidth - getPaddingLeft() - getPaddingRight() + getPaddingTop() + getPaddingBottom();
        setMeasuredDimension(mwidth, mheight);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        //绘制初始图案
        width = getWidth();
        offset = width / 3;
        baseXPos = width / 6;

        switch (currentStatus) {
            case STATUS_DEFAULT:
                initPaint.setColor(Color.parseColor("#BED0F7"));
                for (int i = 0; i < 9; i++) {
                    canvas.drawCircle(baseXPos + i / 3 * offset, baseYPos + i % 3 * offset, 80, initPaint);
                }
                break;
            case STATUS_DRAWING:
                for (int i = 0; i < 9; i++) {
                    float pointX = baseXPos + i / 3 * offset;
                    float pointY = baseYPos + i % 3 * offset;
                    if (!list.contains(i)) {
                        if (x >= pointX - 80 && x <= pointX + 80 && y >= pointY - 80 && y <= pointY + 80) {
                            int n = list.size();
                            if (n > 0) {
                                if (i / 3 == list.get(n - 1) / 3 && Math.abs(i % 3 - list.get(n - 1) % 3) == 2 && !list.contains(i - 1 / 3 * 3 + 1)) {
                                    list.add(i / 3 * 3 + 1);
                                } else if (i % 3 == list.get(n - 1) % 3 && Math.abs(i / 3 - list.get(n - 1) / 3) == 2 && !list.contains(3 + i % 3)) {
                                    list.add(3 + i % 3);
                                } else if (Math.abs(i / 3 - list.get(n - 1) / 3) == 2 && Math.abs(i % 3 - list.get(n - 1) % 3) == 2 && !list.contains(4)) {
                                    list.add(4);
                                }
                            }
                            list.add(i);
                            vibrator.vibrate(50);
                            initPaint.setColor(Color.parseColor("#4F7FE8"));
                            initPaint.setStyle(Paint.Style.FILL);
                            canvas.drawCircle(pointX, pointY, 30, initPaint);
                        } else {
                            initPaint.setColor(Color.parseColor("#BED0F7"));
                        }
                    } else {
                        initPaint.setColor(Color.parseColor("#4F7FE8"));
                        initPaint.setStyle(Paint.Style.FILL);
                        canvas.drawCircle(pointX, pointY, 30, initPaint);
                    }
                    initPaint.setStyle(Paint.Style.STROKE);
                    canvas.drawCircle(pointX, pointY, 80, initPaint);
                }
                if (list.size() > 0) {
                    choosenPaint.setColor(Color.parseColor("#4F7FE8"));
                    Path path = new Path();
//                    path.addCircle(baseXPos + list.get(0) / 3 * offset, baseYPos + list.get(0) % 3 * offset, 30, Path.Direction.CW);
                    path.moveTo(baseXPos + list.get(0) / 3 * offset, baseYPos + list.get(0) % 3 * offset);
                    for (int i = 1; i < list.size(); i++) {
                        path.lineTo(baseXPos + list.get(i) / 3 * offset, baseYPos + list.get(i) % 3 * offset);
//                        path.addCircle(baseXPos + list.get(i) / 3 * offset, baseYPos + list.get(i) % 3 * offset, 30, Path.Direction.CW);
                    }
                    path.lineTo(x, y);
                    canvas.drawPath(path, choosenPaint);
                }
                break;
            case STATUS_PAS_WRONG:
                for (int i = 0; i < 9; i++) {
                    float pointX = baseXPos + i / 3 * offset;
                    float pointY = baseYPos + i % 3 * offset;
                    if (!list.contains(i)) {
                        initPaint.setColor(Color.parseColor("#BED0F7"));
                    } else {
                        initPaint.setColor(Color.parseColor("#FE4C40"));
                        initPaint.setStyle(Paint.Style.FILL);
                        canvas.drawCircle(pointX, pointY, 30, initPaint);
                    }
                    initPaint.setStyle(Paint.Style.STROKE);
                    canvas.drawCircle(pointX, pointY, 80, initPaint);
                }
                if (list.size() > 0) {
                    choosenPaint.setColor(Color.parseColor("#FE4C40"));
                    Path path = new Path();
                    path.moveTo(baseXPos + list.get(0) / 3 * offset, baseYPos + list.get(0) % 3 * offset);
                    for (int i = 1; i < list.size(); i++) {
                        path.lineTo(baseXPos + list.get(i) / 3 * offset, baseYPos + list.get(i) % 3 * offset);
                    }
                    canvas.drawPath(path, choosenPaint);
                }
                list.clear();
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        currentStatus = STATUS_DEFAULT;
                        invalidate();
                    }
                }, 500);

        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                handleActionMove(event);
                break;
            case MotionEvent.ACTION_MOVE:
                handleActionMove(event);
                break;
            case MotionEvent.ACTION_UP:
                handleActionUp();
                break;
            default:
                return false;
        }
        return true;
    }

    private void handleActionMove(MotionEvent event) {
        x = event.getX();
        y = event.getY();
        currentStatus = STATUS_DRAWING;
        invalidate();
    }

    private void handleActionUp() {
        if (passList.isEmpty()) {
            if (list.size() < 4) {
                Toast.makeText(getContext(), "请至少连接四个点", Toast.LENGTH_SHORT).show();
            } else {
                passList.addAll(list);
                currentStatus = STATUS_REINPUT_PAS;
                SetGestureActivity.statusChange.setStatus(currentStatus);
            }
            list.clear();
            currentStatus = STATUS_DEFAULT;
            invalidate();
        } else {
            if (!list.equals(passList)) {
                currentStatus = STATUS_PAS_WRONG;
                vibrator.vibrate(200);
                invalidate();
            } else {
                currentStatus = STATUS_PAS_RIGHT;
                if (TextUtils.isEmpty(preferences.getString("gesture", ""))) {
                    editor.putString("gesture", listToString(passList));
                    editor.commit();
                }
//                Log.i("wuhuigesture",preferences.getString("gesture", ""));
                SetGestureActivity.statusChange.setStatus(currentStatus);
                currentStatus = STATUS_DEFAULT;
            }
        }
    }

    public String listToString(List<Integer> list) {
        String s = "";
        for (int i = 0; i < list.size(); i++) {
            s = s + list.get(i) + " ";
        }
        return s;
    }

    public List<Integer> stringToList(String s) {
        List<Integer> list = new ArrayList<Integer>();
        String array[] = s.split(" ");
        for (int i = 0; i < array.length; i++) {
            if (!TextUtils.isEmpty(array[i])) {
                list.add(Integer.parseInt(array[i]));
            }
        }
        return list;
    }
}
