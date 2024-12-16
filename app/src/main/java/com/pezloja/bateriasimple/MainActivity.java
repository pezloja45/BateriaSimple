package com.pezloja.bateriasimple;

import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    TextView str_inicio, str_final;
    ImageView btn_play, btn_pause, btn_stop, btn_bombo, btn_caja, btn_platillo, btn_bombos;
    Runnable handlerTask;
    SeekBar sk_fondo;
    MediaPlayer mp_fondo;
    ListView lv_canciones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        str_inicio = findViewById(R.id.str_inicio);
        str_final = findViewById(R.id.str_final);
        btn_play = findViewById(R.id.btn_play);
        btn_pause = findViewById(R.id.btn_pause);
        btn_stop = findViewById(R.id.btn_stop);
        btn_bombo = findViewById(R.id.btn_bombo);
        btn_caja = findViewById(R.id.btn_caja);
        btn_platillo = findViewById(R.id.btn_platillo);
        btn_bombos = findViewById(R.id.btn_bombos);
        sk_fondo = findViewById(R.id.sk_fondo);
        lv_canciones = findViewById(R.id.lv_canciones);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        sk_fondo.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    mp_fondo.seekTo(i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        btn_play.setOnClickListener(view -> {
            if (mp_fondo == null) {
                mp_fondo = MediaPlayer.create(getApplicationContext(), R.raw.fondo);
                mp_fondo.setLooping(true);
                sk_fondo.setMax(mp_fondo.getDuration());
            }
            mp_fondo.start();
            startTimer();
        });
        btn_pause.setOnClickListener(view -> mp_fondo.pause());
        btn_stop.setOnClickListener(view -> {
            mp_fondo.stop();
            mp_fondo.release();
            mp_fondo = MediaPlayer.create(getApplicationContext(), R.raw.fondo);
            mp_fondo.setLooping(true);
        });

        SoundPool sp = new SoundPool.Builder().setMaxStreams(3).build();
        int bombos = sp.load(getApplicationContext(), R.raw.bombos, 1);
        int bombo = sp.load(getApplicationContext(), R.raw.bombo, 1);
        int caja = sp.load(getApplicationContext(), R.raw.caja, 1);
        int platillos = sp.load(getApplicationContext(), R.raw.platillos, 1);

        btn_bombos.setOnClickListener(view -> sp.play(bombos, 1, 1, 0, 0, 1));
        btn_bombo.setOnClickListener(view -> sp.play(bombo, 1, 1, 0, 0, 1));
        btn_caja.setOnClickListener(view -> sp.play(caja, 1, 1, 0, 0, 1));
        btn_platillo.setOnClickListener(view -> sp.play(platillos, 1, 1, 0, 0, 1));

        List lst_canciones = new ArrayList<String>();
        lst_canciones.add("WYA");
        lst_canciones.add("Chill");
        lst_canciones.add("Badgyal");
        lst_canciones.add("Halo");

        lv_canciones.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, lst_canciones));

        lv_canciones.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int posicion, long l) {
                if (posicion == 0) {
                    iniciar(R.raw.wya);
                } else if (posicion == 1) {
                    iniciar(R.raw.fondo);
                } else if (posicion == 2) {
                    iniciar(R.raw.badgyal);
                } else if (posicion == 3) {
                    iniciar(R.raw.halo);
                }
            }
        });
    }

    public void iniciar(int cancion) {
        if (mp_fondo != null && mp_fondo.isPlaying()) {
            mp_fondo.stop();
            mp_fondo.release();
        }
        mp_fondo = MediaPlayer.create(getApplicationContext(), cancion);
        mp_fondo.setLooping(true);
        sk_fondo.setMax(mp_fondo.getDuration());
        mp_fondo.start();
        startTimer();
    }

    public void mostrarTiempos() {
        mostarTimeMMSS(mp_fondo.getCurrentPosition(), str_inicio);
        mostarTimeMMSS(mp_fondo.getDuration(), str_final);
    }

    private void mostarTimeMMSS(long valor, TextView txtView) {
        String formatTime = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(valor)
                , TimeUnit.MILLISECONDS.toSeconds( valor) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes( valor)));
        txtView.setText(formatTime);
    }

    public void startTimer() {
        handlerTask = () -> {
            sk_fondo.setProgress(mp_fondo.getCurrentPosition());
            mostrarTiempos();
            new Handler().postDelayed(handlerTask, 1000);
        };
        handlerTask.run();
    }
}