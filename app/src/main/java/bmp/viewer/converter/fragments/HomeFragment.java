package bmp.viewer.converter.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.opensooq.supernova.gligar.GligarPicker;

import bmp.viewer.converter.R;
import bmp.viewer.converter.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private static final int PICKER_REQUEST_CODE = 101;
    private static final int PERMISSION_REQUEST_CODE = 102;
    FragmentHomeBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       binding = FragmentHomeBinding.inflate(inflater, container, false);
       View view = binding.getRoot();
       initViews(view);
       initListeners();
       checkPermission();
        return view;
    }

    private void initListeners() {
       binding.btnOpenFile.setOnClickListener(v-> {
           if (checkPermission()) {
               new GligarPicker().requestCode(PICKER_REQUEST_CODE).withFragment(this).limit(1).show();
           }else {

           }
       });
    }

    private void initViews(View view) {

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        switch (requestCode){
            case PICKER_REQUEST_CODE : {
                String pathsList[]= data.getExtras().getStringArray(GligarPicker.IMAGES_RESULT); // return list of selected images paths.
                Bundle bundle = new Bundle();
                bundle.putString("path", pathsList[0]);
                Navigation.findNavController(getView()).navigate(R.id.action_homeFragment_to_imageViewerFragment, bundle);
                break;
            }
        }
    }

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        // request permission if it has not been grunted.
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            return false;
        }
        return true;
    }

}