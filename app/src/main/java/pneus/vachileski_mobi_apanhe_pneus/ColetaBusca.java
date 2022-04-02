package pneus.vachileski_mobi_apanhe_pneus;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;

import com.github.barteksc.pdfviewer.PDFView;
import com.google.android.material.snackbar.Snackbar;
import com.mazenrashed.printooth.Printooth;
import com.mazenrashed.printooth.ui.ScanningActivity;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import pneus.vachileski_mobi_funcoes_genericas.CheckConnection;
import pneus.vachileski_mobi_funcoes_genericas.ConexaoBDExt;
import pneus.vachileski_mobi_funcoes_genericas.ConexaoBDInt;
import pneus.vachileski_mobi_funcoes_genericas.EnviaEmail;
import pneus.vachileski_mobi_funcoes_genericas.FuncoesGenericas;
import pneus.vachileski_mobi_funcoes_genericas.GeraArquivosColeta;
import pneus.vachileski_mobi_funcoes_genericas.GettersSetters;
import pneus.vachileski_mobi_funcoes_genericas.ImprimeColeta;
import pneus.vachileski_mobi_funcoes_genericas.MaskEditUtil;

@SuppressWarnings("Convert2Lambda")
public class ColetaBusca extends AppCompatActivity {
    LinearLayout formItensColetaBusca, llVendBusca;
    ScrollView scrollViewColetas;
    Button btnBuscaColeta, btnLimpaDadosBusca, btnData, btnTrocaVendedor;
    EditText cmpDataBusca, cmpNomeCliBusca, cmpNumColeta;
    View viewSnackBar;
    Spinner spnEmpresas;
    Spinner spnStatusColeta;
    TextView txtQtdColExt, txtVendedorBusca;

    BuscaColetasInt buscaColetasInt = null;
    BuscaColetasExt buscaColetasExt = null;
    BuscaColetaCabecExtDados buscaColetaCabecExtDados = null;
    BuscaColetasExtItens buscaColetasExtItens = null;
    GeraPDF geraPDF = null;
    EnviaColetas enviaColetas = null;
    TaskEnviaEmail taskEnviaEmail = null;

    ConexaoBDInt db;
    ConexaoBDExt conecta = new ConexaoBDExt();
    GeraArquivosColeta geraArquivosColeta = new GeraArquivosColeta();
    FuncoesGenericas funcoesGenericas = new FuncoesGenericas();

    File filePdf;

    boolean sucesso = false;
    boolean isCelValid = false;
    boolean cancelaEnvio = false;
    boolean isShowPDF = false;
    boolean isSendWhats = false;
    boolean isSendTelegram = false;
    boolean isCancelarEnvioEmail = false;

    String vendedor = "";
    String ddd = "";
    String celular = "";
    String filialBusca = "";
    String documento = "";
    String emailCli = "";
    String novaData = "";
    String dataColeta = "";
    String statusCabec = "";
    String vale = "";

    String statusColetaTemp = ""; //status da coleta temporário para uso no fonte em geral
    /*
     * VARIÁVEIS PARA ENVIO DO EMAIL EXT
     **/
    String nomeColeta = "";
    String data = "";
    String numeroColeta = "";
    String filial = "";
    String codCli = "";
    String lojaCli = "";
    String valor = "";
    String codVend = "";
    String status = "";

    AlertDialog.Builder builderMail = null;
    AlertDialog dialogCelular = null;
    AlertDialog dialogOpcoes = null;
    AlertDialog alertDialog = null;

    final DecimalFormat df = new DecimalFormat("#,##0.00");

    final ArrayList<String> arrIdUsuario = new ArrayList<>();
    final ArrayList<String> arrUsuario = new ArrayList<>();
    final ArrayList<String> arrCodTotvs = new ArrayList<>();
    final ArrayList<String> arrNomeTotvs = new ArrayList<>();
    final ArrayList<String> arrTipovend = new ArrayList<>();
    final ArrayList<String> arrCliPermitidos = new ArrayList<>();
    final ArrayList<String> arrSpinnerStatus = new ArrayList<>();
    final ArrayList<String> arrSpinnerEmpresa = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coleta_busca);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setTitle(getString(R.string.tituloColeta) + " - Busca");

        db = new ConexaoBDInt(this);

        cmpDataBusca = findViewById(R.id.cmpDataBusca);
        cmpNomeCliBusca = findViewById(R.id.cmpNomeCliBusca);
        cmpNumColeta = findViewById(R.id.cmpNumColeta);
        spnStatusColeta = findViewById(R.id.spnStatusColeta);
        viewSnackBar = findViewById(R.id.viewSnackBar);
        btnBuscaColeta = findViewById(R.id.btnBuscaColeta);
        btnLimpaDadosBusca = findViewById(R.id.btnLimpaDadosBusca);
        formItensColetaBusca = findViewById(R.id.formItensColetaBusca);
        scrollViewColetas = findViewById(R.id.scrollViewColetas);
        spnEmpresas = findViewById(R.id.spnFilial);
        txtQtdColExt = findViewById(R.id.txtQtdColExt);
        btnData = findViewById(R.id.btnData);
        btnTrocaVendedor = findViewById(R.id.btnTrocaVendedor);
        llVendBusca = findViewById(R.id.llVendBusca);
        txtVendedorBusca = findViewById(R.id.txtVendedorBusca);

        if (GettersSetters.getTipoUsuario().equals("V") || GettersSetters.getTipoUsuario().equals("T")) {
            btnTrocaVendedor.setVisibility(View.GONE);
            llVendBusca.setVisibility(View.GONE);
        } else {
            if (!Home.isColetaEnvio) {
                trocaVendedor();
            }
            txtVendedorBusca.setText(GettersSetters.getNomeVend());
        }

        deletaColetas15Dias();

        txtQtdColExt.setVisibility(View.GONE);
        cmpDataBusca.setText(getData());

        arrSpinnerStatus.add(0, "[INT] Todas");
        arrSpinnerStatus.add(1, "[INT] Não Enviadas");
        arrSpinnerStatus.add(2, "[INT] Enviadas");
        if (CheckConnection.isConnected(ColetaBusca.this)) {
            arrSpinnerStatus.add(3, "TODAS Enviadas");
            arrSpinnerStatus.add(4, "Enviadas");
            arrSpinnerStatus.add(5, "Análise Crédito");
            arrSpinnerStatus.add(6, "Produção"); // STATUS 4 AGENDA E 5 PRODUÇÃO (4,5)
            arrSpinnerStatus.add(7, "Crédito Recusado");
            arrSpinnerStatus.add(8, "Aguard. Crédito");
            arrSpinnerStatus.add(9, "Faturadas"); // STATUS 4 AGENDA E 5 PRODUÇÃO (4,5)
        }

        ArrayAdapter<String> adapterStatus = new ArrayAdapter<>(ColetaBusca.this, android.R.layout.simple_list_item_1, arrSpinnerStatus);
        adapterStatus.setDropDownViewResource(android.R.layout.simple_list_item_1);
        spnStatusColeta.setAdapter(adapterStatus);

        arrSpinnerEmpresa.clear();
        arrSpinnerEmpresa.add("TODAS FILIAIS");
        if (GettersSetters.getCodigoVendedor().equals("000") || (Home.isColetaEnvio && (GettersSetters.getTipoUsuario().equals("A") || GettersSetters.getTipoUsuario().equals("G")))) {
            Cursor resFiliais = db.buscaFilais();
            if (resFiliais.getCount() > 0) {
                if (resFiliais.moveToFirst()) {
                    do {
                        arrSpinnerEmpresa.add(resFiliais.getString(3) + " / " + resFiliais.getString(2));
                    } while (resFiliais.moveToNext());
                }
            }
        } else {
            db.buscaFiliaisUsuarios(GettersSetters.getCodigoVendedor());
            arrSpinnerEmpresa.addAll(db.arrDadosFiliaisUsuarios);
        }

        final ArrayAdapter<String> adapterEmpresa = new ArrayAdapter<>(ColetaBusca.this, android.R.layout.simple_spinner_item, arrSpinnerEmpresa);
        adapterEmpresa.setDropDownViewResource(android.R.layout.simple_list_item_activated_1);
        spnEmpresas.setAdapter(adapterEmpresa);
        spnEmpresas.setSelection(0);

        /* BUSCA AUTOMATICAMENTE QUANDO COLETAS NÃO ENVIADAS **/
        if (Home.isColetaEnvio) {
            cmpDataBusca.getText().clear();
            spnStatusColeta.setSelection(1);
            formItensColetaBusca.removeAllViews();
            buscaColetasInt = new BuscaColetasInt();
            buscaColetasInt.execute(String.valueOf(spnStatusColeta.getSelectedItemPosition()), "", "", "", "");
            Home.isColetaEnvio = false;
        }

        btnData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                trocaData();
            }
        });

        btnTrocaVendedor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                trocaVendedor();
            }
        });

        btnBuscaColeta.setOnClickListener(new View.OnClickListener() {
            @SuppressLint({"ResourceAsColor", "DefaultLocale"})
            @Override
            public void onClick(View v) {
                txtQtdColExt.setVisibility(View.GONE);
                viewSnackBar.setVisibility(View.GONE);

                if (!spnEmpresas.getSelectedItem().toString().contains("TODAS")) {
                    for (int i = 0; i < arrSpinnerEmpresa.size(); i++) {
                        if (arrSpinnerEmpresa.get(i).contains(spnEmpresas.getSelectedItem().toString())) {
                            String[] filialSelecionada = spnEmpresas.getSelectedItem().toString().split(" / ");
                            filialBusca = filialSelecionada[1];
                            break;
                        }
                    }
                } else {
                    filialBusca = "";
                }

                if (spnStatusColeta.getSelectedItem().toString().contains("[INT")) {
                    formItensColetaBusca.removeAllViews();
                    buscaColetasInt = new BuscaColetasInt();
                    buscaColetasInt.execute(String.valueOf(spnStatusColeta.getSelectedItemPosition()),
                            cmpNomeCliBusca.getText().toString().replace(" ", "%"),
                            cmpNumColeta.getText().toString(),
                            cmpDataBusca.getText().toString(), filialBusca);
                } else {
                    status = "";

                    if (spnStatusColeta.getSelectedItemPosition() == 4) {
                        status = "2";
                    } else if (spnStatusColeta.getSelectedItemPosition() == 5) {
                        status = "3";
                    } else if (spnStatusColeta.getSelectedItemPosition() == 6 || spnStatusColeta.getSelectedItemPosition() == 9) {
                        status = "4,5";
                    } else if (spnStatusColeta.getSelectedItemPosition() == 7) {
                        status = "6";
                    } else if (spnStatusColeta.getSelectedItemPosition() == 8) {
                        status = "7";
                    }

                    formItensColetaBusca.removeAllViews();
                    buscaColetasExt = new BuscaColetasExt();
                    buscaColetasExt.execute((!cmpDataBusca.getText().toString().trim().equals("") ? GettersSetters.converteData(cmpDataBusca.getText().toString().trim(), "BR") : cmpDataBusca.getText().toString().trim()),
                            cmpNumColeta.getText().toString(), cmpNomeCliBusca.getText().toString().replace(" ", "%"), status, filialBusca, GettersSetters.getCodigoVendedor(),
                            GettersSetters.getTipoUsuario(), String.valueOf(spnStatusColeta.getSelectedItemPosition()));

                }
            }
        });

        btnLimpaDadosBusca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtQtdColExt.setVisibility(View.GONE);
                formItensColetaBusca.removeAllViews();
                spnStatusColeta.setSelection(0);
                spnEmpresas.setSelection(0);
                cmpNomeCliBusca.setText("");
                cmpNumColeta.setText("");
                cmpDataBusca.setText("");

                spnEmpresas.setSelection(arrSpinnerEmpresa.indexOf(String.valueOf(0)));

                arrSpinnerStatus.clear();
                spnStatusColeta.setAdapter(null);

                arrSpinnerStatus.add(0, "[INT] Todas");
                arrSpinnerStatus.add(1, "[INT] Não Enviadas");
                arrSpinnerStatus.add(2, "[INT] Enviadas");
                if (CheckConnection.isConnected(ColetaBusca.this)) {
                    arrSpinnerStatus.add(3, "TODAS Enviadas");
                    arrSpinnerStatus.add(4, "Enviadas");
                    arrSpinnerStatus.add(5, "Análise Crédito");
                    arrSpinnerStatus.add(6, "Produção"); // STATUS 4 AGENDA E 5 PRODUÇÃO (4,5)
                    arrSpinnerStatus.add(7, "Crédito Recusado");
                    arrSpinnerStatus.add(8, "Aguard. Crédito");
                    arrSpinnerStatus.add(9, "Faturadas"); // STATUS 4 AGENDA E 5 PRODUÇÃO (4,5)
                }

                spnStatusColeta.setAdapter(adapterStatus);

                Snackbar.make(viewSnackBar, "Para uma nova busca, preencha os dados", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    public void enviaColetaEmailWhatsapp(@NotNull final String outEmail, final String outNomeCole, final String data, final String identifColeta, final String filial,
                                         final String codCli, final String lojaCli, final String valor, final boolean whatsapp, final File pdf, final String codVend,
                                         final String status, final boolean email) {
        sucesso = false;

        if (email) {
            if ((!outEmail.trim().toUpperCase().contains("NOMAIL") || !outEmail.trim().equals("."))) {
                try {
                    taskEnviaEmail = new TaskEnviaEmail();
                    taskEnviaEmail.execute(vendedor, outNomeCole, identifColeta, GettersSetters.converteData(data, "BR"), filial, lojaCli, codCli, valor,
                            outEmail, codVend, (pdf != null ? pdf.toString() : null), status);
                } catch (Exception e) {
                    e.printStackTrace();
                    Snackbar.make(viewSnackBar, "Erro enviar e-mail " + e.toString(), Snackbar.LENGTH_LONG).show();
                    sucesso = false;
                }
            } else {
                final AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ColetaBusca.this);
                builder.setIcon(R.drawable.error);
                builder.setCancelable(false);
                builder.setTitle("Sem e-mail");
                builder.setMessage("Cliente " + outNomeCole + " não possui e-mail cadastrado ou o e-mail está inválido!\nEnvio do e-mail interrompido!");
                builder.setPositiveButton("Fechar", null);
                final AlertDialog dialogEmail = builder.create();
                dialogEmail.show();

                sucesso = false;
            }
        } else if (whatsapp) {
            final AlertDialog.Builder builderMail = new android.app.AlertDialog.Builder(ColetaBusca.this);
            builderMail.setIcon(R.drawable.whatsapp);
            builderMail.setCancelable(false);
            builderMail.setTitle("Número de celular");
            builderMail.setMessage("Digite o número de celular para encaminhar a coleta por Whatsapp!");
            final EditText inputCelular = new EditText(ColetaBusca.this);
            inputCelular.setInputType(InputType.TYPE_CLASS_NUMBER);
            inputCelular.setHint(getString(R.string.mensagem_dialog_numero_celular));
            int maxLength = 11;
            InputFilter[] fArray = new InputFilter[1];
            fArray[0] = new InputFilter.LengthFilter(maxLength);
            inputCelular.setFilters(fArray);
            builderMail.setView(inputCelular);
            builderMail.setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    celular = inputCelular.getText().toString();

                    if (celular.length() > 0) {
                        if (celular.startsWith("0")) {
                            celular = celular.substring(1);
                        }
                        ddd = celular.substring(0, 2);
                        if ((!celular.startsWith("0"))) {
                            celular = celular.substring(2);
                            if (celular.startsWith("9") || celular.startsWith("8")) {
                                if (celular.length() != 8) {
                                    celular = celular.substring(1);
                                }
                                isCelValid = true;
                            }
                        }
                    }
                }
            });
            builderMail.setNegativeButton("Múltiplos", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    isCelValid = true;
                }
            });

            builderMail.setNeutralButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    isCelValid = false;
                    celular = "";
                    cancelaEnvio = true;
                }
            });
            dialogCelular = builderMail.create();
            dialogCelular.show();

            dialogCelular.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    if (isCelValid) {
                        funcoesGenericas.preparaWhatsappEmail(ColetaBusca.this, codVend, outNomeCole, identifColeta, data, filial, lojaCli,
                                codCli, valor, "+55" + ddd + celular, null, pdf, spnStatusColeta.getSelectedItem().toString(), true, false,
                                vale);
                        isCelValid = false;
                        celular = "";
                        cancelaEnvio = false;
                        sucesso = true;
                    } else {
                        if (!cancelaEnvio) {
                            Snackbar.make(viewSnackBar, "Celular inválido! Tente novamente.", Snackbar.LENGTH_LONG).show();
                            isCelValid = false;
                            celular = "";
                            cancelaEnvio = false;
                            sucesso = false;
                        }
                    }
                }
            });
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class TaskEnviaEmail extends AsyncTask<String, String, Boolean> {
        AlertDialog dialogEnviaEmail = null;

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPreExecute() {
            final AlertDialog.Builder builder = new AlertDialog.Builder(ColetaBusca.this);
            LayoutInflater layoutInflater = getLayoutInflater();
            final View dialogView = layoutInflater.inflate(R.layout.dialog_carregamento, null);
            final TextView textView = dialogView.findViewById(R.id.txtInfoCarreg);
            textView.setText("Enviando coleta por E-mail, para\n" + emailCli.trim() + "\nAguarde...");
            builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    taskEnviaEmail.cancel(true);
                }
            });
            builder.setCancelable(false);
            builder.setView(dialogView);
            dialogEnviaEmail = builder.create();
            dialogEnviaEmail.show();
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                if (!strings[8].trim().equals(".") && !strings[8].contains("nomail")) {
                    strings[8] = emailCli.trim();
                    //strings[8] = "ti@vachileski.com.br".trim();
                    String[] emailsCli = strings[8].trim().split(";");
                    if (funcoesGenericas.preparaWhatsappEmail(ColetaBusca.this, strings[9], strings[1], strings[2], strings[3], strings[4], strings[5],
                            strings[6], strings[7], null, emailsCli, (strings[10] != null ? new File(strings[10]) : null), spnStatusColeta.getSelectedItem().toString(),
                            false, true, vale)) {
                        sucesso = true;
                    }
                } else {
                    Snackbar.make(viewSnackBar, "E-mail [" + strings[8].trim() + "] inválido!", Snackbar.LENGTH_LONG).show();
                    sucesso = false;
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.i("Erro email", e.toString());
                sucesso = false;
            }
            return sucesso;
        }

        @Override
        protected void onPostExecute(Boolean sucesso) {
            if (sucesso) {
                AlertDialog.Builder builderSucesso = new AlertDialog.Builder(ColetaBusca.this);
                builderSucesso.setIcon(R.drawable.emailsender);
                builderSucesso.setTitle("Sucesso");
                builderSucesso.setMessage("E-mail enviado com sucesso!");
                builderSucesso.setCancelable(true);
                builderSucesso.setNegativeButton("Fechar", null);
                builderSucesso.show();
            } else {
                if (!GettersSetters.getErroEnvioColetaBDExt().equals("")) {
                    AlertDialog.Builder builderFilial = new AlertDialog.Builder(ColetaBusca.this);
                    builderFilial.setTitle("Erro ao enviar o E-mail!");
                    builderFilial.setIcon(R.drawable.error);
                    builderFilial.setCancelable(false);
                    builderFilial.setMessage(GettersSetters.getErroEnvioColetaBDExt());
                    builderFilial.setPositiveButton("Fechar", null);
                    AlertDialog dialog = builderFilial.create();
                    dialog.show();
                } else {
                    Snackbar.make(viewSnackBar, "Erro ao enviar o e-mail!", Snackbar.LENGTH_LONG).show();
                }
            }

            Log.i("AsyncTask", "Tirando dialog da tela Thread: " + Thread.currentThread().getName());

            if (dialogEnviaEmail != null) {
                dialogEnviaEmail.dismiss();
            }
        }
    }

    /*
     * Função para quando virar a tela não resetar o layout
     **/
    @Override
    public void onConfigurationChanged(@NotNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @SuppressWarnings("rawtypes")
    @SuppressLint("StaticFieldLeak")
    public class EnviaColetas extends AsyncTask<String, String, ArrayList> {
        AlertDialog dialogEnviaColeta = null;

        @Override
        protected void onPreExecute() {
            AlertDialog.Builder builder = new AlertDialog.Builder(ColetaBusca.this);
            LayoutInflater layoutInflater = getLayoutInflater();
            final View dialogView = layoutInflater.inflate(R.layout.dialog_carregamento, null);
            final TextView textView = dialogView.findViewById(R.id.txtInfoCarreg);
            textView.setText("Enviando Coletas...\nAguarde...");
            builder.setPositiveButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    enviaColetas.cancel(true);
                    Toast.makeText(ColetaBusca.this, "Envio cancelado!", Toast.LENGTH_LONG).show();
                    onBackPressed();
                }
            });
            builder.setCancelable(false);
            builder.setView(dialogView);
            dialogEnviaColeta = builder.create();
            dialogEnviaColeta.show();
        }

        @SuppressLint("DefaultLocale")
        @Override
        protected ArrayList<String> doInBackground(String... params) {
//            boolean coletaEnviada;
            boolean envioConcluidoCabec = false;
            boolean envioConcluidoItens = false;
            boolean envioConcluidoCliente = false;
            int contaItens = 0;

//            String seqIdentifColeta, identificadorColeta;
            ArrayList<String> coletasEnviadas = new ArrayList<>();
            Cursor res = db.buscaColetasNaoEnviadas(params[0], params[1], true, params[2]);
            if (res.getCount() > 0) {
                for (int i = 0; i < db.arrBuscaColetaNaoEnviada.size(); i++) {
                    String resultado = conecta.selecionaColetaJaEnviada(db.arrBuscaColetaNaoEnviada.get(i), db.arrBuscaColetaNaoEnviadaColPor.get(i), db.arrBuscaColetaNaoEnviadaFilial.get(i));
                    if (!resultado.trim().equals("")) {
                        conecta.deleteColeta(db.arrBuscaColetaNaoEnviadaFilial.get(i), db.arrBuscaColetaNaoEnviadaData.get(i), db.arrBuscaColetaNaoEnviada.get(i), true);

//                            coletaEnviada = true;
//                            while (coletaEnviada) {
//                                seqIdentifColeta = conecta.selecionaMaxNumColeta(db.arrBuscaColetaNaoEnviadaColPor.get(i));
//                                identificadorColeta = String.format("%06d", Integer.parseInt(db.arrBuscaColetaNaoEnviadaColPor.get(i))).substring(2, 6) + seqIdentifColeta; // CÓDIGO DO VENDENDOR COM 4 ULTIMOS CARACTERES + SEQUENCIAL
//
//                                /* UPDATE DOS ITENTIFICADORES **/
//                                String updateNumeroColetaCabecalho = "UPDATE COLETA_CABEC " +
//                                        "SET COL_CABEC_IDENTIF = '" + identificadorColeta + "', " +
//                                        "    COL_CABEC_SEQ_IDENTIFICADOR = '" + seqIdentifColeta + "' " +
//                                        "WHERE trim(COL_CABEC_FILIAL) = '" + db.arrBuscaColetaNaoEnviadaFilial.get(i).trim() + "' " +
//                                        "  AND trim(COL_CABEC_IDENTIF) = '" + db.arrBuscaColetaNaoEnviada.get(i).trim() + "'" +
//                                        "  AND trim(COL_CABEC_STATUS_ENVIO) = '1' " +
//                                        "  AND trim(COL_CABEC_COD_CLI) = '" + db.arrBuscaColetaNaoEnviadaCodCli.get(i).trim() + "' " +
//                                        "  AND trim(COL_CABEC_LOJA_CLI) = '" + db.arrBuscaColetaNaoEnviadaLojaCli.get(i).trim() + "' " +
//                                        "  AND trim(COL_CABEC_DATA) = '" + db.arrBuscaColetaNaoEnviadaData.get(i).trim() + "'";
//                                db.updColeta(updateNumeroColetaCabecalho);
//
//                                String updateNumeroColetaItens = "UPDATE COLETA_ITENS " +
//                                        "SET COL_IT_IDENTIF = '" + identificadorColeta + "' " +
//                                        "WHERE trim(COL_IT_FILIAL) = '" + db.arrBuscaColetaNaoEnviadaFilial.get(i).trim() + "' " +
//                                        "  AND trim(COL_IT_IDENTIF) = '" + db.arrBuscaColetaNaoEnviada.get(i).trim() + "'" +
//                                        "  AND trim(COL_IT_STATUS_ENVIO) = '1' " +
//                                        "  AND trim(COL_IT_COD_CLI) = '" + db.arrBuscaColetaNaoEnviadaCodCli.get(i).trim() + "'" +
//                                        "  AND trim(COL_IT_LOJA_CLI) = '" + db.arrBuscaColetaNaoEnviadaLojaCli.get(i).trim() + "'" +
//                                        "  AND trim(COL_IT_DATA) = '" + db.arrBuscaColetaNaoEnviadaData.get(i).trim() + "'";
//                                db.updColeta(updateNumeroColetaItens);
//
//                                // ATUALIZA NOVO NUMERO DA COLETA PARA CLIENTE NÃO ENVIADO
//                                String updNumColCli = "UPDATE COLETA_CLI " +
//                                        "SET COL_CLI_IDENTIF = '" + identificadorColeta + "' " +
//                                        "WHERE trim(COL_CLI_FILIAL) = '" + db.arrBuscaColetaNaoEnviadaFilial.get(i).trim() + "' " +
//                                        "  AND trim(COL_CLI_IDENTIF) = '" + db.arrBuscaColetaNaoEnviada.get(i).trim() + "'" +
//                                        "  AND trim(COL_CLI_COD_CLI) = '" + db.arrBuscaColetaNaoEnviadaCodCli.get(i).trim() + "'" +
//                                        "  AND trim(COL_CLI_LOJA_CLI) = '" + db.arrBuscaColetaNaoEnviadaLojaCli.get(i).trim() + "'" +
//                                        "  AND trim(COL_CLI_DATA) = '" + db.arrBuscaColetaNaoEnviadaData.get(i).trim() + "'";
//                                db.updColeta(updNumColCli);
//
//                                resultado = conecta.selecionaColetaJaEnviada(db.arrBuscaColetaNaoEnviada.get(i), db.arrBuscaColetaNaoEnviadaColPor.get(i), db.arrBuscaColetaNaoEnviadaFilial.get(i));
//                                if (resultado.trim().equals("")) {
//                                    coletaEnviada = false;
//
//                                    db.arrBuscaColetaNaoEnviada.set(i, identificadorColeta); //NOVO IDENTIFICADOR
//                                    db.insereNumColeta(seqIdentifColeta, db.arrBuscaColetaNaoEnviadaColPor.get(i), identificadorColeta, db.arrBuscaColetaNaoEnviadaData.get(i));
//                                    db.buscaColetasNaoEnviadas(identificadorColeta, params[1], true);
//                                }
//                            }
                    }

                    res = db.buscaColetasCabec(db.arrBuscaColetaNaoEnviadaData.get(i), db.arrBuscaColetaNaoEnviada.get(i), db.arrBuscaColetaNaoEnviadaNomeCli.get(i), 1, db.arrBuscaColetaNaoEnviadaFilial.get(i), 0, ColetaBusca.this); //BUSCA CABEÇALHO DAS COLETAS
                    if (res.getCount() > 0) {
                        /* INSERCAO DO CABECALHO **/
                        for (int j = 0; j < db.arrBuscaNumeroColeta.size(); j++) {
//                                if (db.arrBuscaColetaPdf.get(j) == null || !db.arrBuscaColetaIdentificador.get(j).equals(identificadorColeta)) {
//                                    GeraArquivosColeta geraArquivosColeta = new GeraArquivosColeta();
//                                    File pdfColeta = geraArquivosColeta.montaPdf(ColetaBusca.this, db.arrBuscaColetaNomeCli.get(j), db.arrBuscaColetaIdentificador.get(j), db.arrBuscaColetaData.get(j),
//                                            db.arrBuscaColetaFilial.get(j), db.arrBuscaColetaLojaCli.get(j), db.arrBuscaColetaCodCli.get(j), db.arrBuscaColetaValor.get(j));
//
//                                    pdfToByte = geraArquivosColeta.pdfToImage(ColetaBusca.this, pdfColeta, 1750, 2500); //ALTURA e LARGURA da pg igual ao do PDF
//
//                                    String encodedPDF = Base64.encodeToString(pdfToByte, Base64.DEFAULT);
//
//                                    if (pdfToByte != null) {
//                                        //UPDATE PARA INSERCAO DO PDF CONVERTIDO PARA BYTE
//                                        String updatePdfByteColeta = "COL_CABEC_FILIAL = '" + db.arrBuscaColetaFilial.get(j) + "' " +
//                                                " AND COL_CABEC_IDENTIF = '" + db.arrBuscaColetaIdentificador.get(j) + "'" +
//                                                " AND COL_CABEC_COD_CLI = '" + db.arrBuscaColetaCodCli.get(j) + "'" +
//                                                " AND COL_CABEC_LOJA_CLI = '" + db.arrBuscaColetaLojaCli.get(j) + "'" +
//                                                " AND COL_CABEC_DATA = '" + db.arrBuscaColetaData.get(j) + "'";
//
//                                        db.updColetaInsertPdfImg(updatePdfByteColeta, encodedPDF);
//
//                                        /* SETA OS BYTES DO PDF NA POSIÇÃO PARA NÃO TER QUE BUSCAR NOVAMENTE **/
//                                        db.arrBuscaColetaPdf.set(j, pdfToByte);
//                                    }
//                                }
                            //DECODIFICA O PDF DA IMG GRAVADO NO BANCO
                            //String decodedImage = Base64.encodeToString(db.arrBuscaColetaPdf.get(j), Base64.DEFAULT);

                            String encondeAssinaturaBorr = null;
                            if (db.arrBuscaColetaAssinBorr.get(j) != null) {
                                encondeAssinaturaBorr = Base64.encodeToString(db.arrBuscaColetaAssinBorr.get(j), Base64.DEFAULT);
                            }
                            String encondeAssinaturaCli = Base64.encodeToString(db.arrBuscaColetaAssinCli.get(j), Base64.DEFAULT);
                            if (conecta.insereDadosColetaCabecalho(db.arrBuscaColetaFilial.get(j), db.arrBuscaNumeroColeta.get(j), db.arrBuscaColetaData.get(j), db.arrBuscaColetaCodCli.get(j),
                                    db.arrBuscaColetaLojaCli.get(j), db.arrBuscaColetaNomeCli.get(j), db.arrBuscaColetaCodVend.get(j), db.arrBuscaColetaQtdItens.get(j), db.arrBuscaColetaValor.get(j),
                                    db.arrBuscaColetaCodCondPg.get(j), db.arrBuscaColetaFormaPg.get(j), db.arrBuscaColetaInfoAdic.get(j), db.arrBuscaColetaCodBorr.get(j), db.arrBuscaColetaNomeBorr.get(j),
                                    db.arrBuscaColetaDocumBorr.get(j), encondeAssinaturaBorr, encondeAssinaturaCli, Integer.parseInt(db.arrBuscaColetaIdSeq.get(j)),
                                    db.arrBuscaColetaTipoColeta.get(j), db.arrBuscaColetaHoraColeta.get(j), db.arrBuscaColetaColPor.get(j), null, db.arrBuscaColetaIdUsr.get(j),
                                    db.arrBuscaColetaDocCli.get(j), db.arrBuscaColetaDtChegada.get(j), (db.arrBuscaColetaOrcamento.get(j).equals("0") ? "F" : db.arrBuscaColetaOrcamento.get(j)),
                                    ColetaBusca.this, db.arrBuscaColetaOffline.get(j), db.arrBuscaColetaCliNovo.get(j))) {
                                envioConcluidoCabec = true;
                            }

                            if (!envioConcluidoCabec) {
                                /* DELETA A COLETA QUANDO DA ERRO NO ENVIO **/
                                conecta.deleteColeta(db.arrBuscaColetaFilial.get(j), db.arrBuscaColetaData.get(j), db.arrBuscaNumeroColeta.get(j), false);

                                db.updColeta("UPDATE COLETA_ITENS " +
                                        "SET COL_IT_STATUS_ENVIO = '1' " +
                                        "WHERE trim(COL_IT_FILIAL) = '" + db.arrBuscaColetaFilial.get(j).trim() + "'" +
                                        "  AND trim(COL_IT_IDENTIF) = '" + db.arrBuscaNumeroColeta.get(j).trim() + "'" +
                                        "  AND trim(COL_IT_COD_CLI) = '" + db.arrBuscaColetaCodCli.get(j).trim() + "'" +
                                        "  AND trim(COL_IT_LOJA_CLI) = '" + db.arrBuscaColetaLojaCli.get(j).trim() + "'" +
                                        "  AND trim(COL_IT_DATA) = '" + db.arrBuscaColetaData.get(j).trim() + "'");

                                /* ATUALIZA O STATUS DA COLETA **/
                                db.updColeta("UPDATE COLETA_CABEC " +
                                        "SET COL_CABEC_STATUS_ENVIO = '1' " +
                                        "WHERE trim(COL_CABEC_FILIAL) = '" + db.arrBuscaColetaFilial.get(j).trim() + "' " +
                                        "  AND trim(COL_CABEC_IDENTIF) = '" + db.arrBuscaNumeroColeta.get(j).trim() + "'" +
                                        "  AND trim(COL_CABEC_COD_CLI) = '" + db.arrBuscaColetaCodCli.get(j).trim() + "'" +
                                        "  AND trim(COL_CABEC_LOJA_CLI) = '" + db.arrBuscaColetaLojaCli.get(j).trim() + "'" +
                                        "  AND trim(COL_CABEC_DATA) = '" + db.arrBuscaColetaData.get(j).trim() + "'");
                                coletasEnviadas.clear();
                            } else {
                                res = db.buscaColetaItens("SELECT * " +
                                        "FROM COLETA_ITENS " +
                                        "WHERE 1 = 1 " +
                                        " AND trim(COL_IT_FILIAL) = '" + db.arrBuscaColetaFilial.get(j).trim() + "' " +
                                        " AND trim(COL_IT_IDENTIF) = '" + db.arrBuscaNumeroColeta.get(j).trim() + "' " +
                                        " AND trim(COL_IT_DATA) = '" + db.arrBuscaColetaData.get(j).trim() + "' " +
                                        " AND trim(COL_IT_COD_CLI) = '" + db.arrBuscaColetaCodCli.get(j).trim() + "' " +
                                        " AND trim(COL_IT_LOJA_CLI) = '" + db.arrBuscaColetaLojaCli.get(j).trim() + "'");

                                if (res.getCount() > 0) {
                                    /* INSERCAO DOS ITENS **/
                                    for (int k = 0; k < db.arrBuscaColetaItIdentif.size(); k++) {
                                        if (conecta.insereDadosColetaItens(db.arrBuscaColetaItFilial.get(k), db.arrBuscaColetaItIdentif.get(k), db.arrBuscaColetaItItem.get(k), db.arrBuscaColetaItData.get(k), db.arrBuscaColetaItCodCli.get(k),
                                                db.arrBuscaColetaItLojaCli.get(k), db.arrBuscaColetaItQtd.get(k), db.arrBuscaColetaItVlrUnit.get(k), db.arrBuscaColetaItVlrTotal.get(k), db.arrBuscaColetaItCodPrd.get(k),
                                                db.arrBuscaColetaItBitola.get(k), db.arrBuscaColetaItMarca.get(k), db.arrBuscaColetaItModelo.get(k), db.arrBuscaColetaItSerie.get(k),
                                                db.arrBuscaColetaItDot.get(k), db.arrBuscaColetaItMontado.get(k), db.arrBuscaColetaItDesenho.get(k), db.arrBuscaColetaItUrgente.get(k),
                                                db.arrBuscaColetaItPercComBorr.get(k), db.arrBuscaColetaItVlrComBorr.get(k), db.arrBuscaColetaItObs.get(k), db.arrBuscaColetaItCAgua.get(k),
                                                db.arrBuscaColetaItCCamara.get(k), db.arrBuscaColetaItGarantia.get(k), ColetaBusca.this, "", "", "")) {
                                            contaItens++;
                                        }
                                    }

                                    if (contaItens == db.arrBuscaColetaItIdentif.size()) {
                                        /* ATUALIZA O STATUS DA COLETA ITENS **/
                                        String qryUpdColetaItens = "UPDATE COLETA_ITENS " +
                                                "SET COL_IT_STATUS_ENVIO = '2' " +
                                                "WHERE trim(COL_IT_FILIAL) = '" + db.arrBuscaColetaFilial.get(j).trim() + "'" +
                                                "  AND trim(COL_IT_IDENTIF) = '" + db.arrBuscaNumeroColeta.get(j).trim() + "'" +
                                                "  AND trim(COL_IT_STATUS_ENVIO) = '1' " +
                                                "  AND trim(COL_IT_COD_CLI) = '" + db.arrBuscaColetaCodCli.get(j).trim() + "'" +
                                                "  AND trim(COL_IT_LOJA_CLI) = '" + db.arrBuscaColetaLojaCli.get(j).trim() + "'" +
                                                "  AND trim(COL_IT_DATA) = '" + db.arrBuscaColetaData.get(j).trim() + "'";

                                        if (db.updColeta(qryUpdColetaItens)) {
                                            /* ATUALIZA O STATUS DA COLETA CABEÇALHO **/
                                            String qryUpdColetaCabec = "UPDATE COLETA_CABEC " +
                                                    "SET COL_CABEC_STATUS_ENVIO = '2' " +
                                                    "WHERE trim(COL_CABEC_FILIAL) = '" + db.arrBuscaColetaFilial.get(j).trim() + "' " +
                                                    "  AND trim(COL_CABEC_IDENTIF) = '" + db.arrBuscaNumeroColeta.get(j).trim() + "'" +
                                                    "  AND trim(COL_CABEC_STATUS_ENVIO) = '1' " +
//                                                                " AND COL_CABEC_RZ_SOCIAL LIKE '%" + db.arrBuscaColetaNomeCli.get(j) + "%'" +
                                                    "  AND trim(COL_CABEC_COD_CLI) = '" + db.arrBuscaColetaCodCli.get(j).trim() + "'" +
                                                    "  AND trim(COL_CABEC_LOJA_CLI) = '" + db.arrBuscaColetaLojaCli.get(j).trim() + "'" +
                                                    "  AND trim(COL_CABEC_DATA) = '" + db.arrBuscaColetaData.get(j).trim() + "'";

                                            if (db.updColeta(qryUpdColetaCabec)) {
                                                envioConcluidoItens = true;
                                            }
                                        }
                                    } else {
                                        envioConcluidoCabec = false;
                                        envioConcluidoItens = false;

                                        /* DELETA A COLETA QUANDO DA ERRO NO ENVIO **/
                                        conecta.deleteColeta(db.arrBuscaColetaFilial.get(j), db.arrBuscaColetaData.get(j), db.arrBuscaNumeroColeta.get(j), false);

                                        String qryUpdColetaItens = "UPDATE COLETA_ITENS " +
                                                "SET COL_IT_STATUS_ENVIO = '1' " +
                                                "WHERE trim(COL_IT_FILIAL) = '" + db.arrBuscaColetaFilial.get(j).trim() + "'" +
                                                "  AND trim(COL_IT_IDENTIF) = '" + db.arrBuscaNumeroColeta.get(j).trim() + "'" +
                                                "  AND trim(COL_IT_COD_CLI) = '" + db.arrBuscaColetaCodCli.get(j).trim() + "'" +
                                                "  AND trim(COL_IT_LOJA_CLI) = '" + db.arrBuscaColetaLojaCli.get(j).trim() + "'" +
                                                "  AND trim(COL_IT_DATA) = '" + db.arrBuscaColetaData.get(j).trim() + "'";
                                        db.updColeta(qryUpdColetaItens);

                                        /* ATUALIZA O STATUS DA COLETA **/
                                        String qryUpdColetaCabec = "UPDATE COLETA_CABEC " +
                                                "SET COL_CABEC_STATUS_ENVIO = '1' " +
                                                "WHERE trim(COL_CABEC_FILIAL) = '" + db.arrBuscaColetaFilial.get(j).trim() + "' " +
                                                "  AND trim(COL_CABEC_IDENTIF) = '" + db.arrBuscaNumeroColeta.get(j).trim() + "'" +
                                                "  AND trim(COL_CABEC_COD_CLI) = '" + db.arrBuscaColetaCodCli.get(j).trim() + "'" +
                                                "  AND trim(COL_CABEC_LOJA_CLI) = '" + db.arrBuscaColetaLojaCli.get(j).trim() + "'" +
                                                "  AND trim(COL_CABEC_DATA) = '" + db.arrBuscaColetaData.get(j).trim() + "'";
                                        db.updColeta(qryUpdColetaCabec);
                                    }
                                    coletasEnviadas.clear();
                                }

                                /* INSERCAO DOS NOVOS CLIENTES **/
                                if (db.novoClienteColeta("COL_CLI_COD_CLI", db.arrBuscaColetaCodCli.get(j), db.arrBuscaColetaLojaCli.get(j), false, db.arrBuscaNumeroColeta.get(j)) != null) {
                                    db.novoClienteColeta("*", db.arrBuscaColetaCodCli.get(j), db.arrBuscaColetaLojaCli.get(j), true, db.arrBuscaNumeroColeta.get(j));

                                    /* ABAIXO RODA 1X CADA QUERY PARA BUSCAR AS IMAGENS POIS ESTAVA CAUSANDO O ERRO: 'Row too big to fit into CursorWindow requiredPos=0, totalRows=1' */
                                    db.novoClienteColeta("COL_CLI_RG", db.arrBuscaColetaCodCli.get(j), db.arrBuscaColetaLojaCli.get(j), true, db.arrBuscaNumeroColeta.get(j));
                                    db.novoClienteColeta("COL_CLI_CPF", db.arrBuscaColetaCodCli.get(j), db.arrBuscaColetaLojaCli.get(j), true, db.arrBuscaNumeroColeta.get(j));
                                    db.novoClienteColeta("COL_CLI_COMP_RESID", db.arrBuscaColetaCodCli.get(j), db.arrBuscaColetaLojaCli.get(j), true, db.arrBuscaNumeroColeta.get(j));

                                    String encodeRG = Base64.encodeToString(db.buscaColetaCliRG, Base64.DEFAULT);
                                    String encodeDoc = Base64.encodeToString(db.buscaColetaCliCPF, Base64.DEFAULT);
                                    String encodeCompResi = Base64.encodeToString(db.buscaColetaCliCompResid, Base64.DEFAULT);

                                    if (conecta.insereDadosColetaCli(db.buscaColetaCliFilial, db.buscaColetaCliIdentif, db.buscaColetaCliData, db.buscaColetaCliCodCli,
                                            db.buscaColetaCliLojali, db.buscaColetaCliDodCli, db.buscaColetaCliIe, db.buscaColetaCliNome, db.buscaColetaCliEmail,
                                            db.buscaColetaCliEntEnd, db.buscaColetaCliEntBairro, db.buscaColetaCliEntMunic, db.buscaColetaCliEntEst, db.buscaColetaCliEntCep,
                                            db.buscaColetaCliEntDDD, db.buscaColetaCliEntFone, db.buscaColetaCliCobrEnd, db.buscaColetaCliCobrBairro,
                                            db.buscaColetaCliCobrMunic, db.buscaColetaCliCobrEst, db.buscaColetaCliCobrCep, db.buscaColetaCliCobrDDD, db.buscaColetaCliCobrFone,
                                            db.buscaColetaCliInfoAdic, encodeRG, encodeDoc, encodeCompResi, db.buscaColetaCliCategoria)) {
                                        envioConcluidoCliente = true;
                                    }
                                } else {
                                    envioConcluidoCliente = true;
                                }
                            }
                            if (envioConcluidoCabec && envioConcluidoItens && envioConcluidoCliente) {
                                coletasEnviadas.add(db.arrBuscaNumeroColeta.get(j) + " - " + db.arrBuscaColetaNomeCli.get(j));
                            }
                        }
                    }
                }
            }
//            } catch (Exception exp) {
//                exp.printStackTrace();
//                new Thread() {
//                    public void run() {
//                        ColetaBusca.this.runOnUiThread(new Runnable() {
//                            public void run() {
//                                AlertDialog.Builder builderFilial = new AlertDialog.Builder(ColetaBusca.this);
//                                builderFilial.setTitle("Erro ao enviar a coleta!");
//                                builderFilial.setIcon(R.drawable.error);
//                                builderFilial.setCancelable(false);
//                                builderFilial.setMessage(GettersSetters.getErroEnvioColetaBDExt());
//                                builderFilial.setNegativeButton("Fechar", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialogInterface, int i) {
//                                        Thread.currentThread().interrupt();
//                                    }
//                                });
//                                AlertDialog dialog = builderFilial.create();
//                                dialog.show();
//                            }
//                        });
//                    }
//                }.start();
//            }
            return coletasEnviadas;
        }

        @Override
        protected void onPostExecute(ArrayList enviadas) {
            if (dialogEnviaColeta != null) {
                dialogEnviaColeta.dismiss();
            }

            AlertDialog.Builder builderEnvioColetas = new AlertDialog.Builder(ColetaBusca.this);
            if (enviadas.size() > 0) {
                builderEnvioColetas.setTitle("Coleta Enviada");
                builderEnvioColetas.setIcon(R.drawable.success);
                builderEnvioColetas.setMessage("Coleta enviada para o sistema:\n" + enviadas + "\nPara envio do PDF aos clientes, utilizar a função Busca de Coletas");
            } else {
                builderEnvioColetas.setTitle("Coleta NÃO Enviada");
                builderEnvioColetas.setIcon(R.drawable.error);
                builderEnvioColetas.setMessage(GettersSetters.getErroEnvioColetaBDExt());
            }
            builderEnvioColetas.setCancelable(false);
            builderEnvioColetas.setPositiveButton("Fechar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    formItensColetaBusca.removeAllViews();
                    enviaColetas.cancel(true);
                    buscaColetasInt.cancel(true);
                    buscaColetasInt = new BuscaColetasInt();
                    buscaColetasInt.execute(String.valueOf(spnStatusColeta.getSelectedItemPosition()),
                            cmpNomeCliBusca.getText().toString().replace(" ", "%"),
                            cmpNumColeta.getText().toString(),
                            cmpDataBusca.getText().toString(), filialBusca);
                }
            });
            AlertDialog dialog = builderEnvioColetas.create();
            dialog.show();
        }
    }

    @Override
    public void onBackPressed() {
        GettersSetters.setCodigoVendedor(GettersSetters.getUsuarioLogado());
        Intent it = new Intent(ColetaBusca.this, Home.class);
        startActivity(it);
        finish();
    }

    @SuppressLint("StaticFieldLeak")
    public class BuscaColetasInt extends AsyncTask<String, String, Cursor> {
        AlertDialog dialogBuscaColInt = null;

        @Override
        protected void onPreExecute() {
            AlertDialog.Builder builder = new AlertDialog.Builder(ColetaBusca.this);
            LayoutInflater layoutInflater = getLayoutInflater();
            View dialogView = layoutInflater.inflate(R.layout.dialog_carregamento, null);
            TextView textView = dialogView.findViewById(R.id.txtInfoCarreg);
            textView.setText("Buscando Coletas...\nAguarde...");
            builder.setCancelable(false);
            builder.setPositiveButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    buscaColetasExt.cancel(true);
                    Toast.makeText(ColetaBusca.this, "Busca Cancelada", Toast.LENGTH_LONG).show();
                }
            });
            builder.setView(dialogView);
            dialogBuscaColInt = builder.create();
            dialogBuscaColInt.show();
        }

        @Override
        protected Cursor doInBackground(String... params) {
            return db.buscaColetasCabec((!params[3].equals("") ? GettersSetters.converteData(params[3], "BR") : params[3]),
                    params[2], params[1], Integer.parseInt(params[0]), params[4], 0, ColetaBusca.this);
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(final Cursor cursorColetaCabec) {
            formItensColetaBusca.removeAllViews();

            if (cursorColetaCabec.getCount() > 0) {
                final LayoutInflater layoutInflater = getLayoutInflater();
                assert layoutInflater != null;
                for (int j = 0; j < db.arrBuscaColetaFilial.size(); j++) {
                    final View addView = layoutInflater.inflate(R.layout.view_coleta, formItensColetaBusca, false);

                    final CardView btnOpcoes = addView.findViewById(R.id.btnOpcoes);
                    final TextView outStatusCol = addView.findViewById(R.id.txtStatusCol);
                    final TextView outFilialDesc = addView.findViewById(R.id.txtFilialDesc);
                    final TextView outNumCol = addView.findViewById(R.id.txtNumeroCol);
                    final TextView outDataCole = addView.findViewById(R.id.txtDataCol);
                    final TextView outDocCli = addView.findViewById(R.id.txtDocCli);
                    final TextView outNomeCole = addView.findViewById(R.id.txtNomeCol);
                    final TextView outValorCol = addView.findViewById(R.id.txtValUnit);
                    final TextView outTxtNomeVend = addView.findViewById(R.id.txtVended);
                    final LinearLayout llVale = addView.findViewById(R.id.llVale);
                    final LinearLayout llNota = addView.findViewById(R.id.llNota);
                    final LinearLayout llPedido = addView.findViewById(R.id.llPedido);

                    llVale.setVisibility(View.GONE);
                    llNota.setVisibility(View.GONE);
                    llPedido.setVisibility(View.GONE);

                    final String codFilRs = db.arrBuscaColetaFilial.get(j).trim();
                    final String codCliRs = db.arrBuscaColetaCodCli.get(j).trim();
                    final String lojaCliRs = db.arrBuscaColetaLojaCli.get(j).trim();
                    final String codVendRs = db.arrBuscaColetaCodVend.get(j).trim();
                    final String statusColeta = db.arrBuscaColetaStatus.get(j).trim();

                    dataColeta = GettersSetters.converteData(db.arrBuscaColetaData.get(j).substring(0, 4) + "-" + db.arrBuscaColetaData.get(j).substring(4, 6) + "-" + db.arrBuscaColetaData.get(j).substring(6, 8), "EN");

                    if (db.arrBuscaColetaDocCli.get(j) != null) {
                        if (db.arrBuscaColetaDocCli.get(j).trim().length() == 14) {
                            documento = db.arrBuscaColetaDocCli.get(j).replaceAll("([0-9]{2})([0-9]{3})([0-9]{3})([0-9]{4})([0-9]{2})", "$1\\.$2\\.$3/$4-$5");
                        } else {
                            documento = db.arrBuscaColetaDocCli.get(j).replaceAll("([0-9]{3})([0-9]{3})([0-9]{3})([0-9]{2})", "$1\\.$2\\.$3-$4");
                        }
                    }

                    db.selectUsuariosVendedores(db.arrBuscaColetaCodVend.get(j), "", true, false);

                    outStatusCol.setText(db.arrBuscaColetaStatus.get(j));
                    outFilialDesc.setText(db.buscaFiliaisDescr(codFilRs));
                    outNumCol.setText(db.arrBuscaNumeroColeta.get(j));
                    outTxtNomeVend.setText(db.buscaColetaNomeVend);
                    outDataCole.setText(dataColeta);
                    outDocCli.setText(documento);
                    outNomeCole.setText(db.arrBuscaColetaNomeCli.get(j));
                    outValorCol.setText(df.format(Double.parseDouble(db.arrBuscaColetaValor.get(j))));

                    if (outStatusCol.getText().equals("1")) {
                        outStatusCol.setText("Não Enviada");
                        outStatusCol.setTextColor(Color.parseColor("#FF0000"));
                    } else if (outStatusCol.getText().equals("2")) {
                        outStatusCol.setText("Enviada");
                        outStatusCol.setTextColor(Color.parseColor("#00BFA5"));
                    }

//                    final int finalJ = j;
                    btnOpcoes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(ColetaBusca.this);
                            LayoutInflater inflater = getLayoutInflater();
                            View dialogView = inflater.inflate(R.layout.dialog_opcoes_busca_coleta, formItensColetaBusca, false);
                            builder.setCancelable(true);
                            builder.setView(dialogView);

                            final CardView cardExcluir = dialogView.findViewById(R.id.cardExcluir);
                            final CardView cardEnviar = dialogView.findViewById(R.id.cardEnviar);
                            final CardView cardPDF = dialogView.findViewById(R.id.cardPDF);
                            final CardView cardEmail = dialogView.findViewById(R.id.cardEmail);
                            final CardView cardWhats = dialogView.findViewById(R.id.cardWhatsapp);
                            final CardView cardVisualizar = dialogView.findViewById(R.id.cardVisualizar);
                            final CardView cardImprime = dialogView.findViewById(R.id.cardImprime);
                            final TextView txtEnviar = dialogView.findViewById(R.id.txtEnviar);

                            dialogOpcoes = builder.create();

                            nomeColeta = outNomeCole.getText().toString();
                            data = outDataCole.getText().toString();
                            numeroColeta = outNumCol.getText().toString();
                            filial = codFilRs;
                            codCli = codCliRs;
                            lojaCli = lojaCliRs;
                            valor = outValorCol.getText().toString();
                            codVend = codVendRs;
                            statusCabec = outStatusCol.getText().toString();

                            if (!CheckConnection.isConnected(ColetaBusca.this)) {
                                cardPDF.setVisibility(View.GONE);
                                cardEnviar.setVisibility(View.GONE);
                                cardEmail.setVisibility(View.GONE);
                                cardWhats.setVisibility(View.GONE);
                            }

                            cardExcluir.setVisibility(View.GONE);

                            if (statusColeta.equals("1")) {
                                if (CheckConnection.isConnected(ColetaBusca.this)) {
                                    cardEnviar.setVisibility(View.VISIBLE);
                                }
                                cardExcluir.setVisibility(View.VISIBLE);
                            } else if (statusColeta.equals("2")) {
                                if (CheckConnection.isConnected(ColetaBusca.this)) { //DISPONIVEL OPÇÃO DE REENVIO
                                    txtEnviar.setText("Reenviar");
                                    cardEnviar.setVisibility(View.VISIBLE);
                                }
                            }

                            cardVisualizar.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    db.buscaColetaItens("SELECT COL_IT_FILIAL, COL_IT_IDENTIF, COL_IT_ITEM, COL_IT_DATA, COL_IT_COD_CLI, COL_IT_LOJA_CLI, " +
                                            "                        COL_IT_QTD, COL_IT_VALOR_UNIT, COL_IT_VALOR, COL_IT_COD_PROD, COL_IT_BITOLA, COL_IT_MARCA, " +
                                            "                        COL_IT_MODELO, COL_IT_SERIE, COL_IT_DOT, COL_IT_MONTADO, COL_IT_DESENHO, COL_IT_URGENTE, " +
                                            "                        COL_IT_PERC_COM_BORR, COL_IT_VLR_COM_BORR, COL_IT_STATUS_ENVIO, COL_IT_OBS," +
                                            "                        COL_IT_C_AGUA, COL_IT_C_CAMARA, COL_IT_GARANTIA " +
                                            "FROM COLETA_ITENS " +
                                            "WHERE 1 = 1 " +
                                            "  AND trim(COL_IT_FILIAL) = '" + codFilRs.trim() + "' " +
                                            "  AND trim(COL_IT_IDENTIF) = '" + outNumCol.getText().toString().trim() + "' " +
                                            "  AND trim(COL_IT_DATA) = '" + GettersSetters.converteData(outDataCole.getText().toString(), "BR").trim() + "' " +
                                            "  AND trim(COL_IT_COD_CLI) = '" + codCliRs.trim() + "' " +
                                            "  AND trim(COL_IT_LOJA_CLI) = '" + lojaCli.trim() + "' " +
                                            "GROUP BY COL_IT_FILIAL, COL_IT_IDENTIF, COL_IT_ITEM, COL_IT_DATA, COL_IT_COD_CLI, COL_IT_LOJA_CLI, " +
                                            "         COL_IT_QTD, COL_IT_VALOR_UNIT, COL_IT_VALOR, COL_IT_COD_PROD, COL_IT_BITOLA, COL_IT_MARCA, " +
                                            "         COL_IT_MODELO, COL_IT_SERIE, COL_IT_DOT, COL_IT_MONTADO, COL_IT_DESENHO, COL_IT_URGENTE, " +
                                            "         COL_IT_PERC_COM_BORR, COL_IT_VLR_COM_BORR, COL_IT_STATUS_ENVIO, COL_IT_OBS," +
                                            "         COL_IT_C_AGUA, COL_IT_C_CAMARA, COL_IT_GARANTIA " +
                                            "ORDER BY CAST(COL_IT_ITEM AS NUMERIC)");

                                    AlertDialog.Builder builderItens = new AlertDialog.Builder(ColetaBusca.this);
                                    builderItens.setCancelable(true);
                                    LayoutInflater layoutInflaterAndroid = getLayoutInflater();
                                    View view1 = layoutInflaterAndroid.inflate(R.layout.view_generica, null);
                                    LinearLayout llView = view1.findViewById(R.id.llViewGenerica);

                                    llView.removeAllViews();

                                    for (int i = 0; i < db.arrBuscaColetaItFilial.size(); i++) {
                                        final View addViewItens = layoutInflaterAndroid.inflate(R.layout.view_visual_itens_coleta, llView, false);

                                        if (i % 2 == 0) {
                                            addViewItens.setBackgroundColor(getResources().getColor(R.color.backgroundcolor));
                                        }

                                        final TextView outIdentifItem = addViewItens.findViewById(R.id.cmpIdentificador);
                                        final TextView outItemCol = addViewItens.findViewById(R.id.txtItem);
                                        final TextView outQtdCol = addViewItens.findViewById(R.id.cmpQtdCol);
                                        final TextView outBitCol = addViewItens.findViewById(R.id.cmpBitCol);
                                        final TextView outMarcaCol = addViewItens.findViewById(R.id.cmpMarcaCol);
                                        final TextView outModelCol = addViewItens.findViewById(R.id.cmpModCol);
                                        final TextView outSerieCol = addViewItens.findViewById(R.id.cmpSerCol);
                                        final TextView outDotCol = addViewItens.findViewById(R.id.cmpDotCol);
                                        final ToggleButton outMontado = addViewItens.findViewById(R.id.toggleButtonMontado);
                                        final TextView outDesenCol = addViewItens.findViewById(R.id.cmpDesCol);
                                        final TextView outVlrUnit = addViewItens.findViewById(R.id.cmpValUnit);
                                        final TextView outValorTotal = addViewItens.findViewById(R.id.cmpValorTotal);
                                        final CheckBox outUrgCol = addViewItens.findViewById(R.id.chkUrgente);
                                        final TextView outPercComissBorr = addViewItens.findViewById(R.id.cmpPrcComissBorr);
                                        final TextView outVlrComissBorr = addViewItens.findViewById(R.id.cmpVlrComissBorr);
                                        final TextView outObservacao = addViewItens.findViewById(R.id.cmpObsItem);
                                        final LinearLayout llPercComBorr = addViewItens.findViewById(R.id.llComissBorr);
                                        final TextView txtComissBorr = addViewItens.findViewById(R.id.txtComissBorr);
                                        final EditText outStatus = addViewItens.findViewById(R.id.cmpStatusIt);
                                        final TextView txtStatusIt = addViewItens.findViewById(R.id.txtStatusIt);

                                        txtStatusIt.setVisibility(View.GONE);
                                        outStatus.setVisibility(View.GONE);

                                        outIdentifItem.setText(db.arrBuscaColetaItIdentif.get(i) + " / " + db.arrBuscaColetaItItem.get(i));
                                        outItemCol.setText(db.arrBuscaColetaItItem.get(i));
                                        outQtdCol.setText(db.arrBuscaColetaItQtd.get(i));
                                        outBitCol.setText(db.arrBuscaColetaItBitola.get(i));
                                        outMarcaCol.setText(db.arrBuscaColetaItMarca.get(i));
                                        outModelCol.setText(db.arrBuscaColetaItModelo.get(i));
                                        outSerieCol.setText(db.arrBuscaColetaItSerie.get(i));
                                        outDotCol.setText(db.arrBuscaColetaItDot.get(i));
                                        outDesenCol.setText(db.arrBuscaColetaItDesenho.get(i));
                                        outVlrUnit.setText("R$ " + df.format(Double.parseDouble(db.arrBuscaColetaItVlrUnit.get(i))));
                                        outValorTotal.setText("R$ " + df.format(Double.parseDouble(db.arrBuscaColetaItVlrTotal.get(i))));

                                        if (!db.arrBuscaColetaItPercComBorr.get(i).equals("0")) {
                                            outPercComissBorr.setText(db.arrBuscaColetaItPercComBorr.get(i) + "%");
                                            outVlrComissBorr.setText("R$ " + df.format(Double.parseDouble(db.arrBuscaColetaItVlrComBorr.get(i))));
                                        } else {
                                            txtComissBorr.setVisibility(View.GONE);
                                            llPercComBorr.setVisibility(View.GONE);
                                        }

                                        if (db.arrBuscaColetaItUrgente.get(i).equals("1")) {
                                            outUrgCol.setChecked(true);
                                            outUrgCol.setTextColor(getResources().getColor(R.color.red));
                                        } else {
                                            outUrgCol.setChecked(false);
                                            outUrgCol.setTextColor(getResources().getColor(R.color.green));
                                        }

                                        if (db.arrBuscaColetaItMontado.get(i).equals("0")) {
                                            outMontado.setChecked(true);
                                            outMontado.setTextColor(getResources().getColor(R.color.red));
                                        } else {
                                            outMontado.setChecked(false);
                                            outMontado.setTextColor(getResources().getColor(R.color.green));
                                        }

                                        outObservacao.setText(db.arrBuscaColetaItObs.get(i));

                                        llView.addView(addViewItens);
                                    }
                                    builderItens.setView(view1);
                                    builderItens.show();
                                }
                            });

                            cardEnviar.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (statusColeta.equals("2")) {
                                        final AlertDialog.Builder builderConexao = new AlertDialog.Builder(ColetaBusca.this);
                                        builderConexao.setTitle("Confirma o REENVIO da Coleta " + outNumCol.getText().toString() + "?");
                                        builderConexao.setIcon(R.drawable.atencao);
                                        builderConexao.setCancelable(false);
                                        builderConexao.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                AlertDialog.Builder builder1 = new AlertDialog.Builder(ColetaBusca.this);
                                                builder1.setTitle("Reenvio de Coletas");
                                                builder1.setIcon(R.drawable.ampulheta);
                                                builder1.setCancelable(true);
                                                builder1.setMessage("Aguarde...");
                                                alertDialog = builder1.create();
                                                alertDialog.show();

                                                dialogOpcoes.dismiss();

                                                Handler handler = new Handler();
                                                handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        if (CheckConnection.isConnected(ColetaBusca.this)) {
                                                            ((AlertDialog) dialogInterface).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                                                            ((AlertDialog) dialogInterface).getButton(AlertDialog.BUTTON_NEGATIVE).setEnabled(false);

                                                            //                                                ResultSet rsCabec = conecta.buscaColetasExtCabec(outDataCole.getText().toString(), outNumCol.getText().toString(), outNomeCole.getText().toString(),
                                                            //                                                        codFilRs, codVendRs, true, "99");

                                                            ResultSet rsCabec99 = conecta.buscaColetasReenvio(outNumCol.getText().toString(), "99");
                                                            ResultSet rsCabecGeral = conecta.buscaColetasReenvio(outNumCol.getText().toString(), "");
                                                            String query;
                                                            int totalDados99, totalDadosGeral;

                                                            try {
                                                                rsCabec99.last();
                                                                totalDados99 = rsCabec99.getRow();
                                                                rsCabec99.beforeFirst();

                                                                rsCabecGeral.last();
                                                                totalDadosGeral = rsCabecGeral.getRow();
                                                                rsCabecGeral.beforeFirst();

                                                                if (totalDados99 > 0 || totalDadosGeral == 0) {
                                                                    /* DELETA COLETA **/
                                                                    if (conecta.deleteColeta(codFilRs, GettersSetters.converteData(outDataCole.getText().toString(), "BR"), outNumCol.getText().toString(), true) ||
                                                                            totalDadosGeral == 0) {
                                                                        /* REENVIO **/
                                                                        query = "UPDATE COLETA_CABEC " +
                                                                                "SET COL_CABEC_STATUS_ENVIO = '1' " +
                                                                                "WHERE trim(COL_CABEC_FILIAL) = '" + codFilRs.trim() + "' " +
                                                                                " AND trim(COL_CABEC_IDENTIF) = '" + outNumCol.getText().toString().trim() + "'" +
                                                                                " AND trim(COL_CABEC_STATUS_ENVIO) = '2' " +
                                                                                " AND trim(COL_CABEC_DATA) = '" + GettersSetters.converteData(outDataCole.getText().toString(), "BR").trim() + "'";

                                                                        if (db.updColeta(query)) {
                                                                            query = "UPDATE COLETA_ITENS " +
                                                                                    "SET COL_IT_STATUS_ENVIO = '1' " +
                                                                                    "WHERE trim(COL_IT_FILIAL) = '" + codFilRs.trim() + "' " +
                                                                                    " AND trim(COL_IT_IDENTIF) = '" + outNumCol.getText().toString().trim() + "'" +
                                                                                    " AND trim(COL_IT_STATUS_ENVIO) = '2' " +
                                                                                    " AND trim(COL_IT_COD_CLI) = '" + codCliRs.trim() + "'" +
                                                                                    " AND trim(COL_IT_LOJA_CLI) = '" + lojaCliRs.trim() + "' " +
                                                                                    " AND trim(COL_IT_DATA) = '" + GettersSetters.converteData(outDataCole.getText().toString(), "BR").trim() + "'";
                                                                            db.updColeta(query);
                                                                        }

                                                                        db.updColeta("UPDATE COLETA_CLI " +
                                                                                " SET COL_CLI_STATUS_ENVIO = '1' " +
                                                                                "WHERE trim(COL_CLI_FILIAL) = '" + codFilRs.trim() + "' " +
                                                                                "  AND trim(COL_CLI_IDENTIF) = '" + outNumCol.getText().toString().trim() + "'" +
                                                                                "  AND trim(COL_CLI_STATUS_ENVIO) = '2' " +
                                                                                "  AND trim(COL_CLI_COD_CLI) = '" + codCliRs.trim() + "'" +
                                                                                "  AND trim(COL_CLI_LOJA_CLI) = '" + lojaCliRs.trim() + "' " +
                                                                                "  AND trim(COL_CLI_DATA) = '" + GettersSetters.converteData(outDataCole.getText().toString(), "BR").trim() + "'");

                                                                        Cursor resColeta = db.buscaColetasNaoEnviadas(outNumCol.getText().toString(), codFilRs, false, codVendRs); //BUSCA COLETAS JÁ ENVIADAS, POREM COM STATUS 2 NO COLETA_CABEC
                                                                        if (resColeta.getCount() > 0) {
                                                                            if (CheckConnection.isConnected(ColetaBusca.this)) {
                                                                                enviaColetas = new EnviaColetas();
                                                                                enviaColetas.execute(outNumCol.getText().toString(), codFilRs, codVendRs);
                                                                            } else {
                                                                                final AlertDialog.Builder builder = new AlertDialog.Builder(ColetaBusca.this);
                                                                                builder.setIcon(R.drawable.atencao);
                                                                                builder.setCancelable(false);
                                                                                builder.setTitle("Envio Cancelado!");
                                                                                builder.setMessage("O aparelho foi DESCONECTADO da INTERNET, o envio para o Sistema deve ser feito posteriormente!");
                                                                                builder.setNeutralButton("Fechar", null);
                                                                                builder.show();
                                                                            }
                                                                        } else {
                                                                            AlertDialog.Builder builder1 = new AlertDialog.Builder(ColetaBusca.this);
                                                                            builder1.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.sending, null));
                                                                            builder1.setTitle("Envio");
                                                                            builder1.setMessage("Nenhuma Coleta a ser Enviada.");
                                                                            builder1.setCancelable(false);
                                                                            builder1.setPositiveButton("Fechar", null);
                                                                            builder1.show();
                                                                        }
                                                                    } else {
                                                                        AlertDialog.Builder builder1 = new AlertDialog.Builder(ColetaBusca.this);
                                                                        builder1.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.sending, null));
                                                                        builder1.setTitle("Reenvio!");
                                                                        builder1.setMessage("Coleta não pôde ser reenviada. Entre em contato com o Administrado!");
                                                                        builder1.setCancelable(false);
                                                                        builder1.setPositiveButton("Fechar", null);
                                                                        builder1.show();
                                                                    }
                                                                } else {
                                                                    AlertDialog.Builder builder1 = new AlertDialog.Builder(ColetaBusca.this);
                                                                    builder1.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.sending, null));
                                                                    builder1.setTitle("Reenvio");
                                                                    builder1.setMessage("NENHUMA Coleta a ser REENVIADA.\nVerifique com o CRÉDITO se a Coleta está apta ao reenvio.");
                                                                    builder1.setCancelable(false);
                                                                    builder1.setPositiveButton("Fechar", null);
                                                                    builder1.show();
                                                                }
                                                            } catch (SQLException e) {
                                                                AlertDialog.Builder builder1 = new AlertDialog.Builder(ColetaBusca.this);
                                                                builder1.setTitle("Erro reenviar coleta!");
                                                                builder1.setMessage(e.toString());
                                                                builder1.setCancelable(false);
                                                                builder1.setPositiveButton("Fechar", null);
                                                                builder1.show();
                                                            }
                                                        } else {
                                                            final AlertDialog.Builder builder = new AlertDialog.Builder(ColetaBusca.this);
                                                            builder.setIcon(R.drawable.atencao);
                                                            builder.setCancelable(false);
                                                            builder.setTitle("Envio Cancelado!");
                                                            builder.setMessage("O aparelho foi DESCONECTADO da INTERNET, o envio para o Sistema deve ser feito posteriormente!");
                                                            builder.setNegativeButton("Fechar", null);
                                                            builder.show();
                                                        }
                                                        alertDialog.dismiss();
                                                    }
                                                }, 2000);
                                            }
                                        });
                                        builderConexao.setNegativeButton("Não", null);
                                        AlertDialog dialog = builderConexao.create();
                                        dialog.show();
                                    } else {
                                        Cursor resColeta = db.buscaColetasNaoEnviadas(outNumCol.getText().toString(), codFilRs, false, codVendRs);
                                        if (resColeta.getCount() > 0) {
                                            dialogOpcoes.dismiss();

                                            if (CheckConnection.isConnected(ColetaBusca.this)) {
                                                enviaColetas = new EnviaColetas();
                                                enviaColetas.execute(outNumCol.getText().toString(), codFilRs, codVendRs);
                                            } else {
                                                final AlertDialog.Builder builder = new AlertDialog.Builder(ColetaBusca.this);
                                                builder.setIcon(R.drawable.atencao);
                                                builder.setCancelable(false);
                                                builder.setTitle("Envio Cancelado!");
                                                builder.setMessage("O aparelho foi DESCONECTADO da INTERNET, o envio para o Sistema deve ser feito posteriormente!");
                                                builder.setNeutralButton("Fechar", null);
                                                builder.show();
                                            }
                                        } else {
                                            AlertDialog.Builder builder1 = new AlertDialog.Builder(ColetaBusca.this);
                                            builder1.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.atencao, null));
                                            builder1.setTitle("Envio de Coletas");
                                            builder1.setMessage("Nenhuma coleta pendente de envio. Realize a busca novamente!");
                                            builder1.setCancelable(false);
                                            builder1.setPositiveButton("Fechar", null);
                                            builder1.show();
                                        }
                                    }
                                }
                            });

                            cardExcluir.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(final View view) {
                                    dialogOpcoes.dismiss();

                                    final AlertDialog.Builder builder = new AlertDialog.Builder(ColetaBusca.this);
                                    builder.setCancelable(false);
                                    builder.setTitle("ATENÇÃO!");
                                    builder.setIcon(R.drawable.exclamation);
                                    builder.setMessage("Confirma exclusão da coleta " + outNumCol.getText().toString() + "?\nEste processo não pode ser desfeito!!");
                                    builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();

                                            String qryDelColetasCabec = "DELETE FROM COLETA_CABEC " +
                                                    "WHERE trim(COL_CABEC_FILIAL) = '" + codFilRs.trim() + "'" +
                                                    " AND trim(COL_CABEC_IDENTIF) = '" + outNumCol.getText().toString().trim() + "'" +
                                                    " AND trim(COL_CABEC_DATA) = '" + GettersSetters.converteData(outDataCole.getText().toString(), "BR").trim() + "'" +
                                                    " AND trim(COL_CABEC_COD_CLI) = '" + codCliRs.trim() + "'" +
                                                    " AND trim(COL_CABEC_LOJA_CLI) = '" + lojaCliRs.trim() + "'";

                                            String qryDelColetaItens = "DELETE FROM COLETA_ITENS " +
                                                    "WHERE trim(COL_IT_FILIAL) = '" + codFilRs.trim() + "'" +
                                                    " AND trim(COL_IT_IDENTIF) = '" + outNumCol.getText().toString().trim() + "'" +
                                                    " AND trim(COL_IT_DATA) = '" + GettersSetters.converteData(outDataCole.getText().toString(), "BR").trim() + "'" +
                                                    " AND trim(COL_IT_COD_CLI) = '" + codCliRs.trim() + "'" +
                                                    " AND trim(COL_IT_LOJA_CLI) = '" + lojaCliRs.trim() + "'";

                                            String qryBuscaCliColeta = "SELECT COL_CLI_NOME FROM COLETA_CLI " +
                                                    "WHERE trim(COL_CLI_FILIAL) = '" + codFilRs.trim() + "'" +
                                                    " AND trim(COL_CLI_IDENTIF) = '" + outNumCol.getText().toString().trim() + "'" +
                                                    " AND trim(COL_CLI_DATA) = '" + GettersSetters.converteData(outDataCole.getText().toString(), "BR").trim() + "'" +
                                                    " AND trim(COL_CLI_COD_CLI) = '" + codCliRs.trim() + "'" +
                                                    " AND trim(COL_CLI_LOJA_CLI) = '" + lojaCliRs.trim() + "'";

                                            Cursor cursor = db.buscaColetaCli(qryBuscaCliColeta, true);
                                            String qryDelCliColeta = "";
                                            if (cursor.getCount() > 0) {
                                                qryDelCliColeta = "DELETE FROM COLETA_CLI " +
                                                        "WHERE trim(COL_CLI_FILIAL) = '" + codFilRs.trim() + "'" +
                                                        " AND trim(COL_CLI_IDENTIF) = '" + outNumCol.getText().toString().trim() + "'" +
                                                        " AND trim(COL_CLI_DATA) = '" + GettersSetters.converteData(outDataCole.getText().toString(), "BR").trim() + "'" +
                                                        " AND trim(COL_CLI_COD_CLI) = '" + codCliRs.trim() + "'" +
                                                        " AND trim(COL_CLI_LOJA_CLI) = '" + lojaCliRs.trim() + "'";
                                            }

                                            boolean resultado = db.delColeta(qryDelColetasCabec, qryDelColetaItens, qryDelCliColeta);
                                            if (resultado) {
                                                Snackbar.make(viewSnackBar, "Coleta " + outNumCol.getText().toString() + " excluída com sucesso!", Snackbar.LENGTH_LONG).show();
                                                formItensColetaBusca.removeAllViews();
                                                buscaColetasInt = new BuscaColetasInt();
                                                buscaColetasInt.execute(String.valueOf(spnStatusColeta.getSelectedItemPosition()),
                                                        cmpNomeCliBusca.getText().toString().replace(" ", "%"),
                                                        cmpNumColeta.getText().toString(), cmpDataBusca.getText().toString(), filialBusca);
                                            } else {
                                                Snackbar.make(viewSnackBar, "Coleta " + outNumCol.getText().toString() + " não excluída!", Snackbar.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                    builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    });
                                    builder.show();
                                }
                            });

                            cardPDF.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    statusColetaTemp = statusColeta;

                                    numeroColeta = outNumCol.getText().toString();
                                    isShowPDF = true;

                                    geraPDF = new GeraPDF();
                                    geraPDF.execute(outNomeCole.getText().toString(), outNumCol.getText().toString(), outDataCole.getText().toString(), codFilRs,
                                            lojaCliRs, codCliRs, outValorCol.getText().toString(), codVendRs, "");
                                }
                            });

                            cardWhats.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent sendIntent = new Intent();
                                    sendIntent.setPackage(null);
                                    if (ColetaBusca.this.getPackageManager().getLaunchIntentForPackage("com.whatsapp") != null) {
                                        sendIntent.setPackage("com.whatsapp");
                                    } else if (ColetaBusca.this.getPackageManager().getLaunchIntentForPackage("com.whatsapp.w4b") != null) {
                                        sendIntent.setPackage("com.whatsapp.w4b"); //WPP BUSINESS
                                    }

                                    if (sendIntent.getPackage() != null) {
                                        dialogOpcoes.dismiss();

                                        isShowPDF = false;
                                        isSendWhats = true;

                                        geraPDF = new GeraPDF();
                                        geraPDF.execute(outNomeCole.getText().toString(), outNumCol.getText().toString(), outDataCole.getText().toString(), codFilRs,
                                                lojaCliRs, codCliRs, outValorCol.getText().toString(), codVendRs, "");
                                    } else {
                                        AlertDialog.Builder builderFilial = new AlertDialog.Builder(ColetaBusca.this);
                                        builderFilial.setTitle("WhatsApp");
                                        builderFilial.setIcon(R.drawable.error);
                                        builderFilial.setCancelable(false);
                                        builderFilial.setMessage("Aplicativo WhatsApp não instalado!");
                                        builderFilial.setPositiveButton("OK", null);
                                        AlertDialog dialog = builderFilial.create();
                                        dialog.show();
                                    }
                                }
                            });

                            cardEmail.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialogOpcoes.dismiss();

                                    isShowPDF = false;
                                    isSendWhats = false;

                                    geraPDF = new GeraPDF();
                                    geraPDF.execute(outNomeCole.getText().toString(), outNumCol.getText().toString(), outDataCole.getText().toString(), codFilRs,
                                            lojaCliRs, codCliRs, outValorCol.getText().toString(), codVendRs, "");

                                }
                            });

                            cardImprime.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    String condPgto = "";
                                    String formaPgto = "";

                                    String query = "SELECT COL_IT_FILIAL, COL_IT_IDENTIF, COL_IT_ITEM, COL_IT_DATA, COL_IT_COD_CLI, COL_IT_LOJA_CLI, " +
                                            "              COL_IT_QTD, COL_IT_VALOR_UNIT, COL_IT_VALOR, COL_IT_COD_PROD, COL_IT_BITOLA, COL_IT_MARCA, " +
                                            "              COL_IT_MODELO, COL_IT_SERIE, COL_IT_DOT, COL_IT_MONTADO, COL_IT_DESENHO, COL_IT_URGENTE, " +
                                            "              COL_IT_PERC_COM_BORR, COL_IT_VLR_COM_BORR, COL_IT_STATUS_ENVIO, COL_IT_OBS," +
                                            "              COL_IT_C_AGUA, COL_IT_C_CAMARA, COL_IT_GARANTIA " +
                                            "FROM COLETA_ITENS " +
                                            "WHERE 1 = 1 " +
                                            "  AND trim(COL_IT_FILIAL) = '" + codCliRs.trim() + "' " +
                                            "  AND trim(COL_IT_IDENTIF) = '" + outNumCol.getText().toString() + "' " +
                                            "  AND trim(COL_IT_DATA) = '" + GettersSetters.converteData(outDataCole.getText().toString(), "BR") + "'" +
                                            "  AND trim(COL_IT_COD_CLI) = '" + codCliRs.trim() + "'" +
                                            "  AND trim(COL_IT_LOJA_CLI) = '" + lojaCliRs.trim() + "'" +
                                            "GROUP BY COL_IT_FILIAL, COL_IT_IDENTIF, COL_IT_ITEM, COL_IT_DATA, COL_IT_COD_CLI, COL_IT_LOJA_CLI, " +
                                            "         COL_IT_QTD, COL_IT_VALOR_UNIT, COL_IT_VALOR, COL_IT_COD_PROD, COL_IT_BITOLA, COL_IT_MARCA, " +
                                            "         COL_IT_MODELO, COL_IT_SERIE, COL_IT_DOT, COL_IT_MONTADO, COL_IT_DESENHO, COL_IT_URGENTE, " +
                                            "         COL_IT_PERC_COM_BORR, COL_IT_VLR_COM_BORR, COL_IT_STATUS_ENVIO, COL_IT_OBS," +
                                            "         COL_IT_C_AGUA, COL_IT_C_CAMARA, COL_IT_GARANTIA " +
                                            "ORDER BY CAST(COL_IT_ITEM AS NUMERIC)";

                                    Cursor cursorItens = db.buscaColetaItens(query);
                                    db.selectUsuariosVendedores(codVendRs.trim(), "V", true, false);
                                    db.buscaClientesColeta(outDocCli.getText().toString().replace(".", "").replace("-", "").replace("/", ""), outNomeCole.getText().toString(), "", new ArrayList<>(), codCliRs.trim(), lojaCliRs.trim());

                                    Cursor resBuscaCondPgColeta = db.buscaCondPgColeta(outNumCol.getText().toString(), codFilRs.trim(), outDocCli.getText().toString());
                                    if (resBuscaCondPgColeta != null && resBuscaCondPgColeta.moveToFirst()) {
                                        do {
                                            condPgto = resBuscaCondPgColeta.getString(0);
                                            formaPgto = resBuscaCondPgColeta.getString(1);
                                        } while (resBuscaCondPgColeta.moveToNext());
                                    }

                                    ImprimeColeta imprimeColeta = new ImprimeColeta();
                                    imprimeColeta.inicializa(ColetaBusca.this);

                                    Printooth.INSTANCE.init(ColetaBusca.this);
                                    if (!Printooth.INSTANCE.hasPairedPrinter()) {
                                        startActivityForResult(new Intent(ColetaBusca.this, ScanningActivity.class), ScanningActivity.SCANNING_FOR_PRINTER);
                                    } else {
                                        /* IMPRESSÃO DA COLETA **/
                                        imprimeColeta.impressaoDeColetas(outNumCol.getText().toString(), outDataCole.getText().toString(), db.buscaFiliaisDescr(codFilRs.trim()),
                                                outDocCli.getText().toString(), outNomeCole.getText().toString(),
                                                db.endCli + " - " + db.bairroCli + " - " + db.munCli + "/" + db.ufCli,
                                                db.dddCli.replace("0", "") + db.foneCli, outValorCol.getText().toString(), cursorItens.getCount(),
                                                db.buscaColetaNomeVend, ColetaBusca.this, db.arrBuscaColetaItBitola, db.arrBuscaColetaItVlrUnit,
                                                ((condPgto.trim().equals("0") ? "A VISTA" : condPgto.trim() + " dias") + " " + formaPgto.trim()), db.arrBuscaColetaItMontado, db.arrBuscaColetaItSerie,
                                                db.arrBuscaColetaItDot, db.arrBuscaColetaItMarca);
                                    }
                                }
                            });
                            dialogOpcoes.show();
                        }
                    });
                    formItensColetaBusca.addView(addView);
                }
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(ColetaBusca.this);
                builder.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.buscacoletas, null));
                builder.setTitle("Busca de Coletas");
                builder.setMessage("Nenhuma Coleta localizada para os dados informados.\nVerifique os dados e tente novamente!");
                builder.setPositiveButton("Fechar", null);
                builder.show();
            }

            if (dialogBuscaColInt != null) {
                dialogBuscaColInt.dismiss();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class BuscaColetasExt extends AsyncTask<String, String, ResultSet> {
        AlertDialog dialogBuscaColExt = null;

        @Override
        protected void onPreExecute() {
            AlertDialog.Builder builder = new AlertDialog.Builder(ColetaBusca.this);
            LayoutInflater layoutInflater = getLayoutInflater();
            View dialogView = layoutInflater.inflate(R.layout.dialog_carregamento, null);
            TextView textView = dialogView.findViewById(R.id.txtInfoCarreg);
            textView.setText("Buscando Coletas...\nAguarde...");
            builder.setCancelable(false);
            builder.setPositiveButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    buscaColetasExt.cancel(true);
                    Toast.makeText(ColetaBusca.this, "Busca Cancelada", Toast.LENGTH_LONG).show();
                    try {
                        conecta.preparedStatement.cancel();
                        conecta.connection.close();
                    } catch (SQLException e){
                        e.printStackTrace();
                    }
                }
            });
            builder.setView(dialogView);
            dialogBuscaColExt = builder.create();
            dialogBuscaColExt.show();
        }

        @Override
        protected ResultSet doInBackground(String... params) {
            return conecta.buscaColetasExtCabec(params[0], params[1], params[2], params[4], params[5], false, params[3], params[7]);
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(final ResultSet rsCabec) {
            formItensColetaBusca.removeAllViews();
            int totalDados = 0;

            if (rsCabec != null) {
                try {
                    rsCabec.last();
                    totalDados = rsCabec.getRow();
                    rsCabec.beforeFirst();
                } catch (SQLException e) {
                    e.printStackTrace();
                    totalDados = 0;
                }

                if (totalDados > 0) {
                    txtQtdColExt.setVisibility(View.VISIBLE);
                    txtQtdColExt.setText(totalDados > 1 ? "[ " + totalDados + " ] Coletas localizadas!" : "[ " + totalDados + " ] Coleta localizada!");

                    if (dialogBuscaColExt != null) {
                        dialogBuscaColExt.dismiss();
                    }

                    buscaColetasExt.cancel(true);
                    buscaColetaCabecExtDados = new BuscaColetaCabecExtDados();
                    if (totalDados > 200) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ColetaBusca.this);
                        builder.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.buscacoletas, null));
                        builder.setTitle("Busca de Coletas");
                        builder.setMessage("Foram localizadas " + totalDados + " coletas. Deseja continuar?\nATENÇÃO: Esta consulta pode demorar e ocorrer travamentos no dispositivo!");
                        builder.setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                buscaColetaCabecExtDados.execute(rsCabec);
                            }
                        });
                        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                txtQtdColExt.setVisibility(View.GONE);
                            }
                        });
                        builder.show();
                    } else {
                        buscaColetaCabecExtDados.execute(rsCabec);
                    }
                } else {
                    if (dialogBuscaColExt != null) {
                        dialogBuscaColExt.dismiss();
                    }

                    txtQtdColExt.setVisibility(View.GONE);

                    AlertDialog.Builder builder = new AlertDialog.Builder(ColetaBusca.this);
                    builder.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.buscacoletas, null));
                    builder.setTitle("Busca de Coletas");
                    builder.setMessage("Nenhuma Coleta localizada para os dados informados.\nVerifique os dados e tente novamente!");
                    builder.setPositiveButton("Fechar", null);
                    builder.show();
                }
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(ColetaBusca.this);
                builder.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.error, null));
                builder.setTitle("Busca de Coletas");
                builder.setMessage("Erro: " + GettersSetters.getErroEnvioColetaBDExt());
                builder.setPositiveButton("Fechar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (dialogBuscaColExt != null) {
                            dialogBuscaColExt.dismiss();
                        }
                    }
                });
                builder.show();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class BuscaColetaCabecExtDados extends AsyncTask<ResultSet, View, ArrayList<View>> {
        AlertDialog dialogBuscaColExtDados = null;

        @Override
        protected void onPreExecute() {
            AlertDialog.Builder builder = new AlertDialog.Builder(ColetaBusca.this);
            LayoutInflater layoutInflater = getLayoutInflater();
            View dialogView = layoutInflater.inflate(R.layout.dialog_carregamento, null);
            TextView textView = dialogView.findViewById(R.id.txtInfoCarreg);
            textView.setText("Processando Coletas...\nAguarde...");
            builder.setCancelable(false);
            builder.setPositiveButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    buscaColetaCabecExtDados.cancel(true);
                    Toast.makeText(ColetaBusca.this, "Busca Cancelada", Toast.LENGTH_LONG).show();
                }
            });
            builder.setView(dialogView);
            dialogBuscaColExtDados = builder.create();
            dialogBuscaColExtDados.show();
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected ArrayList<View> doInBackground(ResultSet... rsCabec) {
            ArrayList<View> arrayList = new ArrayList<>();

            try {
                LayoutInflater layoutInflater = getLayoutInflater();
                while (rsCabec[0].next()) {
                    View addView = layoutInflater.inflate(R.layout.view_coleta, formItensColetaBusca, false);
                    final CardView btnOpcoes = addView.findViewById(R.id.btnOpcoes);
                    final TextView outStatusCol = addView.findViewById(R.id.txtStatusCol);
                    final TextView outFilialColDesc = addView.findViewById(R.id.txtFilialDesc);
                    final TextView outNumeroCol = addView.findViewById(R.id.txtNumeroCol);
                    final TextView outDataCole = addView.findViewById(R.id.txtDataCol);
                    final TextView outDocCli = addView.findViewById(R.id.txtDocCli);
                    final TextView outNomeCole = addView.findViewById(R.id.txtNomeCol);
                    final TextView outValorCol = addView.findViewById(R.id.txtValUnit);
                    final TextView outTxtVended = addView.findViewById(R.id.txtVended);
                    final LinearLayout llPedido = addView.findViewById(R.id.llPedido);
                    final TextView outPedido = addView.findViewById(R.id.txtPedido);
                    final LinearLayout llVale = addView.findViewById(R.id.llVale);
                    final TextView outVale = addView.findViewById(R.id.txtVale);
                    final LinearLayout llNota = addView.findViewById(R.id.llNota);
                    final TextView outNota = addView.findViewById(R.id.txtNota);

                    llVale.setVisibility(View.GONE);
                    llNota.setVisibility(View.GONE);

                    final String codFilRs = rsCabec[0].getString(1).trim();
                    final String codCliRs = rsCabec[0].getString(4).trim();
                    final String lojaCliRs = rsCabec[0].getString(5).trim();
                    final String codVendRs = rsCabec[0].getString(29).trim();
                    final String statusCabecRs = rsCabec[0].getString(18).trim();

                    /* Converte data EN do BD para PT **/
                    String dataColeta = GettersSetters.converteData(rsCabec[0].getString(3).substring(0, 4) + "-" + rsCabec[0].getString(3).substring(4, 6) + "-" + rsCabec[0].getString(3).substring(6, 8), "EN");

                    if (rsCabec[0].getString(27) != null) {
                        if (rsCabec[0].getString(27).trim().length() == 14) {
                            documento = rsCabec[0].getString(27).replaceAll("([0-9]{2})([0-9]{3})([0-9]{3})([0-9]{4})([0-9]{2})", "$1\\.$2\\.$3/$4-$5");
                        } else {
                            documento = rsCabec[0].getString(27).replaceAll("([0-9]{3})([0-9]{3})([0-9]{3})([0-9]{2})", "$1\\.$2\\.$3-$4");
                        }
                    }

                    outStatusCol.setText(statusCabecRs);
                    outFilialColDesc.setText(rsCabec[0].getString(30).trim());
                    outNumeroCol.setText(rsCabec[0].getString(2).trim());
                    outTxtVended.setText(rsCabec[0].getString(28).trim());
                    outDataCole.setText(dataColeta + " - " + rsCabec[0].getString(21).trim());
                    outNomeCole.setText(rsCabec[0].getString(6).trim());
                    outValorCol.setText(df.format(Double.parseDouble(rsCabec[0].getString(9))));
                    outDocCli.setText(documento);

                    if (!rsCabec[0].getString(33).trim().equals("")) {
                        llPedido.setVisibility(View.VISIBLE);
                        outPedido.setText(rsCabec[0].getString(33).trim());
                    } else {
                        llPedido.setVisibility(View.GONE);
                    }

                    if (!rsCabec[0].getString(34).trim().equals("/")) {
                        if (rsCabec[0].getString(34).startsWith("XXXX")) {
                            outStatusCol.setText("RECUSADO PRD.");
                            outStatusCol.setTextColor(getResources().getColor(R.color.red));
                        } else {
                            outStatusCol.setText("FATURADO");
                            outStatusCol.setTextColor(getResources().getColor(R.color.cinza));
                            llVale.setVisibility(View.VISIBLE);
                            outVale.setText(rsCabec[0].getString(34).trim());

                            if (!rsCabec[0].getString(35).trim().equals("-")) {
                                llNota.setVisibility(View.VISIBLE);
                                outNota.setText(rsCabec[0].getString(35).trim());
                            } else {
                                llNota.setVisibility(View.GONE);
                            }
                        }
                    } else {
                        llVale.setVisibility(View.GONE);

                        switch (statusCabecRs) {
                            case "2":
                                outStatusCol.setText("ENVIADA");
                                outStatusCol.setTextColor(getResources().getColor(R.color.yellow));
                                break;
                            case "3":
                                outStatusCol.setText("EM ANÁLISE CRÉDITO");
                                outStatusCol.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                                break;
                            case "4":
                            case "5":
                                if (rsCabec[0].getString(33).trim().equals("")) {
                                    outStatusCol.setText("AGUARD. FICHA");
                                    outStatusCol.setTextColor(getResources().getColor(R.color.blue_light));
                                } else {
                                    outStatusCol.setText("PRODUÇÃO");
                                    outStatusCol.setTextColor(getResources().getColor(R.color.green));
                                }
                                break;
                            case "6":
                                outStatusCol.setText("CRÉDITO RECUSADO");
                                outStatusCol.setTextColor(getResources().getColor(R.color.red));
                                break;
                            case "7":
                                outStatusCol.setText("AGUARD. CRÉDITO");
                                outStatusCol.setTextColor(getResources().getColor(R.color.blue));
                                break;
                            default:
                                outStatusCol.setText("EM SISTEMA");
                                outStatusCol.setTextColor(getResources().getColor(R.color.blue_light_2));
                                break;
                        }
                    }

                    btnOpcoes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(ColetaBusca.this);
                            LayoutInflater inflater = getLayoutInflater();
                            View dialogView = inflater.inflate(R.layout.dialog_opcoes_busca_coleta, formItensColetaBusca, false);
                            builder.setTitle("Coleta Número: " + outNumeroCol.getText().toString());
                            builder.setCancelable(true);
                            builder.setView(dialogView);

                            final CardView cardExcluir = dialogView.findViewById(R.id.cardExcluir);
                            final CardView cardEnviar = dialogView.findViewById(R.id.cardEnviar);
                            final CardView cardVisualizar = dialogView.findViewById(R.id.cardVisualizar);
                            final CardView cardPDF = dialogView.findViewById(R.id.cardPDF);
                            final CardView cardWhats = dialogView.findViewById(R.id.cardWhatsapp);
                            final CardView cardEmail = dialogView.findViewById(R.id.cardEmail);
                            final CardView cardImprime = dialogView.findViewById(R.id.cardImprime);

                            final AlertDialog dialog = builder.create();

                            cardExcluir.setVisibility(View.GONE);
                            cardEnviar.setVisibility(View.GONE);

                            nomeColeta = outNomeCole.getText().toString();
                            data = outDataCole.getText().toString();
                            numeroColeta = outNumeroCol.getText().toString();
                            filial = codFilRs;
                            codCli = codCliRs;
                            lojaCli = lojaCliRs;
                            valor = outValorCol.getText().toString();
                            codVend = codVendRs;
                            statusCabec = outStatusCol.getText().toString();

                            cardVisualizar.setOnClickListener(new View.OnClickListener() {
                                @SuppressLint("SetTextI18n")
                                @Override
                                public void onClick(View view) {
                                    dialog.dismiss();

                                    buscaColetasExtItens = new BuscaColetasExtItens();
                                    buscaColetasExtItens.execute(filial, numeroColeta, data, codCli, lojaCli);
                                }
                            });

                            cardPDF.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();

                                    isShowPDF = true;
                                    vale = outVale.getText().toString().trim();

                                    geraPDF = new GeraPDF();
                                    geraPDF.execute(nomeColeta, numeroColeta, data, filial, lojaCli, codCli, valor, codVend, "EXT", status, vale);
                                }
                            });

                            cardWhats.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent sendIntent = new Intent();
                                    sendIntent.setPackage(null);
                                    if (ColetaBusca.this.getPackageManager().getLaunchIntentForPackage("com.whatsapp") != null) {
                                        sendIntent.setPackage("com.whatsapp");
                                    } else if (ColetaBusca.this.getPackageManager().getLaunchIntentForPackage("com.whatsapp.w4b") != null) {
                                        sendIntent.setPackage("com.whatsapp.w4b"); //WPP BUSINESS
                                    }

                                    if (sendIntent.getPackage() != null) {
                                        dialog.dismiss();

                                        isShowPDF = false;
                                        isSendWhats = true;

                                        vale = outVale.getText().toString().trim();

                                        geraPDF = new GeraPDF();
                                        geraPDF.execute(nomeColeta, numeroColeta, data, filial, lojaCli, codCli, valor, codVend, "EXT", status, vale);
                                    } else {
                                        AlertDialog.Builder builderFilial = new AlertDialog.Builder(ColetaBusca.this);
                                        builderFilial.setTitle("WhatsApp");
                                        builderFilial.setIcon(R.drawable.error);
                                        builderFilial.setCancelable(false);
                                        builderFilial.setMessage("Aplicativo WhatsApp não instalado!");
                                        builderFilial.setPositiveButton("OK", null);
                                        AlertDialog dialog = builderFilial.create();
                                        dialog.show();
                                    }
                                }
                            });

                            cardEmail.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialog.dismiss();

                                    isShowPDF = false;
                                    isSendWhats = false;
                                    vale = outVale.getText().toString().trim();

                                    geraPDF = new GeraPDF();
                                    geraPDF.execute(nomeColeta, numeroColeta, data, filial, lojaCli, codCli, valor, codVend, "EXT", status, vale);
                                }
                            });

                            cardImprime.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialog.dismiss();

                                    String condPgto = "";
                                    String formaPgto = "";
                                    int contaItens = 0;
                                    ArrayList<String> arrBitolas = new ArrayList<>();
                                    ArrayList<String> arrValorUnit = new ArrayList<>();
                                    ArrayList<String> arrMontado = new ArrayList<>();
                                    ArrayList<String> arrSerie = new ArrayList<>();
                                    ArrayList<String> arrDot = new ArrayList<>();
                                    ArrayList<String> arrMarca = new ArrayList<>();


                                    ResultSet rsItens = conecta.buscaColetaExtItens(filial.trim(), outNumeroCol.getText().toString(), outDataCole.getText().toString(), codCli.trim(), lojaCli.trim());

                                    db.selectUsuariosVendedores(codVend.trim(), "V", true, false);
                                    db.buscaClientesColeta(outDocCli.getText().toString().replace(".", "").replace("-", "").replace("/", ""), outNomeCole.getText().toString(), "", new ArrayList<>(), codCliRs.trim(), lojaCliRs.trim());

                                    try {
                                        arrBitolas.clear();
                                        arrValorUnit.clear();
                                        arrMontado.clear();
                                        arrSerie.clear();
                                        arrDot.clear();
                                        arrMarca.clear();

                                        while (rsItens.next()) {
                                            contaItens++;

                                            arrValorUnit.add(rsItens.getString(8));
                                            arrBitolas.add(rsItens.getString(11));
                                            arrMarca.add(rsItens.getString(12));
                                            arrSerie.add(rsItens.getString(14));
                                            arrDot.add(rsItens.getString(15));
                                            arrMontado.add(rsItens.getString(16));
                                        }
                                    } catch (SQLException exp) {
                                        exp.printStackTrace();
                                    }

                                    ImprimeColeta imprimeColeta = new ImprimeColeta();
                                    imprimeColeta.inicializa(ColetaBusca.this);

                                    Printooth.INSTANCE.init(ColetaBusca.this);
                                    if (!Printooth.INSTANCE.hasPairedPrinter()) {
                                        startActivityForResult(new Intent(ColetaBusca.this, ScanningActivity.class), ScanningActivity.SCANNING_FOR_PRINTER);
                                    } else {
                                        /* IMPRESSÃO DA COLETA **/
                                        imprimeColeta.impressaoDeColetas(outNumeroCol.getText().toString(), outDataCole.getText().toString(), db.buscaFiliaisDescr(filial.trim()),
                                                outDocCli.getText().toString(), outNomeCole.getText().toString(),
                                                db.endCli + " - " + db.bairroCli + " - " + db.munCli + "/" + db.ufCli,
                                                db.dddCli.replace("0", "") + db.foneCli, outValorCol.getText().toString(), contaItens,
                                                db.buscaColetaNomeVend, ColetaBusca.this, arrBitolas, arrValorUnit,
                                                condPgto.trim() + " " + formaPgto.trim(), arrMontado, arrSerie, arrDot, arrMarca);
                                    }
                                }
                            });

                            cardImprime.setOnLongClickListener(new View.OnLongClickListener() {
                                @Override
                                public boolean onLongClick(View view) {
                                    ImprimeColeta imprimeColeta = new ImprimeColeta();
                                    Printooth.INSTANCE.init(ColetaBusca.this);

                                    if (Printooth.INSTANCE.hasPairedPrinter()) {
                                        Printooth.INSTANCE.removeCurrentPrinter();
                                        imprimeColeta.printingCallback = null;
                                        imprimeColeta.printing = null;

                                        AlertDialog.Builder builder = new AlertDialog.Builder(ColetaBusca.this);
                                        builder.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.buscacoletas, null));
                                        builder.setTitle("Impressora desconectada!");
                                        builder.setMessage("Para imprimir novamente, feche a aplicação e realize a conexão com a nova impressora!!");
                                        builder.setPositiveButton("SAIR", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                Thread.currentThread().interrupt();
                                                GettersSetters.resetGettersSetters();
                                                finish();
                                                finishAffinity();
                                                System.exit(0);
                                                android.os.Process.killProcess(android.os.Process.myPid());
                                            }
                                        });
                                        builder.setNegativeButton("Fechar", null);
                                        builder.show();
                                    }
                                    return true;
                                }
                            });

                            dialog.show();
                        }
                    });
                    arrayList.add(addView);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                final Snackbar snackbar = Snackbar.make(viewSnackBar, "Erro na busca dos dados. Tente novamente!", Snackbar.LENGTH_LONG);
                snackbar.setBackgroundTint(getResources().getColor(R.color.red_light));
                snackbar.setAction("Fechar", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                }).show();
            }
            return arrayList;
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(final ArrayList<View> addView) {
            for (View i : addView) {
                formItensColetaBusca.addView(i);
            }

            if (dialogBuscaColExtDados != null) {
                dialogBuscaColExtDados.cancel();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class BuscaColetasExtItens extends AsyncTask<String, String, ResultSet> {
        AlertDialog dialogBuscaColExt = null;

        @Override
        protected void onPreExecute() {
            AlertDialog.Builder builder = new AlertDialog.Builder(ColetaBusca.this);
            LayoutInflater layoutInflater = getLayoutInflater();
            View dialogView = layoutInflater.inflate(R.layout.dialog_carregamento, null);
            TextView textView = dialogView.findViewById(R.id.txtInfoCarreg);
            textView.setText("Buscando Itens...\nAguarde...");
            builder.setCancelable(false);
            builder.setPositiveButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    buscaColetasExt.cancel(true);
                    Toast.makeText(ColetaBusca.this, "Busca Cancelada", Toast.LENGTH_LONG).show();
                }
            });
            builder.setView(dialogView);
            dialogBuscaColExt = builder.create();
            dialogBuscaColExt.show();
        }

        @Override
        protected ResultSet doInBackground(String... params) {
            return conecta.buscaColetaExtItens(params[0], params[1], params[2], params[3], params[4]);
        }

        @SuppressLint({"SetTextI18n", "DefaultLocale"})
        @Override
        protected void onPostExecute(final ResultSet rsItens) {
            int totalDados;

            if (rsItens != null) {
                try {
                    rsItens.last();
                    totalDados = rsItens.getRow();
                    rsItens.beforeFirst();
                } catch (SQLException e) {
                    e.printStackTrace();
                    totalDados = 0;
                }

                if (totalDados > 0) {
                    AlertDialog.Builder builderItens = new AlertDialog.Builder(ColetaBusca.this);
                    builderItens.setCancelable(true);
                    LayoutInflater layoutInflaterAndroid = getLayoutInflater();
                    View view1 = layoutInflaterAndroid.inflate(R.layout.view_generica, null);
                    LinearLayout llView = view1.findViewById(R.id.llViewGenerica);
                    llView.removeAllViews();
                    try {
                        totalDados = 0;
                        while (rsItens.next()) {
                            final View addViewItens = layoutInflaterAndroid.inflate(R.layout.view_visual_itens_coleta, llView, false);

                            if (totalDados % 2 == 0) {
                                addViewItens.setBackgroundColor(getResources().getColor(R.color.backgroundcolor));
                            }

                            final TextView outIdentifItem = addViewItens.findViewById(R.id.cmpIdentificador);
                            final TextView outItemCol = addViewItens.findViewById(R.id.txtItem);
                            final TextView outQtdCol = addViewItens.findViewById(R.id.cmpQtdCol);
                            final TextView outBitCol = addViewItens.findViewById(R.id.cmpBitCol);
                            final TextView outMarcaCol = addViewItens.findViewById(R.id.cmpMarcaCol);
                            final TextView outModelCol = addViewItens.findViewById(R.id.cmpModCol);
                            final TextView outSerieCol = addViewItens.findViewById(R.id.cmpSerCol);
                            final TextView outDotCol = addViewItens.findViewById(R.id.cmpDotCol);
                            final ToggleButton outMontado = addViewItens.findViewById(R.id.toggleButtonMontado);
                            final TextView outDesenCol = addViewItens.findViewById(R.id.cmpDesCol);
                            final TextView outVlrUnit = addViewItens.findViewById(R.id.cmpValUnit);
                            final TextView outValorTotal = addViewItens.findViewById(R.id.cmpValorTotal);
                            final CheckBox outUrgCol = addViewItens.findViewById(R.id.chkUrgente);
                            final TextView outPercComissBorr = addViewItens.findViewById(R.id.cmpPrcComissBorr);
                            final TextView outVlrComissBorr = addViewItens.findViewById(R.id.cmpVlrComissBorr);
                            final TextView outObservacao = addViewItens.findViewById(R.id.cmpObsItem);
                            final LinearLayout llPercComBorr = addViewItens.findViewById(R.id.llComissBorr);
                            final TextView txtComissBorr = addViewItens.findViewById(R.id.txtComissBorr);
                            final EditText outStatus = addViewItens.findViewById(R.id.cmpStatusIt);
                            final LinearLayout llFicha = addViewItens.findViewById(R.id.llFicha);
                            final EditText cmpFicha = addViewItens.findViewById(R.id.cmpFicha);

                            outIdentifItem.setText(rsItens.getString(2) + "/" + rsItens.getString(3));
                            outItemCol.setText(rsItens.getString(3));
                            outQtdCol.setText(rsItens.getString(7));
                            outBitCol.setText(rsItens.getString(11));
                            outMarcaCol.setText(rsItens.getString(12));
                            outModelCol.setText(rsItens.getString(13));
                            outSerieCol.setText(rsItens.getString(14));
                            outDotCol.setText(rsItens.getString(15));
                            outDesenCol.setText(rsItens.getString(17));
                            outVlrUnit.setText("R$ " + df.format(Double.parseDouble(rsItens.getString(8))));
                            outValorTotal.setText("R$ " + df.format(Double.parseDouble(rsItens.getString(9))));

                            if (!rsItens.getString(19).equals("0")) {
                                outPercComissBorr.setText(rsItens.getString(19) + "%");
                                outVlrComissBorr.setText("R$ " + df.format(Double.parseDouble(rsItens.getString(20))));
                            } else {
                                txtComissBorr.setVisibility(View.GONE);
                                llPercComBorr.setVisibility(View.GONE);
                            }

                            if (rsItens.getString(18).equals("1")) {
                                outUrgCol.setChecked(true);
                                outUrgCol.setTextColor(getResources().getColor(R.color.red));
                            } else {
                                outUrgCol.setChecked(false);
                                outUrgCol.setTextColor(getResources().getColor(R.color.green));
                            }

                            if (rsItens.getString(16).equals("0")) {
                                outMontado.setChecked(true);
                                outMontado.setTextColor(getResources().getColor(R.color.red));
                            } else {
                                outMontado.setChecked(false);
                                outMontado.setTextColor(getResources().getColor(R.color.green));
                            }
                            outObservacao.setText(rsItens.getString(22));

                            if (rsItens.getString(27).trim().equals("1") || rsItens.getString(28).trim().equals("99")) {
                                outStatus.setText("RECUSADO");
                                if (!rsItens.getString(42).trim().equals("")) {
                                    outStatus.setText("RECUSD. " + rsItens.getString(42).trim() +
                                            (!rsItens.getString(43).trim().equals("") ? " | Motivo: " + rsItens.getString(43).trim() : "") +
                                            (!rsItens.getString(40).trim().equals("") ? "\nEXPED " + GettersSetters.converteData(rsItens.getString(40).trim().substring(0, 4) + "-" + rsItens.getString(40).trim().substring(4, 6) + "-" + rsItens.getString(40).trim().substring(6, 8), "EN") : "") +
                                            (!rsItens.getString(44).trim().equals("") ? " ÀS " + rsItens.getString(44).trim() : ""));
                                }
                            } else if (!rsItens.getString(38).trim().equals("")) {
                                if (!rsItens.getString(40).trim().equals("")) {
                                    outStatus.setText("EXPEDIDO EM " + GettersSetters.converteData(rsItens.getString(40).trim().substring(0, 4) + "-" + rsItens.getString(40).trim().substring(4, 6) + "-" + rsItens.getString(40).trim().substring(6, 8), "EN") +
                                            (!rsItens.getString(44).trim().equals("") ? " ÀS " + rsItens.getString(44).trim() : ""));
                                } else {
                                    outStatus.setText("FATURADO");
                                }
                            } else {
                                if (!statusCabec.startsWith("PRODU")) {
                                    outStatus.setText(statusCabec);
                                } else {
                                    if (!rsItens.getString(37).trim().equals("")) {
                                        outStatus.setText("EXAME FINAL");
                                    } else if (!rsItens.getString(36).trim().equals("")) {
                                        outStatus.setText("AUTOCLAVE");
                                    } else if (!rsItens.getString(35).trim().equals("")) {
                                        outStatus.setText("APLIC. BANDA/CAMELBACK");
                                    } else if (!rsItens.getString(34).trim().equals("")) {
                                        outStatus.setText("CORTE BANDA");
                                    } else if (!rsItens.getString(33).trim().equals("")) {
                                        outStatus.setText("APLIC. CONSERTOS");
                                    } else if (!rsItens.getString(32).trim().equals("")) {
                                        outStatus.setText("ESCAREAÇÃO");
                                    } else if (!rsItens.getString(31).trim().equals("")) {
                                        outStatus.setText("RASPAGEM");
                                    } else if (!rsItens.getString(30).trim().equals("")) {
                                        outStatus.setText("EXAME INICIAL");
                                    } else if (!rsItens.getString(29).trim().equals("")) {
                                        outStatus.setText("AGUARD. INÍCIO PRODUÇÃO");
                                    } else {
                                        outStatus.setText("AGUARD. CHEGADA DO PNEU");
                                    }
                                }
                            }

                            if (!rsItens.getString(41).equals("")) {
                                outStatus.setText(outStatus.getText() + " (C/ RETRABALHO SETOR " + rsItens.getString(41).trim() + ")");
                            }

                            if (!rsItens.getString(25).trim().equals("")) { //FICHA
                                llFicha.setVisibility(View.VISIBLE);
                                cmpFicha.setText(rsItens.getString(25) + rsItens.getString(26) + String.format("%03d", Integer.parseInt(rsItens.getString(26))));
                            }

                            llView.addView(addViewItens);
                            totalDados++;
                        }
                        builderItens.setView(view1);
                        builderItens.show();
                    } catch (Exception e) {
                        e.printStackTrace();

                        AlertDialog.Builder builder = new AlertDialog.Builder(ColetaBusca.this);
                        builder.setCancelable(false);
                        builder.setTitle("Erro ao buscar os itens Coleta");
                        builder.setMessage(GettersSetters.getErroEnvioColetaBDExt());
                        builder.setPositiveButton("Fechar", null);
                        builder.show();
                    }
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ColetaBusca.this);
                    builder.setCancelable(false);
                    builder.setTitle("Busca de Coletas");
                    builder.setMessage("Nenhum item localizado!\nTente novamente!");
                    builder.setPositiveButton("Fechar", null);
                    builder.show();
                }
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(ColetaBusca.this);
                builder.setCancelable(false);
                builder.setTitle("Erro ao buscar os itens Coleta");
                builder.setMessage(GettersSetters.getErroEnvioColetaBDExt());
                builder.setPositiveButton("Fechar", null);
                builder.show();
            }

            if (dialogBuscaColExt != null) {
                dialogBuscaColExt.cancel();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class GeraPDF extends AsyncTask<String, String, File> {
        AlertDialog dialogPDF = null;

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPreExecute() {
            AlertDialog.Builder builder = new AlertDialog.Builder(ColetaBusca.this);
            LayoutInflater layoutInflater = getLayoutInflater();
            final View dialogView = layoutInflater.inflate(R.layout.dialog_carregamento, null);
            final TextView textView = dialogView.findViewById(R.id.txtInfoCarreg);
            textView.setText("Gerando PDF da Coleta nº [" + numeroColeta + "]...");
            builder.setPositiveButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    geraPDF.cancel(true);
                    Toast.makeText(ColetaBusca.this, "Geração Cancelada", Toast.LENGTH_LONG).show();
                }
            });
            builder.setCancelable(false);
            builder.setView(dialogView);
            dialogPDF = builder.create();
            dialogPDF.show();
        }

        @Override
        protected File doInBackground(String... params) {
            nomeColeta = params[0];
            numeroColeta = params[1];
            data = params[2];
            filial = params[3];
            lojaCli = params[4];
            codCli = params[5];
            valor = params[6];
            codVend = params[7];

            if (params[8].equals("EXT")) {
                filePdf = geraArquivosColeta.montaPdfExt(ColetaBusca.this, params[0], params[1], params[2], params[3], params[4], params[5], params[6], params[7], params[9], params[10]);
            } else {
                filePdf = geraArquivosColeta.montaPdf(ColetaBusca.this, params[0], params[1], params[2], params[3], params[4], params[5], params[6], params[7]);
            }

            return filePdf;
        }

        @Override
        protected void onPostExecute(final File filePDF) {
            if (filePDF.length() > 0) {
                if (isShowPDF) {
                    final AlertDialog.Builder builderPdf = new AlertDialog.Builder(ColetaBusca.this);
                    builderPdf.setCancelable(true);
                    LayoutInflater layoutInfPdf = getLayoutInflater();
                    final View addViewPdf = layoutInfPdf.inflate(R.layout.view_pdf, null);
                    final PDFView pdfView = addViewPdf.findViewById(R.id.imgViewPdf);
                    final CardView btnEnviarWhatsPdf = addViewPdf.findViewById(R.id.btnEnviarWhatsPdf);
                    final CardView btnEnviaEmailPdf = addViewPdf.findViewById(R.id.btnEnviarEmailPdf);

                    pdfView.useBestQuality(true);
                    pdfView.documentFitsView();
                    pdfView.computeScroll();
                    pdfView.fromFile(filePDF)
                            .enableAntialiasing(false)
                            .load();
                    builderPdf.setView(addViewPdf);
                    AlertDialog alertPDF = builderPdf.create();
                    alertPDF.show();

                    btnEnviarWhatsPdf.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertPDF.dismiss();
                            enviaColetaEmailWhatsapp("", nomeColeta, data, numeroColeta, filial, codCli, lojaCli, valor, true, filePDF, codVend,
                                    spnStatusColeta.getSelectedItem().toString(), false);
                        }
                    });

                    btnEnviaEmailPdf.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertPDF.dismiss();
                            dialogEmail(filial, numeroColeta, data, codCli, lojaCli, nomeColeta, codVend, valor, filePDF);
                        }
                    });
                } else {
                    if (isSendWhats) {
                        enviaColetaEmailWhatsapp("", nomeColeta, data, numeroColeta, filial, codCli, lojaCli, valor, isSendWhats, filePdf, codVend,
                                spnStatusColeta.getSelectedItem().toString(), false);
                    } else if (isSendTelegram) {
                        enviaColetaEmailWhatsapp("", nomeColeta, data, numeroColeta, filial, codCli, lojaCli, valor, false, filePdf, codVend,
                                spnStatusColeta.getSelectedItem().toString(), isSendTelegram);
                    } else {
                        dialogEmail(filial, numeroColeta, data, codCli, lojaCli, nomeColeta, codVend, valor, filePdf);
                    }
                }
            } else {
                AlertDialog.Builder builderFilial = new AlertDialog.Builder(ColetaBusca.this);
                builderFilial.setTitle("PDF");
                builderFilial.setIcon(R.drawable.error);
                builderFilial.setCancelable(false);
                builderFilial.setMessage("PDF não gerado!");
                builderFilial.setPositiveButton("OK", null);
                AlertDialog dialog = builderFilial.create();
                dialog.show();
            }

            dialogPDF.dismiss();
        }
    }

    @SuppressLint("SimpleDateFormat")
    public static String getData() {
        Date date = new Date();
        DateFormat dataPtBR = new SimpleDateFormat("dd/MM/yyyy"); //data
        return dataPtBR.format(date);
    }

    public void trocaData() {
        AlertDialog.Builder builderItens = new AlertDialog.Builder(ColetaBusca.this);
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(ColetaBusca.this);
        View view1 = layoutInflaterAndroid.inflate(R.layout.view_generica, null);
        LinearLayout llView = view1.findViewById(R.id.llViewGenerica);

        LayoutInflater layoutInflater = getLayoutInflater();

        View addViewCalendario;
        CalendarView calendarView;
        Button button;
        final TextView textView;
        final EditText editText;

        if (Build.VERSION.SDK_INT >= 23) { //ANDROID 6.0+
            addViewCalendario = layoutInflater.inflate(R.layout.dialog_calendario, llView, false);
            calendarView = addViewCalendario.findViewById(R.id.calendario);
            button = addViewCalendario.findViewById(R.id.btnConfData);
            textView = addViewCalendario.findViewById(R.id.txtDataTemp);

            calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                @SuppressLint("DefaultLocale")
                @Override
                public void onSelectedDayChange(@NonNull CalendarView calendarView, int ano, int mes, int dia) {
                    novaData = String.format("%02d", dia) + "/" + String.format("%02d", mes + 1) + "/" + ano;
                    textView.setText(novaData);
                }
            });
        } else {
            addViewCalendario = layoutInflater.inflate(R.layout.dialog_calendario_old_api, llView, false);
            editText = addViewCalendario.findViewById(R.id.cmpCalendario);
            button = addViewCalendario.findViewById(R.id.btnConfData);
            textView = addViewCalendario.findViewById(R.id.txtDataTemp);

            editText.addTextChangedListener(MaskEditUtil.mask(editText, MaskEditUtil.FORMAT_DATE));

            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @SuppressLint("DefaultLocale")
                @Override
                public void afterTextChanged(Editable editable) {
                    if (editText.getText().toString().trim().length() == 10) {
                        String[] dataArr = editText.getText().toString().trim().split("/");
                        novaData = String.format("%02d", Integer.parseInt(dataArr[0])) + "/" + String.format("%02d", Integer.parseInt(dataArr[1])) + "/" + dataArr[2];
                        textView.setText(novaData);
                    }
                }
            });
        }

        llView.addView(addViewCalendario);
        builderItens.setView(view1);
        builderItens.setCancelable(true);
        final AlertDialog alertDialog = builderItens.create();
        alertDialog.show();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (textView.getText().toString().isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ColetaBusca.this);
                    builder.setTitle("Sem data!");
                    if (Build.VERSION.SDK_INT >= 23) {
                        builder.setMessage("Selecione uma data.");
                    } else {
                        builder.setMessage("Digite a data.");
                    }
                    builder.setCancelable(false);
                    builder.setPositiveButton("Ok", null);
                    builder.show();
                } else {
                    if (Build.VERSION.SDK_INT >= 23) {
                        cmpDataBusca.setText(novaData);
                        alertDialog.dismiss();
                    } else {
                        if (FuncoesGenericas.validaData(novaData)) {
                            cmpDataBusca.setText(novaData);
                            alertDialog.dismiss();
                        } else {
                            cmpDataBusca.getText().clear();
                            AlertDialog.Builder builder = new AlertDialog.Builder(ColetaBusca.this);
                            builder.setTitle("Erro data!");
                            builder.setMessage("Data inválida. Digite outra data!");
                            builder.setCancelable(false);
                            builder.setPositiveButton("Ok", null);
                            builder.show();
                        }
                    }
                }
            }
        });
    }

    public void dialogEmail(final String filial, final String numeroColeta, final String data, final String codCli, final String lojaCli, final String nomeCli,
                            final String codVend, final String valor, final File filePdf) {
        isCancelarEnvioEmail = false;

        builderMail = new AlertDialog.Builder(ColetaBusca.this);
        builderMail.setIcon(R.drawable.emailsender);
        builderMail.setCancelable(false);
        builderMail.setTitle("E-mail");
        builderMail.setMessage("Digite o e-mail do Cliente:");
        final EditText inputEmail = new EditText(ColetaBusca.this);
        inputEmail.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        inputEmail.setHint(getString(R.string.mensagem_dialog_email));
        builderMail.setView(inputEmail);
        builderMail.setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                emailCli = inputEmail.getText().toString();
            }
        });
        builderMail.setNegativeButton("Fechar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                emailCli = "";
                isCancelarEnvioEmail = true;
            }
        });
        builderMail.setNeutralButton("Email Cadastro", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String qryBuscaCliColeta = "SELECT COL_CLI_NOME FROM COLETA_CLI " +
                        "WHERE trim(COL_CLI_FILIAL) = '" + filial.trim() + "'" +
                        " AND trim(COL_CLI_IDENTIF) = '" + numeroColeta.trim() + "'" +
                        " AND trim(COL_CLI_DATA) = '" + GettersSetters.converteData(data, "BR").trim() + "'" +
                        " AND trim(COL_CLI_COD_CLI) = '" + codCli.trim() + "'" +
                        " AND trim(COL_CLI_LOJA_CLI) = '" + lojaCli.trim() + "'";
                Cursor cursor = db.buscaColetaCli(qryBuscaCliColeta, true);
                if (cursor.getCount() > 0) {
                    emailCli = db.novoClienteColeta("COL_CLI_EMAIL", codCli, lojaCli, false, numeroColeta);
                } else {
                    emailCli = conecta.cliente("A1_EMAIL", codCli, lojaCli).trim();
                }
            }
        });
        AlertDialog dialogMail = builderMail.create();
        dialogMail.show();

        dialogMail.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if (!isCancelarEnvioEmail) {
                    if (emailCli != null) {
                        if (!emailCli.trim().equals("") && !emailCli.toUpperCase().contains("NOMAIL") && !emailCli.equals(".")) {
                            try {
                                enviaColetaEmailWhatsapp(emailCli, nomeCli, data, numeroColeta, filial, codCli, lojaCli, valor, false, filePdf, codVend, spnStatusColeta.getSelectedItem().toString(), true);
                            } catch (Exception e) {
                                Snackbar.make(viewSnackBar, "Erro enviar e-mail " + e.toString(), Snackbar.LENGTH_LONG).show();
                            }
                        } else {
                            AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ColetaBusca.this);
                            builder.setIcon(R.drawable.mail);
                            builder.setCancelable(false);
                            builder.setTitle("E-mail");
                            builder.setMessage("Cliente " + nomeCli + " não possui e-mail cadastrado ou o e-mail (" + emailCli.trim() + ") é inválido!\nEnvio do e-mail interrompido!");
                            builder.setPositiveButton("Fechar", null);
                            AlertDialog dialogEmail = builder.create();
                            dialogEmail.show();
                        }
                    } else {
                        AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ColetaBusca.this);
                        builder.setIcon(R.drawable.mail);
                        builder.setCancelable(false);
                        builder.setTitle("Erro envio E-mail");
                        builder.setMessage(GettersSetters.getErroEnvioColetaBDExt());
                        builder.setPositiveButton("Fechar", null);
                        AlertDialog dialogEmail = builder.create();
                        dialogEmail.show();

//                        Toast.makeText(ColetaBusca.this, "Erro ao enviar email.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    public void trocaVendedor() {
        arrIdUsuario.clear();
        arrUsuario.clear();
        arrCodTotvs.clear();
        arrNomeTotvs.clear();
        arrTipovend.clear();
        arrCliPermitidos.clear();

        GettersSetters.arrClientesPermitidos.clear();
        GettersSetters.setCodBorrachPatio("");
        GettersSetters.setIsBorracheiroVendPat(false);

        Cursor res = db.selectUsuariosVendedores(GettersSetters.getIdUsuarioLogado(), GettersSetters.getTipoUsuario(), false, false);
        if (CheckConnection.isConnected(ColetaBusca.this) && res.getColumnCount() == 0) {
            conecta.selectUsuariosVendedores(GettersSetters.getIdUsuarioLogado(), GettersSetters.getTipoUsuario());
            arrIdUsuario.addAll(conecta.arrIdUsuarioVendedor);
            arrUsuario.addAll(conecta.arrUsuarioVendedor);
            arrCodTotvs.addAll(conecta.arrCodTotvsUsuarioVendedor);
            arrNomeTotvs.addAll(conecta.arrNomeTotvsUsuarioVendedor);
            arrTipovend.addAll(conecta.arrTipoVendUsuarioVendedor);
            arrCliPermitidos.addAll(conecta.arrClientesPermitidosUsuarioVendedor);
        } else {
            arrIdUsuario.addAll(db.arrIdUsuario);
            arrUsuario.addAll(db.arrUsuario);
            arrCodTotvs.addAll(db.arrCodTotvs);
            arrNomeTotvs.addAll(db.arrNomeTotvs);
            arrTipovend.addAll(db.arrTipovend);
            arrCliPermitidos.addAll(db.arrCliPermitidos);
        }

        String[] usuariosLocalizados = new String[arrIdUsuario.size() + 1];
        String[] idUsuarioLocalizado = new String[arrIdUsuario.size() + 1];
        idUsuarioLocalizado[0] = "000";
        usuariosLocalizados[0] = "TODOS";

        for (int i = 0; i < arrIdUsuario.size(); i++) {
            usuariosLocalizados[i + 1] = arrUsuario.get(i);
            idUsuarioLocalizado[i + 1] = arrIdUsuario.get(i);
        }

        final AlertDialog.Builder builderUsuarios = new AlertDialog.Builder(ColetaBusca.this);
        builderUsuarios.setCancelable(true);
        builderUsuarios.setTitle("Selecione o Vendedor para busca as Coletas");

        final ArrayAdapter<String> adapterSpnUsuarios = new ArrayAdapter<String>(ColetaBusca.this, R.layout.text_view_item_high, usuariosLocalizados) {
            @Override
            public View getView(int position, View convertView, @NotNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                if (((TextView) view).getText().toString().contains("** ")) {
                    ((TextView) view).setTextColor(Color.parseColor("#FF0000"));
                } else {
                    ((TextView) view).setTextColor(Color.parseColor("#000000"));
                }
                ((TextView) view).setTextSize(16);
                return view;
            }
        };

        builderUsuarios.setAdapter(adapterSpnUsuarios, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int itemSelecionado) {
                String idUsuarioEdit = idUsuarioLocalizado[itemSelecionado];
                String usuarioEdit = usuariosLocalizados[itemSelecionado];

                if (!idUsuarioEdit.trim().equals("")) {
                    GettersSetters.setCodigoVendedor(idUsuarioEdit);
                    GettersSetters.setNomeVend(usuarioEdit.replace("** ", ""));

                    txtVendedorBusca.setText(GettersSetters.getNomeVend());
                }
            }
        });
        AlertDialog dialogUsr2 = builderUsuarios.create();
        dialogUsr2.show();
    }

    public void deletaColetas15Dias() {
        /* DELETA AS COLETAS ARMAZENADAS E COM STATUS 2 - DELETA COM MAIS DE X DIAS ARMAZENADA NO APARELHO */
        String qryDelColetasCabec = "DELETE " +
                "FROM COLETA_CABEC " +
                "WHERE 1 = 1 " +
                " AND COL_CABEC_STATUS_ENVIO = '2' " +
                " AND COL_CABEC_DATA <= '" + GettersSetters.calendarDataSubtr(GettersSetters.getDataEN(), -15) + "'";

        String qryDelColetaItens = "DELETE " +
                "FROM COLETA_ITENS " +
                "WHERE 1 = 1 " +
                " AND COL_IT_STATUS_ENVIO = '2' " +
                " AND COL_IT_DATA <= '" + GettersSetters.calendarDataSubtr(GettersSetters.getDataEN(), -15) + "'";

        String qryBuscaCliColeta = "DELETE " +
                "FROM COLETA_CLI " +
                "WHERE 1 = 1 " +
                " AND COL_CLI_STATUS_ENVIO = '2' " +
                " AND COL_CLI_DATA <= '" + GettersSetters.calendarDataSubtr(GettersSetters.getDataEN(), -15) + "'";
        db.delColeta(qryDelColetasCabec, qryDelColetaItens, qryBuscaCliColeta);
    }
}