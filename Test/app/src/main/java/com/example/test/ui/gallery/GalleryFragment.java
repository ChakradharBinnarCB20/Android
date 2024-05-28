package com.example.test.ui.gallery;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.example.test.R;


public class GalleryFragment extends AppCompatActivity {

    Button btnGal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.fragment_gallery);

        btnGal =findViewById(R.id.btnGallary);

        btnGal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(GalleryFragment.this, "You pressed gallary button", Toast.LENGTH_SHORT).show();
            }
        });



    }
}