package com.pmdm.buscaminas;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.view.View;

/**
 * Define la visualización de la pantalla de juego.
 * @author Aitor Etxabarren (GPL v3)
 */
public class Lienzo extends View {

    /** Referencia la modelo de juego */
    private Juego juego;
    /** Contexto de ejecución */
    private Context context;
    /** Offset izquierdo */
    private static final int LEFT_OFFSET=80;

    /**
     * Constructor que define las circunstacias
     * @param context Contexto de ejecución
     * @param juego Referencia la modelo de juego
     */
    public Lienzo(Context context, Juego juego) {
        super(context);
        this.context = context;
        this.juego = juego;
    }

    /**
     * Traduce coordenada 'y' en fila del tablero
     * @param y coordenada vertical
     * @return fila correspondiente
     */
    public int getFila(double y){
        int heightCell = this.getHeight() / juego.getFilas();
        return (int)Math.floor(y/ heightCell);
    }

    /**
     * Traduce coordenada 'x' en columna del tablero
     * @param x coordenada horizontal
     * @return columna correspondiente (-1 en el offset izquierdo)
     */
    public int getColumna(double x) {
        int widthCell = (this.getWidth()-LEFT_OFFSET) / juego.getColumnas();
        return (int)Math.floor((x-LEFT_OFFSET)/ widthCell);
    }

    /**
     * Cuando haya que pintar (tras invalidación)
     * @param canvas lienzo sobre el que dibujar
     */
    protected void onDraw(Canvas canvas) {
        pintarFondo(canvas);
        pintarLineas(canvas);
        pintarVecinos(canvas);
        pintarVisitables(canvas);
        pintarInicio(canvas);
        pintarFin(canvas);
        pintarHUD(canvas);
        if (juego.isFinPartida()){
            pintarFinPartida(canvas);
        }
        if (juego.isFinFase()){
            pintarFinFase(canvas);
        }
    }

    /**
     * Dibuja una imagen de fondo
     * @param canvas lienzo sobre el que dibujar
     */
    private void pintarFondo(Canvas canvas){
        Resources res = getResources();
        Bitmap bitmap = BitmapFactory.decodeResource(res, juego.getFase().getFondo());
        Rect srcRect = new Rect(0,0,bitmap.getWidth(),bitmap.getHeight());
        Rect dstRect = canvas.getClipBounds();
        dstRect.left = LEFT_OFFSET;
        canvas.drawBitmap(bitmap, srcRect, dstRect, null);
        //canvas.drawBitmap(bitmap, 0, 0, null);
    }

    /**
     * Dibuja las líneas que definen las celdas
     * @param canvas lienzo sobre el que dibujar
     */
    private void pintarLineas(Canvas canvas){
        int canvasWidth = (canvas.getWidth()-LEFT_OFFSET);
        int canvasHeight = canvas.getHeight();
        Paint paint = new Paint();
        paint.setARGB(75, 175, 175, 175);
        paint.setStrokeWidth(4);
        paint.setStyle(Paint.Style.STROKE);
        int cellHeight = canvas.getHeight() / juego.getFilas();
        for(int i=0; i<=juego.getFilas();i++){
            canvas.drawLine(LEFT_OFFSET, i*cellHeight, LEFT_OFFSET+canvasWidth, i*cellHeight, paint);
        }
        int cellWidth = (canvas.getWidth()-LEFT_OFFSET) / juego.getColumnas();
        for(int j=0; j<=juego.getColumnas();j++){
            canvas.drawLine(LEFT_OFFSET+j*cellWidth, 0, LEFT_OFFSET+j*cellWidth, canvasHeight, paint);
        }
    }

    /**
     * Escribe cuantas celdas vecinas no son fuego o si la propia celda es fuego.
     * También si la celda tiene premio o vida extra.
     * @param canvas lienzo sobre el que dibujar
     */
    private void pintarVecinos(Canvas canvas){
        String str;
        Posicion posicion = new Posicion();
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setTextSize(50);
        for(int fila=0;fila<juego.getFilas();fila++) {
            for (int columna = 0; columna <juego.getColumnas(); columna++) {
                posicion.setFilaColumna(fila,columna);
                int valor = juego.getValorMapaSiVisto(posicion);
                if (valor!=Juego.NULO){
                    str = (valor==Juego.FUEGO)?"X":String.valueOf(valor);
                    if (valor!=Juego.FUEGO){
                        pintarCeldaCamino(canvas, posicion);
                    }
                    escribirTexto(canvas, paint, posicion, str);
                    if (posicion.equals(juego.getPosicionVida())){
                        pintarVida(canvas,posicion);
                    }
                    if (posicion.equals(juego.getPosicionPremio())){
                        pintarPremio(canvas,posicion);
                    }
                }
            }
        }
    }

    /**
     * Escribe cuantas celdas vecinas no son fuego o si la propia celda es fuego
     * @param canvas lienzo sobre el que dibujar
     */
    private void pintarVisitables(Canvas canvas){
        for(Posicion posicion : juego.getVisitables()) {
            pintarCeldaVisitable(canvas,posicion);
        }
    }

    /**
     * Dibuja las celda visitada que pertenece al camino (no es fuego)
     * @param canvas lienzo sobre el que dibujar
     * @param posicion de la celda
     */
    private void pintarCeldaCamino(Canvas canvas, Posicion posicion){
        int canvasWidth = canvas.getWidth();
        int canvasHeight = canvas.getHeight();
        Paint paint = new Paint();
        paint.setARGB(75, 0, 0, 175);
        paint.setStrokeWidth(4);
        paint.setStyle(Paint.Style.FILL);
        int cellWidth = (canvas.getWidth()-LEFT_OFFSET) / juego.getColumnas();
        int cellHeight = canvas.getHeight() / juego.getFilas();
        int x = LEFT_OFFSET + posicion.getColumna()*cellWidth;
        int y = posicion.getFila()*cellHeight;
        canvas.drawRect(x, y, x + cellWidth, y + cellHeight, paint);
    }

    /**
     * Dibuja las celda visitable
     * @param canvas lienzo sobre el que dibujar
     * @param posicion de la celda
     */
    private void pintarCeldaVisitable(Canvas canvas, Posicion posicion){
        Paint paint = new Paint();
        paint.setARGB(75, 0, 0, 175);
        paint.setStrokeWidth(4);
        paint.setStyle(Paint.Style.STROKE);
        int cellWidth = (canvas.getWidth()-LEFT_OFFSET) / juego.getColumnas();
        int cellHeight = canvas.getHeight() / juego.getFilas();
        int x = LEFT_OFFSET + posicion.getColumna()*cellWidth;
        int y = posicion.getFila()*cellHeight;
        RectF rect = new RectF(x,y,x+cellWidth,y+cellHeight);
        canvas.drawOval(rect, paint);
    }

    /**
     * Dibuja posición de inicio como rombo azul
     * @param canvas lienzo sobre el que dibujar
     */
    private void pintarInicio(Canvas canvas){
        pintarRombo(canvas, juego.getPosicionInicial(), Color.BLUE);
    }

    /**
     * Dibuja posición final como rombo azul
     * @param canvas lienzo sobre el que dibujar
     */
    private void pintarFin(Canvas canvas){
        pintarRombo(canvas, juego.getPosicionFinal(), Color.BLUE);
    }

    /**
     * Dibuja un rombo de un color en una celda
     * @param canvas Lienzo sobre el que dibujar
     * @param posicion de la celda
     * @param color del borde
     */
    private void pintarRombo(Canvas canvas, Posicion posicion, int color){
        int fila = posicion.getFila();
        int columna = posicion.getColumna();
        int cellWidth = (canvas.getWidth()-LEFT_OFFSET) / juego.getColumnas();
        int cellHeight = canvas.getHeight() / juego.getFilas();
        int x1,y1,x2,y2;
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setStrokeWidth(4);
        paint.setStyle(Paint.Style.STROKE);
        x1 = LEFT_OFFSET + columna*cellWidth + cellWidth/2;
        y1 = fila*cellHeight;
        x2 = LEFT_OFFSET + (columna+1)*cellWidth;
        y2 = fila*cellHeight + cellHeight/2;
        canvas.drawLine(x1, y1, x2, y2, paint);
        x1 = x2;
        y1 = y2;
        x2 = LEFT_OFFSET + columna*cellWidth + cellWidth/2;
        y2 = (fila+1)*cellHeight;
        canvas.drawLine(x1, y1, x2, y2, paint);
        x1 = x2;
        y1 = y2;
        x2 = LEFT_OFFSET + columna*cellWidth;
        y2 = fila*cellHeight + cellHeight/2;
        canvas.drawLine(x1, y1, x2, y2, paint);
        x1 = x2;
        y1 = y2;
        x2 = LEFT_OFFSET + columna*cellWidth + cellWidth/2;
        y2 = fila*cellHeight;
        canvas.drawLine(x1, y1, x2, y2, paint);
    }

    /**
     * Dibuja un rombo de un color en una celda
     * @param canvas Lienzo sobre el que dibujar
     * @param paint elemento con el que dibujar
     * @param posicion de la celda
     * @param str Texto
     */
    private void escribirTexto(Canvas canvas,  Paint paint, Posicion posicion, String str){
        int cellWidth = (canvas.getWidth()-LEFT_OFFSET) / juego.getColumnas();
        int cellHeight = canvas.getHeight() / juego.getFilas();

        int textWidth = getTextWidth(paint, str);
        int textHeight = getTextHeight(paint, str);

        canvas.drawText(str, LEFT_OFFSET + posicion.getColumna()*cellWidth + (cellWidth-textWidth)/2,
                             (posicion.getFila()+1)*cellHeight - (cellHeight-textHeight)/2, paint);
    }

    /**
     * Obtiene el ancho del texto según el elemento con el que se escribe
     * @param paint elemento con el que dibujar
     * @param text Cadena de caracteres
     * @return anchura
     */
    public int getTextWidth(Paint paint, String text) {
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        return bounds.left + bounds.width();
    }

    /**
     * Obtiene el alto del texto según el elemento con el que se escribe
     * @param paint elemento con el que dibujar
     * @param text Cadena de caracteres
     * @return altura
     */
    public int getTextHeight(Paint paint, String text) {
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        return bounds.bottom + bounds.height();
    }


    /**
     * Dibuja la información relativa a la fase, las vidas, la puntuación y las minas que hay
     * @param canvas Lienzo sobre el que dibujar
     */
    private void pintarHUD(Canvas canvas){
        String str, s;
        int i, textWidth, textHeight;
        Paint paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setTextSize(40);

        str = String.valueOf(juego.getNivel());
        textWidth = getTextWidth(paint, str);
        textHeight = getTextHeight(paint, str);
        canvas.drawText(str, (LEFT_OFFSET - textWidth) / 2, textHeight * 2, paint);

        str = String.valueOf(juego.getVidas());
        textWidth = getTextWidth(paint, str);
        textHeight = getTextHeight(paint, str);
        canvas.drawText(str, (LEFT_OFFSET - textWidth)/2, canvas.getHeight()/2 - textHeight*3, paint);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle((LEFT_OFFSET)/2, canvas.getHeight()/2 - textHeight*3 - textHeight/2,(LEFT_OFFSET - 20)/2, paint);

        str = String.valueOf(juego.getPuntos());
        textWidth = getTextWidth(paint, str);
        textHeight = getTextHeight(paint, str);
        i = 0;
        while(str.length()>0){
            i++;
            s = str.substring(0,1);
            str = str.substring(1,str.length());
            textWidth = getTextWidth(paint, s);
            canvas.drawText(s, (LEFT_OFFSET - textWidth)/2, canvas.getHeight()/2 + (textHeight+5)*(i-1), paint);
        }

        str = String.valueOf(juego.getFuegos());
        textWidth = getTextWidth(paint, str);
        textHeight = getTextHeight(paint, str);
        canvas.drawText(str, (LEFT_OFFSET - textWidth)/2, canvas.getHeight() - textHeight, paint);
    }

    private void pintarFinFase(Canvas canvas){
        Paint paint = new Paint();
        Typeface tf = Typeface.createFromAsset(context.getAssets(),
                "fonts/AllCrackedOut.ttf");
        paint.setTypeface(tf);
        paint.setColor(Color.BLUE);
        paint.setTextSize(80);
        String str = getResources().getString(R.string.level_title);
        int textWidth = getTextWidth(paint, str);
        canvas.drawText(str, LEFT_OFFSET + (canvas.getWidth() - LEFT_OFFSET - textWidth) / 2, canvas.getHeight() / 3, paint);
        paint.setTextSize(40);
        str = getResources().getString(R.string.press_to_continue);
        textWidth = getTextWidth(paint, str);
        canvas.drawText(str, LEFT_OFFSET + (canvas.getWidth() - LEFT_OFFSET - textWidth) / 2, canvas.getHeight() * 2 / 3, paint);
    }

    private void pintarFinPartida(Canvas canvas){
        Paint paint = new Paint();
        Typeface tf = Typeface.createFromAsset(context.getAssets(),
                "fonts/AllCrackedOut.ttf");
        paint.setTypeface(tf);
        paint.setColor(Color.BLUE);
        paint.setTextSize(80);
        String str = getResources().getString(R.string.game_over_title);
        int textWidth = getTextWidth(paint, str);
        canvas.drawText(str, LEFT_OFFSET + (canvas.getWidth() - LEFT_OFFSET - textWidth) / 2, canvas.getHeight() / 3, paint);
        paint.setTextSize(40);
        str = getResources().getString(R.string.press_to_return);
        textWidth = getTextWidth(paint, str);
        canvas.drawText(str, LEFT_OFFSET + (canvas.getWidth() - LEFT_OFFSET - textWidth) / 2, canvas.getHeight() * 2 / 3, paint);
    }


    /**
     * Dibuja un símbolo que representa una vida extra
     * @param canvas Lienzo sobre el que dibujar
     * @param posicion de la celda
     */
    private void pintarVida(Canvas canvas, Posicion posicion){
        int fila = posicion.getFila();
        int columna = posicion.getColumna();
        int cellWidth = (canvas.getWidth()-LEFT_OFFSET) / juego.getColumnas();
        int cellHeight = canvas.getHeight() / juego.getFilas();
        Paint paint = new Paint();
        paint.setTextSize(20);
        paint.setColor(Color.RED);
        paint.setStrokeWidth(4);
        paint.setStyle(Paint.Style.STROKE);
        int textWidth = getTextWidth(paint, "1");
        int textHeight = getTextHeight(paint, "1");
        int a = (textWidth>textHeight)?textWidth:textHeight;
        canvas.drawText("1", LEFT_OFFSET + cellWidth * columna + cellWidth / 4, cellHeight * fila + cellHeight / 4 + textHeight, paint);
        canvas.drawCircle(LEFT_OFFSET + cellWidth * columna + cellWidth / 4 + textWidth / 2, cellHeight * fila + cellHeight / 4 + textHeight / 2, a / 2 + 10, paint);
    }

    /**
     * Dibuja un símbolo que representa puntos extra
     * @param canvas Lienzo sobre el que dibujar
     * @param posicion de la celda
     */
    private void pintarPremio(Canvas canvas, Posicion posicion){
        int fila = posicion.getFila();
        int columna = posicion.getColumna();
        int cellWidth = (canvas.getWidth()-LEFT_OFFSET) / juego.getColumnas();
        int cellHeight = canvas.getHeight() / juego.getFilas();
        Paint paint = new Paint();
        paint.setTextSize(20);
        paint.setColor(Color.RED);
        paint.setStrokeWidth(4);
        paint.setStyle(Paint.Style.STROKE);
        int x = LEFT_OFFSET + cellWidth*columna + cellWidth*3/4;
        int y = cellHeight * fila + cellHeight*3/4;
        Rect rect = new Rect(x-10, y-10, x+20, y+20);
        canvas.drawRect(rect,paint);
        canvas.drawLine(x - 10, y + 5, x + 20, y + 5, paint);
        canvas.drawLine(x+5,y-10,x+5,y+20,paint);
    }

}
