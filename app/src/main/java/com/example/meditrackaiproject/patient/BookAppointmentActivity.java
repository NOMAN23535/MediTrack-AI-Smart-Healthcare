package com.example.meditrackaiproject.patient;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.meditrackaiproject.databinding.ActivityBookAppointmentBinding;
import com.example.meditrackaiproject.models.Appointment;
import com.example.meditrackaiproject.models.Notification;
import com.example.meditrackaiproject.models.User;
import com.example.meditrackaiproject.repository.AppRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.Calendar;
import java.util.Locale;

public class BookAppointmentActivity extends AppCompatActivity {

    private ActivityBookAppointmentBinding binding;
    private String doctorId, doctorName, specialization;
    private String selectedDate = "", selectedTime = "";
    private DatabaseReference mDatabase;
    private String patientName = "Patient";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBookAppointmentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mDatabase = FirebaseDatabase.getInstance().getReference();
        doctorId = getIntent().getStringExtra("doctorId");
        doctorName = getIntent().getStringExtra("doctorName");
        specialization = getIntent().getStringExtra("specialization");

        if (doctorName != null) binding.tvDoctorName.setText(doctorName);
        if (specialization != null) binding.tvSpecialization.setText(specialization);

        fetchPatientName();

        binding.ivBack.setOnClickListener(v -> finish());
        binding.btnSelectDate.setOnClickListener(v -> showDatePicker());
        binding.btnSelectTime.setOnClickListener(v -> showTimePicker());
        binding.btnBook.setOnClickListener(v -> confirmBooking());
    }

    private void fetchPatientName() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabase.child("Users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user != null && user.getFullName() != null) {
                    patientName = user.getFullName();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void showDatePicker() {
        final Calendar c = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, monthOfYear, dayOfMonth) -> {
                    selectedDate = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
                    binding.tvSelectedDate.setText(selectedDate);
                    binding.tvSelectedDate.setVisibility(View.VISIBLE);
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    private void showTimePicker() {
        final Calendar c = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minute) -> {
                    String am_pm = (hourOfDay < 12) ? "AM" : "PM";
                    int hourDisplay = (hourOfDay == 0 || hourOfDay == 12) ? 12 : hourOfDay % 12;
                    selectedTime = String.format(Locale.getDefault(), "%02d:%02d %s", hourDisplay, minute, am_pm);
                    binding.tvSelectedTime.setText(selectedTime);
                    binding.tvSelectedTime.setVisibility(View.VISIBLE);
                }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), false);
        timePickerDialog.show();
    }

    private void confirmBooking() {
        String symptoms = binding.etSymptoms.getText().toString().trim();
        if (TextUtils.isEmpty(selectedDate) || TextUtils.isEmpty(selectedTime) || TextUtils.isEmpty(symptoms)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.btnBook.setEnabled(false);
        binding.btnBook.setText("Booking...");

        String patientId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Appointment appointment = new Appointment(null, patientId, patientName, doctorId, doctorName, specialization, selectedDate, selectedTime, symptoms);
        
        AppRepository.getInstance().bookAppointment(appointment, new AppRepository.RepositoryCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                sendNotificationToDoctor(appointment);
                Toast.makeText(BookAppointmentActivity.this, "Appointment request sent successfully!", Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onError(String message) {
                binding.btnBook.setEnabled(true);
                binding.btnBook.setText("Book Appointment");
                Toast.makeText(BookAppointmentActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendNotificationToDoctor(Appointment app) {
        DatabaseReference notifRef = mDatabase.child("Notifications").child(doctorId).push();
        String id = notifRef.getKey();
        String message = "New appointment request from " + app.getPatientName() + " for " + app.getDate() + " at " + app.getTime();
        Notification notification = new Notification(id, "New Appointment Request", message, "Appointment");
        notifRef.setValue(notification);
    }
}