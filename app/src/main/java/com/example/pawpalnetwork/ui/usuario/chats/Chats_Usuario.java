package com.example.pawpalnetwork.ui.usuario.chats;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pawpalnetwork.R;

public class Chats_Usuario extends Fragment {

    private ChatsUsuarioViewModel mViewModel;

    public static Chats_Usuario newInstance() {
        return new Chats_Usuario();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chats_usuario, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ChatsUsuarioViewModel.class);
        // TODO: Use the ViewModel
    }

}