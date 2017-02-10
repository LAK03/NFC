package com.example.coco.coconfctag;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class NfcMultiReader extends AppCompatActivity {

    private ListView listView;
    public static TextView tv_total;
    private String names[] = {
            "1001",
            "1002",
            "1003",
            "1004",
            "1001",
            "1002",
            "1003",
            "1004",
            "1001",
            "1002",
            "1003",
            "1004",
            "1001",
            "1002",
            "1003",
            "1004",
            "1001",
            "1002",
            "1003",
            "1004",
            "1001",
            "1002",
            "1003",
            "1004",
            "1001",
            "1002",
            "1003",
            "1004",
            "1001",
            "1002",
            "1003",
            "1004",
            "1001",
            "1002",
            "1003",
            "1004",
            "1001",
            "1002",
            "1003",
            "1004"
    };


    private String desc[] = {
            "Dove Soap",
            "Washing Power",
            "Toys",
            "Cooking Oil",
            "Dove Soap",
            "Washing Power",
            "Toys",
            "Cooking Oil",
            "Dove Soap",
            "Washing Power",
            "Toys",
            "Cooking Oil",
            "Dove Soap",
            "Washing Power",
            "Toys",
            "Cooking Oil",
            "Dove Soap",
            "Washing Power",
            "Toys",
            "Cooking Oil",
            "Dove Soap",
            "Washing Power",
            "Toys",
            "Cooking Oil",
            "Dove Soap",
            "Washing Power",
            "Toys",
            "Cooking Oil",
            "Dove Soap",
            "Washing Power",
            "Toys",
            "Cooking Oil",
            "Dove Soap",
            "Washing Power",
            "Toys",
            "Cooking Oil",
            "Dove Soap",
            "Washing Power",
            "Toys",
            "Cooking Oil"
    };

    private String price[] = {
            " $2.10c",
            " $1.10c",
            " $2.99c",
            " $5.50c",
            " $2.10c",
            " $1.10c",
            " $2.99c",
            " $5.50c",
            " $2.10c",
            " $1.10c",
            " $2.99c",
            " $5.50c",
            " $2.10c",
            " $1.10c",
            " $2.99c",
            " $5.50c",
            " $2.10c",
            " $1.10c",
            " $2.99c",
            " $5.50c",
            " $2.10c",
            " $1.10c",
            " $2.99c",
            " $5.50c",
            " $2.10c",
            " $1.10c",
            " $2.99c",
            " $5.50c",
            " $2.10c",
            " $1.10c",
            " $2.99c",
            " $5.50c",
            " $2.10c",
            " $1.10c",
            " $2.99c",
            " $5.50c",
            " $2.10c",
            " $1.10c",
            " $2.99c",
            " $5.50c"
    };

    private Integer imageid[] = {
            //R.drawable.html,
            //R.drawable.css,
            //R.drawable.js,
            //R.drawable.wp
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc_multi_reader);
        tv_total= (TextView) findViewById(R.id.textView);
        CustomList customList = new CustomList(this, names, desc,price);

        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(customList);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getApplicationContext(),"You Clicked "+names[i],Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
