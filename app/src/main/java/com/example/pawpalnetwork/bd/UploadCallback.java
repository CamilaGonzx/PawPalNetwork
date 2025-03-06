package com.example.pawpalnetwork.bd;

public interface UploadCallback {
    void onUploadStart();
    void onUploadProgress(int progress); // Opcional, si quieres reportar progreso
    void onUploadSuccess();
    void onUploadFailure(String errorMessage);
}
