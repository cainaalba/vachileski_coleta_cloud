package pneus.vachileski_mobi_apanhe_pneus;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.material.snackbar.Snackbar;
import com.mazenrashed.printooth.Printooth;
import com.mazenrashed.printooth.ui.ScanningActivity;
import com.mazenrashed.printooth.utilities.ImageUtils;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.ByteBuffer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import pneus.vachileski_mobi_funcoes_genericas.CheckConnection;
import pneus.vachileski_mobi_funcoes_genericas.ConexaoBDExt;
import pneus.vachileski_mobi_funcoes_genericas.ConexaoBDInt;
import pneus.vachileski_mobi_funcoes_genericas.FuncoesGenericas;
import pneus.vachileski_mobi_funcoes_genericas.GettersSetters;
import pneus.vachileski_mobi_funcoes_genericas.ImprimeColeta;
import pneus.vachileski_mobi_funcoes_genericas.MaskEditUtil;

@SuppressWarnings("Convert2Lambda")
@SuppressLint("StaticFieldLeak")
public class ColetaConclusao extends AppCompatActivity {
    TextView txtData, txtHora, txtFilial, txtDescrFilial, txtVendedor,
            txtNumOS, txtDocCli, txtRzSocial, txtInsEst, txtMail, txtColetadoPor;
    TextView txtQtdItens, txtValorTotal;
    TextView txtCondPgto, txtDecrFormaPgto;
    TextView txtNomeBorr;
    EditText cmpInfoAdicionais, cmpDataChegPn;
    RadioButton rbComOrcamento, rbSemOrcamento;
    Button btnAssinar, btnCancelar, btnData, btnConfirmar, btnVerDadosCli, btnVisualizarItens;
    LinearLayout llBorracheiro, llColetadoPor, llOrcamento;
    static ImageView imgViewAssCli;
    ImageView imgViewAssBorr;
    static View viewSnackBar;

    ConexaoBDExt conecta = new ConexaoBDExt();
    ConexaoBDInt db;
    GeraPdfEmaiWhats geraPdfEmaiWhats = null;
    TaskEnviaEmail taskEnviaEmail = null;

    String numeroColeta = "";
    String seqIdentifColeta = "";
    String celular = "";
    String ddd = "";
    String hora = "";
    String emailCli = null;
    String dataChegada = "";
    String dataMaxChegada = "";
    String encodeAssinaturaBorr = null;
    String encodeAssinaturaCli = null;

    boolean isCelValid = false;
    boolean sucessoEmail = false;
    boolean dadosOK = true;

    boolean isEmail = false;
    boolean isSendWhats = false;
    boolean isSendTelegram = false;

    boolean isCancelarEnvioEmail = false;

    byte[] assintBorrByte = new byte[0];
    byte[] assinatCliByte = new byte[0];
    byte[] fotoRgCliByte = new byte[0];
    byte[] fotoDocCliByte = new byte[0];
    byte[] fotoCompResidByte = new byte[0];

    AlertDialog dialogEnviarColeta = null;
    AlertDialog dialogItens = null;

    DecimalFormat df = new DecimalFormat("#,##0.00");

    FuncoesGenericas funcoesGenericas = new FuncoesGenericas();
    EnviaColetas enviaColetas = new EnviaColetas();

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_coleta_conclusao);
        setTitle(getString(R.string.tituloColeta) + " - CONCLUSÃO");

        db = new ConexaoBDInt(this);

        txtData = findViewById(R.id.txtData);
        txtHora = findViewById(R.id.txtHora);
        txtFilial = findViewById(R.id.txtFilial);
        txtDescrFilial = findViewById(R.id.txtDescrFilial);
        txtVendedor = findViewById(R.id.txtVendedor);
        txtColetadoPor = findViewById(R.id.txtColetadoPor);
        txtNumOS = findViewById(R.id.txtNumOS);
        txtDocCli = findViewById(R.id.txtDocCli);
        txtRzSocial = findViewById(R.id.txtRzSocial);
        txtInsEst = findViewById(R.id.txtInsEst);
        txtMail = findViewById(R.id.txtMail);
        btnVerDadosCli = findViewById(R.id.btnVerDadosCli);
        btnVisualizarItens = findViewById(R.id.btnVisualizarItens);
        txtQtdItens = findViewById(R.id.txtQtdItens);
        txtValorTotal = findViewById(R.id.txtValorTotal);
        txtCondPgto = findViewById(R.id.txtCondPgto);
        txtDecrFormaPgto = findViewById(R.id.txtDecrFormaPgto);
        txtNomeBorr = findViewById(R.id.txtNomeBorr);
        cmpInfoAdicionais = findViewById(R.id.cmpInfoAdicionais);
        llOrcamento = findViewById(R.id.llOrcamento);
        llColetadoPor = findViewById(R.id.llColetadoPor);
        rbComOrcamento = findViewById(R.id.rbComOrcamento);
        rbSemOrcamento = findViewById(R.id.rbSemOrcamento);

        btnAssinar = findViewById(R.id.btnAssinar);
        btnConfirmar = findViewById(R.id.btnConfirmar);
        imgViewAssCli = findViewById(R.id.imgAssCli);
        imgViewAssBorr = findViewById(R.id.imgViewAssBorr);
        btnCancelar = findViewById(R.id.btnCancelar);
        cmpDataChegPn = findViewById(R.id.cmpDataChegPn);
        btnData = findViewById(R.id.btnData);

        viewSnackBar = findViewById(R.id.viewSnackBar);
        llBorracheiro = findViewById(R.id.llBorracheiro);

        hora = GettersSetters.getHora();
        txtNumOS.setText(GettersSetters.getNumeroColeta());

        /*
         * AJUSTA TIPO DE COLETA QDO RECLAMAÇÃO
         * */
        if (GettersSetters.getDescrTipoColeta().equals("RECLAMACAO")) {
            if (GettersSetters.getTipoColeta().replace("'", "").trim().equals("PC")) {
                GettersSetters.setTipoColeta("RPC");
            } else {
                GettersSetters.setTipoColeta("RECL");
            }
        }

        if (GettersSetters.isClienteGrp()) {
            llOrcamento.setVisibility(View.GONE);
        }

        /* VALIDAÇÃO DO VALOR TOTAL - FEITA POIS ESTAVA DANDO DIVERGÊNCIA **/
        double totalRecalculado = 0.0;
        for (int i = 0; i < GettersSetters.getArrItemColeta().size(); i++) {
            totalRecalculado += Double.parseDouble(GettersSetters.getArrValorTotal().get(i));
        }

        if (totalRecalculado != Double.parseDouble(GettersSetters.getValorColeta())) {
            GettersSetters.setValorColeta(String.valueOf(totalRecalculado));
        }

        txtData.setText(GettersSetters.getDataColetaBR());
        txtHora.setText(hora);
        txtFilial.setText(GettersSetters.getFilial().trim());
        txtDescrFilial.setText(" - " + GettersSetters.getDescrFilial().trim());
        if (!GettersSetters.getIdUsuarioLogado().equals(GettersSetters.getCodigoVendedor())) {
            txtVendedor.setText(/*GettersSetters.getCodigoVendedor() + " - " + */GettersSetters.getNomeVend().toUpperCase());
            txtColetadoPor.setText(/*GettersSetters.getIdUsuarioLogado() + " - " + */GettersSetters.getUsuarioLogado().toUpperCase());
        } else {
            llColetadoPor.setVisibility(View.GONE);
            txtVendedor.setText(/*GettersSetters.getCodigoVendedor() + " - " + */GettersSetters.getNomeVend().toUpperCase());
        }
        if (GettersSetters.getDocCli().trim().length() == 14) {
            txtDocCli.setText(GettersSetters.getDocCli().trim().replaceAll("([0-9]{2})([0-9]{3})([0-9]{3})([0-9]{4})([0-9]{2})", "$1\\.$2\\.$3/$4-$5"));
        } else {
            txtDocCli.setText(GettersSetters.getDocCli().trim().replaceAll("([0-9]{3})([0-9]{3})([0-9]{3})([0-9]{2})", "$1\\.$2\\.$3-$4"));
        }
        txtRzSocial.setText(GettersSetters.getRzSocialCli());
        txtInsEst.setText(GettersSetters.getInscrCli());
        txtMail.setText(GettersSetters.getEmailCli());
        txtQtdItens.setText(GettersSetters.getQtdItensColeta());
        txtValorTotal.setText("R$ " + df.format(Double.parseDouble(GettersSetters.getValorColeta())));
        if (GettersSetters.getCondPgto().equals("0")) {
            txtCondPgto.setText("À VISTA");
        } else {
            txtCondPgto.setText(GettersSetters.getCondPgto() + " dias");
        }
        txtDecrFormaPgto.setText(GettersSetters.getDescrFormaPgto());
        cmpInfoAdicionais.setText(GettersSetters.getInfoAdicionais());

        if (GettersSetters.isIsOrcamento()) {
            rbComOrcamento.setChecked(true);
        } else {
            rbSemOrcamento.setChecked(true);
        }

        if (GettersSetters.getPathAssinBorr() == null) {
            llBorracheiro.setVisibility(View.GONE);
        } else {
            txtNomeBorr.setText(GettersSetters.getNomeBorracheiro());
            setAssinaturaBorr();
        }

        /* ASSINATURA CLIENTE **/
        setAssinaturaCli();

        btnVerDadosCli.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Vibrator vibe = (Vibrator) ColetaConclusao.this.getSystemService(Context.VIBRATOR_SERVICE);
                Objects.requireNonNull(vibe).vibrate(50); // 50 is time in ms

                AlertDialog.Builder builder = new AlertDialog.Builder(ColetaConclusao.this);

                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dialog_visualiza_enderecos, null);

                builder.setCancelable(true);
                builder.setView(dialogView);

                final TextView txtEndEntrega = dialogView.findViewById(R.id.txtEndEntrega);
                final TextView txtBairroEntrega = dialogView.findViewById(R.id.txtBairroEntrega);
                final TextView txtMunEntrega = dialogView.findViewById(R.id.txtMunEntrega);
                final TextView txtEstEntrega = dialogView.findViewById(R.id.txtEstEntrega);
                final TextView txtCepEntrega = dialogView.findViewById(R.id.txtCepEntrega);
                final TextView txtDDDTelEntrega = dialogView.findViewById(R.id.txtDDDTelEntrega);
                final TextView txtEndCobran = dialogView.findViewById(R.id.txtEndCobran);
                final TextView txtBairroCobran = dialogView.findViewById(R.id.txtBairroCobran);
                final TextView txtMunCobran = dialogView.findViewById(R.id.txtMunCobran);
                final TextView txtEstCobran = dialogView.findViewById(R.id.txtEstCobran);
                final TextView txtCepCobran = dialogView.findViewById(R.id.txtCepCobran);
                final TextView txtDDDTelCobran = dialogView.findViewById(R.id.txtDDDTelCobran);
                final AlertDialog dialog = builder.create();

                txtEndEntrega.setText(GettersSetters.getEndEntregaCli());
                txtBairroEntrega.setText(GettersSetters.getBairroEntregaCli());
                txtMunEntrega.setText(GettersSetters.getMunicEntregaCli());
                txtEstEntrega.setText(GettersSetters.getEstEntregaCli());
                txtCepEntrega.setText(GettersSetters.getCepEntregaCli());
                txtDDDTelEntrega.setText(GettersSetters.getDddEntregaCli() + " " + GettersSetters.getFoneEntregaCli());
                txtEndCobran.setText(GettersSetters.getEndCobranCli());
                txtBairroCobran.setText(GettersSetters.getBairroCobranCli());
                txtMunCobran.setText(GettersSetters.getMunicCobranCli());
                txtEstCobran.setText(GettersSetters.getEstCobranCli());
                txtCepCobran.setText(GettersSetters.getCepCobranCli());
                txtDDDTelCobran.setText(GettersSetters.getDddCobranCli() + " " + GettersSetters.getFoneCobranCli());

                dialog.show();
            }
        });

        btnVisualizarItens.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ColetaConclusao.this);
                builder.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.wheel, null));
                LayoutInflater layoutInflaterAndroid = getLayoutInflater(); //LayoutInflater.from(ColetaConclusao.this);
                View viewPrincipal = layoutInflaterAndroid.inflate(R.layout.view_generica, null);
                LinearLayout viewLayout = viewPrincipal.findViewById(R.id.llViewGenerica);

                LayoutInflater inflater = getLayoutInflater();
                for (int i = 0; i < GettersSetters.getArrItemColeta().size(); i++) {
                    final View viewItens = inflater.inflate(R.layout.view_itens_coleta, viewLayout, false);

                    final TextView outItemCol = viewItens.findViewById(R.id.txtItemCol);
                    final TextView outQtdCol = viewItens.findViewById(R.id.txtQtdCol);
                    final TextView outBitCol = viewItens.findViewById(R.id.txtBitCol);
                    final TextView outMarcaCol = viewItens.findViewById(R.id.txtMarcaCol);
                    final TextView outModelCol = viewItens.findViewById(R.id.txtModCol);
                    final TextView outSerieCol = viewItens.findViewById(R.id.txtSerCol);
                    final TextView outDotCol = viewItens.findViewById(R.id.txtDotCol);
                    final TextView outMontado = viewItens.findViewById(R.id.txtMontado);
                    final TextView outDesenCol = viewItens.findViewById(R.id.txtDesCol);
                    final TextView outVlrUnit = viewItens.findViewById(R.id.txtValUnit);
                    final TextView outVlrTotIt = viewItens.findViewById(R.id.txtValTotIt);
                    final TextView outUrgCol = viewItens.findViewById(R.id.txtUrgCol);
                    final TextView outObsItem = viewItens.findViewById(R.id.txtObservItem);
                    final LinearLayout llIdentifItem = viewItens.findViewById(R.id.llIdentifItem);
                    final CardView configItens = viewItens.findViewById(R.id.btnConfigIt);
                    final CardView visualItem = viewItens.findViewById(R.id.btnVisualItem);

                    llIdentifItem.setVisibility(View.GONE);
                    configItens.setVisibility(View.GONE);

                    final String outCodPrd = GettersSetters.getArrCodProdColeta().get(i);
                    final String outCAgua = GettersSetters.getArrCAgua().get(i);
                    final String outCCamara = GettersSetters.getArrCCamara().get(i);
                    final String outGarantia = GettersSetters.getArrGarantia().get(i);
                    final String outPercComissBorr = GettersSetters.getArrPorcComisBorr().get(i);
                    final String outVlrComissBorr = GettersSetters.getArrValrComisBorr().get(i);

                    outItemCol.setText(GettersSetters.getArrItemColeta().get(i));
                    outBitCol.setText(GettersSetters.getArrBitolaColeta().get(i));
//                    outCodProd.setText(GettersSetters.getArrCodProdColeta().get(i));
                    outMarcaCol.setText(GettersSetters.getArrMarcaColeta().get(i));
                    outModelCol.setText(GettersSetters.getArrModeloColeta().get(i));
                    outSerieCol.setText(GettersSetters.getArrSerieColeta().get(i));
                    outDotCol.setText(GettersSetters.getArrDotColeta().get(i));
                    outMontado.setText(GettersSetters.getArrMontadoColeta().get(i));
                    outDesenCol.setText(GettersSetters.getArrDesenhoColeta().get(i));
                    outQtdCol.setText(GettersSetters.getArrQtdItemColeta().get(i));
                    outVlrUnit.setText(GettersSetters.getArrValorUnit().get(i));
                    outVlrTotIt.setText(GettersSetters.getArrValorTotal().get(i));
                    outUrgCol.setText(GettersSetters.getArrUrgenteColeta().get(i));
                    outObsItem.setText(GettersSetters.getArrObsItens().get(i));

                    if (!GettersSetters.getTipoColeta().contains("SE") && !GettersSetters.getTipoColeta().contains("PC")) {
                        outMontado.setText("");
                        outUrgCol.setText("");
                    }

                    visualItem.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(ColetaConclusao.this);
                            LayoutInflater layoutInflater = getLayoutInflater();
                            final View dialogView = layoutInflater.inflate(R.layout.view_visual_itens_coleta, null);

                            builder.setCancelable(true);
                            builder.setView(dialogView);

                            final EditText item = dialogView.findViewById(R.id.txtItem);
                            final EditText quantidade = dialogView.findViewById(R.id.cmpQtdCol);
                            final TextView txtIdentif = dialogView.findViewById(R.id.txtIdentif);
                            final EditText identificador = dialogView.findViewById(R.id.cmpIdentificador);
                            final EditText bitola = dialogView.findViewById(R.id.cmpBitCol);
                            final EditText marca = dialogView.findViewById(R.id.cmpMarcaCol);
                            final EditText modelo = dialogView.findViewById(R.id.cmpModCol);
                            final EditText serie = dialogView.findViewById(R.id.cmpSerCol);
                            final EditText dot = dialogView.findViewById(R.id.cmpDotCol);
                            final EditText desenho = dialogView.findViewById(R.id.cmpDesCol);
                            final EditText valorUnit = dialogView.findViewById(R.id.cmpValUnit);
                            final EditText valorTotalIt = dialogView.findViewById(R.id.cmpValorTotal);
                            final EditText obs = dialogView.findViewById(R.id.cmpObsItem);
                            final CheckBox urgente = dialogView.findViewById(R.id.chkUrgente);
                            final ToggleButton montado = dialogView.findViewById(R.id.toggleButtonMontado);
                            final EditText porcComB = dialogView.findViewById(R.id.cmpPrcComissBorr);
                            final EditText vlrComB = dialogView.findViewById(R.id.cmpVlrComissBorr);
                            final TextView txtComissBorr = dialogView.findViewById(R.id.txtComissBorr);
                            final LinearLayout llComissBorr = dialogView.findViewById(R.id.llComissBorr);
                            final CheckBox cAgua = dialogView.findViewById(R.id.chkcAgua);
                            final CheckBox cCamara = dialogView.findViewById(R.id.chkcCamara);
                            final CheckBox garantia = dialogView.findViewById(R.id.chkGarantia);
                            final TextView txtStatusIt = dialogView.findViewById(R.id.txtStatusIt);
                            final EditText cmpStatusIt = dialogView.findViewById(R.id.cmpStatusIt);

                            txtIdentif.setVisibility(View.GONE);
                            identificador.setVisibility(View.GONE);
                            txtStatusIt.setVisibility(View.GONE);
                            cmpStatusIt.setVisibility(View.GONE);

                            item.setText(outItemCol.getText().toString());
                            quantidade.setText(outQtdCol.getText().toString());
                            bitola.setText(outBitCol.getText().toString());
                            marca.setText(outMarcaCol.getText().toString());
                            modelo.setText(outModelCol.getText().toString());
                            serie.setText(outSerieCol.getText().toString());
                            dot.setText(outDotCol.getText().toString());
                            desenho.setText(outDesenCol.getText().toString());
                            valorUnit.setText("R$ " + df.format(Double.parseDouble(outVlrUnit.getText().toString())));
                            valorTotalIt.setText("R$ " + df.format(Double.parseDouble(outVlrTotIt.getText().toString())));
                            obs.setText(outObsItem.getText().toString());

                            if (!GettersSetters.isCheckComissBorr()) {
                                txtComissBorr.setVisibility(View.GONE);
                                llComissBorr.setVisibility(View.GONE);
                                porcComB.setText("0 %");
                                vlrComB.setText("R$ 0,00");
                            } else {
                                porcComB.setText(outPercComissBorr + "%");
                                vlrComB.setText("R$ " + df.format(Double.parseDouble(outVlrComissBorr)));
                            }

                            if (outUrgCol.getText().equals("Sim")) {
                                urgente.setChecked(true);
                                urgente.setTextColor(getResources().getColor(R.color.red));
                            } else {
                                urgente.setChecked(false);
                                urgente.setTextColor(getResources().getColor(R.color.green));
                            }

                            if (outMontado.getText().equals("Montado")) {
                                montado.setChecked(true);
                                montado.setTextColor(getResources().getColor(R.color.red));
                            } else {
                                montado.setChecked(false);
                                montado.setTextColor(getResources().getColor(R.color.green));
                            }

                            cAgua.setChecked(outCAgua.equals("Sim"));
                            cCamara.setChecked(outCCamara.equals("Sim"));
                            garantia.setChecked(outGarantia.equals("Sim"));

                            builder.show();
                        }
                    });
                    viewLayout.addView(viewItens);
                }

                builder.setTitle("Visualização\n(arraste para o lado ->)");
                builder.setCancelable(false);
                builder.setNegativeButton("Fechar", null);
                builder.setView(viewPrincipal);

                dialogItens = builder.create();
                dialogItens.show();
            }
        });


        btnData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dataChegPn();
            }
        });

        btnConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                btnConfirmar.setVisibility(View.GONE);
                /* VALIDAÇÃO SE OS DADOS ESTÃO PREENCHIDOS **/
                if (txtNumOS.getText().toString().equals("") ||
                        txtFilial.getText().toString().trim().equals("") ||
                        txtVendedor.getText().toString().trim().equals("") ||
                        txtDocCli.getText().toString().trim().equals("") ||
                        txtRzSocial.getText().toString().trim().equals("")) {
                    dadosOK = false;
                }

                if (dadosOK) {
                    if (cmpDataChegPn.getText().toString().trim().isEmpty() && GettersSetters.getTipoColeta().contains("SE")) { //DATA DE CHEGADA SOMENTE PARA SERVIÇOS
                        btnConfirmar.setVisibility(View.VISIBLE);

                        AlertDialog.Builder builder = new AlertDialog.Builder(ColetaConclusao.this);
                        builder.setTitle("Data de Chegada");
                        builder.setMessage("Preencher a data prevista de chegada dos itens na unidade!");
                        builder.setPositiveButton("Preencher", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dataChegPn();
                            }
                        });
                        builder.setCancelable(false);
                        builder.show();
                    } else if (imgViewAssCli.getDrawable() == null) {
                        btnConfirmar.setVisibility(View.VISIBLE);

                        final Snackbar snackbar = Snackbar.make(viewSnackBar, "Sem assinatura do Cliente!", Snackbar.LENGTH_INDEFINITE);
                        snackbar.setAction("Assinar", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                assinar();
                                snackbar.dismiss();
                            }
                        }).show();
                    } else if (Integer.parseInt(GettersSetters.getQtdItensColeta()) != GettersSetters.getArrItemColeta().size()) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ColetaConclusao.this);
                        builder.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.error, null));
                        builder.setTitle("Problema nos itens!");
                        builder.setMessage("Há um problema com a quantidade de itens.\n" +
                                "Cabecalho: " + GettersSetters.getQtdItensColeta() + "\n" +
                                "Itens: " + GettersSetters.getArrItemColeta().size() + "\n" +
                                "Favor readicionar os itens!");
                        builder.setPositiveButton("Novos Itens!", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finishAffinity();
                                Intent it = new Intent(ColetaConclusao.this, ColetaItens.class);
                                startActivity(it);
                            }
                        });
                        builder.setNegativeButton("Fechar!", null);
                        builder.setCancelable(false);
                        builder.show();
                    } else {
                        /* CONVERTER ASSINATURAS PARA BYTE **/
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        if (imgViewAssCli.getDrawable() != null || imgViewAssCli.getBackground() != null) {
                            Bitmap bitmapAssCli = ((BitmapDrawable) imgViewAssCli.getDrawable()).getBitmap();
                            if (bitmapAssCli != null) {
                                bitmapAssCli.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
                                assinatCliByte = byteArrayOutputStream.toByteArray();
                                dadosOK = true;
                                encodeAssinaturaCli = Base64.encodeToString(assinatCliByte, Base64.DEFAULT);
                            } else {
                                encodeAssinaturaCli = "";
                                assinatCliByte = null;
                                dadosOK = false;
                            }
                        } else {
                            dadosOK = false;
                        }

                        if (assinatCliByte == null) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(ColetaConclusao.this);
                            builder.setTitle("Erro converter assinatura!");
                            builder.setIcon(R.drawable.error);
                            builder.setCancelable(true);
                            builder.setMessage("Erro ao converter assinatura do cliente para byte.\nTente assinar novamente!");
                            AlertDialog dialogNumCol = builder.create();
                            dialogNumCol.show();
                        } else {
                            ByteArrayOutputStream bmImgAssingBorr = new ByteArrayOutputStream();
                            if (imgViewAssBorr.getDrawable() != null || imgViewAssBorr.getBackground() != null) {
                                Bitmap bitmapAssBorr = ((BitmapDrawable) imgViewAssBorr.getDrawable()).getBitmap();
                                if (bitmapAssBorr != null) {
                                    bitmapAssBorr.compress(Bitmap.CompressFormat.JPEG, 80, bmImgAssingBorr);
                                    assintBorrByte = bmImgAssingBorr.toByteArray();
                                    dadosOK = true;
                                    encodeAssinaturaBorr = Base64.encodeToString(assintBorrByte, Base64.DEFAULT);
                                } else {
                                    encodeAssinaturaBorr = "";
                                    assintBorrByte = null;
                                    dadosOK = false;
                                }
                            }
                            /* *********** FIM *************/

                            /* --- STATUS COLETAS --- */
                            /* 1 - Salvas/Não Enviadas*/
                            /* 2 - Enviadas */
                            /* 3 - Em Análise */
                            /* 4 - Agenda */
                            /* 5 - Produção */
                            /* 6 - Recusado */
                            /* 7 - Aguardando Crédito */
                            /* C - Pendente de Validação dos Clientes */
                            /* P - Pendente de Validação de Produtos (Totvs) */

                            if (dadosOK) {
                                /*
                                 * ATUALIZAÇÃO DOS DADOS
                                 * **/
                                GettersSetters.setInfoAdicionais(cmpInfoAdicionais.getText().toString().toUpperCase().
                                        replace("*", " ").
                                        replace("|", " ").
                                        replace("_", " ").
                                        replace("@", "").
                                        replace("(", "").
                                        replace(")", "")); //INFORMAÇÕES ADICIONAIS

                                /*
                                 * SALVA COLETAS
                                 * **/
                                if (GettersSetters.getInfoAdicionais().trim().isEmpty()) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(ColetaConclusao.this);
                                    builder.setTitle("INFORMAÇÕES ADICIONAIS");
                                    builder.setIcon(R.drawable.exclamation);
                                    builder.setCancelable(false);
                                    builder.setMessage("O campo INFORMAÇÕES ADICIONAIS está em BRANCO, continua?");
                                    builder.setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            SalvaColetas salvaColetas = new SalvaColetas();
                                            salvaColetas.execute(true);
                                        }
                                    });
                                    builder.setNegativeButton("Preencher", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            cmpInfoAdicionais.requestFocus();
                                            btnConfirmar.setVisibility(View.VISIBLE);
                                        }
                                    });
                                    AlertDialog dialogNumCol = builder.create();
                                    dialogNumCol.show();
                                } else {
                                    SalvaColetas salvaColetas = new SalvaColetas();
                                    salvaColetas.execute(true);
                                }
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(ColetaConclusao.this);
                                builder.setTitle("Erro ao gravar a Coleta!");
                                builder.setIcon(R.drawable.error);
                                builder.setCancelable(true);
                                builder.setMessage("Esta coleta não pode ser gravada.\nVerifique os dados e tente novamente ou reinicie a aplicação!");
                                AlertDialog dialogNumCol = builder.create();
                                dialogNumCol.show();
                                btnConfirmar.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ColetaConclusao.this);
                    builder.setTitle("Dados inconsistentes!");
                    builder.setIcon(R.drawable.error);
                    builder.setCancelable(true);
                    builder.setMessage("Os dados desta Coleta estão inconsistentes.\nA aplicação será reiniciada!");
                    builder.setPositiveButton("Reiniciar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent mStartActivity = new Intent(ColetaConclusao.this, Login.class);
                            int mPendingIntentId = 123456;
                            PendingIntent mPendingIntent = PendingIntent.getActivity(ColetaConclusao.this, mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
                            AlarmManager mgr = (AlarmManager) ColetaConclusao.this.getSystemService(Context.ALARM_SERVICE);
                            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                            System.exit(0);
                        }
                    });
                    AlertDialog dialogNumCol = builder.create();
                    dialogNumCol.show();
                    btnConfirmar.setVisibility(View.VISIBLE);
                }
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ColetaConclusao.this);
                builder.setCancelable(true);
                builder.setMessage("Deseja cancelar o processo? \nTodas as informações serão perdidas!");
                builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        GettersSetters.resetGettersSetters();
                        finishAffinity();
                        Intent it = new Intent(ColetaConclusao.this, Home.class);
                        startActivity(it);
                    }
                });
                builder.setNegativeButton("Não", null);
                builder.show();
            }
        });

        btnAssinar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                assinar();
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

    public static void setAssinaturaCli() {
        try {
            String image_path = GettersSetters.getPathAssinCli() + GettersSetters.getPicNameAssinCli();

            Bitmap bitmap = BitmapFactory.decodeFile(image_path);
            if (bitmap == null) {
                imgViewAssCli.setVisibility(View.GONE);
            } else {
                imgViewAssCli.setVisibility(View.VISIBLE);
                imgViewAssCli.setImageBitmap(bitmap);
//                Snackbar.make(viewSnackBar, "Assinado com Sucesso!", Snackbar.LENGTH_SHORT).show();
            }
        } catch (Exception err) {
            Snackbar.make(viewSnackBar, "Erro ao salvar assinatura" + err, Snackbar.LENGTH_LONG).show();
        }
    }

    public void setAssinaturaBorr() {
        try {
            String image_path = GettersSetters.getPathAssinBorr() + GettersSetters.getPicNameAssinBorr(); //getIntent().getStringExtra("imagePath");
            Bitmap bitmap = BitmapFactory.decodeFile(image_path);

            if (bitmap == null) {
                imgViewAssBorr.setVisibility(View.GONE);
                Snackbar.make(viewSnackBar, "Borracheiro sem assinatura, favor assinar!!", Snackbar.LENGTH_SHORT).show();
            } else {
                imgViewAssBorr.setVisibility(View.VISIBLE);
                imgViewAssBorr.setImageBitmap(bitmap);
            }
        } catch (Exception err) {
            Snackbar.make(viewSnackBar, "Erro assinatura borracheiro: " + err, Snackbar.LENGTH_LONG).show();
        }
    }

    /*
     * ASSINATURA
     **/
    public void assinar() {
        GettersSetters.setTipoAssinatura("CLI");
        GettersSetters.setPathAssinCli(null);
        GettersSetters.setPicNameAssinCli("");

        if (GettersSetters.getPathAssinBorr() == null || !GettersSetters.isIsBorracheiro()) {
            Intent i = new Intent(ColetaConclusao.this, Assinatura.class);
            startActivity(i);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(ColetaConclusao.this);
            LayoutInflater layoutInflater = getLayoutInflater();
            View dialogView = layoutInflater.inflate(R.layout.view_assinatura_cliente, null);

            builder.setCancelable(true);
            builder.setView(dialogView);

            final ImageView btnAssinar = dialogView.findViewById(R.id.btnViewAssinar);
            final ImageView btnFotografar = dialogView.findViewById(R.id.btnViewFotografar);

            btnFotografar.setVisibility(View.VISIBLE);

            final AlertDialog dialog = builder.create();

            btnAssinar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GettersSetters.setTipoAssinatura("CLI");
                    Intent i = new Intent(ColetaConclusao.this, Assinatura.class);
                    startActivity(i);
                    dialog.dismiss();
                }
            });

            btnFotografar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GettersSetters.setTipoAssinatura("CLI");
                    Intent i = new Intent(ColetaConclusao.this, Fotografar.class);
                    startActivity(i);
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
    }

    @SuppressWarnings("Convert2Lambda")
    @SuppressLint("StaticFieldLeak")
    public class SalvaColetas extends AsyncTask<Boolean, String, Boolean> {
        AlertDialog dialogAtualizaBorracheiros = null;
        AlertDialog.Builder builderEnviaColeta = null;

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPreExecute() {
            AlertDialog.Builder builder = new AlertDialog.Builder(ColetaConclusao.this);
            LayoutInflater layoutInflater = getLayoutInflater();
            final View dialogView = layoutInflater.inflate(R.layout.dialog_carregamento, null);
            final TextView textView = dialogView.findViewById(R.id.txtInfoCarreg);
            textView.setText("Salvando Coleta " + GettersSetters.getNumeroColeta() + "...\nAguarde...");
            builder.setCancelable(false);
            builder.setView(dialogView);
            dialogAtualizaBorracheiros = builder.create();
            dialogAtualizaBorracheiros.show();
        }

        @SuppressWarnings("ResultOfMethodCallIgnored")
        @Override
        protected Boolean doInBackground(Boolean... params) {
            boolean salva;
            boolean resultInsereNovoCliente = false;
            boolean resultInsereItens = false;
            boolean resultInsereCabecalho = false;
            int contaItens = 0;

            if (db.insereDadosColetaCabecalho(GettersSetters.getFilial(), GettersSetters.getNumeroColeta(), GettersSetters.getDataColetaEN(), GettersSetters.getCodCli(),
                    GettersSetters.getLojaCli(), GettersSetters.getRzSocialCli(), (!GettersSetters.getCodTotvs().trim().isEmpty() ? GettersSetters.getCodTotvs() : GettersSetters.getCodigoVendedor()), GettersSetters.getQtdItensColeta(),
                    GettersSetters.getValorColeta(), GettersSetters.getCodCondPgto(), GettersSetters.getCondPgto(), GettersSetters.getDescrFormaPgto(), GettersSetters.getInfoAdicionais(),
                    GettersSetters.getCodBorracheiro(), GettersSetters.getNomeBorracheiro(), GettersSetters.getDocBorracheiro(), assintBorrByte, assinatCliByte, "1", GettersSetters.getSeqIdenficador(),
                    GettersSetters.getTipoColeta(), hora, GettersSetters.getIdUsuarioLogado(), "", GettersSetters.getCodigoVendedor(), GettersSetters.getDocCli(), dataChegada, (GettersSetters.isIsOrcamento() ? "T" : "F"),
                    (!CheckConnection.isConnected(ColetaConclusao.this) ? "S" : ""), (GettersSetters.isValidarDadosCli() ? "S" : ""))) {

                resultInsereCabecalho = true;
            }

            if (resultInsereCabecalho) {
                /* ORDENAÇÃO DOS ITENS */
                for (int i = 0; i < GettersSetters.getArrItemColeta().size(); i++) {
                    GettersSetters.getArrItemColeta().set(i, Integer.toString(i + 1));
                }

                for (int i = 0; i < GettersSetters.getArrItemColeta().size(); i++) {
                    if (db.insereDadosColetaItens(GettersSetters.getFilial(), GettersSetters.getNumeroColeta(), GettersSetters.getArrItemColeta().get(i),
                            GettersSetters.getDataColetaEN(), GettersSetters.getCodCli(), GettersSetters.getLojaCli(), GettersSetters.getArrQtdItemColeta().get(i),
                            GettersSetters.getArrValorUnit().get(i), GettersSetters.getArrValorTotal().get(i),//dfTotal.format(Double.parseDouble(GettersSetters.getArrValorUnit().get(i)) * Double.parseDouble(GettersSetters.getArrQtdColeta().get(i))),
                            GettersSetters.getArrCodProdColeta().get(i), GettersSetters.getArrBitolaColeta().get(i),
                            GettersSetters.getArrMarcaColeta().get(i), GettersSetters.getArrModeloColeta().get(i), GettersSetters.getArrSerieColeta().get(i),
                            GettersSetters.getArrDotColeta().get(i), (GettersSetters.getArrMontadoColeta().get(i).equals("Montado") ? "0" : "1"), GettersSetters.getArrDesenhoColeta().get(i),
                            (GettersSetters.getArrUrgenteColeta().get(i).equals("Sim") ? "1" : "0"), GettersSetters.getArrPorcComisBorr().get(i), GettersSetters.getArrValrComisBorr().get(i),
                            "1", GettersSetters.getArrObsItens().get(i), (GettersSetters.getArrCAgua().get(i).equals("Sim") ? "1" : "0"), (GettersSetters.getArrCCamara().get(i).equals("Sim") ? "1" : "0"),
                            (GettersSetters.getArrGarantia().get(i).equals("Sim") ? "1" : "0"))) {
                        contaItens++;
                    }
                }
            }

            /* VALIDAÇÃO USADA PARA TER CERTEZA 100% QUE TODOS OS ITENS FORAM INSERIDOS **/
            if (contaItens == GettersSetters.getArrItemColeta().size()) {
                resultInsereItens = true;
            }

            /* INSERÇÃO DO NOVO CLIENTE **/
            if (GettersSetters.isValidarDadosCli()) {
                ByteArrayOutputStream bmpImgDocsCliRG = new ByteArrayOutputStream();
                ByteArrayOutputStream bmpImgDocsCliCPF = new ByteArrayOutputStream();
                ByteArrayOutputStream bmpImgDocsCliCompResi = new ByteArrayOutputStream();

                String image_pathRG;
                String image_pathCPF;
                String image_pathCompResid;

                Bitmap bitmapRG = null;
                Bitmap bitmapCPF = null;
                Bitmap bitmapCompResid = null;

                if (!GettersSetters.getPathDocsNovoCli().equals("")) {
                    if (!GettersSetters.getPicNameRGCli().trim().equals("")) {
                        image_pathRG = GettersSetters.getPathDocsNovoCli() + GettersSetters.getPicNameRGCli();
                        bitmapRG = BitmapFactory.decodeFile(image_pathRG);
                    }

                    if (!GettersSetters.getPicNameDocCli().trim().equals("")) {
                        image_pathCPF = GettersSetters.getPathDocsNovoCli() + GettersSetters.getPicNameDocCli();
                        bitmapCPF = BitmapFactory.decodeFile(image_pathCPF);
                    }

                    if (!GettersSetters.getPicNameComprResidCli().trim().equals("")) {
                        image_pathCompResid = GettersSetters.getPathDocsNovoCli() + GettersSetters.getPicNameComprResidCli();
                        bitmapCompResid = BitmapFactory.decodeFile(image_pathCompResid);
                    }

                    try {
                        if (bitmapRG != null) {
                            bitmapRG.compress(Bitmap.CompressFormat.PNG, 100, bmpImgDocsCliRG);
                            fotoRgCliByte = bmpImgDocsCliRG.toByteArray();
                        } else {
                            fotoRgCliByte = new byte[0];
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("Erro RG");
                        Log.v("Coleta", "Foto RG Cli", e.fillInStackTrace());
                    }

                    try {
                        if (bitmapCPF != null) {
                            bitmapCPF.compress(Bitmap.CompressFormat.PNG, 100, bmpImgDocsCliCPF);
                            fotoDocCliByte = bmpImgDocsCliCPF.toByteArray();
                        } else {
                            fotoDocCliByte = new byte[0];
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("Erro CPF");
                        Log.v("Coleta", "Foto Doc Cli", e.fillInStackTrace());
                    }

                    try {
                        if (bitmapCompResid != null) {
                            bitmapCompResid.compress(Bitmap.CompressFormat.PNG, 100, bmpImgDocsCliCompResi);
                            fotoCompResidByte = bmpImgDocsCliCompResi.toByteArray();
                        } else {
                            fotoCompResidByte = new byte[0];
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("Erro Comp Resid");
                        Log.v("Coleta", "Foto Comp Resid", e.fillInStackTrace());
                    }
                }

//                    String encodedRG = Base64.encodeToString(photoRGCli, Base64.DEFAULT);
//                    String encodedDoc = Base64.encodeToString(photoCPFCli, Base64.DEFAULT);
//                    String encodedCompResid = Base64.encodeToString(photoCompResidCli, Base64.DEFAULT);

                if (db.insereDadosColetaCli(GettersSetters.getFilial(), GettersSetters.getNumeroColeta(), GettersSetters.getDataColetaEN(), GettersSetters.getCodCli(), GettersSetters.getLojaCli(),
                        GettersSetters.getDocCli(), GettersSetters.getInscrCli(), GettersSetters.getEmailCli(), GettersSetters.getEndEntregaCli(), GettersSetters.getBairroEntregaCli(), GettersSetters.getMunicEntregaCli(),
                        GettersSetters.getEstEntregaCli(), GettersSetters.getCepEntregaCli(), GettersSetters.getDddEntregaCli(), GettersSetters.getFoneEntregaCli(), GettersSetters.getEndCobranCli(), GettersSetters.getBairroCobranCli(),
                        GettersSetters.getMunicCobranCli(), GettersSetters.getEstCobranCli(), GettersSetters.getCepCobranCli(), GettersSetters.getDddCobranCli(), GettersSetters.getFoneCobranCli(), GettersSetters.getReferenciasComerciais(),
                        fotoRgCliByte, fotoDocCliByte, fotoCompResidByte, GettersSetters.getRzSocialCli(), GettersSetters.getCategoriaCli())) {

                    resultInsereNovoCliente = true;

                    /* DELETA OS ARQUIVOS DAS FOTOS DOS DOCS DE NOVOS CLIENTES **/
                    new File(GettersSetters.getPathDocsNovoCli(), GettersSetters.getPicNameRGCli()).delete();
                    new File(GettersSetters.getPathDocsNovoCli(), GettersSetters.getPicNameDocCli()).delete();
                    new File(GettersSetters.getPathDocsNovoCli(), GettersSetters.getPicNameComprResidCli()).delete();
                } else {
                    Snackbar.make(viewSnackBar, "Erro na inserção do novo cliente! Reinicie o Processo!", Snackbar.LENGTH_LONG).show();
                }
            } else {
                resultInsereNovoCliente = true;
            }

//                /* GERAÇÃO DO PDF PARA BYTE **/
//                if (CheckConnection.isConnected(ColetaConclusao.this)) {
//                    GeraArquivosColeta geraArquivosColeta = new GeraArquivosColeta();
//
//                    File pdfColeta = geraArquivosColeta.montaPdf(ColetaConclusao.this, GettersSetters.getRzSocialCli(), GettersSetters.getNumeroOS(), GettersSetters.getDataColetaEN(),
//                            GettersSetters.getFilial(), GettersSetters.getLojaCli(), GettersSetters.getCodCli(), df.format(Double.parseDouble(GettersSetters.getValorColeta())));
//                    pdfToByte = geraArquivosColeta.pdfToImage(ColetaConclusao.this, pdfColeta, 1750, 2500); //ALTURA e LARGURA da pg igual ao do PDF
//
//                    String encodePdf = Base64.encodeToString(pdfToByte, Base64.DEFAULT);
//                    if (encodePdf != null) {
//                        //UPDATE PARA INSERCAO DO PDF CONVERTIDO PARA BYTE
//                        String updatePdfByteColeta = "UPDATE COLETA_CABEC " +
//                                "SET COL_CABEC_PDF = '" + encodePdf + "' " +
//                                "WHERE COL_CABEC_FILIAL = '" + GettersSetters.getFilial() + "' " +
//                                " AND COL_CABEC_IDENTIF = '" + GettersSetters.getNumeroOS() + "'" +
//                                " AND COL_CABEC_COD_CLI = '" + GettersSetters.getCodCli() + "'" +
//                                " AND COL_CABEC_LOJA_CLI = '" + GettersSetters.getLojaCli() + "'" +
//                                " AND COL_CABEC_DATA = '" + GettersSetters.getDataColetaEN() + "'";
//
//                        //db.updColetaInsertPdfImg(updatePdfByteColeta, encodePdf);
//                        System.out.println(updatePdfByteColeta);
//
//                        db.updColeta(updatePdfByteColeta);
//                    }
//                }
            if (!GettersSetters.getErroSalvarColetaBDInt().equals("")) {
                resultInsereNovoCliente = false;
                resultInsereItens = false;
                resultInsereCabecalho = false;
                salva = false;
            } else if (resultInsereCabecalho && resultInsereItens && resultInsereNovoCliente) {
                salva = true;

                /* APÓS CONFIRMAÇÃO DE ENVIO DAS INFORMAÇÕES, DELETA AS ASSINATURAS DO APARELHO **/
                if (GettersSetters.getPathAssinBorr() != null) {
                    new File(GettersSetters.getPathAssinBorr(), GettersSetters.getPicNameAssinBorr()).delete();
                }
                if (GettersSetters.getPathAssinCli() != null) {
                    new File(GettersSetters.getPathAssinCli(), GettersSetters.getPicNameAssinCli()).delete();
                }
            } else {
                salva = false;
            }

            return salva;
        }

        @Override
        protected void onPostExecute(Boolean salva) {
            if (dialogAtualizaBorracheiros != null) {
                dialogAtualizaBorracheiros.dismiss();
            }

            if (salva) {
                db.insereNumColeta(GettersSetters.getSeqIdenficador(), GettersSetters.getIdUsuarioLogado(), GettersSetters.getNumeroColeta(), GettersSetters.getDataColetaEN());

//                /* IMPRESSÃO DA COLETA **/
//                ImprimeColeta imprimeColeta = new ImprimeColeta();
//                imprimeColeta.impressaoDeColetas(GettersSetters.getNumeroColeta(), GettersSetters.getDataColetaBR(), GettersSetters.getFilial(), GettersSetters.getDocCli(),
//                        GettersSetters.getCodCli(), GettersSetters.getLojaCli(), GettersSetters.getRzSocialCli(),
//                        GettersSetters.getEndCobranCli() + " " +  GettersSetters.getMunicCobranCli() + " " + GettersSetters.getEstCobranCli(),
//                        GettersSetters.getFoneCobranCli(), GettersSetters.getValorColeta(), GettersSetters.getQtdItensColeta());

                if (CheckConnection.isConnected(ColetaConclusao.this)) {
                    builderEnviaColeta = new AlertDialog.Builder(ColetaConclusao.this);
                    builderEnviaColeta.setIcon(R.drawable.sending);
                    builderEnviaColeta.setCancelable(false);
                    builderEnviaColeta.setTitle("** ATENÇÃO ** Envio de Coleta Eletrônica");
                    builderEnviaColeta.setMessage("O envio da Coleta [" + GettersSetters.getNumeroColeta() + "] será realizado!\nNÃO DESCONECTE O APARELHO DA INTERNET!");
                    builderEnviaColeta.setPositiveButton("Prosseguir", new DialogInterface.OnClickListener() {
                        @SuppressLint("DefaultLocale")
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE).setVisibility(View.GONE);

                            if (CheckConnection.isConnected(ColetaConclusao.this)) {
                                boolean coletaEnviada;

                                String resultado = conecta.selecionaColetaJaEnviada(GettersSetters.getNumeroColeta(), GettersSetters.getIdUsuarioLogado(), GettersSetters.getFilial());

                                if (!resultado.trim().equals("")) {
                                    coletaEnviada = true;
                                    while (coletaEnviada) {
                                        /* ATUALIZA NÚMERO DA COLETA PARA REENVIAR **/
                                        seqIdentifColeta = conecta.selecionaMaxNumColeta(GettersSetters.getIdUsuarioLogado());
                                        numeroColeta = String.format("%06d", Integer.parseInt(GettersSetters.getIdUsuarioLogado())).substring(2, 6) + seqIdentifColeta; // CÓDIGO DO VENDENDOR COM 4 ULTIMOS CARACTERES + SEQUENCIAL

                                        String updateNumeroColetaCabecalho = "UPDATE COLETA_CABEC " +
                                                "SET COL_CABEC_IDENTIF = '" + numeroColeta + "', " +
                                                "    COL_CABEC_SEQ_IDENTIFICADOR = '" + seqIdentifColeta + "' " +
                                                "WHERE trim(COL_CABEC_FILIAL) = '" + GettersSetters.getFilial().trim() + "' " +
                                                " AND trim(COL_CABEC_IDENTIF) = '" + GettersSetters.getNumeroColeta().trim() + "'" +
                                                " AND trim(COL_CABEC_STATUS_ENVIO) = '1' " +
                                                " AND trim(COL_CABEC_RZ_SOCIAL) LIKE '%" + GettersSetters.getRzSocialCli().trim() + "%'" +
                                                " AND trim(COL_CABEC_DATA) = '" + GettersSetters.getDataColetaEN().trim() + "'";

                                        boolean resultadoUpdCabe = db.updColeta(updateNumeroColetaCabecalho);

                                        if (resultadoUpdCabe) {
                                            String updateNumeroColetaItens = "UPDATE COLETA_ITENS " +
                                                    "SET COL_IT_IDENTIF = '" + numeroColeta + "' " +
                                                    "WHERE trim(COL_IT_FILIAL) = '" + GettersSetters.getFilial().trim() + "' " +
                                                    " AND trim(COL_IT_IDENTIF) = '" + GettersSetters.getNumeroColeta().trim() + "'" +
                                                    " AND trim(COL_IT_STATUS_ENVIO) = '1' " +
                                                    " AND trim(COL_IT_COD_CLI) = '" + GettersSetters.getCodCli().trim() + "'" +
                                                    " AND trim(COL_IT_LOJA_CLI) = '" + GettersSetters.getLojaCli().trim() + "'" +
                                                    " AND trim(COL_IT_DATA) = '" + GettersSetters.getDataColetaEN().trim() + "'";
                                            db.updColeta(updateNumeroColetaItens);

                                            /* ATUALIZA NOVO NUMERO DA COLETA PARA CLIENTE NÃO ENVIADO **/
                                            String updNumColCli = "UPDATE COLETA_CLI " +
                                                    "SET COL_CLI_IDENTIF = '" + numeroColeta + "' " +
                                                    "WHERE trim(COL_CLI_FILIAL) = '" + GettersSetters.getFilial().trim() + "' " +
                                                    " AND trim(COL_CLI_IDENTIF) = '" + GettersSetters.getNumeroColeta().trim() + "'" +
                                                    " AND trim(COL_CLI_COD_CLI) = '" + GettersSetters.getCodCli().trim() + "'" +
                                                    " AND trim(COL_CLI_LOJA_CLI) = '" + GettersSetters.getLojaCli().trim() + "'" +
                                                    " AND trim(COL_CLI_DATA) = '" + GettersSetters.getDataColetaEN().trim() + "'";
                                            db.updColeta(updNumColCli);
                                        }

                                        db.insereNumColeta(seqIdentifColeta, GettersSetters.getCodigoVendedor(), numeroColeta, GettersSetters.getDataColetaEN());
                                        GettersSetters.setSeqIdenficador(seqIdentifColeta);
                                        GettersSetters.setNumeroColeta(numeroColeta);
                                        txtNumOS.setText(GettersSetters.getNumeroColeta());

                                        resultado = conecta.selecionaColetaJaEnviada(GettersSetters.getNumeroColeta(), GettersSetters.getIdUsuarioLogado(), GettersSetters.getFilial());
                                        if (resultado.trim().equals("")) {
                                            coletaEnviada = false;

                                            enviaColetas = new EnviaColetas();
                                            enviaColetas.execute();
                                        }
                                    }
                                } else {
                                    enviaColetas = new EnviaColetas();
                                    enviaColetas.execute();
                                }
                            } else {
                                final AlertDialog.Builder builder = new AlertDialog.Builder(ColetaConclusao.this);
                                builder.setIcon(R.drawable.success);
                                builder.setCancelable(false);
                                builder.setTitle("Envio Cancelado!");
                                builder.setMessage("O aparelho foi DESCONECTADO da INTERNET, o envio para o Sistema deve ser feito posteriormente!\n\nNúmero da Coleta: " + GettersSetters.getNumeroColeta() + ". Anote este número!");
                                builder.setNeutralButton("Fim", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        GettersSetters.resetGettersSetters(); //zera variaveis antes de nova coleta
                                        Intent it = new Intent(ColetaConclusao.this, Home.class);
                                        it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(it);
                                    }
                                });
                                builder.setPositiveButton("Nova Coleta", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        GettersSetters.resetGettersSetters(); //zera variaveis antes de nova coleta
                                        Intent it = new Intent(ColetaConclusao.this, ColetaCliente.class);
                                        it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(it);
                                    }
                                });
                                builder.show();
                            }
                        }
                    });
                    builderEnviaColeta.show();
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(ColetaConclusao.this);
                    builder.setIcon(R.drawable.success);
                    builder.setCancelable(false);
                    builder.setTitle("Coleta [" + GettersSetters.getNumeroColeta() + "] SALVA com sucesso!");
                    builder.setMessage("O envio para o Sistema deve ser feito posteriormente!\n\nNúmero da Coleta: " + GettersSetters.getNumeroColeta() + ". Anote este número!");
                    builder.setNeutralButton("Fim", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            GettersSetters.resetGettersSetters(); //zera variaveis antes de nova coleta
                            Intent it = new Intent(ColetaConclusao.this, Home.class);
                            it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(it);
                        }
                    });
                    builder.setPositiveButton("Nova Coleta", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            GettersSetters.resetGettersSetters(); //zera variaveis antes de nova coleta
                            Intent it = new Intent(ColetaConclusao.this, ColetaCliente.class);
                            it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(it);
                        }
                    });
//                    builder.setNegativeButton("Imprime", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//                            imprimeTicket();
//                        }
//                    });
                    builder.show();
                }
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(ColetaConclusao.this);
                builder.setIcon(R.drawable.error);
                builder.setCancelable(false);
                builder.setTitle("Erro ao salvar a Coleta [" + GettersSetters.getNumeroColeta() + "]!");
                builder.setMessage("Erro " + GettersSetters.getErroSalvarColetaBDInt());
                builder.setNeutralButton("Recomeçar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        GettersSetters.resetGettersSetters(); //zera variaveis antes de nova coleta
                        Intent it = new Intent(ColetaConclusao.this, Home.class);
                        it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(it);
                    }
                });
                builder.setPositiveButton("Tentar novamente", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        GettersSetters.setNumeroColeta("");
                        GettersSetters.setSeqIdenficador("");
                        funcoesGenericas.geraNumeroColeta(ColetaConclusao.this, true, conecta, new ConexaoBDInt(ColetaConclusao.this));

                        txtNumOS.setText(GettersSetters.getNumeroColeta());

                        btnConfirmar.setEnabled(true);
                        btnConfirmar.setVisibility(View.VISIBLE);
                    }
                });
                builder.show();
            }
        }
    }

    @SuppressWarnings("Convert2Lambda")
    @SuppressLint("StaticFieldLeak")
    public class EnviaColetas extends AsyncTask<Boolean, String, Boolean> {
        AlertDialog dialoEnviando = null;
        AlertDialog.Builder builderEnvioOK = null;
        AlertDialog dialogEnvioOK = null;
        AlertDialog.Builder builder = null;
        AlertDialog.Builder builderMail = null;

        boolean envioConcluidoCabec = false;
        boolean envioConcluidoItens = false;
        boolean envioConcluidoCliente = false;
        int contaItens = 0;

        String qryUpdColetaCabec;
        String qryUpdColetaItens;
        String encodedRG = "";
        String encodedDoc = "";
        String encodedCompResid = "";

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPreExecute() {
            final AlertDialog.Builder builder = new AlertDialog.Builder(ColetaConclusao.this);
            LayoutInflater layoutInflater = getLayoutInflater();
            final View dialogView = layoutInflater.inflate(R.layout.dialog_carregamento, null);
            final TextView textView = dialogView.findViewById(R.id.txtInfoCarreg);
            textView.setText("Enviando Coleta...\nAguarde...");
            builder.setCancelable(false);
            builder.setPositiveButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    cancelaEnvioColeta(enviaColetas);
                }
            });
            builder.setView(dialogView);
            dialoEnviando = builder.create();
            dialoEnviando.show();
        }

        @Override
        protected Boolean doInBackground(Boolean... strings) {
            //DECODIFICA O PDF DA IMG GRAVADO NO BANCO
            //String decodedImage = Base64.encodeToString(pdfToByte, Base64.DEFAULT);
            //String decodedImage = new String(pdfToByte);

//            GeraArquivosColeta geraArquivosColeta = new GeraArquivosColeta();
//            File pdfColeta = geraArquivosColeta.montaPdf(ColetaConclusao.this, GettersSetters.getRzSocialCli(), GettersSetters.getNumeroOS(), GettersSetters.getDataColetaEN(),
//                    GettersSetters.getFilial(), GettersSetters.getLojaCli(), GettersSetters.getCodCli(), df.format(Double.parseDouble(GettersSetters.getValorColeta())));

//            pdfToByte = geraArquivosColeta.pdfToImage(ColetaConclusao.this, pdfColeta, 1750, 2500); //ALTURA e LARGURA da pg igual ao do PDF
//
//            String decodedImage = Base64.encodeToString(pdfToByte, Base64.DEFAULT);

            GettersSetters.setErroEnvioColetaBDExt("");
            if (conecta.insereDadosColetaCabecalho(GettersSetters.getFilial(), GettersSetters.getNumeroColeta(), GettersSetters.getDataColetaEN(),
                    GettersSetters.getCodCli(), GettersSetters.getLojaCli(), GettersSetters.getRzSocialCli(), (!GettersSetters.getCodTotvs().trim().isEmpty() ? GettersSetters.getCodTotvs() : GettersSetters.getCodigoVendedor()),
                    GettersSetters.getQtdItensColeta(), GettersSetters.getValorColeta(), GettersSetters.getCodCondPgto(), GettersSetters.getDescrFormaPgto(), GettersSetters.getInfoAdicionais(),
                    GettersSetters.getCodBorracheiro(), GettersSetters.getNomeBorracheiro(), GettersSetters.getDocBorracheiro(), encodeAssinaturaBorr, encodeAssinaturaCli, Integer.parseInt(GettersSetters.getSeqIdenficador()),
                    GettersSetters.getTipoColeta(), hora, GettersSetters.getIdUsuarioLogado(), "", GettersSetters.getCodigoVendedor(), GettersSetters.getDocCli(), dataChegada,
                    (GettersSetters.isIsOrcamento() ? "T" : "F"), ColetaConclusao.this, (!CheckConnection.isConnected(ColetaConclusao.this) ? "S" : ""), (GettersSetters.isValidarDadosCli() ? "S" : ""))) {

                envioConcluidoCabec = true;

                for (int i = 0; i < GettersSetters.getArrItemColeta().size(); i++) {
                    if (conecta.insereDadosColetaItens(GettersSetters.getFilial(), GettersSetters.getNumeroColeta(), GettersSetters.getArrItemColeta().get(i),
                            GettersSetters.getDataColetaEN(), GettersSetters.getCodCli(), GettersSetters.getLojaCli(), GettersSetters.getArrQtdItemColeta().get(i),
                            GettersSetters.getArrValorUnit().get(i), GettersSetters.getArrValorTotal().get(i),//dfTotal.format(Double.parseDouble(GettersSetters.getArrValorUnit().get(i)) * Double.parseDouble(GettersSetters.getArrQtdColeta().get(i))),
                            GettersSetters.getArrCodProdColeta().get(i), GettersSetters.getArrBitolaColeta().get(i),
                            GettersSetters.getArrMarcaColeta().get(i), GettersSetters.getArrModeloColeta().get(i), GettersSetters.getArrSerieColeta().get(i),
                            GettersSetters.getArrDotColeta().get(i), (GettersSetters.getArrMontadoColeta().get(i).equals("Montado") ? "0" : "1"), GettersSetters.getArrDesenhoColeta().get(i),
                            (GettersSetters.getArrUrgenteColeta().get(i).equals("Sim") ? "1" : "0"), GettersSetters.getArrPorcComisBorr().get(i), GettersSetters.getArrValrComisBorr().get(i),
                            GettersSetters.getArrObsItens().get(i), (GettersSetters.getArrCAgua().get(i).equals("Sim") ? "1" : "0"), (GettersSetters.getArrCCamara().get(i).equals("Sim") ? "1" : "0"),
                            (GettersSetters.getArrGarantia().get(i).equals("Sim") ? "1" : "0"), ColetaConclusao.this, GettersSetters.getArrFilialOri().get(i), GettersSetters.getArrColetaOri().get(i),
                            GettersSetters.getArrItemOri().get(i))) {
                        contaItens++;

                        if (!GettersSetters.getErroEnvioColetaBDExt().trim().equals("")) {
                            GettersSetters.setErroEnvioColetaBDExt("Erro envio item " + i + "  " + GettersSetters.getErroEnvioColetaBDExt().trim());
                            envioConcluidoItens = false;
                            contaItens = 0;
                            break;
                        }
                    }
                }

                if (contaItens == Integer.parseInt(GettersSetters.getQtdItensColeta())) {
                    envioConcluidoItens = true;

                    qryUpdColetaCabec = "UPDATE COLETA_CABEC " +
                            "SET COL_CABEC_STATUS_ENVIO = '2' " +
                            "WHERE trim(COL_CABEC_FILIAL) = '" + GettersSetters.getFilial().trim() + "' " +
                            "  AND trim(COL_CABEC_IDENTIF) = '" + GettersSetters.getNumeroColeta().trim() + "'" +
                            "  AND trim(COL_CABEC_STATUS_ENVIO) = '1' " +
                            "  AND trim(COL_CABEC_COD_CLI) = '" + GettersSetters.getCodCli().trim() + "' " +
                            "  AND trim(COL_CABEC_LOJA_CLI) = '" + GettersSetters.getLojaCli().trim() + "' " +
                            "  AND trim(COL_CABEC_DATA) = '" + GettersSetters.getDataColetaEN().trim() + "'";

                    db.updColeta(qryUpdColetaCabec);

                    qryUpdColetaItens = "UPDATE COLETA_ITENS " +
                            "SET COL_IT_STATUS_ENVIO = '2' " +
                            "WHERE trim(COL_IT_FILIAL) = '" + GettersSetters.getFilial().trim() + "' " +
                            "  AND trim(COL_IT_IDENTIF) = '" + GettersSetters.getNumeroColeta().trim() + "'" +
                            "  AND trim(COL_IT_STATUS_ENVIO) = '1' " +
                            "  AND trim(COL_IT_COD_CLI) = '" + GettersSetters.getCodCli().trim() + "'" +
                            "  AND trim(COL_IT_LOJA_CLI) = '" + GettersSetters.getLojaCli().trim() + "' " +
                            "  AND trim(COL_IT_DATA) = '" + GettersSetters.getDataColetaEN().trim() + "'";

                    db.updColeta(qryUpdColetaItens);
                }

                if (GettersSetters.isValidarDadosCli()) {
                    try {
                        encodedRG = Base64.encodeToString(fotoRgCliByte, Base64.DEFAULT);
                    } catch (Exception e) {
                        e.printStackTrace();
                        encodedRG = "";
                    }

                    try {
                        encodedDoc = Base64.encodeToString(fotoDocCliByte, Base64.DEFAULT);
                    } catch (Exception e) {
                        e.printStackTrace();
                        encodedDoc = "";
                    }

                    try {
                        encodedCompResid = Base64.encodeToString(fotoCompResidByte, Base64.DEFAULT);
                    } catch (Exception e) {
                        e.printStackTrace();
                        encodedCompResid = "";
                    }

                    if (conecta.insereDadosColetaCli(GettersSetters.getFilial(), GettersSetters.getNumeroColeta(), GettersSetters.getDataColetaEN(), GettersSetters.getCodCli(), GettersSetters.getLojaCli(),
                            GettersSetters.getDocCli(), GettersSetters.getInscrCli(), GettersSetters.getRzSocialCli(), GettersSetters.getEmailCli(), GettersSetters.getEndEntregaCli(), GettersSetters.getBairroEntregaCli(),
                            GettersSetters.getMunicEntregaCli(), GettersSetters.getEstEntregaCli(), GettersSetters.getCepEntregaCli(), GettersSetters.getDddEntregaCli(), GettersSetters.getFoneEntregaCli(), GettersSetters.getEndCobranCli(),
                            GettersSetters.getBairroCobranCli(), GettersSetters.getMunicCobranCli(), GettersSetters.getEstCobranCli(), GettersSetters.getCepCobranCli(), GettersSetters.getDddCobranCli(), GettersSetters.getFoneCobranCli(),
                            GettersSetters.getReferenciasComerciais(), encodedRG, encodedDoc, encodedCompResid, GettersSetters.getCategoriaCli())) {
                        envioConcluidoCliente = true;
                    }
                } else {
                    envioConcluidoCliente = true;
                }
            } else {
                envioConcluidoCabec = false;
                envioConcluidoItens = false;
                envioConcluidoCliente = false;
                updColetasReenvio();
            }

            if (!GettersSetters.getErroEnvioColetaBDExt().trim().equals("")) {
                envioConcluidoCabec = false;
                envioConcluidoItens = false;
                envioConcluidoCliente = false;
            }

            if (envioConcluidoCabec && envioConcluidoItens && envioConcluidoCliente) {
                /* CONFIRMAÇÃO SE A COLETA REALMENTE FOI ENVIADA **/
                try {
                    int dadosLocalizados;
                    ResultSet rsConfirmaEnvio = conecta.buscaColetasExtCabec(GettersSetters.getDataColetaEN(), GettersSetters.getNumeroColeta(),
                            GettersSetters.getRzSocialCli(), GettersSetters.getFilial(), GettersSetters.getCodigoVendedor(), false, "2", "");
                    rsConfirmaEnvio.last();
                    dadosLocalizados = rsConfirmaEnvio.getRow();

                    if (dadosLocalizados > 0) {
                        rsConfirmaEnvio = conecta.buscaColetaExtItens(GettersSetters.getFilial(), GettersSetters.getNumeroColeta(), GettersSetters.getDataColetaBR(), GettersSetters.getCodCli(), GettersSetters.getLojaCli());
                        rsConfirmaEnvio.last();
                        dadosLocalizados = rsConfirmaEnvio.getRow();

                        if (dadosLocalizados != Integer.parseInt(GettersSetters.getQtdItensColeta())) {
                            envioConcluidoCabec = false;
                            envioConcluidoItens = false;
                            envioConcluidoCliente = false;
                            updColetasReenvio();
                        }
                    } else {
                        envioConcluidoCabec = false;
                        envioConcluidoItens = false;
                        envioConcluidoCliente = false;
                        updColetasReenvio();
                    }
                } catch (SQLException exp) {
                    exp.printStackTrace();
                    Log.v("Coleta", "Confrm Envio Cabec", exp.fillInStackTrace());
                }
            }

            return envioConcluidoCabec && envioConcluidoItens && envioConcluidoCliente;
        }

        @Override
        protected void onPostExecute(Boolean sucesso) {
            if (dialoEnviando != null) {
                dialoEnviando.dismiss();
            }

            isEmail = false;
            isSendWhats = false;
            isSendTelegram = false;

            if (sucesso) {
                builderEnvioOK = new AlertDialog.Builder(ColetaConclusao.this);
                builderEnvioOK.setTitle("Coleta enviada com Sucesso!");
                builderEnvioOK.setIcon(R.drawable.success);
                builderEnvioOK.setCancelable(false);
                builderEnvioOK.setMessage("Coleta [" + GettersSetters.getNumeroColeta() + "] enviada com Sucesso!\n\n*** Escreva este número nos Pneus!");
                builderEnvioOK.setPositiveButton("OK", null);
                dialogEnvioOK = builderEnvioOK.create();
                dialogEnvioOK.show();

                dialogEnvioOK.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        builder = new android.app.AlertDialog.Builder(ColetaConclusao.this);
                        final LayoutInflater inflater = getLayoutInflater();
                        View dialogView = inflater.inflate(R.layout.view_opcoes_fim_coleta, null);
                        builder.setCancelable(false);
                        builder.setView(dialogView);

                        final ImageView imgViewEmail = dialogView.findViewById(R.id.imgViewEmail);
                        final ImageView imgViewWhatsapp = dialogView.findViewById(R.id.imgViewWhatsapp);
//                        final CardView cardImprime = dialogView.findViewById(R.id.cardImprime);
                        final ImageView imgViewImprimir = dialogView.findViewById(R.id.imgViewImprimir);
                        final Button btnFechar = dialogView.findViewById(R.id.btnFechar);
                        dialogEnviarColeta = builder.create();

                        imgViewEmail.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                isEmail = true;
                                isCancelarEnvioEmail = false;

                                builderMail = new android.app.AlertDialog.Builder(ColetaConclusao.this);
                                builderMail.setIcon(R.drawable.emailsender);
                                builderMail.setCancelable(false);
                                builderMail.setTitle("E-mail");
                                builderMail.setMessage("Digite o e-mail do Cliente:");
                                final EditText inputEmail = new EditText(ColetaConclusao.this);
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
                                                "WHERE trim(COL_CLI_FILIAL) = '" + GettersSetters.getFilial() + "'" +
                                                "  AND trim(COL_CLI_IDENTIF) = '" + GettersSetters.getNumeroColeta() + "'" +
                                                "  AND trim(COL_CLI_DATA) = '" + GettersSetters.getDataColetaEN() + "'" +
                                                "  AND trim(COL_CLI_COD_CLI) = '" + GettersSetters.getCodCli() + "'" +
                                                "  AND trim(COL_CLI_LOJA_CLI) = '" + GettersSetters.getLojaCli() + "'";
                                        Cursor cursor = db.buscaColetaCli(qryBuscaCliColeta, true);
                                        if (cursor.getCount() > 0) {
                                            emailCli = db.novoClienteColeta("COL_CLI_EMAIL", GettersSetters.getCodCli(), GettersSetters.getLojaCli(), false, GettersSetters.getNumeroColeta()).trim();
                                        } else {
                                            emailCli = conecta.cliente("A1_EMAIL", GettersSetters.getCodCli(), GettersSetters.getLojaCli()).trim();
                                        }
                                    }
                                });
                                final AlertDialog dialogMail = builderMail.create();
                                dialogMail.show();

                                dialogMail.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                    @Override
                                    public void onDismiss(DialogInterface dialog) {
                                        if (!isCancelarEnvioEmail) {
                                            if (emailCli != null) {
                                                if (!emailCli.trim().equals("") && !emailCli.toUpperCase().contains("NOMAIL") && !emailCli.equals(".")) {
                                                    try {
                                                        taskEnviaEmail = new TaskEnviaEmail();
                                                        taskEnviaEmail.execute(GettersSetters.getCodTotvs(), GettersSetters.getRzSocialCli(), GettersSetters.getNumeroColeta(), GettersSetters.getDataColetaBR(),
                                                                GettersSetters.getFilial(), GettersSetters.getLojaCli(), GettersSetters.getCodCli(), df.format(Double.parseDouble(GettersSetters.getValorColeta())), emailCli);
                                                    } catch (Exception e) {
                                                        Snackbar.make(viewSnackBar, "Erro enviar e-mail " + e.toString(), Snackbar.LENGTH_LONG).show();
                                                    }
                                                } else {
                                                    AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ColetaConclusao.this);
                                                    builder.setIcon(R.drawable.mail);
                                                    builder.setCancelable(false);
                                                    builder.setTitle("Sem e-mail");
                                                    builder.setMessage("Cliente " + GettersSetters.getRzSocialCli() + " não possui e-mail cadastrado ou o e-mail (" + emailCli.trim() + ") é inválido!\nEnvio do e-mail interrompido!");
                                                    builder.setPositiveButton("Fechar", null);
                                                    AlertDialog dialogEmail = builder.create();
                                                    dialogEmail.show();
                                                }
                                            } else {
                                                Toast.makeText(ColetaConclusao.this, "Erro ao enviar email.", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }
                                });
                            }
                        });

                        imgViewWhatsapp.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent sendIntent = new Intent();
                                sendIntent.setPackage(null);
                                if (ColetaConclusao.this.getPackageManager().getLaunchIntentForPackage("com.whatsapp") != null) {
                                    sendIntent.setPackage("com.whatsapp");
                                } else if (ColetaConclusao.this.getPackageManager().getLaunchIntentForPackage("com.whatsapp.w4b") != null) {
                                    sendIntent.setPackage("com.whatsapp.w4b"); //WPP BUSINESS
                                }

                                if (sendIntent.getPackage() != null) {
                                    isSendWhats = true;
                                    celular = "";
                                    isCelValid = false;

                                    builderMail = new android.app.AlertDialog.Builder(ColetaConclusao.this);
                                    builderMail.setIcon(R.drawable.whatsapp);
                                    builderMail.setCancelable(false);
                                    builderMail.setTitle("Número de celular");
                                    builderMail.setMessage("Digite o número de celular para encaminhar a coleta por WhatsApp!");
                                    final EditText inputCelular = new EditText(ColetaConclusao.this);
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
                                        }
                                    });
                                    final AlertDialog dialogCelular = builderMail.create();
                                    dialogCelular.show();

                                    dialogCelular.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                        @Override
                                        public void onDismiss(DialogInterface dialogInterface) {
                                            if (isCelValid) {
                                                geraPdfEmaiWhats = new GeraPdfEmaiWhats();
                                                geraPdfEmaiWhats.execute(isEmail, isSendWhats);
                                            } else {
                                                AlertDialog.Builder buildWpp = new AlertDialog.Builder(ColetaConclusao.this);
                                                buildWpp.setTitle("Número Inválido!");
                                                buildWpp.setIcon(R.drawable.number);
                                                buildWpp.setCancelable(false);
                                                buildWpp.setMessage("Número inválido! Verifique os dados e tente novamente.");
                                                buildWpp.setPositiveButton("Entendi", null);
                                                AlertDialog dialogWpp = buildWpp.create();
                                                dialogWpp.show();
                                            }
                                        }
                                    });
                                } else {
                                    AlertDialog.Builder builderFilial = new AlertDialog.Builder(ColetaConclusao.this);
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
                        imgViewImprimir.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                imprimeTicket();
                            }
                        });
                        btnFechar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialogEnviarColeta.dismiss();
                            }
                        });
                        dialogEnviarColeta.show();

                        /* AO FINALIZAR O DIALOGO DOS ENVIOS **/
                        dialogEnviarColeta.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialogInterface) {
                                builderEnvioOK = new AlertDialog.Builder(ColetaConclusao.this);
                                builderEnvioOK.setTitle("Processo finalizado!");
                                builderEnvioOK.setIcon(R.drawable.success);
                                builderEnvioOK.setCancelable(false);
                                builderEnvioOK.setMessage("Processo concuído com sucesso!\nO que deseja fazer?");
                                builderEnvioOK.setNegativeButton("Finalizar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent it = new Intent(ColetaConclusao.this, Home.class);
                                        it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(it);
                                    }
                                });
                                builderEnvioOK.setPositiveButton("Nova Coleta", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        GettersSetters.resetGettersSetters(); //zera variaveis antes de nova coleta
                                        Intent it = new Intent(ColetaConclusao.this, ColetaCliente.class);
                                        it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(it);
                                    }
                                });
                                dialogEnvioOK = builderEnvioOK.create();
                                dialogEnvioOK.show();
                            }
                        });
                    }
                });
            } else {
                AlertDialog.Builder builderEnvioNOK = new AlertDialog.Builder(ColetaConclusao.this);
                builderEnvioNOK.setTitle("Erro ao enviar a Coleta!");
                builderEnvioNOK.setIcon(R.drawable.error);
                builderEnvioNOK.setCancelable(false);
                builderEnvioNOK.setMessage("Envie esta Coleta manualmente ou tente novamente!\n" + (!GettersSetters.getErroEnvioColetaBDExt().trim().equals("") ? "Erro: " + GettersSetters.getErroEnvioColetaBDExt().trim() : ""));
                builderEnvioNOK.setPositiveButton("Tentar Novamente", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        updColetasReenvio();
                        enviaColetas = new EnviaColetas();
                        enviaColetas.execute();
                    }
                });
                builderEnvioNOK.setNegativeButton("Finalizar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        updColetasReenvio();
                        Intent it = new Intent(ColetaConclusao.this, Home.class);
                        it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(it);
                    }
                });
                AlertDialog dialogEnvioNOK = builderEnvioNOK.create();
                dialogEnvioNOK.show();
            }

            db.close();
        }
    }

    public void cancelaEnvioColeta(AsyncTask envioColeta) {
        AlertDialog.Builder builderSucesso = new AlertDialog.Builder(ColetaConclusao.this);
        builderSucesso.setIcon(R.drawable.sending);
        builderSucesso.setTitle("Confirma o cancelamento do Envio?");
        builderSucesso.setMessage("Caso cancelar, deverá enviar manualmente!");
        builderSucesso.setCancelable(true);
        builderSucesso.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                envioColeta.cancel(true);
                Toast.makeText(ColetaConclusao.this, "Envio cancelado!", Toast.LENGTH_LONG).show();
            }
        });
        builderSucesso.setNegativeButton("Não", null);
        builderSucesso.show();
    }

    @SuppressLint("StaticFieldLeak")
    public class TaskEnviaEmail extends AsyncTask<String, String, Boolean> {
        AlertDialog dialogEnviaEmail = null;

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPreExecute() {
            final AlertDialog.Builder builder = new AlertDialog.Builder(ColetaConclusao.this);
            LayoutInflater layoutInflater = getLayoutInflater();
            final View dialogView = layoutInflater.inflate(R.layout.dialog_carregamento, null);
            final TextView textView = dialogView.findViewById(R.id.txtInfoCarreg);
            textView.setText("Enviando coleta por E-mail, para\n" + emailCli.trim() + "\nAguarde...");
            builder.setPositiveButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    taskEnviaEmail.cancel(true);
                    Toast.makeText(ColetaConclusao.this, "Envio do E-mail cancelado", Toast.LENGTH_SHORT).show();
                }
            });
            builder.setCancelable(false);
            builder.setView(dialogView);
            dialogEnviaEmail = builder.create();
            dialogEnviaEmail.show();
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            if (!strings[8].trim().equals(".") && !strings[8].contains("nomail")) {
                //strings[8] = "ti@vachileski.com.br".trim() + ";" + "ti4@vachileski.com.br".trim();
                strings[8] = emailCli.trim();
                String[] emailsCli = strings[8].split(";");
                sucessoEmail = funcoesGenericas.preparaWhatsappEmail(ColetaConclusao.this, strings[0], strings[1], strings[2], strings[3], strings[4], strings[5], strings[6], strings[7],
                        null, emailsCli, null, "", false, true, "");
            } else {
                Snackbar.make(viewSnackBar, "E-mail [" + strings[8].trim() + "] inválido!", Snackbar.LENGTH_LONG).show();
                sucessoEmail = false;
            }
            return sucessoEmail;
        }

        @Override
        protected void onPostExecute(Boolean sucesso) {
            if (dialogEnviaEmail != null) {
                dialogEnviaEmail.dismiss();
            }

            if (sucesso) {
                AlertDialog.Builder builderSucesso = new AlertDialog.Builder(ColetaConclusao.this);
                builderSucesso.setIcon(R.drawable.emailsender);
                builderSucesso.setTitle("Sucesso");
                builderSucesso.setMessage("E-mail enviado com sucesso!");
                builderSucesso.setCancelable(true);
                builderSucesso.setNegativeButton("Fechar", null);
                builderSucesso.show();
            } else {
                if (!GettersSetters.getErroEnvioColetaBDExt().equals("")) {
                    AlertDialog.Builder builderFilial = new AlertDialog.Builder(ColetaConclusao.this);
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
        }
    }

    public void updColetasReenvio() {
        db.updColeta("UPDATE COLETA_CABEC " +
                "SET COL_CABEC_STATUS_ENVIO = '1' " +
                "WHERE trim(COL_CABEC_FILIAL) = '" + GettersSetters.getFilial().trim() + "' " +
                "  AND trim(COL_CABEC_IDENTIF) = '" + GettersSetters.getNumeroColeta().trim() + "'" +
                "  AND trim(COL_CABEC_COD_CLI) = '" + GettersSetters.getCodCli().trim() + "' " +
                "  AND trim(COL_CABEC_LOJA_CLI) = '" + GettersSetters.getLojaCli().trim() + "' " +
                "  AND trim(COL_CABEC_DATA) = '" + GettersSetters.getDataColetaEN().trim() + "'");

        db.updColeta("UPDATE COLETA_ITENS " +
                "SET COL_IT_STATUS_ENVIO = '1' " +
                "WHERE trim(COL_IT_FILIAL) = '" + GettersSetters.getFilial().trim() + "' " +
                " AND trim(COL_IT_IDENTIF) = '" + GettersSetters.getNumeroColeta().trim() + "'" +
                " AND trim(COL_IT_COD_CLI) = '" + GettersSetters.getCodCli().trim() + "' " +
                " AND trim(COL_IT_LOJA_CLI) = '" + GettersSetters.getLojaCli().trim() + "' " +
                " AND trim(COL_IT_DATA) = '" + GettersSetters.getDataColetaEN().trim() + "'");

        db.updColeta("UPDATE COLETA_CLI " +
                " SET COL_CLI_STATUS_ENVIO = '1' " +
                "WHERE trim(COL_CLI_FILIAL) = '" + GettersSetters.getFilial().trim() + "' " +
                " AND trim(COL_CLI_IDENTIF) = '" + GettersSetters.getNumeroColeta().trim() + "'" +
                " AND trim(COL_CLI_COD_CLI) = '" + GettersSetters.getCodCli().trim() + "'" +
                " AND trim(COL_CLI_LOJA_CLI) = '" + GettersSetters.getLojaCli().trim() + "'" +
                " AND trim(COL_CLI_DATA) = '" + GettersSetters.getDataColetaEN().trim() + "'");

        /* DELETA A COLETA QUANDO DA ERRO NO ENVIO **/
        conecta.deleteColeta(GettersSetters.getFilial(), GettersSetters.getDataColetaEN(), GettersSetters.getNumeroColeta(), false);
    }

    @SuppressLint("InflateParams")
    public void dataChegPn() {
        /* VALIDA DATA MÁXIMA DE CHEGADA DOS PNEUS **/
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(Objects.requireNonNull(simpleDateFormat.parse(ColetaBusca.getData())));
            calendar.add(Calendar.DATE, 10); //SOMA 10 DIAS
            Date date = new Date(calendar.getTimeInMillis());
            dataMaxChegada = simpleDateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        AlertDialog.Builder builderItens = new AlertDialog.Builder(ColetaConclusao.this);
        builderItens.setCancelable(true);
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(ColetaConclusao.this);
        View view1 = layoutInflaterAndroid.inflate(R.layout.view_generica, null);
        LinearLayout llView = view1.findViewById(R.id.llViewGenerica);

        final LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert layoutInflater != null;
        View addViewCalendario;
        CalendarView calendarView;
        EditText editText;
        Button button;
        TextView textView;

        if (Build.VERSION.SDK_INT >= 23) {
            addViewCalendario = layoutInflater.inflate(R.layout.dialog_calendario, null);
            calendarView = addViewCalendario.findViewById(R.id.calendario);
            button = addViewCalendario.findViewById(R.id.btnConfData);
            textView = addViewCalendario.findViewById(R.id.txtDataTemp);

            calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                @SuppressLint("DefaultLocale")
                @Override
                public void onSelectedDayChange(@NonNull CalendarView calendarView, int ano, int mes, int dia) {
                    dataChegada = String.format("%02d", dia) + "/" + String.format("%02d", mes + 1) + "/" + ano;
                    textView.setText(dataChegada);
                }
            });
        } else {
            addViewCalendario = layoutInflater.inflate(R.layout.dialog_calendario_old_api, null);
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
                        String[] dataChegadaArr = editText.getText().toString().trim().split("/");
                        dataChegada = String.format("%02d", Integer.parseInt(dataChegadaArr[0])) + "/" + String.format("%02d", Integer.parseInt(dataChegadaArr[1])) + "/" + dataChegadaArr[2];
                        textView.setText(dataChegada);
                    }
                }
            });
        }

        llView.addView(addViewCalendario);
        builderItens.setView(view1);
        final AlertDialog alertDialog = builderItens.create();
        alertDialog.show();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (textView.getText().toString().isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ColetaConclusao.this);
                    builder.setTitle("Sem data!");
                    if (Build.VERSION.SDK_INT >= 23) {
                        builder.setMessage("Selecione uma data.");
                    } else {
                        builder.setMessage("Digite a data.");
                    }
                    builder.setCancelable(false);
                    builder.setPositiveButton("Ok", null);
                    builder.show();
                } else if (Integer.parseInt(GettersSetters.converteData(dataChegada, "BR")) > Integer.parseInt(GettersSetters.converteData(dataMaxChegada, "BR"))) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ColetaConclusao.this);
                    builder.setTitle("Data inválida!");
                    builder.setMessage("Data de chegada dos pneus na unidade NÃO pode ser MAIOR que " + dataMaxChegada + ".\nSelecione outra data.");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Ok", null);
                    builder.show();
                } else if (Integer.parseInt(GettersSetters.converteData(dataChegada, "BR")) < Integer.parseInt(GettersSetters.converteData(ColetaBusca.getData(), "BR"))) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ColetaConclusao.this);
                    builder.setTitle("Data inválida!");
                    builder.setMessage("Data de chegada dos pneus na unidade NÃO pode ser MENOR que " + ColetaBusca.getData() + ".\nSelecione outra data.");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Ok", null);
                    builder.show();
                } else {
                    if (Build.VERSION.SDK_INT >= 23) {
                        cmpDataChegPn.setText(dataChegada);
                        alertDialog.dismiss();
                    } else {
                        if (FuncoesGenericas.validaData(dataChegada)) {
                            cmpDataChegPn.setText(dataChegada);
                            alertDialog.dismiss();
                        } else {
                            cmpDataChegPn.getText().clear();
                            AlertDialog.Builder builder = new AlertDialog.Builder(ColetaConclusao.this);
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

    @SuppressLint("StaticFieldLeak")
    public class GeraPdfEmaiWhats extends AsyncTask<Boolean, String, Boolean> {
        AlertDialog dialogGerandoPDF = null;

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPreExecute() {
            AlertDialog.Builder builder = new AlertDialog.Builder(ColetaConclusao.this);
            LayoutInflater layoutInflater = getLayoutInflater();
            final View dialogView = layoutInflater.inflate(R.layout.dialog_carregamento, null);
            final TextView textView = dialogView.findViewById(R.id.txtInfoCarreg);
            textView.setText("Gerando Arquivos...\nAguarde...");
            builder.setPositiveButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    geraPdfEmaiWhats.cancel(true);
                    Toast.makeText(ColetaConclusao.this, "Processo Cancelado!", Toast.LENGTH_LONG).show();
                }
            });
            builder.setCancelable(false);
            builder.setView(dialogView);
            dialogGerandoPDF = builder.create();
            dialogGerandoPDF.show();
        }

        @Override
        protected Boolean doInBackground(Boolean... params) {
            return funcoesGenericas.preparaWhatsappEmail(ColetaConclusao.this, GettersSetters.getCodTotvs(), GettersSetters.getRzSocialCli(), GettersSetters.getNumeroColeta(), GettersSetters.getDataColetaEN(),
                    GettersSetters.getFilial(), GettersSetters.getLojaCli(), GettersSetters.getCodCli(), df.format(Double.parseDouble(GettersSetters.getValorColeta())),
                    "+55" + ddd + celular, null, null, "", true, false, "");
        }

        @Override
        protected void onPostExecute(Boolean enviado) {
            if (dialogGerandoPDF != null) {
                dialogGerandoPDF.dismiss();
            }

            if (!enviado) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ColetaConclusao.this);
                builder.setTitle("Geração do PDF");
                builder.setMessage("PDF da Coleta não gerado");
                builder.setCancelable(false);
                builder.setPositiveButton("Ok", null);
                builder.show();
            }
        }

    }

    /*
     * IMPRESSÃO DOS TICKET
     **/
    public void imprimeTicket() {
        Printooth.INSTANCE.init(ColetaConclusao.this);
        if (!Printooth.INSTANCE.hasPairedPrinter()) {
            startActivityForResult(new Intent(ColetaConclusao.this, ScanningActivity.class), ScanningActivity.SCANNING_FOR_PRINTER);
        } else {
            /* IMPRESSÃO DA COLETA **/
            ImprimeColeta imprimeColeta = new ImprimeColeta();
            imprimeColeta.impressaoDeColetas(GettersSetters.getNumeroColeta(), GettersSetters.getDataColetaBR(), GettersSetters.getDescrFilial(), GettersSetters.getDocCli(), GettersSetters.getRzSocialCli(),
                    GettersSetters.getEndEntregaCli() + " - " + GettersSetters.getBairroEntregaCli() + " - " + GettersSetters.getMunicEntregaCli() + "/" + GettersSetters.getEstEntregaCli(),
                    GettersSetters.getDddEntregaCli().replace("0", "") + GettersSetters.getFoneEntregaCli(), GettersSetters.getValorColeta(), Integer.parseInt(GettersSetters.getQtdItensColeta()),
                    GettersSetters.getCodigoVendedor() + " " + GettersSetters.getNomeVend(), this, GettersSetters.getArrBitolaColeta(), GettersSetters.getArrValorUnit(),
                    ((GettersSetters.getCondPgto().equals("0") ? "A VISTA" : GettersSetters.getCondPgto() + " dias") + " " + GettersSetters.getDescrFormaPgto().trim()), GettersSetters.getArrMontadoColeta(),
                    GettersSetters.getArrSerieColeta(), GettersSetters.getArrDotColeta(), GettersSetters.getArrMarcaColeta());
        }
    }
}