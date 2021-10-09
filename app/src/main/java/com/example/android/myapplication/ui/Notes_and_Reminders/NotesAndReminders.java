package com.example.android.myapplication.ui.Notes_and_Reminders;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.android.myapplication.DBOpenHelper;
import com.example.android.myapplication.NotesActivity;
import com.example.android.myapplication.R;
import com.example.android.myapplication.databinding.FragmentNotesBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class NotesAndReminders extends Fragment {

    private NotesAndReminderViewModel notesAndReminderViewModel;
    private FragmentNotesBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notesAndReminderViewModel =
                new ViewModelProvider(this).get(NotesAndReminderViewModel.class);

        binding = FragmentNotesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        return root;
    }



    public static String[] String_Array(ArrayList<String> arr)
    {
        String str[] = new String[arr.size()];
        for (int j = 0; j < arr.size(); j++)
            str[j] = arr.get(j);
        return str;
    }
    ArrayList<String> noteslist = new ArrayList<String>(); // Array that will have all the notes
    ArrayList<Integer> notesIDlist = new ArrayList<Integer>(); // Array that will have all the ID of the notes
    DBOpenHelper tdb; // Import database's assets
    SQLiteDatabase sdb; // Import database's assets
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tdb = new DBOpenHelper(getContext(), "test.db", null, 1);
        sdb = tdb.getReadableDatabase();

            // Prepare access to data
            String table_name = "test";
            String[] columns = {"ID", "TITLE_NAME","USER_ID", "DATA_NAME", "TIME_NAME"};
            FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            String userid = currentFirebaseUser.getUid().trim();
            String where_args[]=null;
            String group_by = null;
            String having = null;
            String order_by = null;


            //Cursor c = sdb.query(table_name, columns, where, where_args, group_by, having, order_by);
            Cursor c = sdb.rawQuery("SELECT * FROM " + table_name + " where USER_ID = '" +userid + "'" , null);

            // Add notes to the list
            c.moveToFirst();
            for (int x = 0; x < c.getCount(); x++)
            {
                noteslist.add(c.getString(2));

                notesIDlist.add(c.getInt(0));
                // Add the title and the ID of the note in the list
                c.moveToNext();
            }
            sdb.close();

            // Convert the array list to a string list
            String[] strlist = String_Array(noteslist);

            // Display the list of notes (using ListView)
            final ListView listofnotes = view.findViewById(R.id.listofnotes);
            final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                    android.R.layout.simple_list_item_1, strlist);
            listofnotes.setAdapter(adapter);

            // To know which note the user will select
            listofnotes.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {
                    // Get the ID of the note
                    long selectedNotes_long = parent.getItemIdAtPosition(position);
                    int selectedNotes = (int)selectedNotes_long;
                    String selectedID = notesIDlist.get(selectedNotes).toString();

                    // Display the new activity and send the ID of the selected note
                    Intent intent = new Intent(getContext(), NotesActivity.class);
                    intent.putExtra("selectedID", selectedID);

                    // Case for whether it is a new note or not
                    intent.putExtra("isNew", "0"); // 0 = false (edited note)


                    startActivity(intent);
                }
            });



            // Button that creates a new note (and call then NotesActivity)
            FloatingActionButton create = view.findViewById(R.id.create);
            create.setOnClickListener(new View.OnClickListener()
            {
                public void onClick(View v)
                {
                    Intent intent = new Intent(getContext(), NotesActivity.class);
                    String selectedID = "-1"; // it is a new note
                    intent.putExtra("selectedID", selectedID);
                    intent.putExtra("isNew", "1"); // 1 = true (new note)

                    startActivity(intent);
                }
            });


    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}