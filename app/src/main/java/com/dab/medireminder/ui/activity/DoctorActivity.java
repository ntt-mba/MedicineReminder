package com.dab.medireminder.ui.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dab.medireminder.R;
import com.dab.medireminder.base.BaseActivity;
import com.dab.medireminder.constant.Constants;
import com.dab.medireminder.data.model.DoctorNote;
import com.dab.medireminder.ui.adapter.PrescriptionAdapter;
import com.dab.medireminder.ui.fragment.DialogGalleryFragment;
import com.dab.medireminder.utils.AppUtils;
import com.dab.medireminder.utils.RecyclerUtils;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.dab.medireminder.constant.Constants.REQUEST_PICK_IMAGE_CAMERA;
import static com.dab.medireminder.constant.Constants.REQUEST_PICK_IMAGE_GALLERY;

public class DoctorActivity extends BaseActivity implements PrescriptionAdapter.PrescriptionListener, DialogGalleryFragment.GalleryListener {

    private static final String TAG = "DoctorActivity";

    @BindView(R.id.rv_prescription)
    RecyclerView rvPrescription;

    @BindView(R.id.edt_note)
    EditText edtNote;

    @BindView(R.id.rl_main)
    View rlMain;

    @BindView(R.id.tv_save)
    TextView tvSave;

    private PrescriptionAdapter prescriptionAdapter;
    private List<String> imageList;
    private DialogGalleryFragment dialogGalleryFragment;
    private boolean isEdit = false;
    private SharedPreferences sharedPreferences;
    private DoctorNote doctorNote;
    private String actionCurrent;
    private String imagePath;

    @Override
    public int getLayout() {
        return R.layout.activity_doctor;
    }

    @Override
    public void initView() {
        RecyclerUtils.setupHorizontalRecyclerView(this, rvPrescription);
        hideKeyboard(rlMain);
        sharedPreferences = getSharedPreferences(Constants.NAME_SHARED_PREFERENCE, MODE_PRIVATE);
    }

    @Override
    public void setEvents() {
    }

    @Override
    public void setData() {
        String json = sharedPreferences.getString(Constants.KEY_PRESCRIPTION, "");
        if (!TextUtils.isEmpty(json)) {
            doctorNote = new Gson().fromJson(json, new TypeToken<DoctorNote>() {
            }.getType());
        }
        dialogGalleryFragment = new DialogGalleryFragment(this);
        loadData();

        if (doctorNote != null && (!TextUtils.isEmpty(doctorNote.getNote()) || (doctorNote.getPrescriptions() != null && doctorNote.getPrescriptions().size() > 0))) {
            edtNote.setText(doctorNote.getNote());
            enableEdit(false);
        } else {
            enableEdit(true);
        }
    }

    private void loadData() {
        imageList = new ArrayList<>();
        if (doctorNote != null && doctorNote.getPrescriptions() != null && doctorNote.getPrescriptions().size() > 0) {
            imageList.addAll(doctorNote.getPrescriptions());
        } else {
            imageList.add("ADD");
        }
        prescriptionAdapter = new PrescriptionAdapter(this, imageList, this);
        rvPrescription.setAdapter(prescriptionAdapter);
    }

    private void enableEdit(boolean isEdited) {
        this.isEdit = isEdited;
        if (isEdited) {
            tvSave.setText("Lưu lại");
            edtNote.setEnabled(true);
        } else {
            tvSave.setText("Chỉnh sửa");
            edtNote.setEnabled(false);
        }
    }

    @OnClick(R.id.btn_close)
    public void closeScreen() {
        if (isEdit && (!TextUtils.isEmpty(edtNote.getText().toString()) || imageList.size() > 1)) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Lưu đơn thuốc")
                    .setMessage("Bạn có muốn lưu lại ghi chú của bác sĩ không?");

            alertDialogBuilder.setNegativeButton("Không", (dialogInterface, i) -> {
                dialogInterface.dismiss();
                finish();
            });

            alertDialogBuilder.setPositiveButton("Có", (dialog, id) -> {
                saveNoteDoctor();
                dialog.dismiss();
                finish();
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        } else {
            finish();
        }
    }

    @OnClick(R.id.btn_save)
    public void saveNoteDoctor() {
        if (isEdit) {
            if (doctorNote == null) {
                doctorNote = new DoctorNote();
            }
            doctorNote.setNote(edtNote.getText().toString());
            doctorNote.setPrescriptions(imageList);
            String json = new Gson().toJson(doctorNote);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(Constants.KEY_PRESCRIPTION, json);
            editor.apply();
            enableEdit(false);
        } else {
            enableEdit(true);
        }
    }

    @Override
    public void onZoomImage(String image) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        View mView = getLayoutInflater().inflate(R.layout.alert_preview_photo, null);
        PhotoView photoView = mView.findViewById(R.id.photo_view);
        CardView btnClose = mView.findViewById(R.id.btn_close);
        Glide.with(this).load(image).into(photoView);

        mBuilder.setView(mView);
        AlertDialog mDialog = mBuilder.create();
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog.show();

        btnClose.setOnClickListener(v -> mDialog.dismiss());
    }

    @Override
    public void onClickAddImage() {
        if (isEdit) {
            if (dialogGalleryFragment != null && !dialogGalleryFragment.isShow) {
                dialogGalleryFragment.show(getSupportFragmentManager(), dialogGalleryFragment.getTag());
            }
        } else {
            AppUtils.toast(this, "Nhấn vào nút chỉnh sửa để thay đổi");
        }
    }

    @Override
    public void onClickDeleteImage(String image, int position) {

    }

    private void hideKeyboard(View view) {
        if (!(view instanceof EditText)) {
            view.setOnTouchListener((v, event) -> {
                AppUtils.hideSoftKeyboard(v, DoctorActivity.this);
                return false;
            });
        }
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                hideKeyboard(innerView);
            }
        }
    }

    @Override
    public void onChooseAction(String action) {
        actionCurrent = action;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int PERMISSION_ALL = 1;
            String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,};
            if (!AppUtils.hasPermissions(this, PERMISSIONS)) {
                ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
            } else {
                selectImage(action);
            }
        } else {
            selectImage(action);
        }
    }

    private void selectImage(String action) {
        if (action.equals(DialogGalleryFragment.GALLERY)) {
            Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(pickPhoto, REQUEST_PICK_IMAGE_GALLERY);
        } else if (action.equals(DialogGalleryFragment.CAMERA)) {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_FINISH_ON_COMPLETION, true);
            if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                File pictureFile;
                try {
                    pictureFile = getPictureFile();
                } catch (IOException ex) {
                    AppUtils.toast(this,
                            "Không thể tạo tệp ảnh, vui lòng thử lại");
                    return;
                }
                if (pictureFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(this,
                            "com.dab.medireminder.provider",
                            pictureFile);
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                }
                startActivityForResult(cameraIntent, REQUEST_PICK_IMAGE_CAMERA);
            } else {
                AppUtils.toast(this,
                        "Không thể mở camera trên thiết bị của bạn.");
            }
        }
    }

    private File getPictureFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String pictureFile = "IMG_MEDICINE_" + timeStamp;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(pictureFile, ".jpg", storageDir);
        imagePath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (resultCode == RESULT_OK) {
                if (requestCode == REQUEST_PICK_IMAGE_GALLERY  && data != null) {
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    if (selectedImage != null) {
                        Cursor cursor = getContentResolver().query(selectedImage,
                                filePathColumn, null, null, null);
                        if (cursor != null) {
                            cursor.moveToFirst();

                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            String picturePath = cursor.getString(columnIndex);
                            Log.e(TAG, "onActivityResult: " + picturePath);
                            imageList.add(0, picturePath);
                            prescriptionAdapter.notifyDataSetChanged();
                            cursor.close();
                            new Handler().postDelayed(() -> rvPrescription.scrollToPosition(0), 100);
                        }
                    }
                } else if (requestCode == REQUEST_PICK_IMAGE_CAMERA) {
                    File imgFile = new File(imagePath);
                    if (imgFile.exists()) {
                        imageList.add(0, imagePath);
                        prescriptionAdapter.notifyDataSetChanged();
                        new Handler().postDelayed(() -> rvPrescription.scrollToPosition(0), 100);
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "onActivityResult: " + e.toString());
        }
    }

    private Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Prescription_" + System.currentTimeMillis(), null);
        return Uri.parse(path);
    }

    private String getRealPathFromURI(Uri uri) {
        String path = "";
        if (getContentResolver() != null) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                path = cursor.getString(idx);
                cursor.close();
            }
        }
        return path;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    selectImage(actionCurrent);
                } else {
                    AppUtils.toast(this, "Ứng dung cần sử dụng quyền truy cập vào thư viện ảnh và camera");
                }
            }
        }
    }
}
