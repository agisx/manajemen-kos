package com.example.kosmanajemen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private adapter adapter;

    private SwipeRefreshLayout sflRefresh;

    private ImageButton btnInsert;

    private RecyclerView rvList;

    private ModelKos modelKos = new ModelKos();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sflRefresh = findViewById(R.id.sflRefresh);

        btnInsert = findViewById(R.id.btnInsert);

        rvList = findViewById(R.id.rvList);

        adapter = new adapter();
    }

    @Override
    protected void onStart() {
        super.onStart();
        rvList.setHasFixedSize(true);
        rvList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        reload();

        btnInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(isALlPermissionGranted()){
                    Intent intent = new Intent(getApplicationContext(), DetailKosActivity.class);
                    startActivity(intent);
                }
            }
        });

        sflRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                reload();
            }
        });
    }

    private void reload(){
        ApiClient.getKaretEndPoint().readAll()
                .enqueue(new Callback<ModelKos>() {
                    @Override
                    public void onResponse(Call<ModelKos> call, Response<ModelKos> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            adapter.reset();
                            modelKos = response.body();

                            rvList.setAdapter(adapter);
                            sflRefresh.setRefreshing(false);
                        }
                    }

                    @Override
                    public void onFailure(Call<ModelKos> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    public class adapter extends RecyclerView.Adapter<ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.res_adapter_view_kos, parent, false);
            return new SelectedView(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            ModelKos.Kos kos = modelKos.getKoss().get(position);
            holder.tvNamaKos.setText(String.valueOf(kos.getNama()));
            holder.tvHarga.setText(String.valueOf(Float.valueOf(kos.getHarga())/1000) + "K");
            holder.tvAlamat.setText(String.valueOf(kos.getAlamat()));
        }

        @Override
        public int getItemCount() {
            return modelKos.getKoss().size();
        }

        public void reset() {
            modelKos.setKoss(new ArrayList<>());
        }

        public class SelectedView extends ViewHolder{
            public SelectedView(@NonNull View view) {
                super(view);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(isALlPermissionGranted()){
                            DetailKosActivity.kos = modelKos.getKoss().get(getLayoutPosition());
                            startActivity(new Intent(getApplicationContext(), DetailKosActivity.class));
                            finish();
                        }
                    }
                });
            }
        }
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvNamaKos, tvHarga, tvAlamat;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvNamaKos = itemView.findViewById(R.id.tvNamaKos);
            tvHarga = itemView.findViewById(R.id.tvHarga);
            tvAlamat = itemView.findViewById(R.id.tvAlamat);
        }
    }
    public boolean isALlPermissionGranted() {
        // location
        boolean permissionAccesFineLocation = ActivityCompat.checkSelfPermission(
                getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED;
        boolean permissionAccessCoarseLocation = ActivityCompat.checkSelfPermission(
                getApplicationContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED;

        // karet permission
        boolean isAllGranteds =
                        permissionAccesFineLocation && permissionAccessCoarseLocation;
        if (!isAllGranteds) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                ActivityCompat.requestPermissions(this,
                        new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                        }, 1
                );
            }

        }
        return isAllGranteds;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}