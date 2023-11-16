package com.example.khike.ui.profile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.khike.Login;
import com.example.khike.ChangePassword;
import com.example.khike.DBHelper;
import com.example.khike.EditProfile;
import com.example.khike.R;
import com.google.android.material.imageview.ShapeableImageView;


public class ProfileFragment extends Fragment {

    private ShapeableImageView avatarImg;
    private TextView nameProfile;
    private String fullName;
    private int userId;
    private DBHelper dbHelper;
    private TextView btnMore;

    private Button editBtn;

    private ImageView cameraBtn;
    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            View view = getView(); // Get the root view of the fragment
            if (view != null) {
                ShapeableImageView avatar = view.findViewById(R.id.avatar_img);
                avatar.setImageURI(selectedImage);
            }
        }
    }



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        nameProfile = view.findViewById(R.id.txt_name_profile);
        btnMore = view.findViewById(R.id.btn_more);
        editBtn = view.findViewById(R.id.edit_btn);

        dbHelper = new DBHelper(getActivity()); // Replace with your context
        cameraBtn = view.findViewById(R.id.camera_icon);
        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,3);
            }
        });


        // Do something with dbHelper, e.g., fetch the full name of a user
        if (dbHelper != null && getActivity() != null) {
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("USER_CREDENTIALS", Context.MODE_PRIVATE);
            String loggedInUserName = sharedPreferences.getString("CURRENT_USER",  null);
            userId = dbHelper.getUserIdByName(loggedInUserName);
            if(loggedInUserName != null) {
                fullName = dbHelper.getFullName(loggedInUserName);
                Log.d("Debug", "Retrieved fullName: " + fullName);
            }

        } else {
            Log.e("DB_ERROR", "DBHelper or getActivity() is null.");
        }

        nameProfile.setText(fullName);

        // Set onClickListener on Edit Button
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create Intent to navigate to EditProfile activity
                Intent intent = new Intent(getContext(), EditProfile.class);
                intent.putExtra("USER_ID", userId);
                // Start EditProfile activity
                startActivity(intent);
            }
        });






//        btnMore.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                PopupMenu popup = new PopupMenu(getContext(), v);
//                popup.getMenuInflater().inflate(R.menu.profile_menu, popup.getMenu());
//                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                    public boolean onMenuItemClick(MenuItem item) {
//                        int itemId = item.getItemId();
//                        if (itemId == R.id.action_change_password) {
//                            Intent intent = new Intent(getContext(), ChangePassword.class);
//                            intent.putExtra("USER_ID", userId);
//                            startActivity(intent);
//                            return true;
//                        } else if (itemId == R.id.action_logout) {
//                            SharedPreferences preferences = getContext().getSharedPreferences("userPrefs", Context.MODE_PRIVATE);
//                            SharedPreferences.Editor editor = preferences.edit();
//                            editor.clear();
//                            editor.apply();
//                            Intent intent = new Intent(getContext(), Login.class);
//                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                            startActivity(intent);
//                            return true;
//                        }
//                        return false;
//                    }
//                });
//                popup.show();
//            }
//        });



        return view;

    }


    @Override
    public void onResume() {
        super.onResume();
        loadUserData();
    }




    private void loadUserData() {
        // Do something with dbHelper, e.g., fetch the full name of a user
        if (dbHelper != null && getActivity() != null) {
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("USER_CREDENTIALS", Context.MODE_PRIVATE);
            String loggedInUserName = sharedPreferences.getString("CURRENT_USER",  null);
            userId = dbHelper.getUserIdByName(loggedInUserName);
            if(loggedInUserName != null) {
                fullName = dbHelper.getFullName(loggedInUserName);
                Log.d("Debug", "Retrieved fullName: " + fullName);
            }

        } else {
            Log.e("DB_ERROR", "DBHelper or getActivity() is null.");
        }

        nameProfile.setText(fullName);
    }


}