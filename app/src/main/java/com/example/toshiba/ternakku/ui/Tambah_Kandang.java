package com.example.toshiba.ternakku.ui;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.toshiba.ternakku.R;
import com.example.toshiba.ternakku.ui.adapter.CustomArrayAdapter;
import com.example.toshiba.ternakku.util.BaseActivity;
import com.example.toshiba.ternakku.util.Debug;
import com.example.toshiba.ternakku.util.ImageUtil;
import com.example.toshiba.ternakku.util.StringUtil;
import com.example.toshiba.ternakku.util.Util;

import java.io.File;
import java.util.List;

/**
 * Created by Toshiba on 12/11/2015.
 */
public class Tambah_Kandang extends BaseActivity {

    EditText namakandang, makang;
    ImageView fotokandang;
    private static final int PICK_FROM_CAMERA = 1;
    private static final int CROP_FROM_CAMERA = 2;
    private static final int PICK_FROM_FILE = 3;
    Bitmap bitmap;
    private String mFilePath = "";
    private Uri mImageCaptureUri;
    private Uri mImageCropUri;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tambah_kandang);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);



        fotokandang = (ImageView) findViewById(R.id.fototernak);
        fotokandang.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showChooserDialog();
            }
        });


        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.action_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent home = new Intent(Tambah_Kandang.this,MainActivity.class);
                startActivity(home);
            }
        });
}

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Debug.i("On Result");

        if (requestCode == PICK_FROM_FILE && resultCode == Activity.RESULT_OK) {
            mImageCaptureUri = data.getData();

            doCrop();
        } else if (requestCode == CROP_FROM_CAMERA && resultCode == Activity.RESULT_OK) {
            mFilePath = mImageCropUri.getPath();
            File file = new File(mFilePath);

            if (!file.exists()) {
                showToast("Image crop failed");
            } else {
                Bitmap bitmap = ImageUtil.getBitmapFromFile(mFilePath);


                fotokandang.setVisibility(View.VISIBLE);

                fotokandang.setImageBitmap(bitmap);


            }
        } else if (requestCode == PICK_FROM_CAMERA && resultCode == Activity.RESULT_OK) {
            doCrop();
        }
    }

    private void doCrop() {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image/*");

        List<ResolveInfo> list = getActivity().getPackageManager().queryIntentActivities(intent, 0);

        int size = list.size();

        if (size == 0) {
            showToast("Can not find image crop app");

            return;
        } else {
            intent.setData(mImageCaptureUri);

            String fileName = StringUtil.getFileName(mImageCaptureUri.getPath());
            String cropFile = Util.getAppDir() + "/crop_" + fileName + ".jpg";

            Debug.i(cropFile);
            Debug.i("File " + mImageCaptureUri.getPath());

            mImageCropUri = Uri.fromFile(new File(cropFile));

            intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCropUri);

            if (size == 1) {
                Intent i = new Intent(intent);
                ResolveInfo res = list.get(0);

                i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));

                startActivityForResult(i, CROP_FROM_CAMERA);
            } else {
                Intent i = new Intent(intent);
                ResolveInfo res = list.get(0);

                i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));

                startActivityForResult(i, CROP_FROM_CAMERA);
            }
        }
    }

    private void showChooserDialog() {
        String[] options = {getString(R.string.text_camera), getString(R.string.text_galerry)};

        CustomArrayAdapter adapter = new CustomArrayAdapter(getActivity(), R.layout.list_item_adapter, options);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int position) {
                if (position == 0) {
                    if (isIntentAvailable(getActivity(), MediaStore.ACTION_IMAGE_CAPTURE)) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                        mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),
                                "tmp_avatar_" + String.valueOf(System.currentTimeMillis()) + ".jpg"));

                        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);

                        try {
                            intent.putExtra("return-data", true);
                            startActivityForResult(intent, PICK_FROM_CAMERA);
                        } catch (ActivityNotFoundException e) {
                            e.printStackTrace();
                        }
                    } else {
                        showToast("Sorry, camera is not available");
                    }
                } else if (position == 1) { //gallery
                    Intent intent = new Intent();

                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);

                    startActivityForResult(Intent.createChooser(intent, "Complete action using"), PICK_FROM_FILE);
                }
            }
        });

        builder.create().show();
    }

    private boolean isIntentAvailable(Context context, String action) {
        final PackageManager packageManager = context.getPackageManager();
        final Intent intent = new Intent(action);
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);

        return list.size() > 0;

    }
}

