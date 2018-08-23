package com.example.mengxiangda.demo6_iosimageedit;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, MyChildrenView.Callback {

    private ImageView mIvUndo;
    private ImageView mIvRedo;
    private ImageView mIvSave;
    private MyChildrenView mChildView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();


    }


    private void initView() {
        mIvUndo = (ImageView) findViewById(R.id.iv_undo);
        mIvUndo.setOnClickListener(this);
        mIvRedo = (ImageView) findViewById(R.id.iv_redo);
        mIvRedo.setOnClickListener(this);
        mIvSave = (ImageView) findViewById(R.id.iv_save);
        mIvSave.setOnClickListener(this);
        mChildView = (MyChildrenView) findViewById(R.id.child_view);

        mIvUndo.setEnabled(false);
        mIvRedo.setEnabled(false);
        mChildView.setCallback(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.iv_undo:
                mChildView.undo();
                break;
            case R.id.iv_redo:
                mChildView.redo();
                break;
            case R.id.iv_save:
                Bitmap bm = mChildView.buildBitmap();
                if (bm != null) {
                    String savedFile = saveImage(bm, 100);
                    if (savedFile != null) {
                        scanFile(MainActivity.this, savedFile);
                        Toast.makeText(this, "保存成功", 0).show();
                    }else{
                        //失败
                        Toast.makeText(this, "保存失败", 0).show();
                    }
                }
                break;
        }
    }

    @Override
    public void onUndoRedoStatusChanged() {
        mIvUndo.setEnabled(mChildView.canUndo());
        mIvRedo.setEnabled(mChildView.canRedo());
    }

    private static void scanFile(Context context, String filePath) {
        Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        scanIntent.setData(Uri.fromFile(new File(filePath)));
        context.sendBroadcast(scanIntent);
    }

    private static String saveImage(Bitmap bmp, int quality) {
        if (bmp == null) {
            return null;
        }
        File appDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        if (appDir == null) {
            return null;
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, quality, fos);
            fos.flush();
            return file.getAbsolutePath();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

}
