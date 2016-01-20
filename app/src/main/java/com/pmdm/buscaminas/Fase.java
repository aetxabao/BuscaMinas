package com.pmdm.buscaminas;

/**
 * Define las características de la fase de juego.
 * @author Aitor Etxabarren (GPL v3)
 */
public class Fase {
    private int filas;
    private int columnas;
    private int fuegos;
    private int fondo;

    public Fase(int filas, int columnas, int fuegos, int fondo) {
        this.filas = filas;
        this.columnas = columnas;
        this.fuegos = fuegos;
        this.fondo = fondo;
    }

    public int getFilas() {
        return filas;
    }

    public int getColumnas() {
        return columnas;
    }

    public int getFuegos() {
        return fuegos;
    }

    public int getFondo() {
        return fondo;
    }
}
