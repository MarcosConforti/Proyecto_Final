package com.example.proyectofinal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Nivel_Seis extends AppCompatActivity {

    private EditText et_respuesta;
    private TextView tv_nombre, tv_score;
    private ImageView iv_Uno, iv_Dos,iv_signo, iv_vidas;
    private MediaPlayer mp, mp_great, mp_bad;

    int score, numAleatorio_uno, numAleatorio_dos,signoAleatorio, resultado, vidas = 3;
    String nombre_jugador, string_score, string_vidas;
    //creamos un vector de tipo String
    //otra forma de crear vectores es la siguiente
    String numero[] = {"cero","uno","dos","tres","cuatro","cinco","seis","siete","ocho","nueve"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nivel__seis);

        Toast.makeText(this, "Nivel 6 - Sumas, Restas y Multiplicaciones", Toast.LENGTH_SHORT).show();

        tv_nombre = (TextView)findViewById(R.id.textView_nombre);
        tv_score = (TextView)findViewById(R.id.textView2_score);
        iv_Uno = (ImageView)findViewById(R.id.imageView_numUno);
        iv_Dos = (ImageView)findViewById(R.id.imageView_numDos);
        iv_vidas = (ImageView)findViewById(R.id.imageView_vidas);
        iv_signo = (ImageView)findViewById(R.id.imageView_signo);
        et_respuesta = (EditText)findViewById(R.id.editText_resultado);

        //extraemos el nombre de la activity anterior
        nombre_jugador = getIntent().getStringExtra("nombre");
        tv_nombre.setText("Jugador: " + nombre_jugador);

        //extraemos el score
        string_score = getIntent().getStringExtra("score");
        score = Integer.parseInt(string_score);
        tv_score.setText("Score: " + score);

        //extraemos las vidas
        vidas = getIntent().getIntExtra("vidas",vidas);
        if(vidas == 3){
            iv_vidas.setImageResource(R.drawable.tresvidas);
        }
        if(vidas == 2){
            iv_vidas.setImageResource(R.drawable.dosvidas);
        }
        if(vidas == 1){
            iv_vidas.setImageResource(R.drawable.unavida);
        }

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        mp = MediaPlayer.create(this, R.raw.goats);
        mp.start();
        mp.setLooping(true);

        mp_great = MediaPlayer.create(this, R.raw.wonderful);
        mp_bad = MediaPlayer.create(this, R.raw.bad);

        SignoAleatorio();
        NumAleatorio();
    }
    public void Comprobar(View view){
        String respuesta = et_respuesta.getText().toString();
        int respuesta_int = Integer.parseInt(respuesta);
        if(!respuesta.equals(" ")){
            if(respuesta_int == resultado) {
                mp_great.start();
                score++;
                tv_score.setText("Score: " + score);
                et_respuesta.setText("");
                BaseDeDatos();
            }else{
                mp_bad.start();
                vidas --;
                BaseDeDatos();
                switch (vidas){
                    case 3:
                        iv_vidas.setImageResource(R.drawable.tresvidas);
                        break;
                    case 2:
                        Toast.makeText(this, "Te quedan 2 manzanas", Toast.LENGTH_SHORT).show();
                        iv_vidas.setImageResource(R.drawable.dosvidas);
                        break;
                    case 1:
                        Toast.makeText(this, "Te queda 1 manzana", Toast.LENGTH_SHORT).show();
                        iv_vidas.setImageResource(R.drawable.unavida);
                        break;
                    case 0:
                        Toast.makeText(this, "Has perdido todas tus manzanas", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(this, MainActivity.class);
                        startActivity(intent);
                        mp.stop();
                        mp.release();
                        finish();
                        break;
                }
                et_respuesta.setText("");
            }
            SignoAleatorio();
            NumAleatorio();
        }else{
            Toast.makeText(this, "Escribe tu respuesta", Toast.LENGTH_SHORT).show();
        }
    }
    public void NumAleatorio(){
        if(score <= 59){
            numAleatorio_uno = (int) (Math.random() * 10);
            numAleatorio_dos = (int) (Math.random() * 10);

            if(signoAleatorio == 0 || signoAleatorio == 3){
                resultado = numAleatorio_uno + numAleatorio_dos;
            }else if(signoAleatorio == 1 || signoAleatorio == 4) {
                resultado = numAleatorio_uno - numAleatorio_dos;
            }else if(signoAleatorio == 2 || signoAleatorio == 5){
                resultado = numAleatorio_uno * numAleatorio_dos;
            }

            if(resultado >= 0){

                for(int i = 0; i < numero.length; i++) {
                    int id = getResources().getIdentifier(numero[i], "drawable", getPackageName());
                    if (numAleatorio_uno == i) {
                        iv_Uno.setImageResource(id);
                    }
                    if (numAleatorio_dos == i) {
                        iv_Dos.setImageResource(id);
                    }
                }
            }else{
                NumAleatorio();
            }
        }else {
            Intent intent = new Intent(this, MainActivity.class);
            Toast.makeText(this, "Felicidades, eres el ganador!", Toast.LENGTH_LONG).show();
            startActivity(intent);
            finish();
            mp.stop();
            mp.release();
        }
    }
    public void SignoAleatorio(){
        int id;
        signoAleatorio = (int) (Math.random() *5);
        if(signoAleatorio == 0 || signoAleatorio == 3){
            id = getResources().getIdentifier("adicion", "drawable", getPackageName());
            iv_signo.setImageResource(id);
        }
        if(signoAleatorio == 1 || signoAleatorio == 4){
            id = getResources().getIdentifier("resta", "drawable", getPackageName());
            iv_signo.setImageResource(id);
        }
        if(signoAleatorio == 2 || signoAleatorio == 5){
            id = getResources().getIdentifier("multiplicacion", "drawable", getPackageName());
        }
    }
    public void BaseDeDatos() {
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "bd", null, 1);
        SQLiteDatabase bd = admin.getWritableDatabase();

        Cursor consulta = bd.rawQuery("select * from puntaje where score = (select max(score) from puntaje)", null);
        //en caso se que se encuentre un registro:
        if(consulta.moveToFirst()){
            //recuperamos el nombre y el score
            String temp_nombre = consulta.getString(0);
            String temp_score = consulta.getString(1);
            int best_score = Integer.parseInt(temp_score);
            //vereificamos que el score sea mayor al registrado
            if(score > best_score){
                ContentValues modificacion = new ContentValues();
                modificacion.put("nombres", nombre_jugador);
                modificacion.put("score", score);

                bd.update("puntaje", modificacion, "score=" + best_score, null);
            }
            bd.close();
            //caso contrario
        }else{
            ContentValues insertar = new ContentValues();
            insertar.put("nombres", nombre_jugador);
            insertar.put("score", score);

            bd.insert("puntaje", null, insertar);
            bd.close();
        }
    }
    @Override
    public void onBackPressed(){

    }
}