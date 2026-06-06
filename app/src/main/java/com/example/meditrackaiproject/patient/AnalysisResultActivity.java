package com.example.meditrackaiproject.patient;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.example.meditrackaiproject.R;
import com.example.meditrackaiproject.databinding.ActivityAnalysisResultBinding;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class AnalysisResultActivity extends AppCompatActivity {

    private ActivityAnalysisResultBinding binding;
    private String specialization = "General Physician";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAnalysisResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.ivBack.setOnClickListener(v -> finish());

        String jsonResult = getIntent().getStringExtra("json_result");
        if (jsonResult != null) {
            parseAndDisplayResult(jsonResult);
        } else {
            Toast.makeText(this, "No analysis result found", Toast.LENGTH_SHORT).show();
            finish();
        }

        binding.btnViewDoctors.setOnClickListener(v -> {
            Intent intent = new Intent(this, RecommendedDoctorsActivity.class);
            intent.putExtra("specialization", specialization);
            startActivity(intent);
        });
    }

    private void parseAndDisplayResult(String json) {
        try {
            JsonObject result = new JsonParser().parse(json).getAsJsonObject();
            
            String disease = result.has("disease") ? result.get("disease").getAsString() : "Unknown Condition";
            String severity = result.has("severity") ? result.get("severity").getAsString() : "Moderate";
            specialization = result.has("specialization") ? result.get("specialization").getAsString() : "General Physician";

            binding.tvDiseaseName.setText(disease);
            binding.tvSeverity.setText(severity);

            // Handle Severity UI
            if ("Severe".equalsIgnoreCase(severity)) {
                binding.tvSeverity.setTextColor(ContextCompat.getColor(this, R.color.error_red));
                binding.cardSevereWarning.setVisibility(View.VISIBLE);
            } else if ("Mild".equalsIgnoreCase(severity)) {
                binding.tvSeverity.setTextColor(ContextCompat.getColor(this, R.color.success_green));
                binding.cardSevereWarning.setVisibility(View.GONE);
            } else {
                binding.tvSeverity.setTextColor(ContextCompat.getColor(this, R.color.warning_orange));
                binding.cardSevereWarning.setVisibility(View.GONE);
            }

            // Precautions
            if (result.has("precautions") && result.get("precautions").isJsonArray()) {
                JsonArray precArray = result.getAsJsonArray("precautions");
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < precArray.size(); i++) {
                    sb.append("• ").append(precArray.get(i).getAsString()).append("\n");
                }
                binding.tvPrecautions.setText(sb.toString().trim());
            }

            // Medicines
            if (result.has("medicines") && result.get("medicines").isJsonArray()) {
                JsonArray medArray = result.getAsJsonArray("medicines");
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < medArray.size(); i++) {
                    sb.append("• ").append(medArray.get(i).getAsString()).append("\n");
                }
                binding.tvMedicines.setText(sb.toString().trim());
            }

        } catch (Exception e) {
            Toast.makeText(this, "Error parsing results: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}