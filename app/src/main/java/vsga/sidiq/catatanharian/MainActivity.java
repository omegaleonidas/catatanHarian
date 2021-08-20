package vsga.sidiq.catatanharian;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import vsga.sidiq.catatanharian.adapter.DataAdapter;
import vsga.sidiq.catatanharian.model.Data;

public class MainActivity  extends AppCompatActivity implements DataAdapter.DataListener {

    private static final int REQUEST_PERMISSION = 201;
    public static final int ADD_DATA = 401;
    public static final int EDIT_DATA = 402;

    private ArrayList<Data> listData = new ArrayList<>();
    private Data data;
    private DataAdapter dataAdapter;

    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dataAdapter = new DataAdapter(this);
        recyclerView = findViewById(R.id.rcvItem);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        checkPermission();
    }

    void checkPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            readFile();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (grantResults.length > 0 && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    readFile();
//                    showAlertDialog();
                } else {
                    Toast.makeText(this, "Permission is required", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Permission is required", Toast.LENGTH_SHORT).show();
            }
        }
    }

    void deleteFiles(String filename) {
        File file = new File(getFilesDir(), filename.concat(".txt"));
        if (file.exists()) {
            file.delete();
        }
    }

    void readFile() {
        File sdcard = getFilesDir();
        Log.e("TAG", "length: " + sdcard.listFiles().length);
        listData.clear();
        for (int i = 0; i < sdcard.list().length; i++) {
            Date lastModDate = new Date(sdcard.listFiles()[i].lastModified());
            data = new Data(sdcard.list()[i].replace(".txt", ""), lastModDate.toString());
            listData.add(data);
        }
        dataAdapter.getData(listData);
        recyclerView.setAdapter(dataAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.add:
                Intent intent = new Intent(MainActivity.this, InsertData.class);
                intent.putExtra("data", ADD_DATA);
                startActivityForResult(intent, ADD_DATA);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_DATA) {
            if (resultCode == RESULT_OK) {
                readFile();
            }
        }
    }

    @Override
    public void onclickListener(Data data) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Catatan")
                .setMessage("Apakah anda yakin ingin delete catatan ini?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Update", (dialogInterface, i) -> {
                    Intent intent = new Intent(MainActivity.this, InsertData.class);
                    intent.putExtra("data", EDIT_DATA);
                    intent.putExtra("title",data.getName());
                    startActivityForResult(intent, EDIT_DATA);
                })
                .setNeutralButton("Delete", (dialogInterface, i) -> {
                    deleteFiles(data.getName());
                    readFile();
                })
                .setNegativeButton(android.R.string.no, (dialogInterface, i) -> dialogInterface.dismiss()).show();
    }
}