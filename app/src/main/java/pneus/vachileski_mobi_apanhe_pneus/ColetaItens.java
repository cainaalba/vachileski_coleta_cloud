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
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Objects;

import pneus.vachileski_mobi_funcoes_genericas.CheckConnection;
import pneus.vachileski_mobi_funcoes_genericas.ConexaoBDExt;
import pneus.vachileski_mobi_funcoes_genericas.ConexaoBDInt;
import pneus.vachileski_mobi_funcoes_genericas.GettersSetters;

@SuppressWarnings({"SpellCheckingInspection", "DanglingJavadoc", "Convert2Lambda"})
public class ColetaItens extends AppCompatActivity {
    Button btnAddItem, btnAvancar, btnVoltar, btnCancelar,
            btnConfEdit, btnCancelEdit, btnBuscaProduto, btnAtualizaProdutos,
            btnCancelaCopia, btnTrocatTipoPnCasa, btnVisualizaItens,
            btnImportarItens;
    TextView txtQtdItens, txtVlrTotal;

    EditText cmpItem, cmpQtdItem, cmpBitola, cmpModeloPneu, cmpSeriePneu,
            cmpDotPneu, cmpDesenhoPneu, cmpValorSrv;
    CheckBox chkUrgente, chkcAgua, chkcCamara, chkGarantia;
    AutoCompleteTextView cmpMarcaPneu, cmpObsItem;

    public static ProgressDialog mProgressDialogItens;

    ToggleButton tgBtnMontado;

    LinearLayout llEdit, llCamposOst, llOpcoes, formBotoesColeta;

    View viewSnackBar;
    int totalItens = 0, sequencialItens = 1;

    double valorComissaoBorracheiro = 0, percentualComissaoBorracheiro = 0, valorTotalColeta = 0, porcentgDescontoGrp = 0, tempPercentualComissaoBorracheiro = 0,
            valorUnitarioItem = 0, valorTotalItem = 0, valorTabelaProduto = 0, valorTabela = 0,
            porcentgDesconto = 0, totalDesconto = 0, valorBloqueio = 0, baseCalculo = 0,
            valorMaxComissaoBorracheiro = 0;
    String codigoProduto = "", descricaoProduto = "", codigoCondicaoPagamento = "",
            descrFormaPagaento = "", descrCondicaoPagamento = "", condicaoPagamento = "",
            numeroColetaImportacao = "", msgColetasImportadas = "";

    String[] filialImportar = new String[2];

    boolean isPneuCargaPasseio = false;
    boolean isPneuAgricola = false;
    boolean isEdicaoItem = false;
    boolean isTabela = false;
    boolean isReclamacao = false;

    AlertDialog dialogItens = null;

    final DecimalFormat df = new DecimalFormat("#,##0.00");

    final ArrayList<String> arrItemColeta = new ArrayList<>();
    final ArrayList<String> arrQtdItemColeta = new ArrayList<>();
    final ArrayList<String> arrCodProdColeta = new ArrayList<>();
    final ArrayList<String> arrBitolaColeta = new ArrayList<>();
    final ArrayList<String> arrMarcaColeta = new ArrayList<>();
    final ArrayList<String> arrModeloColeta = new ArrayList<>();
    final ArrayList<String> arrSerieColeta = new ArrayList<>();
    final ArrayList<String> arrDotColeta = new ArrayList<>();
    final ArrayList<String> arrMontadoColeta = new ArrayList<>();
    final ArrayList<String> arrDesenhoColeta = new ArrayList<>();
    final ArrayList<String> arrValorUnit = new ArrayList<>();
    final ArrayList<String> arrValorTotal = new ArrayList<>();
    final ArrayList<String> arrVlrTabelaItem = new ArrayList<>();
    final ArrayList<String> arrPercDesc = new ArrayList<>();
    final ArrayList<String> arrPercDescGrp = new ArrayList<>();
    final ArrayList<String> arrUrgenteColeta = new ArrayList<>();
    final ArrayList<String> arrPorcComissBorr = new ArrayList<>();
    final ArrayList<String> arrVlrComissBorr = new ArrayList<>();
    final ArrayList<String> arrObservItem = new ArrayList<>();

    final ArrayList<String> arrVlrBlqIt = new ArrayList<>();
    final ArrayList<String> arrBaseCalcIt = new ArrayList<>();

    final ArrayList<String> arrCodProdutos = new ArrayList<>();
    final ArrayList<String> arrDescProdutos = new ArrayList<>();
    final ArrayList<String> arrProdBlq = new ArrayList<>();
    final ArrayList<String> arrDesenhoProd = new ArrayList<>();
    final ArrayList<String> arrPrecoTabela = new ArrayList<>();
    final ArrayList<String> arrDescVachiOst = new ArrayList<>();
    final ArrayList<String> arrDescLauxen = new ArrayList<>();
    final ArrayList<Boolean> arrIsDescGrp = new ArrayList<>();
    final ArrayList<String> arrCAgua = new ArrayList<>(); //0=Não, 1=Sim
    final ArrayList<String> arrCCamara = new ArrayList<>(); //0=Não, 1=Sim
    final ArrayList<String> arrGarantia = new ArrayList<>(); //0=Não, 1=Sim
    final ArrayList<String> arrFilialOri = new ArrayList<>();
    final ArrayList<String> arrColetaOri = new ArrayList<>();
    final ArrayList<String> arrItemOri = new ArrayList<>();

    final ArrayList<String> arrcodCondPgto = new ArrayList<>();
    final ArrayList<String> arrdescCondPgto = new ArrayList<>();
    final ArrayList<String> arrformaCondPgto = new ArrayList<>();
    final ArrayList<String> arrdescFormaCondPgto = new ArrayList<>();
    final ArrayList<String> arrCondPgto = new ArrayList<>();

    ArrayList<String> arrColetasImportadas = new ArrayList<>();

    final ConexaoBDExt conecta = new ConexaoBDExt();
    ConexaoBDInt db;

    BuscaItens buscaItens = null;
    BuscaItensImportacao buscaItensImportacao = null;

    @SuppressLint({"SetTextI18n", "ApplySharedPref", "UseCompatLoadingForDrawables"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coleta_itens);
        setTitle(getString(R.string.tituloColeta) + " - " + GettersSetters.getDescrTipoColeta());

        db = new ConexaoBDInt(this);

        btnAddItem = findViewById(R.id.btdAddIt);
        btnAvancar = findViewById(R.id.btnAvancar);
        btnVoltar = findViewById(R.id.btnVoltar);
        btnCancelar = findViewById(R.id.btnCancelar);
        btnConfEdit = findViewById(R.id.btnConfEdit);
        btnCancelEdit = findViewById(R.id.btnCancelEdit);
        btnBuscaProduto = findViewById(R.id.btnBuscaProduto);
        btnAtualizaProdutos = findViewById(R.id.btnAtualProdutos);
        btnTrocatTipoPnCasa = findViewById(R.id.btnTrocatTipoPnCasa);
        btnVisualizaItens = findViewById(R.id.btnVisualizaItens);
        btnImportarItens = findViewById(R.id.btnImportarItens);

        cmpItem = findViewById(R.id.txtItem);
        txtQtdItens = findViewById(R.id.txtQtdItens);
        cmpQtdItem = findViewById(R.id.cmpQtdCol);
        cmpBitola = findViewById(R.id.cmpBitCol);
        cmpMarcaPneu = findViewById(R.id.cmpMarcaCol);
        cmpModeloPneu = findViewById(R.id.cmpModCol);
        cmpSeriePneu = findViewById(R.id.cmpSerCol);
        cmpDotPneu = findViewById(R.id.cmpDotCol);
        cmpDesenhoPneu = findViewById(R.id.cmpDesCol);
        cmpValorSrv = findViewById(R.id.cmpValUnit);
        cmpObsItem = findViewById(R.id.cmpObsItem);
        chkUrgente = findViewById(R.id.chkUrgente);
        txtVlrTotal = findViewById(R.id.txtVlrTot);
        tgBtnMontado = findViewById(R.id.toggleButtonMontado);
        llEdit = findViewById(R.id.llEdit);
        formBotoesColeta = findViewById(R.id.formBotoesFimColeta);
        viewSnackBar = findViewById(R.id.viewSnackBar);
        btnCancelaCopia = findViewById(R.id.btnCancelCopia);
        llCamposOst = findViewById(R.id.llCamposOst);
        llOpcoes = findViewById(R.id.llOpcoes);
        chkcAgua = findViewById(R.id.chkcAgua);
        chkcCamara = findViewById(R.id.chkcCamara);
        chkGarantia = findViewById(R.id.chkGarantia);

        txtVlrTotal.setText("R$ 0,00");
        cmpItem.setText(Integer.toString(sequencialItens));
        txtQtdItens.setText(Integer.toString(totalItens));
        cmpQtdItem.setText("1");
        cmpMarcaPneu.setEnabled(false);
        cmpModeloPneu.setEnabled(false);
        cmpSeriePneu.setEnabled(false);
        cmpDotPneu.setEnabled(false);
        cmpDesenhoPneu.setEnabled(false);
        cmpValorSrv.setEnabled(false);
        cmpObsItem.setEnabled(false);
        chkUrgente.setEnabled(false);
        txtVlrTotal.setEnabled(false);
        tgBtnMontado.setEnabled(false);
        chkcAgua.setEnabled(false);
        chkcCamara.setEnabled(false);
        chkGarantia.setEnabled(false);
        chkcAgua.setEnabled(false);
        chkcCamara.setEnabled(false);
        chkGarantia.setEnabled(false);
        btnAddItem.setVisibility(View.GONE);
        btnAvancar.setVisibility(View.GONE);
        llEdit.setVisibility(View.GONE);
        btnCancelaCopia.setVisibility(View.GONE);
        btnTrocatTipoPnCasa.setVisibility(View.GONE);

        if (!CheckConnection.isConnected(ColetaItens.this)) {
            btnAtualizaProdutos.setVisibility(View.GONE);
            btnImportarItens.setVisibility(View.GONE);
        }

        if (totalItens == 0) {
            btnVisualizaItens.setVisibility(View.GONE);
        }

        cmpBitola.requestFocus();

        /* ARRAY COM AS MARCAS **/
        String[] marcas = getResources().getStringArray(R.array.array_marcas);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, marcas);
        cmpMarcaPneu.setThreshold(1);
        cmpMarcaPneu.setAdapter(adapter);

        /* ARRAY COM AS INFORMAÇÕES DOS ITENS **/
        String[] infoPrd = getResources().getStringArray(R.array.mensagens_faturamento);
        ArrayAdapter<String> adapterInfo = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, infoPrd);
        cmpObsItem.setThreshold(1);
        cmpObsItem.setAdapter(adapterInfo);

//        if (CheckConnection.isConnected(ColetaItens.this)) {
//            /** ADICIONA FLAG SE O DESCONTO DE 2% DE COMISSÃO DE BORRACHEIROS PARA ITENS MONTADOS ESTÁ ATIVA **/
//            SharedPreferences.Editor editor = getSharedPreferences(Home.ATUALIZACAO_DADOS, MODE_PRIVATE).edit();
//            editor.putBoolean("com2borr", !conecta.selecionaParametro("VK_CMBRMNT").trim().equals(".F.")); //VALOR DA DEDUÇÃO
//            editor.putString("vlrcom2borr", conecta.selecionaParametro("VK_VDEDCBR")); //CONTROLE DE HABILITAÇÃO
//            editor.commit();
//        }

        if (GettersSetters.getTipoColeta().contains("SE")) {
            cmpQtdItem.setEnabled(false);
        }

        /* VALIDAÇÃO QUANDO PNEUS DA CASA **/
        if (GettersSetters.getTipoColeta().equals("'PC'")) {
            isPneusCasa();
        }

        /* ÓRGÃOS PÚBLICOS **/
        if (GettersSetters.getCategoriaCli().equals("01") && !GettersSetters.getTipoColeta().contains("RECL")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ColetaItens.this);
            builder.setIcon(R.drawable.exclamation);
            builder.setTitle("Órgãos Públicos");
            if ((GettersSetters.getFilial().startsWith("1101") || GettersSetters.getFilial().startsWith("1201")) && GettersSetters.isIsOrcamento()) {
                cmpValorSrv.setEnabled(true);
                builder.setMessage("Para ÓRGÃOS PÚBLICOS alteração de valores permitida pois COM ORÇAMENTO está marcado!");
            } else {
                cmpValorSrv.setEnabled(false);
                builder.setMessage("Para ÓRGÃOS PÚBLICOS não é permitido alterar o valor dos itens, pois são definidos via Licitação!");
            }
            builder.setCancelable(false);
            builder.setNeutralButton("OK", null);
            AlertDialog dialog1 = builder.create();
            dialog1.show();
        }

        /* CLIENTES COM TABELA DE PREÇO **/
        if (!GettersSetters.getTabelaPrcCli().trim().equals("") || GettersSetters.getDescExclusivo() > 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ColetaItens.this);
            builder.setIcon(R.drawable.desconto);
            builder.setTitle("Cliente com " + (GettersSetters.getDescExclusivo() > 0 ? "Desconto Fixo" : "Tabela"));
            builder.setMessage("Para clientes com " + (GettersSetters.getDescExclusivo() > 0 ? "DESCONTO FIXO, " +
                    "NÃO é permitido alterar o valor do item" : "TABELA DE PREÇO, " +
                    "NÃO é permitido alterar o valor do item (CADASTRADO NA TABELA)!") +
                    "\nEm caso de dúvidas, entre em contato com o administrativo.");
            builder.setCancelable(false);
            builder.setPositiveButton("Fechar", null);
            AlertDialog dialog1 = builder.create();
            dialog1.show();
        }

        /* VENDAS ENTRE O GRUPO **/
        if (GettersSetters.isClienteGrp()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ColetaItens.this);
            builder.setIcon(R.drawable.desconto);
            builder.setTitle("Vendas entre o Grupo");
            builder.setMessage("Cliente selecionado é um CLIENTE DO GRUPO e será aplicado em cada item o desconto de " + GettersSetters.getDescGrupo() + "%");
            builder.setCancelable(false);
            builder.setPositiveButton("Fechar", null);
            AlertDialog dialog1 = builder.create();
            dialog1.show();
        } else {
            btnImportarItens.setVisibility(View.GONE);
        }

        btnAtualizaProdutos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VerificacaoProdutos verificacaoProdutos = new VerificacaoProdutos();
                verificacaoProdutos.execute(true);
            }
        });

        btnVisualizaItens.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                try {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ColetaItens.this);
                    LayoutInflater layoutInflaterAndroid = getLayoutInflater();
                    View viewPrincipal = layoutInflaterAndroid.inflate(R.layout.view_generica, null);
                    LinearLayout viewLayout = viewPrincipal.findViewById(R.id.llViewGenerica);

                    for (int i = 0; i < arrItemColeta.size(); i++) {
                        final View viewItens = layoutInflaterAndroid.inflate(R.layout.view_itens_coleta, viewLayout, false);
                        final TextView outItemCol = viewItens.findViewById(R.id.txtItemCol);
                        final TextView outQtdItemCol = viewItens.findViewById(R.id.txtQtdCol);
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

                        final String outCodPrd = arrCodProdColeta.get(i);
                        final String outCAgua = arrCAgua.get(i);
                        final String outCCamara = arrCCamara.get(i);
                        final String outGarantia = arrGarantia.get(i);
                        final String outPercComissBorr = arrPorcComissBorr.get(i);
                        final String outVlrComissBorr = arrVlrComissBorr.get(i);

                        final String outVlrBlq = arrVlrBlqIt.get(i);
                        final String outBaseCalculo = arrBaseCalcIt.get(i);
                        final String outTabelaItem = arrVlrTabelaItem.get(i);
                        final String outPercDesc = arrPercDesc.get(i);
                        final String outMaxPercDesc = arrPercDescGrp.get(i);

                        final String outFilialOri = arrFilialOri.get(i);
                        final String outColetaOri = arrColetaOri.get(i);
                        final String outItemOri = arrItemOri.get(i);

                        outItemCol.setText(arrItemColeta.get(i));
                        outQtdItemCol.setText(arrQtdItemColeta.get(i));
                        outBitCol.setText(arrBitolaColeta.get(i));
                        outMarcaCol.setText(arrMarcaColeta.get(i));
                        outModelCol.setText(arrModeloColeta.get(i));
                        outSerieCol.setText(arrSerieColeta.get(i));
                        outDotCol.setText(arrDotColeta.get(i));
                        outMontado.setText(arrMontadoColeta.get(i));
                        outDesenCol.setText(arrDesenhoColeta.get(i));
                        outVlrUnit.setText(arrValorUnit.get(i));
                        outVlrTotIt.setText(arrValorTotal.get(i));
                        outUrgCol.setText(arrUrgenteColeta.get(i));
                        outObsItem.setText(arrObservItem.get(i));

                        if (!GettersSetters.getTipoColeta().contains("SE") && !GettersSetters.getTipoColeta().contains("PC")) {
                            outMontado.setText("");
                            outUrgCol.setText("");
                        }

                        /* CONFIGURAÇÃO DE ITENS **/
                        configItens.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(ColetaItens.this);
                                LayoutInflater inflater = getLayoutInflater();
                                final View dialogViewOpcItens = inflater.inflate(R.layout.dialog_opcoes_visual_itens, null);
                                builder.setCancelable(true);
                                builder.setView(dialogViewOpcItens);

                                final ImageView btnDelItemCol = dialogViewOpcItens.findViewById(R.id.btnDelItemCol);
                                final ImageView btnEditItemCol = dialogViewOpcItens.findViewById(R.id.btnEditItemCol);
                                final ImageView btnCopiarItem = dialogViewOpcItens.findViewById(R.id.btnCopiar);
                                final CardView cardCopiaItem = dialogViewOpcItens.findViewById(R.id.cardCopiaItem);
                                final AlertDialog dialog = builder.create();

                                if (totalItens == 99) {
                                    cardCopiaItem.setVisibility(View.GONE);
                                } else {
                                    cardCopiaItem.setVisibility(View.VISIBLE);
                                }

                                /*
                                 * EXCLUSÃO DE ITEM *
                                 */
                                btnDelItemCol.setOnClickListener(new View.OnClickListener() {
                                    @SuppressLint("SetTextI18n")
                                    @Override
                                    public void onClick(View v) {
                                        dialog.cancel();

                                        final AlertDialog.Builder builder = new AlertDialog.Builder(ColetaItens.this);
                                        builder.setCancelable(false);
                                        builder.setTitle("ATENÇÃO!");
                                        builder.setIcon(R.drawable.exclamation);
                                        builder.setMessage("Confirma exclusão do item " + outItemCol.getText().toString() + "?\nEste processo não pode ser desfeito!!");
                                        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                try {
                                                    for (int i = 0; i < arrCodProdColeta.size(); i++) {
                                                        if (arrBitolaColeta.get(i).contains(outBitCol.getText().toString())) {
                                                            codigoProduto = arrCodProdColeta.get(i);
                                                            break;
                                                        }
                                                    }

                                                    arrItemColeta.remove(outItemCol.getText().toString());
                                                    arrQtdItemColeta.remove(outQtdItemCol.getText().toString());
                                                    arrCodProdColeta.remove(codigoProduto);
                                                    arrBitolaColeta.remove(outBitCol.getText().toString());
                                                    arrMarcaColeta.remove(outMarcaCol.getText().toString());
                                                    arrModeloColeta.remove(outModelCol.getText().toString());
                                                    arrSerieColeta.remove(outSerieCol.getText().toString());
                                                    arrDotColeta.remove(outDotCol.getText().toString());
                                                    arrMontadoColeta.remove(outMontado.getText().toString());
                                                    arrDesenhoColeta.remove(outDesenCol.getText().toString());
                                                    arrValorUnit.remove(outVlrUnit.getText().toString());
                                                    arrValorTotal.remove(outVlrTotIt.getText().toString());
                                                    arrVlrTabelaItem.remove(outTabelaItem);
                                                    arrPercDesc.remove(outPercDesc);
                                                    arrUrgenteColeta.remove(outUrgCol.getText().toString());
                                                    arrPorcComissBorr.remove(outPercComissBorr);
                                                    arrVlrComissBorr.remove(outVlrComissBorr);
                                                    arrObservItem.remove(outObsItem.getText().toString());
                                                    arrPercDescGrp.remove(outMaxPercDesc);
                                                    arrCAgua.remove(outCAgua);
                                                    arrCCamara.remove(outCCamara);
                                                    arrGarantia.remove(outGarantia);
                                                    arrFilialOri.remove(outFilialOri);
                                                    arrColetaOri.remove(outColetaOri);
                                                    arrItemOri.remove(outItemOri);

                                                    valorTotalColeta -= Double.parseDouble(outVlrUnit.getText().toString()) * Double.parseDouble(outQtdItemCol.getText().toString());
                                                    txtVlrTotal.setText("R$ " + df.format(valorTotalColeta));

                                                    ((LinearLayout) viewItens.getParent()).removeView(viewItens);
                                                    totalItens -= 1;

                                                    /* ORDENAÇÃO DOS ITENS */
                                                    for (int item = 0; item < arrItemColeta.size(); item++) {
                                                        arrItemColeta.set(item, Integer.toString(item + 1));
                                                    }

                                                    if (arrQtdItemColeta.size() == 0) {
                                                        valorTotalColeta = 0;
                                                        sequencialItens = 1;
                                                        totalItens = 0;
                                                        cmpItem.setText(Integer.toString(sequencialItens));
                                                        cmpQtdItem.setText("1");
                                                        arrItemColeta.clear();
                                                        arrQtdItemColeta.clear();
                                                        arrCodProdColeta.clear();
                                                        arrBitolaColeta.clear();
                                                        arrMarcaColeta.clear();
                                                        arrModeloColeta.clear();
                                                        arrSerieColeta.clear();
                                                        arrDotColeta.clear();
                                                        arrMontadoColeta.clear();
                                                        arrDesenhoColeta.clear();
                                                        arrValorUnit.clear();
                                                        arrVlrTabelaItem.clear();
                                                        arrPercDesc.clear();
                                                        arrUrgenteColeta.clear();
                                                        arrPorcComissBorr.clear();
                                                        arrVlrComissBorr.clear();
                                                        arrObservItem.clear();
                                                        arrPercDescGrp.clear();
                                                        arrCAgua.clear();
                                                        arrCCamara.clear();
                                                        arrGarantia.clear();
                                                        arrVlrBlqIt.clear();
                                                        arrBaseCalcIt.clear();
                                                        arrFilialOri.clear();
                                                        arrColetaOri.clear();
                                                        arrItemOri.clear();
                                                        txtVlrTotal.setText("R$ 0,00");
                                                        btnAvancar.setVisibility(View.GONE);
                                                        btnVisualizaItens.setVisibility(View.GONE);
                                                        cmpBitola.requestFocus();
                                                        cmpMarcaPneu.setEnabled(false);
                                                        cmpModeloPneu.setEnabled(false);
                                                        cmpSeriePneu.setEnabled(false);
                                                        cmpDotPneu.setEnabled(false);
                                                        cmpDesenhoPneu.setEnabled(false);
                                                        cmpValorSrv.setEnabled(false);
                                                        cmpObsItem.setEnabled(false);
                                                        chkUrgente.setEnabled(false);
                                                        txtVlrTotal.setEnabled(false);
                                                        tgBtnMontado.setEnabled(false);
                                                        chkcAgua.setEnabled(false);
                                                        chkcCamara.setEnabled(false);
                                                        chkGarantia.setEnabled(false);
                                                        chkcAgua.setChecked(false);
                                                        chkcCamara.setChecked(false);
                                                        chkGarantia.setChecked(false);
                                                    }

                                                    dialogItens.dismiss();

                                                    codigoProduto = "";
                                                    sequencialItens = arrItemColeta.size() + 1;
                                                    cmpBitola.requestFocus();
                                                    txtQtdItens.setText(String.valueOf(arrItemColeta.size()));
                                                    cmpItem.setText(String.valueOf(sequencialItens));
                                                    Toast.makeText(ColetaItens.this, "Item excluído com sucesso!", Toast.LENGTH_LONG).show();
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                    Toast.makeText(ColetaItens.this, "Erro ao excluir item " + outItemCol.getText().toString() + "\nTente novamente.", Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                                        builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                                cmpBitola.requestFocus();
                                            }
                                        });
                                        builder.show();
                                    }
                                });

                                /*
                                 * EDIÇÃO DO ITEM *
                                 **/
                                btnEditItemCol.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialogItens.dismiss();
                                        isEdicaoItem = true;

                                        btnAddItem.setVisibility(View.GONE);
                                        llEdit.setVisibility(View.VISIBLE);
                                        formBotoesColeta.setVisibility(View.GONE);

                                        cmpObsItem.setEnabled(true);
                                        txtVlrTotal.setEnabled(true);

                                        cmpDesenhoPneu.setEnabled(outCodPrd.equals("GENERICO") || outCodPrd.startsWith("OS") || outCodPrd.startsWith("UC"));

                                        cmpItem.setText(outItemCol.getText().toString());
                                        cmpQtdItem.setText(outQtdItemCol.getText().toString());
                                        codigoProduto = outCodPrd;
                                        descricaoProduto = outBitCol.getText().toString().trim();
                                        cmpBitola.setText(outBitCol.getText().toString());
                                        cmpMarcaPneu.setText(outMarcaCol.getText().toString());
                                        cmpModeloPneu.setText(outModelCol.getText().toString());
                                        cmpSeriePneu.setText(outSerieCol.getText().toString());
                                        cmpDotPneu.setText(outDotCol.getText().toString());
                                        tgBtnMontado.setChecked(outMontado.getText().toString().equals("Montado"));
                                        cmpDesenhoPneu.setText(outDesenCol.getText().toString());
                                        cmpValorSrv.setText(outVlrUnit.getText().toString());
                                        cmpObsItem.setText(outObsItem.getText().toString());
                                        chkUrgente.setChecked(outUrgCol.getText().toString().equals("Sim"));
                                        chkcAgua.setChecked(outCAgua.equals("Sim"));
                                        chkcCamara.setChecked(outCAgua.equals("Sim"));
                                        chkGarantia.setChecked(outCAgua.equals("Sim"));

                                        valorTabela = Double.parseDouble(outTabelaItem);
                                        porcentgDescontoGrp = Double.parseDouble(outMaxPercDesc);
                                        valorBloqueio = Double.parseDouble(outVlrBlq);
                                        baseCalculo = Double.parseDouble(outBaseCalculo);
                                        valorTotalItem = Double.parseDouble(outVlrTotIt.getText().toString());
                                        porcentgDesconto = Double.parseDouble(outPercDesc);

                                        if (GettersSetters.getDescExclusivo() > 0 && GettersSetters.getTipoColeta().contains("SE")) { //DESCONTO FIXO NO CADASTRO DO CLIENTE
                                            isTabela = true;
                                            totalDesconto = baseCalculo - valorTabela;
                                            porcentgDesconto = (valorTabela * 100) / baseCalculo;
                                            cmpValorSrv.setEnabled(false);
                                        } else if (!GettersSetters.getTabelaPrcCli().trim().equals("")) { //TABELA DE PREÇO DO CLIENTE
                                            double tmpValorTabela;
                                            tmpValorTabela = db.selectTabPrcCli(GettersSetters.getTabelaPrcCli().trim(), codigoProduto.trim());
                                            if (tmpValorTabela != 0) {
                                                valorTabela = tmpValorTabela;
                                                isTabela = true;
                                                totalDesconto = baseCalculo - valorTabela;
                                                porcentgDesconto = (valorTabela * 100) / baseCalculo;
                                                cmpValorSrv.setEnabled(false);
                                            } else {
                                                isTabela = false;
                                                totalDesconto = baseCalculo - valorTabela;
                                                porcentgDesconto = (valorTabela * 100) / baseCalculo;
                                                cmpValorSrv.setEnabled(true);
                                            }
                                        } else {
                                            cmpValorSrv.setEnabled(true);
                                            isTabela = false;
                                            totalDesconto = baseCalculo - valorTabela;
                                            porcentgDesconto = (valorTabela * 100) / baseCalculo;
                                        }

                                        if (GettersSetters.getTipoColeta().contains("SE") || GettersSetters.getTipoColeta().contains("PC")) {
                                            cmpMarcaPneu.setEnabled(true);
                                            cmpModeloPneu.setEnabled(true);
                                            cmpSeriePneu.setEnabled(true);
                                            cmpDotPneu.setEnabled(true);
                                            chkUrgente.setEnabled(true);
                                            tgBtnMontado.setEnabled(true);
                                            chkcAgua.setEnabled(true);
                                            chkcCamara.setEnabled(true);
                                            chkGarantia.setEnabled(true);
                                        }

                                        if (GettersSetters.getCategoriaCli().equals("01")) {
                                            cmpValorSrv.setEnabled((GettersSetters.getFilial().startsWith("1101") || GettersSetters.getFilial().startsWith("1201")) && GettersSetters.isIsOrcamento());
                                        } else if (!GettersSetters.getDescrTipoColeta().contains("RECL") && !isTabela) {
                                            cmpValorSrv.setEnabled(true);
                                        }

                                        if (GettersSetters.isClienteGrp()) {
                                            cmpValorSrv.setEnabled(false);
                                        }

                                        dialog.cancel();

                                        /*
                                         * CONFIRMAÇÃO DE EDIÇÃO *
                                         **/
                                        btnConfEdit.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                if (isValidCampo()) {
                                                    if ((Double.parseDouble(cmpValorSrv.getText().toString()) == 0) && !GettersSetters.getTipoUsuario().equals("01")) {
                                                        AlertDialog.Builder builder = new AlertDialog.Builder(ColetaItens.this);
                                                        builder.setTitle("[EDIÇÃO] Valor ZERO");
                                                        builder.setIcon(R.drawable.money);
                                                        builder.setCancelable(false);
                                                        builder.setMessage("Não é permitido valor do item " + sequencialItens + " igual a ZERO!");
                                                        builder.setPositiveButton("Fechar", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                cmpValorSrv.getText().clear();
                                                            }
                                                        });
                                                        AlertDialog dialog = builder.create();
                                                        dialog.show();
                                                    } else {
                                                        isEdicaoItem = false;

                                                        if (GettersSetters.getCategoriaCli().equals("01")) {
                                                            valorBloqueio = 0;
                                                        }

                                                        porcentgDesconto = (((baseCalculo - (Double.parseDouble(cmpValorSrv.getText().toString()))) / baseCalculo) * 100);

                                                        if (Double.parseDouble(cmpValorSrv.getText().toString()) < (int) valorBloqueio &&
                                                                !GettersSetters.getDescrTipoColeta().contains("RECL") &&
                                                                !GettersSetters.getCategoriaCli().equals("01")) {
                                                            AlertDialog.Builder builder = new AlertDialog.Builder(ColetaItens.this);
                                                            builder.setTitle("[EDIÇÃO] Valor abaixo do mínimo permitido");
                                                            builder.setIcon(R.drawable.cash);
                                                            builder.setCancelable(false);
                                                            builder.setMessage("Valor informado (R$" + cmpValorSrv.getText() + ") está abaixo do mínimo permitido. Digite outro valor.");
                                                            builder.setPositiveButton("Alterar", null);
                                                            AlertDialog dialog = builder.create();
                                                            dialog.show();
                                                        } else {
                                                            totalItens -= 1;
                                                            valorTotalColeta -= valorTotalItem;

                                                            for (int i = 0; i < arrCodProdColeta.size(); i++) {
                                                                if (arrBitolaColeta.get(i).contains(cmpBitola.getText().toString().replace("*** ", ""))) {
                                                                    codigoProduto = arrCodProdColeta.get(i);
                                                                    break;
                                                                }
                                                            }

                                                            if (codigoProduto.equals("GENERICO")) {
                                                                descricaoProduto = cmpBitola.getText().toString();
                                                            }

                                                            for (int i = 0; i < arrItemColeta.size(); i++) {
                                                                if (arrItemColeta.get(i).equals((cmpItem.getText().toString()))) {
                                                                    arrQtdItemColeta.set(i, cmpQtdItem.getText().toString());
                                                                    arrCodProdColeta.set(i, codigoProduto);
                                                                    arrBitolaColeta.set(i, descricaoProduto);
                                                                    arrMarcaColeta.set(i, cmpMarcaPneu.getText().toString());
                                                                    arrModeloColeta.set(i, cmpModeloPneu.getText().toString());
                                                                    arrSerieColeta.set(i, cmpSeriePneu.getText().toString());
                                                                    arrDotColeta.set(i, cmpDotPneu.getText().toString());
                                                                    if (chkcAgua.isChecked()) {
                                                                        arrCAgua.set(i, "Sim");
                                                                    } else {
                                                                        arrCAgua.set(i, "Não");
                                                                    }
                                                                    if (chkcCamara.isChecked()) {
                                                                        arrCCamara.set(i, "Sim");
                                                                    } else {
                                                                        arrCCamara.set(i, "Não");
                                                                    }
                                                                    if (chkGarantia.isChecked()) {
                                                                        arrGarantia.set(i, "Sim");
                                                                    } else {
                                                                        arrCCamara.set(i, "Não");
                                                                    }
                                                                    if (chkUrgente.isChecked()) {
                                                                        arrUrgenteColeta.set(i, "Sim");
                                                                    } else {
                                                                        arrUrgenteColeta.set(i, "Não");
                                                                    }
                                                                    if (tgBtnMontado.isChecked()) {
                                                                        arrMontadoColeta.set(i, "Montado");
                                                                    } else {
                                                                        arrMontadoColeta.set(i, "Não montado");
                                                                    }
                                                                    arrDesenhoColeta.set(i, cmpDesenhoPneu.getText().toString());
                                                                    arrValorUnit.set(i, cmpValorSrv.getText().toString());
                                                                    arrValorTotal.set(i, (String.valueOf(Double.parseDouble(cmpQtdItem.getText().toString()) * Double.parseDouble(cmpValorSrv.getText().toString()))));
                                                                    arrObservItem.set(i, cmpObsItem.getText().toString());

                                                                    arrVlrTabelaItem.set(i, String.valueOf(valorTabela));
                                                                    arrPercDesc.set(i, String.valueOf(porcentgDesconto));
                                                                    arrPercDescGrp.set(i, String.valueOf(porcentgDescontoGrp));
                                                                    arrVlrBlqIt.set(i, String.valueOf(valorBloqueio));
                                                                    arrBaseCalcIt.set(i, String.valueOf(baseCalculo));

                                                                    valorTotalItem = Double.parseDouble(cmpQtdItem.getText().toString()) * Double.parseDouble(cmpValorSrv.getText().toString());

                                                                    /*
                                                                     * EDIÇÃO COMISSÃO DE BORRACHEIRO *
                                                                     **/
                                                                    if (GettersSetters.isCheckComissBorr()) {
                                                                        if (valorTabela > 0) {
                                                                            dialogComissBorr(true, i, outPercComissBorr, outVlrComissBorr);
                                                                        } else {
                                                                            arrPorcComissBorr.add("0");
                                                                            arrVlrComissBorr.add("0");
                                                                        }
                                                                    } else {
                                                                        arrPorcComissBorr.add("0");
                                                                        arrVlrComissBorr.add("0");
                                                                    }
                                                                    break;
                                                                }
                                                            }
                                                            outItemCol.setText(cmpItem.getText().toString());
                                                            outQtdItemCol.setText(cmpQtdItem.getText().toString());
                                                            outBitCol.setText(descricaoProduto);
                                                            outMarcaCol.setText(cmpMarcaPneu.getText().toString());
                                                            outModelCol.setText(cmpModeloPneu.getText().toString());
                                                            outSerieCol.setText(cmpSeriePneu.getText().toString());
                                                            outDotCol.setText(cmpDotPneu.getText().toString());
                                                            if (tgBtnMontado.isChecked()) {
                                                                outMontado.setText("Montado");
                                                            } else {
                                                                outMontado.setText("Não montado");
                                                            }
                                                            outDesenCol.setText(cmpDesenhoPneu.getText().toString());
                                                            outVlrUnit.setText(cmpValorSrv.getText().toString());
                                                            outVlrTotIt.setText(String.valueOf(Double.parseDouble(cmpQtdItem.getText().toString()) * Double.parseDouble(cmpValorSrv.getText().toString())));
                                                            outObsItem.setText(cmpObsItem.getText().toString());
                                                            if (chkUrgente.isChecked()) {
                                                                outUrgCol.setText("Sim");
                                                            } else {
                                                                outUrgCol.setText("Não");
                                                            }

                                                            /* ATUALIZA TOTAIS **/
                                                            totalItens += 1;
                                                            txtQtdItens.setText(Integer.toString(arrItemColeta.size()));

                                                            valorTotalColeta += Double.parseDouble(cmpValorSrv.getText().toString()) * Double.parseDouble(cmpQtdItem.getText().toString());
                                                            txtVlrTotal.setText("R$ " + df.format(valorTotalColeta));

                                                            Toast.makeText(ColetaItens.this, "Item " + cmpItem.getText().toString() + " editado com sucesso!", Toast.LENGTH_LONG).show();

                                                            /* RESETA CAMPOS **/
                                                            cmpItem.setText(Integer.toString(arrItemColeta.size() + 1));
                                                            cmpQtdItem.setText("1");
                                                            cmpBitola.getText().clear();
                                                            cmpMarcaPneu.getText().clear();
                                                            cmpModeloPneu.getText().clear();
                                                            cmpSeriePneu.getText().clear();
                                                            cmpDotPneu.getText().clear();
                                                            cmpDesenhoPneu.getText().clear();
                                                            cmpValorSrv.getText().clear();
                                                            cmpValorSrv.setHint("Valor R$");
                                                            cmpObsItem.getText().clear();
                                                            tgBtnMontado.setChecked(false);
                                                            chkUrgente.setChecked(false);
                                                            chkcAgua.setChecked(false);
                                                            chkcCamara.setChecked(false);
                                                            chkGarantia.setChecked(false);
                                                            cmpMarcaPneu.setEnabled(false);
                                                            cmpModeloPneu.setEnabled(false);
                                                            cmpSeriePneu.setEnabled(false);
                                                            cmpDotPneu.setEnabled(false);
                                                            cmpDesenhoPneu.setEnabled(false);
                                                            cmpValorSrv.setEnabled(false);
                                                            cmpObsItem.setEnabled(false);
                                                            chkUrgente.setEnabled(false);
                                                            txtVlrTotal.setEnabled(false);
                                                            tgBtnMontado.setEnabled(false);
                                                            chkcAgua.setEnabled(false);
                                                            chkcCamara.setEnabled(false);
                                                            chkGarantia.setEnabled(false);
                                                            valorComissaoBorracheiro = 0;
                                                            porcentgDescontoGrp = 0;
                                                            tempPercentualComissaoBorracheiro = 0;
                                                            descricaoProduto = "";
                                                            codigoProduto = "";
                                                            cmpBitola.requestFocus();
                                                            btnAddItem.setVisibility(View.GONE);
                                                            llEdit.setVisibility(View.GONE);
                                                            formBotoesColeta.setVisibility(View.VISIBLE);
                                                            if (!isPneuCargaPasseio) {
                                                                btnBuscaProduto.setVisibility(View.VISIBLE);
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        });

                                        /* CANCELAMENTO DE EDIÇÃO **/
                                        btnCancelEdit.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                isEdicaoItem = false;

                                                cmpItem.setText(Integer.toString(sequencialItens));
                                                cmpQtdItem.setText("1");
                                                cmpBitola.getText().clear();
                                                cmpMarcaPneu.getText().clear();
                                                cmpModeloPneu.getText().clear();
                                                cmpSeriePneu.getText().clear();
                                                cmpDotPneu.getText().clear();
                                                cmpDesenhoPneu.getText().clear();
                                                cmpValorSrv.getText().clear();
                                                cmpValorSrv.setHint("Valor R$");
                                                cmpObsItem.getText().clear();
                                                tgBtnMontado.setChecked(false);
                                                chkcAgua.setChecked(false);
                                                chkcCamara.setChecked(false);
                                                chkGarantia.setChecked(false);
                                                chkUrgente.setChecked(false);
                                                cmpMarcaPneu.setEnabled(false);
                                                cmpModeloPneu.setEnabled(false);
                                                cmpSeriePneu.setEnabled(false);
                                                cmpDotPneu.setEnabled(false);
                                                cmpDesenhoPneu.setEnabled(false);
                                                cmpValorSrv.setEnabled(false);
                                                cmpObsItem.setEnabled(false);
                                                chkUrgente.setEnabled(false);
                                                txtVlrTotal.setEnabled(false);
                                                chkcAgua.setEnabled(false);
                                                chkcCamara.setEnabled(false);
                                                chkGarantia.setEnabled(false);
                                                valorComissaoBorracheiro = 0;
                                                porcentgDescontoGrp = 0;
                                                tempPercentualComissaoBorracheiro = 0;
                                                descricaoProduto = "";
                                                codigoProduto = "";
                                                cmpBitola.requestFocus();
                                                llEdit.setVisibility(View.GONE);
                                                btnAddItem.setVisibility(View.GONE);
                                                formBotoesColeta.setVisibility(View.VISIBLE);
                                                if (!isPneuCargaPasseio) {
                                                    btnBuscaProduto.setVisibility(View.VISIBLE);
                                                }
                                                Toast.makeText(ColetaItens.this, "Edição cancelada.", Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }
                                });

                                /* COPIAR ITEM **/
                                btnCopiarItem.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        btnCancelaCopia.setVisibility(View.VISIBLE);
                                        cmpBitola.setText(outBitCol.getText().toString());

                                        cmpValorSrv.setText(outVlrTotIt.getText().toString());
                                        codigoProduto = outCodPrd;
                                        valorTabela = Double.parseDouble(outTabelaItem);
                                        porcentgDescontoGrp = Double.parseDouble(outMaxPercDesc);
                                        cmpDesenhoPneu.setText(outDesenCol.getText().toString());
                                        descricaoProduto = cmpBitola.getText().toString();

                                        cmpMarcaPneu.setText("");
                                        cmpModeloPneu.setText("");
                                        cmpSeriePneu.setText("");
                                        cmpDotPneu.setText("");

                                        valorBloqueio = Double.parseDouble(outVlrBlq);
                                        baseCalculo = Double.parseDouble(outBaseCalculo);
                                        valorTotalItem = Double.parseDouble(outVlrTotIt.getText().toString());
                                        porcentgDesconto = Double.parseDouble(outPercDesc);

                                        if (GettersSetters.getDescExclusivo() > 0 && GettersSetters.getTipoColeta().contains("SE")) { //DESCONTO FIXO NO CADASTRO DO CLIENTE
                                            isTabela = true;
                                            totalDesconto = baseCalculo - valorTabela;
                                            porcentgDesconto = (valorTabela * 100) / baseCalculo;
                                            cmpValorSrv.setEnabled(false);
                                        } else if (!GettersSetters.getTabelaPrcCli().trim().equals("")) { //TABELA DE PREÇO DO CLIENTE
                                            double tmpValorTabela;
                                            tmpValorTabela = db.selectTabPrcCli(GettersSetters.getTabelaPrcCli().trim(), codigoProduto.trim());
                                            if (tmpValorTabela != 0) {
                                                valorTabela = tmpValorTabela;
                                                isTabela = true;
                                                totalDesconto = baseCalculo - valorTabela;
                                                porcentgDesconto = (valorTabela * 100) / baseCalculo;
                                                cmpValorSrv.setEnabled(false);
                                            } else {
                                                isTabela = false;
                                                totalDesconto = baseCalculo - valorTabela;
                                                porcentgDesconto = (valorTabela * 100) / baseCalculo;
                                                cmpValorSrv.setEnabled(true);
                                            }
                                        } else {
                                            cmpValorSrv.setEnabled(true);
                                            isTabela = false;
                                            totalDesconto = baseCalculo - valorTabela;
                                            porcentgDesconto = (valorTabela * 100) / baseCalculo;
                                        }

                                        btnBuscaProduto.setVisibility(View.GONE);
                                        if (!isPneuCargaPasseio) {
                                            cmpBitola.setEnabled(false);
                                            cmpDesenhoPneu.setEnabled(false);
                                        }
                                        chkUrgente.setChecked(false);
                                        tgBtnMontado.setChecked(false);

                                        btnAddItem.setVisibility(View.VISIBLE);

                                        if (GettersSetters.getTipoColeta().contains("SE") || GettersSetters.getTipoColeta().contains("PC")) {
                                            cmpMarcaPneu.setEnabled(true);
                                            cmpModeloPneu.setEnabled(true);
                                            cmpSeriePneu.setEnabled(true);
                                            cmpDotPneu.setEnabled(true);
                                            chkUrgente.setEnabled(true);
                                            tgBtnMontado.setEnabled(true);
                                            chkcAgua.setEnabled(true);
                                            chkcCamara.setEnabled(true);
                                            chkGarantia.setEnabled(true);
                                        }

                                        dialogItens.dismiss();

                                        cmpValorSrv.setEnabled(!GettersSetters.getCategoriaCli().equals("01") && !GettersSetters.getDescrTipoColeta().contains("RECL") && !isTabela);
                                        cmpObsItem.setEnabled(true);
                                        cmpMarcaPneu.requestFocus();
                                        exibeTeclado();

                                        /** CANCELAMENTO DA CÓPIA **/
                                        btnCancelaCopia.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                AlertDialog.Builder builder1 = new AlertDialog.Builder(ColetaItens.this);
                                                builder1.setCancelable(false);
                                                builder1.setTitle("Cancelamento de Cópia");
                                                builder1.setTitle("Confirma o cancelamento da Cópia de Itens?");
                                                builder1.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        resetaCampos();
                                                    }
                                                });
                                                builder1.setNegativeButton("Não", null);
                                                builder1.show();
                                            }
                                        });
                                        dialog.dismiss();
                                    }
                                });
                                dialog.show();
                            }
                        });

                        visualItem.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(ColetaItens.this);
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
                                final LinearLayout llComissBorr = dialogView.findViewById(R.id.llComissBorr);
                                final EditText vlrComB = dialogView.findViewById(R.id.cmpVlrComissBorr);
                                final TextView txtComissBorr = dialogView.findViewById(R.id.txtComissBorr);
                                final CheckBox cAgua = dialogView.findViewById(R.id.chkcAgua);
                                final CheckBox cCamara = dialogView.findViewById(R.id.chkcCamara);
                                final CheckBox garantia = dialogView.findViewById(R.id.chkGarantia);
                                final EditText status = dialogView.findViewById(R.id.cmpStatusIt);
                                final TextView txtStatus = dialogView.findViewById(R.id.txtStatusIt);

                                status.setVisibility(View.GONE);
                                txtStatus.setVisibility(View.GONE);

                                txtIdentif.setVisibility(View.GONE);
                                identificador.setVisibility(View.GONE);

                                item.setText(outItemCol.getText().toString());
                                quantidade.setText(outQtdItemCol.getText().toString());
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
                } catch (Exception exp) {
                    exp.printStackTrace();
                }
            }
        });

        btnBuscaProduto.setOnClickListener(new View.OnClickListener() {
            @SuppressWarnings("unchecked")
            @Override
            public void onClick(View v) {
                cmpValorSrv.getText().clear();
                valorTabela = 0;
                codigoProduto = "";
                descricaoProduto = "";

                descricaoProduto = cmpBitola.getText().toString().trim().
                        replace("/", " ").
                        replace("-", " ").
                        replace(".", " ").
                        replace(" ", "").
                        replace("RECAPE", "RECAP").
                        replace("RECAUCHE", "RECAUCH");

                if (descricaoProduto.length() < 4 && (GettersSetters.getTipoColeta().contains("SE") || GettersSetters.getTipoColeta().contains("PC"))) {
                    cmpBitola.setError(getString(R.string.erro_tamanho_string));
                    cmpBitola.requestFocus();
                } else {
                    if (llEdit.getVisibility() == View.GONE) { //validação para quando for edição dos itens não limpar os campos
                        cmpMarcaPneu.getText().clear();
                        cmpModeloPneu.getText().clear();
                        cmpSeriePneu.getText().clear();
                        cmpDotPneu.getText().clear();
                        cmpDesenhoPneu.getText().clear();
                        cmpValorSrv.getText().clear();
                        cmpObsItem.getText().clear();
                        chkUrgente.setChecked(false);
                        tgBtnMontado.setChecked(false);

                        cmpMarcaPneu.setError(null);
                        cmpModeloPneu.setError(null);
                        cmpSeriePneu.setError(null);
                        cmpDotPneu.setError(null);
                        cmpDesenhoPneu.setError(null);
                        cmpValorSrv.setError(null);
                        cmpObsItem.setError(null);
                    }

                    buscaItens = new BuscaItens();
                    buscaItens.execute();
                }
            }
        });

        chkUrgente.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    chkUrgente.setTextColor(getResources().getColor(R.color.red));
                } else {
                    chkUrgente.setTextColor(getResources().getColor(R.color.preto));
                }
            }
        });

        tgBtnMontado.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    tgBtnMontado.setTextColor(getResources().getColor(R.color.red));
                } else {
                    tgBtnMontado.setTextColor(getResources().getColor(R.color.preto));
                }
            }
        });

        btnAvancar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builderQtdItens = new AlertDialog.Builder(ColetaItens.this);
                builderQtdItens.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.tires, null));
                builderQtdItens.setTitle((totalItens > 1 ? "Itens" : "Item") + " Coleta " + GettersSetters.getNumeroColeta());
                builderQtdItens.setMessage("Nesta Coleta " + (totalItens > 1 ? "foram adicionados" : "foi adicionado") + " [" + totalItens + "]" + (totalItens > 1 ? " itens" : " item") + ".\nConfirma?");
                builderQtdItens.setPositiveButton("SIM", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int item) {
                        GettersSetters.setValorColeta(Double.toString(valorTotalColeta));
                        GettersSetters.setQtdItensColeta(Integer.toString(totalItens)); //TOTAL DE ITENS

                        GettersSetters.setArrItemColeta(arrItemColeta);
                        GettersSetters.setArrQtdItemColeta(arrQtdItemColeta); //QNT ITEM A ITEM
                        GettersSetters.setArrBitolaColeta(arrBitolaColeta);
                        GettersSetters.setArrCodProdColeta(arrCodProdColeta);
                        GettersSetters.setArrMarcaColeta(arrMarcaColeta);
                        GettersSetters.setArrModeloColeta(arrModeloColeta);
                        GettersSetters.setArrSerieColeta(arrSerieColeta);
                        GettersSetters.setArrDotColeta(arrDotColeta);
                        GettersSetters.setArrMontadoColeta(arrMontadoColeta);
                        GettersSetters.setArrDesenhoColeta(arrDesenhoColeta);
                        GettersSetters.setArrValorUnit(arrValorUnit);
                        GettersSetters.setArrValorTotal(arrValorTotal);
                        GettersSetters.setArrUrgenteColeta(arrUrgenteColeta);
                        GettersSetters.setArrPorcComisBorr(arrPorcComissBorr);
                        GettersSetters.setArrValrComisBorr(arrVlrComissBorr);
                        GettersSetters.setArrObsItens(arrObservItem);
                        GettersSetters.setArrCAgua(arrCAgua);
                        GettersSetters.setArrCCamara(arrCCamara);
                        GettersSetters.setArrGarantia(arrGarantia);

                        GettersSetters.setArrFilialOri(arrFilialOri);
                        GettersSetters.setArrColetaOri(arrColetaOri);
                        GettersSetters.setArrItemOri(arrItemOri);

                        GettersSetters.setInfoAdicionais(GettersSetters.getInfoAdicionais() + (msgColetasImportadas.length() > 0 ? " Coletas refat: " + msgColetasImportadas : ""));

                        if (GettersSetters.getCategoriaCli().equals("01")) {
                            if (GettersSetters.getDescrTipoColeta().equals("RECLAMACAO")) {
                                isReclamacao = true;
                                GettersSetters.setCondPgto("28");
                                GettersSetters.setCodCondPgto("D04");
                                GettersSetters.setDescrFormaPgto("BOLETO BANCARIO");

                                AlertDialog.Builder builder = new AlertDialog.Builder(ColetaItens.this);
                                builder.setCancelable(false);
                                builder.setTitle("Condição de pagamento RECLAMAÇÃO");
                                builder.setMessage("Para o tipo da Coleta RECLAMAÇÃO foi fixada a condição de pagamento " + GettersSetters.getCondPgto() + " dias.\nEste tipo de coleta NÃO GERA FINANCEIRO!");
                                builder.setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent it = new Intent(ColetaItens.this, ColetaConclusao.class);
                                        startActivity(it);
                                    }
                                });
                                AlertDialog dialog = builder.create();
                                dialog.show();

                                Intent it = new Intent(ColetaItens.this, ColetaConclusao.class);
                                startActivity(it);
                            } else {
                                GettersSetters.setCondPgto("28");
                                GettersSetters.setCodCondPgto("D56");
                                GettersSetters.setDescrFormaPgto("DEPOSITO BANCARIO");

                                Intent it = new Intent(ColetaItens.this, ColetaConclusao.class);
                                startActivity(it);
                            }
                        } else if (GettersSetters.getDescrTipoColeta().equals("RECLAMACAO")) {
                            isReclamacao = true;
                            GettersSetters.setCondPgto("28");
                            GettersSetters.setCodCondPgto("D04");
                            GettersSetters.setDescrFormaPgto("BOLETO BANCARIO");

                            AlertDialog.Builder builder = new AlertDialog.Builder(ColetaItens.this);
                            builder.setCancelable(false);
                            builder.setTitle("Condição de pagamento RECLAMAÇÃO");
                            builder.setMessage("Para o tipo da Coleta RECLAMAÇÃO foi fixada a condição de pagamento " + GettersSetters.getCondPgto() + " dias.\nEste tipo de coleta NÃO GERA FINANCEIRO!");
                            builder.setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent it = new Intent(ColetaItens.this, ColetaConclusao.class);
                                    startActivity(it);
                                }
                            });
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        } else if (GettersSetters.getCodCondPgto().trim().equals("")) {
                            cmpBitola.getText().clear();
                            cmpMarcaPneu.getText().clear();
                            cmpModeloPneu.getText().clear();
                            cmpSeriePneu.getText().clear();
                            cmpDotPneu.getText().clear();
                            cmpDesenhoPneu.getText().clear();
                            cmpValorSrv.getText().clear();
                            valorComissaoBorracheiro = 0;
                            porcentgDescontoGrp = 0;
                            tempPercentualComissaoBorracheiro = 0;
                            descricaoProduto = "";
                            codigoProduto = "";
                            cmpObsItem.getText().clear();
                            chkUrgente.setChecked(false);
                            tgBtnMontado.setChecked(false);

                            cmpMarcaPneu.setEnabled(false);
                            cmpModeloPneu.setEnabled(false);
                            cmpSeriePneu.setEnabled(false);
                            cmpDotPneu.setEnabled(false);
                            cmpDesenhoPneu.setEnabled(false);
                            cmpValorSrv.setEnabled(false);
                            cmpObsItem.setEnabled(false);
                            txtVlrTotal.setEnabled(false);
                            chkUrgente.setEnabled(false);
                            tgBtnMontado.setEnabled(false);
                            chkcAgua.setEnabled(false);
                            chkcCamara.setEnabled(false);
                            chkGarantia.setEnabled(false);

                            Intent it = new Intent(ColetaItens.this, ColetaPagamento.class);
                            startActivity(it);
                        } else {
                            if (!GettersSetters.getCondPgto().trim().equals("")) {
                                arrcodCondPgto.clear();
                                arrdescCondPgto.clear();
                                arrformaCondPgto.clear();
                                arrdescFormaCondPgto.clear();
                                arrCondPgto.clear();

                                Cursor cursor = db.buscaCondPgto("", GettersSetters.getCondPgto().trim());
                                if (cursor.getCount() > 0) {
                                    arrcodCondPgto.addAll(db.arrcodCondPgto);
                                    arrdescFormaCondPgto.addAll(db.arrdescFormaCondPgto);
                                    arrdescCondPgto.addAll(db.arrdescCondPgto);
                                    arrCondPgto.addAll(db.arrCondPgto);

                                    if (arrcodCondPgto.size() > 0) {
                                        for (int i = 0; i < arrcodCondPgto.size(); i++) {
                                            if (arrcodCondPgto.get(i).equals(GettersSetters.getCondPgto())) {
                                                codigoCondicaoPagamento = arrcodCondPgto.get(i);
                                                condicaoPagamento = arrCondPgto.get(i);
                                                descrFormaPagaento = arrdescFormaCondPgto.get(i);
                                                descrCondicaoPagamento = arrdescCondPgto.get(i);
                                                break;
                                            }
                                        }
                                    }
                                }

                                if (!codigoCondicaoPagamento.trim().equals("")) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(ColetaItens.this);
                                    builder.setCancelable(false);
                                    builder.setTitle("Há uma condição de pagamento já vinculada ao Cliente");
                                    builder.setMessage("Deseja utilizar a mesma?\n" + descrCondicaoPagamento + " - " + descrFormaPagaento); // + "\nVencimento(s):\n" + mensagemDialogCondPgto);
                                    builder.setPositiveButton("Confirma", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (GettersSetters.getCategoriaCli().trim().equals("01")) {
                                                GettersSetters.setCondPgto("28");
                                                GettersSetters.setCodCondPgto("D56");
                                                GettersSetters.setDescrFormaPgto("DEPOSITO BANCARIO");
                                            } else {
                                                GettersSetters.setCondPgto(condicaoPagamento);
                                                GettersSetters.setDescrFormaPgto(descrFormaPagaento);
                                                GettersSetters.setDescrCondPgto(descrCondicaoPagamento);
                                            }

                                            cmpBitola.getText().clear();
                                            cmpMarcaPneu.getText().clear();
                                            cmpModeloPneu.getText().clear();
                                            cmpSeriePneu.getText().clear();
                                            cmpDotPneu.getText().clear();
                                            cmpDesenhoPneu.getText().clear();
                                            cmpValorSrv.getText().clear();
                                            valorTabela = 0;
                                            cmpObsItem.getText().clear();
                                            chkUrgente.setChecked(false);
                                            tgBtnMontado.setChecked(false);

                                            cmpMarcaPneu.setEnabled(false);
                                            cmpModeloPneu.setEnabled(false);
                                            cmpSeriePneu.setEnabled(false);
                                            cmpDotPneu.setEnabled(false);
                                            cmpDesenhoPneu.setEnabled(false);
                                            cmpValorSrv.setEnabled(false);
                                            cmpObsItem.setEnabled(false);
                                            chkUrgente.setEnabled(false);
                                            txtVlrTotal.setEnabled(false);
                                            tgBtnMontado.setEnabled(false);
                                            chkcAgua.setEnabled(false);
                                            chkcCamara.setEnabled(false);
                                            chkGarantia.setEnabled(false);

                                            Intent it = new Intent(ColetaItens.this, ColetaConclusao.class);
                                            startActivity(it);
                                        }
                                    });
                                    builder.setNegativeButton("Alterar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            GettersSetters.setCondPgto("");
                                            GettersSetters.setCondPgto("");
                                            GettersSetters.setCodCondPgto("");
                                            GettersSetters.setDescrFormaPgto("");

                                            cmpBitola.getText().clear();
                                            cmpMarcaPneu.getText().clear();
                                            cmpModeloPneu.getText().clear();
                                            cmpSeriePneu.getText().clear();
                                            cmpDotPneu.getText().clear();
                                            cmpDesenhoPneu.getText().clear();
                                            cmpValorSrv.getText().clear();
                                            valorComissaoBorracheiro = 0;
                                            porcentgDescontoGrp = 0;
                                            tempPercentualComissaoBorracheiro = 0;
                                            descricaoProduto = "";
                                            codigoProduto = "";
                                            cmpObsItem.getText().clear();
                                            chkUrgente.setChecked(false);
                                            tgBtnMontado.setChecked(false);

                                            cmpMarcaPneu.setEnabled(false);
                                            cmpModeloPneu.setEnabled(false);
                                            cmpSeriePneu.setEnabled(false);
                                            cmpDotPneu.setEnabled(false);
                                            cmpDesenhoPneu.setEnabled(false);
                                            cmpValorSrv.setEnabled(false);
                                            cmpObsItem.setEnabled(false);
                                            chkUrgente.setEnabled(false);
                                            txtVlrTotal.setEnabled(false);
                                            tgBtnMontado.setEnabled(false);
                                            chkcAgua.setEnabled(false);
                                            chkcCamara.setEnabled(false);
                                            chkGarantia.setEnabled(false);

                                            Intent it = new Intent(ColetaItens.this, ColetaPagamento.class);
                                            startActivity(it);
                                        }
                                    });
                                    AlertDialog dialog = builder.create();
                                    dialog.show();
                                } else {
                                    Intent it = new Intent(ColetaItens.this, ColetaPagamento.class);
                                    startActivity(it);
                                }
                            } else {
                                Intent it = new Intent(ColetaItens.this, ColetaPagamento.class);
                                startActivity(it);
                            }
                        }
                    }
                });
                builderQtdItens.setNegativeButton("NÃO", null);
                builderQtdItens.show();
            }
        });

        btnImportarItens.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (totalItens == 99) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ColetaItens.this);
                    builder.setCancelable(false);
                    builder.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.atencao, null));
                    builder.setTitle("Limite de itens atingido!");
                    builder.setMessage("Limite de 99 (noventa e nove) itens atingido!\n" +
                            "Finalize esta Coleta!");
                    builder.setPositiveButton("Fechar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            btnAddItem.setVisibility(View.GONE);
                            btnBuscaProduto.setVisibility(View.GONE);
                        }
                    });
                    builder.show();
                } else {
                    ArrayList<String> arrFiliais = new ArrayList<>();

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

                    AlertDialog.Builder builder = new AlertDialog.Builder(ColetaItens.this);
                    builder.setTitle("Importação de Itens");
                    builder.setMessage("Digite o número completo da Coleta (inclusive os zeros à esquerda) e selecione a Filial:");

                    LinearLayout linearLayout = new LinearLayout(ColetaItens.this);
                    linearLayout.setOrientation(LinearLayout.VERTICAL);

                    EditText editTextNumeroColeta = new EditText(ColetaItens.this);
                    editTextNumeroColeta.setInputType(InputType.TYPE_CLASS_NUMBER);
                    editTextNumeroColeta.setHint("Número da Coleta");

                    Spinner spinnerFilial = new Spinner(ColetaItens.this);
                    ArrayAdapter<String> adapterFilial = new ArrayAdapter<>(ColetaItens.this, android.R.layout.simple_spinner_item, arrFiliais);
                    adapterFilial.setDropDownViewResource(R.layout.text_view_item_high);
                    spinnerFilial.setAdapter(adapterFilial);

                    linearLayout.addView(editTextNumeroColeta);
                    linearLayout.addView(spinnerFilial);

                    builder.setView(linearLayout);
                    builder.setPositiveButton("Importar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            boolean coletaJaImportada = false;
                            numeroColetaImportacao = editTextNumeroColeta.getText().toString();
                            filialImportar = spinnerFilial.getSelectedItem().toString().split("/");

                            for (int i = 0; i < arrColetasImportadas.size(); i++) {
                                if (numeroColetaImportacao.trim().equals(arrColetasImportadas.get(i))) {
                                    coletaJaImportada = true;
                                    break;
                                }
                            }

                            if (!coletaJaImportada) {
                                if (!numeroColetaImportacao.equals("")) {
                                    buscaItensImportacao = new BuscaItensImportacao();
                                    buscaItensImportacao.execute(filialImportar[1].trim(), numeroColetaImportacao);
                                } else {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(ColetaItens.this);
                                    builder.setTitle("Número em branco!");
                                    builder.setIcon(R.drawable.password);
                                    builder.setCancelable(true);
                                    builder.setMessage("Preencher o número da coleta!");
                                    AlertDialog dialogPswd = builder.create();
                                    dialogPswd.show();
                                }
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(ColetaItens.this);
                                builder.setTitle("Coleta já importada!");
                                builder.setIcon(R.drawable.atencao);
                                builder.setCancelable(true);
                                builder.setMessage("Coleta " + numeroColetaImportacao + " já importada!\n" +
                                        "Busque outra coleta!");
                                AlertDialog dialogPswd = builder.create();
                                dialogPswd.show();
                            }
                        }
                    });
                    builder.show();
                }
            }
        });

        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(ColetaItens.this);
                builder.setCancelable(true);
                builder.setMessage("Deseja cancelar o processo? \nTodas as informações serão perdidas!");
                builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishAffinity();
                        Intent it = new Intent(ColetaItens.this, Home.class);
                        startActivity(it);
                    }
                });
                builder.setNegativeButton("Não", null);
                builder.show();
            }
        });

        cmpValorSrv.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    if (!cmpValorSrv.getText().toString().trim().isEmpty()) {
                        cmpValorSrv.getText().clear();
                    }
                } else {
                    if (cmpValorSrv.getText().toString().trim().isEmpty()) {
                        cmpValorSrv.setText(String.valueOf(valorTabela));
                    }
                }
            }
        });

        /* TROCA DO TIPO DO PNEU QUANDO FOR PNEU DA CASA **/
        btnTrocatTipoPnCasa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isPneusCasa();
            }
        });

        /* ADIÇÃO DE ITENS **/
        btnAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!cmpValorSrv.getText().toString().trim().isEmpty()) {
                    porcentgDesconto = 100 - (Double.parseDouble(cmpValorSrv.getText().toString()) * 100) / baseCalculo;
                    totalDesconto = Double.parseDouble(cmpValorSrv.getText().toString()) - baseCalculo;
                }

                if (isValidCampo()) {
                    if (Double.parseDouble(cmpValorSrv.getText().toString().trim()) < (int) valorBloqueio
                            && !GettersSetters.getDescrTipoColeta().contains("RECL")
                            && !GettersSetters.getCategoriaCli().equals("01")) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ColetaItens.this);
                        builder.setTitle("Valor abaixo do mínimo permitido");
                        builder.setIcon(R.drawable.cash);
                        builder.setCancelable(false);
                        builder.setMessage("Valor informado (R$" + cmpValorSrv.getText().toString().trim() + ") está abaixo do mínimo permitido. Digite outro valor.");
                        builder.setPositiveButton("Alterar", null);
                        AlertDialog dialog = builder.create();
                        dialog.show();

                        cmpValorSrv.setFocusable(true);
                        cmpValorSrv.requestFocus();
                        cmpValorSrv.setText(String.valueOf(valorTabela));
                    } else if ((Double.parseDouble(cmpValorSrv.getText().toString()) == 0) && !GettersSetters.getTipoUsuario().equals("01")) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ColetaItens.this);
                        builder.setTitle("Valor ZERO");
                        builder.setIcon(R.drawable.money);
                        builder.setCancelable(false);
                        builder.setMessage("Não é permitido valor do item " + sequencialItens + " igual a ZERO!");
                        builder.setPositiveButton("Fechar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                cmpValorSrv.getText().clear();
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    } else {
                        btnTrocatTipoPnCasa.setVisibility(View.GONE);

                        if (GettersSetters.getTipoColeta().contains("PC")) {
                            btnTrocatTipoPnCasa.setVisibility(View.VISIBLE);
                        }

                        btnCancelaCopia.setVisibility(View.GONE);
                        cmpItem.setText(Integer.toString(sequencialItens));

                        totalItens += 1;
                        txtQtdItens.setText(Integer.toString(totalItens));

                        valorTotalColeta += Double.parseDouble(cmpValorSrv.getText().toString()) * Double.parseDouble(cmpQtdItem.getText().toString());
                        txtVlrTotal.setText("R$ " + df.format(valorTotalColeta));

                        if (totalItens == 0) {
                            btnVisualizaItens.setVisibility(View.GONE);
                        } else {
                            btnVisualizaItens.setVisibility(View.VISIBLE);
                        }

                        valorUnitarioItem = Double.parseDouble(cmpValorSrv.getText().toString());
                        valorTotalItem = Double.parseDouble(cmpQtdItem.getText().toString()) * valorUnitarioItem;

                        /** COMISSÃO DE BORRACHEIRO **/
                        if (GettersSetters.isCheckComissBorr()) {
                            if (valorTabela > 0) {
                                dialogComissBorr(false, 0, null, null);
                            } else {
                                arrPorcComissBorr.add("0");
                                arrVlrComissBorr.add("0");
                            }
                        } else {
                            arrPorcComissBorr.add("0");
                            arrVlrComissBorr.add("0");
                        }
                        /**---------------------------- FIM -----------------------------------*/

                        sequencialItens++;

                        if (sequencialItens > 1) {
                            btnAvancar.setVisibility(View.VISIBLE);
                        }

                        valorUnitarioItem = Double.parseDouble(cmpValorSrv.getText().toString());
                        valorTotalItem = Double.parseDouble(cmpQtdItem.getText().toString()) * valorUnitarioItem;

                        if (codigoProduto.equals("") || codigoProduto.equals("GENERICO")) {
                            codigoProduto = "GENERICO";
                            valorTabelaProduto = 0;
                            valorTabela = valorTotalItem;
                            baseCalculo = valorTotalItem;
                            totalDesconto = baseCalculo - valorTabela;
                            porcentgDesconto = (valorTabela * 100) / baseCalculo;
                        }

                        //AJUSTE CMD 1680
                        if (isPneuCargaPasseio) {
                            descricaoProduto = cmpBitola.getText().toString().trim();
                        }

                        arrItemColeta.add(cmpItem.getText().toString());
                        arrQtdItemColeta.add(cmpQtdItem.getText().toString());
                        arrCodProdColeta.add(codigoProduto.trim());
                        arrBitolaColeta.add(descricaoProduto.trim());
                        arrMarcaColeta.add(cmpMarcaPneu.getText().toString().trim());
                        arrModeloColeta.add(cmpModeloPneu.getText().toString().trim());
                        arrSerieColeta.add(cmpSeriePneu.getText().toString().trim());
                        arrDotColeta.add(cmpDotPneu.getText().toString().trim());
                        arrObservItem.add(cmpObsItem.getText().toString());

                        if (codigoProduto.equals("GENERICO")) {
                            if (GettersSetters.getDescrTipoColeta().equals("RECLAMACAO")) {
                                isReclamacao = true;
                                arrDesenhoColeta.add("ANALISE TECNICA");
                            } else if (cmpBitola.getText().toString().contains("VULC")) {
                                arrDesenhoColeta.add("VULC");
                            } else if (cmpBitola.getText().toString().contains("DUPLAGEM")) {
                                arrDesenhoColeta.add("DUPLAGEM");
                            } else if (cmpBitola.getText().toString().contains("OSTFORTE")) {
                                arrDesenhoColeta.add("OSTFORTE");
                            } else {
                                arrDesenhoColeta.add(cmpDesenhoPneu.getText().toString().trim());
                            }
                        } else {
                            arrDesenhoColeta.add(cmpDesenhoPneu.getText().toString().trim());
                        }

                        arrMontadoColeta.add(tgBtnMontado.isChecked() ? "Montado" : "Não montado");
                        arrUrgenteColeta.add(chkUrgente.isChecked() ? "Sim" : "Não");
                        arrCAgua.add(chkcAgua.isChecked() ? "Sim" : "Não");
                        arrCCamara.add(chkcCamara.isChecked() ? "Sim" : "Não");
                        arrGarantia.add(chkGarantia.isChecked() ? "Sim" : "Não");

                        arrValorUnit.add(Double.toString(valorUnitarioItem));
                        arrValorTotal.add(Double.toString(valorTotalItem));
                        arrVlrTabelaItem.add(String.valueOf(valorTabela));
                        arrPercDesc.add(String.valueOf(porcentgDesconto));
                        arrPercDescGrp.add(String.valueOf(porcentgDescontoGrp));
                        arrVlrBlqIt.add(String.valueOf(valorBloqueio));
                        arrBaseCalcIt.add(String.valueOf(baseCalculo));

                        arrFilialOri.add("");
                        arrColetaOri.add("");
                        arrItemOri.add("");

                        cmpItem.setText(Integer.toString(sequencialItens));
                        cmpQtdItem.setText("1");
                        cmpBitola.getText().clear();
                        cmpMarcaPneu.getText().clear();
                        cmpModeloPneu.getText().clear();
                        cmpSeriePneu.getText().clear();
                        cmpDotPneu.getText().clear();
                        cmpDesenhoPneu.getText().clear();
                        cmpValorSrv.getText().clear();
                        cmpValorSrv.setHint("Valor R$");
                        cmpObsItem.getText().clear();
                        if (CheckConnection.isConnected(ColetaItens.this)) {
                            codigoProduto = "";
                        } else {
                            codigoProduto = "GENERICO";
                        }
                        tgBtnMontado.setChecked(false);
                        chkcAgua.setChecked(false);
                        chkcCamara.setChecked(false);
                        chkGarantia.setChecked(false);
                        chkUrgente.setChecked(false);
                        cmpDesenhoPneu.setEnabled(false);
                        cmpMarcaPneu.setEnabled(false);
                        cmpModeloPneu.setEnabled(false);
                        cmpSeriePneu.setEnabled(false);
                        cmpDotPneu.setEnabled(false);
                        cmpValorSrv.setEnabled(false);
                        cmpObsItem.setEnabled(false);
                        chkUrgente.setEnabled(false);
                        txtVlrTotal.setEnabled(false);
                        tgBtnMontado.setEnabled(false);
                        chkcAgua.setEnabled(false);
                        chkcCamara.setEnabled(false);
                        chkGarantia.setEnabled(false);
                        valorComissaoBorracheiro = 0;
                        porcentgDescontoGrp = 0;
                        tempPercentualComissaoBorracheiro = 0;
                        descricaoProduto = "";
                        codigoProduto = "";
                        cmpBitola.requestFocus();

                        btnAddItem.setVisibility(View.GONE);
                        btnAvancar.setVisibility(View.VISIBLE);

                        if (!isPneuCargaPasseio) {
                            btnBuscaProduto.setVisibility(View.VISIBLE);
                        }
                        cmpBitola.setEnabled(true);

                        if (totalItens == 99) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(ColetaItens.this);
                            builder.setCancelable(false);
                            builder.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.atencao, null));
                            builder.setTitle("Limite de itens atingido!");
                            builder.setMessage("Limite de 99 (noventa e nove) itens atingido!\n" +
                                    "Finalize esta Coleta!");
                            builder.setPositiveButton("Fechar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    btnAddItem.setVisibility(View.GONE);
                                    btnBuscaProduto.setVisibility(View.GONE);
                                }
                            });
                            builder.show();
                        } else {
                            if (!GettersSetters.isCheckComissBorr()) {
                                /* RESETA CAMPOS **/
                                LayoutInflater layoutInflater = getLayoutInflater();
                                View view = layoutInflater.inflate(R.layout.text_view_toast, null);
                                TextView txtItem = view.findViewById(R.id.txtItemToast);
                                txtItem.setText("Item " + (Integer.parseInt(cmpItem.getText().toString()) - 1) + " adicionado com sucesso!");
                                Toast toast = new Toast(ColetaItens.this);
                                toast.setView(view);
                                toast.setDuration(Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        }
                    }
                }
            }
        });
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
    private void isPneusCasa() {
        cmpValorSrv.setEnabled(true);
        cmpValorSrv.addTextChangedListener(textWatcher);

        if (GettersSetters.getDescrTipoColeta().equals("RECLAMACAO")) {
            isReclamacao = true;
            cmpDesenhoPneu.setText("ANALISE TECNICA");
            cmpQtdItem.setEnabled(false);
            cmpValorSrv.setEnabled(false);
            cmpValorSrv.setText("0.01");
        } else {
            cmpDesenhoPneu.setEnabled(true);
        }

        cmpObsItem.setEnabled(true);
        llOpcoes.setVisibility(View.GONE);

        AlertDialog.Builder builder = new AlertDialog.Builder(ColetaItens.this);
        builder.setCancelable(false);
        builder.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.exclamation, null));
        builder.setTitle("Qual o tipo de Pneu?");
        builder.setMessage("Para [PNEUS DA CASA] selecione:");
        builder.setPositiveButton("Agrícola", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                isPneuAgricola = true;
                btnBuscaProduto.setVisibility(View.VISIBLE);
                cmpValorSrv.removeTextChangedListener(textWatcher);
            }
        });
        builder.setNegativeButton("Carga/Passeio", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                codigoProduto = "GENERICO";
                isPneuCargaPasseio = true;
                cmpMarcaPneu.setEnabled(true);
                cmpModeloPneu.setEnabled(true);
                cmpSeriePneu.setEnabled(true);
                cmpDotPneu.setEnabled(true);

                if (!isReclamacao) { //AJUSTE CHAMADO 3271
                    AlertDialog.Builder builder = new AlertDialog.Builder(ColetaItens.this);
                    builder.setIcon(R.drawable.atencao);
                    builder.setTitle("Carga/Passeio");
                    builder.setMessage("Para este tipo de pneu, informe TODOS OS DADOS manualmente e após ADICIONE O ITEM.\nNão é possível buscar o item.");
                    builder.setCancelable(false);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            btnBuscaProduto.setVisibility(View.GONE);
                        }
                    });
                    AlertDialog dialog1 = builder.create();
                    dialog1.show();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void exibeTeclado() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    @Override
    public void onBackPressed() {
        if (totalItens > 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ColetaItens.this);
            builder.setCancelable(true);
            builder.setMessage("Deseja cancelar a operação? \nTodas as informações serão perdidas!");
            builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            builder.setNegativeButton("Não", null);
            builder.show();
        } else {
            finish();
        }
    }

    /*
     * Função para quando virar a tela não resetar o layout
     **/
    @Override
    public void onConfigurationChanged(@NotNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public double percentualComissBorr(double valorItem, double baseCalculo) {
        double faixaComissao = 0;
        double percentualDescontoAcrescimo;

        ArrayList<String> arrIdComissBorr = new ArrayList<>();
        ArrayList<String> arrFxIniComissBorr = new ArrayList<>();
        ArrayList<String> arrFxFimComissBorr = new ArrayList<>();
        ArrayList<Double> arrPercComissBorr = new ArrayList<>();

        Cursor res = db.selecionaComissaoBorracheiro();
        if (res.getCount() > 0) {
            arrIdComissBorr.addAll(db.arrIdComissBorr);
            arrFxIniComissBorr.addAll(db.arrFxIniComissBorr);
            arrFxFimComissBorr.addAll(db.arrFxFimComissBorr);
            arrPercComissBorr.addAll(db.arrPercComissBorr);
        }

        percentualDescontoAcrescimo = BigDecimal.valueOf((((baseCalculo - 1) - (valorItem)) / (baseCalculo - 1)) * 100).setScale(2, RoundingMode.HALF_EVEN).doubleValue();

        if (percentualDescontoAcrescimo < -299.99) {
            faixaComissao = 0;
        } else {
            if (percentualDescontoAcrescimo <= 0 && percentualDescontoAcrescimo >= -299.99) {
                faixaComissao = 0;
                for (int i = 0; i < arrPercComissBorr.size(); i++) {
                    if (arrPercComissBorr.get(i) > faixaComissao) {
                        faixaComissao = arrPercComissBorr.get(i);
                    }
                }
            } else {
                for (int i = 0; i < arrIdComissBorr.size(); i++) {
                    if ((percentualDescontoAcrescimo >= Double.parseDouble(arrFxIniComissBorr.get(i)) && (percentualDescontoAcrescimo <= Double.parseDouble(arrFxFimComissBorr.get(i))))) {
                        faixaComissao = arrPercComissBorr.get(i);
                        break;
                    }
                }
            }
        }
        return faixaComissao;
    }

    @SuppressWarnings("rawtypes")
    @SuppressLint("StaticFieldLeak")
    public class BuscaItens extends AsyncTask<ArrayList<String>, String, ArrayList<String>> {
        AlertDialog dialogBuscaItens = null;

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPreExecute() {
            AlertDialog.Builder builder = new AlertDialog.Builder(ColetaItens.this);
            LayoutInflater layoutInflater = getLayoutInflater();
            final View dialogView = layoutInflater.inflate(R.layout.dialog_carregamento, null);
            final TextView textView = dialogView.findViewById(R.id.txtInfoCarreg);
            textView.setText("Buscando Produtos...\nAguarde...");
            builder.setCancelable(false);
            builder.setView(dialogView);
            dialogBuscaItens = builder.create();
            dialogBuscaItens.show();
        }

        @SuppressWarnings("unchecked")
        @Override
        protected ArrayList doInBackground(ArrayList... params) {
            arrCodProdutos.clear();
            arrDescProdutos.clear();
            arrProdBlq.clear();
            arrDesenhoProd.clear();
            arrPrecoTabela.clear();
            arrDescVachiOst.clear();
            arrDescLauxen.clear();
            arrIsDescGrp.clear();

            /** SEPARAÇÃO DAS LETRAS DA BUSCA PARA BUSCAR ITENS GERAIS SEM OS CARACTERES ESPECIAIS **/
            StringBuilder descricao = new StringBuilder();
            char[] letras = descricaoProduto.toCharArray();

            for (char letra : letras) {
                descricao.append(letra).append("%");
            }

            Cursor resProdutos = db.buscaProduto(descricao.toString(), isPneuAgricola, "");
            if (resProdutos.getCount() > 0) {
                arrCodProdutos.addAll(db.arrCodProdutos);
                arrDescProdutos.addAll(db.arrDescProdutos);
                arrProdBlq.addAll(db.arrProdBlq);
                arrDesenhoProd.addAll(db.arrDesenhoProd);
                arrPrecoTabela.addAll(db.arrPrecoTabela);
                arrDescVachiOst.addAll(db.arrDescVachiOst);
                arrDescLauxen.addAll(db.arrDescLauxen);
                arrIsDescGrp.addAll(db.arrIsDescGrp);
            }
            return arrDescProdutos;
        }

        @Override
        protected void onPostExecute(ArrayList<String> itens) {
            if (dialogBuscaItens != null) {
                dialogBuscaItens.dismiss();
            }

            valorTabela = 0;
            codigoProduto = "";
            descricaoProduto = "";
            valorTabelaProduto = 0;
            porcentgDescontoGrp = 0;

            baseCalculo = 0;
            porcentgDesconto = 0;
            totalDesconto = 0;
            valorBloqueio = 0;

            if (itens.size() > 0) {
                final String[] produtosLocalizados = new String[itens.size()];
                final String[] codProdutoSelecionado = new String[arrCodProdutos.size()];
                final String[] desenhoProdutoSelecionado = new String[arrDesenhoProd.size()];
                final String[] precoVendaProdSelecionado = new String[arrPrecoTabela.size()];
                final String[] descontoVachiOst = new String[arrDescVachiOst.size()];
                final String[] descontoLauxen = new String[arrDescLauxen.size()];
                final Boolean[] isDescGrupo = new Boolean[arrIsDescGrp.size()];
                for (int i = 0; i < itens.size(); i++) {
                    produtosLocalizados[i] = itens.get(i);
                    codProdutoSelecionado[i] = arrCodProdutos.get(i);
                    desenhoProdutoSelecionado[i] = arrDesenhoProd.get(i);
                    precoVendaProdSelecionado[i] = arrPrecoTabela.get(i);
                    descontoVachiOst[i] = arrDescVachiOst.get(i);
                    descontoLauxen[i] = arrDescLauxen.get(i);
                    isDescGrupo[i] = arrIsDescGrp.get(i);
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(ColetaItens.this);
                builder.setTitle("Selecione o Produto");
                builder.setIcon(R.drawable.tires);
                builder.setCancelable(true);

                final ArrayAdapter<String> adapterProdutos = new ArrayAdapter<String>(ColetaItens.this, android.R.layout.simple_expandable_list_item_1, produtosLocalizados) {
                    @Override
                    public View getView(int position, View convertView, @NotNull ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);
                        if (((TextView) view).getText().toString().contains("*** ")) {
                            ((TextView) view).setTextColor(Color.parseColor("#FF0000"));
                            cmpBitola.setTextColor(Color.parseColor("#FF0000"));
                        } else {
                            ((TextView) view).setTextColor(Color.parseColor("#000000"));
                            cmpBitola.setTextColor(Color.parseColor("#000000"));
                        }
                        ((TextView) view).setTextSize(12);
                        return view;
                    }
                };

                builder.setAdapter(adapterProdutos, new DialogInterface.OnClickListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onClick(DialogInterface dialog, int itemSelecionado) {
                        String produtoSelecionado = Objects.requireNonNull(adapterProdutos.getItem(itemSelecionado));

                        if (produtoSelecionado.contains("*** ")) {
                            final AlertDialog.Builder builder = new AlertDialog.Builder(ColetaItens.this);
                            builder.setCancelable(false);
                            builder.setIcon(R.drawable.exclamation);
                            builder.setTitle("Atenção!");
                            builder.setMessage("Produto " + produtoSelecionado.replace("*** ", "") + " está BLOQUEADO para uso! Entre em contato com o SETOR ADMINISTRATIVO ou SELECIONE OUTRO ITEM.");
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    codigoProduto = "";
                                    descricaoProduto = "";
                                    valorTabela = 0;
                                    valorTotalColeta = 0;
                                    cmpBitola.getText().clear();
                                    cmpDesenhoPneu.getText().clear();
                                    cmpValorSrv.getText().clear();
                                    cmpBitola.requestFocus();
                                    dialog.dismiss();
                                }
                            });
                            builder.show();
                        }

                        codigoProduto = Objects.requireNonNull((codProdutoSelecionado[itemSelecionado].trim()));
                        cmpBitola.setText(produtoSelecionado.replace("*** ", "").trim());
                        descricaoProduto = cmpBitola.getText().toString().trim();

                        if (desenhoProdutoSelecionado[itemSelecionado].trim().isEmpty()) {
                            cmpDesenhoPneu.setEnabled(true);
                        } else {
                            cmpDesenhoPneu.setText(Objects.requireNonNull(desenhoProdutoSelecionado[itemSelecionado].trim()));
                            if (!cmpDesenhoPneu.getText().toString().trim().isEmpty()) {
                                cmpDesenhoPneu.setEnabled(false);
                            }
                        }

                        if (descricaoProduto.contains("VULC") || cmpBitola.getText().toString().contains("VULC")) {
                            cmpDesenhoPneu.setText("VULC");
                        } else if (descricaoProduto.contains("DUPLAGEM") || cmpBitola.getText().toString().contains("DUPLAGEM")) {
                            cmpDesenhoPneu.setText("DUPLAGEM");
                        } else if (descricaoProduto.contains("OSTFORTE") || cmpBitola.getText().toString().contains("OSTFORTE")) {
                            cmpDesenhoPneu.setText("OSTFORTE");
                        }

                        /** DESCONTO MAX DOS ITENS **/
                        if (GettersSetters.isClienteGrp()) {
                            porcentgDescontoGrp = GettersSetters.getDescGrupo();
                        } else {
                            if (GettersSetters.getDescExclusivo() > 0) {
                                porcentgDescontoGrp = GettersSetters.getDescExclusivo();
                            } else {
                                if (GettersSetters.getFilial().contains("1301") || GettersSetters.getFilial().contains("1401")) {
                                    porcentgDescontoGrp = Double.parseDouble(Objects.requireNonNull(descontoLauxen[itemSelecionado]));
                                } else {
                                    porcentgDescontoGrp = Double.parseDouble(Objects.requireNonNull(descontoVachiOst[itemSelecionado]));
                                }
                            }
                        }
                        /**---------------------------**/

                        baseCalculo = Double.parseDouble(precoVendaProdSelecionado[itemSelecionado]); // VALOR CHEIO DA TABELA
                        valorBloqueio = new BigDecimal(baseCalculo - (baseCalculo * (porcentgDescontoGrp / 100)), MathContext.DECIMAL32).setScale(2, RoundingMode.HALF_UP).doubleValue();

                        if (GettersSetters.getDescrTipoColeta().equals("RECLAMACAO")) {
                            isReclamacao = true;
                            cmpValorSrv.setText("0.01");
                            cmpDesenhoPneu.setText("ANALISE TECNICA");
                            cmpValorSrv.setEnabled(false);
                        } else if (GettersSetters.getCategoriaCli().equals("01")) {
                            cmpValorSrv.setText(Objects.requireNonNull(precoVendaProdSelecionado[itemSelecionado]));
                            cmpValorSrv.setEnabled(GettersSetters.getFilial().startsWith("1101") || GettersSetters.getFilial().startsWith("1201"));
                        } else {
                            if (GettersSetters.isClienteGrp()) {
                                isTabela = true;
                                if (isDescGrupo[itemSelecionado]) {
                                    valorTabela = valorBloqueio;
                                    cmpValorSrv.setEnabled(false);
                                } else {
                                    valorTabela = baseCalculo;
                                    cmpValorSrv.setEnabled(true);
                                }
                                totalDesconto = baseCalculo - valorTabela;
                                porcentgDesconto = new BigDecimal((valorTabela * 100) / baseCalculo, MathContext.DECIMAL32).setScale(2, RoundingMode.HALF_UP).doubleValue();
                                cmpValorSrv.setText(String.valueOf(valorTabela));
                                cmpValorSrv.setHint(String.valueOf(valorTabela));
                                valorBloqueio = valorTabela;
                            } else if (GettersSetters.getDescExclusivo() > 0 && GettersSetters.getTipoColeta().contains("SE")) { //DESCONTO FIXO NO CADASTRO DO CLIENTE
                                isTabela = true;
                                // SE O CLIENTE TEM DESCONTO FIXO, E POR ACASO, O ITEM SELECIONADO TEM TABELA DE PREÇOS CADASTRADA, BUSCA DA TABELA - CMD 2119
                                valorTabela = db.selectTabPrcCli(GettersSetters.getTabelaPrcCli().trim(), codigoProduto.trim());
                                if (valorTabela == 0) {
                                    valorTabela = new BigDecimal(baseCalculo - (baseCalculo * (GettersSetters.getDescExclusivo() / 100)), MathContext.DECIMAL32).setScale(2, RoundingMode.HALF_UP).doubleValue(); //VALOR DE TABELA COM DESCONTO FIXO APLICADO
                                }
                                totalDesconto = baseCalculo - valorTabela;
                                porcentgDesconto = new BigDecimal((valorTabela * 100) / baseCalculo, MathContext.DECIMAL32).setScale(2, RoundingMode.HALF_UP).doubleValue();
                                cmpValorSrv.setText(String.valueOf(valorTabela));
                                cmpValorSrv.setHint(String.valueOf(valorTabela));
                                valorBloqueio = valorTabela;
                                cmpValorSrv.setEnabled(false);
                            } else if (!GettersSetters.getTabelaPrcCli().trim().equals("")) { //TABELA DE PREÇO DO CLIENTE
                                valorTabela = db.selectTabPrcCli(GettersSetters.getTabelaPrcCli().trim(), codigoProduto.trim());
                                if (valorTabela != 0) {
                                    isTabela = true;
                                    totalDesconto = baseCalculo - valorTabela;
                                    porcentgDesconto = new BigDecimal((valorTabela * 100) / baseCalculo, MathContext.DECIMAL32).setScale(2, RoundingMode.HALF_UP).doubleValue();
                                    cmpValorSrv.setText(String.valueOf(valorTabela));
                                    cmpValorSrv.setHint(String.valueOf(valorTabela));
                                    valorBloqueio = valorTabela;
                                    cmpValorSrv.setEnabled(false);
                                } else {
                                    isTabela = false;
                                    valorTabela = Double.parseDouble(Objects.requireNonNull(precoVendaProdSelecionado[itemSelecionado].trim())); //usado na comissão de borracheiro para calculo (preco de tabela do sistema)
                                    totalDesconto = baseCalculo - valorTabela;
                                    porcentgDesconto = new BigDecimal((valorTabela * 100) / baseCalculo, MathContext.DECIMAL32).setScale(2, RoundingMode.HALF_UP).doubleValue();
                                    cmpValorSrv.setText(Objects.requireNonNull(precoVendaProdSelecionado[itemSelecionado]));
                                    cmpValorSrv.setHint(Objects.requireNonNull(precoVendaProdSelecionado[itemSelecionado]));
                                    cmpValorSrv.setEnabled(true);
                                }
                            } else {
                                isTabela = false;
                                valorTabela = Double.parseDouble(Objects.requireNonNull(precoVendaProdSelecionado[itemSelecionado].trim())); //usado na comissão de borracheiro para calculo (preco de tabela do sistema)
                                totalDesconto = baseCalculo - valorTabela;
                                porcentgDesconto = new BigDecimal((valorTabela * 100) / baseCalculo, MathContext.DECIMAL32).setScale(2, RoundingMode.HALF_UP).doubleValue();
                                cmpValorSrv.setText(Objects.requireNonNull(precoVendaProdSelecionado[itemSelecionado]));
                                cmpValorSrv.setHint(Objects.requireNonNull(precoVendaProdSelecionado[itemSelecionado]));
                                cmpValorSrv.setEnabled(true);
                            }
                        }

                        cmpBitola.setTextColor(Color.BLACK);
                        cmpObsItem.setEnabled(true);
                        txtVlrTotal.setEnabled(true);

                        if (llEdit.getVisibility() == View.GONE) {
                            btnAddItem.setVisibility(View.VISIBLE);
                        } else {
                            btnAddItem.setVisibility(View.GONE);
                        }

                        if (GettersSetters.getTipoColeta().contains("SE") || GettersSetters.getTipoColeta().contains("PC")) {
                            cmpMarcaPneu.setEnabled(true);
                            cmpModeloPneu.setEnabled(true);
                            cmpSeriePneu.setEnabled(true);
                            cmpDotPneu.setEnabled(true);
                            chkUrgente.setEnabled(true);
                            tgBtnMontado.setEnabled(true);
                            chkcAgua.setEnabled(true);
                            chkcCamara.setEnabled(true);
                            chkGarantia.setEnabled(true);
                        }
                        cmpMarcaPneu.requestFocus();
                    }
                });

                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(ColetaItens.this);
                        builder1.setTitle("Encontrou o item que pesquisou?");
                        builder1.setIcon(R.drawable.exclamation);
                        builder1.setCancelable(false);
                        builder1.setPositiveButton("Não", new DialogInterface.OnClickListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                codigoProduto = "GENERICO";
                                valorTabela = 0;
                                cmpDesenhoPneu.getText().clear();
                                descricaoProduto = cmpBitola.getText().toString();

                                if (GettersSetters.getDescrTipoColeta().equals("RECLAMACAO")) {
                                    isReclamacao = true;
                                    cmpDesenhoPneu.setText("ANALISE TECNICA");
                                    cmpValorSrv.setText("0.01");
                                    cmpValorSrv.setEnabled(false);
                                } else {
                                    cmpDesenhoPneu.setEnabled(true);
                                }
                                if (GettersSetters.getTipoColeta().contains("SE") || GettersSetters.getTipoColeta().contains("PC")) {
                                    cmpMarcaPneu.setEnabled(true);
                                    cmpModeloPneu.setEnabled(true);
                                    cmpSeriePneu.setEnabled(true);
                                    cmpDotPneu.setEnabled(true);
                                    chkUrgente.setEnabled(true);
                                    tgBtnMontado.setEnabled(true);
                                    chkcAgua.setEnabled(true);
                                    chkcCamara.setEnabled(true);
                                    chkGarantia.setEnabled(true);
                                }
                                if (!GettersSetters.getCategoriaCli().equals("01")) {
                                    cmpValorSrv.setEnabled(true);
                                }
                                cmpObsItem.setEnabled(true);
                                txtVlrTotal.setEnabled(true);

                                if (llEdit.getVisibility() == View.GONE) {
                                    btnAddItem.setVisibility(View.VISIBLE);
                                } else {
                                    btnAddItem.setVisibility(View.GONE);
                                }

                                AlertDialog.Builder builder = new AlertDialog.Builder(ColetaItens.this);
                                builder.setIcon(R.drawable.atencao);
                                builder.setTitle("Pesquisa de item");
                                builder.setMessage("Preencha os dados do item manualmente!");
                                builder.setCancelable(false);
                                builder.setPositiveButton("Fechar", null);
                                AlertDialog dialog1 = builder.create();
                                dialog1.show();
                            }
                        });

                        builder1.setNegativeButton("Repetir Busca", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                                Toast.makeText(ColetaItens.this, "Verifique a bitola digitada e pesquise novamente!", Toast.LENGTH_LONG).show();

                                cmpMarcaPneu.setEnabled(false);
                                cmpModeloPneu.setEnabled(false);
                                cmpSeriePneu.setEnabled(false);
                                cmpDotPneu.setEnabled(false);
                                cmpDesenhoPneu.setEnabled(false);
                                cmpValorSrv.setEnabled(false);
                                cmpObsItem.setEnabled(false);
                                chkUrgente.setEnabled(false);
                                txtVlrTotal.setEnabled(false);
                                tgBtnMontado.setEnabled(false);
                                chkcAgua.setEnabled(false);
                                chkcCamara.setEnabled(false);
                                chkGarantia.setEnabled(false);

                                cmpBitola.setFocusable(true);
                                cmpBitola.requestFocus();

                                if (llEdit.getVisibility() == View.GONE) {
                                    btnAddItem.setVisibility(View.VISIBLE);
                                } else {
                                    btnAddItem.setVisibility(View.GONE);
                                }

                                exibeTeclado();
                            }
                        });

                        if (codigoProduto.equals("")) {
                            AlertDialog dialog1 = builder1.create();
                            dialog1.show();
                        }
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(ColetaItens.this);
                builder.setIcon(R.drawable.exclamation);
                builder.setTitle("Atenção!");
                builder.setMessage("Produto [" + cmpBitola.getText().toString().trim() + "] não encontrado!\nVerifique os dados digitados, tipo de coleta ou preencha manualmente!");
                builder.setCancelable(false);
                builder.setPositiveButton("Fechar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        codigoProduto = "GENERICO";
                        valorTabela = 0;
                        cmpDesenhoPneu.getText().clear();
                        cmpDesenhoPneu.setEnabled(true);
                        descricaoProduto = cmpBitola.getText().toString();

                        if (GettersSetters.getTipoColeta().contains("SE") || GettersSetters.getTipoColeta().contains("PC")) {
                            cmpMarcaPneu.setEnabled(true);
                            cmpModeloPneu.setEnabled(true);
                            cmpSeriePneu.setEnabled(true);
                            cmpDotPneu.setEnabled(true);
                            chkUrgente.setEnabled(true);
                            tgBtnMontado.setEnabled(true);
                            chkcAgua.setEnabled(true);
                            chkcCamara.setEnabled(true);
                            chkGarantia.setEnabled(true);
                        }
                        if (!GettersSetters.getCategoriaCli().equals("01")) {
                            cmpValorSrv.setEnabled(true);
                        }
                        cmpObsItem.setEnabled(true);
                        txtVlrTotal.setEnabled(true);

                        if (llEdit.getVisibility() == View.GONE) {
                            btnAddItem.setVisibility(View.VISIBLE);
                        } else {
                            btnAddItem.setVisibility(View.GONE);
                        }
                    }
                });
                AlertDialog dialog1 = builder.create();
                dialog1.show();
            }

            arrCodProdutos.clear();
            arrDescProdutos.clear();
            arrProdBlq.clear();
            arrDesenhoProd.clear();
            arrPrecoTabela.clear();
            arrDescVachiOst.clear();
            arrDescLauxen.clear();
            arrIsDescGrp.clear();
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class VerificacaoProdutos extends AsyncTask<Boolean, Integer, Boolean> {
        @Override
        protected void onPreExecute() {
            mProgressDialogItens = new ProgressDialog(ColetaItens.this, 0);
            mProgressDialogItens.setIcon(R.drawable.ampulheta);
            mProgressDialogItens.setTitle("Produtos e Tabelas");
            mProgressDialogItens.setMessage("[1] Atualizando Produtos...\n[2] Atualizando tabelas de preços...\nAguarde...");
            mProgressDialogItens.setIndeterminate(false);
            mProgressDialogItens.setMax(100);
            mProgressDialogItens.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialogItens.setCancelable(false);
            mProgressDialogItens.show();
        }

        @SuppressLint({"WrongThread", "ApplySharedPref"})
        @Override
        protected Boolean doInBackground(Boolean... params) {
            /** ATUALIZA PRODUTOS LOCALMENTE - PARA UTILIZAR DURANTE AS COLETAS ONLINE E OFFLINE **/
            if (CheckConnection.isConnected(ColetaItens.this)) {
                if (params[0]) {
                    if (conecta.selecionaEInsereProdutos(ColetaItens.this, 1)) {
                        SharedPreferences.Editor editor = getSharedPreferences(Home.ATUALIZACAO_DADOS, MODE_PRIVATE).edit();
                        editor.putString("produtos", conecta.selecionaParametroAtualizaDados("PRODUTOS"));
                        editor.commit();

                        conecta.updDadosAuditApp(GettersSetters.getIdUsuarioLogado(), "DT_ATT_PROD_COL", GettersSetters.getDataEN() + " - " + GettersSetters.getHora());
                        GettersSetters.setAtualizarProdutos(false);
                    } else {
                        Snackbar.make(viewSnackBar, "Erro ao atualizar os produtos. Tente novamente!", Snackbar.LENGTH_LONG).show();
                    }
                }
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean atualiza) {
            mProgressDialogItens.dismiss();

            if (GettersSetters.isAtualizarProdutos()) {
                btnAtualizaProdutos.setVisibility(View.VISIBLE);
            } else {
                btnAtualizaProdutos.setVisibility(View.GONE);
            }
        }
    }

    @SuppressLint("SetTextI18n")
    public void resetaCampos() {
        if (!isPneuCargaPasseio) {
            btnBuscaProduto.setVisibility(View.VISIBLE);
        } else {
            btnBuscaProduto.setVisibility(View.GONE);
        }

        cmpBitola.setEnabled(true);

        cmpItem.setText(Integer.toString(sequencialItens));
        cmpQtdItem.setText("1");
        cmpBitola.getText().clear();
        cmpMarcaPneu.getText().clear();
        cmpModeloPneu.getText().clear();
        cmpSeriePneu.getText().clear();
        cmpDotPneu.getText().clear();
        cmpDesenhoPneu.getText().clear();
        cmpValorSrv.getText().clear();
        cmpValorSrv.setHint("Valor R$");
        cmpObsItem.getText().clear();
        if (CheckConnection.isConnected(ColetaItens.this)) {
            codigoProduto = "";
        } else {
            codigoProduto = "GENERICO";
        }
        tgBtnMontado.setChecked(false);
        chkUrgente.setChecked(false);
        cmpDesenhoPneu.setEnabled(false);
        cmpMarcaPneu.setEnabled(false);
        cmpModeloPneu.setEnabled(false);
        cmpSeriePneu.setEnabled(false);
        cmpDotPneu.setEnabled(false);
        cmpValorSrv.setEnabled(false);
        cmpObsItem.setEnabled(false);
        chkUrgente.setEnabled(false);
        txtVlrTotal.setEnabled(false);
        tgBtnMontado.setEnabled(false);
        chkcAgua.setEnabled(false);
        chkcCamara.setEnabled(false);
        chkGarantia.setEnabled(false);
        valorComissaoBorracheiro = 0;
        porcentgDescontoGrp = 0;
        tempPercentualComissaoBorracheiro = 0;
        descricaoProduto = "";
        codigoProduto = "";
        cmpBitola.requestFocus();
        btnAddItem.setVisibility(View.GONE);

        btnCancelaCopia.setVisibility(View.GONE);
    }

    public boolean isValidCampo() {
        if (codigoProduto == null || codigoProduto.equals("")) {
            Toast.makeText(ColetaItens.this, "Realize a busca do item!", Toast.LENGTH_LONG).show();
            return false;
        } else if (cmpQtdItem.getText().toString().trim().isEmpty()) {
            cmpQtdItem.setFocusable(true);
            cmpQtdItem.requestFocus();
            cmpQtdItem.setError(getString(R.string.erro_campo_obrigatorio));
            exibeTeclado();
            return false;
        } else if (cmpBitola.getText().toString().trim().isEmpty()) {
            cmpBitola.setFocusable(true);
            cmpBitola.requestFocus();
            cmpBitola.setError(getString(R.string.erro_campo_obrigatorio));
            exibeTeclado();
            return false;
        } else if (cmpMarcaPneu.getText().toString().trim().isEmpty() && (GettersSetters.getTipoColeta().contains("SE") || (GettersSetters.getDescrTipoColeta().contains("RECLAM") && GettersSetters.getTipoColeta().contains("PC")))) {
            cmpMarcaPneu.setFocusable(true);
            cmpMarcaPneu.requestFocus();
            cmpMarcaPneu.setError(getString(R.string.erro_campo_obrigatorio));
            exibeTeclado();
            return false;
        } else if (cmpModeloPneu.getText().toString().trim().isEmpty() && (GettersSetters.getTipoColeta().contains("SE") || (GettersSetters.getDescrTipoColeta().contains("RECLAM") && GettersSetters.getTipoColeta().contains("PC")))) {
            cmpModeloPneu.setFocusable(true);
            cmpModeloPneu.requestFocus();
            cmpModeloPneu.setError(getString(R.string.erro_campo_obrigatorio));
            exibeTeclado();
            return false;
        } else if (cmpSeriePneu.getText().toString().trim().isEmpty() && (GettersSetters.getTipoColeta().contains("SE") || (GettersSetters.getDescrTipoColeta().contains("RECLAM") && GettersSetters.getTipoColeta().contains("PC")))) {
            cmpSeriePneu.setFocusable(true);
            cmpSeriePneu.requestFocus();
            cmpSeriePneu.setError(getString(R.string.erro_campo_obrigatorio));
            exibeTeclado();
            return false;
        } else if (cmpDotPneu.getText().toString().trim().isEmpty() && (GettersSetters.getTipoColeta().contains("SE") || (GettersSetters.getDescrTipoColeta().contains("RECLAM") && GettersSetters.getTipoColeta().contains("PC")))) {
            cmpDotPneu.setFocusable(true);
            cmpDotPneu.requestFocus();
            cmpDotPneu.setError(getString(R.string.erro_campo_obrigatorio));
            exibeTeclado();
            return false;
        } else if (cmpDesenhoPneu.getText().toString().trim().isEmpty() && (GettersSetters.getTipoColeta().contains("SE") || (GettersSetters.getDescrTipoColeta().contains("RECLAM") && GettersSetters.getTipoColeta().contains("PC")))) {
            cmpDesenhoPneu.setFocusable(true);
            cmpDesenhoPneu.requestFocus();
            cmpDesenhoPneu.setError(getString(R.string.erro_campo_obrigatorio));
            exibeTeclado();
            return false;
        } else if ((cmpValorSrv.getText().toString().trim().isEmpty() || cmpValorSrv.getText().toString().trim().equals("0.00")) && !GettersSetters.getCategoriaCli().equals("01")) {
            cmpValorSrv.setFocusable(true);
            cmpValorSrv.requestFocus();
            cmpValorSrv.setError(getString(R.string.erro_campo_obrigatorio));
            exibeTeclado();
            return false;
        } else if (cmpMarcaPneu.getText().toString().trim().length() < 3 && (GettersSetters.getTipoColeta().contains("SE") || (GettersSetters.getDescrTipoColeta().contains("RECLAM") && GettersSetters.getTipoColeta().contains("PC")))) {
            cmpMarcaPneu.setFocusable(true);
            cmpMarcaPneu.requestFocus();
            cmpMarcaPneu.setError(getString(R.string.tamanho_3_invalido));
            exibeTeclado();
            return false;
        } else if (cmpModeloPneu.getText().toString().trim().length() < 3 && (GettersSetters.getTipoColeta().contains("SE") || (GettersSetters.getDescrTipoColeta().contains("RECLAM") && GettersSetters.getTipoColeta().contains("PC")))) {
            cmpModeloPneu.setFocusable(true);
            cmpModeloPneu.requestFocus();
            cmpModeloPneu.setError(getString(R.string.tamanho_3_invalido));
            exibeTeclado();
            return false;
        } else if (cmpSeriePneu.getText().toString().trim().length() < 3 && (GettersSetters.getTipoColeta().contains("SE") || (GettersSetters.getDescrTipoColeta().contains("RECLAM") && GettersSetters.getTipoColeta().contains("PC")))) {
            cmpSeriePneu.setFocusable(true);
            cmpSeriePneu.requestFocus();
            cmpSeriePneu.setError(getString(R.string.tamanho_3_invalido));
            exibeTeclado();
            return false;
        } else if (cmpDotPneu.getText().toString().trim().length() < 3 && (GettersSetters.getTipoColeta().contains("SE") || (GettersSetters.getDescrTipoColeta().contains("RECLAM") && GettersSetters.getTipoColeta().contains("PC")))) {
            cmpDotPneu.setFocusable(true);
            cmpDotPneu.requestFocus();
            cmpDotPneu.setError(getString(R.string.tamanho_3_invalido));
            exibeTeclado();
            return false;
        } else if (cmpDesenhoPneu.getText().toString().trim().length() < 3 && codigoProduto.equals("GENERICO") && (GettersSetters.getTipoColeta().contains("SE") || (GettersSetters.getDescrTipoColeta().contains("RECLAM") && GettersSetters.getTipoColeta().contains("PC")))) {
            cmpDesenhoPneu.setFocusable(true);
            cmpDesenhoPneu.requestFocus();
            cmpDesenhoPneu.setError(getString(R.string.tamanho_3_invalido));
            exibeTeclado();
            return false;
        } else {
            return true;
        }
    }

    public void dialogComissBorr(boolean isEditItem, int i, String outPercComissBorr, String outVlrComissBorr) {
        AlertDialog.Builder builderComiss = new AlertDialog.Builder(ColetaItens.this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_inf_val_comissao_borr, null);
        builderComiss.setCancelable(false);
        builderComiss.setView(dialogView);

        final Button btn_positive = dialogView.findViewById(R.id.btnSalvaDados);
        final EditText porcentComBorr = dialogView.findViewById(R.id.cmpPercComissao);
        final EditText valorComBorr = dialogView.findViewById(R.id.cmpValorComissao);

        valorMaxComissaoBorracheiro = percentualComissBorr(valorTotalItem, baseCalculo);

        porcentComBorr.getText().clear();
        valorComBorr.getText().clear();

        if (valorMaxComissaoBorracheiro > 0) {
            final AlertDialog dialogComissBorr = builderComiss.create();
            porcentComBorr.setFocusable(true);
            porcentComBorr.requestFocus();
            porcentComBorr.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @SuppressLint("SetTextI18n")
                @Override
                public void afterTextChanged(Editable s) {
                    if (porcentComBorr.isFocused()) {
                        if (!porcentComBorr.getText().toString().trim().isEmpty()) {
                            if ((Double.parseDouble(porcentComBorr.getText().toString()) > valorMaxComissaoBorracheiro)) {
                                porcentComBorr.setText("");
                                valorComBorr.setText("");
                                btn_positive.setVisibility(View.GONE);

                                AlertDialog.Builder builder = new AlertDialog.Builder(ColetaItens.this);
                                builder.setTitle("Percentual de Comissão");
                                builder.setIcon(R.drawable.commission);
                                builder.setCancelable(false);
                                builder.setMessage("Percentual de comissão máximo é de: " + valorMaxComissaoBorracheiro + "%\nSomente é aceita porcentagem abaixo de " + valorMaxComissaoBorracheiro + "%");
                                builder.setPositiveButton("OK", null);
                                AlertDialog dialog = builder.create();
                                dialog.show();
                            } else {
                                if (!porcentComBorr.getText().toString().trim().isEmpty()) {
                                    btn_positive.setVisibility(View.VISIBLE);
                                    valorComissaoBorracheiro = (valorTotalItem * Double.parseDouble(porcentComBorr.getText().toString()) / 100);
                                    BigDecimal vComissBig = new BigDecimal(valorComissaoBorracheiro, MathContext.DECIMAL32).setScale(2, RoundingMode.HALF_UP);
                                    valorComBorr.setText(String.valueOf(vComissBig));
                                } else {
                                    valorComBorr.getText().clear();
                                    btn_positive.setVisibility(View.GONE);
                                    valorComBorr.setText("");
                                }
                            }
                        } else {
                            btn_positive.setVisibility(View.GONE);
                            valorComBorr.setText("");
                        }
                    }
                }
            });

            valorComBorr.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (valorComBorr.isFocused()) {
                        if (!valorComBorr.getText().toString().trim().isEmpty()) {
                            percentualComissaoBorracheiro = (Double.parseDouble(valorComBorr.getText().toString()) * 100) / valorTotalItem;
                            porcentComBorr.setText(String.valueOf(percentualComissaoBorracheiro));
                            btn_positive.setVisibility(View.VISIBLE);

                            if ((Double.parseDouble(porcentComBorr.getText().toString()) > valorMaxComissaoBorracheiro)) {
                                porcentComBorr.setText("");
                                valorComBorr.setText("");
                                btn_positive.setVisibility(View.GONE);

                                AlertDialog.Builder builder = new AlertDialog.Builder(ColetaItens.this);
                                builder.setTitle("Percentual de Comissão");
                                builder.setIcon(R.drawable.commission);
                                builder.setCancelable(false);
                                builder.setMessage("Percentual de comissão máximo é de: " + valorMaxComissaoBorracheiro + "%\nSomente é aceita porcentagem abaixo de " + valorMaxComissaoBorracheiro + "%");
                                builder.setPositiveButton("OK", null);
                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }
                        } else {
                            btn_positive.setVisibility(View.GONE);
                            porcentComBorr.setText("");
                        }
                    }
                }
            });

            btn_positive.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onClick(View v) {
                    if (Double.parseDouble(porcentComBorr.getText().toString()) == 0 ||
                            Double.parseDouble(valorComBorr.getText().toString().replace(",", ".")) == 0) {
                        porcentComBorr.setText("");
                        valorComBorr.setText("");
                        btn_positive.setVisibility(View.GONE);

                        AlertDialog.Builder builder = new AlertDialog.Builder(ColetaItens.this);
                        builder.setTitle("Comissão");
                        builder.setIcon(R.drawable.exclamation);
                        builder.setCancelable(false);
                        builder.setMessage("Percentual de comissão não pode ser 0% e nem valor R$0,00.\nPreencha corretamente!");
                        builder.setNegativeButton("Alterar", null);
                        AlertDialog dialog2 = builder.create();
                        dialog2.show();
                    } else {
                        if (isEditItem) {
                            arrPorcComissBorr.set(i, porcentComBorr.getText().toString());
                            arrVlrComissBorr.set(i, valorComBorr.getText().toString());
                        } else {
                            arrPorcComissBorr.add(porcentComBorr.getText().toString());
                            arrVlrComissBorr.add(valorComBorr.getText().toString());
                        }

                        dialogComissBorr.dismiss();

                        LayoutInflater layoutInflater = getLayoutInflater();
                        View view = layoutInflater.inflate(R.layout.text_view_toast, null);
                        TextView txtItem = view.findViewById(R.id.txtItemToast);
                        txtItem.setText("Item " + (Integer.parseInt(cmpItem.getText().toString()) - 1) + " adicionado com sucesso!");
                        Toast toast = new Toast(ColetaItens.this);
                        toast.setView(view);
                        toast.setDuration(Toast.LENGTH_LONG);
                        toast.show();
                    }
                }
            });
            dialogComissBorr.show();
        } else {
            AlertDialog.Builder builderComissZero = new AlertDialog.Builder(ColetaItens.this);
            builderComissZero.setTitle("Comissão de borracheiro R$0,00");
            builderComissZero.setIcon(R.drawable.commission);
            builderComissZero.setCancelable(false);
            builderComissZero.setMessage("Comissão de borracheiro R$0,00, devido o " + (porcentgDesconto < 0 ? "acréscimo" : "desconto") + " ser de " + df.format((porcentgDesconto * (-1))) + "%");
            builderComissZero.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    LayoutInflater layoutInflater = getLayoutInflater();
                    View view = layoutInflater.inflate(R.layout.text_view_toast, null);
                    TextView txtItem = view.findViewById(R.id.txtItemToast);
                    txtItem.setText("Item " + (Integer.parseInt(cmpItem.getText().toString()) - 1) + " adicionado com sucesso!");
                    Toast toast = new Toast(ColetaItens.this);
                    toast.setView(view);
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.show();
                }
            });
            AlertDialog dialogComissZero = builderComissZero.create();
            dialogComissZero.show();

            if (isEditItem) {
                arrPorcComissBorr.set(i, "0");
                arrVlrComissBorr.set(i, "0");
            } else {
                arrPorcComissBorr.add("0");
                arrVlrComissBorr.add("0");
            }
        }
    }

    protected final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void afterTextChanged(Editable s) {

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!isEdicaoItem) {
                if ((cmpValorSrv.getText().toString().trim().length() >= 1 && Double.parseDouble(cmpValorSrv.getText().toString()) != 0) ||
                        (GettersSetters.getTipoColeta().contains("PC") && GettersSetters.getDescrTipoColeta().contains("RECL"))) {
                    btnAddItem.setVisibility(View.VISIBLE);
                } else {
                    btnAddItem.setVisibility(View.GONE);
                }
            }
        }
    };

    @SuppressLint("StaticFieldLeak")
    public class BuscaItensImportacao extends AsyncTask<String, String, ResultSet> {
        AlertDialog dialogImportItens = null;

        @Override
        protected void onPreExecute() {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ColetaItens.this);
            LayoutInflater layoutInflater = getLayoutInflater();
            View dialogView = layoutInflater.inflate(R.layout.dialog_carregamento, null);
            TextView textView = dialogView.findViewById(R.id.txtInfoCarreg);
            textView.setText("Processando Itens...\nAguarde...");
            builder.setCancelable(false);
            builder.setPositiveButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    buscaItensImportacao.cancel(true);
                    Toast.makeText(ColetaItens.this, "Busca Cancelada", Toast.LENGTH_LONG).show();
                }
            });
            builder.setView(dialogView);
            dialogImportItens = builder.create();
            dialogImportItens.show();
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected ResultSet doInBackground(String... params) {
            return conecta.buscaColetaExtItens(params[0], params[1], "", "", "");
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(final ResultSet resultSet) {
            int contaItensImportados = 0;

            try {
                resultSet.last();
                if (resultSet.getRow() > 0) {
                    if ((resultSet.getRow() + (totalItens)) <= 99) {
                        resultSet.beforeFirst();
                        while (resultSet.next()) {
                            /* BUSCA NOVAMENTE O PRODUTO PARA REVALIDAÇÃO DO PREÇO DE TABELA, PARA PEGAR O PREÇO CHEIO QUANDO FOR CLIENTE DO GRUPO */
                            db.buscaProduto("", false, resultSet.getString(10).trim());
                            valorUnitarioItem = Double.parseDouble(db.precoTabela); //VALOR DE TABELA CHEIO
                            /*---------------------------------------------------------------------------------------------------------------------*/
                            if (valorUnitarioItem <= 2) {
                                valorUnitarioItem = Double.parseDouble(resultSet.getString(8));
                            }

                            /* DESCONTO MAX DOS ITENS **/
                            if (GettersSetters.isClienteGrp()) {
                                porcentgDescontoGrp = GettersSetters.getDescGrupo();
                            } else {
                                if (GettersSetters.getDescExclusivo() > 0) {
                                    porcentgDescontoGrp = GettersSetters.getDescExclusivo();
                                } else {
                                    if (GettersSetters.getFilial().contains("1301") || GettersSetters.getFilial().contains("1401")) {
                                        porcentgDescontoGrp = Double.parseDouble(db.descontLauxen);
                                    } else {
                                        porcentgDescontoGrp = Double.parseDouble(db.descontVachiOst);
                                    }
                                }
                            }

                            valorUnitarioItem = new BigDecimal(valorUnitarioItem - (valorUnitarioItem * (porcentgDescontoGrp / 100)), MathContext.DECIMAL32).setScale(2, RoundingMode.HALF_UP).doubleValue();
                            valorTotalItem = valorUnitarioItem * Double.parseDouble(resultSet.getString(7));

                            arrItemColeta.add(String.valueOf(sequencialItens));
                            arrQtdItemColeta.add(resultSet.getString(7));
                            arrValorUnit.add(String.valueOf(valorUnitarioItem));
                            arrVlrBlqIt.add(String.valueOf(valorUnitarioItem));
                            arrBaseCalcIt.add(String.valueOf(valorUnitarioItem));
                            arrValorTotal.add(String.valueOf(valorTotalItem));
                            arrVlrTabelaItem.add(String.valueOf(valorUnitarioItem));
                            arrCodProdColeta.add(resultSet.getString(10));
                            arrBitolaColeta.add(resultSet.getString(11));
                            arrMarcaColeta.add(resultSet.getString(12));
                            arrModeloColeta.add(resultSet.getString(13));
                            arrSerieColeta.add(resultSet.getString(14));
                            arrDotColeta.add(resultSet.getString(15));
                            arrObservItem.add(resultSet.getString(22));
                            arrDesenhoColeta.add(resultSet.getString(17));
                            arrMontadoColeta.add(resultSet.getString(16));
                            arrUrgenteColeta.add(resultSet.getString(18));
                            arrCAgua.add("0");
                            arrCCamara.add("0");
                            arrGarantia.add("0");
                            arrPercDesc.add("0");
                            arrPercDescGrp.add("0");

                            arrFilialOri.add(resultSet.getString(1));
                            arrColetaOri.add(resultSet.getString(2));
                            arrItemOri.add(resultSet.getString(3));

                            if (GettersSetters.isCheckComissBorr()) {
                                arrPorcComissBorr.add(resultSet.getString(19));
                                arrVlrComissBorr.add(resultSet.getString(20));
                            } else {
                                arrPorcComissBorr.add("0");
                                arrVlrComissBorr.add("0");
                            }

                            valorTotalColeta += valorTotalItem;
                            sequencialItens++;
                            totalItens++;
                            valorUnitarioItem = 0;
                            valorTotalItem = 0;
                            contaItensImportados++;
                        }
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ColetaItens.this);
                        builder.setCancelable(false);
                        builder.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.atencao, null));
                        builder.setTitle("Limite de itens atingido!");
                        builder.setMessage("Limite de 99 (noventa e nove) itens será atingido ao importar a coleta " + numeroColetaImportacao + ".\n" +
                                "Importação da Coleta " + numeroColetaImportacao + " cancelada!\n" +
                                "Finalize esta Coleta " + (totalItens < 99 ? " ou adicione os itens manualmente!" : "") + "!");
                        builder.setPositiveButton("Fechar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (totalItens == 99) {
                                    btnAddItem.setVisibility(View.GONE);
                                    btnBuscaProduto.setVisibility(View.GONE);
                                    btnImportarItens.setVisibility(View.GONE);
                                    cmpBitola.clearFocus();
                                }
                            }
                        });
                        builder.show();
                    }

                    if (arrItemColeta.size() > 1) {
                        arrColetasImportadas.add(numeroColetaImportacao.trim());
                        msgColetasImportadas += numeroColetaImportacao.trim() + ", "; //MENSAGEM PARA A NOTA

                        LayoutInflater layoutInflater = getLayoutInflater();
                        View view = layoutInflater.inflate(R.layout.text_view_toast, null);
                        TextView txtItem = view.findViewById(R.id.txtItemToast);
                        txtItem.setText(contaItensImportados + " item(ns) importado(s)!");
                        Toast toast = new Toast(ColetaItens.this);
                        toast.setView(view);
                        toast.setDuration(Toast.LENGTH_LONG);
                        toast.show();

                        contaItensImportados = 0;

                        btnVisualizaItens.setVisibility(View.VISIBLE);
                        btnAvancar.setVisibility(View.VISIBLE);
                        cmpItem.setText(String.valueOf(sequencialItens));
                        txtQtdItens.setText(Integer.toString(arrItemColeta.size()));
                        txtVlrTotal.setText("R$ " + df.format(valorTotalColeta));
                        cmpBitola.clearFocus();
                    }
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ColetaItens.this);
                    builder.setTitle("Coleta não localizada");
                    builder.setIcon(R.drawable.atencao);
                    builder.setCancelable(false);
                    builder.setMessage("Nenhuma coleta localizada para os dados informados.");
                    builder.setNegativeButton("Fechar", null);
                    builder.show();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            if (dialogImportItens != null) {
                dialogImportItens.dismiss();
            }
        }
    }
}