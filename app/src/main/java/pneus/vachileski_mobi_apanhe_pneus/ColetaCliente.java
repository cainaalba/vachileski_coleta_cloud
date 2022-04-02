package pneus.vachileski_mobi_apanhe_pneus;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import pneus.vachileski_mobi_funcoes_genericas.CheckConnection;
import pneus.vachileski_mobi_funcoes_genericas.ConexaoBDExt;
import pneus.vachileski_mobi_funcoes_genericas.ConexaoBDInt;
import pneus.vachileski_mobi_funcoes_genericas.FuncoesGenericas;
import pneus.vachileski_mobi_funcoes_genericas.GettersSetters;
import pneus.vachileski_mobi_funcoes_genericas.HTTPDataHandler;
import pneus.vachileski_mobi_funcoes_genericas.ValidaCPFeCNPJ;

@SuppressWarnings({"SpellCheckingInspection", "Convert2Diamond", "DanglingJavadoc", "ResultOfMethodCallIgnored", "Convert2Lambda"})
@SuppressLint("StaticFieldLeak")
public class ColetaCliente extends AppCompatActivity {
    Button btnAvancar, btnVoltar, btnBuscarCli, btnAtualizaClientes, btnLimparDados, btnVisualizarDoc;
    EditText cmpDataIni, cmpDoc, cmpRzSocial, cmpIE, cmpEmail, cmpRefComerciais;
    TextView codVendedor, nomeVendedor;

    // Entrega
    EditText cmpCepEntrega, cmpEndEntrega, cmpBairroEntrega, cmpDDDEntrega, cmpFoneEntrega, cmpNumEntrega, cmpComplEntrega;
    AutoCompleteTextView cmpMunEntrega;
    Spinner spnEstEntr;

    // Cobrança
    EditText cmpCepCobr, cmpEndCobr, cmpBairroCobr, cmpDDDCobr, cmpFoneCobr, cmpNumCobr, cmpComplCobr;
    AutoCompleteTextView cmpMunCobr;

    Spinner spnEstCobr, spnFilial;
    LinearLayout llRefComerc;
    View viewSnackBar;

    public static Button btnRGNovoCli;
    public static Button btnDocNovoCli;
    public static Button btnCompResidNovoCli;
    public static Button btnConfirmarDocumentos;
    public static Button btnCancelaDocumentos;
    public static ProgressDialog mProgressDialog;

    boolean isDocValid = false;
    boolean isDocVisiv = false;

    public static boolean rgOK = false, cpfCnpjOk = false, comprResidOK = false;

    String documentoClienteSemFormatacao = "";
    String documentoClienteComFormatacao = "";

    // Busca CEP
    String cepEntrada = "";
    String urlCep = "";

    // Busca CNPJ
    String urlCNPJ = "";

    StringBuilder documentoOcultoCliente;

    ArrayList<String> arrEstadoSigla = new ArrayList<String>();
    ArrayList<String> arrFiliais = new ArrayList<String>();
    ArrayAdapter<String> arrAdapterFilial = null;

    ArrayList<String> arrNomeCliente = new ArrayList<>();
    ArrayList<String> arrDocCliente = new ArrayList<>();
    ArrayList<String> arrMunCliente = new ArrayList<>();
    ArrayList<String> arrEstCliente = new ArrayList<>();
    ArrayList<String> arrIECliente = new ArrayList<>();
    ArrayList<String> arrRecnoCliente = new ArrayList<>();

    public String nomeCli = "";
    public String endCli = "";
    public String bairroCli = "";
    public String cepCli = "";
    public String emailCli = "";
    public String munCli = "";
    public String ufCli = "";
    public String dddCli = "";
    public String foneCli = "";
    public String codCli = "";
    public String lojaCli = "";
    public String ieCli = "";
    public String endCobCli = "";
    public String bairroCobCli = "";
    public String cepCobCli = "";
    public String munCobCli = "";
    public String ufCobCli = "";
    public String cliBlq = "";
    public String condPgtoCli = "";
    public String catCli = "";
    public String ddd2Cli = "";
    public String teleXCli = "";
    public String tabPrcCli = "";
    public String recnoCli = "";
    public String codBorr = "";
    public boolean clienteGrp = false;
    public double descExclusivo = 0;

    ConexaoBDExt conecta = new ConexaoBDExt();
    ConexaoBDInt db;

    String[] estados = null;

    CarregamentoInicial carregamentoInicial = null;
    VerificaClientes verificaClientes = null;

    AlertDialog dialogDocs = null;

    @SuppressWarnings("unchecked")
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_coleta_cliente);

        db = new ConexaoBDInt(this);

        btnAvancar = findViewById(R.id.btnAvancar);
        btnVoltar = findViewById(R.id.btnVoltar);
        btnBuscarCli = findViewById(R.id.btnBuscaCli);
        btnAtualizaClientes = findViewById(R.id.btnAtualizaClientes);
        btnLimparDados = findViewById(R.id.btnLimparDados);
        spnFilial = findViewById(R.id.spnFilial);
        codVendedor = findViewById(R.id.txtCodVendedor);
        nomeVendedor = findViewById(R.id.txtNomeVendedor);
        cmpDoc = findViewById(R.id.cmpDoc);
        cmpRzSocial = findViewById(R.id.cmpRzSocial);
        cmpIE = findViewById(R.id.cmpIE);
        cmpEmail = findViewById(R.id.cmpMail);
        btnVisualizarDoc = findViewById(R.id.btnVisualizadDoc);

        //Entrega
        cmpCepEntrega = findViewById(R.id.cmpCepEntrega);
        cmpEndEntrega = findViewById(R.id.cmpEndEntrega);
        cmpBairroEntrega = findViewById(R.id.cmpBairroEnterga);
        cmpMunEntrega = findViewById(R.id.cmpMunEntrega);
        cmpDDDEntrega = findViewById(R.id.cmpDDDEntrega);
        cmpFoneEntrega = findViewById(R.id.cmpFoneEntrega);
        cmpNumEntrega = findViewById(R.id.cmpNumEntrega);
        cmpComplEntrega = findViewById(R.id.cmpComplEntrega);

        spnEstEntr = findViewById(R.id.spnEstEntr);
        spnEstCobr = findViewById(R.id.spnEstCob);

        //Cobrança
        cmpCepCobr = findViewById(R.id.cmpCepCobr);
        cmpEndCobr = findViewById(R.id.cmpEndCobr);
        cmpBairroCobr = findViewById(R.id.cmpBairroCobr);
        cmpMunCobr = findViewById(R.id.cmpMunCobr);
        cmpDDDCobr = findViewById(R.id.cmpDDDCobr);
        cmpFoneCobr = findViewById(R.id.cmpFoneCobr);
        cmpNumCobr = findViewById(R.id.cmpNumCobr);
        cmpComplCobr = findViewById(R.id.cmpComplCobr);

        llRefComerc = findViewById(R.id.llRefComerc);
        viewSnackBar = findViewById(R.id.viewSnackBar);
        cmpRefComerciais = findViewById(R.id.cmpRefComerciais);

        llRefComerc.setVisibility(View.GONE);
        btnAvancar.setVisibility(View.VISIBLE);
        btnAtualizaClientes.setVisibility(View.GONE);

        cmpNumEntrega.setVisibility(View.GONE);
        cmpNumCobr.setVisibility(View.GONE);
        cmpComplEntrega.setVisibility(View.GONE);
        cmpComplCobr.setVisibility(View.GONE);

        codVendedor.setVisibility(View.GONE);
        codVendedor.setText(GettersSetters.getCodigoVendedor());
        nomeVendedor.setText(GettersSetters.getNomeVend());

        ArrayAdapter<String> adapterFilial = new ArrayAdapter<String>(ColetaCliente.this, android.R.layout.simple_spinner_item, arrFiliais);
        adapterFilial.setDropDownViewResource(R.layout.text_view_item_high);
        spnFilial.setAdapter(adapterFilial);
        //noinspection unchecked
        arrAdapterFilial = (ArrayAdapter<String>) spnFilial.getAdapter();

        carregamentoInicial = new CarregamentoInicial();
        carregamentoInicial.execute(false);

        /** ESTADOS **/
        estados = getResources().getStringArray(R.array.estados);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, estados);

        //ArrayAdapter<String> adapterEstado = new ArrayAdapter<String>(ColetaCliente.this, android.R.layout.simple_spinner_item, estados);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_activated_1);
        spnEstEntr.setAdapter(adapter);

        //ArrayAdapter<String> adapterEstadoCob = new ArrayAdapter<String>(ColetaCliente.this, android.R.layout.simple_spinner_item, arrEstadoSigla);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_activated_1);
        spnEstCobr.setAdapter(adapter);

        /** BUSCA DOS ESTADOS **/
        Cursor resEstados = db.buscaEstados();
        if (resEstados.getCount() == 0) {
            for (String estado : estados) {
                db.insereEstados(estado);
            }
        } else {
            ArrayAdapter<String> adpEnt = (ArrayAdapter<String>) spnEstEntr.getAdapter();
            ArrayAdapter<String> adpCob = (ArrayAdapter<String>) spnEstCobr.getAdapter();

            adpEnt.setNotifyOnChange(false);
            adpCob.setNotifyOnChange(false);

            Collections.addAll(arrEstadoSigla, estados);

            // Habilitar novamente a notificacao
            adpEnt.setNotifyOnChange(true);
            adpCob.setNotifyOnChange(true);

            // Notifica o Spinner de que houve mudanca no modelo
            adpEnt.notifyDataSetChanged();
            adpCob.notifyDataSetChanged();
        }

        if (!CheckConnection.isConnected(ColetaCliente.this)) {
            AlertDialog.Builder builderConexao = new AlertDialog.Builder(ColetaCliente.this);
            builderConexao.setTitle("Aplicação offline!");
            builderConexao.setIcon(R.drawable.off);
            builderConexao.setCancelable(true);
            builderConexao.setMessage("Não é possível buscar: Municípios, CEP e dados de Novos Clientes.");
            builderConexao.setPositiveButton("Entendi", null);
            AlertDialog dialog = builderConexao.create();
            dialog.show();
        } else {
            btnAtualizaClientes.setVisibility(View.VISIBLE);
        }

        cmpDoc.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                nomeCli = "";
                recnoCli = "";

                if (!hasFocus && !documentoClienteSemFormatacao.trim().equals("")) {
                    arrNomeCliente.clear();
                    arrDocCliente.clear();
                    arrMunCliente.clear();
                    arrEstCliente.clear();
                    arrRecnoCliente.clear();

                    if (documentoClienteSemFormatacao.length() < 4) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ColetaCliente.this);
                        builder.setCancelable(false);
                        builder.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.atencao, null));
                        builder.setTitle("Busca de Documento!");
                        builder.setMessage(getString(R.string.erro_tamanho_string));
                        builder.setPositiveButton("Fechar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                cmpDoc.requestFocus();
                                cmpDoc.setFocusable(true);
                            }
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    } else {
                        isDocValid = ValidaCPFeCNPJ.isDocValid(documentoClienteSemFormatacao);

                        if (isDocValid) {
                            habCamposDadosCli();
                            BuscaCli buscaCli = new BuscaCli();
                            buscaCli.execute();
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(ColetaCliente.this);
                            builder.setCancelable(false);
                            builder.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.atencao, null));
                            builder.setTitle("Documeto inválido!");
                            builder.setMessage(getString(R.string.erro_documento_invalido));
                            builder.setPositiveButton("Fechar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    cmpDoc.getText().clear();
                                    cmpDoc.setFocusable(true);
                                    cmpDoc.requestFocus();
                                }
                            });
                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                        }
                    }
                } else if (documentoClienteSemFormatacao.trim().equals("")) {
                    habCamposDadosCli();
                    limpaCampos();
                }
            }
        });

        btnVisualizarDoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isDocVisiv) {
                    isDocVisiv = true;
                    cmpDoc.setText(documentoClienteComFormatacao);
                    btnVisualizarDoc.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.visualizar, null));
                } else {
                    isDocVisiv = false;
                    cmpDoc.setText(documentoOcultoCliente);
                    btnVisualizarDoc.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.ocultar, null));
                }
            }
        });

        cmpEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus && GettersSetters.isValidarDadosCli() && cmpCepEntrega.getText().toString().trim().equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ColetaCliente.this);
                    builder.setTitle("Cadastro de Cliente");
                    builder.setMessage("Para continuar o cadastro, preencha o CEP de Entrega.");
                    builder.setPositiveButton("Preencher", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            cmpCepEntrega.setFocusable(true);
                            cmpCepEntrega.requestFocus();

                            cmpCepCobr.addTextChangedListener(textWatcherCepCobr);
                        }
                    });
                    builder.setCancelable(false);
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            }
        });

        btnBuscarCli.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                documentoClienteSemFormatacao = "";
                recnoCli = "";

                nomeCli = cmpRzSocial.getText().toString().trim().toUpperCase();

                arrNomeCliente.clear();
                arrDocCliente.clear();
                arrMunCliente.clear();
                arrEstCliente.clear();
                arrRecnoCliente.clear();

                if (nomeCli.length() < 3) {
                    cmpRzSocial.setFocusable(true);
                    cmpRzSocial.requestFocus();
                    cmpRzSocial.setError(getString(R.string.tamanho_3_invalido));
                } else {
                    habCamposDadosCli();

                    BuscaCli buscaCli = new BuscaCli();
                    buscaCli.execute();
                }
            }

        });

        btnLimparDados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(ColetaCliente.this);
                builder.setCancelable(false);
                builder.setTitle("Atenção!");
                builder.setMessage("Confirma a limpeza de TODOS os campos?");
                builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cmpDoc.getText().clear();
                        limpaCampos();
                        habCamposDadosCli();

                        documentoClienteComFormatacao = "";
                        documentoClienteSemFormatacao = "";
                        btnVisualizarDoc.setVisibility(View.GONE);
                        isDocVisiv = false;

                        GettersSetters.setTabelaPrcCli("");
                        GettersSetters.setDescExclusivo(0);
                        GettersSetters.setDescGrupo(0);
                        GettersSetters.setCodCli("");
                        GettersSetters.setLojaCli("");
                        GettersSetters.setTabelaPrcCli("");
                        GettersSetters.setCategoriaCli("");
                        GettersSetters.setCondPgto("");
                        GettersSetters.setClienteGrp(false);

                        GettersSetters.setValidarDadosCli(true);
                        GettersSetters.setDocsNovoCliOK(true);

                        cmpNumEntrega.setVisibility(View.GONE);
                        cmpNumCobr.setVisibility(View.GONE);
                        cmpComplEntrega.setVisibility(View.GONE);
                        cmpComplCobr.setVisibility(View.GONE);

                        btnBuscarCli.setVisibility(View.VISIBLE);
                    }
                });
                builder.setNegativeButton("Não", null);
                builder.show();
            }
        });

        cmpCepEntrega.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (GettersSetters.isValidarDadosCli()) {
                    if (CheckConnection.isConnected(ColetaCliente.this)) {
                        if (cmpCepEntrega.getText().toString().trim().length() == 8 && !cmpCepEntrega.getText().toString().trim().equals("00000000")) {
                            cepEntrada = cmpCepEntrega.getText().toString().trim();
                            urlCep = "https://viacep.com.br/ws/" + cepEntrada + "/json/";
                            new ProcessJSONCEP().execute(urlCep);

                            cmpDDDEntrega.setFocusable(true);
                            cmpDDDEntrega.requestFocus();
                        }
                    }
                }
            }
        });

//        cmpCepEntrega.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (CheckConnection.isConnected(ColetaCliente.this)) {
//                    if (!hasFocus && !cmpCepEntrega.getText().toString().trim().isEmpty()) {
//                        cepEntrada = cmpCepEntrega.getText().toString().trim();
//                        urlCep = "https://viacep.com.br/ws/" + cepEntrada + "/json/";
//                        new ProcessJSONCEP().execute(urlCep);
//                    }
//                }
//            }
//        });

//        cmpCepCobr.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//                if (GettersSetters.isValidarDadosCliente()) {
//                    if (CheckConnection.isConnected(ColetaCliente.this)) {
//                        if (cmpCepCobr.getText().toString().trim().length() == 8) {
//                            cepEntrada = cmpCepCobr.getText().toString().trim();
//                            urlCep = "https://viacep.com.br/ws/" + cepEntrada + "/json/";
//                            new ProcessJSONCEP().execute(urlCep);
//                        }
//                    }
//                }
//            }
//        });

//        cmpCepCobr.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (CheckConnection.isConnected(ColetaCliente.this)) {
//                    if (!hasFocus && !cmpCepCobr.getText().toString().trim().isEmpty()) {
//                        cepEntrada = cmpCepCobr.getText().toString().trim();
//                        urlCep = "https://viacep.com.br/ws/" + cepEntrada + "/json/";
//                        new ProcessJSONCEPCob().execute(urlCep);
//                    }
//                }
//            }
//        });

//        spnEstEntr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                estadoSelecionado = spnEstEntr.getItemAtPosition(position).toString();
//
//                if (cmpCepEntrega.getText().toString().trim().isEmpty()) {
//                    urlEstados = "http://servicodados.ibge.gov.br/api/v1/localidades/estados/" + estadoSelecionado + "/municipios";
//                    new ProcessJSONMunicipio().execute(urlEstados);
//                    cmpMunEntrega.setAdapter(getMunicipios(ColetaCliente.this));
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });

//        spnEstCobr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                estadoSelecionado = spnEstCobr.getItemAtPosition(position).toString();
//
//                if (cmpCepCobr.getText().toString().trim().isEmpty()) {
//                    urlEstados = "http://servicodados.ibge.gov.br/api/v1/localidades/estados/" + estadoSelecionado + "/municipios";
//                    new ProcessJSONMunicipio().execute(urlEstados);
//                    cmpMunCobr.setAdapter(getMunicipios(ColetaCliente.this));
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });

//        cmpFoneEntrega.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (!hasFocus && !cmpEndEntrega.getText().toString().trim().isEmpty() && !cmpBairroEntrega.getText().toString().trim().isEmpty() && !cmpMunEntrega.getText().toString().trim().isEmpty()
//                        && !cmpCepEntrega.getText().toString().trim().isEmpty() && !cmpDDDEntrega.getText().toString().trim().isEmpty() && !cmpFoneEntrega.getText().toString().trim().isEmpty()) {
//                    final AlertDialog.Builder builder = new AlertDialog.Builder(ColetaCliente.this);
//                    builder.setCancelable(false);
//                    builder.setTitle("Atenção!");
//                    builder.setMessage("O endereço de COBRANÇA é o mesmo da ENTREGA?");
//                    builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            cmpCepCobr.removeTextChangedListener(textWatcher);
//
//                            cmpCepCobr.setText(cmpCepEntrega.getText().toString().trim().toUpperCase());
//                            cmpEndCobr.setText(cmpEndEntrega.getText().toString().trim().toUpperCase());
//                            cmpBairroCobr.setText(cmpBairroEntrega.getText().toString().trim().toUpperCase());
//                            cmpMunCobr.setText(cmpMunEntrega.getText().toString().trim().toUpperCase());
//                            cmpDDDCobr.setText(cmpDDDEntrega.getText().toString().trim().toUpperCase());
////                            cmpFoneCobr.setText(cmpFoneEntrega.getText().toString().trim().toUpperCase());
//                            for (int i = 0; i < spnEstCobr.getAdapter().getCount(); i++) {
//                                if (spnEstCobr.getAdapter().getItem(i).toString().contains(spnEstEntr.getSelectedItem().toString())) {
//                                    spnEstCobr.setSelection(i);
//                                }
//                            }
//                        }
//                    });
//                    builder.setNegativeButton("Não", null);
//                    builder.show();
//                }
//            }
//        });

        btnAvancar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cmpDoc.setError(null);
                cmpRzSocial.setError(null);
                cmpIE.setError(null);
                cmpEmail.setError(null);
                cmpEndEntrega.setError(null);
                cmpBairroEntrega.setError(null);
                cmpMunEntrega.setError(null);
                cmpCepEntrega.setError(null);
                cmpDDDEntrega.setError(null);
                cmpFoneEntrega.setError(null);
                cmpEndCobr.setError(null);
                cmpBairroCobr.setError(null);
                cmpMunCobr.setError(null);
                cmpCepCobr.setError(null);
                cmpDDDCobr.setError(null);
                cmpFoneCobr.setError(null);
                cmpRefComerciais.setError(null);

                if (spnFilial.getSelectedItem() == null) {
                    final Snackbar snackbar = Snackbar.make(viewSnackBar, "Selecione a Filial", Snackbar.LENGTH_LONG);
                    snackbar.setBackgroundTint(getResources().getColor(R.color.colorPrimaryDark));
                    snackbar.setAction("Fechar", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    }).show();
                } else if (cmpDoc.getText().toString().trim().isEmpty()) {
                    cmpDoc.setFocusable(true);
                    cmpDoc.requestFocus();
                    cmpDoc.setError(getString(R.string.erro_campo_obrigatorio));
                    mostraTeclado();
                } else if (!ValidaCPFeCNPJ.isDocValid(documentoClienteSemFormatacao)) {
                    cmpDoc.setFocusable(true);
                    cmpDoc.requestFocus();
                    cmpDoc.setError(getString(R.string.erro_documento_invalido));
                    mostraTeclado();
                } else if (cmpRzSocial.getText().toString().trim().isEmpty() && GettersSetters.isValidarDadosCli()) {
                    cmpRzSocial.setFocusable(true);
                    cmpRzSocial.requestFocus();
                    cmpRzSocial.setError(getString(R.string.erro_campo_obrigatorio));
                    mostraTeclado();
                } else if (cmpIE.getText().toString().trim().isEmpty() && GettersSetters.isValidarDadosCli()) {
                    cmpIE.setFocusable(true);
                    cmpIE.requestFocus();
                    cmpIE.setError(getString(R.string.erro_campo_obrigatorio));
                    mostraTeclado();
                } else if (cmpEmail.getText().toString().trim().isEmpty() && GettersSetters.isValidarDadosCli()) {
                    cmpEmail.setFocusable(true);
                    cmpEmail.requestFocus();
                    cmpEmail.setError(getString(R.string.erro_campo_obrigatorio));
                    mostraTeclado();
                } else if (cmpCepEntrega.getText().toString().trim().isEmpty() && GettersSetters.isValidarDadosCli()) {
                    cmpCepEntrega.setFocusable(true);
                    cmpCepEntrega.requestFocus();
                    cmpCepEntrega.setError(getString(R.string.erro_campo_obrigatorio));
                    mostraTeclado();
                } else if (cmpEndEntrega.getText().toString().trim().isEmpty() && GettersSetters.isValidarDadosCli()) {
                    cmpEndEntrega.setFocusable(true);
                    cmpEndEntrega.requestFocus();
                    cmpEndEntrega.setError(getString(R.string.erro_campo_obrigatorio));
                    mostraTeclado();
                } else if (cmpBairroEntrega.getText().toString().trim().isEmpty() && GettersSetters.isValidarDadosCli()) {
                    cmpBairroEntrega.setFocusable(true);
                    cmpBairroEntrega.requestFocus();
                    cmpBairroEntrega.setError(getString(R.string.erro_campo_obrigatorio));
                    mostraTeclado();
                } else if (cmpMunEntrega.getText().toString().trim().isEmpty() && GettersSetters.isValidarDadosCli()) {
                    cmpMunEntrega.setFocusable(true);
                    cmpMunEntrega.requestFocus();
                    cmpMunEntrega.setError(getString(R.string.erro_campo_obrigatorio));
                    mostraTeclado();
                } else if (cmpDDDEntrega.getText().toString().trim().isEmpty() && GettersSetters.isValidarDadosCli()) {
                    cmpDDDEntrega.setFocusable(true);
                    cmpDDDEntrega.requestFocus();
                    cmpDDDEntrega.setError(getString(R.string.erro_campo_obrigatorio));
                    mostraTeclado();
                } else if (cmpFoneEntrega.getText().toString().trim().isEmpty() && GettersSetters.isValidarDadosCli()) {
                    cmpFoneEntrega.setFocusable(true);
                    cmpFoneEntrega.requestFocus();
                    cmpFoneEntrega.setError(getString(R.string.erro_campo_obrigatorio));
                    mostraTeclado();
                } else if (cmpNumEntrega.getText().toString().trim().isEmpty() && GettersSetters.isValidarDadosCli()) {
                    cmpNumEntrega.setFocusable(true);
                    cmpNumEntrega.requestFocus();
                    cmpNumEntrega.setError(getString(R.string.erro_campo_obrigatorio));
                    mostraTeclado();
//                } else if (cmpComplEntrega.getText().toString().trim().isEmpty() && GettersSetters.isValidarDadosCliente()) {
//                    cmpComplEntrega.setFocusable(true);
//                    cmpComplEntrega.requestFocus();
//                    cmpComplEntrega.setError(getString(R.string.erro_campo_obrigatorio));
//                    mostraTeclado();
                } else if (cmpEndCobr.getText().toString().trim().isEmpty() && GettersSetters.isValidarDadosCli()) {
                    cmpEndCobr.setFocusable(true);
                    cmpEndCobr.requestFocus();
                    cmpEndCobr.setError(getString(R.string.erro_campo_obrigatorio));
                    mostraTeclado();
                } else if (cmpBairroCobr.getText().toString().trim().isEmpty() && GettersSetters.isValidarDadosCli()) {
                    cmpBairroCobr.setFocusable(true);
                    cmpBairroCobr.requestFocus();
                    cmpBairroCobr.setError(getString(R.string.erro_campo_obrigatorio));
                    mostraTeclado();
                } else if (cmpMunCobr.getText().toString().trim().isEmpty() && GettersSetters.isValidarDadosCli()) {
                    cmpMunCobr.setFocusable(true);
                    cmpMunCobr.requestFocus();
                    cmpMunCobr.setError(getString(R.string.erro_campo_obrigatorio));
                    mostraTeclado();
                } else if (cmpCepCobr.getText().toString().trim().isEmpty() && GettersSetters.isValidarDadosCli()) {
                    cmpCepCobr.setFocusable(true);
                    cmpCepCobr.requestFocus();
                    cmpCepCobr.setError(getString(R.string.erro_campo_obrigatorio));
                    mostraTeclado();
                } else if (cmpDDDCobr.getText().toString().trim().isEmpty() && GettersSetters.isValidarDadosCli()) {
                    cmpDDDCobr.setFocusable(true);
                    cmpDDDCobr.requestFocus();
                    cmpDDDCobr.setError(getString(R.string.erro_campo_obrigatorio));
                    mostraTeclado();
                } else if (cmpFoneCobr.getText().toString().trim().isEmpty() && GettersSetters.isValidarDadosCli()) {
                    cmpFoneCobr.setFocusable(true);
                    cmpFoneCobr.requestFocus();
                    cmpFoneCobr.setError(getString(R.string.erro_campo_obrigatorio));
                    mostraTeclado();
                } else if (cmpNumCobr.getText().toString().trim().isEmpty() && GettersSetters.isValidarDadosCli()) {
                    cmpNumCobr.setFocusable(true);
                    cmpNumCobr.requestFocus();
                    cmpNumCobr.setError(getString(R.string.erro_campo_obrigatorio));
                    mostraTeclado();
//                } else if (cmpComplCobr.getText().toString().trim().isEmpty() && GettersSetters.isValidarDadosCliente()) {
//                    cmpComplCobr.setFocusable(true);
//                    cmpComplCobr.requestFocus();
//                    cmpComplCobr.setError(getString(R.string.erro_campo_obrigatorio));
//                    mostraTeclado();
                } else {
                    if (spnFilial.getSelectedItem() == null) {
                        final Snackbar snackbar = Snackbar.make(viewSnackBar, "Selecione a Filial", Snackbar.LENGTH_LONG);
                        snackbar.setBackgroundTint(getResources().getColor(R.color.colorPrimaryDark));
                        snackbar.setAction("Fechar", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        }).show();
                    } else {
                        String[] filiais = spnFilial.getSelectedItem().toString().split("/");
                        GettersSetters.setDescrFilial(filiais[0].trim());
                        GettersSetters.setFilial(filiais[1].trim());


                        GettersSetters.setDocCli(documentoClienteSemFormatacao);
                        GettersSetters.setRzSocialCli(cmpRzSocial.getText().toString().trim().toUpperCase());
                        GettersSetters.setInscrCli(cmpIE.getText().toString().trim());
                        GettersSetters.setEmailCli(cmpEmail.getText().toString().trim().toLowerCase());
                        GettersSetters.setEndEntregaCli(cmpEndEntrega.getText().toString().trim().toUpperCase() + (GettersSetters.isValidarDadosCli() ? " " + cmpNumEntrega.getText().toString().trim() + " " + cmpComplEntrega.getText().toString().trim() : ""));
                        GettersSetters.setBairroEntregaCli(cmpBairroEntrega.getText().toString().trim().toUpperCase());
                        GettersSetters.setMunicEntregaCli(cmpMunEntrega.getText().toString().trim().toUpperCase());
                        GettersSetters.setEstEntregaCli(spnEstEntr.getSelectedItem().toString());
                        GettersSetters.setCepEntregaCli(cmpCepEntrega.getText().toString().trim());
                        GettersSetters.setDddEntregaCli(cmpDDDEntrega.getText().toString().trim());
                        GettersSetters.setFoneEntregaCli(cmpFoneEntrega.getText().toString().trim());
                        GettersSetters.setEndCobranCli(cmpEndCobr.getText().toString().trim().toUpperCase() + (GettersSetters.isValidarDadosCli() ? " " + cmpNumCobr.getText().toString().trim() + " " + cmpComplCobr.getText().toString().trim() : ""));
                        GettersSetters.setBairroCobranCli(cmpBairroCobr.getText().toString().trim().toUpperCase());
                        GettersSetters.setMunicCobranCli(cmpMunCobr.getText().toString().trim().toUpperCase());
                        GettersSetters.setEstCobranCli(spnEstCobr.getSelectedItem().toString());
                        GettersSetters.setCepCobranCli(cmpCepCobr.getText().toString().trim());
                        GettersSetters.setDddCobranCli(cmpDDDCobr.getText().toString().trim());
                        GettersSetters.setFoneCobranCli(cmpFoneCobr.getText().toString().trim());

                        if (!cmpRefComerciais.getText().toString().trim().isEmpty()) {
                            GettersSetters.setReferenciasComerciais(cmpRefComerciais.getText().toString().trim());
                        } else {
                            GettersSetters.setReferenciasComerciais("");
                        }

                        /**CODIGO E LOJA**/
                        if (GettersSetters.getCodCli().isEmpty()) { //(conecta.codCli.isEmpty() || db.codCli.isEmpty()) {
                            if (documentoClienteSemFormatacao.length() == 14) {
                                GettersSetters.setCodCli(documentoClienteSemFormatacao.substring(0, 8)); // Código do cliente conforme o CNPJ
                                GettersSetters.setLojaCli(documentoClienteSemFormatacao.substring(8, 12)); // Loja do cliente conforme o CNPJ
                            } else {
                                GettersSetters.setCodCli(documentoClienteSemFormatacao.substring(0, 9)); // Código do cliente conforme o CPF
                                GettersSetters.setLojaCli("0001"); // Loja do cliente conforme o CPF
                            }
                        }

                        /** TIRAR FOTOS DOS DOCUMENTOS DOS NOVOS CLIENTES **/
                        if (GettersSetters.isValidarDadosCli() && !GettersSetters.isDocsNovoCliOK()) {
                            GettersSetters.setTipoFoto("DOCUMENTOS");

                            AlertDialog.Builder builderDocs = new AlertDialog.Builder(ColetaCliente.this);
                            LayoutInflater layoutInflater = getLayoutInflater();
                            View dialogView = layoutInflater.inflate(R.layout.view_docs_novo_cli, null);
                            builderDocs.setCancelable(false);
                            builderDocs.setView(dialogView);

                            btnRGNovoCli = dialogView.findViewById(R.id.btnRGNovoCli);
                            btnDocNovoCli = dialogView.findViewById(R.id.btnDocNovoCli);
                            btnCompResidNovoCli = dialogView.findViewById(R.id.btnCompResidNovoCli);
                            btnConfirmarDocumentos = dialogView.findViewById(R.id.btnConfirmarDocumentos);
                            btnCancelaDocumentos = dialogView.findViewById(R.id.btnCancelaDocumentos);

                            if (GettersSetters.getDocCli().replace(".", "").replace("/", "").replace("-", "").length() == 14) {
                                btnRGNovoCli.setVisibility(View.GONE);
                                btnDocNovoCli.setText("Certidão\nCNPJ");
                                rgOK = true;
                            } else {
                                btnDocNovoCli.setText("CPF");
                            }

                            btnRGNovoCli.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    GettersSetters.setTipoDoc("RG");
                                    Intent i = new Intent(ColetaCliente.this, Fotografar.class);
                                    startActivity(i);
                                }
                            });

                            btnDocNovoCli.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    GettersSetters.setTipoDoc("CPF/CNPJ");
                                    Intent i = new Intent(ColetaCliente.this, Fotografar.class);
                                    startActivity(i);
                                }
                            });

                            btnCompResidNovoCli.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    GettersSetters.setTipoDoc("COMPROV-RESID");
                                    Intent i = new Intent(ColetaCliente.this, Fotografar.class);
                                    startActivity(i);
                                }
                            });

                            btnConfirmarDocumentos.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (rgOK && cpfCnpjOk && comprResidOK) {
                                        dialogDocs.cancel();
                                        GettersSetters.setDocsNovoCliOK(true);
                                        Intent it = new Intent(ColetaCliente.this, ColetaBorracheiro.class);
                                        startActivity(it);
                                    } else {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(ColetaCliente.this);
                                        builder.setTitle("Documentos Incompletos");
                                        builder.setIcon(R.drawable.exclamation);
                                        builder.setCancelable(false);
                                        builder.setMessage("Verifique todos os documentos do Cliente " + GettersSetters.getRzSocialCli());
                                        builder.setPositiveButton("Fechar", null);
                                        AlertDialog dialog = builder.create();
                                        dialog.show();
                                    }
                                }
                            });

                            btnCancelaDocumentos.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(ColetaCliente.this);
                                    builder.setTitle("Cancelar Processo?");
                                    builder.setIcon(R.drawable.atencao);
                                    builder.setCancelable(false);
                                    builder.setMessage("ATENÇÃO: Todos os dados serão perdidos!");
                                    builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            File docs;
                                            /** DELETA OS ARQUIVOS DAS FOTOS DOS DOCS DE NOVOS CLIENTES **/
                                            docs = new File(GettersSetters.getPathDocsNovoCli(), GettersSetters.getPicNameRGCli());
                                            docs.delete();

                                            docs = new File(GettersSetters.getPathDocsNovoCli(), GettersSetters.getPicNameDocCli());
                                            docs.delete();

                                            docs = new File(GettersSetters.getPathDocsNovoCli(), GettersSetters.getPicNameComprResidCli());
                                            docs.delete();

                                            Intent it = new Intent(ColetaCliente.this, Home.class);
                                            startActivity(it);
                                            ColetaCliente.this.finish();
                                            GettersSetters.resetGettersSetters();
                                        }
                                    });
                                    builder.setNegativeButton("Não", null);
                                    AlertDialog dialog = builder.create();
                                    dialog.show();
                                }
                            });

                            dialogDocs = builderDocs.create();
                            dialogDocs.show();

                        } else {
                            Intent it = new Intent(ColetaCliente.this, ColetaBorracheiro.class);
                            startActivity(it);
                        }
                    }
                }
            }
        });

        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnAtualizaClientes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(ColetaCliente.this);
                builder.setCancelable(false);
                builder.setMessage("Deseja atualizar os Clientes?");
                builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        verificaClientes = new VerificaClientes();
                        verificaClientes.execute(true);
                    }
                });
                builder.setNegativeButton("Não", null);
                builder.show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(ColetaCliente.this);
        builder.setCancelable(false);
        builder.setMessage("Deseja cancelar a operação?\nTodas as informações serão perdidas!");
        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent it = new Intent(ColetaCliente.this, Home.class);
                startActivity(it);
                ColetaCliente.this.finish();
                GettersSetters.resetGettersSetters();
            }
        });
        builder.setNegativeButton("Não", null);
        builder.show();
    }

    /**
     * JSON Busca CEP
     **/
    @SuppressLint("StaticFieldLeak")
    private class ProcessJSONCEP extends AsyncTask<String, Void, String> {
        AlertDialog dialogBusca = null;

        @Override
        public void onPreExecute() {
            AlertDialog.Builder builder = new AlertDialog.Builder(ColetaCliente.this);
            LayoutInflater layoutInflater = getLayoutInflater();
            final View dialogView = layoutInflater.inflate(R.layout.dialog_carregamento, null);
            final TextView textView = dialogView.findViewById(R.id.txtInfoCarreg);
            textView.setText("Buscando CEP...\nAguarde...");
            builder.setCancelable(false);
            builder.setView(dialogView);
            dialogBusca = builder.create();
            dialogBusca.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            String stream;
            String urlString = strings[0];

            HTTPDataHandler hh = new HTTPDataHandler();
            stream = hh.GetHTTPData(urlString);

            return stream;
        }

        @SuppressLint("SetTextI18n")
        protected void onPostExecute(String stream) {
            if (stream != null) {
                try {
                    JSONObject reader = new JSONObject(stream);

                    final String logradouro = reader.getString("logradouro").toUpperCase();
                    final String complemento = reader.getString("complemento").toUpperCase();
                    final String bairro = reader.getString("bairro").toUpperCase();
                    final String localidade = reader.getString("localidade").toUpperCase();
                    final String uf = reader.getString("uf");

                    if (!logradouro.isEmpty()) {
                        cmpEndEntrega.setError(null);
                        cmpEndEntrega.setText(logradouro.trim());

                        cmpComplEntrega.setError(null);
                        cmpComplEntrega.setText(complemento.trim());
                    }
                    if (!bairro.isEmpty()) {
                        cmpBairroEntrega.setError(null);
                        cmpBairroEntrega.setText(bairro.trim());
                    }
                    if (!localidade.isEmpty()) {
                        cmpMunEntrega.setError(null);
                        cmpMunEntrega.setText(localidade.trim());
                    }

                    for (int i = 0; i < spnEstEntr.getAdapter().getCount(); i++) {
                        if (spnEstEntr.getAdapter().getItem(i).toString().contains(uf)) {
                            spnEstEntr.setSelection(i);
                            spnEstEntr.setEnabled(false);
                        }
                    }

                    cmpNumEntrega.setFocusable(true);
                    cmpNumEntrega.requestFocus();

                } catch (JSONException e) {
                    Snackbar.make(viewSnackBar, "CEP " + cepEntrada + " não localizado!", Snackbar.LENGTH_LONG).show();
                    cmpCepEntrega.setText("00000000");

                    cmpCepEntrega.getText().clear();
                    cmpEndEntrega.getText().clear();
                    cmpBairroEntrega.getText().clear();
                    cmpMunEntrega.getText().clear();
                    cmpComplEntrega.getText().clear();
                    cmpNumEntrega.getText().clear();
                }
            }

            cmpEndEntrega.setEnabled(true);
            cmpBairroEntrega.setEnabled(true);
            cmpMunEntrega.setEnabled(true);
            spnEstEntr.setEnabled(true);
            cmpEndCobr.setEnabled(true);
            cmpBairroCobr.setEnabled(true);
            cmpMunCobr.setEnabled(true);
            spnEstCobr.setEnabled(true);
            cmpNumCobr.setEnabled(true);
            cmpNumEntrega.setEnabled(true);
            cmpComplCobr.setEnabled(true);
            cmpComplEntrega.setEnabled(true);

            if (dialogBusca != null) {
                dialogBusca.dismiss();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class ProcessJSONCEPCob extends AsyncTask<String, Void, String> {
        AlertDialog dialogBusca = null;

        @Override
        public void onPreExecute() {
            AlertDialog.Builder builder = new AlertDialog.Builder(ColetaCliente.this);
            LayoutInflater layoutInflater = getLayoutInflater();
            final View dialogView = layoutInflater.inflate(R.layout.dialog_carregamento, null);
            final TextView textView = dialogView.findViewById(R.id.txtInfoCarreg);
            textView.setText("Buscando CEP...\nAguarde...");
            builder.setCancelable(false);
            builder.setView(dialogView);
            dialogBusca = builder.create();
            dialogBusca.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            String stream;
            String urlString = strings[0];

            HTTPDataHandler hh = new HTTPDataHandler();
            stream = hh.GetHTTPData(urlString);

            return stream;
        }

        @SuppressLint("SetTextI18n")
        protected void onPostExecute(String stream) {
            if (stream != null) {
                try {
                    JSONObject reader = new JSONObject(stream);

                    final String logradouro = reader.getString("logradouro").toUpperCase();
                    final String complemento = reader.getString("complemento").toUpperCase();
                    final String bairro = reader.getString("bairro").toUpperCase();
                    final String localidade = reader.getString("localidade").toUpperCase();
                    final String uf = reader.getString("uf");

                    if (!logradouro.isEmpty()) {
                        cmpEndCobr.setError(null);
                        cmpEndCobr.setText(logradouro.trim());

                        cmpComplCobr.setError(null);
                        cmpComplCobr.setText(complemento.trim());
                    }

                    if (!bairro.isEmpty()) {
                        cmpBairroCobr.setError(null);
                        cmpBairroCobr.setText(bairro.trim());
                    }

                    if (!localidade.isEmpty()) {
                        cmpMunCobr.setError(null);
                        cmpMunCobr.setText(localidade.trim());
                    }

                    for (int i = 0; i < spnEstCobr.getAdapter().getCount(); i++) {
                        if (spnEstCobr.getAdapter().getItem(i).toString().contains(uf)) {
                            spnEstCobr.setSelection(i);
                        }
                    }

                    cmpNumCobr.setFocusable(true);
                    cmpNumCobr.requestFocus();

                } catch (JSONException e) {
                    Snackbar.make(viewSnackBar, "CEP " + cepEntrada + " não localizado!", Snackbar.LENGTH_LONG).show();
                    cmpCepCobr.setText("00000000");

                    cmpCepCobr.getText().clear();
                    cmpEndCobr.getText().clear();
                    cmpBairroCobr.getText().clear();
                    cmpMunCobr.getText().clear();
                    cmpComplCobr.getText().clear();
                    cmpNumCobr.getText().clear();
                }
            }

            cmpEndEntrega.setEnabled(true);
            cmpBairroEntrega.setEnabled(true);
            cmpMunEntrega.setEnabled(true);
            spnEstEntr.setEnabled(true);
            cmpEndCobr.setEnabled(true);
            cmpBairroCobr.setEnabled(true);
            cmpMunCobr.setEnabled(true);
            spnEstCobr.setEnabled(true);

            if (dialogBusca != null) {
                dialogBusca.dismiss();
            }
        }
    }

    /**
     * JSON Busca CNPJ
     **/
    @SuppressLint("StaticFieldLeak")
    private class ProcessJSONCNPJ extends AsyncTask<String, Void, String> {
        AlertDialog dialogBuscaCNPJ = null;

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPreExecute() {
            AlertDialog.Builder builder = new AlertDialog.Builder(ColetaCliente.this);
            LayoutInflater layoutInflater = getLayoutInflater();
            final View dialogView = layoutInflater.inflate(R.layout.dialog_carregamento, null);
            final TextView textView = dialogView.findViewById(R.id.txtInfoCarreg);
            textView.setText("Buscando CNPJ...\nAguarde...");
            builder.setCancelable(false);
            builder.setView(dialogView);
            dialogBuscaCNPJ = builder.create();
            dialogBuscaCNPJ.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            String stream = null;
            String urlString = strings[0];

            if (CheckConnection.isConnected(ColetaCliente.this)) {
                HTTPDataHandler hh = new HTTPDataHandler();
                stream = hh.GetHTTPData(urlString);
            }

            return stream;
        }

        @SuppressLint("SetTextI18n")
        protected void onPostExecute(String stream) {
            if (stream != null) {
                try {
                    JSONObject reader = new JSONObject(stream);

                    final String cnpj = reader.getString("cnpj").replace(".", "").replace("/", "").replace("-", "");
                    final String nome = reader.getString("nome").toUpperCase();
                    final String telefone = reader.getString("telefone").replace(" ", "").replace("-", "").replace("(", "").replace(")", "").replace("/", "");
                    final String email = reader.getString("email");
                    final String situacao = reader.getString("situacao");
                    final String cep = reader.getString("cep").replace(".", "").replace("-", "");
                    final String logradouro = reader.getString("logradouro").toUpperCase();
                    final String complemento = reader.getString("complemento").toUpperCase();
                    final String numero = reader.getString("numero");
                    final String bairro = reader.getString("bairro").toUpperCase();
                    final String municipio = reader.getString("municipio").toUpperCase();
                    final String uf = reader.getString("uf");

                    if (situacao.contains("ATIVA")) {
                        GettersSetters.setCodCli(cnpj.substring(0, 8)); // Código do cliente conforme o CNPJ
                        GettersSetters.setLojaCli(cnpj.substring(8, 12)); // Loja do cliente conforme o CNPJ

                        if (!logradouro.isEmpty()) {
                            cmpEndEntrega.setError(null);
                            cmpEndEntrega.setText(logradouro.trim());

                            cmpComplEntrega.setError(null);
                            cmpComplEntrega.setText(complemento.trim());

                            cmpNumEntrega.setError(null);
                            cmpNumEntrega.setText(numero.trim());
                        }

                        if (!bairro.isEmpty()) {
                            cmpBairroEntrega.setError(null);
                            cmpBairroEntrega.setText(bairro.trim());
                        }

                        if (!municipio.isEmpty()) {
                            cmpMunEntrega.setError(null);
                            cmpMunEntrega.setText(municipio.trim());
                        }

                        if (!nome.isEmpty()) {
                            cmpMunEntrega.setError(null);
                            cmpRzSocial.setText(nome.trim());
                        }

                        if (!email.isEmpty()) {
                            cmpEmail.setError(null);
                            cmpEmail.setText(email.trim());
                        }

                        if (!telefone.isEmpty()) {
                            cmpFoneEntrega.setError(null);
                            cmpDDDEntrega.setError(null);
                            cmpFoneEntrega.setText(telefone.substring(2));
                            cmpDDDEntrega.setText(telefone.substring(0,2));
                        }

                        if (!cep.isEmpty()) {
                            cmpCepEntrega.setError(null);
                            cmpCepEntrega.setText(cep);
                        }

                        for (int i = 0; i < spnEstEntr.getAdapter().getCount(); i++) {
                            if (spnEstEntr.getAdapter().getItem(i).toString().contains(uf)) {
                                spnEstEntr.setSelection(i);
                            }
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Situação do CNPJ " + documentoClienteSemFormatacao + " é " + situacao, Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    AlertDialog.Builder builderCliCNPJ;
                    try {
                        JSONObject reader = new JSONObject(stream);
                        final String message = reader.getString("message");

                        builderCliCNPJ = new AlertDialog.Builder(ColetaCliente.this);
                        builderCliCNPJ.setTitle("Retorno CNPJ");
                        builderCliCNPJ.setCancelable(false);
                        builderCliCNPJ.setMessage(message + "\nPreencha os dados manualmente.");
                        builderCliCNPJ.setPositiveButton("OK", null);
                        AlertDialog dialogCli = builderCliCNPJ.create();
                        dialogCli.show();

                        dialogCli.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialogInterface) {
                                limpaCampos();
                                habCamposDadosCli();
                            }
                        });

                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }
                }
            } else {
                Toast.makeText(getApplicationContext(), "Preencha os dados manualmente!", Toast.LENGTH_LONG).show();
                limpaCampos();
                habCamposDadosCli();
            }

            if (dialogBuscaCNPJ != null) {
                dialogBuscaCNPJ.dismiss();

                if (!dialogBuscaCNPJ.isShowing()) {
                    cmpCepCobr.addTextChangedListener(textWatcherCepCobr);

                    /** CATEGORIA DO CLIENTE **/
                    if (documentoClienteSemFormatacao.length() == 14) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ColetaCliente.this);
                        builder.setTitle("[CLIENTE NOVO] Tipo do cliente");
                        builder.setIcon(R.drawable.exclamation);
                        builder.setCancelable(false);
                        builder.setMessage("Este cliente é Órgão Público?");
                        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                GettersSetters.setCategoriaCli("01");
                                dialog.dismiss();
                            }
                        });
                        builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                GettersSetters.setCategoriaCli("09");
                                dialog.dismiss();
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    } else {
                        GettersSetters.setCategoriaCli("09");
                    }
                }
            }
        }
    }

    // JSON Busca Municipio
//    @SuppressLint("StaticFieldLeak")
//    private class ProcessJSONMunicipio extends AsyncTask<String, Void, String> {
//        @Override
//        protected String doInBackground(String... strings) {
//            String stream = null;
//            String urlString = strings[0];
//
//            if (CheckConnection.isConnected(ColetaCliente.this)) {
//                HTTPDataHandler hh = new HTTPDataHandler();
//                stream = hh.GetHTTPData(urlString);
//            }
//            return stream;
//        }
//
//        protected void onPostExecute(String stream) {
//            if (stream != null) {
//                try {
//                    if (GettersSetters.getValidaConexaoBD()) {
//                        JSONArray jsonMun = new JSONArray(stream);
//                        arrMunicipios.clear();
//
//                        for (int i = 0; i < jsonMun.length(); i++) {
//                            JSONObject arrayMun = jsonMun.getJSONObject(i);
//                            arrMunicipios.add(arrayMun.getString("nome"));
//                        }
//
//                        try {
//                            Cursor resMunicipio = db.buscaMunicipios(estadoSelecionado);
//                            if (resMunicipio.getCount() == 0) {
//                                try {
//                                    boolean resultado = false;
//                                    for (int i = 0; i < arrMunicipios.size(); i++) {
//                                        String municipio = arrMunicipios.get(i);
//                                        resultado = db.insereMunicipioEstado(estadoSelecionado, municipio);
//                                    }
//                                    if (resultado) {
//                                        Toast.makeText(ColetaCliente.this, "Municipios/" + estadoSelecionado + " inseridos para uso offline!", Toast.LENGTH_LONG).show();
//                                    } else {
//                                        Toast.makeText(ColetaCliente.this, "Municipios/" + estadoSelecionado + " NÃO inseridos para uso offline. " + stream, Toast.LENGTH_LONG).show();
//                                    }
//                                } catch (Exception e) {
//                                    Toast.makeText(ColetaCliente.this, "Erro inserção Municipios/" + estadoSelecionado + ": " + e, Toast.LENGTH_LONG).show();
//                                }
//                            }
//
//                        } catch (Exception e) {
//                            Toast.makeText(ColetaCliente.this, "Erro busca Municipios/" + estadoSelecionado + ": " + e, Toast.LENGTH_LONG).show();
//                        }
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                    Toast.makeText(getApplicationContext(), "Municípios não localizados!", Toast.LENGTH_LONG).show();
//                }
//
//            } else {
//                arrMunicipios.clear();
//                db.buscaMunicipios(estadoSelecionado);
//                Collections.sort(db.arrMunEst);
//                arrMunicipios.addAll(db.arrMunEst);
//            }
//        }
//    }

    /**
     * Função para quando virar a tela não resetar o layout
     **/
    @Override
    public void onConfigurationChanged(@NotNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public void limpaCampos() {
        conecta.codigoCliente = "";
        conecta.lojaCliente = "";
        cmpRzSocial.getText().clear();
        cmpIE.getText().clear();
        cmpEmail.getText().clear();
        cmpEmail.getText().clear();
        cmpEndEntrega.getText().clear();
        cmpBairroEntrega.getText().clear();
        cmpMunEntrega.getText().clear();
        cmpCepEntrega.getText().clear();
        cmpDDDEntrega.getText().clear();
        cmpFoneEntrega.getText().clear();
        cmpCepCobr.getText().clear();
        cmpEndCobr.getText().clear();
        cmpBairroCobr.getText().clear();
        cmpMunCobr.getText().clear();
        cmpDDDCobr.getText().clear();
        cmpFoneCobr.getText().clear();
    }

    public void habCamposDadosCli() {
        cmpDoc.setEnabled(true);
        cmpRzSocial.setEnabled(true);
        cmpIE.setEnabled(true);
        cmpEmail.setEnabled(true);
        cmpCepEntrega.setEnabled(true);
        cmpEndEntrega.setEnabled(true);
        cmpBairroEntrega.setEnabled(true);
        cmpMunEntrega.setEnabled(true);
        cmpCepEntrega.setEnabled(true);
        cmpDDDEntrega.setEnabled(true);
        cmpFoneEntrega.setEnabled(true);
        spnEstEntr.setEnabled(true);
        cmpCepCobr.setEnabled(true);
        cmpEndCobr.setEnabled(true);
        cmpBairroCobr.setEnabled(true);
        cmpMunCobr.setEnabled(true);
        cmpCepCobr.setEnabled(true);
        cmpDDDCobr.setEnabled(true);
        cmpFoneCobr.setEnabled(true);
        spnEstCobr.setEnabled(true);
        cmpNumCobr.setEnabled(true);
        cmpNumEntrega.setEnabled(true);
        cmpComplCobr.setEnabled(true);
        cmpComplEntrega.setEnabled(true);
    }

    public void desabCamposDadosCli() {
        cmpDoc.setEnabled(false);
        cmpRzSocial.setEnabled(false);
        cmpIE.setEnabled(false);
        cmpEmail.setEnabled(false);
        cmpCepEntrega.setEnabled(false);
        cmpEndEntrega.setEnabled(false);
        cmpBairroEntrega.setEnabled(false);
        cmpMunEntrega.setEnabled(false);
        cmpCepEntrega.setEnabled(false);
        cmpDDDEntrega.setEnabled(false);
        cmpFoneEntrega.setEnabled(false);
        spnEstEntr.setEnabled(false);
        cmpCepCobr.setEnabled(false);
        cmpEndCobr.setEnabled(false);
        cmpBairroCobr.setEnabled(false);
        cmpMunCobr.setEnabled(false);
        cmpCepCobr.setEnabled(false);
        cmpDDDCobr.setEnabled(false);
        cmpFoneCobr.setEnabled(false);
        spnEstCobr.setEnabled(false);
        spnEstEntr.setEnabled(false);
        spnEstCobr.setEnabled(false);
        cmpNumCobr.setEnabled(false);
        cmpNumEntrega.setEnabled(false);
        cmpComplCobr.setEnabled(false);
        cmpComplEntrega.setEnabled(false);
    }

    /**
     * INSERÇÃO DOS DADOS NO BANCO DE DADOS LOCAL E CARREGAMENTO DE DADOS
     **/
    @SuppressLint("StaticFieldLeak")
    private class CarregamentoInicial extends AsyncTask<Boolean, String, Boolean> {
        AlertDialog dialogProcessoInicial = null;
        boolean retorno = false;
        FuncoesGenericas funcoesGenericas = new FuncoesGenericas();

        @Override
        protected void onPreExecute() {
            arrAdapterFilial.setNotifyOnChange(false);

            AlertDialog.Builder builder = new AlertDialog.Builder(ColetaCliente.this);
            LayoutInflater layoutInflater = getLayoutInflater();
            final View dialogView = layoutInflater.inflate(R.layout.dialog_carregamento, null);
            builder.setCancelable(false);
            builder.setPositiveButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    carregamentoInicial.cancel(true);
                    Intent it = new Intent(ColetaCliente.this, Home.class);
                    startActivity(it);
                    ColetaCliente.this.finish();
                    GettersSetters.resetGettersSetters();
                }
            });
            builder.setView(dialogView);
            dialogProcessoInicial = builder.create();
            dialogProcessoInicial.show();
        }

        @SuppressLint("DefaultLocale")
        @Override
        protected Boolean doInBackground(Boolean... params) {
            /* GERAÇÃO DO NÚMERO DA COLETA */
            if (!funcoesGenericas.geraNumeroColeta(ColetaCliente.this, false, conecta, new ConexaoBDInt(ColetaCliente.this)).trim().equals("")) {
                /** BUSCA DAS FILIAIS **/
                try {
                    arrFiliais.clear();

                    //Desabilito a notificao por enquanto, ate terminar de adicionar tudo
                    arrAdapterFilial.setNotifyOnChange(false);
                    if (GettersSetters.getTipoUsuario().equals("P") || GettersSetters.getTipoUsuario().equals("V") || GettersSetters.getTipoUsuario().equals("T")) {
                        db.buscaFiliaisUsuarios(GettersSetters.getCodigoVendedor());
                        arrFiliais.addAll(db.arrDadosFiliaisUsuarios);
                    } else {
                        Cursor resFiliais = db.buscaFilais();
                        if (resFiliais.getCount() > 0) {
                            if (resFiliais.moveToFirst()) {
                                do {
                                    arrFiliais.add(resFiliais.getString(3) + " / " + resFiliais.getString(2));
                                } while (resFiliais.moveToNext());
                            }
                        }
                    }
                    retorno = true;
                } catch (final Exception e) {
                    e.printStackTrace();
                    GettersSetters.setErroEnvioColetaBDExt(e.getMessage());
//                    Snackbar.make(viewSnackBar, "Erro filiais: " + e, Snackbar.LENGTH_LONG).show();
                    retorno = false;
                }
            } else {
                retorno = false;
            }
            return retorno;
        }

        @Override
        protected void onPostExecute(Boolean retorno) {
            if (dialogProcessoInicial != null) {
                dialogProcessoInicial.dismiss();
            }

            if (retorno) {
                /** PREENCHE DATA **/
                try {
                    cmpDataIni = findViewById(R.id.cmpDtIni);
                    cmpDataIni.setText(GettersSetters.getDataBR());
                    cmpDataIni.setEnabled(false);
                    GettersSetters.setDataColetaBR(cmpDataIni.getText().toString().trim()); //data atual

                    /** CONVERSÃO DE DATA PARA FORMATO AMERICANO */
                    GettersSetters.setDataColetaEN(GettersSetters.getDataEN());
                } catch (final Exception err) {
                    Snackbar.make(viewSnackBar, "Erro de Data. Reinicie a rotina!\n" + err, Snackbar.LENGTH_LONG).setAction("Reiniciar", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            GettersSetters.resetGettersSetters();
                            Intent it = new Intent(ColetaCliente.this, Home.class);
                            startActivity(it);
                            ColetaCliente.this.finish();
                        }
                    }).show();
                    cmpDataIni.setEnabled(true);
                    GettersSetters.setErroEnvioColetaBDExt(err.getMessage());
                }

                arrAdapterFilial.setNotifyOnChange(true);

                spnFilial.setOnTouchListener(new View.OnTouchListener() {
                    @SuppressLint("ClickableViewAccessibility")
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        arrAdapterFilial.notifyDataSetChanged();
                        return false;
                    }
                });

                if (arrFiliais.size() == 0 || spnFilial.getAdapter().isEmpty()) {
                    AlertDialog.Builder builderFilial = new AlertDialog.Builder(ColetaCliente.this);
                    builderFilial.setTitle("Sem Filial!");
                    builderFilial.setIcon(R.drawable.exclamation);
                    builderFilial.setCancelable(true);
                    builderFilial.setMessage("Filiais não vinculadas ao Vendedor.\nSolicite ao Administrador este vínculo!");
                    builderFilial.setNegativeButton("Fechar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            GettersSetters.resetGettersSetters();
                            Intent it = new Intent(ColetaCliente.this, Home.class);
                            it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(it);
                        }
                    });
                    AlertDialog dialog = builderFilial.create();
                    dialog.show();
                }
            } else {
                AlertDialog.Builder builderFilial = new AlertDialog.Builder(ColetaCliente.this);
                builderFilial.setTitle("Erro na inicialização da Coleta!");
                builderFilial.setIcon(R.drawable.error);
                builderFilial.setCancelable(true);
                builderFilial.setMessage(GettersSetters.getErroEnvioColetaBDExt());
                builderFilial.setNegativeButton("Encerrar App", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Thread.currentThread().interrupt();
                        GettersSetters.resetGettersSetters();
                        finish();
                        finishAffinity();
                        System.exit(0);
                        android.os.Process.killProcess(android.os.Process.myPid());
                        funcoesGenericas.clearCache(getBaseContext());
                        onDestroy();
                    }
                });
                AlertDialog dialog = builderFilial.create();
                dialog.show();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class VerificaClientes extends AsyncTask<Boolean, Integer, Boolean> {
        Boolean sucesso;

        @Override
        protected void onPreExecute() {
            mProgressDialog = new ProgressDialog(ColetaCliente.this);
            mProgressDialog.setIcon(R.drawable.ampulheta);
            mProgressDialog.setTitle("Clientes");
            mProgressDialog.setMessage("Atualizando Clientes... Aguarde...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMax(100);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }

        @SuppressLint({"WrongThread", "ApplySharedPref"})
        @Override
        protected Boolean doInBackground(Boolean... params) {
            /** ATUALIZA CLIENTES LOCALMENTE - PARA UTILIZAR DURANTE AS COLETAS ONLINE E OFFLINE **/
            if (CheckConnection.isConnected(ColetaCliente.this)) {
                /** ATUALIZAÇÃO DE CLIENTES **/
                if (params[0]) {
                    if (conecta.selectionaEInsereClientesLocal(ColetaCliente.this, 1)) {
                        SharedPreferences.Editor editor = getSharedPreferences(Home.ATUALIZACAO_DADOS, MODE_PRIVATE).edit();
                        editor.putString("clientes", conecta.selecionaParametroAtualizaDados("CLIENTES"));
                        editor.commit();

                        conecta.updDadosAuditApp(GettersSetters.getIdUsuarioLogado(), "DT_ATT_CLIENTES_COL", GettersSetters.getDataEN() + " - " + GettersSetters.getHora());
                        sucesso = true;
                    } else {
                        Snackbar.make(viewSnackBar, "Erro ao atualizar os clientes. Tente novamente!", Snackbar.LENGTH_LONG).show();
                        sucesso = false;
                    }
                }
            }
            return sucesso;
        }

        @Override
        protected void onPostExecute(Boolean sucesso) {
            if (sucesso) {
                conecta.updDadosAuditApp(GettersSetters.getIdUsuarioLogado(), "DT_ATT_CLIENTES_COL", GettersSetters.getDataEN() + " - " + GettersSetters.getHora());

                btnAtualizaClientes.setVisibility(View.GONE);
            }
            mProgressDialog.dismiss();
        }
    }

    /**
     * EXIBE TECLADO
     **/
    public void mostraTeclado() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    @SuppressWarnings("rawtypes")
    @SuppressLint("StaticFieldLeak")
    public class BuscaCli extends AsyncTask<ArrayList, String, Integer> {
        AlertDialog dialogBuscaCli = null;

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPreExecute() {
            AlertDialog.Builder builder = new AlertDialog.Builder(ColetaCliente.this);
            LayoutInflater layoutInflater = getLayoutInflater();
            final View dialogView = layoutInflater.inflate(R.layout.dialog_carregamento, null);
            final TextView textView = dialogView.findViewById(R.id.txtInfoCarreg);
            textView.setText("Buscando Clientes...\nAguarde...");
            builder.setCancelable(false);
            builder.setView(dialogView);
            dialogBuscaCli = builder.create();
            dialogBuscaCli.show();
        }

        @Override
        protected Integer doInBackground(ArrayList... params) {
            return db.buscaClientesColeta(documentoClienteSemFormatacao, nomeCli.trim(), "", GettersSetters.getArrClientesPermitidos(), "","").getCount();
        }

        @SuppressLint("UseCompatLoadingForDrawables")
        @Override
        protected void onPostExecute(Integer itens) {
            if (dialogBuscaCli != null) {
                dialogBuscaCli.dismiss();
            }

            arrNomeCliente.clear();
            arrDocCliente.clear();
            arrMunCliente.clear();
            arrEstCliente.clear();
            arrIECliente.clear();
            arrRecnoCliente.clear();

            if (itens > 0) {
                arrNomeCliente.addAll(db.arrNomeCliente);
                arrDocCliente.addAll(db.arrDocCliente);
                arrMunCliente.addAll(db.arrMunCliente);
                arrEstCliente.addAll(db.arrEstCliente);
                arrIECliente.addAll(db.arrIECliente);
                arrRecnoCliente.addAll(db.arrRecnoCliente);

                String[] adapterDadosCli = new String[arrNomeCliente.size()];
                final String[] adapterRecnoCli = new String[arrRecnoCliente.size()];
                for (int i = 0; i < arrNomeCliente.size(); i++) {
                    String documento = arrDocCliente.get(i);
                    if (documento.trim().length() == 14) {
                        documento = documento.replaceAll("([0-9]{2})([0-9]{3})([0-9]{3})([0-9]{4})([0-9]{2})", "$1\\.$2\\.$3/$4-$5");
                    } else {
                        documento = documento.replaceAll("([0-9]{3})([0-9]{3})([0-9]{3})([0-9]{2})", "$1\\.$2\\.$3-$4");
                    }
                    /** ADIÇÃO DE DADOS **/
                    adapterDadosCli[i] = arrNomeCliente.get(i) + "\n" + documento + "\n" + arrMunCliente.get(i) + "/" + arrEstCliente.get(i) + "\n" + "IE: " + arrIECliente.get(i);
                    adapterRecnoCli[i] = arrRecnoCliente.get(i);
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(ColetaCliente.this);
                builder.setTitle("Selecione o Cliente");
                builder.setCancelable(true);
                final ArrayAdapter<String> adapterNomeCli = new ArrayAdapter<String>(ColetaCliente.this, R.layout.text_view_item_high, adapterDadosCli) {
                    @Override
                    public View getView(int position, View convertView, @NotNull ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);

                        if (((TextView) view).getText().toString().trim().contains("*** ")) {
                            ((TextView) view).setTextColor(Color.parseColor("#FF0000"));
                            cmpRzSocial.setTextColor(Color.parseColor("#FF0000"));
                        } else {
                            ((TextView) view).setTextColor(Color.parseColor("#000000"));
                            cmpRzSocial.setTextColor(Color.parseColor("#000000"));
                        }

                        ((TextView) view).setTextSize(15);

                        return view;
                    }
                };

                builder.setAdapter(adapterNomeCli, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int itemSelecionado) {
                        String[] arrCliSelecionado = Objects.requireNonNull(adapterNomeCli.getItem(itemSelecionado)).split("\n");
                        cmpRzSocial.setText(arrCliSelecionado[0].trim());

                        documentoClienteComFormatacao = arrCliSelecionado[1];
                        documentoClienteSemFormatacao = arrCliSelecionado[1].replace(".", "").replace("/", "").replace("-", "");

                        documentoOcultoCliente = new StringBuilder(arrCliSelecionado[1].trim());
                        for (int i = 3; i <= (arrCliSelecionado[1].length() - 2); i++) {
                            documentoOcultoCliente.setCharAt(i, '*');
                        }

                        cmpDoc.setText(documentoOcultoCliente.toString()); //(arrCliSelecionado[1].trim());
                        btnVisualizarDoc.setVisibility(View.VISIBLE);
                        cmpRzSocial.setError(null);
                        cmpDoc.setError(null);

                        nomeCli = arrCliSelecionado[0];
                        isDocValid = ValidaCPFeCNPJ.isDocValid(documentoClienteSemFormatacao);
                        recnoCli = adapterRecnoCli[itemSelecionado];

                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();

                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        if (nomeCli.equals("") || documentoClienteSemFormatacao.equals("")) {
                            limpaCampos();
                            cmpDoc.getText().clear();
                        } else {
                            selecionaDadosCliente(documentoClienteSemFormatacao, nomeCli, isDocValid, recnoCli);
                        }
                    }
                });
            } else {
                if (GettersSetters.getArrClientesPermitidos().size() == 0) {
                    GettersSetters.setValidarDadosCli(true);
                    GettersSetters.setDocsNovoCliOK(false);

                    GettersSetters.setClienteGrp(false);
                    GettersSetters.setDescGrupo(0);
                    GettersSetters.setDescExclusivo(0);
                    GettersSetters.setTabelaPrcCli("");

                    codCli = "";
                    lojaCli = "";

                    llRefComerc.setVisibility(View.VISIBLE);
                    cmpNumEntrega.setVisibility(View.VISIBLE);
                    cmpNumCobr.setVisibility(View.VISIBLE);
                    cmpComplEntrega.setVisibility(View.VISIBLE);
                    cmpComplCobr.setVisibility(View.VISIBLE);

                    cmpCepCobr.addTextChangedListener(textWatcherCepCobr);

                    limpaCampos();
                    habCamposDadosCli();

                    if (documentoClienteSemFormatacao.length() == 14) {
                        AlertDialog.Builder builderCNPJ = new AlertDialog.Builder(ColetaCliente.this);
                        builderCNPJ.setCancelable(false);
                        builderCNPJ.setTitle("Busca de CNPJ ReceitaWS");
                        builderCNPJ.setMessage("O CNPJ " + documentoClienteSemFormatacao.trim() + " não foi localizado na base de dados da Empresa e será realizada a busca dos dados na ReceitaWS.");
                        builderCNPJ.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                urlCNPJ = "https://www.receitaws.com.br/v1/cnpj/" + documentoClienteSemFormatacao;
                                new ProcessJSONCNPJ().execute(urlCNPJ);
                            }
                        });
                        builderCNPJ.show();
                    } else {
                        if (CheckConnection.isConnected(ColetaCliente.this)) {
                            cmpEndEntrega.setEnabled(false);
                            cmpBairroEntrega.setEnabled(false);
                            cmpMunEntrega.setEnabled(false);
                            spnEstEntr.setEnabled(false);
                            cmpEndCobr.setEnabled(false);
                            cmpBairroCobr.setEnabled(false);
                            cmpMunCobr.setEnabled(false);
                            spnEstCobr.setEnabled(false);
                            cmpNumCobr.setEnabled(false);
                            cmpNumEntrega.setEnabled(false);
                            cmpComplCobr.setEnabled(false);
                            cmpComplEntrega.setEnabled(false);
                        }

                        AlertDialog.Builder builderCli = new AlertDialog.Builder(ColetaCliente.this);
                        builderCli.setTitle("Cliente não localizado!");
                        builderCli.setCancelable(false);
                        builderCli.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.error, null));
                        builderCli.setMessage("Cliente não localizado!\nVerifique os dados ou preencha-os manualmente!");
                        builderCli.setPositiveButton("OK", null);
                        AlertDialog dialogCli = builderCli.create();
                        dialogCli.show();

                        dialogCli.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialogInterface) {
//                                cmpCepCobr.addTextChangedListener(textWatcherCepCobr);

                                /** CATEGORIA DO CLIENTE **/
                                if (documentoClienteSemFormatacao.length() == 14) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(ColetaCliente.this);
                                    builder.setTitle("[CLIENTE NOVO] Tipo do cliente");
                                    builder.setIcon(R.drawable.exclamation);
                                    builder.setCancelable(false);
                                    builder.setMessage("Este cliente é Órgão Público?");
                                    builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            GettersSetters.setCategoriaCli("01");
                                            dialog.dismiss();
                                        }
                                    });
                                    builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            cmpIE.setText("ISENTO");
                                            GettersSetters.setCategoriaCli("09");
                                            dialog.dismiss();
                                        }
                                    });
                                    AlertDialog dialog = builder.create();
                                    dialog.show();
                                } else if (documentoClienteSemFormatacao.length() == 11) {
                                    cmpIE.setText("ISENTO");
                                    GettersSetters.setCategoriaCli("09");
                                }
                            }
                        });
                    }
                } else {
                    AlertDialog.Builder builderCli = new AlertDialog.Builder(ColetaCliente.this);
                    builderCli.setCancelable(false);
                    builderCli.setTitle("Cliente não localizado!");
                    builderCli.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.error, null));
                    builderCli.setMessage("Solicite permissão de busca para novos clientes ao Gerente!");
                    builderCli.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            cmpDoc.getText().clear();
                            cmpRzSocial.getText().clear();
                        }
                    });
                    AlertDialog dialogCli = builderCli.create();
                    dialogCli.show();
                }
            }
        }
    }

    public void selecionaDadosCliente(final String docCli, String nomeCli, boolean isDocValid, String recno) {
        if (isDocValid) {
            try {
                if (docCli != null && nomeCli == null) {
                    db.buscaClientesColeta(docCli.trim(), null, recno, GettersSetters.getArrClientesPermitidos(), "","");
                } else if (docCli == null && nomeCli != null) {
                    db.buscaClientesColeta(null, nomeCli.replace("*** ", "").trim(), recno, GettersSetters.getArrClientesPermitidos(), "","");
                } else {
                    assert docCli != null;
                    db.buscaClientesColeta(docCli.trim(), nomeCli.replace("*** ", "").trim(), recno, GettersSetters.getArrClientesPermitidos(), "","");
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (!db.nomeCli.equals("")) {
                    GettersSetters.setValidarDadosCli(false);
                    GettersSetters.setDocsNovoCliOK(true);

                    try {
                        nomeCli = db.nomeCli;
                        endCli = db.endCli;
                        bairroCli = db.bairroCli;
                        cepCli = db.cepCli;
                        emailCli = db.emailCli;
                        munCli = db.munCli;
                        ufCli = db.ufCli;
                        dddCli = db.dddCli;
                        foneCli = db.foneCli;
                        codCli = db.codCli;
                        lojaCli = db.lojaCli;
                        ieCli = db.ieCli;
                        endCobCli = db.endCobCli;
                        bairroCobCli = db.bairroCobCli;
                        cepCobCli = db.cepCobCli;
                        munCobCli = db.munCobCli;
                        ufCobCli = db.ufCobCli;
                        ddd2Cli = db.ddd2Cli;
                        teleXCli = db.foneCli;
                        cliBlq = db.cliBlq;
                        condPgtoCli = db.condPgtoCli;
                        catCli = db.catCli;
                        tabPrcCli = db.tabPrcCli;
                        clienteGrp = db.clienteGrp;
                        descExclusivo = db.descExclusivo;
                        codBorr = db.codBorr;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (nomeCli.contains("*** ")) {
                        final AlertDialog.Builder builder = new AlertDialog.Builder(ColetaCliente.this);
                        builder.setCancelable(false);
                        builder.setTitle("Atenção!");
                        builder.setMessage("Cliente [" + nomeCli.replace("*** ", "") + "] está BLOQUEADO. Após a coleta os dados serão analisados pela Matriz!");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                llRefComerc.setVisibility(View.VISIBLE);
                                dialog.dismiss();
                            }
                        });
                        builder.show();
                    }
                    GettersSetters.setCodCli(codCli);
                    GettersSetters.setLojaCli(lojaCli);

                    GettersSetters.setTabelaPrcCli(tabPrcCli);
                    GettersSetters.setCategoriaCli(catCli);
                    GettersSetters.setCondPgto(condPgtoCli);

                    GettersSetters.setClienteGrp(clienteGrp);
                    if (GettersSetters.isClienteGrp()) {
                        if (CheckConnection.isConnected(ColetaCliente.this)) {
                            GettersSetters.setDescGrupo(Double.parseDouble(conecta.selecionaParametro("VK_PDESCGR").trim()));
                        } else {
                            GettersSetters.setDescGrupo(Double.parseDouble(db.selecionaParametro("VK_PDESCGR").trim()));
                        }
                    }
                    GettersSetters.setDescExclusivo(descExclusivo);

                    //ALTERAÇÃO CMD 959
                    if (codBorr != null) {
                        if (!codBorr.equals("")) {
                            GettersSetters.setCodBorracheiro(codBorr);
                            GettersSetters.setIsBorracheiro(true);
                            GettersSetters.setIsBorracheiroCli(true);
                        }
                    }

                    cmpRzSocial.setText(nomeCli.replace("*** ", ""));
                    cmpIE.setText(ieCli);
                    cmpEmail.setText(emailCli);
                    cmpEndEntrega.setText(endCli);
                    cmpBairroEntrega.setText(bairroCli);
                    cmpMunEntrega.setText(munCli);
                    cmpCepEntrega.setText(cepCli);
                    cmpDDDEntrega.setText(dddCli);
                    cmpFoneEntrega.setText(foneCli);
                    cmpCepCobr.setText(cepCobCli);
                    cmpEndCobr.setText(endCobCli);
                    cmpBairroCobr.setText(bairroCobCli);
                    cmpDDDCobr.setText(ddd2Cli);
                    cmpFoneCobr.setText(teleXCli);
                    cmpMunCobr.setText(munCobCli);

                    for (int i = 0; i < spnEstEntr.getAdapter().getCount(); i++) {
                        if (spnEstEntr.getAdapter().getItem(i).toString().contains(ufCli)) {
                            spnEstEntr.setSelection(i);
                            spnEstCobr.setSelection(i);
                            break;
                        }
                    }
                    llRefComerc.setVisibility(View.GONE);
                    desabCamposDadosCli();
                }
            }
        } else {
            cmpDoc.setError(getString(R.string.erro_documento_invalido));
            Snackbar.make(viewSnackBar, "Digito verificador invalido!\nEntre com um documento válido!", Snackbar.LENGTH_LONG).show();
            habCamposDadosCli();
            limpaCampos();
            cmpRzSocial.getText().clear();
            cmpIE.getText().clear();
            cmpEmail.getText().clear();
        }
    }

    protected TextWatcher textWatcherCepCobr = new TextWatcher() {
        @Override
        public void afterTextChanged(Editable s) {

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (GettersSetters.isValidarDadosCli()) {
                if (CheckConnection.isConnected(ColetaCliente.this)) {
                    if (cmpCepCobr.getText().toString().trim().length() == 8 && !cmpCepCobr.getText().toString().trim().equals("00000000")) {
                        cepEntrada = cmpCepCobr.getText().toString().trim();
                        urlCep = "https://viacep.com.br/ws/" + cepEntrada + "/json/";
                        new ProcessJSONCEPCob().execute(urlCep);
                    }
                }
            }
        }
    };
}