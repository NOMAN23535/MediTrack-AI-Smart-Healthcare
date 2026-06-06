package com.example.meditrackaiproject.patient;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.meditrackaiproject.R;
import com.example.meditrackaiproject.databinding.ActivityPatientDashboardBinding;
import com.example.meditrackaiproject.models.Medicine;
import com.example.meditrackaiproject.models.Prescription;
import com.example.meditrackaiproject.models.User;
import com.example.meditrackaiproject.notifications.NotificationsActivity;
import com.example.meditrackaiproject.profile.ProfileActivity;
import com.example.meditrackaiproject.utils.ReminderScheduler;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class PatientDashboardActivity extends AppCompatActivity {

    private ActivityPatientDashboardBinding binding;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private List<Medicine> medicineList = new ArrayList<>();
    private Handler timerHandler = new Handler(Looper.getMainLooper());
    private Runnable timerRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPatientDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();
            mDatabase = FirebaseDatabase.getInstance().getReference();
            loadUserData(userId);
            loadMedicines(userId);
        }

        setupClickListeners();
        setupBottomNavigation();
        startCountdownTimer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.bottomNavigation.setSelectedItemId(R.id.nav_home);
    }

    private void setupClickListeners() {
        binding.cardUploadPrescription.setOnClickListener(v -> startActivity(new Intent(this, UploadPrescriptionActivity.class)));
        binding.cardAiAnalyzer.setOnClickListener(v -> startActivity(new Intent(this, AiAnalyzerActivity.class)));
        binding.cardMedicines.setOnClickListener(v -> startActivity(new Intent(this, MedicineManagementActivity.class)));
        binding.cardPrescriptions.setOnClickListener(v -> startActivity(new Intent(this, PrescriptionListActivity.class)));
        binding.cardReports.setOnClickListener(v -> startActivity(new Intent(this, MedicineReportsActivity.class)));
        binding.cardNotifications.setOnClickListener(v -> startActivity(new Intent(this, NotificationsActivity.class)));
        binding.cardProfile.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
        binding.ivUserProfile.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
        binding.cardCountdown.setOnClickListener(v -> startActivity(new Intent(this, MedicineManagementActivity.class)));
    }

    private void setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_chatbot) {
                startActivity(new Intent(this, ChatbotActivity.class));
            } else if (id == R.id.nav_medicines) {
                startActivity(new Intent(this, MedicineManagementActivity.class));
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
            }
            return true;
        });
    }

    private void loadUserData(String userId) {
        mDatabase.child("Users").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!isFinishing()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        String firstName = "User";
                        if (user.getFullName() != null && !user.getFullName().trim().isEmpty()) {
                            firstName = user.getFullName().trim().split("\\s+")[0];
                        }
                        binding.tvWelcome.setText("Hi, " + firstName);
                        if (user.getProfileImageUrl() != null && !user.getProfileImageUrl().isEmpty()) {
                            Glide.with(PatientDashboardActivity.this)
                                    .load(Uri.parse(user.getProfileImageUrl()))
                                    .placeholder(R.drawable.profile_1)
                                    .into(binding.ivUserProfile);
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void loadMedicines(String userId) {
        mDatabase.child("Prescriptions").orderByChild("patientId").equalTo(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        medicineList.clear();
                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                            Prescription prescription = postSnapshot.getValue(Prescription.class);
                            if (prescription != null && prescription.getMedicines() != null) {
                                for (Medicine med : prescription.getMedicines()) {
                                    if (med != null && med.getName() != null && !isAlreadyInList(med.getName())) {
                                        medicineList.add(med);
                                        ReminderScheduler.scheduleMedicineReminders(PatientDashboardActivity.this, med);
                                    }
                                }
                            }
                        }
                        updateCountdown();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    private boolean isAlreadyInList(String name) {
        for (Medicine m : medicineList) {
            if (m.getName().equalsIgnoreCase(name)) return true;
        }
        return false;
    }

    private void startCountdownTimer() {
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                updateCountdown();
                timerHandler.postDelayed(this, 60000); // Update every minute
            }
        };
        timerHandler.post(timerRunnable);
    }

    private void updateCountdown() {
        if (medicineList.isEmpty()) {
            binding.tvCountdownTimer.setText("No medicines prescribed");
            binding.tvNextDoseTime.setText("--:--");
            return;
        }

        Calendar now = Calendar.getInstance();
        int currentHour = now.get(Calendar.HOUR_OF_DAY);
        int currentMinute = now.get(Calendar.MINUTE);
        int currentTimeInMins = currentHour * 60 + currentMinute;

        int[] doseTimesInMins = {10 * 60 + 30, 15 * 60 + 30, 19 * 60 + 30, 21 * 60 + 30};
        String[] doseNames = {"Morning dose", "Afternoon dose", "Evening dose", "Before sleeping dose"};
        String[] doseTimeStrings = {"10:30 AM", "03:30 PM", "07:30 PM", "09:30 PM"};

        int nextDoseIndex = -1;
        int minDiff = Integer.MAX_VALUE;

        // Check for doses today
        for (int i = 0; i < doseTimesInMins.length; i++) {
            if (doseTimesInMins[i] > currentTimeInMins) {
                int diff = doseTimesInMins[i] - currentTimeInMins;
                if (diff < minDiff) {
                    // Check if any medicine is scheduled for this time
                    if (isAnyMedicineAt(i)) {
                        minDiff = diff;
                        nextDoseIndex = i;
                    }
                }
            }
        }

        // If no doses left today, check first dose tomorrow
        if (nextDoseIndex == -1) {
            for (int i = 0; i < doseTimesInMins.length; i++) {
                if (isAnyMedicineAt(i)) {
                    nextDoseIndex = i;
                    minDiff = (24 * 60 - currentTimeInMins) + doseTimesInMins[i];
                    break;
                }
            }
        }

        if (nextDoseIndex != -1) {
            int hours = minDiff / 60;
            int mins = minDiff % 60;
            String countdownText;
            if (hours > 0) {
                countdownText = String.format(Locale.getDefault(), "%s in %d hr %d mins", doseNames[nextDoseIndex], hours, mins);
            } else {
                countdownText = String.format(Locale.getDefault(), "%s in %d mins", doseNames[nextDoseIndex], mins);
            }
            binding.tvCountdownTimer.setText(countdownText);
            binding.tvNextDoseTime.setText(doseTimeStrings[nextDoseIndex]);
        } else {
            binding.tvCountdownTimer.setText("No doses scheduled");
            binding.tvNextDoseTime.setText("--:--");
        }
    }

    private boolean isAnyMedicineAt(int index) {
        for (Medicine med : medicineList) {
            if (index == 0 && med.isMorning()) return true;
            if (index == 1 && med.isAfternoon()) return true;
            if (index == 2 && med.isEvening()) return true;
            if (index == 3 && (med.isNight() || med.isBeforeSleep())) return true;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timerHandler != null && timerRunnable != null) {
            timerHandler.removeCallbacks(timerRunnable);
        }
    }
}