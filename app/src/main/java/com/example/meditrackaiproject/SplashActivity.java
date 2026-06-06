package com.example.meditrackaiproject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.meditrackaiproject.admin.AdminDashboardActivity;
import com.example.meditrackaiproject.auth.RoleSelectionActivity;
import com.example.meditrackaiproject.doctor.DoctorDashboardActivity;
import com.example.meditrackaiproject.patient.PatientDashboardActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashActivity extends AppCompatActivity {

    private TextView tvAppName;
    private String appName = "MediTrack.AI";
    private int charIndex = 0;
    private Handler textHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView ivLogo = findViewById(R.id.ivLogo);
        tvAppName = findViewById(R.id.tvAppName);
        TextView tvAppSlogan = findViewById(R.id.tvAppSlogan);

        // Logo Rotation Animation
        RotateAnimation rotate = new RotateAnimation(0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(2000);
        rotate.setRepeatCount(Animation.INFINITE);
        ivLogo.startAnimation(rotate);

        // Text Animation
        animateText();

        // Slogan fade in
        tvAppSlogan.animate().alpha(1f).setDuration(1000).setStartDelay(1500).start();

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                checkUserRole(user.getUid());
            } else {
                startActivity(new Intent(SplashActivity.this, RoleSelectionActivity.class));
                finish();
            }
        }, 4000);
    }

    private void animateText() {
        if (charIndex <= appName.length()) {
            tvAppName.setText(appName.substring(0, charIndex));
            charIndex++;
            textHandler.postDelayed(this::animateText, 150);
        }
    }

    private void checkUserRole(String uid) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(uid);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String role = snapshot.child("role").getValue(String.class);
                    if ("Doctor".equals(role)) {
                        String status = snapshot.child("status").getValue(String.class);
                        if ("Approved".equals(status)) {
                            startActivity(new Intent(SplashActivity.this, DoctorDashboardActivity.class));
                        } else {
                            Toast.makeText(SplashActivity.this, "Account pending approval.", Toast.LENGTH_SHORT).show();
                            FirebaseAuth.getInstance().signOut();
                            startActivity(new Intent(SplashActivity.this, RoleSelectionActivity.class));
                        }
                    } else if ("Patient".equals(role)) {
                        startActivity(new Intent(SplashActivity.this, PatientDashboardActivity.class));
                    } else if ("Admin".equals(role)) {
                        startActivity(new Intent(SplashActivity.this, AdminDashboardActivity.class));
                    } else {
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(SplashActivity.this, RoleSelectionActivity.class));
                    }
                } else {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(SplashActivity.this, RoleSelectionActivity.class));
                }
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                startActivity(new Intent(SplashActivity.this, RoleSelectionActivity.class));
                finish();
            }
        });
    }
}