package com.lawrenxiusbenny.atmabbq;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.google.zxing.Result;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.lawrenxiusbenny.atmabbq.api.MenuApi;
import com.lawrenxiusbenny.atmabbq.api.PesananApi;
import com.lawrenxiusbenny.atmabbq.model.Menu;
import com.lawrenxiusbenny.atmabbq.model.Pesanan;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import static com.android.volley.Request.Method.GET;

public class QrFragment extends Fragment {

    private View view;
    private View view2;
    private CodeScannerView scannerView;
    private CodeScanner codeScanner;
    private Fragment fragment=null;

    private String nama="";

    MeowBottomNavigation bottomNavigation;

    private SharedPreferences.Editor editor;
    private SharedPreferences sPreferences;
    public static final String KEY_ID = "id_reservasi";

    Dialog dialog;
    Button btnClose;
    TextView txtTitle;

    ProgressBar progressBar;

    private int id_reservasi= 0;

    public QrFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_qr, container, false);
        bottomNavigation = getActivity().findViewById(R.id.bottom_navigation);
        progressBar = view.findViewById(R.id.progressbar_qr);

        dialog = new Dialog(getContext());

        scannerView = view.findViewById(R.id.scannerView);
        codeScanner = new CodeScanner(view.getContext(),scannerView);
        codeScanner.setDecodeCallback(new DecodeCallback() {

            @Override
            public void onDecoded(@NonNull Result result) {
                getActivity().runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.VISIBLE);
                        getDataToPreference(Integer.parseInt(result.getText()));
                        fragment = new MenuFragment();
                        loadFragment(fragment);
                    }
                });
            }
        });

        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                codeScanner.startPreview();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        requestForCamera();
    }

    private void requestForCamera(){
        Dexter.withActivity(getActivity()).withPermission(Manifest.permission.CAMERA).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse response) {
                codeScanner.startPreview();
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse response) {
                Toast.makeText(view.getContext(), "menutup kamera", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).check();
    }

    public void getDataToPreference(int id){
        sPreferences = getActivity().getSharedPreferences("scan", Context.MODE_PRIVATE);
        editor = sPreferences.edit();
        editor.putInt(KEY_ID,id);
        editor.commit();
        Toast.makeText(view.getContext(), String.valueOf(id), Toast.LENGTH_SHORT).show();
    }

    private void loadFragment(Fragment fragment){


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);

                dialog.setContentView(R.layout.success_dialog);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                btnClose = dialog.findViewById(R.id.closeBtn);
                txtTitle = dialog.findViewById(R.id.textTitleSuccess);

//                getDataReservasi();
                txtTitle.setText("Welcome to Atma BBQ !");

                dialog.show();

                btnClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        getActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.frame_layout,fragment)
                                .commit();
                        bottomNavigation.show(1,true);
                        bottomNavigation.setSelected(false);
                        dialog.dismiss();
                    }
                });
            }
        },2000);

    }

//    public void getDataReservasi(){
//        RequestQueue queue = Volley.newRequestQueue(view.getContext());
//
//        final JsonObjectRequest stringRequest = new JsonObjectRequest(GET, PesananApi.ROOT_SELECT_RESERVASI + this.id_reservasi, null, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                try {
//
//
////                    JSONArray jsonArray = jsonObject.getJSONArray("data");
//
////                    Toast.makeText(getContext(), String.valueOf(jsonArray.length()), Toast.LENGTH_SHORT).show();
////                    JSONObject data = jsonArray.getJSONObject(0);
////                    nama = response.optString("message");
//
//                }catch (JSONException e){
//                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.e("error",error.getMessage());
////                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//        queue.add(stringRequest);
//    }
}