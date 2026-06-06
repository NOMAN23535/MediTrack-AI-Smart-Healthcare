package com.example.meditrackaiproject.patient;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.meditrackaiproject.api.GroqApiService;
import com.example.meditrackaiproject.api.RetrofitClient;
import com.example.meditrackaiproject.databinding.ActivityUploadPrescriptionBinding;
import com.example.meditrackaiproject.databinding.DialogAddMedicineBinding;
import com.example.meditrackaiproject.models.Medicine;
import com.example.meditrackaiproject.models.Prescription;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadPrescriptionActivity extends AppCompatActivity {

    private ActivityUploadPrescriptionBinding binding;
    private static final int PICK_IMAGE_REQUEST = 101;
    private Uri imageUri;
    private GroqApiService groqApiService;
    private static final String GROQ_API_KEY = "Bearer YOUR_GROQ_API_KEY";
    private List<Medicine> detectedMedicines = new ArrayList<>();
    private PatientMedicineAdapter adapter;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUploadPrescriptionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        groqApiService = RetrofitClient.getGroqApiService();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        binding.toolbar.setNavigationOnClickListener(v -> finish());
        binding.cardImage.setOnClickListener(v -> openImagePicker());
        binding.btnAnalyze.setOnClickListener(v -> {
            if (imageUri != null) {
                analyzePrescription();
            } else {
                Toast.makeText(this, "Please select an image first", Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnSave.setOnClickListener(v -> savePrescription());

        adapter = new PatientMedicineAdapter(detectedMedicines, new PatientMedicineAdapter.OnMedicineActionListener() {
            @Override
            public void onEdit(Medicine medicine, int position) {
                showEditMedicineDialog(medicine, position);
            }

            @Override
            public void onDelete(int position) {
                detectedMedicines.remove(position);
                adapter.notifyItemRemoved(position);
                if (detectedMedicines.isEmpty()) {
                    binding.btnSave.setVisibility(View.GONE);
                    binding.btnAnalyze.setVisibility(View.VISIBLE);
                }
            }
        });
        binding.rvDetectedMedicines.setLayoutManager(new LinearLayoutManager(this));
        binding.rvDetectedMedicines.setAdapter(adapter);
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Prescription Image"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            binding.ivPrescription.setImageURI(imageUri);
            binding.layoutPlaceholder.setVisibility(View.GONE);
            binding.tvStatus.setVisibility(View.VISIBLE);
            binding.tvStatus.setText("Image selected");
            binding.btnAnalyze.setVisibility(View.VISIBLE);
            binding.btnSave.setVisibility(View.GONE);
            binding.rvDetectedMedicines.setVisibility(View.GONE);
        }
    }

    private void analyzePrescription() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.btnAnalyze.setEnabled(false);

        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            String base64Image = encodeImage(bitmap);

            JsonObject body = new JsonObject();
            body.addProperty("model", "llama-3.2-11b-vision-preview");
            
            JsonObject responseFormat = new JsonObject();
            responseFormat.addProperty("type", "json_object");
            body.add("response_format", responseFormat);

            JsonArray messages = new JsonArray();
            JsonObject systemMsg = new JsonObject();
            systemMsg.addProperty("role", "system");
            systemMsg.addProperty("content", "Extract medicines from the prescription image. Return JSON with 'medicines' array. Each item: 'name', 'dosage', 'duration', 'frequency', 'morning' (bool), 'afternoon' (bool), 'evening' (bool), 'night' (bool), 'beforeMeal' (bool), 'afterMeal' (bool), 'instructions' (string), 'stockQuantity' (int). Default stock: 10.");
            messages.add(systemMsg);

            JsonObject userMsg = new JsonObject();
            userMsg.addProperty("role", "user");
            JsonArray contentArray = new JsonArray();
            JsonObject textContent = new JsonObject();
            textContent.addProperty("type", "text");
            textContent.addProperty("text", "Extract medicines from this prescription.");
            contentArray.add(textContent);
            JsonObject imageContent = new JsonObject();
            imageContent.addProperty("type", "image_url");
            JsonObject imageUrl = new JsonObject();
            imageUrl.addProperty("url", "data:image/jpeg;base64," + base64Image);
            imageContent.add("image_url", imageUrl);
            contentArray.add(imageContent);
            userMsg.add("content", contentArray);
            messages.add(userMsg);
            body.add("messages", messages);

            groqApiService.getChatCompletion(GROQ_API_KEY, body).enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    binding.progressBar.setVisibility(View.GONE);
                    binding.btnAnalyze.setEnabled(true);
                    if (response.isSuccessful() && response.body() != null) {
                        try {
                            String content = response.body().getAsJsonArray("choices").get(0).getAsJsonObject().getAsJsonObject("message").get("content").getAsString();
                            parseAiResponse(content);
                        } catch (Exception e) {
                            Toast.makeText(UploadPrescriptionActivity.this, "Parsing failed", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(UploadPrescriptionActivity.this, "AI analysis failed", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    binding.progressBar.setVisibility(View.GONE);
                    binding.btnAnalyze.setEnabled(true);
                    Toast.makeText(UploadPrescriptionActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            binding.progressBar.setVisibility(View.GONE);
            binding.btnAnalyze.setEnabled(true);
            Toast.makeText(this, "Error processing image", Toast.LENGTH_SHORT).show();
        }
    }

    private String encodeImage(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        return Base64.encodeToString(baos.toByteArray(), Base64.NO_WRAP);
    }

    private void parseAiResponse(String json) {
        try {
            JsonObject result = new JsonParser().parse(json).getAsJsonObject();
            JsonArray medsArray = result.getAsJsonArray("medicines");
            detectedMedicines.clear();
            for (JsonElement element : medsArray) {
                JsonObject m = element.getAsJsonObject();
                Medicine med = new Medicine();
                med.setName(m.get("name").getAsString());
                med.setDosage(m.has("dosage") ? m.get("dosage").getAsString() : "");
                med.setDuration(m.has("duration") ? m.get("duration").getAsString() : "");
                med.setFrequency(m.has("frequency") ? m.get("frequency").getAsString() : "");
                med.setMorning(m.has("morning") && m.get("morning").getAsBoolean());
                med.setAfternoon(m.has("afternoon") && m.get("afternoon").getAsBoolean());
                med.setEvening(m.has("evening") && m.get("evening").getAsBoolean());
                med.setNight(m.has("night") && m.get("night").getAsBoolean());
                med.setBeforeMeal(m.has("beforeMeal") && m.get("beforeMeal").getAsBoolean());
                med.setAfterMeal(m.has("afterMeal") && m.get("afterMeal").getAsBoolean());
                med.setInstructions(m.has("instructions") ? m.get("instructions").getAsString() : "");
                med.setStockQuantity(m.has("stockQuantity") ? m.get("stockQuantity").getAsInt() : 10);
                detectedMedicines.add(med);
            }
            if (!detectedMedicines.isEmpty()) {
                binding.rvDetectedMedicines.setVisibility(View.VISIBLE);
                binding.btnSave.setVisibility(View.VISIBLE);
                binding.btnAnalyze.setVisibility(View.GONE);
                adapter.notifyDataSetChanged();
            } else {
                Toast.makeText(this, "No medicines detected", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Response parsing error", Toast.LENGTH_SHORT).show();
        }
    }

    private void showEditMedicineDialog(Medicine medicine, int position) {
        DialogAddMedicineBinding dialogBinding = DialogAddMedicineBinding.inflate(getLayoutInflater());
        dialogBinding.etMedName.setText(medicine.getName());
        dialogBinding.etDosage.setText(medicine.getDosage());
        dialogBinding.etDuration.setText(medicine.getDuration());
        dialogBinding.etFrequency.setText(medicine.getFrequency());
        dialogBinding.etStock.setText(String.valueOf(medicine.getStockQuantity()));
        dialogBinding.cbMorning.setChecked(medicine.isMorning());
        dialogBinding.cbAfternoon.setChecked(medicine.isAfternoon());
        dialogBinding.cbEvening.setChecked(medicine.isEvening());
        dialogBinding.cbNight.setChecked(medicine.isNight());
        dialogBinding.rbBeforeMeal.setChecked(medicine.isBeforeMeal());
        dialogBinding.rbAfterMeal.setChecked(medicine.isAfterMeal());
        dialogBinding.etInstructions.setText(medicine.getInstructions());

        new AlertDialog.Builder(this)
                .setTitle("Edit Medicine")
                .setView(dialogBinding.getRoot())
                .setPositiveButton("Update", (dialog, which) -> {
                    medicine.setName(dialogBinding.etMedName.getText().toString());
                    medicine.setDosage(dialogBinding.etDosage.getText().toString());
                    medicine.setDuration(dialogBinding.etDuration.getText().toString());
                    medicine.setFrequency(dialogBinding.etFrequency.getText().toString());
                    String stockStr = dialogBinding.etStock.getText().toString();
                    medicine.setStockQuantity(stockStr.isEmpty() ? 0 : Integer.parseInt(stockStr));
                    medicine.setMorning(dialogBinding.cbMorning.isChecked());
                    medicine.setAfternoon(dialogBinding.cbAfternoon.isChecked());
                    medicine.setEvening(dialogBinding.cbEvening.isChecked());
                    medicine.setNight(dialogBinding.cbNight.isChecked());
                    medicine.setBeforeMeal(dialogBinding.rbBeforeMeal.isChecked());
                    medicine.setAfterMeal(dialogBinding.rbAfterMeal.isChecked());
                    medicine.setInstructions(dialogBinding.etInstructions.getText().toString());
                    adapter.notifyItemChanged(position);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void savePrescription() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        DatabaseReference presRef = mDatabase.child("Prescriptions").push();
        Prescription p = new Prescription(presRef.getKey(), "manual_" + presRef.getKey(), userId, "Patient", "Self", "Manual Upload", "Self Uploaded", detectedMedicines, "AI Detected", date);
        presRef.setValue(p).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(UploadPrescriptionActivity.this, "Medicines saved successfully!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}