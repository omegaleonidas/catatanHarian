package vsga.sidiq.catatanharian;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

public class InsertData extends AppCompatActivity {

    EditText tvTitle, tvContent;
    private static final int REQUEST_PERMISSION = 201;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_data);
        tvTitle = findViewById(R.id.edName);
        tvContent = findViewById(R.id.edNoted);
        int data = getIntent().getIntExtra("data", 0);

        if (data == MainActivity.EDIT_DATA) {
            getSupportActionBar().setTitle("Edit Data");
            String name = getIntent().getStringExtra("title");
            tvTitle.setText(name);
            readFile(name.concat(".txt"));
        } else {
            getSupportActionBar().setTitle("Tambah Data");
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_save, menu);
        return true;
    }

    void showAlertDialogSave() {
        new AlertDialog.Builder(this)
                .setTitle("Simpan Catatan")
                .setMessage("Apakah anda yakin ingin menyimpan catatan ini?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, (dialogInterface, i) -> createFile())
                .setNegativeButton(android.R.string.no, (dialogInterface, i) -> dialogInterface.dismiss()).show();
    }

    void showAlertDialogEdit(String filename) {
        new AlertDialog.Builder(this)
                .setTitle("Edit Catatan")
                .setMessage("Apakah anda yakin ingin edit catatan ini?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, (dialogInterface, i) -> updateFile(filename))
                .setNegativeButton(android.R.string.no, (dialogInterface, i) -> dialogInterface.dismiss()).show();
    }

    void createFile() {
        File file = new File(getFilesDir(), tvTitle.getText().toString().concat(".txt"));
        FileOutputStream outputStream = null;
        try {
            file.createNewFile();
            outputStream = new FileOutputStream(file, true);
            outputStream.write(tvContent.getText().toString().getBytes());
            outputStream.flush();
            outputStream.close();
            Toast.makeText(this, "Data is success save", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void readFile(String filename) {
        File sdcard = getFilesDir();
        File file = new File(sdcard, filename);
        if (file.exists()) {
            StringBuilder text = new StringBuilder();
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line = br.readLine();
                while (line != null) {
                    text.append(line);
                    line = br.readLine();
                }
                br.close();
            } catch (IOException e) {
                System.out.println("Error " + e.getMessage());
            }
            tvContent.setText(text.toString());
        }
    }

    void updateFile(String fileName) {
        File file = new File(getFilesDir(), fileName.concat(".txt"));
        FileOutputStream outputStream = null;

        try {
            file.createNewFile();
            outputStream = new FileOutputStream(file, false);
            outputStream.write(tvContent.getText().toString().getBytes());
            outputStream.flush();
            outputStream.close();
            Toast.makeText(this, "Data is update save", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void checkPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            int data = getIntent().getIntExtra("data", 0);
            if (data == MainActivity.EDIT_DATA) {
                String name = getIntent().getStringExtra("title");
                showAlertDialogEdit(name);
            } else {
                showAlertDialogSave();
            }
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
                    int data = getIntent().getIntExtra("data", 0);
                    if (data == MainActivity.EDIT_DATA) {
                        String name = getIntent().getStringExtra("title");
                        showAlertDialogEdit(name);
                    } else {
                        showAlertDialogSave();
                    }
                } else {
                    Toast.makeText(this, "Permission is required", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Permission is required", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if (item.getItemId() == R.id.save) {
            checkPermission();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}