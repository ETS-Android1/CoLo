package com.example.colo.Announcements;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.colo.R;
import com.example.colo.Announcements.AnnouncementHelperClass;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

public class CreateAnnouncement extends AppCompatActivity {

    // Variable Setup
    EditText title, description;
    private Button create_announcement_btn;
    boolean flag = true;

    // Defines the announcementHelperClass and announcements list, which is not currently used
    AnnouncementHelperClass announcementHelperClass;
    ArrayList<String> announcements;
    RecyclerView recyclerView;
    AnnouncementAdapter myAdapter;
    ArrayList<AnnouncementList> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcements_page);

        // Sets the database reference to the "Announcements"
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Announcements");

        // Sets up the RecyclerView
        recyclerView = findViewById(R.id.announcement_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // Sets up the list, announcements list (not currently used), and adapter
        list = new ArrayList<>();
        announcements = new ArrayList<>();
        myAdapter = new AnnouncementAdapter(this,list);
        recyclerView.setAdapter(myAdapter);

        // Listens for data change in database and updates new entries to the list
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    AnnouncementList announcement = dataSnapshot.getValue(AnnouncementList.class);

                    list.add(announcement);
                }
                myAdapter.notifyDataSetChanged();

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}
