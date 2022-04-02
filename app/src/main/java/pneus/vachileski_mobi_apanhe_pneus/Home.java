package pneus.vachileski_mobi_apanhe_pneus;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
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
import android.os.Handler;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.material.snackbar.Snackbar;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.AesKeyStrength;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.model.enums.EncryptionMethod;
import net.lingala.zip4j.progress.ProgressMonitor;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.net.ssl.HttpsURLConnection;

import pneus.vachileski_mobi_funcoes_genericas.CheckConnection;
import pneus.vachileski_mobi_funcoes_genericas.ConexaoBDExt;
import pneus.vachileski_mobi_funcoes_genericas.ConexaoBDInt;
import pneus.vachileski_mobi_funcoes_genericas.DadosBD;
import pneus.vachileski_mobi_funcoes_genericas.FuncoesGenericas;
import pneus.vachileski_mobi_funcoes_genericas.GettersSetters;

@SuppressWarnings({"DanglingJavadoc", "Convert2Lambda", "ResultOfMethodCallIgnored"})
public class Home extends AppCompatActivity {
    ImageView btnColeta, btnOpcoes, btnBuscarColetas;
    Button btnSair, btnDetalhesVersao;
    View viewSnackBar, viewSnackBarConn;
    TextView txtVersao, txtTeste;

    ConexaoBDExt conecta = new ConexaoBDExt();
    ConexaoBDInt db;

    int contaPressVoltar = 0;
    int segundos = 3;

    String[] avisos;

    public static ProgressDialog mProgressDialog;

    public static final String DADOS_LOGIN = "INFORMACOES_LOGIN_AUTOMATICO";
    public static final String DADOS_CONTROLE = "DADOS_CONTROLE_INFORMACOES";
    public static final String ATUALIZACAO_DADOS = "ATUALIZACAO_DADOS";

    String versaoAtualClientes = "";
    String versaoAtualProdutos = "";
    String versaoAtualBorracheiros = "";
    String versaoAtualCondPagto = "";
    String novaVersaoClientes = "";
    String novaVersaoProdutos = "";
    String novaVersaoBorracheiros = "";
    String novaVersaoCondPgto = "";
    boolean isAtualizaObrigatDados = false;
    boolean isCancelaAtualizacaoDados = false;
    int atualizaDadosObrigat = 0;

    String senhaLib = "";

    String numeroTelefone = "";
    String[] dadoSelecionado = null;

    public static boolean isColetaEnvio = false;
    boolean vendedorSelecionado = false;

    ArrayList<String> arrIdUsuario = new ArrayList<>();
    ArrayList<String> arrUsuario = new ArrayList<>();
    ArrayList<String> arrCodTotvs = new ArrayList<>();
    ArrayList<String> arrNomeTotvs = new ArrayList<>();
    ArrayList<String> arrTipovend = new ArrayList<>();
    ArrayList<String> arrCliPermitidos = new ArrayList<>();
    ArrayList<String> arrCodBorrachPat = new ArrayList<>();

    VerificaBorracheiros verificaBorracheiros = null;
    VerificaCondPgto verificaCondPgto = null;
    VerificacaoProdutos verificacaoProdutos = null;
    VerificaClientes verificaClientes = null;
    AtualizaFiliaisVendedores atualizaFiliaisVendedores = null;
    GeraBackup geraBackup = null;
    AtualizacaoApp asyncAtualizaApp = null;

    /* ARQUIVOS DE BACKUP */
    InputStream inputStream;
    OutputStream outputStream = null;
    File outFileBackup = null;

    AlertDialog dialogCelular = null;
    AlertDialog dialogAdmin = null;
    AlertDialog alertDialog = null;

    FuncoesGenericas funcoesGenericas = new FuncoesGenericas();

    String urlColeta = "";

    File pastaBackup = null;
    File pastaArquivos = null;
    File apkAtualizacao = null;

    @SuppressLint({"SourceLockedOrientationActivity", "DefaultLocale", "SetTextI18n"})
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        txtTeste = findViewById(R.id.txtTeste);
        btnSair = findViewById(R.id.btnSair);
        btnDetalhesVersao = findViewById(R.id.txtDetalhesVersao);
        btnColeta = findViewById(R.id.btnColeta);
        btnOpcoes = findViewById(R.id.btnAdmin);
        btnBuscarColetas = findViewById(R.id.imgViewColeStatus);
        viewSnackBar = findViewById(R.id.viewSnackBar);
        viewSnackBarConn = findViewById(R.id.viewSnackBarConn);
        txtVersao = findViewById(R.id.txtVersao);

        db = new ConexaoBDInt(this);

        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            txtVersao.setText("Versão " + pInfo.versionName);

            if (DadosBD.bdColeta.toLowerCase().contains("teste")) {
                txtTeste.setVisibility(View.VISIBLE);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        FuncoesGenericas.deletaArquivos(new File(Environment.getExternalStorageDirectory() + "/Coleta"));

        if (Login.isConnected) {
            verificaColetasNaoEnviadas(false);
            btnDetalhesVersao.setVisibility(View.VISIBLE);

            //AVISOS
            if (GettersSetters.isIsLogin()) {
                GettersSetters.setIsLogin(false);
                try {
                    int totalDados;
                    ResultSet resultSet = conecta.selecionaAvisos();

                    try {
                        resultSet.last();
                        totalDados = resultSet.getRow();
                        resultSet.beforeFirst();
                    } catch (SQLException e) {
                        e.printStackTrace();
                        totalDados = 0;
                    }

                    avisos = new String[totalDados];
                    while (resultSet.next()) {
                        avisos[resultSet.getRow() - 1] = "* " + resultSet.getString(1) + "\n";
                    }

                    if (totalDados > 0) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
                        builder.setCancelable(false);
                        LayoutInflater layoutInflater = getLayoutInflater();
                        View view = layoutInflater.inflate(R.layout.dialog_aviso, null);
                        TextView textView = view.findViewById(R.id.txtAviso);
                        CheckBox checkBox = view.findViewById(R.id.chkOkAviso);
                        Button button = view.findViewById(R.id.btnOkAviso);
                        ImageView imgAviso1 = view.findViewById(R.id.imgAviso1);
                        ImageView imgAviso2 = view.findViewById(R.id.imgAviso2);

                        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
                        alphaAnimation.setDuration(200);
                        alphaAnimation.setRepeatCount(100);
                        alphaAnimation.setRepeatMode(Animation.REVERSE);
                        imgAviso1.setAnimation(alphaAnimation);
                        imgAviso2.setAnimation(alphaAnimation);

                        textView.setText(Arrays.toString(avisos).replace("[", "").replace("]", "").replace("\n,", "\n\n"));

                        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                                if (isChecked) {
                                    button.setVisibility(View.VISIBLE);

                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                while (segundos >= 1) {
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            button.setText("Aguarde (" + segundos + ")");
                                                        }
                                                    });

                                                    Thread.sleep(1000);
                                                    if (segundos == 1) {
                                                        runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                button.setText("Fechar");
                                                                button.setEnabled(true);
                                                            }
                                                        });

                                                        break;
                                                    } else {
                                                        segundos--;
                                                    }
                                                }
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }).start();
                                } else {
                                    segundos = 3;
                                    button.setEnabled(false);
                                    button.setVisibility(View.GONE);
                                }
                            }
                        });

                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                segundos = 3;
                                alertDialog.dismiss();
                            }
                        });

                        builder.setView(view);
                        alertDialog = builder.create();
                        alertDialog.show();
                    } else {
                        if (ContextCompat.checkSelfPermission(Home.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(Home.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } else {
            Snackbar snackbar = Snackbar.make(viewSnackBarConn, "Sem conexão!", Snackbar.LENGTH_LONG);
            View snackBarView = snackbar.getView();
            snackBarView.setBackgroundColor(getResources().getColor(R.color.red));
            snackbar.show();

            if (ContextCompat.checkSelfPermission(Home.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(Home.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            }
        }

        if (GettersSetters.getTipoUsuario().equals("A")) {
            //EXIBE CONEXÃO
            Snackbar snackbar = Snackbar.make(viewSnackBarConn, "Conexão: " + GettersSetters.getStringConexao(), Snackbar.LENGTH_LONG);
            View snackBarView = snackbar.getView();
            snackBarView.setBackgroundColor(getResources().getColor(R.color.green));
            snackbar.show();
        }

        SharedPreferences prefsAtual = getSharedPreferences(ATUALIZACAO_DADOS, MODE_PRIVATE);
        versaoAtualClientes = prefsAtual.getString("clientes", null);
        versaoAtualProdutos = prefsAtual.getString("produtos", null);

        GettersSetters.setCodigoVendedor(GettersSetters.getIdUsuarioLogado());
        GettersSetters.setNomeVend(GettersSetters.getUsuarioLogado());

        SharedPreferences prefs = getSharedPreferences(Login.INFORMACOES_LOGIN_AUTOMATICO, MODE_PRIVATE);
        final String login = prefs.getString("login", null);
        if (login == null) {
            btnSair.setVisibility(View.GONE);
        }

        btnSair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor resColeta = db.buscaColetasNaoEnviadas("", "", false, ((GettersSetters.getTipoUsuario().equals("V") || GettersSetters.getTipoUsuario().equals("T") || GettersSetters.getTipoUsuario().equals("P")) ?
                        (GettersSetters.getCodigoVendedor().equals("") ? GettersSetters.getIdUsuarioLogado() : GettersSetters.getCodigoVendedor()) : ""));
                if (resColeta.getCount() > 0) {
                    contaPressVoltar = 0;
                    verificaColetasNaoEnviadas(true);
                } else {
                    sair();
                }
            }
        });

        btnDetalhesVersao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String detalhes = "";
                String versaoAtual = "";

                try {
                    PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                    assert pInfo != null;
                    versaoAtual = pInfo.versionName;

                    ResultSet rsVersaoAtual = conecta.selectVersaoApp(versaoAtual);
                    while (rsVersaoAtual.next()) {
                        detalhes = rsVersaoAtual.getString(4);
                    }
                } catch (SQLException | PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }

                if (!detalhes.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
                    builder.setTitle("Detalhes da Versão " + versaoAtual.trim());
                    builder.setCancelable(false);
                    builder.setMessage(detalhes.trim().replace(";", ".\n"));
                    builder.setPositiveButton("Fechar", null);
                    AlertDialog dialogNumCol = builder.create();
                    dialogNumCol.show();
                } else {
                    Snackbar.make(viewSnackBar, "Nenhum dado localizado!", Snackbar.LENGTH_LONG).show();
                }
            }
        });

        btnColeta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnColeta.setEnabled(false);

                arrIdUsuario.clear();
                arrUsuario.clear();
                arrCodTotvs.clear();
                arrNomeTotvs.clear();
                arrTipovend.clear();

                contaPressVoltar = 0;

                if (CheckConnection.isConnected(Home.this)) {
                    isAtualizaObrigatDados = false;
                    versaoAtualClientes = "";
                    versaoAtualProdutos = "";
                    versaoAtualBorracheiros = "";
                    versaoAtualCondPagto = "";
                    novaVersaoProdutos = "";
                    novaVersaoClientes = "";
                    novaVersaoBorracheiros = "";
                    novaVersaoCondPgto = "";
                    atualizaDadosObrigat = 0;

                    if (!isCancelaAtualizacaoDados) {
                        validaAtualizDados(true);
                    } else {
                        iniciarColeta();
                    }
                } else {
                    iniciarColeta();
                }

                btnColeta.setEnabled(true);
            }
        });

        /* ------- FUNÇÕES DE ADMINISTRAÇÃO ------- **/
        btnOpcoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
                LayoutInflater layoutInflater = getLayoutInflater();
                @SuppressLint("InflateParams") final View dialogView = layoutInflater.inflate(R.layout.view_configuracoes, null);

                final ImageView btnViewOptBD = dialogView.findViewById(R.id.btnViewOptBD);
                final LinearLayout llSqLite = dialogView.findViewById(R.id.llSqLite);
                final CardView cardViewComissBorr = dialogView.findViewById(R.id.cardViewComissBorr);
                final ImageView btnViewComissBorr = dialogView.findViewById(R.id.btnViewComissBorr);
                final LinearLayout llComissBorrach = dialogView.findViewById(R.id.llComissBorrach);
                final Button btnSincConfig = dialogView.findViewById(R.id.btnSincConfig);
                final Button btnAtualClientesCfg = dialogView.findViewById(R.id.btnAtualClientesCfg);
                final Button btnAtualProdutosCfg = dialogView.findViewById(R.id.btnAtualProdutosCfg);
                final Button btnAtualCondPgtoCfg = dialogView.findViewById(R.id.btnAtualCondPgtoCfg);
                final Button btnAtualBorracheirosCfg = dialogView.findViewById(R.id.btnAtualBorracheirosCfg);
                final CardView cardViewAtualizar = dialogView.findViewById(R.id.cardViewAtualizar);

                validaAtualizDados(false);

                builder.setCancelable(true);
                builder.setView(dialogView);

                llSqLite.setVisibility(View.GONE);
                cardViewComissBorr.setVisibility(View.GONE);

                if (!CheckConnection.isConnected(Home.this)) {
                    cardViewComissBorr.setVisibility(View.GONE);
                    btnAtualClientesCfg.setVisibility(View.GONE);
                    btnAtualProdutosCfg.setVisibility(View.GONE);
                    btnAtualCondPgtoCfg.setVisibility(View.GONE);
                    btnAtualBorracheirosCfg.setVisibility(View.GONE);
                    btnSincConfig.setVisibility(View.GONE);
                    cardViewAtualizar.setVisibility(View.GONE);
                } else {
                    if (GettersSetters.getTipoUsuario() != null) {
                        if (GettersSetters.getTipoUsuario().equals("A")) {
                            cardViewComissBorr.setVisibility(View.VISIBLE);
                        }
                    }
                }

                btnSincConfig.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        atualizaFiliaisVendedores = new AtualizaFiliaisVendedores();
                        atualizaFiliaisVendedores.execute();
                    }
                });

                btnAtualClientesCfg.setOnClickListener(onClickAtualizaClientes);

                btnAtualProdutosCfg.setOnClickListener(onClickAtualizaProdutos);

                if (GettersSetters.getTipoUsuario().trim().equals("A") ||
                        GettersSetters.getTipoUsuario().trim().equals("D") ||
                        GettersSetters.getCodigoVendedor().trim().equals("12")) {
                    btnAtualProdutosCfg.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
                            builder.setTitle("Atualização de Produtos");
                            builder.setCancelable(false);
                            builder.setMessage("Forçar atualização dos PRODUTOS e TABELAS?");
                            builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (conecta.updParametroAtualizacaoDados("PRODUTOS")) {
                                        Toast.makeText(Home.this, "Atualizado com sucesso!", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(Home.this, "Erro ao atualizar!", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                            builder.setNegativeButton("Não", null);
                            AlertDialog dialogNumCol = builder.create();
                            dialogNumCol.show();
                            return true;
                        }
                    });


                    btnAtualClientesCfg.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
                            builder.setTitle("Atualização de Clientes");
                            builder.setCancelable(false);
                            builder.setMessage("Forçar atualização dos CLIENTES?");
                            builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (conecta.updParametroAtualizacaoDados("CLIENTES")) {
                                        Toast.makeText(Home.this, "Atualizado com sucesso!", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(Home.this, "Erro ao atualizar!", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                            builder.setNegativeButton("Não", null);
                            AlertDialog dialogNumCol = builder.create();
                            dialogNumCol.show();
                            return true;
                        }
                    });

                    btnAtualBorracheirosCfg.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
                            builder.setTitle("Atualização de Borracheiros");
                            builder.setCancelable(false);
                            builder.setMessage("Forçar atualização dos BORRACHEIROS?");
                            builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (conecta.updParametroAtualizacaoDados("BORRACHEIROS")) {
                                        Toast.makeText(Home.this, "Atualizado com sucesso!", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(Home.this, "Erro ao atualizar!", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                            builder.setNegativeButton("Não", null);
                            AlertDialog dialogNumCol = builder.create();
                            dialogNumCol.show();
                            return true;
                        }
                    });

                    btnAtualCondPgtoCfg.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
                            builder.setTitle("Atualização de Condições de Pagamento");
                            builder.setCancelable(false);
                            builder.setMessage("Forçar atualização das CONDIÇÕES DE PAGAMENTO?");
                            builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (conecta.updParametroAtualizacaoDados("CONDIPAGTO")) {
                                        Toast.makeText(Home.this, "Atualizado com sucesso!", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(Home.this, "Erro ao atualizar!", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                            builder.setNegativeButton("Não", null);
                            AlertDialog dialogNumCol = builder.create();
                            dialogNumCol.show();
                            return true;
                        }
                    });
                }

                btnAtualCondPgtoCfg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        verificaCondPgto = new VerificaCondPgto();
                        verificaCondPgto.execute(true, false);
                    }
                });

                btnAtualBorracheirosCfg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        verificaBorracheiros = new VerificaBorracheiros();
                        verificaBorracheiros.execute(true, false);
                    }
                });

                /** OPERAÇÕES COM SQLITE **/
                btnViewOptBD.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        llSqLite.setVisibility(View.VISIBLE);
                        llComissBorrach.setVisibility(View.GONE);

                        final Button btnSQLiteApagar = dialogView.findViewById(R.id.btnSQLiteApagar);
                        final Button btnSQLiteBackup = dialogView.findViewById(R.id.btnSQLiteBackup);

                        btnSQLiteApagar.setVisibility(View.GONE);

                        if (CheckConnection.isConnected(Home.this)) {
                            btnSQLiteApagar.setVisibility(View.VISIBLE);
                        }

                        btnSQLiteApagar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
                                builder.setTitle("Excluir os dados do aplicativo?");
                                builder.setIcon(R.drawable.exclamation);
                                builder.setCancelable(true);
                                builder.setMessage("Todos os dados deste app serão excluídos permanentemente. Isso inclui todos os arquivos, configurações, contas, bancos de dados e outros.");
                                builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        final AlertDialog.Builder mensagem = new AlertDialog.Builder(Home.this);
                                        mensagem.setTitle("Senha");
                                        mensagem.setMessage("Digite sua senha para liberar a exclusão:");
                                        final EditText input = new EditText(Home.this);
                                        input.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                                        input.setTransformationMethod(PasswordTransformationMethod.getInstance());
                                        mensagem.setView(input);
                                        mensagem.setPositiveButton("Liberar", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                senhaLib = input.getText().toString();
                                                if (!senhaLib.equals("")) {
                                                    if (CheckConnection.isConnected(Home.this)) {
                                                        conecta.selectUsuarios(GettersSetters.getUsuarioLogado(), funcoesGenericas.cripto(senhaLib), "", "", GettersSetters.getIdUsuarioLogado());
                                                        //conecta.selectUsuarios(GettersSetters.getUsuarioLogado(), senhaLib, "", "", GettersSetters.getIdUsuarioLogado());
                                                        if (!conecta.nomeUsuarioBdExt.equals("")) {
                                                            try {
                                                                if (db.onDelete(getApplicationContext())) {
                                                                    AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
                                                                    builder.setTitle("Processo concluído!");
                                                                    builder.setIcon(R.drawable.success);
                                                                    builder.setCancelable(false);
                                                                    builder.setMessage("Dados excluídos. Aplicação será reiniciada!");
                                                                    builder.setPositiveButton("Reiniciar!", new DialogInterface.OnClickListener() {
                                                                        @SuppressLint("ApplySharedPref")
                                                                        @Override
                                                                        public void onClick(DialogInterface dialog, int which) {
                                                                            Home.this.getSharedPreferences(DADOS_LOGIN, MODE_PRIVATE).edit().clear().commit();
                                                                            Home.this.getSharedPreferences(DADOS_CONTROLE, MODE_PRIVATE).edit().clear().commit();

                                                                            funcoesGenericas.clearCache(getBaseContext());

                                                                            Intent mStartActivity = new Intent(Home.this, Login.class);
                                                                            int mPendingIntentId = 123456;
                                                                            PendingIntent mPendingIntent = PendingIntent.getActivity(Home.this, mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
                                                                            AlarmManager mgr = (AlarmManager) Home.this.getSystemService(Context.ALARM_SERVICE);
                                                                            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                                                                            System.exit(0);
                                                                        }
                                                                    });
                                                                    builder.show();
                                                                } else {
                                                                    AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
                                                                    builder.setTitle("Erro ao deletar arquivo do BD!!");
                                                                    builder.setIcon(R.drawable.errordatabase);
                                                                    builder.setCancelable(true);
                                                                    builder.setMessage("Banco de dado não localizado!\nPara limpar os dados desinstale e reinstale o aplicativo!");
                                                                    AlertDialog dialogPswd = builder.create();
                                                                    dialogPswd.show();
                                                                }
                                                            } catch (Exception errDel) {
                                                                AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
                                                                builder.setTitle("Erro ao deletar arquivo do BD!!");
                                                                builder.setIcon(R.drawable.errordatabase);
                                                                builder.setCancelable(true);
                                                                builder.setMessage(errDel.toString());
                                                                AlertDialog dialogPswd = builder.create();
                                                                dialogPswd.show();
                                                            }
                                                            dialog.dismiss();

                                                        } else {
                                                            AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
                                                            builder.setTitle("Senha inválida!!");
                                                            builder.setIcon(R.drawable.password);
                                                            builder.setCancelable(true);
                                                            builder.setMessage("Tente novamente!");
                                                            AlertDialog dialogPswd = builder.create();
                                                            dialogPswd.show();
                                                        }
                                                    } else {
                                                        Toast.makeText(getApplication(), "Erro de conexão!", Toast.LENGTH_SHORT).show();
                                                    }
                                                } else {
                                                    AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
                                                    builder.setTitle("Senha em branco!");
                                                    builder.setIcon(R.drawable.password);
                                                    builder.setCancelable(true);
                                                    builder.setMessage("Preencher a senha!");
                                                    AlertDialog dialogPswd = builder.create();
                                                    dialogPswd.show();
                                                }
                                            }
                                        });
                                        mensagem.show();
                                    }
                                });
                                builder.setNegativeButton("Cancelar", null);
                                builder.show();
                            }
                        });

                        btnSQLiteBackup.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (ContextCompat.checkSelfPermission(Home.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(Home.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
                                } else {
                                    pastaBackup = new File(Environment.getExternalStorageDirectory() + "/BackupsDB");
                                    boolean pastaBackupOK = true;
                                    if (pastaBackup.exists()) {
                                        FuncoesGenericas.deletaArquivos(pastaBackup);
                                    } else {
                                        pastaBackupOK = pastaBackup.mkdirs();
                                    }

                                    if (pastaBackupOK) {
                                        pastaArquivos = new File("data/data/pneus.vachileski_coleta/databases/");

                                        llSqLite.setVisibility(View.GONE);

                                        if (ContextCompat.checkSelfPermission(Home.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                            ActivityCompat.requestPermissions(Home.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                                    Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
                                        } else {
                                            geraBackup = new GeraBackup();
                                            geraBackup.execute();
                                        }
                                    }
                                }
                            }
                        });
                    }
                });

                /** COMISSÃO DE BORRACHEIRO **/
                btnViewComissBorr.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        llSqLite.setVisibility(View.GONE);
                        llComissBorrach.setVisibility(View.VISIBLE);
                        llComissBorrach.removeAllViews();

                        conecta.selectComissaoBorracheiros();

                        for (int i = 0; i < conecta.arrIdComissBorracheiro.size(); i++) {
                            LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            assert layoutInflater != null;
                            @SuppressLint("InflateParams") final View addViewComiss = layoutInflater.inflate(R.layout.view_busca_comissao_borracheiro, null);
                            final Button btnEditFxComiss = addViewComiss.findViewById(R.id.btnEditFxComiss);
                            final Button btnSaveFxComiss = addViewComiss.findViewById(R.id.btnSaveFxComiss);
                            final TextView outIdComiss = addViewComiss.findViewById(R.id.txtIDComiss);
                            final EditText outFxIni = addViewComiss.findViewById(R.id.txtFxIni);
                            final EditText outFxFim = addViewComiss.findViewById(R.id.txtFxFim);
                            final EditText outPComiss = addViewComiss.findViewById(R.id.txtPercentualComiss);

                            outFxIni.setEnabled(false);
                            outFxFim.setEnabled(false);
                            outPComiss.setEnabled(false);

                            btnSaveFxComiss.setVisibility(View.GONE);

                            outIdComiss.setText(conecta.arrIdComissBorracheiro.get(i));
                            outFxIni.setText(conecta.arrFxIniComissBorracheiro.get(i));
                            outFxFim.setText(conecta.arrFxFimComissBorracheiro.get(i));
                            outPComiss.setText(conecta.arrPercComissBorracheiro.get(i));

                            llComissBorrach.addView(addViewComiss, llComissBorrach.getChildCount());

                            btnEditFxComiss.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    outFxIni.setEnabled(true);
                                    outFxFim.setEnabled(true);
                                    outPComiss.setEnabled(true);

                                    btnEditFxComiss.setVisibility(View.GONE);
                                    btnSaveFxComiss.setVisibility(View.VISIBLE);

                                    btnSaveFxComiss.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            boolean updOk = conecta.updateFaixasComissaoBorracheiros(outIdComiss.getText().toString(), outFxIni.getText().toString(), outFxFim.getText().toString(), outPComiss.getText().toString());

                                            btnSaveFxComiss.setVisibility(View.GONE);
                                            btnEditFxComiss.setVisibility(View.VISIBLE);

                                            outFxIni.setEnabled(false);
                                            outFxFim.setEnabled(false);
                                            outPComiss.setEnabled(false);

                                            AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
                                            builder.setTitle("Atualização da Faixa de Comissão");
                                            if (updOk) {
                                                builder.setIcon(R.drawable.success);
                                                builder.setCancelable(true);
                                                builder.setMessage("Comissão ID > " + outIdComiss.getText().toString() + " atualizada com sucesso!");
                                                AlertDialog dialog = builder.create();
                                                dialog.show();
                                            } else {
                                                builder.setIcon(R.drawable.error);
                                                builder.setCancelable(true);
                                                builder.setMessage("Erro ao atualizar comissão ID > " + outIdComiss.getText().toString() + conecta.erroExcecao);
                                                AlertDialog dialog = builder.create();
                                                dialog.show();
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    }
                });

                cardViewAtualizar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogAtualizacao();
                    }
                });

                dialogAdmin = builder.create();
                if (dialogAdmin != null) {
                    dialogAdmin.show();
                }
            }
        });

        btnBuscarColetas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnBuscarColetas.setEnabled(false);

                //INSERE CLIENTES QUANDO BASE ZERADA, POIS PRECISA PARA GERAÇÃO DOS PDF (BUSCA APENAS O TESTE TI PARA VALIDAR SE ENCONTRA ALGO...)
                if (db.buscaClientesColeta("", "TESTE TI", "", new ArrayList<>(0), "", "").getCount() == 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
                    builder.setTitle("Clientes não localizados!");
                    builder.setIcon(R.drawable.atencao);
                    builder.setCancelable(true);
                    builder.setMessage("Alguns dados podem gerar em branco!\nAtualize os Clientes (Opções, Atualizar Clientes) antes de continuar!");
                    builder.setPositiveButton("Fechar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            btnBuscarColetas.setEnabled(true);
                        }
                    });
                    AlertDialog dialogPswd = builder.create();
                    dialogPswd.show();
                } else {
                    Intent it = new Intent(Home.this, ColetaBusca.class);
                    startActivity(it);
                    finish();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        contaPressVoltar++;
        if (contaPressVoltar == 1) {
            Cursor resColeta = db.buscaColetasNaoEnviadas("", "", false, ((GettersSetters.getTipoUsuario().equals("V") || GettersSetters.getTipoUsuario().equals("T") || GettersSetters.getTipoUsuario().equals("P")) ?
                    (GettersSetters.getCodigoVendedor().equals("") ? GettersSetters.getIdUsuarioLogado() : GettersSetters.getCodigoVendedor()) : ""));
            if (resColeta.getCount() > 0) {
                contaPressVoltar = 0;
                verificaColetasNaoEnviadas(false);
            } else {
                Toast.makeText(Home.this, "Pressione Voltar novamente para sair!", Toast.LENGTH_SHORT).show();
            }
        } else {
            encerrar();
        }

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                contaPressVoltar = 0;
            }
        }, 1500);
    }

    public void encerrar() {
        funcoesGenericas.clearCache(getBaseContext());
        Thread.currentThread().interrupt();
        GettersSetters.resetGettersSetters();
        finish();
        finishAffinity();
        System.exit(0);
        android.os.Process.killProcess(android.os.Process.myPid());
        super.onDestroy();
    }

    public void sair() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
        builder.setCancelable(false);
        builder.setMessage("Ao sair, no próximo acesso, será necessário informar usuário e senha novamente." +
                "\n\n*** Dica: para minimizar a aplicação, pressione o botão voltar duas vezes na tela Principal.\n\nContinua?");
        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @SuppressLint("ApplySharedPref")
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Home.this.getSharedPreferences(DADOS_LOGIN, MODE_PRIVATE).edit().clear().commit();
                Home.this.getSharedPreferences(DADOS_CONTROLE, MODE_PRIVATE).edit().clear().commit();

                encerrar();

                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int opc) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    public void verificaColetasNaoEnviadas(boolean isSair) {
        String coletasNaoEnviadas = "";
        try {
            Cursor resColeta = db.buscaColetasNaoEnviadas("", "", false, ((GettersSetters.getTipoUsuario().equals("V") || GettersSetters.getTipoUsuario().equals("T") || GettersSetters.getTipoUsuario().equals("P")) ?
                    (GettersSetters.getCodigoVendedor().equals("") ? GettersSetters.getIdUsuarioLogado() : GettersSetters.getCodigoVendedor()) : ""));
            if (resColeta.getCount() > 0) {
                if (resColeta.moveToFirst()) {
                    do {
                        coletasNaoEnviadas += "Nº: " + resColeta.getString(1) + " - " +
                                GettersSetters.converteData(resColeta.getString(2).substring(0, 4) + "-" + resColeta.getString(2).substring(4, 6) + "-" + resColeta.getString(2).substring(6, 8), "EN") + " - " +
                                resColeta.getString(5).trim() + "\n";
                    } while (resColeta.moveToNext());
                }

                AlertDialog.Builder builderFilial = new AlertDialog.Builder(Home.this);
                builderFilial.setTitle("Há [" + resColeta.getCount() + (resColeta.getCount() > 1 ? "] coletas NÃO ENVIADAS" : "] coleta NÃO ENVIADA") + " para o Sistema.");
                builderFilial.setIcon(R.drawable.atencao);
                builderFilial.setCancelable(false);
                builderFilial.setMessage(Arrays.toString(coletasNaoEnviadas.split("\n")).replace(",", "\n").replace("[", "").replace("]", "").replace(" Nº", "Nº")); // + (CheckConnection.isConnected(Home.this) ? "\nEnviar agora?" : "\nEnvio disponível com conexão de rede!"));
                if (CheckConnection.isConnected(Home.this)) {
                    builderFilial.setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            isColetaEnvio = true;
                            Intent i = new Intent(Home.this, ColetaBusca.class);
                            startActivity(i);
                        }
                    });
                } else {
                    builderFilial.setPositiveButton("Voltar", null);
                }
                builderFilial.setNegativeButton("Sair", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (isSair) {
                            sair();
                        } else {
                            encerrar();
                        }
                    }
                });
                builderFilial.setNeutralButton("Adiar envio", null);
                AlertDialog dialog = builderFilial.create();

                dialog.show();
            }
        } catch (Exception erro) {
            erro.printStackTrace();
            AlertDialog.Builder builderFilial = new AlertDialog.Builder(Home.this);
            builderFilial.setTitle("Erro ao buscar coletas para envio!\nRealize a busca manual.");
            builderFilial.setIcon(R.drawable.error);
            builderFilial.setCancelable(false);
            builderFilial.setMessage(erro.getMessage());
            builderFilial.setNegativeButton("Fechar", null);
            AlertDialog dialog = builderFilial.create();
            dialog.show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permissão de armazenamento concedida!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "O aplicativo não tem permissão para gravar em seu armazenamento. " +
                        "Portanto, ele não pode funcionar corretamente. Considere conceder essa permissão!", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Função para quando virar a tela não resetar o layout
     **/
    @Override
    public void onConfigurationChanged(@NotNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @SuppressLint("StaticFieldLeak")
    public class GeraBackup extends AsyncTask<Boolean, String, File> {
        long tamanho = 0;
        long total = 0;
        int comprimento;
        byte[] buffer = new byte[1024];

        @Override
        protected void onPreExecute() {
            mProgressDialog = new ProgressDialog(Home.this, 0);
            mProgressDialog.setIcon(R.drawable.ampulheta);
            mProgressDialog.setTitle("Backup");
            mProgressDialog.setMessage("Gerando backup...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMax(100);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }

        @SuppressLint("ApplySharedPref")
        @Override
        protected File doInBackground(Boolean... params) {
            String[] files = pastaArquivos.list();

            try {
                if (files != null && files.length > 0) {
                    for (String filename : files) {
                        inputStream = new FileInputStream(new File(pastaArquivos + "/" + filename));
                        outFileBackup = new File(pastaBackup + "/", filename);

                        tamanho = inputStream.available();

                        if (outFileBackup.exists()) {
                            outFileBackup.delete();
                        }
                        outputStream = new FileOutputStream(outFileBackup);

                        while ((comprimento = inputStream.read(buffer)) != -1) {
                            total += comprimento;
//                            publishProgress("" + (int) ((total * 100) / tamanho));
                            mProgressDialog.setProgress((int) ((total * 100) / tamanho));
                            outputStream.write(buffer, 0, comprimento);
                        }

                        inputStream.close();
                        outputStream.flush();
                        outputStream.close();
                    }
                    //zip
                    File fileZip = new File(pastaBackup + "/" + GettersSetters.getUsuarioLogado().trim() + ".zip");
                    if (fileZip.exists()) {
                        fileZip.delete();
                    }
                    ZipParameters zipParameters = new ZipParameters();
                    zipParameters.setEncryptFiles(true);
                    zipParameters.setCompressionLevel(CompressionLevel.ULTRA);
                    zipParameters.setEncryptionMethod(EncryptionMethod.AES);
                    zipParameters.setAesKeyStrength(AesKeyStrength.KEY_STRENGTH_256);
                    List<File> filesToAdd = Collections.singletonList(
                            new File((pastaBackup + "/" + ConexaoBDInt.NOME_BANCO))
                    );
                    String senha = GettersSetters.getIdUsuarioLogado().trim() + GettersSetters.getUsuarioLogado().trim();
                    ZipFile zipFile = new ZipFile(fileZip, senha.toCharArray());
                    zipFile.setRunInThread(true);
                    ProgressMonitor progressMonitor = zipFile.getProgressMonitor();
                    zipFile.addFiles(filesToAdd, zipParameters);
                    mProgressDialog.setMessage("Compactando...");
                    while (progressMonitor.getState() == ProgressMonitor.State.BUSY) {
//                        publishProgress("" + progressMonitor.getPercentDone());
                        mProgressDialog.setProgress(progressMonitor.getPercentDone());
                    }
                } else {
                    new Thread() {
                        public void run() {
                            Home.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
                                    builder.setTitle("Atenção!!");
                                    builder.setIcon(R.drawable.atencao);
                                    builder.setCancelable(true);
                                    builder.setMessage("Nenhum arquivo encontrado para Backup!\nTente novamente!");
                                    builder.setPositiveButton("Fechar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            if (outFileBackup != null) {
                                                outFileBackup.delete();
                                            }
                                            Thread.currentThread().interrupt();
                                        }
                                    });
                                    AlertDialog dialog = builder.create();
                                    dialog.show();
                                }
                            });
                        }
                    }.start();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return outFileBackup;
        }

        /**
         * Updating progress bar
         */
        protected void onProgressUpdate(String... progress) {
            mProgressDialog.setProgress(Integer.parseInt(progress[0]));
        }

        @Override
        protected void onPostExecute(File fileBackup) {
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
            if (fileBackup != null) {
                builder.setTitle("Sucesso!");
                builder.setIcon(R.drawable.success);
                builder.setCancelable(true);
                builder.setMessage("Backup realizado com Sucesso!");
                builder.setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final AlertDialog.Builder builderWhats = new AlertDialog.Builder(Home.this);
                        builderWhats.setIcon(R.drawable.whatsapp);
                        builderWhats.setTitle("Selecione o Administrador");
                        builderWhats.setCancelable(false);

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(Home.this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.contato_admins));
                        builderWhats.setAdapter(adapter, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int itemSelecionado) {
                                dadoSelecionado = Objects.requireNonNull(adapter.getItem(itemSelecionado)).split("-");
                                numeroTelefone = dadoSelecionado[1];
                            }
                        });
                        builderWhats.setNegativeButton("Fechar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                FuncoesGenericas.deletaArquivos(pastaBackup);
                            }
                        });
                        dialogCelular = builderWhats.create();
                        dialogCelular.show();

                        dialogCelular.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialogInterface) {
                                if (outFileBackup != null) {
                                    outFileBackup = new File(pastaBackup + "/", GettersSetters.getUsuarioLogado().trim() + ".zip");

                                    String numCel = "+55" + numeroTelefone;
                                    Intent sendIntent = new Intent();
                                    Uri uri;
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                        uri = FileProvider.getUriForFile(Home.this, BuildConfig.APPLICATION_ID + ".provider", outFileBackup);
                                    } else {
                                        uri = Uri.fromFile(outFileBackup);
                                    }
                                    sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
                                    sendIntent.putExtra("jid", numCel.replace("+", "").replace(" ", "") + "@s.whatsapp.net");
                                    sendIntent.setAction(Intent.ACTION_SEND);
                                    if (getPackageManager().getLaunchIntentForPackage("com.whatsapp") != null) {
                                        sendIntent.setPackage("com.whatsapp");
                                    } else {
                                        sendIntent.setPackage("com.whatsapp.w4b"); //WPP BUSINESS
                                    }
                                    sendIntent.setType("*/*");
                                    try {
                                        Home.this.startActivity(sendIntent);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        AlertDialog.Builder builderFilial = new AlertDialog.Builder(Home.this);
                                        builderFilial.setTitle("Whatsapp");
                                        builderFilial.setIcon(R.drawable.error);
                                        builderFilial.setCancelable(false);
                                        builderFilial.setMessage("Aplicativo Whatsapp não instalado!");
                                        builderFilial.setPositiveButton("Fechar", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                FuncoesGenericas.deletaArquivos(pastaBackup);
                                            }
                                        });
                                        AlertDialog dialog = builderFilial.create();
                                        dialog.show();
                                    }
                                } else {
                                    AlertDialog.Builder builderFilial = new AlertDialog.Builder(Home.this);
                                    builderFilial.setTitle("Backup");
                                    builderFilial.setIcon(R.drawable.error);
                                    builderFilial.setCancelable(false);
                                    builderFilial.setMessage("Arquivo de backup não localizado ou não gerado!");
                                    builderFilial.setPositiveButton("Fechar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            FuncoesGenericas.deletaArquivos(pastaBackup);
                                        }
                                    });
                                    AlertDialog dialog = builderFilial.create();
                                    dialog.show();
                                }
                            }
                        });
                    }
                });
                builder.setNegativeButton("Fechar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FuncoesGenericas.deletaArquivos(pastaBackup);
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            } else {
                builder.setTitle("Erro ao fazer backup!");
                builder.setIcon(R.drawable.errordatabase);
                builder.setCancelable(true);
                builder.setMessage("Tente novamente!");
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class VerificaBorracheiros extends AsyncTask<Boolean, String, Boolean> {

        @Override
        protected void onPreExecute() {
            mProgressDialog = new ProgressDialog(Home.this, 0);
            mProgressDialog.setIcon(R.drawable.ampulheta);
            mProgressDialog.setTitle("Atualização de Borracheiros");
            mProgressDialog.setMessage("Atualizando Borracheiros...\nAguarde...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMax(100);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }

        @SuppressLint("ApplySharedPref")
        @Override
        protected Boolean doInBackground(Boolean... params) {
            try {
                Cursor buscaBorracheiros = db.selectBorracheiro("BORRACHARIA", "", "");
                if (buscaBorracheiros.getCount() == 0 || params[0]) {
                    SharedPreferences.Editor editor = getSharedPreferences(ATUALIZACAO_DADOS, MODE_PRIVATE).edit();
                    editor.putString("borracheiros", novaVersaoBorracheiros);
                    editor.commit();

                    conecta.updDadosAuditApp(GettersSetters.getIdUsuarioLogado(), "DT_ATT_BORRACH_COL", GettersSetters.getDataEN() + " - " + GettersSetters.getHora());
                    conecta.selectBorracheiro(Home.this, null, null, true);
                }

                /** UPDATE DE PARÂMETROS **/
                conecta.updateParametros(Home.this);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return params[1];
        }

        @Override
        protected void onPostExecute(Boolean continua) {
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
            }

            if (continua) {
                verificaCondPgto = new VerificaCondPgto();
                verificaCondPgto.execute(false, true);
            }

            Log.i("AsyncTask", "Tirando dialog da tela Thread: " + Thread.currentThread().getName());

        }
    }

    @SuppressLint("StaticFieldLeak")
    public class VerificaCondPgto extends AsyncTask<Boolean, Integer, Boolean> {

        @Override
        protected void onPreExecute() {
            mProgressDialog = new ProgressDialog(Home.this);
            mProgressDialog.setIcon(R.drawable.ampulheta);
            mProgressDialog.setTitle("Condições de Pagamento");
            mProgressDialog.setMessage("Atualizando Condições de Pagamento... Aguarde...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMax(100);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }

        @SuppressLint({"WrongThread", "ApplySharedPref"})
        @Override
        protected Boolean doInBackground(Boolean... params) {
            Cursor condPgto = db.buscaCondPagLocal();
            if (condPgto.getCount() == 0 || params[0]) {
                SharedPreferences.Editor editor = getSharedPreferences(ATUALIZACAO_DADOS, MODE_PRIVATE).edit();
                editor.putString("condPagamento", novaVersaoCondPgto);
                editor.commit();

                conecta.updDadosAuditApp(GettersSetters.getIdUsuarioLogado(), "DT_ATT_CONDPG_COL", GettersSetters.getDataEN() + " - " + GettersSetters.getHora());
                conecta.selectCondPagto("0", "", true, Home.this);
            }

            return params[1];
        }

        @SuppressLint("ApplySharedPref")
        @Override
        protected void onPostExecute(Boolean continua) {
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
            }

            if (continua) {
                /** INSERÇÃO DE PRODUTOS **/
                Cursor buscaProdutos = db.buscaProduto("REFORMA", false, "");
                if (buscaProdutos.getCount() == 0) {
                    mProgressDialog = new ProgressDialog(Home.this, 0);
                    mProgressDialog.setIcon(R.drawable.ampulheta);
                    mProgressDialog.setTitle("Produtos e Tabelas");
                    mProgressDialog.setMessage("[1] Atualizando Produtos...\n[2] Atualizando tabelas de preços...\nAguarde...");
                    mProgressDialog.setIndeterminate(false);
                    mProgressDialog.setMax(100);
                    mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    mProgressDialog.setCancelable(false);

                    verificacaoProdutos = new VerificacaoProdutos();
                    verificacaoProdutos.execute(true, true);
                } else if (buscaProdutos.getCount() == 0) { // != conecta.contProdutos && conecta.contProdutos > 0) {
                    mProgressDialog = null;
                    GettersSetters.setAtualizarProdutos(true);
                    verificacaoProdutos = new VerificacaoProdutos();
                    verificacaoProdutos.onPostExecute(true);
                } else {
                    verificacaoProdutos = new VerificacaoProdutos();
                    verificacaoProdutos.onPostExecute(true);
                }
            }

            Log.i("AsyncTask", "Tirando dialog da tela Thread Cliente: " + Thread.currentThread().getName());
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class VerificacaoProdutos extends AsyncTask<Boolean, Integer, Boolean> {
        private AlertDialog dialogProcessoInicial = null;

        @Override
        protected void onPreExecute() {
            if (mProgressDialog != null) {
                mProgressDialog.show();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
                LayoutInflater layoutInflater = getLayoutInflater();
                final View dialogView = layoutInflater.inflate(R.layout.dialog_carregamento, null);
                final TextView textView = dialogView.findViewById(R.id.txtInfoCarreg);
                textView.setText("Validando Produtos...\nAguarde...");
                builder.setPositiveButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        verificacaoProdutos.cancel(true);
                        Toast.makeText(Home.this, "Atualização Cancelada!", Toast.LENGTH_LONG).show();
                        onBackPressed();
                    }
                });
                builder.setCancelable(false);
                builder.setView(dialogView);
                dialogProcessoInicial = builder.create();
                dialogProcessoInicial.show();
            }
        }

        @SuppressLint({"WrongThread", "ApplySharedPref"})
        @Override
        protected Boolean doInBackground(Boolean... params) {
            /* ATUALIZA PRODUTOS LOCALMENTE - PARA UTILIZAR DURANTE AS COLETAS ONLINE E OFFLINE **/
            if (params[0]) {
                if (conecta.selecionaEInsereProdutos(Home.this, 0)) {
                    SharedPreferences.Editor editor = getSharedPreferences(ATUALIZACAO_DADOS, MODE_PRIVATE).edit();
                    editor.putString("produtos", novaVersaoProdutos);
                    editor.commit();

                    conecta.updDadosAuditApp(GettersSetters.getIdUsuarioLogado(), "DT_ATT_PROD_COL", GettersSetters.getDataEN() + " - " + GettersSetters.getHora());
                    GettersSetters.setAtualizarProdutos(false);
                }
            }
            return params[1];
        }

        @SuppressLint("ApplySharedPref")
        @Override
        protected void onPostExecute(Boolean continua) {
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
            }

            if (continua == null) {
                continua = true;
            }

            if (continua) {
                Log.i("AsyncTask", "Tirando dialog da tela Thread Produtos: " + Thread.currentThread().getName());

                Cursor buscaClientes = db.buscaClientesColeta("", "TESTE TI", "", new ArrayList<>(0), "", "");

                if (dialogProcessoInicial != null) {
                    dialogProcessoInicial.dismiss();
                }

                mProgressDialog = null;

                if (buscaClientes.getCount() == 0) {
                    if (CheckConnection.isConnected(Home.this)) {
                        mProgressDialog = new ProgressDialog(Home.this);
                        mProgressDialog.setIcon(R.drawable.ampulheta);
                        mProgressDialog.setTitle("Clientes");
                        mProgressDialog.setMessage("Atualizando Clientes... Aguarde...");
                        mProgressDialog.setIndeterminate(false);
                        mProgressDialog.setMax(100);
                        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                        mProgressDialog.setCancelable(false);

                        verificaClientes = new VerificaClientes();
                        verificaClientes.execute(true, true);
                    }
                } else {
                    GettersSetters.resetGettersSetters();
                    GettersSetters.setAtualizarClientes(false);
                    Intent it = new Intent(Home.this, ColetaCliente.class);
                    startActivity(it);
                    finish();
                }
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class VerificaClientes extends AsyncTask<Boolean, Integer, Boolean> {
        private AlertDialog dialogProcessoInicial = null;

        @Override
        protected void onPreExecute() {
            if (mProgressDialog != null) {
                mProgressDialog.show();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
                LayoutInflater layoutInflater = getLayoutInflater();
                final View dialogView = layoutInflater.inflate(R.layout.dialog_carregamento, null);
                final TextView textView = dialogView.findViewById(R.id.txtInfoCarreg);
                textView.setText("Validando Clientes...\nAguarde...");
                builder.setPositiveButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        verificaClientes.cancel(true);
                        Toast.makeText(Home.this, "Atualização Cancelada", Toast.LENGTH_LONG).show();
                        onBackPressed();
                    }
                });
                builder.setCancelable(false);
                builder.setView(dialogView);
                dialogProcessoInicial = builder.create();
                dialogProcessoInicial.show();
            }
        }

        @SuppressLint({"WrongThread", "ApplySharedPref"})
        @Override
        protected Boolean doInBackground(Boolean... params) {
            /** ATUALIZAÇÃO DE CLIENTES **/
            if (params[0]) {
                if (conecta.selectionaEInsereClientesLocal(Home.this, 0)) {
                    GettersSetters.setAtualizarClientes(false);

                    SharedPreferences.Editor editor = getSharedPreferences(ATUALIZACAO_DADOS, MODE_PRIVATE).edit();
                    editor.putString("clientes", novaVersaoClientes);
                    editor.commit();

                    conecta.updDadosAuditApp(GettersSetters.getIdUsuarioLogado(), "DT_ATT_CLIENTES_COL", GettersSetters.getDataEN() + " - " + GettersSetters.getHora());
                } else {
                    new Thread() {
                        public void run() {
                            Home.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    AlertDialog.Builder builderFilial = new AlertDialog.Builder(Home.this);
                                    builderFilial.setTitle("Erro ao atualizar os Clientes!");
                                    builderFilial.setIcon(R.drawable.error);
                                    builderFilial.setCancelable(false);
                                    builderFilial.setMessage(GettersSetters.getErroEnvioColetaBDExt());
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
                }
            }
            return params[1];
        }

        @SuppressLint("ApplySharedPref")
        @Override
        protected void onPostExecute(Boolean continua) {
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
            }

            if (continua == null) {
                continua = true;
            }

            if (dialogProcessoInicial != null) {
                dialogProcessoInicial.dismiss();
            }

            mProgressDialog = null;

            if (continua) {
                Intent it = new Intent(Home.this, ColetaCliente.class);
                startActivity(it);
                finish();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class AtualizaFiliaisVendedores extends AsyncTask<Boolean, String, Boolean> {
        AlertDialog alertDialogFiliais = null;

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPreExecute() {
            AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
            LayoutInflater layoutInflater = getLayoutInflater();
            final View dialogView = layoutInflater.inflate(R.layout.dialog_carregamento, null);
            final TextView textView = dialogView.findViewById(R.id.txtInfoCarreg);
            textView.setText("Sincronizando...\nAguarde...");
            builder.setCancelable(false);
            builder.setPositiveButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    atualizaFiliaisVendedores.cancel(true);
                    Toast.makeText(Home.this, "Sincronização Cancelada", Toast.LENGTH_LONG).show();
                }
            });
            builder.setView(dialogView);
            alertDialogFiliais = builder.create();
            alertDialogFiliais.show();
        }

        @SuppressLint({"ApplySharedPref", "DefaultLocale", "WrongThread"})
        @Override
        protected Boolean doInBackground(Boolean... params) {
            try {
                db.delFiliais();
                db.delFiliaisUsuarios();

                conecta.selectFiliais();

                for (int i = 0; i < conecta.arrIdFilial.size(); i++) { //preenche com dados da filial
                    db.insereFilial(conecta.arrIdFilial.get(i), conecta.arrFilial.get(i), conecta.arrCodigoAuxFilial.get(i), conecta.arrRazaoSocialFilial.get(i), conecta.arrNomeFantasiaFilial.get(i));
                }

                conecta.selectFiliaisUsuarios("");
                /** Inserção de Filiais X Usuários no banco local **/
                for (int i = 0; i < conecta.arrRazaoSocialFiliaisUsuarios.size(); i++) {
                    db.insereFiliaisUsuarios(conecta.arrIdEmpFiliaisUsuarios.get(i), conecta.arrIdFiliaisUsuarios.get(i), conecta.arrCodigoAuxiliarFiliaisUsuarios.get(i), conecta.arrIdUsrFiliaisUsuarios.get(i), conecta.arrRazaoSocialFiliaisUsuarios.get(i));
                }

                /** ATUALIZA USUÁRIOS X VENDEDORES **/
                conecta.insereUsuariosVendedores(Home.this);

                return true;
            } catch (Exception exp) {
                exp.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean sucesso) {
            if (alertDialogFiliais != null) {
                alertDialogFiliais.dismiss();
            }

            if (!sucesso) {
                AlertDialog.Builder builderFilial = new AlertDialog.Builder(Home.this);
                builderFilial.setTitle("Erro Att Vendedores/Filiais!");
                builderFilial.setIcon(R.drawable.error);
                builderFilial.setCancelable(false);
                builderFilial.setMessage("Entre em contato com o Administrador!\n" + GettersSetters.getErroEnvioColetaBDExt());
                builderFilial.setNegativeButton("Fechar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Thread.currentThread().interrupt();
                    }
                });
                AlertDialog dialog = builderFilial.create();
                dialog.show();
            } else {
                conecta.updDadosAuditApp(GettersSetters.getIdUsuarioLogado(), "DT_ATT_CFG_COL", GettersSetters.getDataEN() + " - " + GettersSetters.getHora());
            }
        }

    }

//    /**
//     * VALIDA QUANDO A HOME ESTÁ SENDO EXIBIDA
//     **/
//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        super.onWindowFocusChanged(hasFocus);
//        isFocused = hasFocus;
//    }

    /*
     * FUNÇÃO PARA SELECAO DO VENDEDOR QUANDO FOR PÁTIO, GERENTE OU ADMIN
     **/
    @SuppressLint("UseCompatLoadingForDrawables")
    public void trocarVendedor() {
        arrIdUsuario.clear();
        arrUsuario.clear();
        arrCodTotvs.clear();
        arrNomeTotvs.clear();
        arrTipovend.clear();
        arrCliPermitidos.clear();
        arrCodBorrachPat.clear();

        GettersSetters.arrClientesPermitidos.clear();
        GettersSetters.setCodBorrachPatio("");
        GettersSetters.setIsBorracheiroVendPat(false);

        Cursor res = db.selectUsuariosVendedores(GettersSetters.getIdUsuarioLogado(), GettersSetters.getTipoUsuario(), false, false);
        if (CheckConnection.isConnected(Home.this) && res.getColumnCount() == 0) {
            conecta.selectUsuariosVendedores(GettersSetters.getIdUsuarioLogado(), GettersSetters.getTipoUsuario());
            arrIdUsuario.addAll(conecta.arrIdUsuarioVendedor);
            arrUsuario.addAll(conecta.arrUsuarioVendedor);
            arrCodTotvs.addAll(conecta.arrCodTotvsUsuarioVendedor);
            arrNomeTotvs.addAll(conecta.arrNomeTotvsUsuarioVendedor);
            arrTipovend.addAll(conecta.arrTipoVendUsuarioVendedor);
            arrCliPermitidos.addAll(conecta.arrClientesPermitidosUsuarioVendedor);
            arrCodBorrachPat.addAll(conecta.arrCodBorrachPatioUsuarioVendedor);
        } else {
            arrIdUsuario.addAll(db.arrIdUsuario);
            arrUsuario.addAll(db.arrUsuario);
            arrCodTotvs.addAll(db.arrCodTotvs);
            arrNomeTotvs.addAll(db.arrNomeTotvs);
            arrTipovend.addAll(db.arrTipovend);
            arrCliPermitidos.addAll(db.arrCliPermitidos);
            arrCodBorrachPat.addAll(db.arrCodBorrachPat);
        }

        String[] usuariosLocalizados = new String[arrUsuario.size()];
        String[] idUsuarioLocalizado = new String[arrIdUsuario.size()];
        String[] cliPermitidos = new String[arrCliPermitidos.size()];
        String[] codigoLocalizadoTotvs = new String[arrCodTotvs.size()];
        String[] codigoBorrachPat = new String[arrCodBorrachPat.size()];

        for (int i = 0; i < arrIdUsuario.size(); i++) {
            usuariosLocalizados[i] = arrUsuario.get(i);
            idUsuarioLocalizado[i] = arrIdUsuario.get(i);
            codigoLocalizadoTotvs[i] = arrCodTotvs.get(i) + "=" + arrNomeTotvs.get(i) + "=" + arrTipovend.get(i);
            cliPermitidos[i] = arrCliPermitidos.get(i);
            codigoBorrachPat[i] = arrCodBorrachPat.get(i);
        }

        AlertDialog.Builder builderUsuarios = new AlertDialog.Builder(Home.this);
        builderUsuarios.setCancelable(true);
        builderUsuarios.setTitle("Selecione para qual Vendedor será contabilizada esta Coleta:");

        ArrayAdapter<String> adapterSpnUsuarios = new ArrayAdapter<String>(Home.this, R.layout.text_view_item_high, usuariosLocalizados) {
            @Override
            public View getView(int position, View convertView, @NotNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                if (((TextView) view).getText().toString().contains("** ")) {
                    ((TextView) view).setTextColor(Color.parseColor("#FF0000"));
                } else {
                    ((TextView) view).setTextColor(Color.parseColor("#000000"));
                }
                ((TextView) view).setTextSize(15);
                return view;
            }
        };

        builderUsuarios.setAdapter(adapterSpnUsuarios, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int itemSelecionado) {
                String idVendSel = idUsuarioLocalizado[itemSelecionado];
                String usuarioSel = usuariosLocalizados[itemSelecionado];

                if (!idVendSel.trim().equals("")) {
                    vendedorSelecionado = true;

                    String[] arrVendedorTotvs = codigoLocalizadoTotvs[itemSelecionado].split("=");

                    GettersSetters.setCodigoVendedor(idVendSel);
                    GettersSetters.setNomeVend(usuarioSel.replace("** ", ""));
                    GettersSetters.setCodTotvs(arrVendedorTotvs[0]);
                    GettersSetters.setNomeTotvs(arrVendedorTotvs[1]);
                    GettersSetters.setTipoVendedor(arrVendedorTotvs[2]);

                    if (!cliPermitidos[itemSelecionado].equals("")) {
                        GettersSetters.arrClientesPermitidos.add(cliPermitidos[itemSelecionado]);
                    }

                    if (!codigoBorrachPat[itemSelecionado].equals("")) {
                        GettersSetters.setCodBorrachPatio(codigoBorrachPat[itemSelecionado]);
                    }
                }
            }
        });
        AlertDialog alertDialog = builderUsuarios.create();
        alertDialog.show();

        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                btnColeta.setEnabled(true);

                if (vendedorSelecionado) {
                    if (!GettersSetters.getCodTotvs().equals("")) {
                        if (CheckConnection.isConnected(Home.this)) {
                            verificaBorracheiros = new VerificaBorracheiros();
                            verificaBorracheiros.execute(false, true);
                        } else {
                            GettersSetters.resetGettersSetters();
                            Intent it = new Intent(Home.this, ColetaCliente.class);
                            startActivity(it);
                            finish();
                        }
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
                        builder.setTitle("A_T_E_N_Ç_Ã_O");
                        builder.setIcon(R.drawable.atencao);
                        builder.setCancelable(false);
                        builder.setMessage("Código de vendedor (COD_TOTVS) NÃO vinculado à: " + GettersSetters.getCodigoVendedor() + " - " + GettersSetters.getNomeVend() + "!\nEntre em contato com o Administrador.");
                        builder.setNegativeButton("Fechar", null);
                        builder.show();
                    }
                }
            }
        });
    }

    Button.OnClickListener onClickAtualizaProdutos = new Button.OnClickListener() {
        @SuppressLint("UseCompatLoadingForDrawables")
        @Override
        public void onClick(View v) {
            if (CheckConnection.internetConnAvaliable()) {
                atualizarDadosObrigatorios(v);
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
                builder.setTitle("Aviso de CONEXÃO RUIM");
                builder.setIcon(R.drawable.conexaomovel);
                builder.setCancelable(false);
                builder.setMessage("CONEXÃO de internet está RUIM. Atualização não permitida!\nTente novamente mais tarde.");
                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        isAtualizaObrigatDados = false;
                        versaoAtualClientes = "";
                        versaoAtualProdutos = "";
                        versaoAtualBorracheiros = "";
                        versaoAtualCondPagto = "";
                        novaVersaoProdutos = "";
                        novaVersaoClientes = "";
                        novaVersaoBorracheiros = "";
                        novaVersaoCondPgto = "";
                        atualizaDadosObrigat = 0;
                        isCancelaAtualizacaoDados = true;

                        if (dialogAdmin != null) {
                            dialogAdmin.dismiss();
                        }
                    }
                });
                builder.show();
            }
        }
    };

    Button.OnClickListener onClickAtualizaClientes = new Button.OnClickListener() {
        @SuppressLint("UseCompatLoadingForDrawables")
        @Override
        public void onClick(View v) {
            if (CheckConnection.internetConnAvaliable()) {
                atualizarDadosObrigatorios(v);
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
                builder.setTitle("Aviso de CONEXÃO RUIM");
                builder.setIcon(R.drawable.conexaomovel);
                builder.setCancelable(false);
                builder.setMessage("CONEXÃO de internet está RUIM. Atualização não permitida!\nTente novamente mais tarde.");
                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        isAtualizaObrigatDados = false;
                        versaoAtualClientes = "";
                        versaoAtualProdutos = "";
                        versaoAtualBorracheiros = "";
                        versaoAtualCondPagto = "";
                        novaVersaoProdutos = "";
                        novaVersaoClientes = "";
                        novaVersaoCondPgto = "";
                        novaVersaoBorracheiros = "";
                        atualizaDadosObrigat = 0;
                        isCancelaAtualizacaoDados = true;

                        if (dialogAdmin != null) {
                            dialogAdmin.dismiss();
                        }
                    }
                });
                builder.show();
            }
        }
    };

    Button.OnClickListener onClickAtualizaBorracheiros = new Button.OnClickListener() {
        @SuppressLint("UseCompatLoadingForDrawables")
        @Override
        public void onClick(View v) {
            if (CheckConnection.internetConnAvaliable()) {
                atualizarDadosObrigatorios(v);
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
                builder.setTitle("Aviso de CONEXÃO RUIM");
                builder.setIcon(R.drawable.conexaomovel);
                builder.setCancelable(false);
                builder.setMessage("CONEXÃO de internet está RUIM. Atualização não permitida!\nTente novamente mais tarde.");
                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        isAtualizaObrigatDados = false;
                        versaoAtualClientes = "";
                        versaoAtualProdutos = "";
                        versaoAtualBorracheiros = "";
                        versaoAtualCondPagto = "";
                        novaVersaoProdutos = "";
                        novaVersaoCondPgto = "";
                        novaVersaoClientes = "";
                        novaVersaoBorracheiros = "";
                        atualizaDadosObrigat = 0;
                        isCancelaAtualizacaoDados = true;

                        if (dialogAdmin != null) {
                            dialogAdmin.dismiss();
                        }
                    }
                });
                builder.show();
            }
        }
    };

    Button.OnClickListener onClickAtualizaCondPgto = new Button.OnClickListener() {
        @SuppressLint("UseCompatLoadingForDrawables")
        @Override
        public void onClick(View v) {
            if (CheckConnection.internetConnAvaliable()) {
                atualizarDadosObrigatorios(v);
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
                builder.setTitle("Aviso de CONEXÃO RUIM");
                builder.setIcon(R.drawable.conexaomovel);
                builder.setCancelable(false);
                builder.setMessage("CONEXÃO de internet está RUIM. Atualização não permitida!\nTente novamente mais tarde.");
                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        isAtualizaObrigatDados = false;
                        versaoAtualClientes = "";
                        versaoAtualProdutos = "";
                        versaoAtualBorracheiros = "";
                        versaoAtualCondPagto = "";
                        novaVersaoProdutos = "";
                        novaVersaoCondPgto = "";
                        novaVersaoClientes = "";
                        novaVersaoBorracheiros = "";
                        atualizaDadosObrigat = 0;
                        isCancelaAtualizacaoDados = true;

                        if (dialogAdmin != null) {
                            dialogAdmin.dismiss();
                        }
                    }
                });
                builder.show();
            }
        }
    };


    public void atualizarDadosObrigatorios(@NotNull View v) {
        mProgressDialog = new ProgressDialog(Home.this);
        mProgressDialog.setIcon(R.drawable.ampulheta);
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setMax(100);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(false);

        if (v.toString().toLowerCase().contains("cliente")) {
            mProgressDialog.setTitle("Clientes");
            mProgressDialog.setMessage("Atualizando Clientes... Aguarde...");

            GettersSetters.setAtualizarClientes(false);

            verificaClientes = new VerificaClientes();
            verificaClientes.execute(true, false);
        } else if (v.toString().toLowerCase().contains("produto")) {
            mProgressDialog.setTitle("Produtos e Tabelas");
            mProgressDialog.setMessage("[1] Atualizando Produtos...\n[2] Atualizando tabelas de preços...\nAguarde...");

            GettersSetters.setAtualizarProdutos(false);

            verificacaoProdutos = new VerificacaoProdutos();
            verificacaoProdutos.execute(true, false);
        } else if (v.toString().toLowerCase().contains("borrach")) {
            verificaBorracheiros = new VerificaBorracheiros();
            verificaBorracheiros.execute(true, false);
        } else if (v.toString().toLowerCase().contains("cond")) {
            verificaCondPgto = new VerificaCondPgto();
            verificaCondPgto.execute(true, false);
        }

        //VALIDACOES PARA ATUALIZAÇÕES OBRIGATÓRIA DOS DADOS (CLIENTES E PRODUTOS)
        if (isAtualizaObrigatDados) {
            v.setVisibility(View.GONE);
            atualizaDadosObrigat--;
            if (atualizaDadosObrigat == 0) {
                isAtualizaObrigatDados = false;
                if (dialogAdmin != null) {
                    dialogAdmin.dismiss();
                }
            }
        }
    }

    public void iniciarColeta() {
        if (GettersSetters.getTipoUsuario() == null || !GettersSetters.getTipoUsuario().equals("V")) {
            trocarVendedor();
        } else {
            /* SELECAO DE VENDEDOR QUANDO FOR INICIADO O APLICATIVO POR UM GERENTE **/
            if (!GettersSetters.getCodTotvs().equals("")) {
                if (CheckConnection.isConnected(Home.this)) {
                    verificaBorracheiros = new VerificaBorracheiros();
                    verificaBorracheiros.execute(false, true);
                } else {
                    GettersSetters.resetGettersSetters();
                    Intent it = new Intent(Home.this, ColetaCliente.class);
                    startActivity(it);
                    finish();
                }
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
                builder.setTitle("A_T_E_N_Ç_Ã_O");
                builder.setIcon(R.drawable.atencao);
                builder.setCancelable(false);
                builder.setMessage("Código de vendedor (COD_TOTVS) NÃO vinculado à: " + GettersSetters.getCodigoVendedor() + " - " + GettersSetters.getNomeVend() + "!\nEntre em contato com o Administrador.");
                builder.setNegativeButton("Fechar", null);
                builder.show();
            }
        }
    }

    @SuppressLint({"ApplySharedPref", "SetTextI18n"})
    public void validaAtualizDados(boolean isIniciaColeta) {
        SharedPreferences prefsAtual = getSharedPreferences(ATUALIZACAO_DADOS, MODE_PRIVATE);
        versaoAtualClientes = prefsAtual.getString("clientes", "");
        versaoAtualProdutos = prefsAtual.getString("produtos", "");
        versaoAtualBorracheiros = prefsAtual.getString("borracheiros", "");
        versaoAtualCondPagto = prefsAtual.getString("condPagamento", "");

        //REINICIALIZA AS SHARED PREFERENCES QUANDO ESTIVER PREENCHIDA COM A DATA - usado nas versões anteriores
        if (!versaoAtualClientes.equals("") && versaoAtualClientes.contains("-")) {
            SharedPreferences.Editor editor = getSharedPreferences(ATUALIZACAO_DADOS, MODE_PRIVATE).edit();
            editor.putString("clientes", "");
            editor.commit();
            versaoAtualClientes = "0";
        }

        //REINICIALIZA AS SHARED PREFERENCES QUANDO ESTIVER PREENCHIDA COM A DATA - usado nas versões anteriores
        if (!versaoAtualProdutos.equals("") && versaoAtualProdutos.contains("-")) {
            SharedPreferences.Editor editor = getSharedPreferences(ATUALIZACAO_DADOS, MODE_PRIVATE).edit();
            editor.putString("produtos", "");
            editor.commit();
            versaoAtualProdutos = "0";
        }

        //REINICIALIZA AS SHARED PREFERENCES QUANDO ESTIVER PREENCHIDA COM A DATA - usado nas versões anteriores
        if (!versaoAtualBorracheiros.equals("") && versaoAtualBorracheiros.contains("-")) {
            SharedPreferences.Editor editor = getSharedPreferences(ATUALIZACAO_DADOS, MODE_PRIVATE).edit();
            editor.putString("borracheiros", "");
            editor.commit();
            versaoAtualBorracheiros = "0";
        }

        //REINICIALIZA AS SHARED PREFERENCES QUANDO ESTIVER PREENCHIDA COM A DATA - usado nas versões anteriores
        if (!versaoAtualCondPagto.equals("") && versaoAtualCondPagto.contains("-")) {
            SharedPreferences.Editor editor = getSharedPreferences(ATUALIZACAO_DADOS, MODE_PRIVATE).edit();
            editor.putString("condPagamento", "");
            editor.commit();
            versaoAtualCondPagto = "0";
        }

        novaVersaoProdutos = conecta.selecionaParametroAtualizaDados("PRODUTOS");
        novaVersaoClientes = conecta.selecionaParametroAtualizaDados("CLIENTES");
        novaVersaoBorracheiros = conecta.selecionaParametroAtualizaDados("BORRACHEIROS");
        novaVersaoCondPgto = conecta.selecionaParametroAtualizaDados("CONDIPAGTO");

        if (isIniciaColeta) {
            if (versaoAtualProdutos.equals("") || (Double.parseDouble(versaoAtualProdutos) < Double.parseDouble(novaVersaoProdutos))) {
                isAtualizaObrigatDados = true;
                atualizaDadosObrigat++;
            }

            if (versaoAtualClientes.equals("") || (Double.parseDouble(versaoAtualClientes) < Double.parseDouble(novaVersaoClientes))) {
                isAtualizaObrigatDados = true;
                atualizaDadosObrigat++;
            }

            if (versaoAtualBorracheiros.equals("") || (Double.parseDouble(versaoAtualBorracheiros) < Double.parseDouble(novaVersaoBorracheiros))) {
                isAtualizaObrigatDados = true;
                atualizaDadosObrigat++;
            }

            if (versaoAtualCondPagto.equals("") || (Double.parseDouble(versaoAtualCondPagto) < Double.parseDouble(novaVersaoCondPgto))) {
                isAtualizaObrigatDados = true;
                atualizaDadosObrigat++;
            }

            if (atualizaDadosObrigat > 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
                LayoutInflater layoutInflater = getLayoutInflater();
                builder.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.exclamation, null));
                builder.setTitle("ATUALIZAÇÃO");
                builder.setMessage("Para continuar, atualize os seguintes dados:");
                @SuppressLint("InflateParams")
                View dialogView = layoutInflater.inflate(R.layout.view_configuracoes, null);

                final LinearLayout llSqLite = dialogView.findViewById(R.id.llSqLite);
                final LinearLayout llComissBorrach = dialogView.findViewById(R.id.llComissBorrach);
                final LinearLayout llOutrasConfig = dialogView.findViewById(R.id.llOutrasConfig);
                final Button btnSincConfig = dialogView.findViewById(R.id.btnSincConfig);
                final Button btnAtualClientesCfg = dialogView.findViewById(R.id.btnAtualClientesCfg);
                final Button btnAtualProdutosCfg = dialogView.findViewById(R.id.btnAtualProdutosCfg);
                final Button btnAtualCondPgtoCfg = dialogView.findViewById(R.id.btnAtualCondPgtoCfg);
                final Button btnAtualBorracheirosCfg = dialogView.findViewById(R.id.btnAtualBorracheirosCfg);

                builder.setCancelable(false);
                builder.setView(dialogView);

                llSqLite.setVisibility(View.GONE);
                llComissBorrach.setVisibility(View.GONE);
                llOutrasConfig.setVisibility(View.GONE);
                btnSincConfig.setVisibility(View.GONE);
                btnAtualClientesCfg.setVisibility(View.GONE);
                btnAtualProdutosCfg.setVisibility(View.GONE);
                btnAtualCondPgtoCfg.setVisibility(View.GONE);
                btnAtualBorracheirosCfg.setVisibility(View.GONE);

                if (versaoAtualProdutos.equals("") || (Double.parseDouble(versaoAtualProdutos) < Double.parseDouble(novaVersaoProdutos))) {
                    btnAtualProdutosCfg.setVisibility(View.VISIBLE);
                    btnAtualProdutosCfg.setText("Produtos\nV. " + versaoAtualProdutos + " p/ " + novaVersaoProdutos);
                    btnAtualProdutosCfg.setOnClickListener(onClickAtualizaProdutos);
                }

                if (versaoAtualClientes.equals("") || (Double.parseDouble(versaoAtualClientes) < Double.parseDouble(novaVersaoClientes))) {
                    btnAtualClientesCfg.setVisibility(View.VISIBLE);
                    btnAtualClientesCfg.setText("Clientes\nV. " + versaoAtualClientes + " p/ " + novaVersaoClientes);
                    btnAtualClientesCfg.setOnClickListener(onClickAtualizaClientes);
                }

                if (versaoAtualBorracheiros.equals("") || (Double.parseDouble(versaoAtualBorracheiros) < Double.parseDouble(novaVersaoBorracheiros))) {
                    btnAtualBorracheirosCfg.setVisibility(View.VISIBLE);
                    btnAtualBorracheirosCfg.setText("Borracheiros\nV. " + versaoAtualBorracheiros + " p/ " + novaVersaoBorracheiros);
                    btnAtualBorracheirosCfg.setOnClickListener(onClickAtualizaBorracheiros);
                }

                if (versaoAtualCondPagto.equals("") || (Double.parseDouble(versaoAtualCondPagto) < Double.parseDouble(novaVersaoCondPgto))) {
                    btnAtualCondPgtoCfg.setVisibility(View.VISIBLE);
                    btnAtualCondPgtoCfg.setText("Cond.Pgto.\nV. " + versaoAtualCondPagto + " p/ " + novaVersaoCondPgto);
                    btnAtualCondPgtoCfg.setOnClickListener(onClickAtualizaCondPgto);
                }

                dialogAdmin = builder.create();
                dialogAdmin.show();
            } else {
                iniciarColeta();
            }
        }
    }

    public void dialogAtualizacao() {
        apkAtualizacao = new File(Environment.getExternalStorageDirectory() + "/Coleta");
        asyncAtualizaApp = new AtualizacaoApp();

        AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
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
        builder.setNegativeButton("Fechar", null);
        builder.setView(view);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
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
            mProgressDialog = new ProgressDialog(Home.this, 0);
            mProgressDialog.setIcon(R.drawable.download);
            mProgressDialog.setTitle("Atualização");
            mProgressDialog.setMessage("Baixando Atualização...\nAguarde...");
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
                    Uri fileUri = FileProvider.getUriForFile(Home.this, BuildConfig.APPLICATION_ID + ".provider", file);
                    install.setDataAndType(fileUri, type);
                } else {
                    install.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    install.setDataAndType(Uri.fromFile(file), type);
                }

                try {
                    Home.this.startActivity(install);
                    finish();
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(Home.this, "No activity found to open this attachment.", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(Home.this, "Atualização não baixada! Tente novamente.", Toast.LENGTH_LONG).show();
            }
        }
    }
}