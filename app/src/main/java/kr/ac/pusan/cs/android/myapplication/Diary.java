package kr.ac.pusan.cs.android.myapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Diary extends AppCompatActivity implements View.OnClickListener{
    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int CROP_FROM_IMAGE = 2;
    private Uri uri;
    private String date, no, img, content, updateFlag = "NON";
    private String url;
    MyDBHelper helper;
    SQLiteDatabase db;
    private ImageView image;
    private Button btdelete, btstore;
    private EditText diarywrite;
    private TextView diaryDate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary);
        getSupportActionBar().hide();
        View textView = (View) findViewById(R.id.moduleHeader);
        diaryDate = (TextView) findViewById(R.id.diaryDate);
        image = (ImageView) findViewById(R.id.diaryimage);
        diarywrite = (EditText) findViewById(R.id.diarywrite);
        btdelete = (Button) findViewById(R.id.delete);
        btstore = (Button) findViewById(R.id.store);
        image.setOnClickListener(this);
        btdelete.setOnClickListener(this);
        btstore.setOnClickListener(this);
        Intent intent = getIntent();
        if(Choose_module.UPDATEIS == "0") {
            // 전에 적어놓은 일기가 있을 때
            updateFlag = intent.getExtras().getString("FLAG");
            no = intent.getExtras().getString("no");
            date = intent.getExtras().getString("date");
            img = intent.getExtras().getString("img");
            content = intent.getExtras().getString("content");
            url = img;
            diaryDate.setText(date);
            if(img != null)
                Glide.with(getApplicationContext()).load(img).override(200, 180).into(image);
            if(content != null)
                diarywrite.setText(content);
            Choose_module.UPDATEIS = "X";
            Toast.makeText(getApplicationContext(), "일기가 존재합니다.", Toast.LENGTH_SHORT).show();
        }
        else {
            date = intent.getExtras().getString("date");
            diaryDate.setText(date);
            Toast.makeText(getApplicationContext(), "새 일기를 작성하세요.", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onClick(View v) {

        switch(v.getId()) {
            case R.id.diaryimage: {
                DialogInterface.OnClickListener cameraListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        takePhoto();
                    }
                };
                DialogInterface.OnClickListener albumListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        takeAlbum();
                    }
                };
                DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                };
                new AlertDialog.Builder(this)
                        .setTitle("이미지 선택")
                        .setPositiveButton("사진촬영", cameraListener)
                        .setNeutralButton("앨범선택", albumListener)
                        .setNegativeButton("취소", cancelListener)
                        .show();
                break;
            }
            case R.id.store: {
                Log.d("저장할때", url + diarywrite.getText().toString());
                try {
                    helper = new MyDBHelper(Diary.this);
                    db = helper.getWritableDatabase();
                } catch (SQLException e) {
                    db = helper.getReadableDatabase();
                }
                if (updateFlag.equals("UPDATESIGN")) {
                    StringBuffer buffer = new StringBuffer();
                    buffer.append("UPDATE diary ");
                    buffer.append("SET dimgpath = #imgpath#, dcontent = #content# ");
                    buffer.append("WHERE dno = #no#");
                    String query = buffer.toString();
                    query = query.replace("#imgpath#", "'" + url + "'");
                    query = query.replace("#content#", "'" + diarywrite.getText().toString() + "'");
                    query = query.replace("#no#", "'" + no + "'");
                    db.execSQL(query);
                    db.close();
                    Intent intent = new Intent(Diary.this, Choose_module.class);
                    intent.putExtra("date", date);
                    finish();
                } else {
                    StringBuffer buffer = new StringBuffer();
                    buffer.append("INSERT INTO diary (");
                    buffer.append("dno, ddate, dimgpath, dcontent )");
                    buffer.append("VALUES (?, ?, ?, ?)");
                    db.execSQL(buffer.toString(), new Object[]{null, date, url, diarywrite.getText().toString()});
                    db.close();
                    finish();
                }
                Choose_module.UPDATEIS = "X";
                break;
            }
            case R.id.delete: {
                //삭제 부분 구현
                if(updateFlag.equals("NON")) {
                    Toast.makeText(getApplicationContext(), "일기가 없습니다.", Toast.LENGTH_SHORT).show();
                    break;
                }
                deletediary(no);
                break;
            }
        }

    }
    private void deletediary(String no) {

        AlertDialog.Builder alert = new AlertDialog.Builder(Diary.this);
        alert
                .setMessage("정말 삭제하시겠습니까?")
                .setCancelable(false)
                .setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            helper = new MyDBHelper(Diary.this);
                            db = helper.getWritableDatabase();
                        } catch(SQLiteException e) {

                        }
                        StringBuffer buffer = new StringBuffer();
                        buffer.append("DELETE FROM DIARY WHERE dno = #no#");
                        String query = buffer.toString();
                        query = query.replace("#no#", no);
                        db.execSQL(query);
                        db.close();
                        finish();
                    }
                })
                .setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alert.create();
        alertDialog.show();

    }
    // 사진 촬영하는 부분
    public void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intent.resolveActivity(getPackageManager()) != null) {
            File file = null;
            try {
                File tempDir =getCacheDir();
                String tmp = "tmp_" + String.valueOf(System.currentTimeMillis());
                File tempImage = File.createTempFile(
                        tmp,
                        ".jpg",
                        tempDir
                );
                url = tempImage.getAbsolutePath();
                file = tempImage;
            } catch(IOException e) {
                Log.w("파일 생성", "파일 생성 에러", e);
            }
            if(file != null) {
                uri = FileProvider.getUriForFile(this, getPackageName()+".fileprovider",
                        file);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(intent, PICK_FROM_CAMERA);
            }

        }
    }

    //앨범
    public void takeAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        //GET_CONTENT
        startActivityForResult(intent, PICK_FROM_ALBUM);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != RESULT_OK)
            return;
        switch(requestCode) {
            case PICK_FROM_ALBUM: {
                uri = data.getData();
                Log.d("저장경로", uri.getPath().toString());
                try{
                    url = uri.toString();
                    Glide.with(getApplicationContext()).load(uri).override(200, 180).into(image);

                    break;
                } catch(Exception e) {}
                // break;
            }
            case PICK_FROM_CAMERA:
            case CROP_FROM_IMAGE:
                if(resultCode != RESULT_OK)
                    return;

                if(uri != null) {
                    File file = new File(url);
                    try {
                        Bitmap photo = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(file));;
                        if(Build.VERSION.SDK_INT >= 29) {
                            ImageDecoder.Source source = ImageDecoder.createSource(this.getContentResolver(), Uri.fromFile(file));
                            try {
                                photo = ImageDecoder.decodeBitmap(source);


                            } catch(IOException e) { e.printStackTrace();}
                        }
                        if(photo != null) {
                            ExifInterface ei = new ExifInterface(url);
                            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                                    ExifInterface.ORIENTATION_UNDEFINED);

                        }

                        Glide.with(getApplicationContext()).load(Uri.fromFile(file)).override(200, 180).into(image);
                        break;
                    } catch(IOException e) { e.printStackTrace();}

                }
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Diary.this);
        builder
                .setMessage("페이지를 나가시겠습니까?")
                .setCancelable(false)
                .setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}