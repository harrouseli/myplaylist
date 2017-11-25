package fr.eisti.android.myplaylist.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import fr.eisti.android.myplaylist.services.BackgroundSoundService;
import fr.eisti.android.myplaylist.R;

/**
 * Created by Harrous Elias on 07/03/2015
 * This activity allows the user to control the volume of the application
 */
public class SeekBarActivity extends ActionBarActivity implements SeekBar.OnSeekBarChangeListener, View.OnClickListener {

    private SeekBar seekBar;

    private int minValue;
    private int maxValue;
    private int currentValue;

    public SeekBarActivity() {
        super();
        minValue = 0;
        maxValue = 100;
    }

    public SeekBarActivity(Context context) {
        super();
        minValue = 0;
        maxValue = 100;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        currentValue = progress + minValue;
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.volume_slider, (ViewGroup) findViewById(R.id.volume_layout));
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setView(layout)
                .setCancelable(false);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        currentValue = (int) (BackgroundSoundService.volume*100);
        seekBar = (SeekBar)layout.findViewById(R.id.seek_bar);
        seekBar.setMax(maxValue-minValue);
        seekBar.setProgress(currentValue);
        seekBar.setOnSeekBarChangeListener(this);
    }


    public void validate(View view) {
        BackgroundSoundService.volume = (float) currentValue/100;
        BackgroundSoundService.modifyVolume();
        finish();
    }

    public void cancel(View view) {
        finish();
    }
}
