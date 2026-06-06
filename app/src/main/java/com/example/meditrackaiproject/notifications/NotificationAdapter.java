package com.example.meditrackaiproject.notifications;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.meditrackaiproject.R;
import com.example.meditrackaiproject.models.Notification;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private List<Notification> notifications;

    public NotificationAdapter(List<Notification> notifications) {
        this.notifications = notifications;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notification notification = notifications.get(position);
        holder.tvTitle.setText(notification.getTitle());
        holder.tvDesc.setText(notification.getMessage());
        holder.tvTime.setText(formatTime(notification.getTimestamp()));

        if ("Approval".equals(notification.getType())) {
            holder.ivIcon.setImageResource(R.drawable.ic_check_circle);
            holder.ivIcon.setColorFilter(holder.itemView.getContext().getColor(R.color.success_green));
        } else if ("Rejection".equals(notification.getType())) {
            // Replaced ic_error with ic_warning as ic_error was missing from drawables
            holder.ivIcon.setImageResource(R.drawable.ic_warning);
            holder.ivIcon.setColorFilter(holder.itemView.getContext().getColor(R.color.error_red));
        } else {
            holder.ivIcon.setImageResource(R.drawable.notification);
            holder.ivIcon.setColorFilter(holder.itemView.getContext().getColor(R.color.blue_text));
        }
    }

    private String formatTime(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIcon;
        TextView tvTitle, tvDesc, tvTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.ivNotificationIcon);
            tvTitle = itemView.findViewById(R.id.tvNotificationTitle);
            tvDesc = itemView.findViewById(R.id.tvNotificationDesc);
            tvTime = itemView.findViewById(R.id.tvTime);
        }
    }
}