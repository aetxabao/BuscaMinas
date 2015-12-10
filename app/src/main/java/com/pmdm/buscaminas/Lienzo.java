package com.pmdm.buscaminas;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

public class Lienzo extends View {

    private Juego juego;
    private Context context;

    public Lienzo(Context context, Juego juego) {
        super(context);
        this.context = context;
        this.juego = juego;
    }

    public int getFila(double posY){
        int heightCell = this.getHeight() / juego.getFilas();
        return (int)Math.floor(posY/ heightCell);
    }

    public int getColumna(double posX) {
        int widthCell = this.getWidth() / juego.getColumnas();
        return (int)Math.floor(posX/ widthCell);
    }

    protected void onDraw(Canvas canvas) {
        pintarFondo(canvas);
        pintarLineas(canvas);
        pintarVecinos(canvas);
    }

    private void pintarFondo(Canvas canvas){
        Resources res = getResources();
        Bitmap bitmap = BitmapFactory.decodeResource(res, R.drawable.chateau);
        Rect srcRect = new Rect(0,0,bitmap.getWidth(),bitmap.getHeight());
        canvas.drawBitmap(bitmap,srcRect,canvas.getClipBounds(),null);
        //canvas.drawBitmap(bitmap, 0, 0, null);
    }

    private void pintarLineas(Canvas canvas){
        int canvasWidth = canvas.getWidth();
        int canvasHeight = canvas.getHeight();
        Paint paint = new Paint();
        paint.setARGB(75, 175, 175, 175);
        paint.setStrokeWidth(4);
        paint.setStyle(Paint.Style.STROKE);
        int cellHeight = canvas.getHeight() / juego.getFilas();
        for(int i=1; i<juego.getFilas();i++){
            canvas.drawLine(0, i*cellHeight, canvasWidth, i*cellHeight, paint);
        }
        int cellWidth = canvas.getWidth() / juego.getColumnas();
        for(int j=1; j<juego.getColumnas();j++){
            canvas.drawLine(j*cellWidth, 0, j*cellWidth, canvasHeight, paint);
        }
    }

    private void pintarVecinos(Canvas canvas){
        String str;
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setTextSize(50);
        for(int fila=0;fila<juego.getFilas();fila++) {
            for (int columna = 0; columna <juego.getColumnas(); columna++) {
                int valor = juego.getValor(fila,columna);
                if (valor!=Juego.NULO){
                    str = (valor==Juego.BOMBA)?"X":String.valueOf(valor);
                    escribirTexto(canvas, paint, str, fila, columna);
                }
            }
        }
    }

    private void escribirTexto(Canvas canvas, Paint paint, String str, int fila, int columna){
        int cellWidth = canvas.getWidth() / juego.getColumnas();
        int cellHeight = canvas.getHeight() / juego.getFilas();

        int textWidth = getTextWidth(str, paint);
        int textHeight = getTextHeight(str, paint);

        canvas.drawText(str, columna*cellWidth + (cellWidth-textWidth)/2,
                             (fila+1)*cellHeight - (cellHeight-textHeight)/2, paint);
    }

    public int getTextWidth(String text, Paint paint) {
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        int textWidth = bounds.left + bounds.width();
        return textWidth;
    }

    public int getTextHeight(String text, Paint paint) {
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        int textHeight = bounds.bottom + bounds.height();
        return textHeight;
    }

}
