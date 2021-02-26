package com.example.immosnap.data;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.immosnap.R;
import com.example.immosnap.ReadRequestActivity;
import com.example.immosnap.SelectImmoActivity;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.CustomViewHolder> {

    private ArrayList<ImmoMessage> messageList;
    private User loggedUser;
    private Context context;

    public MessageAdapter(Context context, ArrayList<ImmoMessage> messageList, User loggedUser) {
        this.messageList = messageList;
        this.context = context;
        this.loggedUser = loggedUser;
    }

    @NonNull
    @Override
    public MessageAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView =layoutInflater.inflate(R.layout.message_list_row, parent, false);
        return new CustomViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.CustomViewHolder holder, int position) {
        holder.userName.setText(messageList.get(position).getImmoUserName());
        holder.objectTitle.setText(messageList.get(position).getSelectedImmoObject().getTitle());
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        TextView userName;
        TextView objectTitle;

        public CustomViewHolder(@NonNull final View itemView) {
            super(itemView);

            userName = (TextView) itemView.findViewById(R.id.textViewMessageUsername);
            objectTitle = (TextView) itemView.findViewById(R.id.textViewMessageTitle);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ImmoObject immoObject = messageList.get(getAdapterPosition()).getSelectedImmoObject();
                    if (loggedUser.getRenter()){
                        Intent intent = new Intent(itemView.getContext(), SelectImmoActivity.class);
                        intent.putExtra("immoObject", immoObject);
                        intent.putExtra("loggedUser", loggedUser);
                        intent.putExtra("position", getAdapterPosition());
                        ((Activity) itemView.getContext()).finish();
                        itemView.getContext().startActivity(intent);
                    }
                    else {
                        Intent intent = new Intent(itemView.getContext(), ReadRequestActivity.class);
                        intent.putExtra("immoObject", immoObject);
                        intent.putExtra("senderName", messageList.get(getAdapterPosition()).getImmoUserName());
                        ((Activity) itemView.getContext()).finish();
                        itemView.getContext().startActivity(intent);
                    }
                }
            });
        }
    }
}
