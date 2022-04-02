package pneus.vachileski_mobi_apanhe_pneus;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.sql.ResultSet;
import java.util.Collections;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import pneus.vachileski_mobi_funcoes_genericas.CheckConnection;
import pneus.vachileski_mobi_funcoes_genericas.ConexaoBDExt;
import pneus.vachileski_mobi_funcoes_genericas.ConexaoBDInt;
import pneus.vachileski_mobi_funcoes_genericas.CriptografaString;
import pneus.vachileski_mobi_funcoes_genericas.FuncoesGenericas;
import pneus.vachileski_mobi_funcoes_genericas.GettersSetters;

@SuppressWarnings({"DanglingJavadoc", "Convert2Lambda"})
public class Login extends AppCompatActivity {
    EditText cmpUsuario, cmpSenha;
    Button btnAcessar;
    TextView txtPolitica;
    CheckBox chkLembrarMe;
    ImageView btnAtualizarApp, imgDownAtualizacao;

    String usuarioEdit = "";
    String senhaEdit = "";
    String usuarioLocal = "";
    String statusLocal = "";
    String senhaLocal = "";
    String idUsuarioBdLocal = "";
    String senhaUsuarioBdLocal = "";

    String idUsuarioBdExt = "";
    String nomeUsuarioBdExt = "";
    String loginUsuarioBdExt = "";
    String senhaUsuarioBdExt = "";
    String statusUsuarioBdExt = "";
    String emailUsuarioBdExt = "";
    String cpfUsuarioBdExt = "";
    String tipoUsuarioBdExt = "";
    String codigoTotvsUsuarioBdExt = "";
    String nomeTotvsUsuarioBdExt = "";
    String loginDescriptografado = "";
    String senhaDescriptografada = "";

    static boolean isConnected; //CONECTADO NA INTERNET

    ConexaoBDExt conecta = new ConexaoBDExt();
    ConexaoBDInt db;
    FuncoesGenericas funcoesGenericas = new FuncoesGenericas();

    View viewSnackBar;

    VerificaConexaoInternet asyncVerificaConexaoInternet = null;
    InicializacaoApp asyncInicializacaoApp = null;
    AtualizaFiliais asyncAtualizaFiliais = null;
    AtualizacaoApp asyncAtualizaApp = null;
    ProcessoLogin asyncProcessoLogin = null;

    String urlColeta = "";

    public static final String INFORMACOES_LOGIN_AUTOMATICO = "INFORMACOES_LOGIN_AUTOMATICO";
    public static final String VERSAO_ATUAL_SQLITE = "VERSAO_ATUAL_SQLITE";

    AlertDialog dialogLogin = null;
    File apkAtualizacao = null;

    @SuppressLint({"ApplySharedPref", "SourceLockedOrientationActivity"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_login);

        cmpUsuario = findViewById(R.id.cmpUsuario);
        cmpSenha = findViewById(R.id.cmpSenha);
        btnAcessar = findViewById(R.id.btnAcessar);
        chkLembrarMe = findViewById(R.id.chkLembrarMe);
        viewSnackBar = findViewById(R.id.viewSnackBar);
        txtPolitica = findViewById(R.id.txtPolitica);
        btnAtualizarApp = findViewById(R.id.btnAtualizarApp);
        imgDownAtualizacao = findViewById(R.id.imgDownAtualizacao);

        db = new ConexaoBDInt(this);

        /** INICIALIZAÇÃO **/
        asyncVerificaConexaoInternet = new VerificaConexaoInternet();
        asyncVerificaConexaoInternet.execute();

        btnAcessar.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ApplySharedPref")
            @Override
            public void onClick(View v) {
                if (Login.this.checkCallingOrSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(Login.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
                }

                btnAcessar.setEnabled(false);

                usuarioEdit = cmpUsuario.getText().toString().toLowerCase().trim(); //usuário digitado
                senhaEdit = cmpSenha.getText().toString().toLowerCase().trim(); //senha digitada

                if (usuarioEdit.isEmpty()) {
                    cmpUsuario.setError(getString(R.string.erro_campo_obrigatorio));
                    cmpUsuario.requestFocus();
                    btnAcessar.setEnabled(true);
                } else if (senhaEdit.isEmpty()) {
                    cmpSenha.setError(getString(R.string.erro_campo_obrigatorio));
                    cmpSenha.requestFocus();
                    btnAcessar.setEnabled(true);
                } else if (senhaEdit.length() < 3) { //senha exige 3 ou mais caracteres
                    cmpSenha.setError(getString(R.string.erro_senha_invalida));
                    cmpSenha.requestFocus();
                    btnAcessar.setEnabled(true);
                } else {
                    validaDadosLogin(usuarioEdit, senhaEdit);
                }
            }
        });

        txtPolitica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.view_web, null);
                WebView webView = dialogView.findViewById(R.id.webView);
                builder.setCancelable(true);
                webView.loadUrl("file:///android_asset/politicaprivacidade.html");
                builder.setView(dialogView);
                AlertDialog dialogWeb = builder.create();
                dialogWeb.show();
            }
        });

        imgDownAtualizacao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogAtualizacao();
            }
        });
    }

    /*
     * Função para quando virar a tela não resetar o layout
     **/
    @Override
    public void onConfigurationChanged(@NotNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        encerrar();
    }

    @SuppressLint("ApplySharedPref")
    public void loginAutomatico() {
        SharedPreferences prefs = getSharedPreferences(INFORMACOES_LOGIN_AUTOMATICO, MODE_PRIVATE);
        final String loginArmazenado = prefs.getString("login", null);
        final String senhaArmazenada = prefs.getString("senha", null);

        if (loginArmazenado != null) {
            try {
                CriptografaString criptografaString = new CriptografaString();
                loginDescriptografado = criptografaString.decrypt(loginArmazenado);
                senhaDescriptografada = criptografaString.decrypt(senhaArmazenada);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (!loginDescriptografado.trim().equals("")) {
            validaDadosLogin(loginDescriptografado, senhaDescriptografada);
        } else {
            if (dialogLogin != null) {
                dialogLogin.dismiss();
            }

            if (ContextCompat.checkSelfPermission(Login.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(Login.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(Login.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA}, 0);
            }
        }
    }

    @SuppressLint("ApplySharedPref")
    public void validaDadosLogin(String usuario, String senha) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
        builder.setTitle("Erro de conexão!");
        builder.setIcon(R.drawable.errordatabase);
        builder.setCancelable(false);

        if (isConnected) {
            try {
                conecta.selectUsuarios(usuario, funcoesGenericas.cripto(senha), "", "", "");
                if (!conecta.usuarioBdExt.equals("")) {
                    if (conecta.statusUsuarioBdExt.equals("I") || conecta.statusUsuarioBdExt.equals("")) {
                        Snackbar snackbar = Snackbar.make(viewSnackBar, "Usuário bloqueado!\nEntre em contato com o Administrador.", Snackbar.LENGTH_LONG);
                        View snackBarView = snackbar.getView();
                        snackBarView.setBackgroundColor(Color.parseColor("#FFA500"));
                        snackbar.show();

                        Login.this.getSharedPreferences(INFORMACOES_LOGIN_AUTOMATICO, MODE_PRIVATE).edit().clear().commit();
                        Login.this.getSharedPreferences(VERSAO_ATUAL_SQLITE, MODE_PRIVATE).edit().clear().commit();

                        btnAcessar.setEnabled(true);

                        db.onDelete(Login.this);
                    } else {
                        GettersSetters.setUsuarioLogado(conecta.usuarioBdExt);
                        GettersSetters.setIdUsuarioLogado(conecta.idUsuarioBdExt);
                        GettersSetters.setNomeVend(conecta.nomeUsuarioBdExt);
                        GettersSetters.setCodTotvs(conecta.codTotvsUsuarioBdExt);
                        GettersSetters.setNomeTotvs(conecta.nomeTotvsUsuarioBdExt);
                        GettersSetters.setOpcSqlite(conecta.opcSqLiteUsuarioBdExt);
                        GettersSetters.setEmailUsuario(conecta.emailUsuarioBdExt);
                        GettersSetters.setTipoUsuario(conecta.tipoUsuarioBdExt);

                        //deleta usuário para atualizar os dados
                        db.delUsuario(conecta.idUsuarioBdExt);

                        //alimenta as variaveis com os dados usados para a inserção no banco local
                        idUsuarioBdExt = conecta.idUsuarioBdExt;
                        nomeUsuarioBdExt = conecta.nomeUsuarioBdExt;
                        loginUsuarioBdExt = conecta.usuarioBdExt;
                        senhaUsuarioBdExt = funcoesGenericas.cripto(conecta.senhaUsuarioBdExt);
                        statusUsuarioBdExt = conecta.statusUsuarioBdExt;
                        emailUsuarioBdExt = conecta.emailUsuarioBdExt;
                        cpfUsuarioBdExt = conecta.cpfUsuarioBdExt;
                        tipoUsuarioBdExt = conecta.tipoUsuarioBdExt;
                        codigoTotvsUsuarioBdExt = conecta.codTotvsUsuarioBdExt;
                        nomeTotvsUsuarioBdExt = conecta.nomeTotvsUsuarioBdExt;

                        try {
                            PackageInfo pInfo = Login.this.getPackageManager().getPackageInfo(Login.this.getPackageName(), PackageManager.GET_ACTIVITIES);
                            conecta.updDadosAuditApp(idUsuarioBdExt, "VERSAO_COLETA_INSTALADA", pInfo.versionName);
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }

                        asyncProcessoLogin = new ProcessoLogin();
                        asyncProcessoLogin.execute(loginUsuarioBdExt, senhaUsuarioBdExt, codigoTotvsUsuarioBdExt, nomeTotvsUsuarioBdExt);
                    }
                } else {
                    Snackbar snackbar = Snackbar.make(viewSnackBar, "Usuário e/ou Senha incorretos. Tente novamente!", Snackbar.LENGTH_LONG);
                    View snackBarView = snackbar.getView();
                    snackBarView.setBackgroundColor(Color.parseColor("#FFA500"));
                    snackbar.show();

                    Login.this.getSharedPreferences(INFORMACOES_LOGIN_AUTOMATICO, MODE_PRIVATE).edit().clear().commit();
                    Login.this.getSharedPreferences(VERSAO_ATUAL_SQLITE, MODE_PRIVATE).edit().clear().commit();

                    btnAcessar.setEnabled(true);

                    cmpSenha.getText().clear();
                    cmpUsuario.getText().clear();
                    chkLembrarMe.setChecked(false);

                    cmpUsuario.setFocusable(true);
                    cmpUsuario.requestFocus();
                }
            } catch (Exception e) {
                btnAcessar.setEnabled(true);
                builder.setMessage("Erro ao conectar: " + e.getMessage());
                builder.setNegativeButton("Sair", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        encerrar();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        } else {
            try {
                Cursor res = db.buscaUsuarios(usuario); //busca dos dados do usuário no banco local
                if (res.getCount() == 0) {
                    Snackbar snackbar = Snackbar.make(viewSnackBar, "Usuário e/ou Senha incorretos. Tente novamente!", Snackbar.LENGTH_LONG);
                    View snackBarView = snackbar.getView();
                    snackBarView.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                    snackbar.show();
                    btnAcessar.setEnabled(true);

                    cmpSenha.getText().clear();
                    cmpUsuario.getText().clear();
                    chkLembrarMe.setChecked(false);

                    cmpUsuario.setFocusable(true);
                    cmpUsuario.requestFocus();
                } else {
                    while (res.moveToNext()) {
                        CriptografaString descriptografaString = new CriptografaString();
                        senhaLocal = descriptografaString.decrypt(res.getString(3).trim());
                        idUsuarioBdLocal = res.getString(0).trim();
                        usuarioLocal = res.getString(2).trim();
                        statusLocal = res.getString(4).trim();

                        if (statusLocal.equals("I") || statusLocal.equals("")) {
                            Snackbar snackbar = Snackbar.make(viewSnackBar, "Usuário bloqueado!\nEntre em contato com o Administrador.", Snackbar.LENGTH_LONG);
                            View snackBarView = snackbar.getView();
                            snackBarView.setBackgroundColor(Color.parseColor("#FFA500"));
                            snackbar.show();

                            Login.this.getSharedPreferences(INFORMACOES_LOGIN_AUTOMATICO, MODE_PRIVATE).edit().clear().commit();
                            Login.this.getSharedPreferences(VERSAO_ATUAL_SQLITE, MODE_PRIVATE).edit().clear().commit();

                            btnAcessar.setEnabled(true);

                            db.onDelete(Login.this);
                        } else if (usuario.equals(usuarioLocal) && senha.equals(senhaLocal)) {
                            GettersSetters.setIdUsuarioLogado(res.getString(0).trim());
                            GettersSetters.setNomeVend(res.getString(1).trim());
                            GettersSetters.setUsuarioLogado(res.getString(2).trim());
                            GettersSetters.setEmailUsuario(res.getString(5).trim()); // e-mail do usuário
                            GettersSetters.setTipoUsuario(res.getString(7).trim()); // tipo do usuário
                            GettersSetters.setCodTotvs(res.getString(8).trim());
                            GettersSetters.setNomeTotvs(res.getString(9).trim());

                            Intent it = new Intent(Login.this, Home.class);
                            GettersSetters.setIsLogin(true);
                            startActivity(it);
                        } else {
                            Toast.makeText(getApplication(), "Usuário e/ou Senha incorretos!", Toast.LENGTH_LONG).show();
                            btnAcessar.setEnabled(true);
                        }
                    }
                }
            } catch (Exception e) {
                btnAcessar.setEnabled(true);
                builder.setMessage("Erro ao conectar: " + e.getMessage());
                builder.setNegativeButton("Sair", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        encerrar();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class VerificaConexaoInternet extends AsyncTask<Boolean, String, Boolean> {
        AlertDialog alertDialog;

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPreExecute() {
            AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
            LayoutInflater layoutInflater = getLayoutInflater();
            final View dialogView = layoutInflater.inflate(R.layout.dialog_carregamento, null);
            final TextView textView = dialogView.findViewById(R.id.txtInfoCarreg);
            textView.setText("Verificando conexão com a Internet.\nAguarde...");
            builder.setCancelable(false);
            builder.setPositiveButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    asyncVerificaConexaoInternet.cancel(true);
                    Toast.makeText(Login.this, "Inicialização Cancelada", Toast.LENGTH_LONG).show();
                    onBackPressed();
                }
            });
            builder.setView(dialogView);
            alertDialog = builder.create();
            alertDialog.show();
        }

        @SuppressLint({"ApplySharedPref", "DefaultLocale", "WrongThread"})
        @Override
        protected Boolean doInBackground(Boolean... params) {
            isConnected = CheckConnection.internetConnAvaliable();
            return isConnected;
        }

        @Override
        protected void onPostExecute(final Boolean sucesso) {
            if (sucesso) {
                asyncInicializacaoApp = new InicializacaoApp();
                asyncInicializacaoApp.execute();
            } else {
                AlertDialog.Builder builderFilial = new AlertDialog.Builder(Login.this);
                builderFilial.setTitle("Sem conexão!");
                builderFilial.setIcon(R.drawable.off);
                builderFilial.setCancelable(false);
                builderFilial.setMessage("Sem conexão!");
                builderFilial.setPositiveButton("Offline", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        loginAutomatico();
                    }
                });
                builderFilial.setNegativeButton("Sair", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        encerrar();
                    }
                });
                AlertDialog dialog = builderFilial.create();
                dialog.show();
            }

            if (alertDialog != null) {
                alertDialog.dismiss();
            }
        }
    }

    @SuppressWarnings("Convert2Lambda")
    @SuppressLint("StaticFieldLeak")
    public class InicializacaoApp extends AsyncTask<Boolean, String, Boolean> {
        AlertDialog alertDialog;

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPreExecute() {
            AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
            LayoutInflater layoutInflater = getLayoutInflater();
            final View dialogView = layoutInflater.inflate(R.layout.dialog_carregamento, null);
            final TextView textView = dialogView.findViewById(R.id.txtInfoCarreg);
            textView.setText("Validando conexões.\nAguarde...");
            builder.setCancelable(false);
            builder.setPositiveButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    asyncInicializacaoApp.cancel(true);
                    Toast.makeText(Login.this, "Inicialização Cancelada", Toast.LENGTH_LONG).show();
                    onBackPressed();
                }
            });
            builder.setView(dialogView);
            alertDialog = builder.create();
            alertDialog.show();
        }

        @SuppressLint({"ApplySharedPref", "DefaultLocale", "WrongThread"})
        @Override
        protected Boolean doInBackground(Boolean... params) {
            try {
                /* VERIFICAÇÃO DE CONEXÃO COM BD */
                if (conecta.ConnectToDatabase("C", GettersSetters.getConexaoBD()) != null) {
                    /* * VALIDAÇÃO DE CONEXÃO **/
                    if (db.selectUsuariosVendedores("", "", false, true).getCount() == 0) {
                        if (isConnected) {
                            /** ATUALIZA USUÁRIOS X VENDEDORES **/
                            conecta.insereUsuariosVendedores(Login.this);
                            return true;
                        } else {
                            GettersSetters.setErroEnvioColetaBDExt("Sem dados. Favor conectar na internet para o primeiro acesso!");
                            return false;
                        }
                    } else {
                        return true;
                    }
                } else {
                    return false;
                }
            } catch (Exception exp) {
                GettersSetters.setErroEnvioColetaBDExt(exp.getMessage());
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean sucesso) {
            if (sucesso) {
                if (isConnected) {
                    buscaAtualizacao();
                } else {
                    loginAutomatico();
                }
            } else {
                AlertDialog.Builder builderFilial = new AlertDialog.Builder(Login.this);
                builderFilial.setTitle("Sem conexão");
                builderFilial.setIcon(R.drawable.error);
                builderFilial.setCancelable(false);
                builderFilial.setMessage(!GettersSetters.getErroEnvioColetaBDExt().equals("") ? GettersSetters.getErroEnvioColetaBDExt() : "Erro ao iniciar.");
                if (!GettersSetters.getErroEnvioColetaBDExt().equals("")) {
                    builderFilial.setPositiveButton("Tentar novamente", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            asyncInicializacaoApp = new InicializacaoApp();
                            asyncInicializacaoApp.execute();
                        }
                    });
                }
                builderFilial.setNegativeButton("Sair", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        encerrar();
                    }
                });
                AlertDialog dialog = builderFilial.create();
                dialog.show();
            }

            if (alertDialog != null) {
                alertDialog.dismiss();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class AtualizaFiliais extends AsyncTask<Boolean, String, Boolean> {
        AlertDialog alertDialogFiliais = null;

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPreExecute() {
            AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
            LayoutInflater layoutInflater = getLayoutInflater();
            final View dialogView = layoutInflater.inflate(R.layout.dialog_carregamento, null);
            final TextView textView = dialogView.findViewById(R.id.txtInfoCarreg);
            textView.setText("Ajustando Filiais.\nAguarde...");
            builder.setCancelable(false);
            builder.setPositiveButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    asyncAtualizaFiliais.cancel(true);
                    Toast.makeText(Login.this, "Inicialização Cancelada", Toast.LENGTH_LONG).show();
                    onBackPressed();
                }
            });
            builder.setView(dialogView);
            alertDialogFiliais = builder.create();
            alertDialogFiliais.show();
        }

        @SuppressLint({"ApplySharedPref", "DefaultLocale", "WrongThread"})
        @Override
        protected Boolean doInBackground(Boolean... params) {
            Cursor resFiliais;
            String codVendedor;

            try {
                codVendedor = ((GettersSetters.getTipoUsuario().equals("V") || GettersSetters.getTipoUsuario().equals("T") || GettersSetters.getTipoUsuario().equals("P")) ?
                        (GettersSetters.getCodigoVendedor().equals("") ? GettersSetters.getIdUsuarioLogado() : GettersSetters.getCodigoVendedor()) : "");

                resFiliais = db.buscaFilais();

                if (resFiliais.getCount() == 0) {
                    db.delFiliais();
                    db.delFiliaisUsuarios();

                    conecta.selectFiliais();
                    if (conecta.arrIdFilial.size() != resFiliais.getCount()) {
                        db.delFiliais();
                        for (int i = 0; i < conecta.arrIdFilial.size(); i++) { //preenche com dados da filial
                            db.insereFilial(conecta.arrIdFilial.get(i), conecta.arrFilial.get(i), conecta.arrCodigoAuxFilial.get(i), conecta.arrRazaoSocialFilial.get(i), conecta.arrNomeFantasiaFilial.get(i));
                        }
                    }

                    resFiliais = db.buscaFiliaisUsuarios(codVendedor);
                    conecta.selectFiliaisUsuarios(codVendedor);
                    /** Inserção de Filiais X Usuários no banco local **/
                    if ((resFiliais.getCount() == 0) || conecta.arrRazaoSocialFiliaisUsuarios.size() != resFiliais.getCount()) { //se filiais usuarios remotos > que local, zera tabela a reinsere
                        for (int i = 0; i < conecta.arrRazaoSocialFiliaisUsuarios.size(); i++) {
                            db.insereFiliaisUsuarios(conecta.arrIdEmpFiliaisUsuarios.get(i), conecta.arrIdFiliaisUsuarios.get(i), conecta.arrCodigoAuxiliarFiliaisUsuarios.get(i), conecta.arrIdUsrFiliaisUsuarios.get(i), conecta.arrRazaoSocialFiliaisUsuarios.get(i));
                        }
                    }
                    conecta.updDadosAuditApp(GettersSetters.getIdUsuarioLogado(), "DT_ATT_CFG_COL", GettersSetters.getDataEN() + " - " + GettersSetters.getHora());
                }

                return true;
            } catch (Exception exp) {
                new Thread() {
                    public void run() {
                        Login.this.runOnUiThread(new Runnable() {
                            public void run() {
                                AlertDialog.Builder builderFilial = new AlertDialog.Builder(Login.this);
                                builderFilial.setTitle("Erro inserção filiais!");
                                builderFilial.setIcon(R.drawable.error);
                                builderFilial.setCancelable(false);
                                builderFilial.setMessage(exp.getMessage());
                                builderFilial.setNegativeButton("Fechar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Thread.currentThread().interrupt();
                                    }
                                });
                                AlertDialog dialog = builderFilial.create();
                                dialog.show();
                            }
                        });
                    }
                }.start();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean sucesso) {
            if (sucesso) {
                Intent it = new Intent(Login.this, Home.class);
                GettersSetters.setIsLogin(true);
                startActivity(it);
                Login.this.finish();
            }

            if (alertDialogFiliais != null) {
                alertDialogFiliais.dismiss();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class ProcessoLogin extends AsyncTask<String, String, Boolean> {
        AlertDialog dialogLogin = null;

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPreExecute() {
            AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
            LayoutInflater layoutInflater = getLayoutInflater();
            final View dialogView = layoutInflater.inflate(R.layout.dialog_carregamento, null);
            final TextView textView = dialogView.findViewById(R.id.txtInfoCarreg);
            textView.setText("Realizando Login.\nAguarde...");
            builder.setCancelable(false);
            builder.setView(dialogView);
            dialogLogin = builder.create();
            dialogLogin.show();
        }

        @SuppressLint({"ApplySharedPref", "DefaultLocale", "WrongThread"})
        @Override
        protected Boolean doInBackground(String... params) {
            boolean sucesso = true;
            String numeroColeta, sequenciaNumeroColeta;

            try {
                if (chkLembrarMe.isChecked()) {
                    Login.this.getSharedPreferences(INFORMACOES_LOGIN_AUTOMATICO, MODE_PRIVATE).edit().clear().commit();

                    CriptografaString criptografaString = new CriptografaString();
                    SharedPreferences.Editor editor = getSharedPreferences(INFORMACOES_LOGIN_AUTOMATICO, MODE_PRIVATE).edit();
                    editor.putString("login", criptografaString.encrypt(params[0]));
                    editor.putString("senha", criptografaString.encrypt(params[1]));
                    editor.putString("codTotvs", criptografaString.encrypt(params[2]));
                    editor.putString("nomeTotvs", criptografaString.encrypt(params[3]));
                    editor.commit();
                }

                /* * AJUSTA IDENTIFICADORES DA COLETA INICIAIS **/
                Cursor cursorNumfCol = db.selectNumColeta();
                if (cursorNumfCol.getCount() == 0) {
                    sequenciaNumeroColeta = conecta.selecionaMaxNumColeta(GettersSetters.getIdUsuarioLogado());
                    numeroColeta = String.format("%06d", Integer.parseInt(GettersSetters.getIdUsuarioLogado())).substring(2, 6) + sequenciaNumeroColeta; // CÓDIGO DO VENDENDOR COM 4 ULTIMOS CARACTERES + SEQUENCIAL

                    db.insereNumColeta(sequenciaNumeroColeta, GettersSetters.getIdUsuarioLogado(), numeroColeta, GettersSetters.getDataColetaEN());
                }

                /* ATUALIZA DADOS NO BD LOCAL */
                Cursor res = db.buscaUsuarios(params[0]);
                if (res.getCount() == 0) {
                    sucesso = db.insereUsuario(idUsuarioBdExt, nomeUsuarioBdExt, loginUsuarioBdExt, senhaUsuarioBdExt, statusUsuarioBdExt, emailUsuarioBdExt, cpfUsuarioBdExt, tipoUsuarioBdExt, codigoTotvsUsuarioBdExt, nomeTotvsUsuarioBdExt);
                } else {
                    if (res.moveToFirst()) {
                        do {
                            idUsuarioBdLocal = res.getString(0);
                            senhaUsuarioBdLocal = res.getString(3);
                            if (chkLembrarMe.isChecked()) {
                                if (!senhaUsuarioBdLocal.equals(senhaUsuarioBdExt)) {
                                    Integer idUsr = db.delUsuario(idUsuarioBdLocal);
                                    if (idUsr > 0) {
                                        sucesso = db.insereUsuario(idUsuarioBdExt, nomeUsuarioBdExt, loginUsuarioBdExt, senhaUsuarioBdExt, statusUsuarioBdExt, emailUsuarioBdExt, cpfUsuarioBdExt, tipoUsuarioBdExt, codigoTotvsUsuarioBdExt, nomeTotvsUsuarioBdExt);
                                    }
                                }
                            } else {
                                sucesso = true;
                            }
                        } while (res.moveToNext());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                GettersSetters.setExpSqLite(e.getMessage());
                sucesso = false;
            }
            return sucesso;
        }

        @Override
        protected void onPostExecute(Boolean sucesso) {
            if (sucesso) {
                asyncAtualizaFiliais = new AtualizaFiliais();
                asyncAtualizaFiliais.execute();

                cmpUsuario.setText("");
                cmpSenha.setText("");
            } else {
                AlertDialog.Builder builderFilial = new AlertDialog.Builder(Login.this);
                builderFilial.setTitle("Erro ao realizar o Login!");
                builderFilial.setIcon(R.drawable.error);
                builderFilial.setCancelable(false);
                builderFilial.setMessage(GettersSetters.getExpSqLite());
                builderFilial.setNegativeButton("Fechar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Thread.currentThread().interrupt();
                    }
                });
                AlertDialog dialog = builderFilial.create();
                dialog.show();

                btnAcessar.setEnabled(true);
            }

            if (dialogLogin != null) {
                dialogLogin.dismiss();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class AtualizacaoApp extends AsyncTask<File, String, File> {
        ProgressDialog mProgressDialog;
        String nomeFile = "/coleta.apk";

        int tamanho = 0;
        long total = 0;
        int count;
        InputStream inputStream = null;
        OutputStream outputStream = null;

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPreExecute() {
            mProgressDialog = new ProgressDialog(Login.this, 0);
            mProgressDialog.setIcon(R.drawable.download);
            mProgressDialog.setTitle("Atualização");
            mProgressDialog.setMessage("Baixando Atualização.\nAguarde...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMax(100);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    asyncAtualizaApp.cancel(true);
                }
            });
            mProgressDialog.show();
        }

        @SuppressLint({"ApplySharedPref", "DefaultLocale", "WrongThread"})
        @Override
        protected File doInBackground(File... params) {
            boolean diretorioOk = true;

            if (!params[0].exists()) {
                diretorioOk = params[0].mkdirs();
            } else {
                FuncoesGenericas.deletaArquivos(params[0]);
            }

            if (diretorioOk) {
                try {
                    URL url = new URL(urlColeta);
                    HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                    urlConnection.connect();

                    tamanho = urlConnection.getContentLength();

                    if (tamanho <= 0) {
                        tamanho = 30000000; //14359889; //urlConnection.getContentLength();
                    }

                    inputStream = new BufferedInputStream(url.openStream(), tamanho);
                    outputStream = new FileOutputStream(apkAtualizacao + nomeFile);

                    byte[] data = new byte[1024];

                    while ((count = inputStream.read(data)) != -1) {
                        total += count;
                        publishProgress("" + (int) ((total * 100) / tamanho));
                        outputStream.write(data, 0, count);
                    }

                    outputStream.flush();
                    outputStream.close();
                    inputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    apkAtualizacao = null;
                }
            } else {
                apkAtualizacao = null;
            }

            return apkAtualizacao;
        }

        /**
         * Updating progress bar
         */
        protected void onProgressUpdate(String... progress) {
            mProgressDialog.setProgress(Integer.parseInt(progress[0]));
        }

        @Override
        protected void onPostExecute(File file) {
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
            }

            if (file != null) {
                MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
                file = new File(apkAtualizacao, nomeFile);
                String ext = file.getName().substring(file.getName().lastIndexOf(".") + 1);
                String type = mimeTypeMap.getMimeTypeFromExtension(ext);

                Intent install = new Intent();
                install.setAction(Intent.ACTION_VIEW);
                install.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                install.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Uri fileUri = FileProvider.getUriForFile(Login.this, BuildConfig.APPLICATION_ID + ".provider", file);
                    install.setDataAndType(fileUri, type);
                } else {
                    install.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    install.setDataAndType(Uri.fromFile(file), type);
                }

                try {
                    Login.this.startActivity(install);
                    finish();
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(Login.this, "No activity found to open this attachment.", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(Login.this, "Atualização não baixada! Tente novamente.", Toast.LENGTH_LONG).show();
            }
        }
    }

    @SuppressLint("ApplySharedPref")
    public void buscaAtualizacao() {
        PackageInfo pInfo = null;
        String versaoAtualVigencia = "";
        String versaoPrd = "";
        String diferenca = "0";

        try {
            pInfo = Login.this.getPackageManager().getPackageInfo(Login.this.getPackageName(), PackageManager.GET_ACTIVITIES);

            if (pInfo != null) {
                String versaoAtual = pInfo.versionName;
                try {
                    ResultSet rsVersaoPrd = conecta.selectNovaVersao();
                    while (rsVersaoPrd.next()) {
                        versaoPrd = rsVersaoPrd.getString(1);
                    }
                    if (Double.parseDouble(versaoAtual) < Double.parseDouble(versaoPrd)) {
                        imgDownAtualizacao.setVisibility(View.VISIBLE);

                        ResultSet rsVersaoAtual = conecta.selectVersaoApp(versaoAtual);
                        while (rsVersaoAtual.next()) {
                            versaoAtualVigencia = rsVersaoAtual.getString(3);
                            diferenca = rsVersaoAtual.getString(5);
                        }

                        AlertDialog.Builder builderAtualizcao = new AlertDialog.Builder(Login.this);
                        builderAtualizcao.setTitle("Atualização disponível!");
                        builderAtualizcao.setIcon(R.drawable.atualizar);
                        builderAtualizcao.setCancelable(false);

                        final String dataConv = GettersSetters.converteData(versaoAtualVigencia.substring(0, 4) + "-" + versaoAtualVigencia.substring(4, 6) + "-" + versaoAtualVigencia.substring(6, 8), "EN");

                        if ((Double.parseDouble(diferenca) <= 3)) { //OBRIGATORIO ATUALIZAR QUANDO ESTIVER 3 VERSÕES DESATUALIZADAS
                            builderAtualizcao.setMessage("Versão " + versaoPrd + " está disponível para atualização!");

                            builderAtualizcao.setNeutralButton("Tavlez depois", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    AlertDialog.Builder builderAtualizarFechar = new AlertDialog.Builder(Login.this);
                                    builderAtualizarFechar.setTitle("Adiar Atualização?");
                                    builderAtualizarFechar.setIcon(R.drawable.interrogacao);
                                    builderAtualizarFechar.setCancelable(false);
                                    builderAtualizarFechar.setMessage("Deseja adiar a Atualização?");
                                    builderAtualizarFechar.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            loginAutomatico();
                                        }
                                    });
                                    builderAtualizarFechar.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE).setVisibility(View.GONE);
                                            buscaAtualizacao();
                                        }
                                    });
                                    AlertDialog dialogFechaAtual = builderAtualizarFechar.create();
                                    dialogFechaAtual.show();
                                }
                            });
                        } else {
                            builderAtualizcao.setMessage("Seu aplicativo está desatualizado!\n" +
                                    "A versão atual expirou em: " + dataConv + "\n" +
                                    "Versão Atual: " + versaoAtual + "\n" +
                                    "Nova Versão: " + versaoPrd + "\n" +
                                    "ATUALIZAÇÃO OBRIGATÓRIA!");
                        }
                        builderAtualizcao.setPositiveButton("Atualizar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (ContextCompat.checkSelfPermission(Login.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                    buscaAtualizacao();
                                    ActivityCompat.requestPermissions(Login.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
                                } else {
                                    dialogAtualizacao();
                                }
                            }
                        });
                        builderAtualizcao.setNegativeButton("Sair", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                encerrar();
                            }
                        });
                        AlertDialog dialog = builderAtualizcao.create();
                        dialog.show();

                        if (ContextCompat.checkSelfPermission(Login.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                                ContextCompat.checkSelfPermission(Login.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(Login.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.CAMERA}, 0);
                        }
                    } else {
                        loginAutomatico();
                    }
                } catch (Exception e) {
                    e.printStackTrace();

                    AlertDialog.Builder builderFilial = new AlertDialog.Builder(Login.this);
                    builderFilial.setTitle("Erro de conexão!");
                    builderFilial.setIcon(R.drawable.error);
                    builderFilial.setCancelable(false);
                    builderFilial.setMessage(e.getMessage());
                    builderFilial.setNegativeButton("Fechar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Thread.currentThread().interrupt();
                            encerrar();
                        }
                    });
                    AlertDialog dialog = builderFilial.create();
                    dialog.show();
                }
            } else {
                loginAutomatico();
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            Snackbar.make(viewSnackBar, "Erro ao capturar a versão!", Snackbar.LENGTH_LONG).setAction("Fechar", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            }).show();
        }
    }

    public void dialogAtualizacao() {
        apkAtualizacao = new File(Environment.getExternalStorageDirectory() + "/Coleta");
        asyncAtualizaApp = new AtualizacaoApp();

        AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
        LayoutInflater layoutInflater = getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.dialog_diretorios_atualizacao, null);
        CardView cardDropbox = view.findViewById(R.id.cardDropbox);
        CardView cardGDrive = view.findViewById(R.id.cardGDrive);

        cardDropbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                urlColeta = "https://www.dropbox.com/s/zcm6tgy39ovl2nf/app-release.apk?dl=1";
                asyncAtualizaApp.execute(apkAtualizacao);
            }
        });

        cardGDrive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                urlColeta = "https://drive.google.com/file/d/1o-C4Lsvm8RqpYk_1J9NzWL_0LxuUGlDx/view?usp=sharing";

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(urlColeta));
                startActivity(intent);
            }
        });
        builder.setCancelable(false);
        builder.setNegativeButton("Sair", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                encerrar();
            }
        });
        builder.setView(view);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void encerrar() {
        Thread.currentThread().interrupt();
        GettersSetters.resetGettersSetters();
        finish();
        finishAffinity();
        System.exit(0);
        android.os.Process.killProcess(android.os.Process.myPid());
        funcoesGenericas.clearCache(getBaseContext());
        super.onDestroy();
    }
}