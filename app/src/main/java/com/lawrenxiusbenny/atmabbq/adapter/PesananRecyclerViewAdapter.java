package com.lawrenxiusbenny.atmabbq.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chauthai.swipereveallayout.SwipeRevealLayout;

import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.facebook.shimmer.Shimmer;
import com.facebook.shimmer.ShimmerDrawable;
import com.lawrenxiusbenny.atmabbq.R;
import com.lawrenxiusbenny.atmabbq.model.Pesanan;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class PesananRecyclerViewAdapter extends RecyclerView.Adapter<PesananRecyclerViewAdapter.PesananViewHolder>{
    private List<Pesanan> pesananList;
    private List<Pesanan> pesananListFiltered;
    private Context context;
    private View view;
    private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();

    public PesananRecyclerViewAdapter(Context context, List<Pesanan> productList) {
        this.context = context;
        this.pesananList = productList;
        this.pesananListFiltered = productList;
    }

    @NonNull
    @Override
    public PesananRecyclerViewAdapter.PesananViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.list_pesanan, parent, false);

        return new PesananRecyclerViewAdapter.PesananViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PesananRecyclerViewAdapter.PesananViewHolder holder, int position) {
        viewBinderHelper.setOpenOnlyOne(true);
        viewBinderHelper.bind(holder.swipe,String.valueOf(pesananListFiltered.get(position).id_pesanan));
        viewBinderHelper.closeLayout(String.valueOf(pesananListFiltered.get(position).id_pesanan));
        holder.bindData(pesananListFiltered.get(position));
    }

    @Override
    public int getItemCount() {
        return (pesananListFiltered != null) ? pesananListFiltered.size() : 0;
    }

    public class PesananViewHolder extends RecyclerView.ViewHolder {

        private TextView txtNamaMenu, txtHargaMenu, txtJumlahPesanan, txtSubTotal;
        private ImageView ivGambar;
        private LinearLayout mParent;
        private SwipeRevealLayout swipe;
//        private MaterialButton btnOrder;
//        private MaterialTextView txtNot;

        public PesananViewHolder(@NonNull View itemView) {
            super(itemView);
            ivGambar = itemView.findViewById(R.id.gambar_menu_pesanan);
            txtNamaMenu = itemView.findViewById(R.id.txtNamaMenuPesanan);
            txtHargaMenu = itemView.findViewById(R.id.txtHargaMenuPesanan);
            txtJumlahPesanan = itemView.findViewById(R.id.txtJumlahPesanan);
            txtSubTotal = itemView.findViewById(R.id.subTotalPesanan);
            mParent = itemView.findViewById(R.id.linear_layout_pesanan);
            swipe = itemView.findViewById(R.id.swipeLayout);
        }

        void bindData(Pesanan pesanan){
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
            Glide.with(context)
                    .load("http://be.atmabbq.xyz/menus/"+pesanan.getGambar_menu())
                    .placeholder(shimmerDrawable)
                    .into(ivGambar);
            txtNamaMenu.setText(pesanan.getNama_menu());
            if(pesanan.getHarga_menu() == 0){
                txtHargaMenu.setText("Free");
                txtSubTotal.setText("Free");
            }else{
                txtHargaMenu.setText("IDR "+ formatter.format(pesanan.getHarga_menu()));
                txtSubTotal.setText("IDR "+ formatter.format(pesanan.getSub_total()));
            }
            txtJumlahPesanan.setText(String.valueOf(pesanan.getJumlah()));

        }
    }

    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String userInput = charSequence.toString();
                if (userInput.isEmpty()) {
                    pesananListFiltered = pesananList;
                }
                else {
                    List<Pesanan> filteredList = new ArrayList<>();
                    for(Pesanan pesanan : pesananList) {
                        if(String.valueOf(pesanan.getNama_menu()).toLowerCase().contains(userInput) ||
                                pesanan.getStringHarga().toLowerCase().contains(userInput) ||
                                pesanan.getStringSubTotal().toLowerCase().contains(userInput)) {
                            filteredList.add(pesanan);
                        }
                    }
                    pesananListFiltered = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = pesananListFiltered;
                return filterResults;
            }
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                pesananListFiltered = (ArrayList<Pesanan>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}
