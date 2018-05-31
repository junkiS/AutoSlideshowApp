package techacademy.saitou.junki.autoslideshowapp;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_CODE = 100;

    Timer mTimer;
    double mTimerSec;
    Cursor mCursor;//メンバ変数

    Handler mHandler = new Handler();

    Button mNextButton;//送りbtn3
    Button mBuckButton;//戻り btn1
    Button mPlayButton;//再生/停止 btn2


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNextButton = findViewById(R.id.btn3);
        mBuckButton = findViewById(R.id.btn1);
        mPlayButton = findViewById(R.id.btn2);


        // Android 6.0以降の場合
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // パーミッションの許可状態を確認する
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // 許可されている
                getContentsInfo();
            } else {
                Toast.makeText(this, "許可してください", Toast.LENGTH_SHORT).show();
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_CODE);
            }
            // Android 5系以下の場合
        } else {
            getContentsInfo();
        }

        //送り
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCursor.moveToNext()) {
                } else {
                    mCursor.moveToFirst();
                }
                // indexからIDを取得し、そのIDから画像のURIを取得する
                int fieldIndex = mCursor.getColumnIndex(MediaStore.Images.Media._ID);
                Long id = mCursor.getLong(fieldIndex);
                Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
                ImageView imageView = findViewById(R.id.imageView);
                imageView.setImageURI(imageUri);

                //cursor.close();
            }
        });
        mBuckButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCursor.moveToPrevious()) {
                } else {
                    mCursor.moveToLast();
                }
                int fieldIndex = mCursor.getColumnIndex(MediaStore.Images.Media._ID);
                Long id = mCursor.getLong(fieldIndex);
                Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
                ImageView imageView = findViewById(R.id.imageView);
                imageView.setImageURI(imageUri);

                //cursor.close();
            }
        });
        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTimer == null) {
                    mTimer = new Timer();

                    mNextButton.setEnabled(false);//使用できないようにする。
                    mBuckButton.setEnabled(false);
                    mPlayButton.setText("停止");

                    mTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {//TimerTask run
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {//movetonext run
                                    if (mCursor.moveToNext()) {

                                        int fieldIndex = mCursor.getColumnIndex(MediaStore.Images.Media._ID);
                                        Long id = mCursor.getLong(fieldIndex);
                                        Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
                                        ImageView imageView = findViewById(R.id.imageView);
                                        imageView.setImageURI(imageUri);


                                    } else {
                                        mCursor.moveToFirst();

                                        int fieldIndex = mCursor.getColumnIndex(MediaStore.Images.Media._ID);
                                        Long id = mCursor.getLong(fieldIndex);
                                        Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
                                        ImageView imageView = findViewById(R.id.imageView);
                                        imageView.setImageURI(imageUri);


                                    }
                                }
                            });
                        }
                    }, 2000, 2000); //TimerTask
                } else {
                    mNextButton.setEnabled(true);//使用できないようにする。
                    mBuckButton.setEnabled(true);
                    mPlayButton.setText("再生");
                        mTimer.cancel();//タイマー停止
                        mTimer = null;


                }
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContentsInfo();
                }else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        Toast.makeText(this, "許可してください", Toast.LENGTH_SHORT).show();
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_CODE);
                    }
                }
                break;
            default:
                break;
        }
    }

    private void getContentsInfo() {

        // 画像の情報を取得
        ContentResolver resolver = getContentResolver();
        mCursor = resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, // 項目(null = 全項目)
                null, // フィルタ条件(null = フィルタなし)
                null, // フィルタ用パラメータ
                null // ソート (null ソートなし)
        );
    }
}


