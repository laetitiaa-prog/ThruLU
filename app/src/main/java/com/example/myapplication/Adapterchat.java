package com.example.myapplication;



import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Adapterchat extends RecyclerView.Adapter {

    Context context;
    ArrayList<MessageModel> list;


    public Adapterchat(Context context, ArrayList<MessageModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == 0) {
            view = LayoutInflater.from(context).inflate(R.layout.sender, parent, false);
            return new UserView(view);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.receiver, parent, false);
            return new ReceiverView(view);
        }
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MessageModel model = list.get(position);
        Log.d("TYPE", model.getViewType());

        if(getItemViewType(position)==0){
            ((UserView) holder).user.setText(model.getMessage());
        } else{
            ((ReceiverView) holder).receiver.setText(model.getMessage());
        }
    }

    @Override
    public int getItemViewType(int position) {
        String type = list.get(position).getViewType();
        if (type != null && type.equals("user")) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class UserView extends RecyclerView.ViewHolder {
        TextView user;
        public UserView(@NonNull View itemView) {
            super(itemView);
            user=itemView.findViewById(R.id.sender_text);

        }
    }
    public static class ReceiverView extends RecyclerView.ViewHolder{
        TextView receiver;
        public ReceiverView(@NonNull View itemView) {
            super(itemView);
            receiver=itemView.findViewById(R.id.receiver_text);
        }
    }

}

