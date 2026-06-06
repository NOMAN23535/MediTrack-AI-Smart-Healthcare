package com.example.meditrackaiproject.admin;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.meditrackaiproject.databinding.ActivityAdminChatsBinding;
import com.example.meditrackaiproject.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class AdminChatsActivity extends AppCompatActivity {

    private ActivityAdminChatsBinding binding;
    private DatabaseReference mDatabase;
    private List<User> chatUsers;
    private AdminUserAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminChatsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mDatabase = FirebaseDatabase.getInstance().getReference();
        chatUsers = new ArrayList<>();
        
        binding.rvChatUsers.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminUserAdapter(chatUsers);
        binding.rvChatUsers.setAdapter(adapter);

        binding.ivBack.setOnClickListener(v -> finish());

        loadUsersWithChats();
    }

    private void loadUsersWithChats() {
        binding.progressBar.setVisibility(View.VISIBLE);
        mDatabase.child("Chats").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot chatSnapshot) {
                List<String> userIds = new ArrayList<>();
                for (DataSnapshot snapshot : chatSnapshot.getChildren()) {
                    userIds.add(snapshot.getKey());
                }

                if (userIds.isEmpty()) {
                    binding.progressBar.setVisibility(View.GONE);
                    binding.tvNoChats.setVisibility(View.VISIBLE);
                    return;
                }

                mDatabase.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                        chatUsers.clear();
                        for (String uid : userIds) {
                            User user = userSnapshot.child(uid).getValue(User.class);
                            if (user != null) {
                                chatUsers.add(user);
                            }
                        }
                        adapter.notifyDataSetChanged();
                        binding.progressBar.setVisibility(View.GONE);
                        binding.tvNoChats.setVisibility(chatUsers.isEmpty() ? View.VISIBLE : View.GONE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        binding.progressBar.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(AdminChatsActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}