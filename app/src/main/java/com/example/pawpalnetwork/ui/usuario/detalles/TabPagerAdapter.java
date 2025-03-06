package com.example.pawpalnetwork.ui.usuario.detalles;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.pawpalnetwork.bd.Servicio;
import com.example.pawpalnetwork.bd.UsuarioGeneral;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TabPagerAdapter extends FragmentStateAdapter {
    private final List<Fragment> fragmentList = new ArrayList<>();
    private final List<String> fragmentTitleList = new ArrayList<>();
    private final Servicio servicio; // Objeto Servicio
    private final UsuarioGeneral usuario; // Objeto Usuario
    public TabPagerAdapter(@NonNull FragmentActivity fragmentActivity, Servicio servicio, UsuarioGeneral usuario) {
        super(fragmentActivity);
        this.servicio = servicio;
        this.usuario = usuario;
    }

    public void addFragment(Fragment fragment, String title) {
        fragmentList.add(fragment);
        fragmentTitleList.add(title);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment = fragmentList.get(position);

        // Crear un Bundle y agregar ambos objetos
        Bundle args = new Bundle();
        args.putSerializable("servicio", servicio);
        args.putSerializable("usuario", usuario);
        fragment.setArguments(args);

        return fragment;

    }

    @Override
    public int getItemCount() {
        return fragmentList.size();
    }

    public String getPageTitle(int position) {
        return fragmentTitleList.get(position);
    }
}
