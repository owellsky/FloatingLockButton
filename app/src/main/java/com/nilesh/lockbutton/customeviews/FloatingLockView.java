package com.nilesh.lockbutton.customeviews;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import com.nilesh.lockbutton.R;
import com.nilesh.lockbutton.utils.AnimUtils;

/**
 * Created by Nilesh.Pawate on 05/02/2018.
 */

public class FloatingLockView extends FrameLayout {

    private View transView;
    private View lockIcon;
    private LayoutParams params;

    public FloatingLockView(Context context) {
        super(context);
        initialise(context, null);
    }

    public FloatingLockView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialise(context, attrs);
    }

    public FloatingLockView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialise(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public FloatingLockView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialise(context, attrs);
    }

    public void attachToScreen(Activity activity) {
        // We get the View of the Activity
        View content = (View) activity.findViewById(android.R.id.content).getParent();
        ViewGroup parent = (ViewGroup) content.getParent();
        parent.addView(this);
    }

    public void updateView() {

        params = new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        if (rememberedLeft != 0 && rememberedTop != 0) {
            params.leftMargin = rememberedLeft;
            params.topMargin = rememberedTop;
            lockIcon.setLayoutParams(params);
        } else {
            lockIcon.getViewTreeObserver().addOnGlobalLayoutListener(
                    new ViewTreeObserver.OnGlobalLayoutListener() {
                        public void onGlobalLayout() {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                lockIcon.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            } else {
                                lockIcon.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                            }
                            params.leftMargin = (int) lockIcon.getX();
                            params.topMargin = (int) lockIcon.getY();
                            lockIcon.setLayoutParams(params);
                        }
                    }
            );
        }
    }

    public void initialise(Context context, AttributeSet attrs) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.layout_include_lock_view, this, true);
        lockIcon = rootView.findViewById(R.id.lock_icon);
        transView = rootView.findViewById(R.id.t_view);
        updateView();
        lockIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lockIcon.isSelected()) {
                    unlock();
                } else {
                    lock();
                }
            }
        });
//        lockIcon.setOnTouchListener(touchListener);
        lockIcon.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
//                params = new FrameLayout.LayoutParams(
//                        FrameLayout.LayoutParams.WRAP_CONTENT,
//                        FrameLayout.LayoutParams.WRAP_CONTENT);
                AnimUtils.scaleViewAnim(v, 1.5f, 1.5f, 1.5f, 1.5f);

                lockIcon.setOnTouchListener(touchListener);

                return false;
            }
        });
        final FrameLayout parent = findViewById(R.id.parent_view);
        parent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                availableHeight = parent.getHeight();
                availableWidth = parent.getWidth();
                if (availableHeight > 0) {
                    parent.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            }
        });

        lockIcon.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                lockHeight = lockIcon.getHeight();

                if (lockHeight > 0) {
                    lockIcon.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            }
        });
    }

    OnTouchListener touchListener = new OnTouchListener() {
        private int lastAction;
        private int initialX;
        private int initialY;
        private float initialTouchX;
        private float initialTouchY;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
//                    params = new FrameLayout.LayoutParams(
//                            FrameLayout.LayoutParams.WRAP_CONTENT,
//                            FrameLayout.LayoutParams.WRAP_CONTENT);

                    initialX = params.leftMargin;
                    initialY = params.topMargin;
//                    initialX = v.getX();
//                    initialY= v.getY();
//                    initialX = params.leftMargin;
//                    initialY = params.topMargin;

                    initialTouchX = event.getRawX();
                    initialTouchY = event.getRawY();
                    lastAction = event.getAction();
                    return true;
                case MotionEvent.ACTION_UP:
                    if (lastAction == MotionEvent.ACTION_DOWN) {
                        if (lockIcon.isSelected()) {
                            unlock();
                        } else {
                            lock();
                        }
                    } else {
                        AnimUtils.scaleViewAnim(v, 1f, 1f, 1f, 1f);

                        lockIcon.setOnTouchListener(null);
//                        params = new FrameLayout.LayoutParams(
//                                FrameLayout.LayoutParams.WRAP_CONTENT,
//                                FrameLayout.LayoutParams.WRAP_CONTENT);

                        if (params.leftMargin <= 0) {
                            params.leftMargin = 30;
                        }
                        if (params.topMargin <= 0) {
                            params.topMargin = 30;
                        }
                        if (params.topMargin >= availableHeight - lockHeight) {
                            params.topMargin = availableHeight - lockHeight - 30;
                        }
                        if (params.leftMargin >= availableWidth - lockHeight) {
                            params.leftMargin = availableWidth - lockHeight - 30;
                        }
                        rememberedLeft = params.leftMargin;
                        rememberedTop = params.topMargin;
                        lockIcon.setLayoutParams(params);
                    }

                    lastAction = event.getAction();
                    return true;
                case MotionEvent.ACTION_MOVE:
                    Log.d("Parems", lockHeight + ":Lock height:" + availableWidth + ":Left:" + params.leftMargin + " :top:" + params.topMargin + ":right:" + params.rightMargin + ":params.bottomMargin:" + params.bottomMargin);
                    if (params.leftMargin >= 0 && params.topMargin >= 0 && params.rightMargin >= 0 &&
                            params.bottomMargin >= 0 && params.topMargin < availableHeight - lockHeight && params.leftMargin < availableWidth - lockHeight) {
                        isMove = true;
                        params.leftMargin = initialX + (int) (event.getRawX() - initialTouchX) - lockHeight;
                        params.topMargin = initialY + (int) (event.getRawY() - initialTouchY) - lockHeight;

//                        params.leftMargin = (int)(initialX + (event.getRawX() - initialTouchX));
//                        params.topMargin = (int)(initialY +  (event.getRawY() - initialTouchY));

                        lastAction = event.getAction();
                        lockIcon.setLayoutParams(params);
                    } else {
                        lastAction = event.getAction();
                        if (params.leftMargin <= 0) {
                            params.leftMargin = 30;
                        }
                        if (params.topMargin <= 0) {
                            params.topMargin = 30;
                        }
                        if (params.topMargin >= availableHeight - lockHeight) {
                            params.topMargin = availableHeight - lockHeight - 30;
                        }
                        if (params.leftMargin >= availableWidth - lockHeight) {
                            params.leftMargin = availableWidth - lockHeight - 30;
                        }
                    }
                    return true;
            }
            return false;
        }
    };

    boolean isMove = false;
    int availableHeight;
    int availableWidth;
    int lockHeight;
    static int rememberedLeft = 0;
    static int rememberedTop = 0;

    public void lock() {
        lockIcon.setSelected(true);
        transView.setVisibility(VISIBLE);
        lockIcon.setTag("close");
    }

    public void unlock() {
        lockIcon.setSelected(false);
        transView.setVisibility(GONE);
        lockIcon.setTag("open");
    }

    public boolean isLocked() {
        return lockIcon.isSelected();
    }

}
