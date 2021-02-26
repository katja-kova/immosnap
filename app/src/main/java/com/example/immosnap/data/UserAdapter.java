package com.example.immosnap.data;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.immosnap.ListUserActivity;
import com.example.immosnap.R;
import com.example.immosnap.RequestImmoActivity;
import com.example.immosnap.SelectImmoActivity;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;

import static android.content.Context.MODE_PRIVATE;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.CustomViewHolder> {
    private ArrayList<User> usersList;
    private Context context;
    private User sender;

    public UserAdapter(Context context, ArrayList<User> usersList, User sender) {
        this.usersList = usersList;
        this.context = context;
        this.sender = sender;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView =layoutInflater.inflate(R.layout.user_list_row, parent, false);
        return new CustomViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final CustomViewHolder holder, int position) {

        holder.userName.setText(usersList.get(position).getUsername());

    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }


    public class CustomViewHolder extends RecyclerView.ViewHolder {

        TextView userName;

        public CustomViewHolder(@NonNull final View itemView) {
            super(itemView);

            userName = (TextView) itemView.findViewById(R.id.userListName);

            //on Item Click
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!sender.getRenter()){
                        Intent intent = new Intent();
                        intent.putExtra("receiver", usersList.get(getAdapterPosition()));
                        ((Activity) itemView.getContext()).setResult(Activity.RESULT_OK, intent);
                        ((Activity) itemView.getContext()).finish();
                        Toast.makeText(v.getContext(), "Send Message to Renter", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Intent intent = new Intent(itemView.getContext(), RequestImmoActivity.class);
                        intent.putExtra("sender", sender.getUsername());
                        intent.putExtra("receiver", usersList.get(getAdapterPosition()));
                        itemView.getContext().startActivity(intent);
                        ((Activity) itemView.getContext()).finish();
                    }
                }
            });

        }
    }
}
