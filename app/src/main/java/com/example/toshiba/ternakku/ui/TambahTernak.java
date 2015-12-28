package com.example.toshiba.ternakku.ui;

import android.app.Activity;
import android.app.DatePickerDialog;
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
import android.widget.Button;
import android.widget.DatePicker;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by Toshiba on 12/7/2015.
 */
public class TambahTernak  extends BaseActivity {
    EditText namahewan, berat, tanggallahir, indukjantan, indukbetina, kandang;
    ImageView fotoernak;
    Button SImpan;
    private static final int PICK_FROM_CAMERA = 1;
    private static final int CROP_FROM_CAMERA = 2;
    private static final int PICK_FROM_FILE = 3;
    Bitmap bitmap;
    private String mFilePath = "";
    private Uri mImageCaptureUri;
    private Uri mImageCropUri;



    Calendar myCalendar = Calendar.getInstance();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tambahternak);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.action_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent home = new Intent(TambahTernak.this,MainActivity.class);
                startActivity(home);
            }
        });

        tanggallahir = (EditText) findViewById(R.id.tgllahir);
        tanggallahir.setFocusableInTouchMode(false);
        tanggallahir.setFocusable(false);

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };

        tanggallahir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(TambahTernak.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();

            }
        });

        fotoernak = (ImageView) findViewById(R.id.fototernak);
        fotoernak.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showChooserDialog();
            }
        });

    }


    private void updateLabel() {

        String myFormat = "dd/MM/yyyy"; // In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        tanggallahir.setText(sdf.format(myCalendar.getTime()));
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


                fotoernak.setVisibility(View.VISIBLE);

                fotoernak.setImageBitmap(bitmap);


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
