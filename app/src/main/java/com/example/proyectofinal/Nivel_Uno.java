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

public class Nivel_Uno extends AppCompatActivity {

    private EditText et_respuesta;
    private TextView tv_nombre, tv_score;
    private ImageView  iv_Uno, iv_Dos, iv_vidas;
    private MediaPlayer mp, mp_great, mp_bad;

    int score, numAleatorio_uno, numAleatorio_dos, resultado, vidas = 3;
    String nombre_jugador, string_score, string_vidas;
    //creamos un vector de tipo String
    //otra forma de crear vectores es la siguiente
    String numero[] = {"cero","uno","dos","tres","cuatro","cinco","seis","siete","ocho","nueve"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nivel__uno);

        Toast.makeText(this, "Nivel 1 - Sumas Basicas", Toast.LENGTH_SHORT).show();

        tv_nombre = (TextView)findViewById(R.id.textView_nombre);
        tv_score = (TextView)findViewById(R.id.textView2_score);
        iv_Uno = (ImageView)findViewById(R.id.imageView_numUno);
        iv_Dos = (ImageView)findViewById(R.id.imageView_numDos);
        iv_vidas = (ImageView)findViewById(R.id.imageView_vidas);
        et_respuesta = (EditText)findViewById(R.id.editText_resultado);
        //extraemos el nombre de la activity anterior
        nombre_jugador = getIntent().getStringExtra("nombre");
        tv_nombre.setText("Jugador: " + nombre_jugador);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);
        //creamos los 3 media player, pero solo iniciamos el de pantalla y lo ciclamos
        //los otros dos sonaran cuando se apriete un boton
        mp = MediaPlayer.create(this, R.raw.goats);
        mp.start();
        mp.setLooping(true);

        mp_great = MediaPlayer.create(this, R.raw.wonderful);
        mp_bad = MediaPlayer.create(this, R.raw.bad);

        NumAleatorio();
    }
    public void Comprobar(View view){
        String respuesta = et_respuesta.getText().toString();
        int respuesta_int = Integer.parseInt(respuesta);
        if(!respuesta.equals("")){
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
            NumAleatorio();
        }else{
            Toast.makeText(this, "Escribe tu respuesta", Toast.LENGTH_SHORT).show();
        }
    }
    public void NumAleatorio(){
        if(score <= 9){
            numAleatorio_uno = (int) (Math.random() * 10);
            numAleatorio_dos = (int) (Math.random() * 10);

            resultado = numAleatorio_uno + numAleatorio_dos;
            if(resultado <= 10){
                //colocamos las imagenes en sus numeros correspondientes
                for(int i = 0; i <numero.length; i ++){
                    int id = getResources().getIdentifier(numero[i], "drawable", getPackageName());
                    if(numAleatorio_uno == i){
                        iv_Uno.setImageResource(id);
                    }
                    if(numAleatorio_dos == i){
                        iv_Dos.setImageResource(id);
                    }
                }
            }else{
                //aplicamos recursividad, volviendo a ejecutar el metodo
                NumAleatorio();
            }
        }else{
            Intent intent = new Intent(this,Nivel_Dos.class);
            string_score = String.valueOf(score);
            string_vidas = String.valueOf(vidas);
            intent.putExtra("nombre", nombre_jugador);
            intent.putExtra("score", string_score);
            intent.putExtra("vidas", string_vidas);
            startActivity(intent);
            finish();
            mp.stop();
            mp.release();
        }
    }
    public void BaseDeDatos(){
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