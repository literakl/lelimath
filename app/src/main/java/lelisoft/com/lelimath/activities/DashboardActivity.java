package lelisoft.com.lelimath.activities;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.view.View;
import android.widget.TextView;

import lelisoft.com.lelimath.R;

public class DashboardActivity extends LeliBaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.main_collapsing);
        collapsingToolbarLayout.setTitle(getString(R.string.app_name));

//        Toolbar toolbar = (Toolbar) findViewById(R.id.main.appbar);
//        setSupportActionBar(toolbar);

        TextView button = (TextView) findViewById(R.id.main_button_puzzle);
        button.setOnClickListener(this);
        button = (TextView) findViewById(R.id.main_button_calc);
        button.setOnClickListener(this);
        button = (TextView) findViewById(R.id.main_button_settings);
        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_button_puzzle:
                PuzzleActivity.start(this);
                break;

            case R.id.main_button_calc:
//                Toast.makeText(this, "Již brzy", Toast.LENGTH_LONG).show();
                CalcActivity.start(this);
                break;

            case R.id.main_button_settings:
                GamePreferenceActivity.start(this);
                break;
        }
    }
}
