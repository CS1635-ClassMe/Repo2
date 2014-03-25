package com.cs1635.classme;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

public class SearchActivity extends ActionBarActivity {
    SearchActivity activity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);

        //find button set listener
        Button submitSearch = (Button) findViewById(R.id.search_submit);

        submitSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, ResultsActivity.class);
                startActivity(intent);
            }
        });


        //call new intent in lister callback
    }



    public void onCheckboxClicked(View view){
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        /*switch(view.getId()) {
            case R.id.search_filter_activecourse:
                if (checked)
                // Put some meat on the sandwich
                else
                // Remove the meat
                break;
            case R.id.checkbox_cheese:
                if (checked)
                // Cheese me
                else
                // I'm lactose intolerant
                break;
            // TODO: Veggie sandwich
        }*/

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
