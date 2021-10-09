package com.example.android.myapplication;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.util.Date;

public class NotesActivity extends Activity {
    private DBOpenHelper tdb;
    private SQLiteDatabase sdb;

    public String db_title;
    public String db_data;
    public String db_time;

    public int isnew;
    public int db_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notes_activity);

        activeShareButton();

        // Buttons
        final FloatingActionButton save = findViewById(R.id.save);
        final FloatingActionButton returnlist = findViewById(R.id.returnlist);
        final FloatingActionButton delete = findViewById(R.id.delete);
        final FloatingActionButton share = findViewById(R.id.share);

        final EditText title = findViewById(R.id.titleNotes);
        final EditText data = findViewById(R.id.dataNotes);

        tdb = new DBOpenHelper(this, "test.db", null, 1);
        sdb = tdb.getWritableDatabase();

        //get intent for edit note or creat new note
        final Intent ActivityIntent = getIntent();
        final String selectedID = ActivityIntent.getStringExtra("selectedID");
        final String isNew = ActivityIntent.getStringExtra("isNew");

        final String title_ =  ActivityIntent.getStringExtra("title");
        final String data_ =  ActivityIntent.getStringExtra("data");
        Log.e("xxxux",title_+data_+isNew);
        db_id = Integer.parseInt(selectedID);
        isnew = Integer.parseInt(isNew);


        if (db_id == -1) {
            // Creating a new note
            //Toast.makeText(getApplicationContext(),"New note created",Toast.LENGTH_SHORT).show();
        }
        else if (db_id == -2) {
            db_title = title_ ;
            db_data = data_;
            db_time = DateFormat.getDateTimeInstance().format(new Date());
            title.setText(db_title);
            data.setText(db_data);
        } // Case where the user wants to create a new note
        else {

            FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            String userid3 = currentFirebaseUser.getUid().trim();
            // Will recover the data from the note
            Cursor c = sdb.rawQuery("SELECT * FROM " + "test" + " where USER_ID = '" +userid3 + "'" , null);

            int x = 0;
            c.moveToFirst();
            while (x < c.getCount() && c.getInt(0) != db_id) {
                c.moveToNext();
                x++;
            }
            db_id = c.getInt(0); // Current ID

            db_title = c.getString(2);
            db_data = c.getString(3);
            db_time = c.getString(4);
            title.setText(db_title);
            data.setText(db_data);

            // Display the date/time of the last update of the note
            //Toast.makeText(getApplicationContext(), "Last update : " + db_time, Toast.LENGTH_SHORT).show();
        } // Case where the user wants to edit an existing note

        // Button that will save the note
        save.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //create note object.
                FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                String userid = currentFirebaseUser.getUid().trim();
                String title_text = title.getText().toString();
                String data_text = data.getText().toString();
                String time_text = DateFormat.getDateTimeInstance().format(new Date());

                /*
                Notes notes = new Notes(title_text,data_text,time_text);
                DatabaseReference myReff = FirebaseDatabase.getInstance().getReference("Users").child(userid).child("Notes");
                String key = myReff.push().getKey();
                myReff.child(key).setValue(notes);*/

                if (isnew == 1) {
                    // Saving the new note
                    ContentValues newnote = new ContentValues();
                    newnote.put("TITLE_NAME", title_text);
                    newnote.put("DATA_NAME", data_text);
                    newnote.put("TIME_NAME", time_text);
                    String userid1 = currentFirebaseUser.getUid().trim();
                    newnote.put("USER_ID",userid1);
                    sdb.insert("test", null, newnote);
                    isnew = 0;
                }
                else {
                    ContentValues editednote = new ContentValues();
                    editednote.put("TITLE_NAME", title_text);
                    editednote.put("DATA_NAME", data_text);
                    editednote.put("TIME_NAME", time_text);
                    String userid2 = currentFirebaseUser.getUid().trim();
                    editednote.put("USER_ID",userid2);
                    sdb.update("test", editednote, "ID=" + db_id, null);
                }
                Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();
            }
        });

        // Button that will return to MainActivity
        returnlist.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(NotesActivity.this, MainActivity.class);
                sdb.close();
                finish();
                startActivity(intent);
            }
        });

        // Button that will delete the current note and return to MainActivity
        delete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(NotesActivity.this, MainActivity.class);

                sdb.delete("test", "ID=" + db_id, null);
                sdb.close();
                finish();
                startActivity(intent);
            }
        });


        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //create note object.
                FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                String userid = currentFirebaseUser.getUid().trim();
                String title_text = title.getText().toString();
                String data_text = data.getText().toString();
                String time_text = DateFormat.getDateTimeInstance().format(new Date());
                Notes notes = new Notes(title_text, data_text, time_text,currentFirebaseUser.getUid().trim());
                DatabaseReference myReff = FirebaseDatabase.getInstance().getReference("share");
                String key = myReff.push().getKey();
                myReff.child(key).setValue(notes);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        sdb.close();
        finishActivity(1);
    }

    private void activeShareButton() {
        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userid = user.getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://digital-diary-5902b-default-rtdb.asia-southeast1.firebasedatabase.app/");
        DatabaseReference myRef = database.getReference("Users");
        User currrentdUser = null;

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                User currrentdUser = null;
                for (DataSnapshot user : snapshot.getChildren()) {
                    //this is all you need to get a specific user by Uid
                    if (user.getKey().equals(userid)) {
                        currrentdUser = user.getValue(User.class);
                    }
                }
                FloatingActionButton floatingActionButton = findViewById(R.id.share);

                Log.d("zzzzzzzzz", currrentdUser.getType().getClass().getSimpleName());
                if (currrentdUser.getType().equals("Teacher")) {
                    floatingActionButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
}
