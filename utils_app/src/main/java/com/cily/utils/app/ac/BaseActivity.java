package com.cily.utils.app.ac;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cily.utils.app.Init;
import com.cily.utils.app.RxBus;
import com.cily.utils.app.event.Event;
import com.cily.utils.app.utils.L;
import com.cily.utils.app.utils.ToastUtils;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import rx.Subscription;
import rx.functions.Action1;


/**
 * user:cily
 * time:2017/1/16
 * desc:
 */
public class BaseActivity extends AppCompatActivity {
    protected final String TAG = this.getClass().getSimpleName();

    private Subscription mSubscription;
    private boolean resetEvent = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private boolean isResetEvent() {
        return resetEvent;
    }

    protected void setResetEvent(boolean reset) {
        this.resetEvent = reset;
    }

    protected void showToast(String str) {
        ToastUtils.showToast(this, str, Init.isShowToast());
    }

    protected void hideToast(){
        ToastUtils.hideToast();
    }

    protected void showToastNoDelay(String str){
        ToastUtils.showToastNoDelay(this, str, Init.isShowToast());
    }

    protected <V extends View> V findView(@IdRes int id){
        try{
            return (V)findViewById(id);
        }catch (ClassCastException e){
            L.e(TAG, "Could not cast View to concrete class");
            return null;
        }
    }

    protected Button findBtn(@IdRes int id){
        return findView(id);
    }

    protected TextView findTv(@IdRes int id){
        return findView(id);
    }

    protected ImageView findImg(@IdRes int id){
        return findView(id);
    }

    protected void debugToast(String str) {
        ToastUtils.showToast(this, str, Init.isDebug());
    }

    protected void toAc(Class<? extends Activity> c, Bundle b){
        if (c != null){
            Intent i = new Intent(this, c);

            if (b != null){
                i.putExtras(b);
            }

            startActivity(i);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

//        initRxBus();
    }

    @Override
    protected void onStop() {
        super.onStop();
//        unSubscription();
    }

    private void unSubscription(){
        if (mSubscription != null && !mSubscription.isUnsubscribed()) {
            mSubscription.unsubscribe();
        }
        mSubscription = null;
    }

    private void initRxBus() {
        if (mSubscription == null) {
            mSubscription = RxBus.getInstance()
                    .toObservable(Event.class)
                    .subscribe(new Action1<Event>() {
                        @Override
                        public void call(Event event) {
                            if (event == null) {
                                return;
                            }

                            if (event.what == Event.APP_EXIT) {
                                System.exit(0);
                                return;
                            }

                            doRxbus(event);

                            if (isResetEvent()) {
                                event.recycle();
                            }
                        }
                    });
        }
    }

    protected void doRxbus(Event e){}

    protected void toAcWithFinish(Class<? extends Activity> c, Bundle b){
        toAc(c, b);
        finish();
    }
}
