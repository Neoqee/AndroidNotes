package com.neoqee.javalib;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.neoqee.commonlib.system.SystemManager;
import com.neoqee.javalib.databinding.ActivitySearchBinding;

import java.lang.reflect.Field;

public class SearchActivity extends AppCompatActivity {

    private ActivitySearchBinding binding;
    private final static String tag = "SearchActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SearchView.SearchAutoComplete searchTextView  = binding.searchView.findViewById(R.id.search_src_text);
        try {
            Field mCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
            mCursorDrawableRes.setAccessible(true);
            mCursorDrawableRes.set(searchTextView,R.drawable.cursor_black_searchview);

        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        //切换显示方式
        binding.searchView.setIconified(false);
        //展开
        binding.searchView.setIconifiedByDefault(true);
        binding.searchView.onActionViewExpanded();
        binding.searchView.clearFocus();
        binding.searchView.setSubmitButtonEnabled(true);

        //监听focus状态
        binding.searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Log.i(tag,"focus:" + hasFocus);
            }
        });
        binding.searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(tag,"onClick:search");
            }
        });
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.i(tag,"onQueryTextSubmit:" + query);
                SystemManager.hideSoftInputWindow(SearchActivity.this,binding.searchView);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.i(tag,"onQueryTextChange:" + newText);
                return true;
            }
        });
        binding.searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                Log.i(tag,"onClose");
                return true;
            }
        });

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN){
            View currentFocus = getCurrentFocus();
            if (isClearEditTextFocus(currentFocus,ev)){
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (null != imm) {
                    imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    currentFocus.clearFocus();
                }
            }
            if (isClearSearchViewFocus(currentFocus,ev)){
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (null != imm) {
                    imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    currentFocus.clearFocus();
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        return getWindow().superDispatchTouchEvent(ev) || onTouchEvent(ev);
    }

    private static boolean isClearEditTextFocus(View view, MotionEvent event) {
        if ((view instanceof EditText)) {
            int[] leftTop = {0, 0};
            view.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + view.getHeight();
            int right = left + view.getWidth();
            return !(event.getY() > top && event.getY() < bottom && event.getX() > left && event.getX() < right);
        }
        return false;
    }
    private static boolean isClearSearchViewFocus(View view, MotionEvent event) {
        if ((view instanceof SearchView)) {
            int[] leftTop = {0, 0};
            view.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + view.getHeight();
            int right = left + view.getWidth();
            return !(event.getY() > top && event.getY() < bottom && event.getX() > left && event.getX() < right);
        }
        return false;
    }

}
