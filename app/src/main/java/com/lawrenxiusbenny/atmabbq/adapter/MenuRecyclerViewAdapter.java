package com.lawrenxiusbenny.atmabbq.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.facebook.shimmer.Shimmer;
import com.facebook.shimmer.ShimmerDrawable;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.lawrenxiusbenny.atmabbq.R;
import com.lawrenxiusbenny.atmabbq.model.Menu;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class MenuRecyclerViewAdapter extends RecyclerView.Adapter<MenuRecyclerViewAdapter.MenuViewHolder>{
    private List<Menu> menuList;
    private List<Menu> menuListFiltered;
    private Context context;
    private View view;

    public MenuRecyclerViewAdapter(Context context, List<Menu> productList) {
        this.context = context;
        this.menuList = productList;
        this.menuListFiltered = productList;
    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_list_menu, parent, false);

        return new MenuRecyclerViewAdapter.MenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuRecyclerViewAdapter.MenuViewHolder holder, int position) {
        final Menu menu = menuListFiltered.get(position);

        Shimmer shimmer = new Shimmer.ColorHighlightBuilder()
                .setBaseColor(Color.parseColor("#F3F3F3"))
                .setBaseAlpha(1)
                .setHighlightColor(Color.parseColor("#E7E7E7"))
                .setHighlightAlpha(1)
                .setDropoff(50)
                .build();

        ShimmerDrawable shimmerDrawable = new ShimmerDrawable();
        shimmerDrawable.setShimmer(shimmer);

        NumberFormat formatter = new DecimalFormat("#,###");
        holder.txtNama.setText(menu.getNama_menu());
        holder.txtdeskripsi.setText(menu.getDeskripsi_menu());
        if(menu.getServing_size() > menu.getStok_bahan()){
            holder.txtNot.setVisibility(View.VISIBLE);
            holder.btnOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Dialog dialog;
                    dialog = new Dialog(context);
                    dialog.setContentView(R.layout.dialog_kosong);
                    dialog.show();
                    MaterialButton btnClose = dialog.findViewById(R.id.closeBtnKosong);
                    btnClose.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });
                }
            });
        }else{
            holder.btnOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context, "OKE BENTAR YA EVA, GAK TAKUT GENDUT ??", Toast.LENGTH_SHORT).show();
                }
            });
        }


        if(menu.getHarga_menu() == 0){
            holder.txtHarga.setText("Free");
        }else{
            holder.txtHarga.setText("IDR "+ formatter.format(menu.getHarga_menu()));
        }
        Glide.with(context)
                .load("http://be.atmabbq.xyz/menus/"+menu.getGambar_menu())
                .placeholder(shimmerDrawable)
                .into(holder.ivGambar);


//
//        holder.mParent.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return (menuListFiltered != null) ? menuListFiltered.size() : 0;
    }

    public class MenuViewHolder extends RecyclerView.ViewHolder {

        private TextView txtNama, txtHarga, txtdeskripsi;
        private ImageView ivGambar;
        private LinearLayout mParent;
        private MaterialButton btnOrder;
        private MaterialTextView txtNot;

        public MenuViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNama = itemView.findViewById(R.id.txtNamaMenu);
            txtdeskripsi = itemView.findViewById(R.id.txtDeskripsiMenu);
            txtHarga = itemView.findViewById(R.id.txtHargaMenu);
            ivGambar = itemView.findViewById(R.id.imageMenu);
            mParent = itemView.findViewById(R.id.linear_layout_menu);
            btnOrder = itemView.findViewById(R.id.btnOrder);
            txtNot = itemView.findViewById(R.id.txtNot);
        }
    }

    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String userInput = charSequence.toString();
                if (userInput.isEmpty()) {
                    menuListFiltered = menuList;
                }
                else {
                    List<Menu> filteredList = new ArrayList<>();
                    for(Menu menu : menuList) {
                        if(String.valueOf(menu.getNama_menu()).toLowerCase().contains(userInput) ||
                                menu.getStringHarga().toLowerCase().contains(userInput) ||
                                menu.getDeskripsi_menu().toLowerCase().contains(userInput)) {
                            filteredList.add(menu);
                        }
                    }
                    menuListFiltered = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = menuListFiltered;
                return filterResults;
            }
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                menuListFiltered = (ArrayList<Menu>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}
