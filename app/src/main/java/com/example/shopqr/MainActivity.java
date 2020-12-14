package com.example.shopqr;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.icu.text.LocaleDisplayNames;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    SurfaceView surfaceView;
    ToggleButton scanButton;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    String intentData = "";
    ArrayList<CodePlate> codes = new ArrayList<CodePlate>();
    String Getter;

    public MainActivity() {
    }

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        initDB();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        Dialog dialog = new Dialog(this);
        dialog.setTitle("Введите получателя");
        dialog.setContentView(R.layout.dialog);
        Button accept = (Button) dialog.findViewById(R.id.accept);
        Button cancel = (Button) dialog.findViewById(R.id.cancel);
        EditText mail = (EditText)dialog.findViewById(R.id.getterId);
        mail.setText(Getter);
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText mail = (EditText)dialog.findViewById(R.id.getterId);
                String text = mail.getText().toString();
                SQLiteDatabase db = getBaseContext().openOrCreateDatabase("mail.db",MODE_PRIVATE,null);
                db.execSQL("UPDATE Mail SET getter = '" + text + "'");
                Getter = text;
                Toast.makeText(getApplicationContext(),text,Toast.LENGTH_SHORT);
                dialog.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();


        return true;
    }

    public void SetGetter(String text){
        SQLiteDatabase db = getBaseContext().openOrCreateDatabase("mail.db",MODE_PRIVATE,null);
        db.execSQL("UPDATE Mail SET getter = '" + text + "'");
        Getter = text;
    }
    private void initDB(){
        SQLiteDatabase db = getBaseContext().openOrCreateDatabase("mail.db",MODE_PRIVATE,null);
        db.execSQL("CREATE TABLE IF NOT EXISTS Mail(getter TEXT)");
        Cursor querry = db.rawQuery("SELECT * FROM Mail",null);
        if(!querry.moveToFirst()){
            db.execSQL("INSERT INTO Mail(getter) VALUES('tiresmailsender@gmail.com')");
        }else{
            Getter = querry.getString(0);
        }

    }
    private void initViews() { // Функция инициализации компонентов взаимодействия
        scanButton = findViewById(R.id.toggleButton);
        surfaceView = findViewById(R.id.surfaceView);
    }
    private void initialiseDetectorsAndSources() {


        barcodeDetector = new BarcodeDetector.Builder(this) // Инициализация детектора
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();

        cameraSource = new CameraSource.Builder(this, barcodeDetector) // камера
                .setRequestedPreviewSize(1920, 1080)
                .setAutoFocusEnabled(true) //you should add this feature
                .build();

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() { // создание холдера для вывода камеры
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                // Чекаем привелегии
                try {
                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(surfaceView.getHolder());
                    } else {
                        ActivityCompat.requestPermissions(MainActivity.this, new
                                String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        // Создаем процесс детектора
        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }
            // Функция обнаружения
            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() != 0) {


                    scanButton.post(new Runnable() {
                        @Override
                        public void run() {
                            if(scanButton.isChecked()){
                                intentData = barcodes.valueAt(0).displayValue;
                                boolean may = true;
                                for(int p = 0; p < codes.size();p++){
                                    String t = codes.get(p).getCode();

                                    if(t.equals(intentData)){
                                        may = false;
                                    }
                                }
                                if(may) {
                                    Toast.makeText(getApplicationContext(),intentData, Toast.LENGTH_SHORT).show();
                                    newPlate(intentData);
                                }
                            }
                        }
                    });

                }
            }
        });
    }
    public void newPlate(String code){ //  Далеко не самое изящное решение, но весьма приемлемое и быстрое (лучше чем 20 ифоф... хе хе)
        LinearLayout list = findViewById(R.id.CodePlates);
        CodePlate codePlate = new CodePlate(this,code);
        ConstraintLayout.LayoutParams textParams = new ConstraintLayout.LayoutParams
                (ConstraintLayout.LayoutParams.WRAP_CONTENT , ConstraintLayout.LayoutParams.WRAP_CONTENT);
        textParams.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
        textParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
        textParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        TextView text = new TextView(this);
        text.setLayoutParams(textParams);
        ConstraintLayout.LayoutParams buttonParams = new ConstraintLayout.LayoutParams
                (ConstraintLayout.LayoutParams.WRAP_CONTENT , 50);
        buttonParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
        buttonParams.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID;
        buttonParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        Button delete = new Button(this);
        delete.setHeight(100);
        delete.setText("Удалить");
        delete.setOnClickListener(this::deletePlate);
        delete.setPadding(0,0,0,0);
        //delete.setBackgroundColor(Color.rgb(100,50,0));
        delete.setLayoutParams(buttonParams);
        text.setText(code);
        codePlate.addView(text);
        codePlate.addView(delete);

        codes.add(codePlate);
        list.addView(codePlate);
    }

    public void  deletePlate(View view){
        Button delete = (Button)view;
        CodePlate plate = (CodePlate)delete.getParent();
        codes.remove(plate);
        LinearLayout list = findViewById(R.id.CodePlates);
        list.removeView(plate);

    }
    public void SendMail(View view){

        String data = "";
        if(codes.size() > 0) {
            for(int p =0;p < codes.size();p++){
                data += (p + 1) + ". " + codes.get(p).getCode() + "\n";
            }
            String finalData = data;
            new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        GMailSender sender = new GMailSender("tiresmailsender@gmail.com",
                                "tiresauto");
                        sender.sendMail("Список покрышек", finalData,
                                "tiresmailsender@gmail.com", Getter);
                    } catch (Exception e) {
                        Log.e("SendMail", e.getMessage(), e);
                    }
                }

            }).start();
            Toast.makeText(getApplicationContext(),"Отправлено",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getApplicationContext(),"Список пуст",Toast.LENGTH_SHORT).show();
        }
    }

    public void Clear(View view){
        codes.clear();
        LinearLayout list = findViewById(R.id.CodePlates);
        list.removeAllViews();
        Toast.makeText(getApplicationContext(),"Очищено",Toast.LENGTH_SHORT).show();
    }
    @Override
    protected void onPause() { //Приложение свернуто - Камера останавливается
        super.onPause();
        cameraSource.release();
    }

    @Override
    protected void onResume() { // Приложение работает - запускаем детектор
        super.onResume();
        initialiseDetectorsAndSources();


    }
}
