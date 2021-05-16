package com.lawrenxiusbenny.atmabbq.adapter;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.facebook.shimmer.Shimmer;
import com.facebook.shimmer.ShimmerDrawable;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.lawrenxiusbenny.atmabbq.MainActivity;
import com.lawrenxiusbenny.atmabbq.R;
import com.lawrenxiusbenny.atmabbq.api.PesananApi;
import com.lawrenxiusbenny.atmabbq.model.Menu;
import com.lawrenxiusbenny.atmabbq.model.Pesanan;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.android.volley.Request.Method.GET;
import static com.android.volley.Request.Method.POST;

public class MenuRecyclerViewAdapter extends RecyclerView.Adapter<MenuRecyclerViewAdapter.MenuViewHolder>{
    private List<Menu> menuList;
    private List<Menu> menuListFiltered;
    private Context context;
    private Context mainContext;
    private View view;
    MeowBottomNavigation bottomNavigation;

    private SharedPreferences sPreferences;
    public static final String KEY_ID = "id_reservasi";
    int id_reservasi;

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
        View view2 = layoutInflater.inflate(R.layout.activity_main,parent,false);
        bottomNavigation = view2.findViewById(R.id.bottom_navigation);

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
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
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

                    sPreferences = context.getSharedPreferences("scan", Context.MODE_PRIVATE);
                    id_reservasi = sPreferences.getInt(KEY_ID,Context.MODE_PRIVATE);
                    if(id_reservasi==0){
                        Dialog dialog;
                        dialog = new Dialog(context);
                        dialog.setContentView(R.layout.dialog_scanning);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.show();
                        Button btnClose = dialog.findViewById(R.id.btnCloseScanning);
                        btnClose.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                            }
                        });
                    }else{
                        Dialog dialog;
                        dialog = new Dialog(context);
                        dialog.setContentView(R.layout.dialog_tambah_edit);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.show();
                        MaterialButton btnCancel = dialog.findViewById(R.id.btnCancel);
                        MaterialButton btnAdd = dialog.findViewById(R.id.btnAddEdit);
                        TextInputEditText txtInput = dialog.findViewById(R.id.txtInputEdtTambah);
                        ImageView imgAdd = dialog.findViewById(R.id.ivTambahEdit);
                        TextView txtAvailable = dialog.findViewById(R.id.jumlahTersediaAdd);
                        TextView txtNamaAdd = dialog.findViewById(R.id.namaMenuAdd);
                        TextView txtHargaAdd = dialog.findViewById(R.id.HargaMenuAdd);

                        double stok = menu.getStok_bahan();
                        double serving = menu.getServing_size();

                        double available = Math.floor(stok/serving);
                        txtNamaAdd.setText(menu.getNama_menu());
                        if(menu.getHarga_menu()==0){
                            txtHargaAdd.setText("Free");
                        }else{
                            txtHargaAdd.setText("IDR "+ formatter.format(menu.getHarga_menu()));
                        }
                        Glide.with(context)
                                .load("http://be.atmabbq.xyz/menus/"+menu.getGambar_menu())
                                .placeholder(shimmerDrawable)
                                .into(imgAdd);
                        txtAvailable.setText(String.valueOf(available));

                        btnCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                            }
                        });

                        btnAdd.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if(txtInput.getText().toString().length() == 0){
                                    txtInput.setError("cannot be null");
                                }else{
                                    int input = Integer.parseInt(txtInput.getText().toString());
                                    if(input < 1){
                                        txtInput.setError("should be at least 1");
                                    } else if(input>available) {
                                        txtInput.setError("should be less than available number");
                                    }else{
                                        addPesanan(menu.getId_menu(),id_reservasi,Integer.parseInt(txtInput.getText().toString()));
                                        dialog.dismiss();
                                        Intent i = new Intent(context,MainActivity.class);
                                        context.startActivity(i);
                                    }
                                }
                            }
                        });
                    }
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

    public void addPesanan(final int id_menu, final int id_reservasi, final int jumlah){
        RequestQueue queue = Volley.newRequestQueue(context);

        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("loading....");
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(POST, PesananApi.ROOT_INSERT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                try {
                    JSONObject obj = new JSONObject(response);
                    Toast.makeText(context, obj.getString("message"), Toast.LENGTH_SHORT).show();

                    //untuk update bottomnavigation jumlah cart
//                    bottomNavigation = ()
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Network unstable, please try again", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(context, "Network unstable, please try again", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        }){
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("id_menu", String.valueOf(id_menu));
                params.put("id_reservasi", String.valueOf(id_reservasi));
                params.put("jumlah", String.valueOf(jumlah));

                return params;
            }
        };
        queue.add(stringRequest);
    }
}
