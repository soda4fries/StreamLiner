package com.example.streamliner;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

public class ViewProfile extends Fragment {

    private TextView tvName, tvBirthday,tvPhoneNo,tvEmail;
    private Button btnEdit;
    private FirebaseAuth auth;
    private FirebaseFirestore db;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_view_profile,container,false);

        tvName=view.findViewById(R.id.TVName);
        tvBirthday=view.findViewById(R.id.TVBirthday);
        tvEmail=view.findViewById(R.id.TVEmail);
        tvPhoneNo=view.findViewById(R.id.TVPhoneNo);

        auth=FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        loadProfile();

        btnEdit.setOnClickListener(v -> editProfile());

        return view;
    }

    private  void loadProfile(){

    }

    private void editProfile(){



    }
}