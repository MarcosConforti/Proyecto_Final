package com.example.proyectofinal;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private EditText et_nombre;
    private TextView tv_bestScore;
    private ImageView iv_personaje;
    private MediaPlayer mp;
    //la clase math devuelve double, asi que hacemos un casting para pasarlo a Int
    int num_aleatorio = (int) (Math.random() * 4);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et_nombre = (EditText)findViewById(R.id.txt_nombre);
        tv_bestScore = (TextView)findViewById(R.id.Best_Score);
        iv_personaje = (ImageView)findViewById(R.id.imageView_Personaje);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        int id;
        if(num_aleatorio == 0){
            //el metodo identifier nos recupera la ruta de la imagen a utilizar. Colocamos nombre de la imagen, carpeta y getPackageName
            id = getResources().getIdentifier("mango", "drawable", getPackageName());
            //colocamos la imagen en el iv
            iv_personaje.setImageResource(id);
        }else if(num_aleatorio == 1){
            id = getResources().getIdentifier("sandia", "drawable", getPackageName());
            iv_personaje.setImageResource(id);
        }else if(num_aleatorio == 2){
            id = getResources().getIdentifier("manzana","drawable", getPackageName());
            iv_personaje.setImageResource(id);
        }else if(num_aleatorio == 3){
            id = getResources().getIdentifier("fresa","drawable",getPackageName());
            iv_personaje.setImageResource(id);
        }else if(num_aleatorio == 4){
            id = getResources().getIdentifier("uva", "drawable", getPackageName());
            iv_personaje.setImageResource(id);
        }
        mp = MediaPlayer.create(this,R.raw.alphabet_song);
        mp.start();
        //con este metodo loopeamos la pista
        mp.setLooping(true);

        //conectamos a base de datos, creamos un objeto adsqliteopenhelper
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "bd",null,1);
        SQLiteDatabase bd = admin.getWritableDatabase();
        //consulta a base de datos
        Cursor consulta = bd.rawQuery(
                "select * from puntaje where score = (select max(score) from puntaje)", null);
        //creamos un condicional en caso de que no haya resgistrado ningun puntaje
        if(consulta.moveToFirst()){
            //recuperamos el nombre
            String temp_nombre = consulta.getString(0);
            //recuperamos el score
            String temp_score = consulta.getString(1);
            //lo mostramos en el textview
            tv_bestScore.setText("Record: " + temp_score + " de " + temp_nombre);
            bd.close();
        }else{
            bd.close();
        }
    }
    public void Jugar(View view){
        //creamos un string donde guardemos el txt
        String nombre = et_nombre.getText().toString();
        if(!nombre.equals("")) {
            //detenemos el audio
            mp.stop();
            mp.release();
            Intent intent = new Intent(this,Nivel_Uno.class);
            intent.putExtra("nombre",nombre);
            startActivity(intent);
        }else{
            Toast.makeText(this, "Debes escrbir tu nombre", Toast.LENGTH_SHORT).show();
            //con este algoritmo, hacemos que se abra el teclado para escribir en el et_nombre
            et_nombre.requestFocus();
            InputMethodManager imm = (InputMethodManager)getSystemService(this.INPUT_METHOD_SERVICE);
            imm.showSoftInput(et_nombre, InputMethodManager.SHOW_IMPLICIT);
        }
    }
    //sobreescribimos el motodo del boton "atras" del celular
    @Override
    public void onBackPressed(){

    }
}