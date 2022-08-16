package com.example.petcare.adapter;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petcare.R;
import com.example.petcare.ShoppingList;

import java.util.List;

import Model.ToDo;

class ListItemViewHolder extends RecyclerView.ViewHolder implements OnClickListener,View.OnCreateContextMenuListener {

    ItemClickListener itemClickListener;
    TextView item_title;

    public ListItemViewHolder(@NonNull View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
        itemView.setOnCreateContextMenuListener(this);

        item_title=(TextView) itemView.findViewById(R.id.product_title);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view,getAdapterPosition(),false);

    }

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {

        contextMenu.setHeaderTitle("select the action");
        contextMenu.add(0,0,getAdapterPosition(),"מחק");


    }
}

public class ListItemAdapter extends RecyclerView.Adapter<ListItemViewHolder>{
    ShoppingList shoppingList;
    List<ToDo> toDoList;

    public ListItemAdapter(ShoppingList shoppingList, List<ToDo> toDoList) {
        this.shoppingList = shoppingList;
        this.toDoList = toDoList;
    }

    @NonNull
    @Override
    public ListItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(shoppingList.getBaseContext());
        View view=inflater.inflate(R.layout.product_list,parent,false);
        return new ListItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListItemViewHolder holder, int position) {

        //set data for item
        holder.item_title.setText(toDoList.get(position).getTitle());

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                //when user select item,the data will set in editText
                shoppingList.title.setText(toDoList.get(position).getTitle());

                shoppingList.isUpdate=true;
                shoppingList.idUpdate=toDoList.get(position).getId();
            }
        });


    }

    @Override
    public int getItemCount() {
        return toDoList.size();
    }
}
