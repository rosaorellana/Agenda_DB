package com.example.agenda_db;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;

public class agregar extends AppCompatActivity {

    String resp, accion, id, rev;
    TextView Codigo, Nombre, Direccion, Telefono, Dui;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar);

        Codigo = (TextView) findViewById(R.id.txtCodigo);
        Nombre = (TextView) findViewById(R.id.txtNombre);
        Direccion = (TextView) findViewById(R.id.txtDireccion);
        Telefono = (TextView) findViewById(R.id.txtTelefono);
        Dui = (TextView) findViewById(R.id.txtDui);

        Button btnMostrarProductos = (Button) findViewById(R.id.btnMostrar);
        btnMostrarProductos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mostrarProductos = new Intent(agregar.this, MainActivity.class);
                startActivity(mostrarProductos);
            }
        });

        Button btnGuardarProductos = (Button) findViewById(R.id.btnGuardar);
        btnGuardarProductos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String codigo = Codigo.getText().toString();

                String nombre = Nombre.getText().toString();

                String direccion = Direccion.getText().toString();

                String telefono = Telefono.getText().toString();

                String dui = Dui.getText().toString();

                if (!nombre.isEmpty() && !codigo.isEmpty() && !direccion.isEmpty() && !telefono.isEmpty() && !dui.isEmpty()) {

                    // Clase a llamar
                    Guardar();

                } else {
                    Toast.makeText(agregar.this, "POR FAVOR!! Llene todos los campos", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mostrarDatosAmigo();
    }

    void mostrarDatosAmigo(){
        try {
            Bundle recibirParametros = getIntent().getExtras();
            accion = recibirParametros.getString("accion");
            if (accion.equals("modificar")){
                JSONObject dataAmigo = new JSONObject(recibirParametros.getString("dataAmigo")).getJSONObject("value");

                TextView tempVal = (TextView)findViewById(R.id.txtCodigo);
                tempVal.setText(dataAmigo.getString("Codigo"));

                tempVal = (TextView)findViewById(R.id.txtNombre);
                tempVal.setText(dataAmigo.getString("Nombre"));

                tempVal = (TextView)findViewById(R.id.txtDireccion);
                tempVal.setText(dataAmigo.getString("Direccion"));

                tempVal = (TextView)findViewById(R.id.txtTelefono);
                tempVal.setText(dataAmigo.getString("Telefono"));

                tempVal = (TextView)findViewById(R.id.txtDui);
                tempVal.setText(dataAmigo.getString("Dui"));

                id = dataAmigo.getString("_id");
                rev = dataAmigo.getString("_rev");
            }
        }catch (Exception ex){
            ///
        }
    }

    private void Guardar(){
        TextView tempVal = findViewById(R.id.txtCodigo);
        String codigo = tempVal.getText().toString();

        tempVal = findViewById(R.id.txtNombre);
        String nombre = tempVal.getText().toString();

        tempVal = findViewById(R.id.txtDireccion);
        String direccion = tempVal.getText().toString();

        tempVal = findViewById(R.id.txtTelefono);
        String telefono = tempVal.getText().toString();

        tempVal = findViewById(R.id.txtDui);
        String dui = tempVal.getText().toString();

        try {
            JSONObject datosAmigo = new JSONObject();
            if (accion.equals("modificar")){
                datosAmigo.put("_id",id);
                datosAmigo.put("_rev",rev);
            }
            datosAmigo.put("Codigo", codigo);
            datosAmigo.put("Nombre", nombre);
            datosAmigo.put("Direccion", direccion);
            datosAmigo.put("Telefono", telefono);
            datosAmigo.put("Dui", dui);

            enviarDatos objGuardarAmigo = new enviarDatos();
            objGuardarAmigo.execute(datosAmigo.toString());
        }catch (Exception ex){
            Toast.makeText(getApplicationContext(), "Error: "+ ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private class enviarDatos extends AsyncTask<String,String, String> {
        HttpURLConnection urlConnection;
        @Override
        protected String doInBackground(String... parametros) {
            StringBuilder stringBuilder = new StringBuilder();
            String jsonResponse = null;
            String jsonDatos = parametros[0];
            BufferedReader reader;
            try {
                URL url = new URL("http://192.168.1.8:5984/db_agenda/");
                urlConnection = (HttpURLConnection)url.openConnection();
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);

                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type","application/json");
                urlConnection.setRequestProperty("Accept","application/json");

                Writer writer = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8"));
                writer.write(jsonDatos);
                writer.close();

                InputStream inputStream = urlConnection.getInputStream();
                if(inputStream==null){
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                resp = reader.toString();

                String inputLine;
                StringBuffer stringBuffer = new StringBuffer();
                while ((inputLine=reader.readLine())!= null){
                    stringBuffer.append(inputLine+"\n");
                }
                if(stringBuffer.length()==0){
                    return null;
                }
                jsonResponse = stringBuffer.toString();
                return jsonResponse;
            }catch (Exception ex){
                //
            }
            return null;
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try{
                JSONObject jsonObject = new JSONObject(s);
                if(jsonObject.getBoolean("ok")){
                    Toast.makeText(getApplicationContext(), "GUARDADO", Toast.LENGTH_SHORT).show();
                    Intent mostrarProductos = new Intent(agregar.this, MainActivity.class);
                    startActivity(mostrarProductos);
                } else {
                    Toast.makeText(getApplicationContext(), "Ha ocurrido un error", Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){
                Toast.makeText(getApplicationContext(), "Ha ocurrido un error: "+e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }
}