package com.example.eyepagination;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.squareup.picasso.Picasso;

public class eyezoom extends AppCompatActivity {
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eyezoom);
        imageView=findViewById(R.id.zoom);
        Bundle bundle2=getIntent().getExtras();
        if(bundle2!=null){
            Picasso.get().load( bundle2.getString( "eye")).into(imageView);
        }
        else {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
