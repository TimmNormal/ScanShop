package com.example.shopqr;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.zip.Inflater;

public class CodePlate extends ConstraintLayout {
    public String code;
    MainActivity cont;
    public CodePlate(Context context, String codeValue) {
        super(context);
        cont = (MainActivity) context;
        code = codeValue;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.code,this);
        initViews();
    }
    void initViews(){
        Button delete = findViewById(R.id.deleteButton);
        delete.setOnClickListener(this::delete);
        TextView codeText = findViewById(R.id.CodeText);
        codeText.setText(code);
    }
    public void delete(View view){
        cont.deletePlate(this);

    }
    public String getCode() {
        return code;
    }

}
