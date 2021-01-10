package bmp.viewer.converter.fragments;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.codekidlabs.storagechooser.StorageChooser;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import java.io.File;
import java.io.IOException;
import java.nio.BufferOverflowException;
import java.text.SimpleDateFormat;
import java.util.Date;

import bmp.viewer.converter.R;
import bmp.viewer.converter.databinding.FragmentImageViewerBinding;
import bmp.viewer.converter.utils.AndroidBmpUtil;
import bmp.viewer.converter.utils.BmpFile;
import bmp.viewer.converter.utils.BmpFile2;


public class ImageViewerFragment extends Fragment {

    private static final int PERMISSION_REQUEST_CODE = 666;
    FragmentImageViewerBinding binding;
    private InterstitialAd mInterstitialAd;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentImageViewerBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        initializeAds();
        initListeners();
        displayImage();

        return view;
    }

    private void displayImage() {
        binding.imgPhoto.setImageURI(Uri.parse(getPath()));
    }

    private String getPath() {
        return getArguments().getString("path");
    }

    private void initListeners() {
        binding.btnSave.setOnClickListener(v -> chooseLocationDialog());
    }

    private void chooseLocationDialog() {
        StorageChooser chooser = new StorageChooser.Builder()
                .withActivity(getActivity())
                .withFragmentManager(getActivity().getFragmentManager())
                .withMemoryBar(true)
                .allowCustomPath(true)
                .setType(StorageChooser.DIRECTORY_CHOOSER)
                .build();
        chooser.show();

// get path that the user has chosen
        chooser.setOnSelectListener(new StorageChooser.OnSelectListener() {
            @Override
            public void onSelect(String path) {
                saveImageAsBmp(path);
            }
        });
    }

    private void saveImageAsBmp(String path) {
        String sdcardBmpPath = path + "/" + new SimpleDateFormat("yyyyMM_dd-HHmmss").format(new Date()) + ".bmp";
        Bitmap imageBitmap = BitmapFactory.decodeFile(getPath());
        AndroidBmpUtil bmpUtil = new AndroidBmpUtil();

        try {
            if (bmpUtil.save(imageBitmap, sdcardBmpPath)) {
                showDownloadSavedDialog(path);
            } else {
                String sdcardBmpPath2 = path + "/" + new SimpleDateFormat("yyyyMM_dd-HHmmss").format(new Date()) + "file.bmp";
                Bitmap imageBitmap2 = BitmapFactory.decodeFile(getPath());
                BmpFile bmpFile = new BmpFile();
                bmpFile.saveBitmap(sdcardBmpPath2, imageBitmap2);
                showDownloadSavedDialog(path);
            }
        } catch (BufferOverflowException ex) {
            try {
                String sdcardBmpPath3 = path + "/" + new SimpleDateFormat("yyyyMM_dd-HHmmss").format(new Date()) + "_bmp.bmp";
                if (BmpFile2.save(imageBitmap, sdcardBmpPath3)) {
                    showDownloadSavedDialog(path);
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("TAG", "saveImageAsBmp: "+ e.getMessage() );
            }
        }


    }

    private void showDownloadSavedDialog(String path) {
        new AlertDialog.Builder(getActivity())
                .setTitle("IMAGES SAVED SUCCESSFULLY!!")
                .setMessage("Your image has been saved to \n " + path)

                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        showInterstitialAd();
                    }
                })

                .setIcon(android.R.drawable.ic_dialog_info)
                .show();
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

    private void showInterstitialAd() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            Log.e("TAG", "The interstitial wasn't loaded yet.");
        }
    }

    private void initializeAds() {
        MobileAds.initialize(getActivity());

        mInterstitialAd = new InterstitialAd(getActivity());
        mInterstitialAd.setAdUnitId("ca-app-pub-9562015878942760/2108092356");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());


        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when the ad is displayed.
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the interstitial ad is closed.
                initializeAds();
            }
        });


    }

}