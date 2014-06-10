package com.diegolorden.todo.tipcalculator.app;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.TextView;

import org.apache.commons.io.FileUtils;
import java.io.File;
import java.io.IOException;

public class TipActivity extends Activity {

    private TextView tvTotalWithTip;
    private EditText etTotal;
    private int selectedTip = 15;
    private SeekBar sbTip;
    private TextView tvCustomTip;
    private Button btnPlusDiner;
    private Button btnMinusDiner;
    private int diners = 1;
    private TextView tvNumDiners;
    private TextView tvTotalPerDiner;
    private TextView tvWarning;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tip);
        etTotal = (EditText) findViewById(R.id.etTotal);
        tvTotalWithTip = (TextView) findViewById(R.id.tvTotalWithTip);
        sbTip = (SeekBar) findViewById(R.id.sbTip);
        tvCustomTip = (TextView) findViewById(R.id.tvCustomTip);
        btnPlusDiner = (Button) findViewById(R.id.btnPlusDiner);
        btnMinusDiner = (Button) findViewById(R.id.btnMinusDiner);
        tvNumDiners = (TextView) findViewById(R.id.tvNumDiners);
        tvTotalPerDiner = (TextView) findViewById(R.id.tvTotalPerDiner);
        tvWarning = (TextView) findViewById(R.id.tvWarning);

        btnPlusDiner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSoftKeyboard(tvNumDiners);
                diners += 1;
                tvNumDiners.setText(String.valueOf(diners));
                updateTotalWithTip();
            }
        });

        btnMinusDiner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSoftKeyboard(tvNumDiners);
                if (diners > 1) {
                    diners -= 1;
                    tvNumDiners.setText(String.valueOf(diners));
                    updateTotalWithTip();
                }
            }
        });

        sbTip.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                hideSoftKeyboard(sbTip);
                tvCustomTip.setText("Tip: " + String.valueOf(i) + "%");
                selectedTip = i;
                updateTotalWithTip();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        etTotal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                updateTotalWithTip();
            }
        });
        loadTipFromFile();
    }


    private File getFile() {
        File filesDir = getFilesDir();
        return new File(filesDir, "tip.txt");
    }

    private void saveTipToFile(Integer tip){
        File f = getFile();
        try {
            FileUtils.writeStringToFile(f, tip.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadTipFromFile() {
        try {
            File f = getFile();
            String savedTip = FileUtils.readFileToString(f);
            selectedTip = Integer.parseInt(savedTip);
            sbTip.setProgress(selectedTip);
        } catch (IOException e){
            e.printStackTrace();
        }
    }


    private void updateTotalWithTip(){
        String totalString = etTotal.getText().toString();
        if (totalString.isEmpty()) {
            tvTotalWithTip.setText("$0");
        } else {
            Double total = Double.valueOf(etTotal.getText().toString());
            total = total + (total * Double.valueOf(selectedTip) / 100);
            Double totalPerDiner = total / diners;
            tvTotalWithTip.setText(String.format("$%.2f", total));
            tvTotalPerDiner.setText(String.format("$%.2f", totalPerDiner));
        }
        saveTipToFile(selectedTip);
        if (diners > 4) {
            tvWarning.setText("Tip might be already included, check the bill.");
        } else {
            tvWarning.setText("");
        }
    }

    public void hideSoftKeyboard(View view){
        InputMethodManager imm =(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
