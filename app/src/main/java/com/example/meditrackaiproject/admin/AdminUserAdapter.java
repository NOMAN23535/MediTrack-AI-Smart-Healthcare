package com.example.meditrackaiproject.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.meditrackaiproject.R;
import com.example.meditrackaiproject.databinding.ItemDoctorBinding;
import com.example.meditrackaiproject.models.User;
import java.util.List;

public class AdminUserAdapter extends RecyclerView.Adapter<AdminUserAdapter.ViewHolder> {

    private final List<User> users;

    public AdminUserAdapter(List<User> users) {
        this.users = users;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDoctorBinding binding = ItemDoctorBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = users.get(position);
        holder.binding.tvDoctorName.setText(user.getFullName());
        holder.binding.tvSpecialization.setText(user.getEmail());
        holder.binding.tvExperience.setText("Role: " + user.getRole());
        
        holder.binding.btnBook.setVisibility(View.GONE);

        int placeholder = "Doctor".equals(user.getRole()) ? R.drawable.ic_doctor_placeholder : R.drawable.profile;

        if (user.getProfileImageUrl() != null && !user.getProfileImageUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(user.getProfileImageUrl())
                    .placeholder(placeholder)
                    .into(holder.binding.ivDoctorProfile);
        } else {
            holder.binding.ivDoctorProfile.setImageResource(placeholder);
        }
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ItemDoctorBinding binding;
        ViewHolder(ItemDoctorBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}