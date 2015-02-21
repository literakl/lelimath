package lelisoft.com.lelimath;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class CalcActivity extends Activity {


    public void digitClicked(View view) {
    }

    public void resultClicked(View view) {
    }

    public void decimalSeparatorClicked(View view) {
    }

    public void deleteClicked(View view) {
    }

    public void operandClicked(View view) {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calc);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_calc, menu);
        ActionBar actionBar = getActionBar();
//        actionBar.setDisplayShowHomeEnabled(true);setHomeButtonEnabled(true)
//        actionBar.setIcon(R.drawable.ic_launcher);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
