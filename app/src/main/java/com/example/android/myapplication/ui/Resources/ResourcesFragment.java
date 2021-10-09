package com.example.android.myapplication.ui.Resources;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.example.android.myapplication.MainActivity;
import com.example.android.myapplication.Notes;
import com.example.android.myapplication.NotesActivity;
import com.example.android.myapplication.R;
import com.example.android.myapplication.User;
import com.example.android.myapplication.databinding.FragmentResourcesBinding;
import com.example.android.myapplication.databinding.FragmentResourcesBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ResourcesFragment extends Fragment {

    private ResourcesViewModel resourcesViewModel;
    private FragmentResourcesBinding binding;
    String T = null;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        resourcesViewModel =
                new ViewModelProvider(this).get(ResourcesViewModel.class);
        binding = FragmentResourcesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return root;
    }

    String title_Array[];
    String data_Array[];

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        //get current user type
        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String userid3 = currentFirebaseUser.getUid().trim();
        FirebaseDatabase database1 = FirebaseDatabase.getInstance("https://digital-diary-5902b-default-rtdb.asia-southeast1.firebasedatabase.app/");
        DatabaseReference myRef = database1.getReference("Users");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                User currrentdUser = null;
                //Log.w("ssssss","student");
                for (DataSnapshot user : snapshot.getChildren()) {
                    //this is all you need to get a specific user by Uid
                    if (user.getKey().equals(userid3)) {
                        currrentdUser = user.getValue(User.class);
                            //get the data
                            List<String> title_list = new ArrayList<>();
                            List<String> data_list = new ArrayList<>();
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference reference = database.getReference("share");
                            reference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                        Notes score = ds.getValue(Notes.class);



                                            String userTitle = score.getTitle();
                                            String userData = score.getData();
                                            String userid = score.getUser_id();

                                            title_list.add(userTitle);
                                            data_list.add(userData);




                                    }

                                    final ListView listofnotes = view.findViewById(R.id.listofnotes1);
                                    title_Array = new String[title_list.size()];
                                    data_Array = new String[data_list.size()];
                                    for (int j = 0; j < title_list.size(); j++) {
                                        title_Array[j] = title_list.get(j);
                                        data_Array[j] = data_list.get(j);
                                    }


                                    final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                                            android.R.layout.simple_list_item_1, title_Array);
                                    listofnotes.setAdapter(adapter);

                                    listofnotes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            String clicked_titile = title_Array[position];
                                            String clicked_data = data_Array[position];
                                            Intent intent = new Intent(getContext(), NotesActivity.class);
                                            intent.putExtra("title", clicked_titile);
                                            intent.putExtra("data", clicked_data);

                                            // Case for whether it is a new note or not
                                            intent.putExtra("isNew", "1"); // 0 = false (edited note)
                                            intent.putExtra("selectedID", "-2");

                                            startActivity(intent);
                                        }
                                    });
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}