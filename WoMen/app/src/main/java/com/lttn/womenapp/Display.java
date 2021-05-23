package com.lttn.womenapp;
import androidx.appcompat.app.AppCompatActivity;

import android.accessibilityservice.GestureDescription;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.app.AlertDialog.Builder;
import android.widget.Button;

//display
public class Display extends Activity {

    Cursor c;
    Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
       // btn=findViewById(R.id.shake);
        // tao database
        SQLiteDatabase db;
        db=openOrCreateDatabase("NumDB", Context.MODE_PRIVATE, null);


        c=db.rawQuery("SELECT * FROM details", null);
        if(c.getCount()==0)
        {
            showMessage("Error", "No records found.");
            return;
        }
        StringBuffer buffer=new StringBuffer();
        while(c.moveToNext())
        {
            buffer.append("Name: "+c.getString(0)+"\n");
            buffer.append("Number: "+c.getString(1)+"\n");
        }
        showMessage("Details", buffer.toString());

    }

    public void showMessage(String title,String message)
    {
        Builder builder=new Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }




    public void back(View v) {
        Intent i_back=new Intent(Display.this,MainActivity.class);
        startActivity(i_back);

    }

}
