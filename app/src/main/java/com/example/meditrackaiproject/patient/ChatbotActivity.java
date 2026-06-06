package com.example.meditrackaiproject.patient;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.meditrackaiproject.api.GroqApiService;
import com.example.meditrackaiproject.api.RetrofitClient;
import com.example.meditrackaiproject.databinding.ActivityChatbotBinding;
import com.example.meditrackaiproject.models.ChatMessage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatbotActivity extends AppCompatActivity {

    private ActivityChatbotBinding binding;
    private ChatAdapter adapter;
    private List<ChatMessage> chatMessages;
    private DatabaseReference mDatabase;
    private String userId;
    private GroqApiService groqApiService;
    private static final String GROQ_API_KEY = "Bearer YOUR_GROQ_API_KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatbotBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(this, "Session expired. Please login again.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Chats").child(userId);

        chatMessages = new ArrayList<>();
        adapter = new ChatAdapter(chatMessages);
        binding.rvChat.setLayoutManager(new LinearLayoutManager(this));
        binding.rvChat.setAdapter(adapter);

        groqApiService = RetrofitClient.getGroqApiService();

        binding.btnSend.setOnClickListener(v -> sendMessage());
        binding.ivBack.setOnClickListener(v -> finish());

        loadChatHistory();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadChatHistory() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatMessages.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    ChatMessage message = postSnapshot.getValue(ChatMessage.class);
                    if (message != null) {
                        chatMessages.add(message);
                    }
                }
                adapter.notifyDataSetChanged();
                if (!chatMessages.isEmpty()) {
                    binding.rvChat.scrollToPosition(chatMessages.size() - 1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChatbotActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendMessage() {
        String text = binding.etMessage.getText().toString().trim();
        if (text.isEmpty()) return;

        ChatMessage userMessage = new ChatMessage(text, ChatMessage.ROLE_USER);
        mDatabase.push().setValue(userMessage);
        binding.etMessage.setText("");

        getAiResponse(text);
    }

    private void getAiResponse(String userPrompt) {
        showTypingIndicator(true);

        JsonObject body = new JsonObject();
        // Updated model to llama-3.1-8b-instant as llama3-8b-8192 is deprecated
        body.addProperty("model", "llama-3.1-8b-instant");
        
        JsonArray messages = new JsonArray();
        
        JsonObject systemMessage = new JsonObject();
        systemMessage.addProperty("role", "system");
        systemMessage.addProperty("content", "You are MediTrack AI, a professional healthcare assistant. Provide helpful, accurate, and concise health guidance. Always advise users to consult a professional doctor for serious symptoms.");
        messages.add(systemMessage);

        // Include last few messages for context
        int historySize = chatMessages.size();
        int start = Math.max(0, historySize - 5);
        for (int i = start; i < historySize; i++) {
            ChatMessage msg = chatMessages.get(i);
            JsonObject historyMsg = new JsonObject();
            historyMsg.addProperty("role", msg.getRole());
            historyMsg.addProperty("content", msg.getMessage());
            messages.add(historyMsg);
        }

        JsonObject userMsg = new JsonObject();
        userMsg.addProperty("role", ChatMessage.ROLE_USER);
        userMsg.addProperty("content", userPrompt);
        messages.add(userMsg);
        
        body.add("messages", messages);

        groqApiService.getChatCompletion(GROQ_API_KEY, body).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                showTypingIndicator(false);
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String botResponse = response.body().getAsJsonArray("choices")
                                .get(0).getAsJsonObject().getAsJsonObject("message")
                                .get("content").getAsString();
                        
                        ChatMessage aiMessage = new ChatMessage(botResponse, ChatMessage.ROLE_ASSISTANT);
                        mDatabase.push().setValue(aiMessage);
                    } catch (Exception e) {
                        Toast.makeText(ChatbotActivity.this, "Error parsing AI response", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String error = "AI Error: " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            error += " - " + response.errorBody().string();
                        }
                    } catch (Exception ignored) {}
                    Toast.makeText(ChatbotActivity.this, error, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                showTypingIndicator(false);
                Toast.makeText(ChatbotActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showTypingIndicator(boolean show) {
        binding.typingIndicator.setVisibility(show ? View.VISIBLE : View.GONE);
        if (show) {
            binding.rvChat.scrollToPosition(chatMessages.size() - 1);
        }
    }
}