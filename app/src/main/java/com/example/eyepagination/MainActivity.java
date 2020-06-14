package com.example.eyepagination;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.shreyaspatil.firebase.recyclerpagination.DatabasePagingOptions;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ImageAdapter.OnItemClickListener{
    FloatingActionButton fab;
    androidx.recyclerview.widget.RecyclerView RecyclerView;
    ImageAdapter mAdapter;
    FirebaseStorage mStorage;
    DatabaseReference mDatabaseRef;
    private List<Upload> mUploads;
    private ValueEventListener mDBListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // upload button takes you to upload page
        fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, eyeupload.class);
                startActivity(intent);
            }
        });

        //  loading all the pics and description from the firebase database
        RecyclerView = findViewById(R.id.recycler_view);
        RecyclerView.setHasFixedSize(true);
        RecyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
        mUploads = new ArrayList<>();
        mStorage = FirebaseStorage.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("stag");
       

        // following the steps from blog here firebase pagiation

        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(5)
                .setPageSize(10)
                .build();

        DatabasePagingOptions<Upload> options = new DatabasePagingOptions.Builder<Upload>()
                .setLifecycleOwner(this)
                .setQuery(mDatabaseRef, config, Upload.class)
                .build();



        // dont know what to do from here


        




        mDBListener = mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUploads.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Upload upload = postSnapshot.getValue(Upload.class);
                    upload.setKey(postSnapshot.getKey());
                    mUploads.add(upload);
                }
                mAdapter = new ImageAdapter(MainActivity.this, mUploads);
                mAdapter.setOnItemClickListener(MainActivity.this);
                RecyclerView.setAdapter(mAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

  // when activity is closed I.e app is closed
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDatabaseRef.removeEventListener(mDBListener);
    }
    // zoom the image you click & and sends you to new activity
    @Override
    public void onItemClick(String position) {
        Intent intent =new Intent(MainActivity.this,eyezoom.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("eye",position);
        startActivity(intent);
    }
    // on report cicked
    @Override
    public void onWhatEverClick(int position) {
        Toast.makeText(this, "Reported", Toast.LENGTH_SHORT).show();
    }

    // delete the image
    @Override
    public void onDeleteClick(int position) {
        Upload selectedItem = mUploads.get(position);
        final String selectedKey = selectedItem.getKey();
        StorageReference imageRef = mStorage.getReferenceFromUrl(selectedItem.getImageUrl());
        imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mDatabaseRef.child(selectedKey).removeValue();
                Toast.makeText(MainActivity.this, "Item deleted", Toast.LENGTH_SHORT).show();
            }
        });
    }
}