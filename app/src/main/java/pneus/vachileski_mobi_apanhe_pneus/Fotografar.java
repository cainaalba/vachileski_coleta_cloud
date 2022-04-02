package pneus.vachileski_mobi_apanhe_pneus;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import pneus.vachileski_mobi_funcoes_genericas.ComprimirFoto;
import pneus.vachileski_mobi_funcoes_genericas.GettersSetters;

@SuppressWarnings({"SpellCheckingInspection", "Convert2Lambda"})
public class Fotografar extends AppCompatActivity {
    public LinearLayout ll;
    public Button btnTirarFoto, btnExcFoto, btnSalvarFoto, btnUploadImg;
    public ImageView imgFoto;
    public String nomeFoto = "";
    public File caminhoFoto = null;
    public File gravaFotoCapturada = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_fotografar);

        btnTirarFoto = findViewById(R.id.btnTirarFoto);
        imgFoto = findViewById(R.id.imgFoto);
        btnExcFoto = findViewById(R.id.btnExcFoto);
        btnSalvarFoto = findViewById(R.id.btnSalvarFoto);
        btnUploadImg = findViewById(R.id.btnUploadImg);
        ll = findViewById(R.id.ll);

        btnExcFoto.setVisibility(View.GONE);
        btnSalvarFoto.setVisibility(View.GONE);

        if (ContextCompat.checkSelfPermission(Fotografar.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(Fotografar.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Fotografar.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 0);
        }

        if (GettersSetters.getTipoFoto().equals("DOCUMENTOS")) {
            caminhoFoto = Fotografar.this.getExternalFilesDir("DocsNovosClientes");
            if (GettersSetters.getTipoDoc().equals("RG")) {
                nomeFoto = "RG-" + new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date()) + "-" + GettersSetters.getDocCli().trim() + ".png";
            } else if (GettersSetters.getTipoDoc().equals("CPF/CNPJ")) {
                nomeFoto = "Doc-" + new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date()) + "-" + GettersSetters.getDocCli().trim() + ".png";
            } else {
                nomeFoto = "CompResid-" + new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date()) + "-" + GettersSetters.getDocCli().trim() + ".png";
            }

            if (!GettersSetters.isIsSessionFoto()) {
                //DELETA OS ARQUIVOS DA PASTA AUTORIZAÇÕES CLIENTES
                String[] files = caminhoFoto.list();
                File file;
                if (files != null && files.length > 0) {
                    for (String nome : files) {
                        file = new File(caminhoFoto + "/" + nome);
                        if (file.exists()) {
                            file.delete();
                        }
                    }
                }
                GettersSetters.setIsSessionFoto(true);
            }

        } else {
            nomeFoto = "Cliente-Aut-" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date()) + ".png";
            caminhoFoto = Fotografar.this.getExternalFilesDir("AutorizacoesClientes"); //Environment.getExternalStorageDirectory().getAbsolutePath()+"/VachileskiMobi/Assinaturas/"; //Environment.getExternalStorageDirectory().getPath() + "/VachileskiMobi/Assinaturas/PermissCli/";

            //DELETA OS ARQUIVOS DA PASTA AUTORIZAÇÕES CLIENTES
            String[] files = caminhoFoto.list();
            File file;
            if (files != null && files.length > 0) {
                for (String nome : files) {
                    file = new File(caminhoFoto + "/" + nome);
                    if (file.exists()) {
                        file.delete();
                    }
                }
            }
        }

        gravaFotoCapturada = new File(caminhoFoto + "/" + nomeFoto);
        if (!caminhoFoto.exists()) {
            //noinspection ResultOfMethodCallIgnored
            caminhoFoto.mkdirs();
        }

        File fileNoMedia = new File(caminhoFoto + "/" + ".nomedia");
        try {
            FileOutputStream mFileOutStream = new FileOutputStream(caminhoFoto + "/" + ".nomedia");
            mFileOutStream.flush();
            mFileOutStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        btnTirarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(Fotografar.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(Fotografar.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(Fotografar.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 0);
                } else {
                    dispatchTakePictureIntent();
                }
            }
        });

        btnUploadImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(Fotografar.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(Fotografar.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(Fotografar.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 0);
                } else {
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 2);
                }
            }
        });

        btnExcFoto.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View v) {
                imgFoto.setImageDrawable(null);
                File apagaFoto = new File(gravaFotoCapturada.toString());
                boolean apagou = apagaFoto.delete();

                if (apagou) {
                    btnSalvarFoto.setVisibility(View.GONE);
                    btnExcFoto.setVisibility(View.GONE);
                }
            }
        });

        btnSalvarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SalvaFoto salvaFoto = new SalvaFoto();
                salvaFoto.execute();
            }
        });
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(Fotografar.this, BuildConfig.APPLICATION_ID + ".provider", gravaFotoCapturada);
        } else {
            uri = Uri.fromFile(gravaFotoCapturada);
        }

        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, 1);
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bitmap bitmap;
        Bitmap bitCompress;
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            try {
                bitmap = BitmapFactory.decodeFile(gravaFotoCapturada.getPath());
                bitCompress = Bitmap.createScaledBitmap(bitmap, 768, 1024, false);

                imgFoto.setImageBitmap(bitCompress);
            } catch (Exception expBmp) {
                btnExcFoto.setVisibility(View.GONE);
                btnSalvarFoto.setVisibility(View.GONE);

                GettersSetters.setFotoCapturada(null);
                imgFoto.setImageDrawable(null);

                AlertDialog.Builder builderFilial = new AlertDialog.Builder(Fotografar.this);
                builderFilial.setIcon(R.drawable.atencao);
                builderFilial.setCancelable(true);
                builderFilial.setMessage("Erro ao salvar a foto. Reduza a qualidade da câmera e tente novamente!");
                builderFilial.setPositiveButton("Entendi", null);
                AlertDialog dialog = builderFilial.create();
                dialog.show();
            }
        } else if (requestCode == 2 && resultCode == RESULT_OK) {
            Uri selectedImage = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                bitCompress = Bitmap.createScaledBitmap(bitmap, 768, 1024, false);
                imgFoto.setImageBitmap(bitCompress);

            } catch (Exception expBmp) {
                btnExcFoto.setVisibility(View.GONE);
                btnSalvarFoto.setVisibility(View.GONE);

                GettersSetters.setFotoCapturada(null);
                imgFoto.setImageDrawable(null);

                AlertDialog.Builder builderFilial = new AlertDialog.Builder(Fotografar.this);
                builderFilial.setIcon(R.drawable.atencao);
                builderFilial.setCancelable(true);
                builderFilial.setMessage("Erro ao salvar a foto. Tente novamente!");
                builderFilial.setPositiveButton("Entendi", null);
                AlertDialog dialog = builderFilial.create();
                dialog.show();
            }
        }

        if (imgFoto.getDrawable() != null) {
            btnExcFoto.setVisibility(View.VISIBLE);
            btnSalvarFoto.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                btnTirarFoto.setEnabled(true);
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class SalvaFoto extends AsyncTask<String, String, String> {
        AlertDialog dialogFoto = null;
        Bitmap bitmap = null;

        ComprimirFoto comprimirFoto = new ComprimirFoto();

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPreExecute() {
            AlertDialog.Builder builder = new AlertDialog.Builder(Fotografar.this);
            LayoutInflater layoutInflater = getLayoutInflater();
            final View dialogView = layoutInflater.inflate(R.layout.dialog_carregamento, null);
            final TextView textView = dialogView.findViewById(R.id.txtInfoCarreg);
            textView.setText("Salvando Foto...\nAguarde...");
            builder.setCancelable(false);
            builder.setView(dialogView);
            dialogFoto = builder.create();
            dialogFoto.show();
        }

        @SuppressLint("WrongThread")
        @Override
        protected String doInBackground(String... params) {
            String tipoDoc;

            imgFoto.buildDrawingCache();
            bitmap = imgFoto.getDrawingCache();

            try {
                gravaFotoCapturada = new File(caminhoFoto + "/" + nomeFoto);
                OutputStream fOut = new FileOutputStream(gravaFotoCapturada);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);

                fOut.flush();
                fOut.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                GettersSetters.setFotoCapturada(gravaFotoCapturada);
                if (GettersSetters.getTipoFoto().equals("DOCUMENTOS")) {
                    comprimirFoto.foto(nomeFoto, caminhoFoto + "/", "DocNovoCli");
                    if (GettersSetters.getTipoDoc().equals("RG")) {
                        ColetaCliente.rgOK = true;
                        tipoDoc = "RG";
                    } else if (GettersSetters.getTipoDoc().equals("CPF/CNPJ")) {
                        ColetaCliente.cpfCnpjOk = true;
                        tipoDoc = "CPF";
                    } else {
                        ColetaCliente.comprResidOK = true;
                        tipoDoc = "COMPROV-RESID";
                    }
                } else {
                    comprimirFoto.foto(nomeFoto, caminhoFoto + "/", "PermCli");
                    tipoDoc = "PERM-CLI";
                }
            }
            return tipoDoc;
        }

        @Override
        protected void onPostExecute(String tipoDoc) {
            if (!tipoDoc.equals("")) {
                switch (tipoDoc) {
                    case "RG":
                        Toast.makeText(Fotografar.this, "RG capturada com sucesso!", Toast.LENGTH_SHORT).show();
                        ColetaCliente.btnRGNovoCli.setBackgroundColor(getResources().getColor(R.color.green));
                        break;
                    case "CPF":
                        Toast.makeText(Fotografar.this, "CPF/CNPJ capturado com sucesso!", Toast.LENGTH_SHORT).show();
                        ColetaCliente.btnDocNovoCli.setBackgroundColor(getResources().getColor(R.color.green));
                        break;
                    case "COMPROV-RESID":
                        Toast.makeText(Fotografar.this, "Comprov. de Endereço capturado com sucesso!", Toast.LENGTH_SHORT).show();
                        ColetaCliente.btnCompResidNovoCli.setBackgroundColor(getResources().getColor(R.color.green));
                        break;
                    case "PERM-CLI":
                        ColetaConclusao.setAssinaturaCli();
                        Toast.makeText(Fotografar.this, "Autorização capturada com sucesso!", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        ColetaConclusao.setAssinaturaCli();
                        break;
                }
            } else {
                Toast.makeText(Fotografar.this, "Erro ao salvar a foto. Tente novamente!", Toast.LENGTH_SHORT).show();
            }

            Fotografar.this.finish();

            if (dialogFoto != null) {
                dialogFoto.dismiss();
            }
        }
    }
}