package com.example.meditrackaiproject.patient;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.meditrackaiproject.api.GroqApiService;
import com.example.meditrackaiproject.api.RetrofitClient;
import com.example.meditrackaiproject.databinding.ActivityAiAnalyzerBinding;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AiAnalyzerActivity extends AppCompatActivity {

    private ActivityAiAnalyzerBinding binding;
    private GroqApiService groqApiService;
    // Provided Groq API Key
    private static final String GROQ_API_KEY = "Bearer YOUR_GROQ_API_KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAiAnalyzerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbar.setNavigationOnClickListener(v -> finish());
        
        groqApiService = RetrofitClient.getGroqApiService();

        binding.btnAnalyze.setOnClickListener(v -> analyzeSymptoms());
    }

    private void analyzeSymptoms() {
        List<String> selectedSymptoms = new ArrayList<>();
        
        if (binding.chipFever.isChecked()) selectedSymptoms.add("Fever");
        if (binding.chipCough.isChecked()) selectedSymptoms.add("Cough");
        if (binding.chipHeadache.isChecked()) selectedSymptoms.add("Headache");
        if (binding.chipVomiting.isChecked()) selectedSymptoms.add("Vomiting");
        if (binding.chipFatigue.isChecked()) selectedSymptoms.add("Fatigue");
        if (binding.chipChestPain.isChecked()) selectedSymptoms.add("Chest Pain");
        if (binding.chipStomachPain.isChecked()) selectedSymptoms.add("Stomach Pain");
        if (binding.chipSoreThroat.isChecked()) selectedSymptoms.add("Sore Throat");
        if (binding.chipNausea.isChecked()) selectedSymptoms.add("Nausea");
        if (binding.chipDizziness.isChecked()) selectedSymptoms.add("Dizziness");
        if (binding.chipBodyAches.isChecked()) selectedSymptoms.add("Body Aches");
        if (binding.chipBreathing.isChecked()) selectedSymptoms.add("Breathing Problems");

        String other = binding.etOtherSymptoms.getText().toString().trim();
        if (!other.isEmpty()) selectedSymptoms.add(other);

        if (selectedSymptoms.isEmpty()) {
            Toast.makeText(this, "Please select or enter your symptoms", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.progressBar.setVisibility(View.VISIBLE);
        binding.btnAnalyze.setEnabled(false);

        getAiAnalysis(selectedSymptoms);
    }

    private void getAiAnalysis(List<String> symptoms) {
        String symptomsStr = String.join(", ", symptoms);

        JsonObject body = new JsonObject();
        // Use llama-3.1-8b-instant for fast responses and JSON mode support
        body.addProperty("model", "llama-3.1-8b-instant");
        
        JsonObject responseFormat = new JsonObject();
        responseFormat.addProperty("type", "json_object");
        body.add("response_format", responseFormat);
        
        JsonArray messages = new JsonArray();
        JsonObject systemMessage = new JsonObject();
        systemMessage.addProperty("role", "system");
        systemMessage.addProperty("content", "You are an expert medical diagnostic AI for the MediTrack.AI platform. Analyze user symptoms and return a JSON object with: 'disease' (string), 'severity' ('Mild', 'Moderate', or 'Severe'), 'precautions' (array of strings), 'medicines' (array of strings), and 'specialization' (one of: 'General Physician', 'Cardiologist', 'Dermatologist', 'Neurologist', 'Pediatrician', 'Psychiatrist', 'Orthopedic'). Output MUST be valid JSON.");
        messages.add(systemMessage);

        JsonObject userMsg = new JsonObject();
        userMsg.addProperty("role", "user");
        userMsg.addProperty("content", "I am experiencing the following symptoms: " + symptomsStr + ". Please analyze them.");
        messages.add(userMsg);
        
        body.add("messages", messages);

        groqApiService.getChatCompletion(GROQ_API_KEY, body).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                binding.progressBar.setVisibility(View.GONE);
                binding.btnAnalyze.setEnabled(true);
                
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String content = response.body().getAsJsonArray("choices")
                                .get(0).getAsJsonObject().getAsJsonObject("message")
                                .get("content").getAsString();
                        
                        Intent intent = new Intent(AiAnalyzerActivity.this, AnalysisResultActivity.class);
                        intent.putExtra("json_result", content);
                        startActivity(intent);
                        
                    } catch (Exception e) {
                        Toast.makeText(AiAnalyzerActivity.this, "Error: AI response parsing failed", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AiAnalyzerActivity.this, "AI Analysis failed: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
                binding.btnAnalyze.setEnabled(true);
                Toast.makeText(AiAnalyzerActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}