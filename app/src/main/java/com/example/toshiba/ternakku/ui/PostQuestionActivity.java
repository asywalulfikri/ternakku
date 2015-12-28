package com.example.toshiba.ternakku.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.toshiba.ternakku.R;
import com.example.toshiba.ternakku.http.QuestionConnection;
import com.example.toshiba.ternakku.http.exeption.LisaException;
import com.example.toshiba.ternakku.model.User;
import com.example.toshiba.ternakku.ui.adapter.CustomArrayAdapter;
import com.example.toshiba.ternakku.ui.adapter.CustomProgressDialog;
import com.example.toshiba.ternakku.util.BaseActivity;
import com.example.toshiba.ternakku.util.Debug;
import com.example.toshiba.ternakku.util.ImageUtil;
import com.example.toshiba.ternakku.util.StringUtil;
import com.example.toshiba.ternakku.util.Util;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class PostQuestionActivity extends BaseActivity {

    private ImageView mPhotoIv;


    private ImageView postBtn;
    EditText contentEt;
    private RelativeLayout warningView;
    private ArrayList<Integer> mSelectedItems;
    String tagsList[];
    private String mFilePath = "";
    PostQuestionTask mPostQuestionTask;
    private Uri mImageCaptureUri;
    private Uri mImageCropUri;
    private FloatingActionButton fab;

    private static final int PICK_FROM_CAMERA = 1;
    private static final int CROP_FROM_CAMERA = 2;
    private static final int PICK_FROM_FILE = 3;

    @SuppressLint("InlinedApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tambahternak);

        mPhotoIv = (ImageView) findViewById(R.id.fototernak);
        contentEt = (EditText) findViewById(R.id.id_nama_ternak);






        postBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                String content = contentEt.getText().toString().replaceAll("\\s+", " ");

                Log.i("ISI", content);

                if (content.equals("")) {
                    showWarning("error");
                    return;
                }
                (mPostQuestionTask = new PostQuestionTask(content)).execute();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showChooserDialog();
            }
        });

    }


    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }

    public void showAttachment(Bitmap bitmap) {

        mPhotoIv.setVisibility(View.VISIBLE);
        fab.setVisibility(View.GONE);

        mPhotoIv.setImageBitmap(bitmap);
    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Debug.i("On Result");

        if (requestCode == PICK_FROM_FILE  && resultCode == Activity.RESULT_OK) {
            mImageCaptureUri = data.getData();

            doCrop();
        } else if (requestCode == CROP_FROM_CAMERA  && resultCode == Activity.RESULT_OK) {
            mFilePath   = mImageCropUri.getPath();
            File file   = new File(mFilePath);

            if (!file.exists()) {
                showToast("Image crop failed");
            } else {
                Bitmap bitmap = ImageUtil.getBitmapFromFile(mFilePath);


                mPhotoIv.setVisibility(View.VISIBLE);

                mPhotoIv.setImageBitmap(bitmap);


            }
        } else if (requestCode == PICK_FROM_CAMERA   && resultCode == Activity.RESULT_OK) {
            doCrop();
        }
    }

    private void doCrop() {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image/*");

        List<ResolveInfo> list = getActivity().getPackageManager().queryIntentActivities( intent, 0 );

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

            mImageCropUri   = Uri.fromFile(new File(cropFile));

            intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCropUri);

            if (size == 1) {
                Intent i        = new Intent(intent);
                ResolveInfo res = list.get(0);

                i.setComponent( new ComponentName(res.activityInfo.packageName, res.activityInfo.name));

                startActivityForResult(i, CROP_FROM_CAMERA);
            } else {
                Intent i        = new Intent(intent);
                ResolveInfo res = list.get(0);

                i.setComponent( new ComponentName(res.activityInfo.packageName, res.activityInfo.name));

                startActivityForResult(i, CROP_FROM_CAMERA);
            }
        }
    }

    private void showChooserDialog() {
        String[] options = {getString(R.string.text_camera),"gallery"};

        CustomArrayAdapter adapter  = new CustomArrayAdapter(getActivity(), R.layout.list_item_adapter, options);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int position) {
                if (position == 0) {
                    if (isIntentAvailable(getActivity(), MediaStore.ACTION_IMAGE_CAPTURE)) {
                        Intent intent    = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

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
        final Intent intent     = new Intent(action);
        List<ResolveInfo> list  = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);

        return list.size() > 0;
    }

    private void refreshFeed() {
        ((BaseActivity) getActivity()).finish();

        Intent intent = new Intent(getActivity(), MainActivity.class);

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        getActivity().startActivity(intent);
    }

    public class PostQuestionTask extends AsyncTask<URL, Integer, Long> {
        String content,error = "";

        CustomProgressDialog progressDlg;

        User userMode = getUser();

        public PostQuestionTask(String content) {
            this.content	= content;

            progressDlg 	= new CustomProgressDialog(getActivity(),getString(R.string.text_sending));
        }

        protected void onCancelled() {

            progressDlg.dismiss();
        }

        protected void onPreExecute() {

            progressDlg.show();
        }

        protected Long doInBackground(URL... urls) {
            long result = 0;
                try {
                    QuestionConnection conn = new QuestionConnection(getOauthConsumer(), getAccessToken());

                    if (mFilePath.equals("")) {
                        conn.postQuestion(content);
                    } else {
                        conn.postQuestion(content, mFilePath);
                    }

                    result 	= 1;
                } catch (LisaException e) {
                    e.printStackTrace();
                    error  = e.getError();
                } catch (Exception e) {
                    e.printStackTrace();
                }



            return result;
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        protected void onPostExecute(Long result) {
            progressDlg.dismiss();

            if (result == 1) {
                ShowToastNew(getString(R.string.text_post_question_success));

                setResult(RESULT_OK);
                finish();

                refreshFeed();
            } else if (result == 2) {
                ShowToastNew(getString(R.string.no_connection_for_post));

                setResult(RESULT_OK);
                finish();

                refreshFeed();
            } else {
                error = (error.equals("")) ? getString(R.string.text_input_gagal) : error;

                showError(error);
            }
        }
    }
}
