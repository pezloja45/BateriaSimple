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

        mp_fondo = MediaPlayer.create(getApplicationContext(), R.raw.fondo);
        mp_fondo.setLooping(true);
        sk_fondo.setMax(mp_fondo.getDuration());
        mp_fondo.start();
        startTimer();

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

        SoundPool sp = new SoundPool.Builder().setMaxStreams(5).build();
        int sonidoBombo = sp.load(getApplicationContext(), R.raw.bombo, 1);
        int sonidoCaja = sp.load(getApplicationContext(), R.raw.caja, 1);
        int sonidoPlatillo = sp.load(getApplicationContext(), R.raw.platillos, 1);

        btn_bombos.setOnClickListener(view -> sp.play(sonidoBombo, 1, 1, 0, 0, 1));
        btn_caja.setOnClickListener(view -> sp.play(sonidoCaja, 1, 1, 0, 0, 1));
        btn_platillo.setOnClickListener(view -> sp.play(sonidoPlatillo, 1, 1, 0, 0, 1));

        List<String> canciones = new ArrayList<>();
        canciones.add("wya");
        canciones.add("Chill");
        canciones.add("halo");
        canciones.add("badgyal");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, canciones);
        lv_canciones.setAdapter(adapter);

        lv_canciones.setOnItemClickListener((adapterView, view, i, l) -> {
            if (i == 0) {
                cambiarCancionFondo(R.raw.wya);
            } else if (i == 1) {
                cambiarCancionFondo(R.raw.fondo);
            } else if (i == 2) {
                cambiarCancionFondo(R.raw.halo);
            } else if (i == 3) {
                cambiarCancionFondo(R.raw.badgyal);
            }
        });
    }

    public void cambiarCancionFondo(int cancion) {
        if (mp_fondo.isPlaying()) {
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