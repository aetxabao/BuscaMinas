package com.pmdm.buscaminas;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

public class JuegoActivity extends AppCompatActivity  implements View.OnTouchListener {

    private Lienzo lienzo;
    private Juego juego;
    private int bombas, filas, columnas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_juego);

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.LayoutJuego);

        filas = 4;
        columnas = 6;
        bombas = 4;

        juego = new Juego(filas, columnas, bombas);

        lienzo = new Lienzo(this, juego);
        lienzo.setOnTouchListener(this);
        layout.addView(lienzo);
    }

    public boolean onTouch(View v, MotionEvent event) {
        int fila = lienzo.getFila(event.getY());
        int columna = lienzo.getColumna(event.getX());

        juego.descubreBomba(fila, columna);

        lienzo.invalidate();

        return true;
    }

}
