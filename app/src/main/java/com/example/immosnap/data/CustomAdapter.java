package com.example.immosnap.data;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.immosnap.R;
import com.example.immosnap.SelectImmoActivity;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder> {
    private ArrayList<ImmoObject> immoObjects;
    private Context context;
    private User user;

    public CustomAdapter(Context context, ArrayList<ImmoObject> immoObjects, User user) {
        this.immoObjects=immoObjects;
        this.context=context;
        this.user = user;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView =layoutInflater.inflate(R.layout.list_row, parent, false);
        return new CustomViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        Drawable res;
        if (immoObjects.get(position).getImg().substring(0, 4).equals("rent")){
            // object von Default-JSON-Datei
            String img = "@drawable/" + immoObjects.get(position).getImg();
            int imageResource = context.getResources().getIdentifier(img, null, context.getPackageName());
            res = context.getResources().getDrawable(imageResource);
        }
        else {
            // new object
            res = Drawable.createFromPath(immoObjects.get(position).getImg());
        }

        holder.immoImage.setImageDrawable(res);
        holder.immoTitle.setText(immoObjects.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return immoObjects.size();
    }


    public class CustomViewHolder extends RecyclerView.ViewHolder {

        ImageView immoImage;
        TextView immoTitle;

        public CustomViewHolder(@NonNull final View itemView) {
            super(itemView);

            immoImage = (ImageView) itemView.findViewById(R.id.list_image);
            immoTitle = (TextView) itemView.findViewById(R.id.list_title);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ImmoObject immoObject = immoObjects.get(getAdapterPosition());
                    Intent intent = new Intent(itemView.getContext(), SelectImmoActivity.class);
                    intent.putExtra("immoObject", immoObject);
                    intent.putExtra("loggedUser", user);
                    intent.putExtra("position", getAdapterPosition());
                    itemView.getContext().startActivity(intent);
                }
            });
        }
    }
}
