package com.lypeer.zybuluo.mixture.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.lypeer.zybuluo.R;
import com.lypeer.zybuluo.mixture.core.MixtureKeys;


public class NavigatorActivity extends AppCompatActivity {

    private final String TAG = NavigatorActivity.class.getSimpleName();
    private EditText mVideoUrl;
    private EditText mDataUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_layout);
        Button buttonOk = (Button) findViewById(R.id.btn_mixture_ok);
        mVideoUrl = (EditText) findViewById(R.id.et_mixture_video_url);
        mDataUrl = (EditText) findViewById(R.id.et_mixture_json_url);
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(MixtureKeys.KEY_VIDEO_PATH, mVideoUrl.getText().toString());
                intent.putExtra(MixtureKeys.KEY_DATA_PATH, mDataUrl.getText().toString());
                intent.setClass(NavigatorActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
