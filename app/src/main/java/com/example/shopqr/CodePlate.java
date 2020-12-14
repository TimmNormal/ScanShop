package com.example.shopqr;

import android.content.Context;
import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;

public class CodePlate extends ConstraintLayout {
    public String code;
    public CodePlate(Context context, String codeValue) {
        super(context);
        code = codeValue;
    }

    public String getCode() {
        return code;
    }

}
