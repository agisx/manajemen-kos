package com.example.kosmanajemen;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ModelKos {
    @SerializedName("koss")
    @Expose
    private List<Kos> koss = null;

    public List<Kos> getKoss() {
        return koss;
    }

    public void setKoss(List<Kos> koss) {
        this.koss = koss;
    }
    @SerializedName("success")
    @Expose
    private boolean success;
    @SerializedName("message")
    @Expose
    private String message;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static class Kos{

        public Kos(){
            nama = "";
            harga = 0;
            alamat = "";
            geotagging = "";
        }

        @SerializedName("id")
        @Expose
        private int id;
        @SerializedName("nama")
        @Expose
        private String nama;
        @SerializedName("harga")
        @Expose
        private int harga;
        @SerializedName("alamat")
        @Expose
        private String alamat;
        @SerializedName("geotagging")
        @Expose
        private String geotagging;
        @SerializedName("created_at")
        @Expose
        private String createdAt;
        @SerializedName("updated_at")
        @Expose
        private String updatedAt;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getNama() {
            return nama;
        }

        public void setNama(String nama) {
            this.nama = nama;
        }

        public int getHarga() {
            return harga;
        }

        public void setHarga(int harga) {
            this.harga = harga;
        }

        public String getAlamat() {
            return alamat;
        }

        public void setAlamat(String alamat) {
            this.alamat = alamat;
        }

        public String getGeotagging() {
            return geotagging;
        }

        public void setGeotagging(String geotagging) {
            this.geotagging = geotagging;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }

        @Override
        public String toString() {
            return "Kos{" +
                    "id='" + id + '\'' +
                    ", nama='" + nama + '\'' +
                    ", harga='" + harga + '\'' +
                    ", alamat='" + alamat + '\'' +
                    ", geotagging='" + geotagging + '\'' +
                    ", createdAt='" + createdAt + '\'' +
                    ", updatedAt='" + updatedAt + '\'' +
                    '}';
        }
    }
}
