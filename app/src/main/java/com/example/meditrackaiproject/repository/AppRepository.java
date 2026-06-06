package com.example.meditrackaiproject.repository;

import androidx.annotation.NonNull;
import com.example.meditrackaiproject.models.Appointment;
import com.example.meditrackaiproject.models.Doctor;
import com.example.meditrackaiproject.models.ChatMessage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class AppRepository {
    private static AppRepository instance;
    private final DatabaseReference mDatabase;
    private final FirebaseAuth mAuth;

    private AppRepository() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
    }

    public static synchronized AppRepository getInstance() {
        if (instance == null) {
            instance = new AppRepository();
        }
        return instance;
    }

    public void bookAppointment(Appointment appointment, RepositoryCallback<Void> callback) {
        String id = mDatabase.child("Appointments").push().getKey();
        if (id == null) {
            callback.onError("Failed to generate appointment ID");
            return;
        }
        appointment.setId(id);
        mDatabase.child("Appointments").child(id).setValue(appointment)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) callback.onSuccess(null);
                    else callback.onError(task.getException() != null ? task.getException().getMessage() : "Unknown error");
                });
    }

    public void getRelevantDoctors(String specialization, RepositoryCallback<List<Doctor>> callback) {
        mDatabase.child("Users").orderByChild("role").equalTo("Doctor")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<Doctor> doctors = new ArrayList<>();
                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                            Doctor doctor = postSnapshot.getValue(Doctor.class);
                            if (doctor != null && "Approved".equals(doctor.getStatus())) {
                                // If specialization is provided, filter by it. Otherwise return all approved doctors.
                                if (specialization == null || specialization.isEmpty() || 
                                    (doctor.getSpecialization() != null && doctor.getSpecialization().toLowerCase().contains(specialization.toLowerCase()))) {
                                    doctors.add(doctor);
                                }
                            }
                        }
                        callback.onSuccess(doctors);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onError(error.getMessage());
                    }
                });
    }

    public void saveChatMessage(String userId, ChatMessage message) {
        mDatabase.child("Chats").child(userId).push().setValue(message);
    }

    public interface RepositoryCallback<T> {
        void onSuccess(T result);
        void onError(String message);
    }
}