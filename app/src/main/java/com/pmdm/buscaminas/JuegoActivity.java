package com.pmdm.buscaminas;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * Define el funcionamiento del juego. Crea las circunstancias de juego que consiste en alcanzar
 * una meta desde una posición inicial seleccionando las celdas horizontales o verticales adyacentes
 * que no sean fuego.
 * @author Aitor Etxabarren (GPL v3)
 */
public class JuegoActivity extends AppCompatActivity  implements View.OnTouchListener {

    /** Elemento sobre el que dibujar */
    private Lienzo lienzo;
    /** Modelo del juego */
    private Juego juego;
    /** Número de celdas que pueden tener fuego */
    private int fuegos;
    /** Número de filas del tablero */
    private int filas;
    /** Número de columnas del tablero */
    private int columnas;

    /**
     * Cuando se crea la actividad. Definir el juego, poner el lienzo y el listener.
     * @param savedInstanceState Para obtener parámetros
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_juego);

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.LayoutJuego);

        filas = 6;
        columnas = 8;
        fuegos = 10;

        juego = new Juego(filas, columnas, fuegos);

        lienzo = new Lienzo(this, juego);
        lienzo.setOnTouchListener(this);
        layout.addView(lienzo);
    }

    /**
     * Cuando se toca el lienzo. Visita celda si es adyacente a las visitadas.
     * @param v El lienzo
     * @param event Objeto con los detalles del evento
     * @return true
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
            int fila = lienzo.getFila(event.getY());
            int columna = lienzo.getColumna(event.getX());

            if (juego.isFinPartida()){
//                Intent intent = new Intent(JuegoActivity.this,InicioActivity.class);
//                startActivity(intent);
                this.finish();
                return true;
            }

            if (juego.isFinFase()){
                juego.avanzaFase();
                lienzo.invalidate();
                return true;
            }

            if ( (fila>=0) && (fila<filas) && (columna>=0) && (columna<columnas) ) {
                juego.visitaPosicion(fila, columna);

                lienzo.invalidate();
            }
        }
        return true;
    }

}
