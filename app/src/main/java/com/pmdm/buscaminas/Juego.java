package com.pmdm.buscaminas;

import java.util.Random;

public class Juego {

    public static final int BOMBA = 9;
    public static final int VACIO = 0;
    public static final int VISTO = 1;
    public static final int NULO = -1;
    private int filas;
    private int columnas;
    private int bombas;
    private int[][] mapa;
    private int[][] visto;
    private int ultimaColumna;
    private int ultimaFila;

    public Juego(int filas, int columnas, int bombas) {
        this.filas = filas;
        this.columnas = columnas;
        this.bombas = bombas;
        mapa = new int[filas][columnas];
        visto = new int[filas][columnas];
        limpiarVisto();
        limpiarMapa();
        ponerBombas(bombas);
        calcularVecinos();
    }

    public int getColumnas() {
        return columnas;
    }

    public int getFilas() {
        return filas;
    }

    public void limpiarVisto(){
        ponerMatrizValor(visto, VACIO);
    }

    public void limpiarMapa(){
        ponerMatrizValor(mapa, VACIO);
    }

    public void bombearGrid(){
        ponerMatrizValor(mapa, BOMBA);
    }

    private void ponerMatrizValor(int m[][], int v){
        for(int i=0;i<m.length;i++) {
            for (int j = 0; j < m[0].length; j++) {
                m[i][j] = v;
            }
        }
    }

    public void ponerBombas(int n){
        int i,j, v;
        Random random;
        if (n>=filas*columnas){
            bombearGrid();
            return;
        }
        random = new Random();
        while(n>0){
            i = random.nextInt(filas);
            j = random.nextInt(columnas);
            v = mapa[i][j];
            if (v!=BOMBA){
                mapa[i][j] = BOMBA;
                n--;
            }
        }
    }

    public void calcularVecinos() {
        int v, n;
        for(int i=0;i<filas;i++) {
            for (int j = 0; j < columnas; j++) {
                v = mapa[i][j];
                if (v!=BOMBA){
                    n = 0;
                    for(int k=i-1;k<=i+1;k++) {
                        for (int l = j - 1; l <= j + 1; l++) {
                            if (    (k >= 0) && (k <= filas - 1) &&
                                    (l >= 0) && (l <= columnas - 1) &&
                                    (mapa[k][l] == BOMBA)   ) {
                                n++;
                            }
                        }
                    }
                    mapa[i][j] = n;
                }
            }
        }
    }

    public boolean esBomba(int fila, int columna){
        return (mapa[fila][columna]==BOMBA);
    }

    public boolean descubreBomba(int fila, int columna){
        visto[fila][columna] = VISTO;
        ultimaFila = fila;
        ultimaColumna = columna;
        return esBomba(fila,columna);
    }

    /**
     * Devuelve valor para una celda de la matriz
     * @param fila de la matriz
     * @param columna de la matriz
     * @return BOMBA (9) o numero de vecinos (0-8) si se ha visto, o NULO (-1) si no se ha visto
     */
    public int getValor(int fila, int columna){
        if (visto[fila][columna]==VISTO){
            return mapa[fila][columna];
        }else {
            return NULO;
        }
    }

}
