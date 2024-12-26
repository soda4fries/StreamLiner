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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ViewProfile#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ViewProfile extends Fragment {

    private TextView tvName, tvBirthday,tvEmail,tvPhoneNumber;
    private Button btnEditProfile;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ViewProfile() {
        // Required empty public constructor
    }


    public static ViewProfile newInstance(String param1, String param2) {
        ViewProfile fragment = new ViewProfile();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_view_profile,container,false);

        tvName=view.findViewById(R.id.TVName);
        tvBirthday=view.findViewById(R.id.TVBirthday);
        tvEmail=view.findViewById(R.id.TVEmail);
        tvPhoneNumber=view.findViewById(R.id.TVPhoneNo);

        mAuth=FirebaseAuth.getInstance();
        mDatabase= FirebaseDatabase.getInstance().getReference("Users");

        loadProfile();

        btnEditProfile.setOnClickListener(v -> editProfile());

        return view;
    }

    private  void loadProfile(){
        FirebaseUser currentUser=mAuth.getCurrentUser();
        if(currentUser!=null){
            String userId=currentUser.getUid();
            mDatabase.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        String name=snapshot.child("name").getValue(String.class);
                        String email=currentUser.getEmail();

                        tvName.setText(name);
                        tvEmail.setText(email);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getContext(),"Failed to load profile.",Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void editProfile(){

    }
}